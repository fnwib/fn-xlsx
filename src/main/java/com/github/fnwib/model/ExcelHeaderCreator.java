package com.github.fnwib.model;

import com.github.fnwib.exception.SettingException;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcelHeaderCreator {

	public static List<ExcelHeader> create(AtomicInteger incrementColumnIndex, Iterable<String> values) {
		Objects.requireNonNull(incrementColumnIndex);
		Objects.requireNonNull(values);
		if (incrementColumnIndex.get() < 0) {
			throw new SettingException("columnIndex > 0");
		}
		List<ExcelHeader> h = Lists.newArrayList();
		for (String value : values) {
			ExcelHeader header = ExcelHeader.builder().columnIndex(incrementColumnIndex.getAndIncrement())
					.value(value)
					.build();
			h.add(header);
		}
		return h;
	}

	public static List<ExcelHeader> create(AtomicInteger incrementColumnIndex, String... values) {
		Objects.requireNonNull(values);
		return create(incrementColumnIndex, Lists.newArrayList(values));
	}

}
