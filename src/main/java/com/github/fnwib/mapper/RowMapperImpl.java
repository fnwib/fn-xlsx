package com.github.fnwib.mapper;

import com.github.fnwib.context.Context;
import com.github.fnwib.context.LocalConfig;
import com.github.fnwib.mapper.nested.NestedMapper;
import com.github.fnwib.model.Content;
import com.github.fnwib.model.Header;
import com.github.fnwib.util.FnUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 对象与POI ROW的转换关系实现
 * <p>
 * 1 对象的最大嵌套层数由LocalConfig配置
 * 2 匹配跳过的列配置 小于等于 skip的列均被忽略
 *
 * @param <T>
 */
@Slf4j
public class RowMapperImpl<T> implements RowMapper<T> {

	private final LocalConfig localConfig;

	private final Class<T> type;
	private NestedMapper<T> mapper;
	private final int from;
	private final int to;

	public RowMapperImpl(Class<T> type) {
		this(type, Context.INSTANCE.getContextConfig(), 0, Integer.MAX_VALUE);
	}

	public RowMapperImpl(Class<T> type, LocalConfig localConfig, int from, int to) {
		if (from >= to) {
			throw new IllegalArgumentException("from must lt to");
		}
		if (from < 0 && to < 0) {
			throw new IllegalArgumentException("from and to must gt 0");
		}
		this.type = type;
		this.localConfig = localConfig;
		this.from = from;
		this.to = to;
	}

	@Override
	public boolean support(Class<?> type) {
		if (type == null) {
			return false;
		}
		return this.type == type;
	}

	@Override
	public RowMapper<T> of(int from, int to) {
		return new RowMapperImpl<>(type, localConfig, from, to);
	}

	@Override
	public boolean isEmpty(Row row) {
		if (row == null) {
			return true;
		}
		for (Cell cell : row) {
			if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean match(Row fromValue) {
		List<Header> headers = FnUtils.toHeadersWithoutStyle(fromValue);
		return match(headers);
	}

	@Override
	public boolean match(List<Header> headers) {
		List<Header> skipHeaders = headers.stream()
				.filter(header -> header.getColumnIndex() >= from)
				.filter(header -> header.getColumnIndex() < to)
				.collect(Collectors.toList());
		NestedMapper<T> readMapper = Mappers.createNestedMapper(type, localConfig, skipHeaders);
		if (readMapper.getColumns().isEmpty()) {
			return false;
		}
		this.mapper = readMapper;
		return true;
	}

	@Override
	public Optional<T> convert(Row fromValue) {
		return mapper.getValue(fromValue);
	}

	@Override
	public List<Content> convert(T fromValue) {
		if (fromValue == null) {
			return Collections.emptyList();
		}
		return mapper.getContents(fromValue);
	}


}
