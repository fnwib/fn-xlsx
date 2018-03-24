package com.github.fnwib.write;

import com.github.fnwib.databing.LineWriter;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.write.config.WorkbookBuilder;
import com.github.fnwib.write.config.WorkbookConfig;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Excel生成工具
 * <p>
 *
 * @param <T>
 */
public class ExcelWriterProcessor<T> implements ExcelWriter<T> {

    private final WorkbookBuilder<T> workbookBuilder;
    private final AtomicInteger currentRowNum = new AtomicInteger();
    private LineWriter<T> lineWriter;
    private boolean closed = false;

    public ExcelWriterProcessor(WorkbookConfig<T> workbookConfig) {
        this.workbookBuilder = new WorkbookBuilder<>(workbookConfig);
        useNextSheet();
    }

    private synchronized void useNextSheet() {
        if (workbookBuilder.isWritten()) {
            workbookBuilder.write();
        }
        this.lineWriter = workbookBuilder.getWriteParser();
        currentRowNum.set(workbookBuilder.getTitleRowNum() + 1);
    }

    private void checkState() {
        if (closed) {
            throw new ExcelException("ExcelWriter已经关闭");
        }
    }

    @Override
    public void write(T element) {
        checkState();
        if (workbookBuilder.canWrite(currentRowNum.get())) {
            useNextSheet();
        }
        lineWriter.convert(currentRowNum.getAndIncrement(), element);
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
            if (workbookBuilder.canWrite(currentRowNum.get())) {
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
        workbookBuilder.write();

    }

    @Override
    public List<File> getFiles() {
        if (!closed) {
            flush();
        }
        return workbookBuilder.getResultFiles();
    }
}

