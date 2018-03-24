package com.github.fnwib.write.config;

import com.github.fnwib.databing.LineReader;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.write.template.EmptyTemplate;
import com.github.fnwib.write.template.ExistTemplate;
import com.github.fnwib.write.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class WorkbookConfig<T> {

    private static final Logger log = LoggerFactory.getLogger(WorkbookConfig.class);

    private final LineReader<T>     lineReader;
    private final ResultFileSetting resultFileSetting;
    private final TemplateSetting   templateSetting;

    public WorkbookConfig(LineReader<T> lineReader,
                          ResultFileSetting resultFileSetting,
                          TemplateSetting templateSetting) {
        this.lineReader = lineReader;
        this.resultFileSetting = resultFileSetting;
        this.templateSetting = templateSetting;
    }

    public Template<T> getTemplate() {
        File template = templateSetting.getTemplate();
        if (template == null) {
            if (templateSetting.changed()) {
                return new EmptyTemplate<>(lineReader, templateSetting, resultFileSetting);
            } else {
                throw new SettingException("模版没有配置");
            }
        }
        if (!template.exists()) {
            throw new SettingException("模版" + template.getAbsolutePath() + "不存在");
        }
        return new ExistTemplate<>(lineReader, templateSetting, resultFileSetting);
    }




}
