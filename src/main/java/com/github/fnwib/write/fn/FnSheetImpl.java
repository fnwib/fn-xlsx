package com.github.fnwib.write.fn;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.model.*;
import com.github.fnwib.model.Header;
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

@Slf4j
public class FnSheetImpl implements FnSheet {
	private SheetConfig sheetConfig;

	private int startRowNum;
	private CellStyle contentCellStyle;
	private FnCellStyleFactory cellStyleFactory;

	private FileOutputStream outputStream;
	private Workbook workbook;
	@Getter
	private Sheet sheet;

	public FnSheetImpl(SheetConfig sheetConfig) {
		this.sheetConfig = sheetConfig;
		this.startRowNum = 0;
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
		this.cellStyleFactory = new FnCellStyleFactory(workbook);
		addPreHeader(sheet);
		addHeaderRow(sheet);
		this.contentCellStyle = cellStyleFactory.getOrDefault(sheetConfig.getContentCellStyle(), FnCellStyleType.CONTENT);
	}


	/**
	 * 设置表头以上的单元格
	 *
	 * @param sheet
	 */
	private void addPreHeader(Sheet sheet) {
		List<PreHeader> headers = sheetConfig.getPreHeaders();
		if (headers.isEmpty()) {
			return;
		}
		for (PreHeader header : headers) {
			CellStyle cellStyle = cellStyleFactory.getOrDefault(header.getCellStyle(), FnCellStyleType.PRR_HEADER);
			Row row = WriteHelper.getOrCreateRow(sheet, header.getRowNum());
			WriteHelper.setHeightIfGtZero(row, header.getHeight());
			Cell cell = WriteHelper.getOrCreateCell(row, header.getColumnIndex());
			WriteHelper.setCellValue(cell, header.getValue(), cellStyle);
		}
		int max = headers.stream().mapToInt(PreHeader::getRowNum).max().getAsInt();
		startRowNum = Math.max(max, startRowNum) + 1;
	}

	/**
	 * 指定表头
	 *
	 * @param sheet
	 */
	private void addHeaderRow(Sheet sheet) {
		List<Header> headers = sheetConfig.getHeaders();
		if (headers.isEmpty()) {
			return;
		}
		for (Header c : headers) {
			CellStyle cellStyle = cellStyleFactory.getOrDefault(c.getCellStyle(), FnCellStyleType.HEADER);
			WriteHelper.setColumnWidthIfGtZero(sheet, c.getColumnIndex(), c.getWidth());
			Row row = WriteHelper.getOrCreateRow(sheet, startRowNum);
			WriteHelper.setHeightIfGtZero(row, c.getHeight());
			Cell cell = WriteHelper.getOrCreateCell(row, c.getColumnIndex());
			WriteHelper.setCellValue(cell, c.getValue(), cellStyle);
			WriteHelper.setValue(sheet, startRowNum, c.getColumnIndex(), c.getValue(), cellStyle);
			if (c.getDataValidation() != null) {
				c.getDataValidation().createDataValidation(cell, sheet, sheetConfig.getMaxRowNumCanWrite());
			}
		}
		startRowNum++;
	}

	@Override
	public void flush() {
		try {
			if (workbook != null) {
				workbook.write(outputStream);
				outputStream.close();
				workbook.close();
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
		return sheetConfig.getMaxRowNumCanWrite() - startRowNum;
	}

	@Override
	public void addRow(RowContent row) {
		if (row.isEmpty()) {
			return;
		}
		List<Cell> cells = row.getCells();
		for (Cell fromCell : cells) {
			Cell cell = WriteHelper.setCellValue(sheet, startRowNum, fromCell);
			CellStyle cellStyle = cellStyleFactory.copyCellStyle(fromCell.getCellStyle());
			cell.setCellStyle(cellStyle);
		}
		List<Content> contents = row.getContents();
		for (Content content : contents) {
			WriteHelper.setValue(sheet, startRowNum, content.getColumnIndex(), content.getValue(), contentCellStyle);
		}
		startRowNum++;
	}

	/**
	 * 写入数据合并单元格
	 *
	 * @param rows             行
	 * @param mergedRangeIndex 合并的列
	 */
	@Override
	public void addMergeRow(List<RowContent> rows, List<Integer> mergedRangeIndex) {
		int begin = this.startRowNum;
		for (Integer mergeIndex : mergedRangeIndex) {
			//从下一行开始合并
			CellRangeAddress cellRangeAddress = new CellRangeAddress(begin, begin - 1 + rows.size(),
					mergeIndex, mergeIndex);
			sheet.addMergedRegion(cellRangeAddress);
		}
		for (RowContent row : rows) {
			addRow(row);
		}
	}


}
