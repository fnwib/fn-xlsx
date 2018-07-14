package com.github.fnwib.write.model;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.util.UUIDUtils;
import com.github.fnwib.write.CommonPathTest;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

public class SheetConfigTest extends CommonPathTest {

	@Test(expected = ExcelException.class)
	public void check() {
		ExcelHeader build = ExcelHeader.builder()
				.columnIndex(1)
				.value("head")
				.build();
		SheetConfig config = SheetConfig.builder()
				.dir(basePath)
				.addHeaders(Lists.newArrayList(build))
				.addHeaders(UUIDUtils.getHalfId(), UUIDUtils.getHalfId(), "head")
				.build();
		Assert.assertNotNull(config);
	}

	@Test(expected = ExcelException.class)
	public void check2() {
		ExcelHeader build = ExcelHeader.builder()
				.columnIndex(1)
				.value("HEAD")
				.build();
		SheetConfig config = SheetConfig.builder()
				.dir(basePath)
				.addHeaders(Lists.newArrayList(build))
				.addHeaders(UUIDUtils.getHalfId(), UUIDUtils.getHalfId(), "head")
				.build();
		Assert.assertNotNull(config);
	}

}