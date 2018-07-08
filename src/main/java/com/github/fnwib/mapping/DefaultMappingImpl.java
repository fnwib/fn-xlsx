package com.github.fnwib.mapping;

import com.github.fnwib.annotation.Operation;
import com.github.fnwib.databing.valuehandler.ValueFormatter;

import java.util.List;

public class DefaultMappingImpl<T> implements AutoMapping {

	private String name;
	private Operation operation;
	private String title;
	private List<Integer> bind;
	private List<ValueFormatter<T>> valueFormat;

	public void setName(String name) {
		this.name = name;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setBind(List<Integer> bind) {
		this.bind = bind;
	}

	public void setValueFormat(List<ValueFormatter<T>> valueFormat) {
		this.valueFormat = valueFormat;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Operation getOperation() {
		return operation;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public List<Integer> getBind() {
		return bind;
	}

	public List<ValueFormatter<T>> getValueFormat() {
		return valueFormat;
	}
}
