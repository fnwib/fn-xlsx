package com.github.fnwib.testentity;

import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.annotation.ReadValueHandler;
import com.github.fnwib.jackson.Sequence;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode
@ToString
@Getter
@Setter
public class AutoMappingModel {
	@AutoMapping(operation = Operation.LINE_NUM)
	private Integer lineNum;

	@ReadValueHandler({ToUpperHandler.class})
	@AutoMapping("Text One")
	private String text1;
	@AutoMapping(prefix = "Text (2)")
	private String text2;

	@ReadValueHandler({RecordHandler.class})
	@AutoMapping("Text Reorder")
	private String text3;

	@AutoMapping("integer")
	private Integer intNum;
	@AutoMapping("long")
	private Long longNum;
	@AutoMapping("float")
	private Float floatNum;
	@AutoMapping("double")
	private Double doubleNum;
	@AutoMapping("bigDecimal")
	private BigDecimal bigDecimal;

	@AutoMapping("Number Null")
	private Integer intNumNull;

	@AutoMapping(prefix = "LocalDate null", value = "\\d")
	private List<LocalDate> localDateHasNull;
	@AutoMapping(prefix = "LocalDate", value = "\\d+")
	private List<LocalDate> localDateList;

	@AutoMapping(prefix = "Map", value = "\\d+")
	private Map<Sequence, String> intKeyMap;

	@AutoMapping(prefix = "Map", value = "[A-Z]")
	private Map<Integer, String> stringKeyMap;

	@AutoMapping(prefix = "Map", value = "1", suffix = "(Chinese Name)")
	private Map<Integer, String> intKeyMap2;

	@AutoMapping(prefix = "Map", value = "\\d+", suffix = "(Chinese Name)", exclude = "1")
	private Map<Integer, String> intKeyMap3;

	@AutoMapping("Excel no match")
	private Map<Integer, String> noMatchMap;


}
