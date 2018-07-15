package com.github.fnwib.testentity;

import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.ComplexEnum;
import com.github.fnwib.annotation.LineNum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@ToString
@EqualsAndHashCode
@Getter
@Setter
public class TestModel {
	@LineNum
	private Integer lineNum;
	@AutoMapping("序号")
	private Integer sequence;
	@AutoMapping("字符串")
	private String string;
	@AutoMapping("数字")
	private Integer intNum;
	@AutoMapping("日期")
	private LocalDate localDate;
	@AutoMapping(prefix = "集合", value = "\\d+")
	private List<String> list;
	@AutoMapping(prefix = "集合2", value = "\\d+")
	private List<String> list2;
	@AutoMapping("MAP [A-Z] null")
	private Map<Integer, String> mapNull;
	@AutoMapping("Excel no match")
	private Map<Integer, String> noMatchMap;
	@AutoMapping("枚举")
	private EnumType enumType;
	@AutoMapping(complex = ComplexEnum.Nested, order = 1)
	private TestNested testNested;
}