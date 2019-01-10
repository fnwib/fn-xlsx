package com.github.fnwib.read;

import com.github.fnwib.model.FnRow;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Iterator;
import java.util.List;

public interface FnRowReader<T> extends AutoCloseable, Iterable<FnRow<T>> {
	/**
	 * @return current sheet
	 */
	Sheet getSheet();

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

	/**
	 * close workbook
	 */
	@Override
	void close();
}