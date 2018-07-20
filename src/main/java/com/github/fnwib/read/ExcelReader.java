package com.github.fnwib.read;


import java.util.List;

/**
 * 可以自行扩展方法
 *
 * @param <T>
 */
public interface ExcelReader<T> extends AutoCloseable {

	String getPreTitle(int rowNum, int cellNum);

	boolean findTitle();

	/**
	 * 只在前几行查找title
	 *
	 * @param num
	 * @return
	 */
	boolean findTitle(int num);

	List<T> getData();

	/**
	 * 是否还能读取数据
	 *
	 * @return
	 */
	boolean hasNext();

	/**
	 * 取所有的数据 忽略空行
	 *
	 * @return
	 */
	List<T> fetchAllData();

	/**
	 * 取前n条数据 忽略空行
	 *
	 * @param length
	 * @return
	 */
	List<T> fetchData(int length);


	@Override
	void close();
}
