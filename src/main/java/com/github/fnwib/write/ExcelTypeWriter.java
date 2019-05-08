package com.github.fnwib.write;

import com.github.fnwib.mapper.RowMapper;
import com.github.fnwib.mapper.RowMapperImpl;
import com.github.fnwib.model.Content;
import com.github.fnwib.model.Header;
import com.github.fnwib.model.RowContent;
import com.github.fnwib.model.SheetConfig;
import com.github.fnwib.util.FnUtils;
import com.github.fnwib.util.UUIDUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author fengweibin
 * @date 2018-12-26
 */
@Slf4j
public class ExcelTypeWriter {
	/**
	 * 如果有写操作 该状态为 true
	 */
	private boolean state;
	private final RowContentWriter writer;
	private final List<RowMapper<?>> mappers;
	private int size;


	public static ExcelTypeWriter create(Row row, String dir, Class<?>... types) {
		return create(row, dir, null, types);
	}

	public static ExcelTypeWriter create(Row row, String dir, Consumer<SheetConfig.Builder> consumer, Class<?>... types) {
		Objects.requireNonNull(types);
		List<Header> beforeHeaders = FnUtils.toHeadersWithoutStyle(row).stream().map(header -> Header.builder()
				.columnIndex(header.getColumnIndex())
				.value(header.getValue())
				.height((short) (20 * 25))
				.width(500 * 10)
				.build()).collect(Collectors.toList());
		AtomicInteger sequence;
		if (row == null) {
			sequence = new AtomicInteger();
		} else {
			sequence = new AtomicInteger(row.getLastCellNum());
		}
		List<RowMapper<?>> mappers = new ArrayList<>(types.length);
		List<Header> appendHeaders = new ArrayList<>(types.length * 10);
		for (Class<?> type : types) {
			RowMapper<?> mapper = new RowMapperImpl<>(type);
			List<Header> headers = FnUtils.getHeaders(sequence, type);
			boolean match = mapper.match(headers);
			if (!match) {
				throw new IllegalArgumentException("unknown error");
			}
			mappers.add(mapper);
			appendHeaders.addAll(headers);
		}
		SheetConfig.Builder builder = SheetConfig.builder()
				.addHeaders(beforeHeaders)
				.addHeaders(appendHeaders)
				.maxRowNumCanWrite(200000)
				.fileName(UUIDUtils.getId())
				.dir(dir);
		if (consumer != null) {
			consumer.accept(builder);
		}
		return new ExcelTypeWriter(builder.build(), mappers, appendHeaders.size());
	}

	private ExcelTypeWriter(SheetConfig sheetConfig, List<RowMapper<?>> mappers, int size) {
		state = false;
		writer = new RowContentWriter(sheetConfig);
		this.mappers = mappers;
		this.size = size;
	}

	List<Cell> rowToCells(Row row) {
		if (row == null) {
			return Collections.emptyList();
		}
		List<Cell> cells = Lists.newArrayListWithExpectedSize(row.getLastCellNum());
		for (Cell cell : row) {
			cells.add(cell);
		}
		return cells;
	}

	List<Content> toContents(List values) {
		List<Content> contents = Lists.newArrayListWithCapacity(size);
		for (int i = 0; i < mappers.size(); i++) {
			RowMapper mapper = mappers.get(i);
			Object value = values.get(i);
			if (value == null) {
				continue;
			}
			if (mapper.support(value.getClass())) {
				contents.addAll(mapper.convert(value));
			}
		}
		contents.sort(Comparator.comparing(Content::getColumnIndex));
		return contents;
	}

	public void write(Row row, Object... values) {
		write(row, Lists.newArrayList(values));
	}

	public void write(Row row, List<Object> values) {
		if (values == null) {
			writeContents(row, Collections.emptyList());
		} else if (values.size() != mappers.size()) {
			throw new IllegalArgumentException("types size  against values size");
		} else {
			List<Content> contents = toContents(values);
			writeContents(row, contents);
		}
	}

	public void writeContents(Row row, List<Content> contents) {
		List<Cell> cells = rowToCells(row);
		RowContent rowContent = new RowContent(cells, contents);
		writer.write(rowContent);
		if (!state) {
			state = true;
		}
	}

	public void flush() {
		if (state) {
			writer.flush();
		}
	}

	public List<File> getFiles() {
		if (state) {
			return writer.getFiles();
		}
		return Collections.emptyList();
	}
}
