package com.github.fnwib.model;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SheetTemplateViewTest {

	@Test
	public void getPreHeaders() {
		List<PreHeader> a = new ArrayList<>();
		a.add(PreHeader.builder().rowNum(0).columnIndex(2).value("2").build());
		SheetTemplateView view = new SheetTemplateView(a, Collections.emptyList(), Collections.emptyList());

		PreHeader p3 = PreHeader.builder().rowNum(0).columnIndex(3).value("3").build();
		PreHeader p4 = PreHeader.builder().rowNum(0).columnIndex(4).value("4").build();
		view.appendPreHeader(Lists.newArrayList(p3, p4));

		PreHeader p0 = PreHeader.builder().rowNum(0).columnIndex(0).value("0").build();
		PreHeader p1 = PreHeader.builder().rowNum(0).columnIndex(1).value("1").build();
		view.prependPreHeader(Lists.newArrayList(p0, p1));

		List<PreHeader> preHeaders = view.getPreHeaders();
		List<Integer> result = preHeaders.stream().map(PreHeader::getValue).map(Integer::parseInt).collect(Collectors.toList());
		List<Integer> integers = Lists.newArrayList(0, 1, 2, 3, 4);
		Assert.assertArrayEquals("PreHeader", integers.toArray(), result.toArray());
	}

	@Test
	public void getHeaders() {
		List<Header> a = new ArrayList<>();
		a.add(Header.builder().columnIndex(2).value("2").build());
		SheetTemplateView view = new SheetTemplateView(Collections.emptyList(), a, Lists.newArrayList("5"));

		Header p3 = Header.builder().columnIndex(3).value("3").build();
		Header p4 = Header.builder().columnIndex(4).value("4").build();
		view.appendHeaders(Lists.newArrayList(p3, p4));

		Header p0 = Header.builder().columnIndex(0).value("0").build();
		Header p1 = Header.builder().columnIndex(1).value("1").build();
		view.prependHeaders(Lists.newArrayList(p0, p1));

		List<Header> preHeaders = view.getHeaders();
		List<Integer> result = preHeaders.stream().map(Header::getValue).map(Integer::parseInt).collect(Collectors.toList());
		List<Integer> integers = Lists.newArrayList(0, 1, 2, 3, 4, 5);
		Assert.assertArrayEquals("PreHeader", integers.toArray(), result.toArray());
	}

}