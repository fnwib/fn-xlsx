package com.github.fnwib.write.fn;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.model.PreHeader;
import com.github.fnwib.model.RowContent;
import com.github.fnwib.model.SheetConfig;
import com.github.fnwib.util.UUIDUtils;
import com.github.fnwib.write.CommonPathTest;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class FnSheetImplTest extends CommonPathTest {

	/**
	 * getSheet  test header
	 * getSheet2 test preHeader
	 * getSheet3 test sheetName
	 * getSheet4 test filename
	 * <p>
	 * 校验Header写入
	 */
	@Test
	public void getSheet() {
		SheetConfig config = SheetConfig.builder()
				.dir(basePath)
				.addHeaders(getHeaders(10))
				.addHeaders(UUIDUtils.getHalfId(), UUIDUtils.getHalfId())
				.build();
		FnSheetImpl fnSheet = new FnSheetImpl(config);
		Sheet sheet = fnSheet.getSheet();
		int assignCount = 0;
		for (Row row : sheet) {
			for (Cell cell : row) {
				if (Objects.nonNull(cell) || StringUtils.isNotBlank(cell.getStringCellValue())) {
					assignCount++;
				}
			}
		}
		Assert.assertEquals("指定的12列header", 12, assignCount);
		fnSheet.flush();
	}

	/**
	 * 校验PreHeader写入
	 */
	@Test
	public void getSheet2() {
		PreHeader test = PreHeader.builder()
				.rowNum(1).columnIndex(1).value("TEST").build();
		SheetConfig config = SheetConfig.builder()
				.dir(basePath)
				.addPreHeader(test).build();
		FnSheetImpl singleSheet = new FnSheetImpl(config);
		Sheet sheet = singleSheet.getSheet();
		int assignCount = 0;
		for (Row row : sheet) {
			for (Cell cell : row) {
				assignCount++;
				if (cell.getRowIndex() == 1 && cell.getColumnIndex() == 1) {
					Assert.assertEquals("sheetName", "TEST", cell.getStringCellValue());
				}
			}
		}
		Assert.assertEquals("只指定了一个值", 1, assignCount);
		singleSheet.flush();
	}

	/**
	 * 校验生成文件名
	 *
	 * @throws IOException
	 */
	@Test
	public void getSheet3() throws IOException {
		SheetConfig config = SheetConfig.builder()
				.dir(basePath)
				.fileName("test-")
				.build();
		FnSheetImpl singleSheet = new FnSheetImpl(config);
		singleSheet.flush();
		Path path = Paths.get(basePath);
		long count = Files.find(path, 3,
				(dir, name) -> {
					AtomicBoolean flag = new AtomicBoolean();
					dir.iterator().forEachRemaining(n -> flag.set(n.toString().startsWith("test-")
							&& n.toString().endsWith(".xlsx")));
					return flag.get();
				}).count();
		Assert.assertTrue("filename", count > 0);
	}

	/**
	 * 校验sheetName
	 */
	@Test
	public void getSheet4() {
		String sheetName = "sheet-test";
		SheetConfig config = SheetConfig.builder()
				.dir(basePath)
				.sheetName(sheetName).build();
		FnSheetImpl singleSheet = new FnSheetImpl(config);
		//校验sheetName
		Sheet sheet = singleSheet.getSheet();
		Assert.assertEquals("sheetName", sheetName, sheet.getSheetName());
		singleSheet.flush();
	}

	@Test
	public void flush() {
	}

	@Test
	public void getStartRow() {
	}

	@Test
	public void canWriteSize() {
	}

	@Test
	public void addRow() {
		int rowNum = 0;
		SheetConfig config = SheetConfig.builder().dir(basePath).build();
		FnSheetImpl fnSheet = new FnSheetImpl(config);
		FnCellStyle style = new DefaultHeaderCellStyleImpl();
		try {
			Workbook sheets = new XSSFWorkbook();
			Sheet fromSheet = sheets.createSheet();
			Row fromRow = fromSheet.createRow(rowNum);

			Cell none = fromRow.createCell(0);
			none.setCellStyle(style.createCellStyle(sheets));
			Cell blank = fromRow.createCell(1);
			blank.setCellStyle(style.createCellStyle(sheets));
			Cell error = fromRow.createCell(2);
			error.setCellErrorValue((FormulaError.NAME.getCode()));
			error.setCellStyle(style.createCellStyle(sheets));
			Cell bool = fromRow.createCell(3);
			bool.setCellValue(true);
			bool.setCellStyle(style.createCellStyle(sheets));
			Cell formula = fromRow.createCell(4);
			formula.setCellFormula("A1+B1");
			formula.setCellStyle(style.createCellStyle(sheets));
			Cell numeric = fromRow.createCell(5);
			numeric.setCellValue(11);
			numeric.setCellStyle(style.createCellStyle(sheets));
			Cell string = fromRow.createCell(6);
			string.setCellValue("val");
			string.setCellStyle(style.createCellStyle(sheets));

			List<Cell> cells = Lists.newArrayList();
			cells.add(none);
			cells.add(blank);
			cells.add(error);
			cells.add(bool);
			cells.add(formula);
			cells.add(numeric);
			cells.add(string);
			RowContent rowContent = new RowContent(cells, Collections.emptyList());
			fnSheet.addRow(rowContent);
			fnSheet.flush();
			File file = Files.list(Paths.get(basePath)).map(Path::toFile).findFirst().orElse(null);
			Workbook workbook = new XSSFWorkbook(file);
			Sheet toSheet = workbook.getSheetAt(0);
			Row toRow = toSheet.getRow(rowNum);
			for (int i = 0; i < cells.size(); i++) {
				Cell fromCell = cells.get(i);
				Cell toCell = toRow.getCell(i);
				switch (fromCell.getCellTypeEnum()) {
					case _NONE:
					case BLANK:
						Assert.assertEquals("_NONE || BLANK", fromCell.getStringCellValue(), toCell.getStringCellValue());
						break;
					case ERROR:
						Assert.assertEquals("ERROR", fromCell.getErrorCellValue(), toCell.getErrorCellValue());
						break;
					case STRING:
						Assert.assertEquals("STRING", fromCell.getStringCellValue(), toCell.getStringCellValue());
						break;
					case BOOLEAN:
						Assert.assertEquals("BOOLEAN", fromCell.getBooleanCellValue(), toCell.getBooleanCellValue());
						break;
					case FORMULA:
						Assert.assertEquals("FORMULA", fromCell.getCellFormula(), toCell.getCellFormula());
						break;
					case NUMERIC:
						BigDecimal fromNumber = new BigDecimal(fromCell.getNumericCellValue());
						BigDecimal toNumber = new BigDecimal(toCell.getNumericCellValue());
						Assert.assertEquals("NUMERIC", fromNumber, toNumber);
						break;
					default:
						break;
				}

			}
			workbook.close();
		} catch (IOException | InvalidFormatException e) {
			throw new ExcelException(e);
		}
	}

	@Test
	public void addMergeRow() {
	}

}