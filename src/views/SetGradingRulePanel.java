package views;

import grading.*;
import obj.Assignment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class SetGradingRulePanel extends JPanel {
    private JRadioButton ratioRadio;
    private JRadioButton dropLowRadio;
    private ButtonGroup strategyGroup;
    private JPanel ratioPanel;
    private JPanel dropLowPanel;
    private Map<String, JTextField> weightFields = new HashMap<>();
    private JTextField dropNField;
    private JButton saveButton;

    private GradeCalculator calculator;
    private Map<String, Double> weightDefaults;
    private Runnable onSaveCallback;

    public SetGradingRulePanel(Map<String, Double> currentWeights) {
        this.weightDefaults = currentWeights;

        setLayout(new BorderLayout());

        // 选择策略按钮区域
        JPanel strategyPanel = new JPanel();
        ratioRadio = new JRadioButton("Ratio Strategy");
        dropLowRadio = new JRadioButton("Drop Lowest N Strategy");
        strategyGroup = new ButtonGroup();
        strategyGroup.add(ratioRadio);
        strategyGroup.add(dropLowRadio);
        strategyPanel.add(ratioRadio);
        strategyPanel.add(dropLowRadio);
        add(strategyPanel, BorderLayout.NORTH);

        // 中间可切换的区域
        JPanel centerPanel = new JPanel(new CardLayout());
        ratioPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        dropLowPanel = new JPanel(new FlowLayout());

        for (Map.Entry<String, Double> entry : weightDefaults.entrySet()) {
            String aid = entry.getKey();
            double defaultWeight = entry.getValue();
            JLabel label = new JLabel("Assignment " + aid + " Weight (%):");
            JTextField field = new JTextField(String.valueOf(defaultWeight));
            weightFields.put(aid, field);
            ratioPanel.add(label);
            ratioPanel.add(field);
        }

        dropNField = new JTextField(5);
        dropLowPanel.add(new JLabel("Drop N lowest assignments:"));
        dropLowPanel.add(dropNField);

        centerPanel.add(ratioPanel, "RATIO");
        centerPanel.add(dropLowPanel, "DROP");
        add(centerPanel, BorderLayout.CENTER);

        // 保存按钮
        saveButton = new JButton("Save");
        add(saveButton, BorderLayout.SOUTH);

        // 切换策略区域
        CardLayout cl = (CardLayout) centerPanel.getLayout();
        ratioRadio.addActionListener(e -> cl.show(centerPanel, "RATIO"));
        dropLowRadio.addActionListener(e -> cl.show(centerPanel, "DROP"));
        ratioRadio.setSelected(true);
        cl.show(centerPanel, "RATIO");

        // 保存逻辑
        saveButton.addActionListener((ActionEvent e) -> {
            if (ratioRadio.isSelected()) {
                RatioStrategy strategy = new RatioStrategy();
                for (Map.Entry<String, JTextField> entry : weightFields.entrySet()) {
                    String aid = entry.getKey();
                    double weight = Double.parseDouble(entry.getValue().getText());
                    calculator.setAssignmentWeight(aid, weight);
                }
                calculator.setStrategy(strategy);
            } else if (dropLowRadio.isSelected()) {
                int n = Integer.parseInt(dropNField.getText());
                calculator.setStrategy(new DropLowNStrategy(n));
            }

            if (onSaveCallback != null) onSaveCallback.run();
        });
    }

    public void setGradeCalculator(GradeCalculator calculator) {
        this.calculator = calculator;
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }
}
