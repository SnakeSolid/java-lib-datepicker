package ru.snake.datepicker;

import java.awt.Component;
import java.util.Date;

public interface DatePager {

	public Date getSelected();

	public void setSelected(Date date);

	public void addYear(int count);

	public int getYear();

	public void addMonth(int count);

	public int getMonth();

	public void show(Component invoker, int x, int y);

	public void hide();

}
