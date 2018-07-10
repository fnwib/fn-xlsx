package com.github.fnwib.mapping.impl.cell;

import com.github.fnwib.write.CellText;
import org.apache.poi.ss.usermodel.Row;

import java.util.Optional;

public interface CellMapping {

	Integer getColumn();

	Optional<String> getValue(Row row);

	Optional<CellText> createCellText(Object value);

}
