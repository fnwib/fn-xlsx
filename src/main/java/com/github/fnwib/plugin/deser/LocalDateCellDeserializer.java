package com.github.fnwib.plugin.deser;

import com.github.fnwib.mapper.cell.ErrorCellType;
import com.github.fnwib.util.ValueUtil;
import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.Cell;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class LocalDateCellDeserializer implements CellDeserializer<LocalDate> {

	private DateTimeFormatter dateTimeFormatter;

	private Map<Pattern, DateTimeFormatter> formats;

	public LocalDateCellDeserializer() {
		this.formats = Maps.newHashMap();
		init();
	}

	private void init() {
		formats.put(Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$"), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		formats.put(Pattern.compile("^\\d{4}/\\d{1,2}/\\d{1,2}$"), DateTimeFormatter.ofPattern("yyyy/M/d"));
		formats.put(Pattern.compile("^\\d{4}\\.\\d{2}\\.\\d{2}$"), DateTimeFormatter.ofPattern("yyyy.M.d"));
		formats.put(Pattern.compile("^\\d{4}\\\\\\d{2}\\\\\\d{2}$"), DateTimeFormatter.ofPattern("yyyy\\MM\\dd"));
		formats.put(Pattern.compile("^\\d{4}\\d{2}\\d{2}$"), DateTimeFormatter.ofPattern("yyyyMMdd"));
	}

	@Override
	public LocalDate deserialize(Cell cell) {
		if (cell == null) {
			return null;
		}
		switch (cell.getCellType()) {
			case BLANK:
			case _NONE:
				return null;
			case NUMERIC:
				Date date = cell.getDateCellValue();
				Instant instant = date.toInstant();
				return instant.atZone(ZoneId.systemDefault()).toLocalDate();
			case STRING:
				Optional<String> optional = ValueUtil.getCellValue(cell);
				if (!optional.isPresent()) {
					return null;
				}
				String value = optional.get().trim();
				if (dateTimeFormatter == null) {
					dateTimeFormatter = getDateTimeFormatter(value, cell);
					try {
						return LocalDate.parse(value, dateTimeFormatter);
					} catch (DateTimeParseException e) {
						throw ErrorCellType.WRONG_DATE.getException(cell);
					}
				} else {
					try {
						return LocalDate.parse(value, dateTimeFormatter);
					} catch (DateTimeParseException e) {
						DateTimeFormatter formatter = getDateTimeFormatter(value, cell);
						if (this.dateTimeFormatter == formatter) {
							throw ErrorCellType.WRONG_DATE.getException(cell);
						}
						this.dateTimeFormatter = formatter;
						return LocalDate.parse(value, this.dateTimeFormatter);
					}
				}
			case BOOLEAN:
			case FORMULA:
			case ERROR:
			default:
				throw ErrorCellType.NOT_SUPPORT.getException(cell);
		}
	}

	public DateTimeFormatter getDateTimeFormatter(String value, Cell cell) {
		for (Map.Entry<Pattern, DateTimeFormatter> entry : formats.entrySet()) {
			Pattern pattern = entry.getKey();
			if (pattern.matcher(value).matches()) {
				return entry.getValue();
			}
		}
		throw ErrorCellType.STRING_TO_DATE.getException(cell);
	}

}