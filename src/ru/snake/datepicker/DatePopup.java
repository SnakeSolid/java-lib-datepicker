package ru.snake.datepicker;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import ru.snake.datepicker.action.ChangeMonthAction;
import ru.snake.datepicker.listener.GridMouseListener;
import ru.snake.datepicker.model.DatePickerModel;

@SuppressWarnings("serial")
public class DatePopup extends JPopupMenu implements DatePager {

	private static final int DAYS_IN_WEEK = 7;
	private static final int MAX_WEEKS_IN_MONTH = 6;

	private final DatePickerModel model;
	private final List<JLabel> dayLabels;

	private JLabel monthText;

	private Date selected;
	private int year;
	private int month;

	public DatePopup(DatePickerModel model) {
		this.model = model;

		dayLabels = new ArrayList<JLabel>(DAYS_IN_WEEK * MAX_WEEKS_IN_MONTH);

		createCalendar();
	}

	private void createCalendar() {
		LayoutManager containerLayout = new BorderLayout();
		LayoutManager headerLayout = new BorderLayout();
		LayoutManager bodyLayout = new GridLayout(MAX_WEEKS_IN_MONTH + 1,
				DAYS_IN_WEEK, 1, 1);

		JPanel container = new JPanel(containerLayout);
		JPanel header = new JPanel(headerLayout);
		JPanel body = new JPanel(bodyLayout);

		JButton prevMonth = new JButton("<HTML>&lt;</HTML>");
		monthText = new JLabel("---------------");
		JButton nextMonth = new JButton("<HTML>&gt;</HTML>");

		prevMonth.addActionListener(new ChangeMonthAction(this, -1));
		nextMonth.addActionListener(new ChangeMonthAction(this, 1));

		prevMonth.setMargin(new Insets(0, 4, 0, 4));
		monthText.setHorizontalAlignment(SwingConstants.CENTER);
		nextMonth.setMargin(new Insets(0, 4, 0, 4));

		header.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

		body.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		body.setBackground(UIManager.getColor("Table.gridColor"));
		body.setPreferredSize(new Dimension(187, 132));

		header.add(prevMonth, BorderLayout.LINE_START);
		header.add(monthText, BorderLayout.CENTER);
		header.add(nextMonth, BorderLayout.LINE_END);

		Calendar calendar = Calendar.getInstance();
		DateFormatSymbols symbols = new DateFormatSymbols();
		String weekDays[] = symbols.getShortWeekdays();

		int minDay = calendar.getMinimum(Calendar.DAY_OF_WEEK);
		int maxDay = calendar.getMaximum(Calendar.DAY_OF_WEEK);
		int currentDay = calendar.getFirstDayOfWeek();

		for (int i = minDay; i <= maxDay; i++) {
			JLabel headerLabel = new JLabel();

			headerLabel.setText(weekDays[currentDay]);
			headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
			headerLabel
					.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			headerLabel.setBackground(UIManager
					.getColor("TableHeader.background"));
			headerLabel.setForeground(UIManager
					.getColor("TableHeader.foreground"));
			headerLabel.setOpaque(true);

			body.add(headerLabel);

			currentDay++;

			if (currentDay > maxDay) {
				currentDay = minDay;
			}
		}

		dayLabels.clear();

		for (int i = 0; i < MAX_WEEKS_IN_MONTH * DAYS_IN_WEEK; i++) {
			JLabel dayLabel = new JLabel();

			dayLabel.addMouseListener(new GridMouseListener(this, i));
			dayLabel.setHorizontalAlignment(SwingConstants.CENTER);
			dayLabel.setBackground(UIManager.getColor("Table.background"));
			dayLabel.setForeground(UIManager.getColor("Table.foreground"));
			dayLabel.setOpaque(true);

			dayLabels.add(dayLabel);

			body.add(dayLabel);
		}

		container.add(header, BorderLayout.PAGE_START);
		container.add(body, BorderLayout.CENTER);

		add(container);
	}

	private void updateGrid() {
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

		for (int i = 0; i < dayLabels.size(); i++) {
			JLabel dayLabel = dayLabels.get(i);

			if (calendar.get(Calendar.MONTH) == month
					&& calendar.get(Calendar.YEAR) == year) {
				if (datesEquals(new Date(), calendar)) {
					dayLabel.setBorder(UIManager
							.getBorder("Table.focusCellHighlightBorder"));
					dayLabel.setOpaque(true);
				} else {
					dayLabel.setBorder(null);
					dayLabel.setOpaque(true);
				}

				if (model.isValid() && datesEquals(selected, calendar)) {
					dayLabel.setBackground(UIManager
							.getColor("Table.selectionBackground"));
					dayLabel.setForeground(UIManager
							.getColor("Table.selectionForeground"));
					dayLabel.setOpaque(true);
				} else {
					dayLabel.setBackground(UIManager
							.getColor("Table.background"));
					dayLabel.setForeground(UIManager
							.getColor("Table.foreground"));
					dayLabel.setOpaque(true);
				}
			} else {
				dayLabel.setBackground(UIManager
						.getColor("TextField.inactiveBackground"));
				dayLabel.setForeground(UIManager
						.getColor("TextField.inactiveForeground"));
				dayLabel.setOpaque(true);
			}

			dayLabel.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));

			calendar.add(Calendar.DATE, 1);
		}

		calendar.set(year, month, 1);

		SimpleDateFormat headerFormat = new SimpleDateFormat("MMMM, yyyy");

		monthText.setText(headerFormat.format(calendar.getTime()));

		repaint();
	}

	private boolean datesEquals(Date value, Calendar calendar) {
		Calendar other = Calendar.getInstance();
		other.setTime(value);

		return other.get(Calendar.ERA) == calendar.get(Calendar.ERA)
				&& other.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
				&& other.get(Calendar.DAY_OF_YEAR) == calendar
						.get(Calendar.DAY_OF_YEAR);
	}

	@Override
	public Date getSelected() {
		return selected;
	}

	@Override
	public void setSelected(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		if (model.isValid()) {
			Calendar modelCal = Calendar.getInstance();
			Date modelDate = model.getDate();

			modelCal.setTime(modelDate);

			calendar.set(Calendar.HOUR, modelCal.get(Calendar.HOUR));
			calendar.set(Calendar.MINUTE, modelCal.get(Calendar.MINUTE));
			calendar.set(Calendar.SECOND, modelCal.get(Calendar.SECOND));
			calendar.set(Calendar.MILLISECOND,
					modelCal.get(Calendar.MILLISECOND));
		}

		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		selected = calendar.getTime();

		updateGrid();
	}

	@Override
	public int getYear() {
		return year;
	}

	@Override
	public void addYear(int count) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, calendar.getMinimum(Calendar.DAY_OF_MONTH));
		calendar.add(Calendar.YEAR, count);

		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);

		updateGrid();
	}

	@Override
	public int getMonth() {
		return month;
	}

	@Override
	public void addMonth(int count) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, calendar.getMinimum(Calendar.DAY_OF_MONTH));
		calendar.add(Calendar.MONTH, count);

		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);

		updateGrid();
	}

	@Override
	public void show(Component invoker, int x, int y) {
		if (!model.isValid()) {
			return;
		}

		selected = model.getDate();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(selected);

		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);

		updateGrid();

		super.show(invoker, x, y);
	}

	@Override
	public void hide() {
		model.setDate(selected);

		this.setVisible(false);
	}

}
