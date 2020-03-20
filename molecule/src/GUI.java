import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.SQLException;

public class GUI extends Component implements ActionListener {
    JLabel title;
    JLabel add_label;
    JButton add_button;
    JTextField add_field;
    JLabel search_label;
    JButton search_button;
    JTextField search_field;

    public GUI()
    {
        JFrame guiFrame = new JFrame();
        guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        guiFrame.setTitle("Molecule Database");
        guiFrame.setSize(400,150);
        guiFrame.setLocationRelativeTo(null);

        final JPanel titlePanel = new JPanel();
        title = new JLabel("Welcome to Molecule Database!");
        titlePanel.add(title);

        final JPanel addPanel = new JPanel();
        addPanel.setVisible(true);
        add_label = new JLabel("Add molecule from: ");
        addPanel.add(add_label);

        add_field = new JTextField(10);
        addPanel.add(add_field);

        add_button = new JButton("Browse..");
        addPanel.add(add_button);

        search_label = new JLabel("Find molecule: ");
        addPanel.add(search_label);

        search_field = new JTextField(10);
        addPanel.add(search_field);

        search_button = new JButton("Browse..");
        addPanel.add(search_button);

        //JButton vegFruitBut = new JButton( "Fruit or Veg");

        add_button.addActionListener(this);
        search_button.addActionListener(this);

        guiFrame.add(titlePanel, BorderLayout.NORTH);
        guiFrame.add(addPanel, BorderLayout.CENTER);

        guiFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == add_button) {
            JFileChooser fc = new JFileChooser();
            int i = fc.showOpenDialog(this);
            if (i == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                // String filepath = f.getPath();
                add_field.setText(f.getName());
                ReadFile rf = new ReadFile();
                try {
                    rf.singleRead(f.getPath());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        else if (e.getSource() == search_button) {
            JFileChooser fc = new JFileChooser();
            int i = fc.showOpenDialog(this);
            if (i == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                // String filepath = f.getPath();
                search_button.setText(f.getName());
            }
        }
    }
}