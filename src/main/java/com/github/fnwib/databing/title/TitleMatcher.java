package com.github.fnwib.databing.title;

import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.util.ValueUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public final class TitleMatcher {

    private final String  prefix;
    private final Pattern titlePattern;
    private final String  suffix;
    private final String  exclude;

    public TitleMatcher(AutoMapping mapping) {
        this.prefix = mapping.prefix();
        this.titlePattern = Pattern.compile(mapping.title().trim());
        this.suffix = mapping.suffix();
        this.exclude = mapping.exclude();
    }

    public List<CellTitle> match(List<CellTitle> titles) {
        List<CellTitle> result = Lists.newArrayList();
        log.info("annotation -> title is [{}] , prefix is [{}] , suffix is [{}] ,exclude is [{}]",
                titlePattern.toString(),
                prefix,
                suffix,
                exclude);
        for (CellTitle title : titles) {
            Optional<String> root = ValueUtil.substringBetween(title.getText(), prefix, suffix);
            if (!root.isPresent()) {
                continue;
            }
            Matcher titleMatcher = titlePattern.matcher(root.get().trim());
            if (titleMatcher.matches()) {
                if (StringUtils.isNotBlank(exclude) && Pattern.matches(exclude,root.get().trim())) {
                    continue;
                }
                if (title.isBind()) {
                    throw new SettingException("配置错误 ->" + title + "已经被使用");
                }
                log.debug("-->matched -> rownum is [{}],text is [{}] ,middle [{}] ", title.getRowNum(), title.getText(), root.get());
                title.bind();
                result.add(title);
            }
        }
        return result;
    }


}
