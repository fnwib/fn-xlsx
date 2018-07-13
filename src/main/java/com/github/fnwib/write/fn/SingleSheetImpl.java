package com.github.fnwib.write.fn;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.write.model.ExcelHeader;
import com.github.fnwib.write.model.ExcelPreHeader;
import com.github.fnwib.write.model.SheetConfig;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
public class SingleSheetImpl implements FnSheet {
	private SheetConfig sheetConfig;
	private Map<String, Integer> mapping;
	private int startRowNum;
	private CellStyle cellStyle;

	private FileOutputStream outputStream;
	private Workbook workbook;
	@Getter
	private Sheet sheet;

	public SingleSheetImpl(SheetConfig sheetConfig) {
		this.sheetConfig = sheetConfig;
		this.mapping = Maps.newHashMap();
		this.startRowNum = 0;
		cellStyle = null;
		init();
	}

	private void init() {
		try {
			File emptyFile = sheetConfig.getEmptyFile();
			this.outputStream = FileUtils.openOutputStream(emptyFile);
		} catch (IOException e) {
			throw new ExcelException(e);
		}
		this.workbook = new SXSSFWorkbook();
		if (StringUtils.isBlank(sheetConfig.getSheetName())) {
			sheet = workbook.createSheet();
		} else {
			sheet = workbook.createSheet(sheetConfig.getSheetName());
		}
		addPreHeader(sheet);
		addHeaderRow(sheet);
	}


	/**
	 * 设置表头以上的单元格
	 *
	 * @param sheet
	 */
	private void addPreHeader(Sheet sheet) {
		List<ExcelPreHeader> headers = sheetConfig.getPreHeaders();
		for (ExcelPreHeader header : headers) {
			WriteHelper.setValue(sheet, header.getRowNum(), header.getCellIndex(), header.getValue(), header.getCellStyle());
			startRowNum = Math.max(header.getRowNum(), startRowNum);
		}
	}

	/**
	 * 指定表头
	 *
	 * @param sheet
	 */
	private void addHeaderRow(Sheet sheet) {
		mapping.clear();
		List<ExcelHeader> headers = sheetConfig.getHeaders();
		startRowNum++;
		for (ExcelHeader c : headers) {
			mapping.put(c.getId(), c.getCellIndex());
			WriteHelper.setValue(sheet, startRowNum, c.getCellIndex(), c.getValue(), c.getCellStyle());
		}
	}

	@Override
	public void flush() {
		try {
			if (workbook != null) {
				workbook.write(outputStream);
				outputStream.close();
			}
		} catch (IOException e) {
			log.error("error {}", e);
			throw new ExcelException(e);
		}
	}

	@Override
	public int getStartRow() {
		return startRowNum;
	}

	@Override
	public int canWriteSize() {
		return sheetConfig.getMaxRowsCanWrite() - startRowNum;
	}

	@Override
	public void addRow(Map<String, String> row) {
		startRowNum++;
		row.forEach((k, v) -> {
			Integer index = mapping.get(k);
			if (index == null) {
				log.error("mapping {}", mapping);
				log.error("row {}", row);
				log.error("k {}", k);
			}
			WriteHelper.setValue(sheet, startRowNum, index, v, cellStyle);
		});
	}

	/**
	 * 写入数据合并单元格
	 *
	 * @param rows             行  K-id V-text
	 * @param mergedRangeIndex 合并的列
	 */
	@Override
	public void addMergeRow(List<Map<String, String>> rows, List<Integer> mergedRangeIndex) {
		int begin = this.startRowNum;
		for (Map<String, String> row : rows) {
			addRow(row);
		}
		for (Integer mergeIndex : mergedRangeIndex) {
			CellRangeAddress cellRangeAddress = new CellRangeAddress(begin + 1, begin + rows.size(),
					mergeIndex, mergeIndex);
			sheet.addMergedRegion(cellRangeAddress);
		}

	}


}
