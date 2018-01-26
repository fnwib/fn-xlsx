package model;


import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.CellType;
import com.github.fnwib.databing.title.CellTitle;
import com.github.fnwib.parse.TitleDesc;
import lombok.*;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WriteModel {
    @AutoMapping("序号")
    @CellType(title = "序号")
    private Integer                sequence;
    @AutoMapping("字符串")
    @CellType(title = "字符串")
    private String                 string;
    @AutoMapping("数字Int")
    @CellType(title = "数字Int")
    private Integer                intNum;
    @AutoMapping("数字Long")
    @CellType(title = "数字Long")
    private Long                   longNum;
    @AutoMapping("日期")
    @CellType(title = "日期")
    private LocalDate              localDate;
    //    @AutoMapping("MAP \\d+")
    @CellType(title = "MAP \\d+")
    private Map<Integer, String> mapNumber;
    //    @AutoMapping("MAP [A-Z]")
    @CellType(title = "MAP [A-Z]")
    private Map<Integer, String> mapString;

    @AutoMapping("MAP [A-Z] null")
    @CellType(title = "MAP [A-Z] null")
    private Map<Integer, String> mapNull;

    @AutoMapping("Excel no match")
    @CellType(title = "Excel no match")
    private Map<Integer, String> noMatchMap;

    @AutoMapping("AAA")
    @CellType(title = "AAA")
    private String aaa;

    @AutoMapping("enumType")
    private EnumType enumType;
}
