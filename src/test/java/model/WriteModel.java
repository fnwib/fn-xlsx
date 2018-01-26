package model;


import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.CellType;
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
    @AutoMapping(title = "序号")
    @CellType(title = "序号")
    private Integer                sequence;
    @AutoMapping(title = "字符串")
    @CellType(title = "字符串")
    private String                 string;
    @AutoMapping(title = "数字Int")
    @CellType(title = "数字Int")
    private Integer                intNum;
    @AutoMapping(title = "数字Long")
    @CellType(title = "数字Long")
    private Long                   longNum;
    @AutoMapping(title = "日期")
    @CellType(title = "日期")
    private LocalDate              localDate;
    @AutoMapping(title = "MAP \\d+")
    @CellType(title = "MAP \\d+")
    private Map<TitleDesc, String> mapNumber;
    @AutoMapping(title = "MAP [A-Z]")
    @CellType(title = "MAP [A-Z]")
    private Map<TitleDesc, String> mapString;

    @AutoMapping(title = "MAP [A-Z] null")
    @CellType(title = "MAP [A-Z] null")
    private Map<TitleDesc, String> mapNull;

    @AutoMapping(title = "Excel no match")
    @CellType(title = "Excel no match")
    private Map<TitleDesc, String> noMatchMap;

    @AutoMapping(title = "AAA")
    @CellType(title = "AAA")
    private String aaa;
}
