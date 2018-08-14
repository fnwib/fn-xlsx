package com.github.fnwib.mapper;

import com.github.fnwib.context.Context;
import com.github.fnwib.context.LocalConfig;
import com.github.fnwib.mapper.nested.NestedMapper;
import com.github.fnwib.model.Content;
import com.github.fnwib.model.Header;
import com.github.fnwib.util.FnUtils;
import com.google.common.collect.Lists;
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
	private final int skip;

	public RowMapperImpl(Class<T> type) {
		this(type, Context.INSTANCE.getContextConfig(), -1);
	}

	public RowMapperImpl(Class<T> type, int skip) {
		this(type, Context.INSTANCE.getContextConfig(), skip);
	}

	public RowMapperImpl(Class<T> type, LocalConfig localConfig) {
		this(type, localConfig, -1);
	}

	/**
	 * @param type
	 * @param localConfig
	 * @param skip        匹配时候跳过的列索引(columnIndex)  default -1
	 */
	public RowMapperImpl(Class<T> type, LocalConfig localConfig, int skip) {
		this.type = type;
		this.localConfig = localConfig;
		this.skip = Math.max(skip, -1);
	}

	@Override
	public boolean isEmpty(Row row) {
		if (row == null) {
			return true;
		}
		for (Cell cell : row) {
			if (cell.getColumnIndex() <= skip) {
				continue;
			}
			if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean match(Row fromValue) {
		List<Header> headers = FnUtils.toHeaders(fromValue);
		return match(headers);
	}

	@Override
	public boolean match(List<Header> headers) {
		List<Header> skipHeaders = headers.stream().filter(header -> header.getColumnIndex() > skip).collect(Collectors.toList());
		NestedMapper<T> readMapper = Mappers.createNestedMapper(type, localConfig, skipHeaders);
		if (!readMapper.getColumns().isEmpty()) {
			this.mapper = readMapper;
			return true;
		}
		return false;
	}

	@Override
	public Optional<T> convert(Row fromValue) {
		if (isEmpty(fromValue)) {
			return Optional.empty();
		}
		return mapper.getValue(fromValue);
	}


	@Override
	public List<Cell> getSkipCells(Row row) {
		if (skip == -1) {
			return Collections.emptyList();
		}
		List<Cell> cells = Lists.newArrayListWithExpectedSize(skip + 1);
		for (Cell cell : row) {
			if (cell.getColumnIndex() > skip) {
				continue;
			}
			cells.add(cell);
		}
		return cells;
	}


	@Override
	public List<Content> convert(T fromValue) {
		return mapper.getContents(fromValue);
	}


}
