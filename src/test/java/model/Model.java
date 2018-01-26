package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.CellType;
import com.github.fnwib.annotation.Operation;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown=true)
public class Model {
    @CellType(operation = Operation.LINE_NUM)
    private Integer lineNum;
    @CellType(title = "Text One")
    private String text1;
    @CellType(title = "Text Two")
    private String text2;

    @CellType(title = "Text Reorder", operation = Operation.REORDER)
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


    @CellType(title = "yyyy-MM-dd")
    private LocalDate localDate4;
    @CellType(title = "yyyy/MM/dd")
    private LocalDate localDate5;
    @CellType(title = "yyyy/MM/dd2")
    private LocalDate localDate8;
    @CellType(title = "yyyy\\\\MM\\\\dd")
    private LocalDate localDate6;
    @CellType(title = "yyyyMMdd")
    private LocalDate localDate7;

    @CellType(title = "Map \\d+")
    private Map<Integer, String> intKeyMap;

    @CellType(title = "Map [A-Z]")
    private Map<Integer, String> stringKeyMap;

    @CellType(title = "Map 1 \\(Chinese Name\\)")
    private Map<Integer, String> intKeyMap2;

    @CellType(title = "Map \\d+ \\(Chinese Name\\)")
    private Map<Integer, String> intKeyMap3;

    @CellType(title = "Excel no match")
    private Map<Integer, String> noMatchMap;

}
