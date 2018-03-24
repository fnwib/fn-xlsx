package com.github.fnwib.write.config;

import com.github.fnwib.databing.LineWriter;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.util.List;

public interface WorkbookConfig {

    boolean isWritten();

    int getTitleRowNum();

    LineWriter getWriteParser();

    Sheet getNextSheet();

    void write();

    boolean canWrite(int rowNum);

    List<File> getResultFiles();




}
