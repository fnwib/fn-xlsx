package com.github.fnwib.databing.title;

import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.CellType;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.util.ValueUtil;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TitleMatcher {
    private static final Logger log = LoggerFactory.getLogger(TitleMatcher.class);

    private final String  prefix;
    private final Pattern titlePattern;
    private final String  suffix;
    private final String  exclude;

    TitleMatcher(AutoMapping mapping) {
        this.prefix = mapping.prefix();
        this.titlePattern = Pattern.compile(mapping.value().trim());
        this.suffix = mapping.suffix();
        this.exclude = mapping.exclude();
    }

    TitleMatcher(CellType mapping) {
        this.prefix = mapping.prefix();
        this.titlePattern = Pattern.compile(mapping.title().trim());
        this.suffix = mapping.suffix();
        this.exclude = mapping.exclude();
    }

    public List<CellTitle> match(List<CellTitle> titles) {
        List<CellTitle> result = Lists.newArrayList();
        log.debug("annotation -> title is [{}] , prefix is [{}] , suffix is [{}] ,exclude is [{}]",
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
                if (StringUtils.isNotBlank(exclude) && Pattern.matches(exclude, root.get().trim())) {
                    continue;
                }
                if (title.isBind()) {
                    log.error("->配置 [{}] 与 [{}] 冲突", title, this);
                    throw new SettingException("配置错误");
                }
                log.debug("-->matched -> rownum is [{}],text is [{}] ,middle [{}] ", title.getRowNum(), title.getText(), root.get());
                title.bind();
                title.setPrefix(prefix);
                title.setSuffix(suffix);
                title.setValue(root.get().trim());
                result.add(title);
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TitleMatcher that = (TitleMatcher) o;
        return Objects.equal(prefix, that.prefix) &&
                Objects.equal(titlePattern, that.titlePattern) &&
                Objects.equal(suffix, that.suffix) &&
                Objects.equal(exclude, that.exclude);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(prefix, titlePattern, suffix, exclude);
    }

    @Override
    public String toString() {
        return "TitleMatcher{" +
                "prefix='" + prefix + '\'' +
                ", titlePattern=" + titlePattern +
                ", suffix='" + suffix + '\'' +
                ", exclude='" + exclude + '\'' +
                '}';
    }
}
