package com.github.fnwib.write.fn;

public class FnCellStyles {

	public static FnCellStyle getOrDefault(FnCellStyle fnCellStyle, FnCellStyleType type) {
		if (fnCellStyle == null) {
			return type.getStyle();
		}
		return fnCellStyle;
	}


}
