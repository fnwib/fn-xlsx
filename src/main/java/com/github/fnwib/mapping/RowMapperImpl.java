package com.github.fnwib.mapping;

import com.github.fnwib.databing.Context;
import com.github.fnwib.databing.LineWriter;
import com.github.fnwib.databing.LocalConfig;
import com.github.fnwib.exception.NotSupportedException;
import com.github.fnwib.mapping.nested.NestedMapping;
import com.github.fnwib.mapping.model.BindColumn;
import com.github.fnwib.write.model.ExcelContent;
import com.github.fnwib.write.model.ExcelHeader;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 对象与POI ROW的转换关系实现
 * <p>
 * 对象的最大嵌套层数由LocalConfig配置
 *
 * @param <T>
 */
@Slf4j
public class RowMapperImpl<T> implements RowMapper<T> {

	private final LocalConfig localConfig;

	private Class<T> type;
	private NestedMapping<T> mapping;

	public RowMapperImpl(Class<T> type) {
		this(type, Context.INSTANCE.getContextConfig());
	}

	public RowMapperImpl(Class<T> type, LocalConfig localConfig) {
		this.type = type;
		this.localConfig = localConfig;
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
		List<ExcelHeader> headers = to(fromValue);
		return match(headers);
	}

	@Override
	public boolean match(List<ExcelHeader> headers) {
		NestedMapping<T> mapping = Mappings.createNestedMapping(type, localConfig, headers);
		List<BindColumn> columns = mapping.getColumns();
		if (columns.size() > 1) {
			this.mapping = mapping;
			return true;
		}
		return false;
	}

	private List<ExcelHeader> to(Row row) {
		List<ExcelHeader> headers = Lists.newArrayListWithCapacity(row.getLastCellNum());
		for (Cell cell : row) {
			ExcelHeader header = ExcelHeader.builder()
					.columnIndex(cell.getColumnIndex()).value(cell.getStringCellValue()).build();
			headers.add(header);
		}
		return headers;
	}

	@Override
	public Optional<T> convert(Row fromValue) {
		if (isEmpty(fromValue)) {
			return Optional.empty();
		}
		return mapping.getValue(fromValue);
	}

	@Override
	public List<ExcelContent> writeValue(T fromValue) {
		return mapping.getContents(fromValue);
	}

	@Override
	public Map<String, Object> convertToMap(Row row) {
		throw new NotSupportedException();
	}

	@Override
	public LineWriter<T> getLineWriter() {
		throw new NotSupportedException();
	}
}
