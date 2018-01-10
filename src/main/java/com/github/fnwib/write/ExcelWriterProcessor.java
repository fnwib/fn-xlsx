package com.github.fnwib.write;

import com.github.fnwib.write.config.WorkbookConfig;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Excel生成工具
 * <p>
 *
 * @param <T>
 */
public class ExcelWriterProcessor<T> implements ExcelWriter<T> {

    private final WorkbookConfig<T> workbookConfig;

    private final AtomicReference<Sheet> currentSheet = new AtomicReference<>();

    private final AtomicInteger currentRowNum = new AtomicInteger();

    private WriteParser<T> writeParser;

    public ExcelWriterProcessor(WorkbookConfig<T> workbookConfig) {
        this.workbookConfig = workbookConfig;
        useNextSheet();
        this.writeParser = workbookConfig.getWriteParser();
    }

    private synchronized void useNextSheet() {
        Sheet sheet = this.workbookConfig.getNextSheet();
        currentSheet.set(sheet);
        currentRowNum.set(workbookConfig.getTitleRowNum() + 1);
    }

    @Override
    public void write(T element) {
        if (!workbookConfig.getResultFileSetting().valid(currentRowNum)) {
            useNextSheet();
        }
        writeParser.convert(currentSheet.get(), currentRowNum.getAndAdd(1), element);
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
            writeParser.convert(currentSheet.get(), currentRowNum.getAndAdd(elements.size()), elements, mergedRangeIndexes);
        }
    }

    @Override
    public File write2File() {
        workbookConfig.close();
        return workbookConfig.getResultFileSetting().getResultFolder();
    }

    @Override
    public List<File> getFiles() {
        return workbookConfig.getResultFiles();
    }
}

