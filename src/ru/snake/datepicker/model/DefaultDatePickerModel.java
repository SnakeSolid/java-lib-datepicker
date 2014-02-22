package ru.snake.datepicker.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class DefaultDatePickerModel extends AbstractDatePickerModel implements
		DatePickerModel {

	private final DateFormat format;

	public DefaultDatePickerModel() {
		this(ModelFormat.DATETIME);
	}

	public DefaultDatePickerModel(ModelFormat modelFormat) {
		switch (modelFormat) {
		case DATE:
			format = DateFormat.getDateInstance(DateFormat.DEFAULT);
			break;

		case TIME:
			format = DateFormat.getTimeInstance(DateFormat.DEFAULT);
			break;

		case DATETIME:
			format = DateFormat.getDateTimeInstance(DateFormat.DEFAULT,
					DateFormat.SHORT);
			break;

		default:
			throw new IllegalArgumentException();
		}
	}

	public DefaultDatePickerModel(DateFormat format) {
		this.format = format;
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
