package com.github.fnwib.mapping.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.poi.ss.usermodel.Cell;

import java.util.List;

public class Constant {

	static final JavaType LIST_CELL_TYPE = TypeFactory.defaultInstance().constructCollectionType(List.class, Cell.class);
}
