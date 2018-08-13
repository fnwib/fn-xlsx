package com.github.fnwib.mapper;

import com.github.fnwib.context.Context;
import com.github.fnwib.context.LocalConfig;
import com.github.fnwib.mapper.nested.NestedMapper;
import com.github.fnwib.model.Content;
import com.github.fnwib.model.Header;
import com.github.fnwib.model.RowContent;
import com.github.fnwib.util.FnUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 对象与POI ROW的转换关系实现
 * <p>
 * 1 对象的最大嵌套层数由LocalConfig配置
 * 2 匹配跳过的列配置
 *
 * @param <T>
 */
@Slf4j
public class RowMapperImpl<T> implements RowMapper<T> {

	private final LocalConfig localConfig;

	private final Class<T> type;
	private NestedMapper<T> mapper;
	private int skip;

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
	 * @param skip        匹配时候跳过的列索引(columnIndex)  default -1  form 0
	 */
	public RowMapperImpl(Class<T> type, LocalConfig localConfig, int skip) {
		this.type = type;
		this.localConfig = localConfig;
		this.skip = Math.max(skip, -1);
	}

	public RowMapperImpl<T> skip(int skip) {
		this.skip = Math.max(skip, -1);
		return this;
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
		List<Header> headers = FnUtils.toHeader(fromValue);
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
	public RowContent convert(T fromValue) {
		List<Content> contents = mapper.getContents(fromValue);
		return new RowContent(contents);
	}

}
