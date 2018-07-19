package com.github.fnwib.write.fn;

import com.github.fnwib.model.Header;

@FunctionalInterface
public interface MapKeyMapping {

	int getIndex(String value, Header header);
}
