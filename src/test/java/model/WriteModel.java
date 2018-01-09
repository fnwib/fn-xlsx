package model;


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
    @CellType(title = "字符串")
    private String                 string;
    @CellType(title = "数字Int")
    private Integer                intNum;
    @CellType(title = "数字Long")
    private Long                   longNum;
    @CellType(title = "日期")
    private LocalDate              localDate;
    @CellType(title = "MAP \\d+")
    private Map<TitleDesc, String> mapNumber;
    @CellType(title = "MAP [A-Z]")
    private Map<TitleDesc, String> mapString;

    @CellType(title = "MAP [A-Z] null")
    private Map<TitleDesc, String> mapNull;


    @CellType(title = "AAA")
    private String aaa;
}
