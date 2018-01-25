package com.github.fnwib.databing.deser;


import org.apache.poi.ss.usermodel.Cell;

@FunctionalInterface
public interface CellDeserializer<FT> {

    FT deserialize(Cell cell);

}
