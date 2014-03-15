package ru.snake.datepicker.format;

import java.text.DateFormat;
import java.text.ParseException;

import javax.swing.text.DateFormatter;

@SuppressWarnings("serial")
public class NullableDateFormatter extends DateFormatter {

	public NullableDateFormatter() {
		super();
	}

	public NullableDateFormatter(DateFormat format) {
		super(format);
	}

	@Override
	public Object stringToValue(String text) throws ParseException {
		if (text == null) {
			return null;
		}

		if (text.isEmpty()) {
			return null;
		}

		return super.stringToValue(text);
	}

	@Override
	public String valueToString(Object value) throws ParseException {
		if (value == null) {
			return "";
		}

		return super.valueToString(value);
	}

}
