# Text Encryptor

## Table of Contents

- [Features](#features)
- [How to Use](#how-to-use)
- [Stack](#stack)
- [Building the Project](#building-the-project)
    - [1. Build a Fat JAR](#1-build-a-fat-jar)
    - [2. Build a Portable Windows App (.exe)](#2-build-a-portable-windows-app-exe)
- [Distribution](#distribution)
- [Troubleshooting](#troubleshooting)

---

A lightweight, portable Windows utility written in Java 25 that allows you to encrypt or decrypt selected text anywhere 
on your system using global hotkeys. The application lives in the system tray and uses AES-256 GCM encryption.

## Features

* Global Hotkeys: Encrypt or decrypt text in any application (Notepad, Browser, Discord, etc.).
* Secure Encryption: Uses AES/GCM/NoPadding with keys derived via PBKDF2.
* System Tray Integration: Runs quietly in the background without cluttering your taskbar.
* Portable: Can be bundled with its own Java Runtime (JRE).

---

## How to Use
1.  Launch the application.
2.  Enter a Password in the "Secret Key" field and click Set Key & Minimize to Tray.
3.  Highlight any text you want to transform.
4.  Use the Hotkeys:
    * `Ctrl + E`: Encrypt selected text.
    * `Ctrl + D`: Decrypt selected text.
5. To change the key or exit, right-click the icon in the System Tray.

---

## Stack

* Java 25 (JDK)
* Maven 3.9+

### Main Dependencies
* [JNativeHook](https://github.com/kwhat/jnativehook): For global keyboard listening.
* Standard Java `javax.crypto`: For AES/GCM operations.
* Swing/AWT: For the user interface and clipboard manipulation.

---

## Building the Project

### 1. Build a Fat JAR

This creates a single `.jar` file containing all dependencies. To run this file, the target machine must have Java 25
installed.

```bash
mvn clean package
```

### 2. Build a Portable Windows App (.exe)

To create a version that works on any Windows PC without requiring Java to be installed, use jpackage. 
This bundles a stripped-down JRE into the folder.

Run the following command in your terminal after building the Fat JAR:

```bash
jpackage `
  --type app-image `
  --dest dist `
  --name "TextEncryptor" `
  --input target `
  --main-jar text-encryptor-1.0-SNAPSHOT.jar `
  --main-class com.artillepsy.textencryptor.MainApp `
  --java-options "--enable-native-access=ALL-UNNAMED"
```

---

## Distribution:
Zip the entire dist/TextEncryptor folder. The user only needs to run TextEncryptor.exe inside that folder.

---

## Troubleshooting

- Some Antivirus software may flag the app because it uses "Global Hooks" to listen for hotkeys. 
You may need to whitelist the executable.

- If the app fails to copy/paste in certain windows (like Task Manager), try running the application As Administrator.

- If your keyboard acts strangely after a transformation, 
press and release the Ctrl and Shift keys manually to reset the virtual state.