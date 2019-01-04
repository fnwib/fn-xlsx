package com.github.fnwib.read;

import com.github.fnwib.model.FnRow;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

public interface FnRowReader<T> extends AutoCloseable {
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

	/**
	 * hasNext
	 *
	 * @return
	 */
	boolean hasNext();

	/**
	 * next
	 *
	 * @return
	 */
	FnRow<T> next();

	/**
	 * close workbook
	 */
	@Override
	void close();
}