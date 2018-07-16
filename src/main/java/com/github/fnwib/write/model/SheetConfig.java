package com.github.fnwib.write.model;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.util.UUIDUtils;
import com.github.fnwib.write.config.FileNameProducer;
import com.github.fnwib.write.fn.FnCellStyle;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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
import java.util.Map;
import java.util.Set;
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
	/**
	 * 允许写入最大的rowNum
	 */
	@Getter
	private int maxRowNumCanWrite;
	@Getter
	private String sheetName;
	@Getter
	private List<ExcelPreHeader> preHeaders;
	// Template
	private List<ExcelHeader> prependHeaders;
	// 优先添加
	private List<ExcelHeader> headers;
	// LinkedHashSet 如果headers不为空 就去header的第一个元素的配置
	private Set<String> appendHeaders;
	/**
	 * head以下所有的默认使用
	 */
	@Getter
	private FnCellStyle contentCellStyle;

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
		maxRowNumCanWrite = builder.maxRowNumCanWrite;
		sheetName = builder.sheetName;
		preHeaders = builder.preHeaders;
		prependHeaders = Lists.newArrayList();
		headers = builder.headers;
		appendHeaders = builder.appendHeaders;
		this.contentCellStyle = builder.contentCellStyle;
	}

	public void prependHeaders(List<ExcelHeader> headers) {
		this.prependHeaders.addAll(headers);
	}

	private ExcelHeader.ExcelHeaderBuilder getHeaderBuilder() {
		ExcelHeader.ExcelHeaderBuilder builder = ExcelHeader.builder();
		if (!prependHeaders.isEmpty()) {
			ExcelHeader header = headers.get(0);
			FnCellStyle cellStyle = header.getCellStyle();
			builder.cellStyle(cellStyle);
			builder.width(header.getWidth());
			builder.height(header.getHeight());
			return builder;
		}
		if (!headers.isEmpty()) {
			ExcelHeader header = headers.get(0);
			FnCellStyle cellStyle = header.getCellStyle();
			builder.cellStyle(cellStyle);
			builder.width(header.getWidth());
			builder.height(header.getHeight());
			return builder;
		}
		return builder;
	}

	/**
	 * 创建header
	 *
	 * @return
	 */
	public List<ExcelHeader> getHeaders() {
		List<ExcelHeader> hs = Lists.newArrayList();
		AtomicInteger maxColumnIndex = new AtomicInteger();
		ExcelHeader.ExcelHeaderBuilder builder = getHeaderBuilder();
		for (ExcelHeader prependHeader : prependHeaders) {
			maxColumnIndex.set(Math.max(prependHeader.getColumnIndex(), maxColumnIndex.get()));
			hs.add(prependHeader);
		}
		for (ExcelHeader header : headers) {
			maxColumnIndex.set(Math.max(header.getColumnIndex(), maxColumnIndex.get()));
			hs.add(header);
		}
		for (String val : appendHeaders) {
			ExcelHeader header = builder.columnIndex(maxColumnIndex.incrementAndGet())
					.value(val).build();
			hs.add(header);
		}
		checkRepeatHead(hs);
		return hs;
	}

	private void checkRepeatHead(List<ExcelHeader> headers) {
		Map<String, ExcelHeader> map = Maps.newHashMapWithExpectedSize(headers.size());
		for (ExcelHeader header : headers) {
			String key = header.getValue().toLowerCase();
			if (map.containsKey(key)) {
				ExcelHeader exist = map.get(key);
				throw new ExcelException("存在重复的title,index [%s,%s] value [%s] ", exist.getColumnIndex(), header.getColumnIndex(), header.getValue());
			}
			map.put(key, header);
		}
	}

	/**
	 * 检查dir
	 * <p>
	 * 如果dir不为空会新建一个子文件夹使用
	 *
	 * @param dir
	 * @return
	 */
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
		private int maxRowNumCanWrite;
		private String sheetName;
		private List<ExcelPreHeader> preHeaders;
		private List<ExcelHeader> headers;
		private Set<String> appendHeaders;
		private FnCellStyle contentCellStyle;

		public Builder() {
			preHeaders = Lists.newArrayList();
			headers = Lists.newArrayList();
			appendHeaders = Sets.newLinkedHashSet();
		}

		public Builder dir(String val) {
			dir = val;
			return this;
		}

		public Builder fileName(String val) {
			fileName = val;
			return this;
		}

		public Builder maxRowNumCanWrite(int val) {
			maxRowNumCanWrite = val;
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

		public Builder addPreHeader(int rowNum, int columnIndex, String value) {
			ExcelPreHeader preHeader = ExcelPreHeader.builder().rowNum(rowNum).columnIndex(columnIndex).value(value).build();
			preHeaders.add(preHeader);
			return this;
		}

		public Builder addPreHeader(List<ExcelPreHeader> val) {
			preHeaders.addAll(val);
			return this;
		}

		/**
		 * addHeaders(List<ExcelHeader> val)
		 * 优先级大于 addHeaders(String... val)
		 *
		 * @param val
		 * @return
		 */
		public Builder addHeaders(List<ExcelHeader> val) {
			for (ExcelHeader h : val) {
				if (StringUtils.isNotBlank(h.getValue())) {
					headers.add(h);
				}
			}
			return this;
		}

		public Builder addHeaders(String... val) {
			for (String h : val) {
				if (StringUtils.isNotBlank(h)) {
					appendHeaders.add(h);
				}
			}
			return this;
		}

		public Builder addHeaders(Iterable<String> val) {
			for (String h : val) {
				if (StringUtils.isNotBlank(h)) {
					appendHeaders.add(h);
				}
			}
			return this;
		}

		public Builder contentCellStyle(FnCellStyle val) {
			contentCellStyle = val;
			return this;
		}

		public SheetConfig build() {
			return new SheetConfig(this);
		}
	}
}
