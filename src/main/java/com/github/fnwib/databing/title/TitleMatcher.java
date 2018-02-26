package com.github.fnwib.databing.title;

import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.CellType;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.util.ValueUtil;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public final class TitleMatcher {
    private static final Logger log = LoggerFactory.getLogger(TitleMatcher.class);

    private final Operation operation;
    private final String    title;
    private final String    prefix;
    private final String    sequence;
    private final String    suffix;
    private final String    exclude;

    public TitleMatcher(AutoMapping mapping) {
        this.title = mapping.value();
        this.operation = mapping.operation();
        this.prefix = mapping.prefix();
        this.sequence = mapping.value();
        this.suffix = mapping.suffix();
        this.exclude = mapping.exclude();
    }

    public TitleMatcher(CellType mapping) {
        this.title = mapping.title();
        this.operation = mapping.operation();
        this.prefix = mapping.prefix();
        this.sequence = mapping.title().trim();
        this.suffix = mapping.suffix();
        this.exclude = mapping.exclude();
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSequence() {
        return sequence;
    }

    public String getSuffix() {
        return suffix;
    }

    public List<CellTitle> match(List<CellTitle> titles, Collection<ValueHandler> titleValueHandlers) {
        if (StringUtils.isBlank(prefix) && StringUtils.isBlank(sequence) && StringUtils.isBlank(suffix)) {
            return Collections.emptyList();
        }
        List<CellTitle> result = Lists.newArrayList();
        log.debug("annotation -> title is [{}] , prefix is [{}] , suffix is [{}] ,exclude is [{}]",
                title,
                prefix,
                suffix,
                exclude);
        for (CellTitle cellTitle : titles) {
            String text = ValueUtil.getStringValue(cellTitle.getText(), titleValueHandlers);
            String title = ValueUtil.getStringValue(this.title, titleValueHandlers);
            String prefix = ValueUtil.getStringValue(this.prefix, titleValueHandlers);
            String suffix = ValueUtil.getStringValue(this.suffix, titleValueHandlers);
            String exclude = ValueUtil.getStringValue(this.exclude, titleValueHandlers);
            Optional<String> root = ValueUtil.substringBetween(text, prefix, suffix);
            if (!root.isPresent()) {
                continue;
            }
            boolean matches = Pattern.matches(title, root.get().trim());
            if (matches) {
                if (StringUtils.isNotBlank(exclude) && Pattern.matches(exclude, root.get().trim())) {
                    continue;
                }
                if (cellTitle.isBind()) {
                    log.error("->配置 [{}] 与 [{}] 冲突", cellTitle, this);
                    throw new SettingException("配置错误");
                }
                log.debug("-->matched -> rownum is [{}],text is [{}] ,middle [{}] ", cellTitle.getRowNum(), cellTitle.getText(), root.get());
                cellTitle.bind();
                cellTitle.setPrefix(this.prefix);
                cellTitle.setSuffix(this.suffix);
                cellTitle.setValue(root.get().trim());
                result.add(cellTitle);
            }
        }
        return result;
    }

    public Operation getOperation() {
        return operation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TitleMatcher that = (TitleMatcher) o;
        return Objects.equal(prefix, that.prefix) &&
                Objects.equal(sequence, that.sequence) &&
                Objects.equal(suffix, that.suffix) &&
                Objects.equal(exclude, that.exclude);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(prefix, sequence, suffix, exclude);
    }

    @Override
    public String toString() {
        return "TitleMatcher{" +
                "prefix='" + prefix + '\'' +
                ", sequence=" + sequence +
                ", suffix='" + suffix + '\'' +
                ", exclude='" + exclude + '\'' +
                '}';
    }
}
