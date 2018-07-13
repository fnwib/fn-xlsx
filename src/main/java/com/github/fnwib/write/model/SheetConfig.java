package com.github.fnwib.write.model;

import com.github.fnwib.exception.SettingException;
import com.github.fnwib.util.UUIDUtils;
import com.github.fnwib.write.config.FileNameProducer;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class SheetConfig {

	private static final String EXTENSION = ".xlsx";
	private static final String DASH = "-";
	private static final DecimalFormat TWO_DIGITS = new DecimalFormat("00");

	private final AtomicInteger sequence;
	private final FileNameProducer fileNameProducer;
	private final String filename;

	@Getter
	private Path dir;
	@Getter
	private int maxRowsCanWrite;
	@Getter
	private String sheetName;
	@Getter
	private List<ExcelPreHeader> preHeaders;
	@Getter
	private List<ExcelHeader> headers;

	public File getEmptyFile() {
		String filename = fileNameProducer.getFilename(this.filename, EXTENSION);
		return Paths.get(dir.toString(), filename).toFile();
	}

	public static Builder builder() {
		return new Builder();
	}

	private SheetConfig(Builder builder) {
		this.sequence = new AtomicInteger();
		this.fileNameProducer = (name, extension) -> {
			String base = TWO_DIGITS.format(sequence.incrementAndGet()) + extension;
			if (StringUtils.isNotBlank(name)) {
				return name + DASH + base;
			}
			return base;
		};
		dir = checkDir(builder.dir);
		filename = builder.fileName;
		//行号 = rowNum + 1
		maxRowsCanWrite = builder.maxRowsCanWrite - 1;
		sheetName = builder.sheetName;
		preHeaders = builder.preHeaders;
		headers = builder.headers;
	}

	private Path checkDir(String dir) {
		if (StringUtils.isBlank(dir)) {
			throw new SettingException("存放结果的文件夹不能为空");
		}
		Path path = Paths.get(dir);
		if (!Files.exists(path)) {
			throw new SettingException("存放结果的文件夹" + path.toString() + "不存在");
		}
		if (Files.isRegularFile(path)) {
			throw new SettingException(path.toString() + "不是文件夹");
		}
		if (!Files.isReadable(path)) {
			throw new SettingException(path.toString() + "不可读");
		}
		if (!Files.isWritable(path)) {
			throw new SettingException(path.toString() + "不可写");
		}
		try {
			long size = Files.list(path).count();
			if (size > 0) {
				String uuid = UUIDUtils.getHalfId();
				log.info("[{}]不为空,将在该目录下创建[{}]目录并使用该目录", dir, uuid);
				return Paths.get(dir, uuid);
			} else {
				return path;
			}
		} catch (IOException e) {
			log.error("error {}", e);
			throw new SettingException(e);
		}
	}

	public static final class Builder {
		private String dir;
		private String fileName;
		private int maxRowsCanWrite;
		private String sheetName;
		private List<ExcelPreHeader> preHeaders;
		private List<ExcelHeader> headers;

		public Builder() {
			preHeaders = Lists.newArrayList();
			headers = Lists.newArrayList();
		}

		public Builder dir(String val) {
			dir = val;
			return this;
		}

		public Builder fileName(String val) {
			fileName = val;
			return this;
		}

		public Builder maxRowsCanWrite(int val) {
			maxRowsCanWrite = val;
			return this;
		}

		public Builder sheetName(String val) {
			sheetName = val;
			return this;
		}

		public Builder addPreHeader(ExcelPreHeader val) {
			preHeaders.add(val);
			return this;
		}

		public Builder addPreHeader(List<ExcelPreHeader> val) {
			preHeaders.addAll(val);
			return this;
		}

		public Builder addHeaders(List<ExcelHeader> val) {
			headers.addAll(val);
			return this;
		}

		public SheetConfig build() {
			return new SheetConfig(this);
		}
	}
}
