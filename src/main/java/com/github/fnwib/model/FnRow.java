package com.github.fnwib.model;

import lombok.Getter;
import org.apache.poi.ss.usermodel.Row;

import java.util.Objects;

@Getter
public final class FnRow<T> {

	private Row row;
	private boolean error;
	private T value;
	private String errorMsg;

	public FnRow(Row row, T value) {
		this(row, false, Objects.requireNonNull(value), null);
	}

	public FnRow(Row row, String error) {
		this(row, true, null, Objects.requireNonNull(error));
	}

	private FnRow(Row row, boolean error, T value, String errorMsg) {
		this.row = row;
		this.error = error;
		this.value = value;
		this.errorMsg = errorMsg;
	}

	public int getLineNum() {
		return row.getRowNum() +1;
	}

	public Row getRow() {
		return row;
	}

	public boolean isError() {
		return error;
	}

	public T getValue() {
		return value;
	}

	public String getErrorMsg() {
		return errorMsg;
	}
}
