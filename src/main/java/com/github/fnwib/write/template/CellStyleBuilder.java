package com.github.fnwib.write.template;

import com.github.fnwib.exception.ExcelException;
import org.apache.poi.ss.usermodel.*;

import java.util.Optional;
@Deprecated
public class CellStyleBuilder {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Workbook        workbook;
        private IndexedColors   fillBackgroundColor;
        private IndexedColors   fillForegroundColor;
        private FillPatternType fillPattern;

        public Builder workbook(Workbook workbook) {
            this.workbook = workbook;
            return this;
        }

        public Builder fillBackgroundColor(IndexedColors fillBackgroundColor) {
            this.fillBackgroundColor = fillBackgroundColor;
            return this;
        }

        public Builder fillForegroundColor(IndexedColors fillForegroundColor) {
            this.fillForegroundColor = fillForegroundColor;
            return this;
        }

        public Builder fillPattern(FillPatternType fillPattern) {
            this.fillPattern = fillPattern;
            return this;
        }

        public Optional<CellStyle> build() {
            if (workbook == null) {
                throw new ExcelException("workbook must not null");
            }
            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setAlignment(HorizontalAlignment.CENTER); // 居中
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("text"));
            if (fillBackgroundColor != null) {
                cellStyle.setFillBackgroundColor(fillBackgroundColor.index);
            }
            if (fillForegroundColor != null) {
                cellStyle.setFillForegroundColor(fillForegroundColor.index);
            }
            if (fillPattern != null) {
                cellStyle.setFillPattern(fillPattern);
            }
            return Optional.of(cellStyle);
        }
    }

}
