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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

	private final JTextField dateText;
	private final JButton popupButton;
	private final JPopupMenu popup;
	private final JPanel panelCal;
	private final DatePickerModel model;

	private JLabel monthText;

	private List<JLabel> dayLabels;

	private int year;
	private int month;

	public DatePicker(Date value) {
		this(new DefaultDatePickerModel());
	}

	public DatePicker(DatePickerModel model) {
		this.model = model;

		dateText = new JTextField();
		dateText.addFocusListener(this);

		popupButton = new JButton("<HTML>&hellip;</HTML>");
		panelCal = createCalendar();

		popup = new JPopupMenu();
		popup.add(panelCal);

		popupButton.setMargin(new Insets(0, 4, 0, 4));
		popupButton.addActionListener(new DropButtonListener(this, model));

		setLayout(new BorderLayout());
		add(dateText, BorderLayout.CENTER);
		add(popupButton, BorderLayout.LINE_END);

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

			dayLabel.addMouseListener(new GridMouseListener(model, i));

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

		if (dayOfWeek < firstDay) {
			int daysInWeek = calendar.getMaximum(Calendar.DAY_OF_WEEK);

			calendar.add(Calendar.DATE, firstDay - dayOfWeek - daysInWeek);
		} else {
			calendar.add(Calendar.DATE, firstDay - dayOfWeek);
		}

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
		dateText.setText(model.getText());

		if (model.isValid()) {
			dateText.setBackground(UIManager.getColor("TextField.background"));
		} else {
			dateText.setBackground(UIManager
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
			popup.show(popupButton, 0, popupButton.getHeight());
		}

		if (popup.isVisible() && !value) {
			popup.setVisible(false);
		}
	}

	private class GridMouseListener extends MouseAdapter implements
			MouseListener {

		private final DatePickerModel model;
		private final int index;

		GridMouseListener(DatePickerModel model, int index) {
			this.model = model;
			this.index = index;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			updateSelected();
			updateGrid();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			popup.setVisible(false);
		}

		private void updateSelected() {
			Calendar calendar = getStartOfGrid();

			calendar.add(Calendar.DATE, index);

			model.setDate(calendar.getTime());
		}

	}

	private class GridKeyListener extends KeyAdapter implements KeyListener {

		private final DatePickerModel model;
		private final Calendar calendar;

		GridKeyListener(DatePickerModel model) {
			this.model = model;

			Locale locale = Locale.getDefault();
			calendar = Calendar.getInstance(locale);
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
				popup.setVisible(false);
				break;
			}
		}

		private void addMonth(int offsetMonths) {
			if (!model.isValid()) {
				return;
			}

			calendar.setTime(model.getDate());
			calendar.add(Calendar.MONTH, offsetMonths);

			model.setDate(calendar.getTime());
		}

		private void addWeeks(int offsetWeeks) {
			if (!model.isValid()) {
				return;
			}

			calendar.setTime(model.getDate());
			calendar.add(Calendar.WEEK_OF_MONTH, offsetWeeks);

			model.setDate(calendar.getTime());
		}

		private void addDays(int offsetDays) {
			if (!model.isValid()) {
				return;
			}

			calendar.setTime(model.getDate());
			calendar.add(Calendar.DAY_OF_WEEK, offsetDays);

			model.setDate(calendar.getTime());
		}

	}

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent event) {
		model.setText(dateText.getText());
	}

}
