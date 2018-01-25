package com.github.fnwib.databing.ser;

import com.github.fnwib.write.CellText;

import java.util.List;

@FunctionalInterface
public interface Serializer<FT> {

    CellText serialize(FT value);
}
