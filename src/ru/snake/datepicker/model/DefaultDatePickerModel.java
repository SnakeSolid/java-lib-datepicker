package ru.snake.datepicker.model;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

public class DefaultDatePickerModel implements DatePickerModel {

	private Collection<DatePickerModelListener> listeners;

	protected Date date;

	public DefaultDatePickerModel() {
		listeners = new LinkedList<DatePickerModelListener>();

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

	public boolean isValid() {
		return date != null;
	}

	public void setDate(Date value) {
		date = value;

		fireModelChanged();
	}

	public Date getDate() {
		return date;
	}

}
