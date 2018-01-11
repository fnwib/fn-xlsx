package com.github.fnwib.write.config;

import com.github.fnwib.exception.SettingException;
import com.github.fnwib.write.CellText;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.io.File;
import java.util.List;

@Builder
public class TemplateSetting {

    private File           template;
    @Getter
    private String         sheetName;
    @Getter
    private boolean        useDefaultCellStyle;
    @Getter
    @Singular
    private List<String>   addLastTitles;
    @Getter
    @Singular
    private List<CellText> cellTexts;

    public File getTemplate() {
        if (template == null) {
            throw new SettingException("模版没有配置");
        }
        if (!template.exists()) {
            throw new SettingException("模版" + template.getAbsolutePath() + "不存在");
        }
        return template;
    }

    public boolean changed() {
        return !addLastTitles.isEmpty() || !cellTexts.isEmpty();
    }

}
