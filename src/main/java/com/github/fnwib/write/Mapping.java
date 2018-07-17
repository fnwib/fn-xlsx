package com.github.fnwib.write;

import com.github.fnwib.model.ExcelContent;

import java.util.List;
import java.util.Map;

/**
 * 映射关系
 */
@FunctionalInterface
public interface Mapping {

	List<ExcelContent> convert(Map<String, Object> map);
}
