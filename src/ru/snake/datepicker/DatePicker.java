package ru.snake.datepicker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

import ru.snake.datepicker.format.DatePickerFormat;
import ru.snake.datepicker.format.NullableDateFormatter;
import ru.snake.datepicker.model.DatePickerModel;
import ru.snake.datepicker.model.DatePickerModelListener;
import ru.snake.datepicker.model.DefaultDatePickerModel;

@SuppressWarnings("serial")
public class DatePicker extends JPanel implements DatePickerModelListener,
		PropertyChangeListener {

	private static final String BUTTON_TEXT = "<HTML>&hellip;</HTML>";

	private final JFormattedTextField dateText;
	private final JButton popupButton;
	private final DatePopup popup;
	private final DatePickerModel model;

	private DateFormat dateFormat;
	private boolean markEmpty;

	public DatePicker() {
		this(new DefaultDatePickerModel(), DatePickerFormat.DATETIME, false);
	}

	public DatePicker(Date value) {
		this(new DefaultDatePickerModel(), DatePickerFormat.DATETIME, false);

		model.setDate(value);
	}

	public DatePicker(DatePickerModel model) {
		this(model, DatePickerFormat.DATETIME, false);
	}

	public DatePicker(DatePickerModel model, boolean markEmpty) {
		this(model, DatePickerFormat.DATETIME, markEmpty);
	}

	public DatePicker(DatePickerModel model, DatePickerFormat pickerFormat) {
		this(model, pickerFormat, false);
	}

	public DatePicker(DatePickerModel model, DatePickerFormat pickerFormat,
			boolean markEmpty) {
		this.model = model;
		this.markEmpty = markEmpty;

		Color outerBorder = UIManager.getColor("ComboBox.buttonDarkShadow");
		Color innerBorder = UIManager.getColor("ComboBox.buttonShadow");
		Font editorFont = UIManager.getFont("ComboBox.font");

		setBorder(BorderFactory.createLineBorder(outerBorder));

		popup = new DatePopup(model);

		dateText = new JFormattedTextField();
		dateText.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(innerBorder, 1),
				BorderFactory.createEmptyBorder(2, 3, 2, 5)));
		dateText.setFont(editorFont);
		dateText.addPropertyChangeListener("value", this);

		setDatePickerFormat(pickerFormat);

		popupButton = new JButton(BUTTON_TEXT);
		popupButton.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 1, 0, 0, outerBorder),
				BorderFactory.createEmptyBorder(3, 3, 3, 3)));
		popupButton.addActionListener(new DropButtonListener(popup));

		setLayout(new BorderLayout());
		add(dateText, BorderLayout.CENTER);
		add(popupButton, BorderLayout.LINE_END);

		model.addDateChangeListener(this);
	}

	public void setIcon(Icon icon) {
		if (icon == null) {
			popupButton.setText(BUTTON_TEXT);
			popupButton.setIcon(null);
		} else {
			popupButton.setText("");
			popupButton.setIcon(icon);
		}
	}

	public Icon getIcon() {
		return popupButton.getIcon();
	}

	private void updateDatePickerFormat() {
		DateFormatter formatter;

		if (markEmpty) {
			formatter = new DateFormatter(dateFormat);
		} else {
			formatter = new NullableDateFormatter(dateFormat);
		}

		DefaultFormatterFactory factory = new DefaultFormatterFactory();
		factory.setDefaultFormatter(formatter);

		dateText.setFormatterFactory(factory);
	}

	public void setDatePickerFormat(DatePickerFormat pickerFormat) {
		switch (pickerFormat) {
		case DATE:
			dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT);
			break;

		case TIME:
			dateFormat = DateFormat.getTimeInstance(DateFormat.DEFAULT);
			break;

		case DATETIME:
			dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT,
					DateFormat.SHORT);
			break;

		default:
			throw new IllegalArgumentException();
		}

		updateDatePickerFormat();
	}

	public void setFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;

		updateDatePickerFormat();
	}

	public boolean isMarkEmpty() {
		return markEmpty;
	}

	public void setMarkEmpty(boolean markEmpty) {
		this.markEmpty = markEmpty;

		updateDatePickerFormat();
	}

	@Override
	public int getBaseline(int width, int height) {
		return dateText.getBaseline(width, height);
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
		dateText.setValue(model.getDate());

		if (model.isValid() || !markEmpty) {
			dateText.setBackground(UIManager.getColor("TextField.background"));
		} else {
			dateText.setBackground(UIManager
					.getColor("OptionPane.warningDialog.titlePane.background"));
		}
	}

	private class DropButtonListener implements ActionListener {

		private final DatePager pager;

		DropButtonListener(DatePager pager) {
			this.pager = pager;
		}

		public void actionPerformed(ActionEvent arg0) {
			Object source = arg0.getSource();

			if (source instanceof Component) {
				Component component = (Component) source;

				pager.show(component, 0, component.getHeight());
			}
		}

	}

	public void setPopupVisible(boolean value) {
		if (!popup.isVisible() && value) {
			popup.show(popupButton, 0, popupButton.getHeight());
		}

		if (popup.isVisible() && !value) {
			popup.setVisible(false);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getSource() == dateText) {
			Object newValue = event.getNewValue();
			Object oldValue = event.getOldValue();

			if (newValue == oldValue) {
				return;
			}

			if (newValue != null && newValue.equals(oldValue)) {
				return;
			}

			if (newValue == null) {
				model.setDate(null);
			} else {
				Date date = (Date) newValue;

				model.setDate(date);
			}
		}
	}

}
