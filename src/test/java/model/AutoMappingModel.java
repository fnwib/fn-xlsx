package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.annotation.ReadValueHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AutoMappingModel {
    @AutoMapping(operation = Operation.LINE_NUM)
    private Integer lineNum;
    @ReadValueHandler({ToUpperHandler.class})
    @AutoMapping(value = "Text One")
    private String  text1;
    @AutoMapping("Text Two")
    private String  text2;

    @ReadValueHandler({RecordHandler.class})
    @AutoMapping(value = "Text Reorder")
    private String text3;

    @AutoMapping("integer")
    private Integer    intNum;
    @AutoMapping("long")
    private Long       longNum;
    @AutoMapping("float")
    private Float      floatNum;
    @AutoMapping("double")
    private Double     doubleNum;
    @AutoMapping("bigDecimal")
    private BigDecimal bigDecimal;

    @AutoMapping("Number Null")
    private Integer intNumNull;

    @AutoMapping("LocalDate null")
    private LocalDate       localDate;
    @AutoMapping(prefix = "LocalDate", value = "\\d+")
    private List<LocalDate> localDateList;

    @AutoMapping(prefix = "Map", value = "\\d+")
    private Map<Integer, String> intKeyMap;

    @AutoMapping(prefix = "Map", value = "[A-Z]")
    private Map<Integer, String> stringKeyMap;

    @AutoMapping(prefix = "Map", value = "1", suffix = "(Chinese Name)")
    private Map<Integer, String> intKeyMap2;

    @AutoMapping(prefix = "Map", value = "\\d+", suffix = "(Chinese Name)", exclude = "1")
    private Map<Integer, String> intKeyMap3;

    @AutoMapping("Excel no match")
    private Map<Integer, String> noMatchMap;

}
