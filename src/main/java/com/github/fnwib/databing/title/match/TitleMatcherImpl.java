package com.github.fnwib.databing.title.match;

import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.CellType;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.databing.title.CellTitle;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.util.ValueUtil;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TitleMatcherImpl implements TitleMatcher {

    private static final Logger log = LoggerFactory.getLogger(TitleMatcher.class);
    private final Pattern titlePattern;

    private final Operation operation;
    private final String    prefix;
    private final String    sequence;
    private final String    suffix;
    private final String    exclude;

    public TitleMatcherImpl(AutoMapping mapping) {
        this.titlePattern = Pattern.compile(mapping.value().trim(), Pattern.CASE_INSENSITIVE);
        this.operation = mapping.operation();
        this.prefix = mapping.prefix();
        this.sequence = mapping.value().trim();
        this.suffix = mapping.suffix();
        this.exclude = mapping.exclude();
    }

    public TitleMatcherImpl(CellType mapping) {
        this.titlePattern = Pattern.compile(mapping.title().trim(), Pattern.CASE_INSENSITIVE);
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

    @Override
    public List<CellTitle> match(List<CellTitle> titles) {
        if (StringUtils.isBlank(prefix) && StringUtils.isBlank(sequence) && StringUtils.isBlank(suffix)) {
            return Collections.emptyList();
        }
        List<CellTitle> result = Lists.newArrayList();
        log.debug("annotation ->  prefix is [{}] ,value is [{}] , suffix is [{}] ,exclude is [{}]",
                prefix,
                sequence,
                suffix,
                exclude);
        for (CellTitle title : titles) {
            Optional<String> root = ValueUtil.substringBetweenIgnoreCase(title.getText(), prefix, suffix);
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
    public Operation getOperation() {
        return operation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TitleMatcherImpl that = (TitleMatcherImpl) o;
        return Objects.equal(titlePattern, that.titlePattern) &&
                operation == that.operation &&
                Objects.equal(prefix, that.prefix) &&
                Objects.equal(sequence, that.sequence) &&
                Objects.equal(suffix, that.suffix) &&
                Objects.equal(exclude, that.exclude);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(titlePattern, operation, prefix, sequence, suffix, exclude);
    }


    @Override
    public String toString() {
        return "TitleMatcherImpl{" +
                "operation=" + operation +
                ", prefix='" + prefix + '\'' +
                ", sequence='" + sequence + '\'' +
                ", suffix='" + suffix + '\'' +
                ", exclude='" + exclude + '\'' +
                '}';
    }
}
