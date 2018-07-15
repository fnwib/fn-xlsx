package com.github.fnwib.write;

import com.github.fnwib.databing.LineReader;
import com.github.fnwib.databing.LineReaderForExcel;
import com.github.fnwib.databing.LocalConfig;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.mapping.RowMapper;
import com.github.fnwib.mapping.RowMapperImpl;
import com.github.fnwib.write.config.ResultFileSetting;
import com.github.fnwib.write.config.TemplateSetting;
import com.github.fnwib.write.config.WorkbookBuilder;
import com.github.fnwib.write.config.WorkbookConfig;
import com.github.fnwib.write.fn.FnCellStyles;
import com.github.fnwib.write.model.ExcelHeader;
import com.github.fnwib.write.model.ExcelPreHeader;
import com.github.fnwib.write.model.SheetConfig;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ComUtils<T> {
	private TemplateSetting templateSetting;
	private ResultFileSetting resultFileSetting;
	@Getter
	private RowMapper<T> rowMapper;

	public ComUtils(WorkbookConfig workbookConfig) {
		WorkbookBuilder config = (WorkbookBuilder) workbookConfig;
		templateSetting = config.getTemplateSetting();
		resultFileSetting = config.getResultFileSetting();
		LineReader<T> lineReader = config.getLineReader();
		LineReaderForExcel<T> reader = (LineReaderForExcel) lineReader;
		Class<T> entityClass = reader.getEntityClass();
		LocalConfig localConfig = reader.getLocalConfig();
		rowMapper = new RowMapperImpl<>(entityClass, localConfig);
	}

	public SheetConfig toSheetConfig() {
		SheetConfig.Builder builder = to(resultFileSetting, templateSetting);
		File template = templateSetting.getTemplate();
		Row row = null;
		if (template != null) {
			try {
				row = copyFromTemplateAndRtHead(builder, template);
			} catch (IOException e) {
				throw new ExcelException(e);
			}
		}
		addNewCells(builder, templateSetting);
		createNewHeaders(builder, row, templateSetting.getLastTitles());
		return builder.build();
	}

	private SheetConfig.Builder to(ResultFileSetting resultFileSetting, TemplateSetting templateSetting) {
		return SheetConfig.builder()
				.dir(resultFileSetting.getDir())
				.fileName(resultFileSetting.getFilename())
				.sheetName(templateSetting.getSheetName())
				.maxRowNumCanWrite(templateSetting.getMaxRowsCanWrite());
	}

	private void addNewCells(SheetConfig.Builder builder, TemplateSetting templateSetting) {
		List<CellText> cellTexts = templateSetting.getCellTexts();
		for (CellText cellText : cellTexts) {
			ExcelPreHeader preHeader = ExcelPreHeader.builder()
					.rowNum(cellText.getRowNum())
					.columnIndex(cellText.getCellNum())
					.value(cellText.getText())
					.build();
			builder.addPreHeader(preHeader);
		}
	}

	private void createNewHeaders(SheetConfig.Builder builder, Row head, List<String> lastTitles) {
		List<ExcelHeader> headers = Lists.newArrayList();
		int beginIndex = 0;
		if (head != null) {
			Sheet sheet = head.getSheet();
			for (Cell cell : head) {
				ExcelHeader header = ExcelHeader.builder().columnIndex(cell.getColumnIndex())
						.height(head.getHeight())
						.width(sheet.getColumnWidth(cell.getColumnIndex()))
						.value(cell.getStringCellValue())
						.cellStyle(FnCellStyles.to(((XSSFCellStyle) cell.getCellStyle())))
						.build();
				headers.add(header);
				beginIndex = Math.max(cell.getColumnIndex(), beginIndex)+1;
			}
			int size = headers.size();
			ExcelHeader beforeExcelHeader = headers.get(size - 1);
			for (String value : lastTitles) {
				ExcelHeader header = ExcelHeader.builder().columnIndex(beginIndex)
						.value(value)
						.cellStyle(beforeExcelHeader.getCellStyle())
						.width(beforeExcelHeader.getWidth())
						.build();
				headers.add(header);
				beginIndex++;
			}

		} else {
			for (String value : lastTitles) {
				ExcelHeader header = ExcelHeader.builder().columnIndex(beginIndex)
						.value(value)
						.build();
				headers.add(header);
				beginIndex++;
			}
		}
		builder.addHeaders(headers);
	}

	private Row copyFromTemplateAndRtHead(SheetConfig.Builder builder, File template) throws IOException {

		FileInputStream inputStream = FileUtils.openInputStream(template);
		XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
		XSSFSheet sheet = workbook.getSheetAt(0);
		List<Row> preRow = new ArrayList<>();
		Row head = null;
		for (Row row : sheet) {
			if (rowMapper.isEmpty(row)) {
				continue;
			}
			boolean match = rowMapper.match(row);
			if (match) {
				head = row;
			} else {
				preRow.add(row);
			}
		}
		if (head == null) {
			throw new SettingException("模板错误");
		}
		List<ExcelPreHeader> preHeader = Lists.newArrayList();
		for (Row row : preRow) {
			short height = row.getHeight();
			for (Cell cell : row) {
				if (cell == null) {
					continue;
				}
				XSSFCellStyle cellStyle = (XSSFCellStyle) cell.getCellStyle();
				ExcelPreHeader excelPreHeader = ExcelPreHeader.builder().rowNum(row.getRowNum())
						.columnIndex(cell.getColumnIndex())
						.value(cell.getStringCellValue())
						.cellStyle(FnCellStyles.to(cellStyle))
						.height(height)
						.build();
				preHeader.add(excelPreHeader);
			}
		}
		builder.addPreHeader(preHeader);
		return head;

	}


}
