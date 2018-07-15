package com.github.fnwib.databing.convert;
@Deprecated
public interface PropertyConverter extends WriteConverter, ReadConverter {

	boolean isMatched();

	/**
	 * 绑定的列
	 *
	 * @return
	 */
	default int num() {
		return 1;
	}
}
