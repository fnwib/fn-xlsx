package com.github.fnwib.write;

import com.github.fnwib.model.ExcelHeader;
import com.github.fnwib.util.UUIDUtils;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CommonPathTest {

	protected String basePath;

	@Before
	public void initDate() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		String dir = classLoader.getResource("test-file").getFile();
		Path path = Paths.get(dir, UUIDUtils.getHalfId());
		Files.createDirectory(path);
		basePath = path.toString();
	}

	@After
	public void deleteData() throws IOException {
		Path path = Paths.get(basePath);
		Files.list(path).forEach(path1 -> {
			try {
				Files.deleteIfExists(path1);
			} catch (IOException e) {

			}
		});
		Files.deleteIfExists(path);

	}

	protected List<ExcelHeader> getHeaders(int size) {
		List<ExcelHeader> headers = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			ExcelHeader build = ExcelHeader.builder()
					.id("id:" + i)
					.columnIndex(i)
					.value("head " + i)
					.height(((short) 600))
					.width(4000)
					.build();
			headers.add(build);
		}
		return headers;
	}



}
