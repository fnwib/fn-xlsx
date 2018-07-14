package com.github.fnwib.write.fn;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.write.model.*;
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
public class SingleSheetImpl implements FnSheet {
	private SheetConfig sheetConfig;

	private int startRowNum;
	private CellStyle contentCellStyle;

	private FileOutputStream outputStream;
	private Workbook workbook;
	@Getter
	private Sheet sheet;

	public SingleSheetImpl(SheetConfig sheetConfig) {
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
		addPreHeader(sheet);
		addHeaderRow(sheet);
		FnCellStyle fnCellStyle = FnCellStyles.getOrDefault(sheetConfig.getContentCellStyle(), FnCellStyleType.CONTENT);
		this.contentCellStyle = fnCellStyle.createCellStyle(workbook);
	}


	/**
	 * 设置表头以上的单元格
	 *
	 * @param sheet
	 */
	private void addPreHeader(Sheet sheet) {
		List<ExcelPreHeader> headers = sheetConfig.getPreHeaders();
		for (ExcelPreHeader header : headers) {
			FnCellStyle fnCellStyle = FnCellStyles.getOrDefault(header.getCellStyle(), FnCellStyleType.PRR_HEADER);
			CellStyle cellStyle = fnCellStyle.createCellStyle(workbook);
			Row row = WriteHelper.getOrCreateRow(sheet, header.getRowNum());
			WriteHelper.setHeightIfGtZero(row, header.getHeight());
			Cell cell = WriteHelper.getOrCreateCell(row, header.getColumnIndex());
			WriteHelper.setCellValue(cell, header.getValue(), cellStyle);
			startRowNum = Math.max(header.getRowNum(), startRowNum);
		}
	}

	/**
	 * 指定表头
	 *
	 * @param sheet
	 */
	private void addHeaderRow(Sheet sheet) {
		List<ExcelHeader> headers = sheetConfig.getHeaders();
		startRowNum++;
		for (ExcelHeader c : headers) {
			FnCellStyle fnCellStyle = FnCellStyles.getOrDefault(c.getCellStyle(), FnCellStyleType.HEADER);
			CellStyle cellStyle = fnCellStyle.createCellStyle(workbook);
			WriteHelper.setColumnWidthIfGtZero(sheet, c.getColumnIndex(), c.getWidth());
			Row row = WriteHelper.getOrCreateRow(sheet, startRowNum);
			WriteHelper.setHeightIfGtZero(row, c.getHeight());
			Cell cell = WriteHelper.getOrCreateCell(row, c.getColumnIndex());
			WriteHelper.setCellValue(cell, c.getValue(), cellStyle);
			WriteHelper.setValue(sheet, startRowNum, c.getColumnIndex(), c.getValue(), cellStyle);
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
		return sheetConfig.getMaxRowNumCanWrite() - startRowNum;
	}

	@Override
	public void addRow(List<ExcelContent> row) {
		startRowNum++;
		for (ExcelContent cell : row) {
			WriteHelper.setValue(sheet, startRowNum, cell.getColumnIndex(), cell.getValue(), contentCellStyle);
		}
	}

	@Override
	public void addRow(RowExcelContent row) {
		List<ExcelContent> cells = row.getRow();
		addRow(cells);
	}

	/**
	 * 写入数据合并单元格
	 *
	 * @param rows             行
	 * @param mergedRangeIndex 合并的列
	 */
	@Override
	public void addMergeRow(List<RowExcelContent> rows, List<Integer> mergedRangeIndex) {
		int begin = this.startRowNum;
		for (RowExcelContent row : rows) {
			addRow(row);
		}
		for (Integer mergeIndex : mergedRangeIndex) {
			//从下一行开始合并
			CellRangeAddress cellRangeAddress = new CellRangeAddress(begin + 1, begin + rows.size(),
					mergeIndex, mergeIndex);
			sheet.addMergedRegion(cellRangeAddress);
		}
	}


}
