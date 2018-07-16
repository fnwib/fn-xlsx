package com.github.fnwib.mapper;

import com.github.fnwib.databing.Context;
import com.github.fnwib.databing.LineWriter;
import com.github.fnwib.databing.LocalConfig;
import com.github.fnwib.exception.NotSupportedException;
import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.mapper.nested.NestedMapper;
import com.github.fnwib.util.FnUtils;
import com.github.fnwib.write.model.ExcelContent;
import com.github.fnwib.write.model.ExcelHeader;
import com.github.fnwib.write.model.RowExcelContent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

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
public class RowReaderImpl<T> implements RowReader<T> {

	private final LocalConfig localConfig;

	private Class<T> type;
	private NestedMapper<T> mapper;

	public RowReaderImpl(Class<T> type) {
		this(type, Context.INSTANCE.getContextConfig());
	}

	public RowReaderImpl(Class<T> type, LocalConfig localConfig) {
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
		List<ExcelHeader> headers = FnUtils.to(fromValue);
		return match(headers);
	}

	@Override
	public boolean match(List<ExcelHeader> headers) {
		NestedMapper<T> mapper = Mappers.createNestedMapper(type, localConfig, headers);
		List<BindColumn> columns = mapper.getColumns();
		if (columns.size() > 1) {
			this.mapper = mapper;
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
	public RowExcelContent convert(T fromValue) {
		List<ExcelContent> contents = mapper.getContents(fromValue);
		return new RowExcelContent(contents);
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
