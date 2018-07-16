package com.github.fnwib.mapper.flat;

import com.github.fnwib.exception.SettingException;
import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.write.model.ExcelContent;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Row;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * LineNum is rowNum + 1
 * <p>
 * 如果被绑定列 可以将改值写出到
 */
public class LineNumMapper implements FlatMapper {
	private BindColumn column;

	public LineNumMapper() {
		this(Collections.emptyList());
	}


	public LineNumMapper(List<BindColumn> columns) {
		if (columns.size() > 2) {
			throw new SettingException("LineNum匹配到多列");
		}
		if (columns.isEmpty()) {
			this.column = null;
		} else {
			this.column = columns.get(0);

		}
	}

	@Override
	public List<BindColumn> getColumns() {
		return Objects.isNull(column) ? Collections.emptyList() : Lists.newArrayList(column);
	}

	@Override
	public Optional<Integer> getValue(Row row) {
		int s = row.getRowNum() + 1;
		return Optional.of(s);
	}

	@Override
	public List<ExcelContent> getContents(Object value) {
		if (Objects.isNull(column)) {
			return Collections.emptyList();
		}
		Integer index = column.getIndex();
		String val = Objects.isNull(value) ? null : value.toString();
		ExcelContent content = new ExcelContent(index, val);
		return Lists.newArrayList(content);
	}
}
