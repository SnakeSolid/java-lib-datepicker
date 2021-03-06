package ru.snake.datepicker.model;

import java.util.Date;

public interface DatePickerModel {

	public void addDateChangeListener(DatePickerModelListener listener);

	public void removeDateChangeListener(DatePickerModelListener listener);

	public boolean isValid();

	public void setDate(Date date);

	public Date getDate();

}
