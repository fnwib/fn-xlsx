package com.github.fnwib.model;

import com.github.fnwib.write.fn.FnCellStyle;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SheetTemplateView {
	private List<PreHeader> preHeaders;
	private List<Header> headers;
	private List<String> appendHeaders;

	public SheetTemplateView() {
		this(Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList());
	}

	public SheetTemplateView(List<PreHeader> preHeaders, List<Header> headers, List<String> appendHeaders) {
		this.preHeaders = preHeaders;
		this.headers = headers;
		this.appendHeaders = appendHeaders;
	}

	/**
	 * 原数据  8，6，5
	 * 入参  3，6，5
	 * 新数据  3，6，5，8，6，5
	 *
	 * @param preHeaders
	 */
	public void prependPreHeader(List<PreHeader> preHeaders) {
		Collections.reverse(preHeaders);
		for (PreHeader preHeader : preHeaders) {
			this.preHeaders.add(0, preHeader);
		}
	}

	/**
	 * 原数据  8，6，5
	 * 入参  3，6，5
	 * 新数据  8，6，5，3，6，5
	 *
	 * @param preHeaders
	 */
	public void appendPreHeader(List<PreHeader> preHeaders) {
		for (PreHeader preHeader : preHeaders) {
			this.preHeaders.add(preHeader);
		}
	}

	/**
	 * 原数据  8，6，5
	 * 入参  3，6，5
	 * 新数据  3，6，5，8，6，5
	 *
	 * @param headers
	 */
	public void prependHeaders(List<Header> headers) {
		Collections.reverse(headers);
		for (Header header : headers) {
			this.headers.add(0, header);
		}
	}

	/**
	 * 原数据  8，6，5
	 * 入参  3，6，5
	 * 新数据  8，6，5，3，6，5
	 *
	 * @param headers
	 */
	public void appendHeaders(List<Header> headers) {
		for (Header header : headers) {
			this.headers.add(header);
		}
	}

	/**
	 * @return 数据副本 对返回值操作不会影响原值
	 */
	public List<PreHeader> getPreHeaders() {
		return Lists.newArrayList(preHeaders);
	}

	/**
	 * @return 数据副本 对返回值操作不会影响原值
	 */
	public List<Header> getHeaders() {
		List<Header> hs = Lists.newArrayList(headers);
		AtomicInteger maxColumnIndex = new AtomicInteger();
		Header.HeaderBuilder builder = Header.builder();
		if (!headers.isEmpty()) {
			int max = headers.stream().mapToInt(Header::getColumnIndex).max().getAsInt();
			maxColumnIndex.set(max);
			Header header = headers.get(0);
			FnCellStyle cellStyle = header.getCellStyle();
			builder.cellStyle(cellStyle);
			builder.width(header.getWidth());
			builder.height(header.getHeight());
		}
		for (String val : appendHeaders) {
			Header header = builder.columnIndex(maxColumnIndex.incrementAndGet())
					.value(val).build();
			hs.add(header);
		}
		return hs;
	}
}
