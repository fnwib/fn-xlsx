package com.github.fnwib.util;

import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.ComplexEnum;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.mapper.RowMapper;
import com.github.fnwib.model.Header;
import com.github.fnwib.model.PreHeader;
import com.github.fnwib.model.SheetConfig;
import com.github.fnwib.model.SheetTemplateView;
import com.github.fnwib.reflect.BeanResolver;
import com.github.fnwib.reflect.Property;
import com.github.fnwib.write.fn.FnCellStyleType;
import com.github.fnwib.write.fn.FnDataValidation;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class FnUtils {

	private FnUtils() {
	}

	/**
	 * 只做匹配使用，不需要样式
	 *
	 * @param row
	 * @return
	 */
	public static List<Header> toHeadersWithoutStyle(Row row) {
		if (row == null) {
			return Collections.emptyList();
		}
		List<Header> headers = Lists.newArrayListWithCapacity(row.getLastCellNum());
		for (Cell cell : row) {
			Header header = Header.builder()
					.columnIndex(cell.getColumnIndex())
					.value(cell.getStringCellValue())
					.height((short) (20 * 25))
					.width(500 * 10)
					.build();
			headers.add(header);
		}
		return headers;
	}

	private static void addHeader(Class<?> type, AtomicInteger seq, List<Header> headers) {
		List<Property> properties = BeanResolver.INSTANCE.getProperties(type);
		TreeMap<AutoMapping, Class<?>> map = new TreeMap<>(Comparator.comparing(AutoMapping::order));
		for (Property property : properties) {
			AutoMapping mapping = property.getAnnotation(AutoMapping.class);
			map.put(mapping, property.getFieldType().getRawClass());
		}
		map.forEach((mapping, clazz) -> {
			if (mapping.complex() == ComplexEnum.NESTED) {
				addHeader(clazz, seq, headers);
			} else {
				String value = mapping.prefix() + mapping.value() + mapping.suffix();
				Header header = Header.builder()
						.columnIndex(seq.getAndIncrement())
						.value(value)
						.height((short) (20 * 25))
						.width(500 * 10)
						.build();
				headers.add(header);
			}
		});
	}

	public static List<Header> getHeaders(AtomicInteger sequence, Class<?> type) {
		List<Header> headers = new ArrayList<>();
		addHeader(type, sequence, headers);
		return headers;
	}

	public static List<Header> toHeaderWithStyle(Row row) {
		List<? extends DataValidation> validations = row.getSheet().getDataValidations();
		List<Header> headers = Lists.newArrayListWithCapacity(row.getLastCellNum());
		for (Cell cell : row) {
			Header header = Header.builder()
					.columnIndex(cell.getColumnIndex())
					.value(cell.getStringCellValue())
					.height(row.getHeight())
					.width(row.getSheet().getColumnWidth(cell.getColumnIndex()))
					.cellStyle(FnCellStyleType.toFnCellStyle(cell.getCellStyle()))
					.dataValidation(getFnDataValidation(cell.getRowIndex(), cell.getColumnIndex(), validations))
					.build();
			headers.add(header);
		}
		return headers;
	}

	private static FnDataValidation getFnDataValidation(int rowIndex, int columnIndex, List<? extends DataValidation> validations) {

		for (DataValidation validation : validations) {
			CellRangeAddressList regions = validation.getRegions();
			if (null == regions || regions.getSize() == 0) {
				continue;
			}
			for (CellRangeAddress rangeAddress : regions.getCellRangeAddresses()) {
				int y1 = rangeAddress.getFirstRow();
				int y2 = rangeAddress.getLastRow();
				int x1 = rangeAddress.getFirstColumn();
				int x2 = rangeAddress.getLastColumn();
				if (columnIndex >= x1 && columnIndex <= x2 && rowIndex >= y1 && rowIndex <= y2) {
					DataValidationConstraint constraint = validation.getValidationConstraint();
					return new FnDataValidation(constraint.getExplicitListValues());
				}
			}
		}
		return null;
	}

	public static List<PreHeader> toPreHeaderWithStyle(Row row) {
		List<PreHeader> headers = Lists.newArrayListWithCapacity(row.getLastCellNum());
		for (Cell cell : row) {
			PreHeader header = PreHeader.builder()
					.rowNum(row.getRowNum())
					.columnIndex(cell.getColumnIndex())
					.value(cell.getStringCellValue())
					.height(row.getHeight())
					.width(row.getSheet().getColumnWidth(cell.getColumnIndex()))
					.cellStyle(FnCellStyleType.toFnCellStyle(cell.getCellStyle()))
					.build();
			headers.add(header);
		}
		return headers;
	}

	public static <T> void merge(SheetConfig config, File template, RowMapper<T> mapper) {
		if (template == null) {
			return;
		}
		SheetTemplateView view = config.getView();
		try (XSSFWorkbook workbook = new XSSFWorkbook(template)) {
			XSSFSheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				if (mapper.isEmpty(row)) {
					continue;
				}
				boolean match = mapper.match(row);
				if (match) {
					List<Header> headers = toHeaderWithStyle(row);
					view.prependHeaders(headers);
					return;
				} else {
					List<PreHeader> headers = toPreHeaderWithStyle(row);
					view.prependPreHeader(headers);
				}
			}
			throw new ExcelException("模板错误");
		} catch (IOException | InvalidFormatException e) {
			throw new ExcelException(e);
		}
	}

}
