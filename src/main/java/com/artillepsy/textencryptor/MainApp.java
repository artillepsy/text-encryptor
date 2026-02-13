package com.artillepsy.textencryptor;

import com.github.kwhat.jnativehook.GlobalScreen;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainApp {
    private JFrame frame;
    private HotkeyHandler hotkeyHandler;

    public MainApp() {
        try {
            hotkeyHandler = new HotkeyHandler();
            setupGlobalHook();
            setupSystemTray();
            createAndShowGUI();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Startup Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Swing must run on the Event Dispatch Thread
        SwingUtilities.invokeLater(MainApp::new);
    }

    private void setupGlobalHook() throws Exception {
        var logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);

        GlobalScreen.registerNativeHook();
        GlobalScreen.addNativeKeyListener(hotkeyHandler);
    }

    private void setupSystemTray() {
        if (!SystemTray.isSupported()) {
            return;
        }

        try {
            var tray = SystemTray.getSystemTray();

            var image = Toolkit.getDefaultToolkit().createImage(new byte[0]);

            var popup = new PopupMenu();
            var showItem = new MenuItem("Open Settings");
            showItem.addActionListener(e -> frame.setVisible(true));

            var exitItem = new MenuItem("Exit");
            exitItem.addActionListener(e -> {
                try { GlobalScreen.unregisterNativeHook(); } catch (Exception ignored) {}
                System.exit(0);
            });

            popup.add(showItem);
            popup.addSeparator();
            popup.add(exitItem);

            var trayIcon = new TrayIcon(image, "Text Encryptor", popup);
            trayIcon.setImageAutoSize(true);
            tray.add(trayIcon);
        }
        catch (AWTException e) {
            System.err.println("Tray icon could not be added.");
        }
    }

    private void createAndShowGUI() {
        frame = new JFrame("Text Encryptor Settings");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); // Hide instead of exit
        frame.setSize(400, 200);
        frame.setLayout(new GridBagLayout());

        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Components
        var label = new JLabel("Secret Key:");
        var passwordField = new JPasswordField(20);
        var submitButton = getJButton(passwordField);

        // Layout Assembly
        gbc.gridx = 0; gbc.gridy = 0;
        frame.add(label, gbc);

        gbc.gridy = 1;
        frame.add(passwordField, gbc);

        gbc.gridy = 2;
        frame.add(submitButton, gbc);

        frame.setLocationRelativeTo(null); // Center on screen
        frame.setVisible(true);
    }

    private JButton getJButton(JPasswordField passwordField) {
        var submitButton = new JButton("Set Key & Minimize to Tray");

        // Action: When user clicks submit
        submitButton.addActionListener(e -> {
            String password = new String(passwordField.getPassword());
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Provide a password");
                return;
            }

            try {
                hotkeyHandler.setSecretKey(EncryptionHelper.createKey(password));

                // Hide window and show a notification
                frame.setVisible(false);
                System.out.println("Key has been set. App is active in background.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error creating key: " + ex.getMessage());
            }
        });
        return submitButton;
    }
}
