package com.github.fnwib.sheet;

import com.github.fnwib.model.FnRow;
import org.apache.poi.ss.usermodel.Row;

import java.util.Iterator;
import java.util.List;

public interface FnReadSheet<T> extends Iterable<FnRow<T>> {

	/**
	 * @return
	 */
	List<Row> getBeforeHeader();

	/**
	 * header
	 *
	 * @return
	 */
	Row getHeader();

	Iterator<FnRow<T>> iterator();

}