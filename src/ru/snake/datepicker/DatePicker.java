package ru.snake.datepicker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import ru.snake.datepicker.model.DatePickerModel;
import ru.snake.datepicker.model.DatePickerModelListener;
import ru.snake.datepicker.model.DefaultDatePickerModel;

@SuppressWarnings("serial")
public class DatePicker extends JPanel implements DatePickerModelListener,
		FocusListener {

	private static final int DAYS_IN_WEEK = 7;
	private static final int MAX_WEEKS_IN_MONTH = 6;

	private final JTextField text;
	private final JButton button;
	private final JPopupMenu popup;
	private final JPanel calendar;
	private JLabel monthText;

	private List<JLabel> dayLabels;

	private int year;
	private int month;

	private final DatePickerModel model;

	public DatePicker(Date value) {
		this(new DefaultDatePickerModel());
	}

	public DatePicker(DatePickerModel model) {
		this.model = model;

		text = new JTextField();

		text.addFocusListener(this);

		button = new JButton("<HTML>&hellip;</HTML>");
		popup = new JPopupMenu();
		calendar = createCalendar();

		popup.add(calendar);

		button.setMargin(new Insets(0, 4, 0, 4));
		button.addActionListener(new DropButtonListener(this, model));

		setLayout(new BorderLayout());
		add(text, BorderLayout.CENTER);
		add(button, BorderLayout.LINE_END);

		model.addDateChangeListener(this);
	}

	private JPanel createCalendar() {
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

		prevMonth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(year, month, 1);
				calendar.add(Calendar.MONTH, -1);

				year = calendar.get(Calendar.YEAR);
				month = calendar.get(Calendar.MONTH);

				updateGrid();
			}
		});

		nextMonth.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(year, month, 1);
				calendar.add(Calendar.MONTH, 1);

				year = calendar.get(Calendar.YEAR);
				month = calendar.get(Calendar.MONTH);

				updateGrid();
			}
		});

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

		dayLabels = new ArrayList<JLabel>(DAYS_IN_WEEK * MAX_WEEKS_IN_MONTH);

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

		return container;
	}

	private void updateGrid() {
		Calendar calendar = getStartOfGrid();

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

				if (model.isValid() && datesEquals(model.getDate(), calendar)) {
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

		SimpleDateFormat headerFormat = new SimpleDateFormat("MMMM, yyyy");
		calendar.set(year, month, 1);
		monthText.setText(headerFormat.format(calendar.getTime()));
	}

	private Calendar getStartOfGrid() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, 1);

		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int firstDay = calendar.getFirstDayOfWeek();

		calendar.add(Calendar.DATE, firstDay - dayOfWeek);
		return calendar;
	}

	private boolean datesEquals(Date value, Calendar calendar) {
		Calendar other = Calendar.getInstance();
		other.setTime(value);

		return other.get(Calendar.ERA) == calendar.get(Calendar.ERA)
				&& other.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)
				&& other.get(Calendar.DAY_OF_YEAR) == calendar
						.get(Calendar.DAY_OF_YEAR);
	}

	public Date getValue() {
		return model.getDate();
	}

	public void setValue(Date value) {
		model.setDate(value);
	}

	public void modelChenged(DatePickerModel model) {
		if (this.model == model) {
			updateText();
		}
	}

	private void updateText() {
		text.setText(model.getText());

		if (model.isValid()) {
			text.setBackground(UIManager.getColor("TextField.background"));
		} else {
			text.setBackground(UIManager
					.getColor("OptionPane.warningDialog.titlePane.background"));
		}
	}

	private class DropButtonListener implements ActionListener {

		private final DatePicker picker;
		private final DatePickerModel model;

		DropButtonListener(DatePicker picker, DatePickerModel model) {
			this.picker = picker;
			this.model = model;
		}

		public void actionPerformed(ActionEvent arg0) {
			Calendar calendar = Calendar.getInstance();

			if (model.isValid()) {
				calendar.setTime(model.getDate());
			} else {
				calendar.setTime(new Date());
			}

			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);

			picker.setYearAndMonth(year, month);
			picker.setPopupVisible(true);
		}

	}

	public void setYearAndMonth(int year, int month) {
		this.year = year;
		this.month = month;

		updateGrid();
	}

	public void setPopupVisible(boolean value) {
		if (!popup.isVisible() && value) {
			popup.show(button, 0, button.getHeight());
		}

		if (popup.isVisible() && !value) {
			popup.setVisible(false);
		}
	}

	private class GridMouseListener extends MouseAdapter implements
			MouseListener {

		private final DatePicker picker;
		private final int index;

		GridMouseListener(DatePicker picker, int index) {
			this.picker = picker;
			this.index = index;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			updateSelected(e.getSource());
			updateGrid();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			updateSelected(e.getSource());
			updateGrid();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			updateSelected(e.getSource());
			updateGrid();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			popup.setVisible(false);
		}

		private void updateSelected(Object source) {
			int gridIndex = index;

			Calendar calendar = getStartOfGrid();
			calendar.add(Calendar.DATE, gridIndex);

			picker.setValue(calendar.getTime());
		}

	}

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent event) {
		model.setText(text.getText());
	}

}
