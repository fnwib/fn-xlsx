package model;

import com.github.fnwib.annotation.CellType;
import com.github.fnwib.annotation.Operation;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
public class Model {
    @CellType(type = Operation.LINE_NUM)
    private Integer lineNum;

    @CellType(title = "Text One")
    private String text1;
    @CellType(title = "Text Two")
    private String text2;

    @CellType(title = "Text Reorder", type = Operation.REORDER)
    private String text3;

    @CellType(title = "Number")
    private Integer    intNum;
    @CellType(title = "Number")
    private Long       longNum;
    @CellType(title = "Number")
    private Float      floatNum;
    @CellType(title = "Number")
    private Double     doubleNum;
    @CellType(title = "Number")
    private BigDecimal bigDecimal;

    @CellType(title = "Number Null")
    private Integer intNumNull;

    @CellType(title = "LocalDate1")
    private LocalDate localDate1;
    @CellType(title = "LocalDate2")
    private LocalDate localDate2;
    @CellType(title = "LocalDate3")
    private LocalDate localDate3;

    @CellType(title = "Map \\d+")
    private Map<Integer, String> intKeyMap;

    @CellType(title = "Map [A-Z]")
    private Map<String, String> StringKeyMap;

    @CellType(title = "Map 1 \\(Chinese Name\\)")
    private Map<Integer, String> intKeyMap2;

    @CellType(title = "Map \\d+ \\(Chinese Name\\)")
    private Map<Integer, String> intKeyMap3;

}
