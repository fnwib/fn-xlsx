package com.github.fnwib.testentity;

import com.github.fnwib.plugin.ValueHandler;
import com.github.fnwib.util.BCConvert;

public class ToSingleByteHandler implements ValueHandler {

	@Override
	public String convert(String param) {
		return BCConvert.toSingleByte(param);
	}
}
