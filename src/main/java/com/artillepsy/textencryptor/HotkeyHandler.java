package com.artillepsy.textencryptor;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import javax.crypto.SecretKey;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

/**
 * Handles global hotkeys for encrypting (CTRL+Y) and decrypting (CTRL+U) selected text using clipboard operations.
 */
public class HotkeyHandler implements NativeKeyListener {
    private SecretKey currentSecretKey;
    private final Robot robot;
    private final Clipboard clipboard;

    /**
     * Initializes Robot for keyboard simulation and accesses system clipboard.
     */
    public HotkeyHandler() throws AWTException {
        robot = new Robot();
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    /**
     * Sets the secret key used for encryption and decryption operations.
     */
    public void setSecretKey(SecretKey key) {
        currentSecretKey = key;
    }

    /**
     * Intercepts global key presses and triggers encryption (CTRL+Y) or decryption (CTRL+U) of selected text.
     */
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        if (currentSecretKey == null) {
            return;
        }

        var isCtrlPressed = (e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0;

        // CTRL + Y to Encrypt
        if (isCtrlPressed && e.getKeyCode() == NativeKeyEvent.VC_Y) {
            handleTextTransformation(true);
        }
        // CTRL + U to Decrypt
        else if (isCtrlPressed && e.getKeyCode() == NativeKeyEvent.VC_U) {
            handleTextTransformation(false);
        }
    }

    /**
     * Copies selected text, encrypts or decrypts it using the secret key, and pastes the result back.
     */
    private void handleTextTransformation(boolean isEncrypt) {
        try {
            simulateTextCopy();

            Thread.sleep(150);

            // Get text from clipboard
            var contents = clipboard.getContents(null);
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                var selectedText = (String) contents.getTransferData(DataFlavor.stringFlavor);

                var result = isEncrypt
                        ? EncryptionHelper.encrypt(selectedText, currentSecretKey)
                        : EncryptionHelper.decrypt(selectedText, currentSecretKey);

                clipboard.setContents(new StringSelection(result), null);
                simulateTextPaste();
            }
        } catch (Exception ex) {
            System.err.println("Hotkey processing failed: " + ex.getMessage());
        }
    }

    /**
     * Simulates CTRL+C keyboard shortcut to copy selected text to clipboard.
     */
    private void simulateTextCopy() {
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_C);
        robot.keyRelease(KeyEvent.VK_C);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    /**
     * Simulates CTRL+V keyboard shortcut to paste clipboard content at current cursor position.
     */
    private void simulateTextPaste() {
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }
}
