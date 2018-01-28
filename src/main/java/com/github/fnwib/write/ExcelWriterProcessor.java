package com.github.fnwib.write;

import com.github.fnwib.databing.LineWriter;
import com.github.fnwib.exception.ExcelException;
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
    private final AtomicInteger currentRowNum = new AtomicInteger();
    private LineWriter<T> lineWriter;
    private boolean closed = false;

    public ExcelWriterProcessor(WorkbookConfig<T> workbookConfig) {
        this.workbookWrapFactory = new WorkbookWrapFactory<>(workbookConfig);
        this.workbookConfig = workbookConfig;
        this.workbookWraps = Queues.newArrayDeque();
        useNextSheet();
    }

    private synchronized void useNextSheet() {
        if (!workbookWraps.isEmpty()) {
            workbookWraps.poll().write();
        }
        workbookWraps.add(workbookWrapFactory.createWorkbookWrap());
        WorkbookWrap workbookWrap = workbookWraps.peek();
        this.lineWriter = workbookWrap.getWriteParser();
        currentRowNum.set(workbookWrapFactory.getTitleRowNum() + 1);
    }

    private void checkState() {
        if (closed) {
            throw new ExcelException("ExcelWriter已经关闭");
        }
    }

    @Override
    public void write(T element) {
        checkState();
        if (workbookConfig.getResultFileSetting().gt(currentRowNum)) {
            useNextSheet();
        }
        lineWriter.convert(currentRowNum.getAndAdd(1), element);
    }

    @Override
    public void write(List<T> elements) {
        for (T element : elements) {
            this.write(element);
        }
    }

    @Override
    public void writeMergedRegion(List<T> elements, List<Integer> mergedRangeIndexes) {
        checkState();
        if (elements.isEmpty()) {
            return;
        }
        if (elements.size() == 1) {
            this.write(elements);
        } else {
            if (workbookConfig.getResultFileSetting().gt(currentRowNum)) {
                useNextSheet();
            }
            lineWriter.convert(currentRowNum.getAndAdd(elements.size()), elements, mergedRangeIndexes);
        }
    }

    @Override
    public void flush() {
        if (closed) {
            return;
        }
        closed = true;
        workbookWraps.poll().write();

    }

    @Override
    public List<File> getFiles() {
        if (!closed) {
            flush();
        }
        return workbookConfig.getResultFiles();
    }
}

