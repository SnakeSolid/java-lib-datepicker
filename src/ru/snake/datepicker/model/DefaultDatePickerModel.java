package ru.snake.datepicker.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class DefaultDatePickerModel extends AbstractDatePickerModel implements
		DatePickerModel {

	private final DateFormat format;

	public DefaultDatePickerModel() {
		this(DateFormat.DEFAULT, DateFormat.SHORT);
	}

	public DefaultDatePickerModel(int dateStyle, int timeStyle) {
		Locale locale = Locale.getDefault();

		format = DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
	}

	@Override
	protected String doTextFromDate(Date value) {
		if (value == null) {
			return "";
		}

		return format.format(value);
	}

	@Override
	protected Date doTextToDate(String value) {
		Date result;

		try {
			result = format.parse(value);
		} catch (ParseException e) {
			result = null;
		}

		return result;
	}

}
