package com.github.fnwib.write.fn;

import com.github.fnwib.write.CommonPathTest;
import com.github.fnwib.write.model.ExcelPreHeader;
import com.github.fnwib.write.model.SheetConfig;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

public class SingleSheetImplTest extends CommonPathTest {

	/**
	 * getSheet  test header
	 * getSheet2 test preHeader
	 * getSheet3 test sheetName
	 * getSheet4 test filename
	 *
	 * @throws IOException
	 */
	@Test
	public void getSheet() {
		SheetConfig config = SheetConfig.builder()
				.dir(basePath)
				.addHeaders(getHeaders(10))
				.build();
		SingleSheetImpl singleSheet = new SingleSheetImpl(config);
		//校验sheetName
		Sheet sheet = singleSheet.getSheet();
		//校验PreHeader写入
		int assignCount = 0;
		for (Row row : sheet) {
			for (Cell cell : row) {
				assignCount++;
			}
		}
		Assert.assertEquals("指定的10列header", 10, assignCount);
		singleSheet.flush();
	}

	/**
	 * 校验PreHeader写入
	 *
	 * @throws IOException
	 */
	@Test
	public void getSheet2() {
		ExcelPreHeader test = ExcelPreHeader.builder()
				.rowNum(1).columnIndex(1).value("TEST").build();
		SheetConfig config = SheetConfig.builder()
				.dir(basePath)
				.addPreHeader(test).build();
		SingleSheetImpl singleSheet = new SingleSheetImpl(config);
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
		SingleSheetImpl singleSheet = new SingleSheetImpl(config);
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
		singleSheet.flush();
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
		SingleSheetImpl singleSheet = new SingleSheetImpl(config);
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
	}

	@Test
	public void addRow1() {
	}

	@Test
	public void addMergeRow() {
	}

}