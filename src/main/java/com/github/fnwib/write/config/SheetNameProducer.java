package com.github.fnwib.write.config;

@FunctionalInterface
public interface SheetNameProducer {

    String getSheetName(String sheetName);

}
