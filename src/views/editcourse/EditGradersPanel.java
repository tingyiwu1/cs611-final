package views.editcourse;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Optional;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;

import model.Grader;
import views.MainWindow;

public class EditGradersPanel extends JPanel {
    private static final int GRADER_ID_COLUMNS = 10;

    private final MainWindow mainWindow;

    private final JTextField graderIdField;
    private final JButton addGraderButton;
    private final JButton removeGraderButton;
    private final JList<Grader> graderList;

    private final DefaultListModel<Grader> listModel = new DefaultListModel<>();

    public EditGradersPanel(MainWindow mainWindow) {
        this.mainWindow = mainWindow;

        this.graderIdField = new JTextField(GRADER_ID_COLUMNS);
        graderIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, graderIdField.getPreferredSize().height));

        this.addGraderButton = new JButton("Add");
        this.addGraderButton.setPreferredSize(new Dimension(100, 20));
        this.addGraderButton.addActionListener(this::onAddGrader);

        JPanel addGraderForm = new JPanel();
        addGraderForm.setLayout(new BoxLayout(addGraderForm, BoxLayout.X_AXIS));
        addGraderForm.add(new JLabel("Grader ID:"));
        addGraderForm.add(graderIdField);
        addGraderForm.add(addGraderButton);

        this.removeGraderButton = new JButton("Remove");
        this.removeGraderButton.setPreferredSize(new Dimension(100, 20));
        this.removeGraderButton.addActionListener(this::onRemoveGrader);
        this.removeGraderButton.setEnabled(false);
        // this.removeGraderButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.graderList = new JList<>(listModel);
        graderList.setCellRenderer(new ListCellRenderer<Grader>() {

            @Override
            public Component getListCellRendererComponent(JList<? extends Grader> list, Grader value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = new JLabel(value.getName());
                label.setOpaque(true);
                if (isSelected) {
                    label.setBackground(list.getSelectionBackground());
                    label.setForeground(list.getSelectionForeground());
                } else {
                    label.setBackground(list.getBackground());
                    label.setForeground(list.getForeground());
                }
                return label;
            }
        });

        graderList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                removeGraderButton.setEnabled(graderList.getSelectedValue() != null);
            }
        });

        JScrollPane scrollPane = new JScrollPane(graderList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setMaximumSize(new Dimension(scrollPane.getPreferredSize().width, 100));

        JLabel titleLabel = new JLabel("Graders");
        titleLabel.setFont(titleLabel.getFont().deriveFont(16f));

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(titleLabel);
        add(addGraderForm);
        add(scrollPane);
        add(removeGraderButton);
    }

    public void setGraders(ArrayList<Grader> graders) {
        listModel.clear();
        for (Grader g : graders) {
            listModel.addElement(g);
        }
    }

    public ArrayList<Grader> getGraders() {
        ArrayList<Grader> graders = new ArrayList<>();
        for (int i = 0; i < listModel.size(); i++) {
            graders.add(listModel.getElementAt(i));
        }
        return graders;
    }

    private void onAddGrader(ActionEvent e) {
        String graderId = graderIdField.getText();

        if (graderId == null || graderId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Grader ID cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Optional<Grader> graderResult = mainWindow.getStore().get(Grader.class, graderIdField.getText());
        if (!graderResult.isPresent()) {
            JOptionPane.showMessageDialog(this, "Grader with id " + graderId + " not found", "Not Found",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        Grader grader = graderResult.get();
        if (getGraders().contains(grader)) {
            JOptionPane.showMessageDialog(this, "Grader " + grader.getName() + " already added", "Already Added",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        listModel.addElement(grader);
        graderIdField.setText("");
    }

    private void onRemoveGrader(ActionEvent e) {
        Grader selectedGrader = graderList.getSelectedValue();
        if (selectedGrader == null) {
            return;
        }
        listModel.removeElement(selectedGrader);
    }
}
