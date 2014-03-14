package ru.snake.datepicker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Date;

import javax.swing.BorderFactory;
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

	private boolean markEmpty;

	public DatePicker(Date value) {
		this(new DefaultDatePickerModel());
	}

	public DatePicker(DatePickerModel model) {
		this.model = model;

		markEmpty = true;

		Color outerBorder = UIManager.getColor("ComboBox.buttonDarkShadow");
		Color innerBorder = UIManager.getColor("ComboBox.buttonShadow");
		Font editorFont = UIManager.getFont("ComboBox.font");

		setBorder(BorderFactory.createLineBorder(outerBorder));

		popup = new DatePopup(model);

		dateText = new JTextField(model.getText());
		dateText.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(innerBorder, 1),
				BorderFactory.createEmptyBorder(2, 2, 2, 5)));
		dateText.setFont(editorFont);
		dateText.addFocusListener(this);

		popupButton = new JButton("<HTML>&hellip;</HTML>");
		popupButton.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 1, 0, 0, outerBorder),
				BorderFactory.createEmptyBorder(3, 3, 3, 3)));
		popupButton.addActionListener(new DropButtonListener(popup));

		setLayout(new BorderLayout());
		add(dateText, BorderLayout.CENTER);
		add(popupButton, BorderLayout.LINE_END);

		model.addDateChangeListener(this);
	}

	public boolean isMarkEmpty() {
		return markEmpty;
	}

	public void setMarkEmpty(boolean markEmpty) {
		this.markEmpty = markEmpty;
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
		String text = model.getText();

		dateText.setText(text);

		if (text.isEmpty() && markEmpty) {
			dateText.setBackground(UIManager
					.getColor("OptionPane.warningDialog.titlePane.background"));

			return;
		}

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
