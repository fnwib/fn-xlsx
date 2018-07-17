package com.github.fnwib.databing.deser;


import org.apache.poi.ss.usermodel.Cell;

/**
 * cell 反序列化实现
 *
 * @param <T>
 */
@FunctionalInterface
public interface CellDeserializer<T> {

	T deserialize(Cell cell);

}
