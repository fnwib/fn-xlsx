package com.github.fnwib.model;

import com.google.common.collect.Lists;

import java.util.List;

public class SheetTemplateView {
	private List<PreHeader> preHeaders;
	private List<Header> headers;

	public SheetTemplateView() {
		this.preHeaders = Lists.newArrayList();
		this.headers = Lists.newArrayList();
	}

	public SheetTemplateView(List<PreHeader> preHeaders, List<Header> headers) {
		this.preHeaders = preHeaders;
		this.headers = headers;
	}

	public void prependPreHeader(List<PreHeader> preHeaders) {
		for (int i = preHeaders.size() - 1; i <= 0; i--) {
			PreHeader preHeader = preHeaders.get(i);
			this.preHeaders.add(0, preHeader);
		}
	}

	public void appendPreHeader(List<PreHeader> preHeaders) {
		for (PreHeader preHeader : preHeaders) {
			this.preHeaders.add(preHeader);
		}
	}

	public void prependHeaders(List<Header> headers) {
		for (int i = headers.size() - 1; i <= 0; i--) {
			Header header = headers.get(i);
			this.headers.add(0, header);
		}
	}

	public void appendHeaders(List<Header> headers) {
		for (Header header : headers) {
			this.headers.add(header);
		}
	}

	public List<PreHeader> getPreHeaders() {
		return preHeaders;
	}

	public List<Header> getHeaders() {
		return headers;
	}
}
