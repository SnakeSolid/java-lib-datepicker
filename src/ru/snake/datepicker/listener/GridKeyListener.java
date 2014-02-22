package ru.snake.datepicker.listener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import ru.snake.datepicker.DatePager;

public class GridKeyListener extends KeyAdapter implements KeyListener {

	private final DatePager pager;

	GridKeyListener(DatePager pager) {
		this.pager = pager;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		int keyCode = e.getKeyCode();

		switch (keyCode) {
		case KeyEvent.VK_PAGE_UP:
			addMonth(-1);
			break;

		case KeyEvent.VK_PAGE_DOWN:
			addMonth(1);
			break;

		case KeyEvent.VK_UP:
			addDays(-1);
			break;

		case KeyEvent.VK_DOWN:
			addDays(1);
			break;

		case KeyEvent.VK_LEFT:
			addWeeks(-1);
			break;

		case KeyEvent.VK_RIGHT:
			addWeeks(1);
			break;

		case KeyEvent.VK_ENTER:
			pager.hide();
			break;
		}
	}

	private void addMonth(int offsetMonths) {
		pager.addMonth(offsetMonths);
	}

	private void addWeeks(int offsetWeeks) {
		// TODO: pager.addMonth(1);
	}

	private void addDays(int offsetDays) {
		// TODO: offsetMonths
	}

}
