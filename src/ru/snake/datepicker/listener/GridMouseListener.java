package ru.snake.datepicker.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Calendar;

import ru.snake.datepicker.DatePager;

public class GridMouseListener extends MouseAdapter implements MouseListener {

	private final DatePager pager;
	private final int index;

	public GridMouseListener(DatePager pager, int index) {
		this.pager = pager;
		this.index = index;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		updateSelected();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		pager.hide();
	}

	private void updateSelected() {
		Calendar calendar = getStartOfGrid();

		calendar.add(Calendar.DATE, index);

		pager.setSelected(calendar.getTime());
	}

	private Calendar getStartOfGrid() {
		int year = pager.getYear();
		int month = pager.getMonth();

		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, 1);

		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int firstDay = calendar.getFirstDayOfWeek();

		if (dayOfWeek < firstDay) {
			int daysInWeek = calendar.getMaximum(Calendar.DAY_OF_WEEK);

			calendar.add(Calendar.DATE, firstDay - dayOfWeek - daysInWeek);
		} else {
			calendar.add(Calendar.DATE, firstDay - dayOfWeek);
		}

		return calendar;
	}

}
