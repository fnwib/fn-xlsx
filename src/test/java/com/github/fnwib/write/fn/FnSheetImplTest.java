package com.github.fnwib.write.fn;

import com.github.fnwib.model.PreHeader;
import com.github.fnwib.model.SheetConfig;
import com.github.fnwib.util.UUIDUtils;
import com.github.fnwib.write.CommonPathTest;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	}

	@Test
	public void addMergeRow() {
	}

}