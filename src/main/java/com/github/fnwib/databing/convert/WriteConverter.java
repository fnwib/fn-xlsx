package com.github.fnwib.databing.convert;

import com.github.fnwib.write.CellText;

import java.util.List;

public interface WriteConverter {

    <Param> List<CellText> getCellText(Param element);
}
