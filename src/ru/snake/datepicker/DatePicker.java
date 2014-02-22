package ru.snake.datepicker;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import ru.snake.datepicker.model.DatePickerModel;
import ru.snake.datepicker.model.DatePickerModelListener;
import ru.snake.datepicker.model.DefaultDatePickerModel;

@SuppressWarnings("serial")
public class DatePicker extends JPanel implements DatePickerModelListener,
		FocusListener {

	private final JTextField dateText;
	private final JButton popupButton;
	private final DatePopup popup;
	private final DatePickerModel model;

	public DatePicker(Date value) {
		this(new DefaultDatePickerModel());
	}

	public DatePicker(DatePickerModel model) {
		this.model = model;

		dateText = new JTextField(model.getText());
		dateText.addFocusListener(this);

		popupButton = new JButton("<HTML>&hellip;</HTML>");
		popup = new DatePopup(model);

		popupButton.setMargin(new Insets(0, 4, 0, 4));
		popupButton.addActionListener(new DropButtonListener(popup));

		setLayout(new BorderLayout());
		add(dateText, BorderLayout.CENTER);
		add(popupButton, BorderLayout.LINE_END);

		model.addDateChangeListener(this);
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

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent event) {
		model.setText(dateText.getText());
	}

}
