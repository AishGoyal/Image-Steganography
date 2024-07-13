import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class index extends JFrame {

    private JTextField messageField;
    private JButton encryptButton;
    private JButton decryptButton;

    public index() {
        setTitle("Image Steganography");
        setSize(350, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        JLabel messageLabel = new JLabel("Message:");
        messageField = new JTextField(20);
        encryptButton = new JButton("Encrypt");
        decryptButton = new JButton("Decrypt");

        // Set layout
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.insets = new Insets(5, 5, 5, 5);

        // Add components to the frame
        add(messageLabel, constraints);

        constraints.gridx = 1;
        add(messageField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        add(encryptButton, constraints);

        constraints.gridy = 2;
        add(decryptButton, constraints);

        // Add action listeners to the buttons
        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                // Implement your encryption logic here
                // You can use the "message" variable to access the input text
                File imageFile = FileChooser.MakeFileChooser();
                if(imageFile != null){
                    EncryptLSB.Encrypt(imageFile, message);
                }
            }
        });

        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement your decryption logic here
                // You can use the "message" variable to access the input text
                String message = DecryptLSB.Decrypt();
                messageField.setText(message);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new index().setVisible(true);
            }
        });
    }
}