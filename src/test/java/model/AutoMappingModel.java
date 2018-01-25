package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.Operation;
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
    @AutoMapping(title = "Text One", handlers = {ToUpperHandler.class})
    private String  text1;
    @AutoMapping(title = "Text Two")
    private String  text2;

    @AutoMapping(title = "Text Reorder", handlers = {RecordHandler.class})
    private String text3;

    @AutoMapping(title = "integer")
    private Integer    intNum;
    @AutoMapping(title = "integer")
    private Long       longNum;
    @AutoMapping(title = "Number")
    private Float      floatNum;
    @AutoMapping(title = "Number")
    private Double     doubleNum;
    @AutoMapping(title = "Number")
    private BigDecimal bigDecimal;

    @AutoMapping(title = "Number Null")
    private Integer intNumNull;

    @AutoMapping(title = "LocalDate null")
    private LocalDate       localDate;
    @AutoMapping(prefix = "LocalDate", title = "\\d+")
    private List<LocalDate> localDateList;

    @AutoMapping(prefix = "Map", title = "\\d+")
    private Map<Integer, String> intKeyMap;

    @AutoMapping(prefix = "Map", title = "[A-Z]")
    private Map<String, String> stringKeyMap;

    @AutoMapping(prefix = "Map", title = "1", suffix = "(Chinese Name)")
    private Map<Integer, String> intKeyMap2;

    @AutoMapping(prefix = "Map", title = "\\d+", suffix = "(Chinese Name)")
    private Map<Integer, String> intKeyMap3;

    @AutoMapping(title = "Excel no match")
    private Map<Integer, String> noMatchMap;

}
