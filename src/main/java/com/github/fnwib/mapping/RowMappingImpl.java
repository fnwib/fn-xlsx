package com.github.fnwib.mapping;

import com.github.fnwib.databing.Context;
import com.github.fnwib.databing.LocalConfig;
import com.github.fnwib.mapping.nested.NestedMapping;
import com.github.fnwib.mapping.model.BindColumn;
import com.github.fnwib.write.model.ExcelContent;
import com.github.fnwib.write.model.ExcelHeader;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;
import java.util.Optional;

@Slf4j
public class RowMappingImpl<T> implements RowMapping<T> {

	private final LocalConfig localConfig;

	private Class<T> type;
	private NestedMapping<T> mapping;

	public RowMappingImpl(Class<T> type) {
		this(type, Context.INSTANCE.getContextConfig());
	}

	public RowMappingImpl(Class<T> type, LocalConfig localConfig) {
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
	public Optional<T> readValue(Row fromValue) {
		if (isEmpty(fromValue)) {
			return Optional.empty();
		}
		return mapping.getValue(fromValue);
	}


	@Override
	public List<ExcelContent> writeValue(T fromValue) {
		return mapping.getContents(fromValue);
	}


}
