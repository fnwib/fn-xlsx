package model;

import com.github.fnwib.annotation.CellType;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.annotation.ReadValueHandler;
import com.github.fnwib.databing.title.Sequence;
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
	@CellType(operation = Operation.LINE_NUM)
	private Integer lineNum;

	@ReadValueHandler({ToUpperHandler.class})
	@CellType(title = "Text One")
	private String text1;
	@CellType(prefix = "Text (2)")
	private String text2;

	@ReadValueHandler({RecordHandler.class})
	@CellType(title = "Text Reorder")
	private String text3;

	@CellType(title = "integer")
	private Integer intNum;
	@CellType(title = "long")
	private Long longNum;
	@CellType(title = "float")
	private Float floatNum;
	@CellType(title = "double")
	private Double doubleNum;
	@CellType(title = "bigDecimal")
	private BigDecimal bigDecimal;

	@CellType(title = "Number Null")
	private Integer intNumNull;

	@CellType(prefix = "LocalDate null", title = "\\d")
	private List<LocalDate> localDateHasNull;
	@CellType(prefix = "LocalDate", title = "\\d+")
	private List<LocalDate> localDateList;

	@CellType(prefix = "Map", title = "\\d+")
	private Map<Sequence, String> intKeyMap;

	@CellType(prefix = "Map", title = "[A-Z]")
	private Map<Integer, String> stringKeyMap;

	@CellType(prefix = "Map", title = "1", suffix = "(Chinese Name)")
	private Map<Integer, String> intKeyMap2;

	@CellType(prefix = "Map", title = "\\d+", suffix = "(Chinese Name)", exclude = "1")
	private Map<Integer, String> intKeyMap3;

	@CellType(title = "Excel no match")
	private Map<Integer, String> noMatchMap;


}
