package com.github.fnwib.model;

import com.github.fnwib.util.UUIDUtils;
import com.github.fnwib.write.CommonPathTest;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

public class SheetConfigTest extends CommonPathTest {

	@Test
	public void check() {
		Header build = Header.builder()
				.columnIndex(1)
				.value("head")
				.build();
		SheetConfig config = SheetConfig.builder()
				.dir(basePath)
				.addHeaders(Lists.newArrayList(build))
				.addHeaders(UUIDUtils.getHalfId(), UUIDUtils.getHalfId(), "head")
				.build();
		Assert.assertNotNull(config.getHeaders());
	}

	@Test
	public void check2() {
		Header build = Header.builder()
				.columnIndex(1)
				.value("HEAD")
				.build();
		SheetConfig config = SheetConfig.builder()
				.dir(basePath)
				.addHeaders(Lists.newArrayList(build))
				.addHeaders(UUIDUtils.getHalfId(), UUIDUtils.getHalfId(), "head")
				.build();
		Assert.assertNotNull(config.getHeaders());
	}

}