package com.github.fnwib.model;

import com.github.fnwib.exception.SettingException;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class HeaderCreator {

	public static List<Header> create(AtomicInteger incrementColumnIndex, Iterable<String> values) {
		Objects.requireNonNull(incrementColumnIndex);
		Objects.requireNonNull(values);
		if (incrementColumnIndex.get() < 0) {
			throw new SettingException("columnIndex > 0");
		}
		List<Header> h = Lists.newArrayList();
		for (String value : values) {
			Header header = Header.builder().columnIndex(incrementColumnIndex.getAndIncrement())
					.value(value)
					.build();
			h.add(header);
		}
		return h;
	}

	public static List<Header> create(AtomicInteger incrementColumnIndex, String... values) {
		Objects.requireNonNull(values);
		return create(incrementColumnIndex, Lists.newArrayList(values));
	}

}
