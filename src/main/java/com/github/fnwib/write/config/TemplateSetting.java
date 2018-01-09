package com.github.fnwib.write.config;

import com.github.fnwib.exception.SettingException;
import lombok.Builder;
import lombok.Getter;
import org.apache.poi.ss.usermodel.CellStyle;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Builder
public class TemplateSetting {

    private final AtomicInteger fileSeq = new AtomicInteger(1);

    private final SheetNameProducer sheetNameProducer = (sheetName) -> sheetName + fileSeq.getAndAdd(1);

    private File         template;
    private String       sheetName;
    @Getter
    private CellStyle    cellStyle;
    @Getter
    private List<String> addLastTitles;

    public File getTemplate() {
        if (template == null) {
            throw new SettingException("模版没有配置");
        }
        if (!template.exists()) {
            throw new SettingException("模版" + template.getAbsolutePath() + "不存在");
        }
        return template;
    }

    public String getSheetName(ExportType exportType) {
        if (exportType == ExportType.SingleSheet) {
            return sheetName;
        } else {
            return sheetNameProducer.getSheetName(sheetName);
        }
    }

}
