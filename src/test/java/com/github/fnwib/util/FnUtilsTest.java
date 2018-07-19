package com.github.fnwib.util;

import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.mapper.RowReaderImpl;
import com.github.fnwib.model.Header;
import com.github.fnwib.model.SheetConfig;
import com.github.fnwib.write.CommonPathTest;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

public class FnUtilsTest extends CommonPathTest {

	@Test
	public void toHeader() {
	}

	@Test
	public void merge() {
		String ss = "/Users/magina/Downloads/著作权歌曲登记表模版.xlsx";
		File file = Paths.get(ss).toFile();
		SheetConfig config = SheetConfig.builder().dir(basePath).build();
		FnUtils.merge(config, file, new RowReaderImpl<>(TempModel.class));
		List<Header> headers = config.getHeaders();
		for (Header header : headers) {
			System.out.println(header);
		}
	}


	private static class TempModel {
		@AutoMapping("歌曲名称")
		private String name;

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public static void main(String[] args) {
		double db2 = 1.02;
		double pow1 = Math.pow(db2, 2);
		System.out.println(pow1);
		int pow = (int) Math.pow(db2,2);
		System.out.println(pow);
	}

}