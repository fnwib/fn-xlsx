package com.github.fnwib.util;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.mapper.RowReader;
import com.github.fnwib.write.model.ExcelHeader;
import com.github.fnwib.write.model.SheetConfig;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public class FnUtils {

	public static List<ExcelHeader> to(Row row) {
		List<ExcelHeader> headers = Lists.newArrayListWithCapacity(row.getLastCellNum());
		for (Cell cell : row) {
			ExcelHeader header = ExcelHeader.builder()
					.columnIndex(cell.getColumnIndex()).value(cell.getStringCellValue()).build();
			headers.add(header);
		}
		return headers;
	}

	public static <T> void merge(SheetConfig config, File template, RowReader<T> reader) {
		try {
			if (template == null) {
				return;
			}
			XSSFWorkbook workbook = new XSSFWorkbook(template);
			XSSFSheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				if (reader.isEmpty(row)) {
					continue;
				}
				boolean match = reader.match(row);
				if (match) {
					List<ExcelHeader> headers = to(row);
					config.prependHeaders(headers);
					return;
				}
			}
			throw new ExcelException("模板错误");
		} catch (IOException | InvalidFormatException e) {
			throw new ExcelException(e);
		}
	}

}
