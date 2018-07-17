package com.github.fnwib.write.template;

import com.github.fnwib.exception.ExcelException;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Optional;
@Deprecated
public class FontBuilder {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Workbook workbook;
        private String   fontName;
        private short    height;

        public Builder workbook(Workbook workbook) {
            this.workbook = workbook;
            return this;
        }

        public Builder fontName(String fontName) {
            this.fontName = fontName;
            return this;
        }

        public Builder height(short height) {
            this.height = height;
            return this;
        }

        public Optional<Font> build() {
            if (workbook == null) {
                throw new ExcelException("workbook must not null");
            }
            Font font = workbook.createFont();
            font.setFontName(fontName);
            font.setFontHeightInPoints(height);
            return Optional.of(font);
        }
    }

}
