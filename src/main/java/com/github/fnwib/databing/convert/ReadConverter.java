package com.github.fnwib.databing.convert;

import org.apache.poi.ss.usermodel.Row;

import java.util.Optional;

public interface ReadConverter {

    String getKey();

    Optional<?> getValue(Row row);
}
