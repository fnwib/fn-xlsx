package com.github.fnwib.write;

import com.github.fnwib.exception.NotSupportedException;
import com.github.fnwib.write.config.ExportType;
import com.github.fnwib.write.config.WorkbookConfig;
import com.github.fnwib.write.config.WorkbookWrap;
import com.github.fnwib.write.config.WorkbookWrapFactory;
import com.google.common.collect.Queues;

import java.io.File;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Excel生成工具
 * <p>
 *
 * @param <T>
 */
public class ExcelWriterProcessor<T> implements ExcelWriter<T> {

    private final WorkbookConfig<T>      workbookConfig;
    private final WorkbookWrapFactory<T> workbookWrapFactory;
    private final Queue<WorkbookWrap>    workbookWraps;
    private final ExportType             exportType;
    private final AtomicInteger currentRowNum = new AtomicInteger();
    private WriteParser<T> writeParser;

    public ExcelWriterProcessor(WorkbookConfig<T> workbookConfig) {
        this.workbookWrapFactory = new WorkbookWrapFactory<>(workbookConfig);
        this.workbookConfig = workbookConfig;
        this.exportType = workbookConfig.getExportType();
        this.workbookWraps = Queues.newArrayDeque();
        useNextSheet();
    }

    private synchronized void useNextSheet() {
        if (exportType == ExportType.SingleSheet) {
            if (!workbookWraps.isEmpty()) {
                workbookWraps.poll().write();
            }
            workbookWraps.add(workbookWrapFactory.createWorkbookWrap());
            WorkbookWrap workbookWrap = workbookWraps.peek();
            this.writeParser = workbookWrap.getWriteParser();
            currentRowNum.set(workbookWrapFactory.getTitleRowNum() + 1);
        } else if (exportType == ExportType.MultiSheet) {
            throw new NotSupportedException("暂时不支持导出类型, " + exportType.name());
        } else {
            throw new NotSupportedException("不支持导出类型, " + exportType.name());
        }
    }

    @Override
    public void write(T element) {
        if (!workbookConfig.getResultFileSetting().valid(currentRowNum)) {
            useNextSheet();
        }
        writeParser.convert(currentRowNum.getAndAdd(1), element);
    }

    @Override
    public void write(List<T> elements) {
        for (T element : elements) {
            this.write(element);
        }
    }

    @Override
    public void writeMergedRegion(List<T> elements, List<Integer> mergedRangeIndexes) {
        if (elements.isEmpty()) {
            return;
        }
        if (elements.size() == 1) {
            this.write(elements);
        } else {
            if (!workbookConfig.getResultFileSetting().valid(currentRowNum)) {
                useNextSheet();
            }
            writeParser.convert(currentRowNum.getAndAdd(elements.size()), elements, mergedRangeIndexes);
        }
    }

    @Override
    public File write2File() {
        workbookWraps.poll().write();
        return workbookConfig.getResultFileSetting().getResultFolder();
    }

    @Override
    public List<File> getFiles() {
        return workbookConfig.getResultFiles();
    }
}

