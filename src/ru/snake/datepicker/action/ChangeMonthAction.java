package ru.snake.datepicker.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import ru.snake.datepicker.DatePager;

@SuppressWarnings("serial")
public class ChangeMonthAction extends AbstractAction implements ActionListener {

	private final DatePager pager;
	private final int offset;

	public ChangeMonthAction(DatePager pager, int offset) {
		this.pager = pager;
		this.offset = offset;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		pager.addMonth(offset);
	}

}
