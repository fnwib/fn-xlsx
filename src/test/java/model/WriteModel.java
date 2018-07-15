package model;


import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.CellType;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.annotation.ReadValueHandler;
import com.github.fnwib.testentity.EnumType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class WriteModel {
	@CellType(operation = Operation.LINE_NUM)
	private Integer lineNum;
	@AutoMapping("序号")
	private Integer sequence;
	@AutoMapping("字符串")
	private String string;
	@AutoMapping("数字Int")
	private Integer intNum;
	@AutoMapping("数字Long")
	private Long longNum;
	@AutoMapping("日期")
	private LocalDate localDate;
	@AutoMapping(prefix = "MAP", value = "\\d+")
	private List<String> listNumber;
	@CellType(title = "MAP [A-Z]")
	private List<String> listString;

	@AutoMapping("MAP [A-Z] null")
	@CellType(title = "MAP [A-Z] null")
	private Map<Integer, String> mapNull;

	@AutoMapping("Excel no match")
	@CellType(title = "Excel no match")
	private Map<Integer, String> noMatchMap;

	@AutoMapping("AAA")
	@ReadValueHandler({ToUpperHandler.class, RecordHandler.class})
	private String aaa;

	@AutoMapping("enumType")
	private EnumType enumType;


}
