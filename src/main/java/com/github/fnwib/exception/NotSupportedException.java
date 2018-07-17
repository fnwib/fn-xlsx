package com.github.fnwib.exception;

public class NotSupportedException extends RuntimeException {

	public NotSupportedException() {
		super();
	}

	public NotSupportedException(String msg) {
		super(msg);
	}

	public NotSupportedException(Exception e) {
		super(e);
	}

	public NotSupportedException(String msg, Exception e) {
		super(msg, e);
	}

	public NotSupportedException(String format, Object... msg) {
		super(String.format(format, msg));
	}
}