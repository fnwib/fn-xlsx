package com.github.fnwib.write;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.model.SheetConfig;
import com.github.fnwib.write.fn.FnSheet;
import com.github.fnwib.write.fn.FnSheetImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author fengweibin
 * @date 2019-01-04
 */
@Slf4j
public abstract class AbstractWriter<T> implements ExcelWriter<T> {
	protected final SheetConfig sheetConfig;
	protected FnSheet fnSheet;
	protected boolean closed;

	public AbstractWriter(SheetConfig sheetConfig) {
		this.sheetConfig = sheetConfig;
	}

	protected void check(int size) {
		if (closed) {
			throw new ExcelException("已经关闭");
		}
		if (fnSheet == null) {
			fnSheet = new FnSheetImpl(sheetConfig);
		}
		if (fnSheet.canWriteSize() < size) {
			log.debug("需要写入'{}'行, 当前sheet可写入行'{}'不足,将创建一个新sheet", size, fnSheet.canWriteSize());
			fnSheet.flush();
			fnSheet = new FnSheetImpl(sheetConfig);
			if (fnSheet.canWriteSize() < size) {
				throw new SettingException("Sheet起始可写入rowNum'%s'，最大可写入rowNum '%s'。请检查配置", fnSheet.getStartRow(), sheetConfig.getMaxRowNumCanWrite());
			}
		}

	}

	@Override
	public void flush() {
		if (closed) {
			return;
		}
		closed = true;
		if (fnSheet == null) {
			fnSheet = new FnSheetImpl(sheetConfig);
		}
		fnSheet.flush();
	}

	@Override
	public List<File> getFiles() {
		if (!closed) {
			flush();
		}
		try (Stream<Path> pathStream = Files.list(sheetConfig.getDir())) {
			return pathStream.map(Path::toFile).collect(Collectors.toList());
		} catch (IOException e) {
			log.error("error {}", e);
			return Collections.emptyList();
		}
	}

}
