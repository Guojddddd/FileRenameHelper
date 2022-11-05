package filerenamehelper.view;

import filerenamehelper.service.Service;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author guo
 */
@Slf4j
public class Client extends JFrame {
	public static void main(String[] args) {
		new Client();
	}

	private JTextArea dirPathArea = new JTextArea();
	private JTextArea outputArea = new JTextArea();
	private JTextField yearField = new JTextField();
	private JComboBox<String> monthField = new JComboBox<>();
	private JComboBox<String> dayField = new JComboBox<>();
	private JComboBox<String> sequField = new JComboBox<>();
	private JComboBox<String> sortAlgorithmField = new JComboBox<>();
	private JComboBox<String> noField = new JComboBox<>();
	private JRadioButton sequIncrementSelect = new JRadioButton();
	private JRadioButton noIncrementSelect = new JRadioButton();
	private Service service = new Service();
	private JButton confirmButton = new JButton("CONFIRM");

	public Client() {
		this.setLocation(100, 100);
		this.setSize(800, 600);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setTitle("文件重命名辅助器");
		this.init();
		this.setVisible(true);
	}

	private void init() {
		JPanel operatePanel = new JPanel();
		operatePanel.setLayout(null);
		operatePanel.setPreferredSize(new Dimension(800, 200));
		{
			operatePanel.add(getLabel("paths", 40, 100, 10, 10));
			JScrollPane scrollPane = new JScrollPane(dirPathArea);

			scrollPane.setSize(700, 100);
			scrollPane.setLocation(60, 10);
			operatePanel.add(scrollPane);
		}
		{
			Date currentTime = new Date();

			yearField.setText(new SimpleDateFormat("yyyy").format(currentTime));
			yearField.setSize(50, 30);
			yearField.setLocation(10, 120);
			operatePanel.add(yearField);

			operatePanel.add(getLabel("-", 10, 30, 60, 120));

			for (int i = 1; i <= 12; i++) {
				monthField.addItem(String.format("%02d", i));
			}
			monthField.setSelectedIndex(Integer.parseInt(new SimpleDateFormat("MM").format(currentTime)) - 1);
			monthField.setSize(50, 30);
			monthField.setLocation(70, 120);
			operatePanel.add(monthField);

			operatePanel.add(getLabel("-", 10, 30, 120, 120));

			for (int i = 1; i <= 31; i++) {
				dayField.addItem(String.format("%02d", i));
			}
			dayField.setSelectedIndex(Integer.parseInt(new SimpleDateFormat("dd").format(currentTime)) - 1);
			dayField.setSize(50, 30);
			dayField.setLocation(130, 120);
			operatePanel.add(dayField);

			operatePanel.add(getLabel(".", 10, 30, 180, 120));

			for (int i = 1; i <= 99; i++) {
				sequField.addItem(String.format("%02d", i));
			}
			sequField.setSelectedIndex(0);
			sequField.setSize(50, 30);
			sequField.setLocation(190, 120);
			operatePanel.add(sequField);

			operatePanel.add(getLabel(".", 10, 30, 240, 120));

			for (int i = 1; i <= 99; i++) {
				noField.addItem(String.format("%02d", i));
			}
			noField.setSize(50, 30);
			noField.setLocation(250, 120);
			operatePanel.add(noField);

			sortAlgorithmField.addItem("Dict Sequ");
			sortAlgorithmField.addItem("Length First Dict Sequ");
			sortAlgorithmField.addItem("Number First Dict Sequ");
			sortAlgorithmField.addItem("Number First Dict Sequ-NoEx");
			sortAlgorithmField.addItem("Create Time First");
			sortAlgorithmField.setSelectedIndex(0);
			sortAlgorithmField.setSize(210, 30);
			sortAlgorithmField.setLocation(320, 120);
			operatePanel.add(sortAlgorithmField);
		}
		{
			JButton viewButton = new JButton("PRE VIEW");
			viewButton.setSize(100, 30);
			viewButton.setLocation(550, 120);
			operatePanel.add(viewButton);
			viewButton.addActionListener(e -> {
				String mode = "";
				if (sequIncrementSelect.isSelected()) {
					mode = "sequ";
				} else if (noIncrementSelect.isSelected()) {
					mode = "no";
				}

				String report = service.preView(dirPathArea.getText(), yearField.getText(), (String) monthField.getSelectedItem(), (String) dayField.getSelectedItem(), (String) sequField.getSelectedItem(), (String) noField.getSelectedItem(), (String) sortAlgorithmField.getSelectedItem(), mode);
				outputArea.setText(report);
				if (report != null) {
					setConfirmEnabled(true);
				}
			});

			confirmButton.setSize(100, 30);
			confirmButton.setLocation(660, 120);
			confirmButton.setEnabled(false);
			operatePanel.add(confirmButton);
			confirmButton.addActionListener(e -> {
				boolean result = service.executeRename();
				if (result) {
					setConfirmEnabled(false);
					JOptionPane.showMessageDialog(this, "完成");
				}
			});
		}
		{
			ButtonGroup buttonGroup = new ButtonGroup();
			sequIncrementSelect.setLocation(190, 160);
			sequIncrementSelect.setSize(50, 20);
			buttonGroup.add(sequIncrementSelect);

			noIncrementSelect.setLocation(250, 160);
			noIncrementSelect.setSize(50, 20);
			buttonGroup.add(noIncrementSelect);
			noIncrementSelect.setSelected(true);

			operatePanel.add(sequIncrementSelect);
			operatePanel.add(noIncrementSelect);
		}
		this.add(operatePanel, BorderLayout.NORTH);

		outputArea.setFont(new Font("宋体", Font.PLAIN, 12));
		JScrollPane scrollPane = new JScrollPane(outputArea);
		this.add(scrollPane, BorderLayout.CENTER);
	}

	private JLabel getLabel(String msg, int w, int h, int x, int y) {
		JLabel label = new JLabel(msg);
		label.setSize(w, h);
		label.setLocation(x, y);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setVerticalAlignment(SwingConstants.CENTER);
		return label;
	}

	private void reset() {
		outputArea.setText(null);
		service.clear();
		setConfirmEnabled(false);
	}

	private void setConfirmEnabled(boolean bool) {
		confirmButton.setEnabled(bool);
	}
}
