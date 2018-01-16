package model;

import com.github.fnwib.annotation.AutoParse;
import com.github.fnwib.handler.ValueHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Queue;

@Getter
@Setter
@ToString
public class AutoParseModel {
    //    @AutoParse(operation = Operation.LINE_NUM)
//    private Integer lineNum;
//
    @AutoParse(title = "Text One")
    private String       text1;
    //    @AutoParse(title = "Text Two")
//    private String text2;
//
//    @AutoParse(title = "Text Reorder", operation = Operation.REORDER)
//    private String text3;
//
//    @AutoParse(title = "Number")
//    private Integer intNum;
//    @AutoParse(title = "Number")
//    private Long    longNum;
//    @AutoParse(title = "Number")
//    private Float   floatNum;
//
//    @AutoParse(title = "Map \\d+")
//    private Map<Integer, String> intKeyMap;
//
    private List<Model>  list;
    private List<String> list2;
//    private String[]     arr;

    private Queue<String> queue;

//    private TestEnum testEnum;

}
