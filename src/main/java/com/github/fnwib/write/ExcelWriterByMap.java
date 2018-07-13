package com.github.fnwib.write;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.write.fn.FnSheet;
import com.github.fnwib.write.fn.SingleSheetImpl;
import com.github.fnwib.write.model.SheetConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ExcelWriterByMap implements ExcelWriter<Map<String, String>> {
	private SheetConfig sheetConfig;
	private FnSheet fnSheet;
	private boolean closed = false;

	public ExcelWriterByMap(SheetConfig sheetConfig) {
		this.sheetConfig = sheetConfig;
	}

	private void check(int size) {
		if (closed) {
			throw new ExcelException("已经关闭");
		}
		if (fnSheet == null) {
			fnSheet = new SingleSheetImpl(sheetConfig);
		}
		if (fnSheet.canWriteSize() < size) {
			log.debug("需要写入'{}'行, 当前sheet可写入行'{}'不足,将创建一个新sheet", size, fnSheet.canWriteSize());
			fnSheet.flush();
			fnSheet = new SingleSheetImpl(sheetConfig);
			if (fnSheet.canWriteSize() < size) {
				throw new SettingException("Sheet起始可写入rowNum'%s'，最大可写入rowNum '%s'。请检查配置", fnSheet.getStartRow(), sheetConfig.getMaxRowsCanWrite());
			}
		}

	}


	@Override
	public void write(Map<String, String> element) {
		check(1);
		fnSheet.addRow(element);
	}

	@Override
	public void write(List<Map<String, String>> elements) {
		for (Map<String, String> element : elements) {
			write(element);
		}
	}

	@Override
	public void writeMergedRegion(List<Map<String, String>> elements, List<Integer> mergeIndexes) {
		check(elements.size());
		fnSheet.addMergeRow(elements, mergeIndexes);
	}

	@Override
	public void flush() {
		if (closed) {
			return;
		}
		closed = true;
		fnSheet.flush();
	}

	@Override
	public List<File> getFiles() {
		if (!closed) {
			flush();
		}
		try {
			return Files.list(sheetConfig.getDir()).map(Path::toFile).collect(Collectors.toList());
		} catch (IOException e) {
			log.error("error {}", e);
			return Collections.emptyList();
		}
	}
}
