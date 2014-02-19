package ru.snake.datepicker.model;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

public abstract class AbstractDatePickerModel implements DatePickerModel {

	private Collection<DatePickerModelListener> listeners;

	protected String text;
	protected Date date;

	public AbstractDatePickerModel() {
		listeners = new LinkedList<DatePickerModelListener>();

		text = "";
		date = new Date();
	}

	public final void addDateChangeListener(DatePickerModelListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);

			listener.modelChenged(this);
		}
	}

	public final void removeDateChangeListener(DatePickerModelListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	private final void fireModelChanged() {
		for (DatePickerModelListener listener : listeners) {
			listener.modelChenged(this);
		}
	}

	public final void setText(String value) {
		text = value;
		date = doTextToDate(value);

		fireModelChanged();
	}

	public final String getText() {
		return text;
	}

	public final boolean isValid() {
		return date != null;
	}

	public final void setDate(Date value) {
		text = doTextFromDate(value);
		date = value;

		fireModelChanged();
	}

	public final Date getDate() {
		return date;
	}

	protected abstract String doTextFromDate(Date value);

	protected abstract Date doTextToDate(String value);

}
