package com.github.fnwib.model;

import com.github.fnwib.exception.SettingException;
import com.github.fnwib.util.UUIDUtils;
import com.github.fnwib.write.fn.FnCellStyle;
import com.google.common.collect.Lists;
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
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

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

	/**
	 * 补充模板内容 先获取 view 进行修改
	 */
	@Getter
	private SheetTemplateView view;

	/**
	 * head以下所有的默认使用
	 */
	@Getter
	private FnCellStyle contentCellStyle;

	public File getEmptyFile() {
		String f = fileNameProducer.getFilename(this.filename, EXTENSION);
		return Paths.get(dir.toString(), f).toFile();
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
		view = new SheetTemplateView(builder.preHeaders, builder.headers, Lists.newArrayList(builder.appendHeaders));
		this.contentCellStyle = builder.contentCellStyle;
	}

	/**
	 * 创建header
	 *
	 * @return
	 */
	public List<Header> getHeaders() {
		return view.getHeaders();
	}

	public List<PreHeader> getPreHeaders() {
		return view.getPreHeaders();
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
		try (Stream<Path> list = Files.list(path)) {
			long size = list.count();
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
		private List<PreHeader> preHeaders;
		private List<Header> headers;
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

		public Builder addPreHeader(PreHeader val) {
			preHeaders.add(val);
			return this;
		}

		public Builder addPreHeader(int rowNum, int columnIndex, String value) {
			PreHeader preHeader = PreHeader.builder().rowNum(rowNum).columnIndex(columnIndex).value(value).build();
			preHeaders.add(preHeader);
			return this;
		}

		public Builder addPreHeader(List<PreHeader> val) {
			preHeaders.addAll(val);
			return this;
		}

		/**
		 * addHeaders(List<Header> val)
		 * 优先级大于 addHeaders(String... val)
		 *
		 * @param val
		 * @return
		 */
		public Builder addHeaders(List<Header> val) {
			for (Header h : val) {
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
