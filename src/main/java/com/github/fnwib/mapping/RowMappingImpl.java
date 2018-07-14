package com.github.fnwib.mapping;

import com.github.fnwib.databing.Context;
import com.github.fnwib.databing.LocalConfig;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.mapping.model.BindColumn;
import com.github.fnwib.mapping.model.BindProperty;
import com.github.fnwib.reflect.BeanResolver;
import com.github.fnwib.reflect.Property;
import com.github.fnwib.write.model.ExcelContent;
import com.github.fnwib.write.model.ExcelHeader;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
public class RowMappingImpl<T> implements RowMapping<T> {

	private final LocalConfig localConfig;

	private Class<T> type;
	private MappingHelper<T> helper;
	//所有绑定的列的数量
	private LongAdder count;

	public RowMappingImpl(Class<T> type) {
		this(type, Context.INSTANCE.getContextConfig());
	}

	public RowMappingImpl(Class<T> type, LocalConfig localConfig) {
		this.type = type;
		this.localConfig = localConfig;
		this.count = new LongAdder();
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
		LongAdder level = new LongAdder();
		LongAdder bindColumnCount = new LongAdder();
		List<BindProperty> bindProperties = getBindProperties(type, level);
		if (level.intValue() >= 3) {
			throw new SettingException("'%s'嵌套层数超过两层", type);
		}
		Set<Integer> ignoreColumns = Sets.newHashSet();
		bind(headers, bindProperties, ignoreColumns, bindColumnCount);
		if (bindColumnCount.intValue() > 0) {
			for (BindProperty property : bindProperties) {
				Mappings.createMapping(property, localConfig);
			}
			this.helper = new MappingHelper<>(type, bindProperties, localConfig);
			count.add(bindColumnCount.intValue());
			return true;
		}
		return false;
	}

	@Override
	public MappingHelper<T> getMappingHelper() {
		if (helper == null || count.intValue() <= 0) {
			throw new ExcelException("没有进行匹配或者没有匹配到");
		}
		return helper;
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

	/**
	 * 绑定
	 *
	 * @param headers         poi row
	 * @param bindProperties  规则
	 * @param ignoreColumns   不匹配的列 取决于是否配置了独占模式
	 * @param bindColumnCount 规则的有效匹配数量的计数器
	 */
	private void bind(List<ExcelHeader> headers, List<BindProperty> bindProperties, Set<Integer> ignoreColumns, LongAdder bindColumnCount) {
		//按order 顺序绑定
		bindProperties.sort(Comparator.comparing(BindProperty::getOrder));
		for (BindProperty property : bindProperties) {
			if (property.isNested()) {
				bind(headers, property.getSubBindProperties(), ignoreColumns, bindColumnCount);
				continue;
			}
			FnMatcher fnMatcher = new FnMatcher(property.getMatchConfig(), localConfig);
			List<BindColumn> columns = fnMatcher.match(headers, ignoreColumns);
			if (!columns.isEmpty()) {
				bindColumnCount.increment();
				if (property.isExclusive()) {
					columns.forEach(i -> ignoreColumns.add(i.getIndex()));
				}
			}
			property.setBindColumns(columns);
		}
	}

	/**
	 * 将类型转成规则
	 *
	 * @param type  类型
	 * @param level 嵌套层级
	 * @return
	 */
	private List<BindProperty> getBindProperties(final Class<?> type, LongAdder level) {
		level.increment();
		List<BindProperty> result = new ArrayList<>();
		for (Property property : BeanResolver.INSTANCE.getProperties(type)) {
			Optional<BindProperty> optional = property.toBindParam();
			if (!optional.isPresent()) {
				continue;
			}
			BindProperty bind = optional.get();
			if (bind.isNested()) {
				List<BindProperty> sub = getBindProperties(property.getFieldType().getRawClass(), level);
				bind.setSubBindProperties(sub);
			}
			result.add(bind);
		}
		return result;
	}


	@Override
	public Optional<T> readValue(Row fromValue) {
		if (isEmpty(fromValue)) {
			return Optional.empty();
		}
		T convert = helper.convert(fromValue);
		return Optional.of(convert);
	}


	@Override
	public List<ExcelContent> writeValue(T fromValue) {
		List<ExcelContent> contents = Lists.newArrayListWithCapacity(count.intValue());
		helper.writeValue(fromValue, contents);
		return contents;
	}


}
