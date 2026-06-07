package org.example;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        System.out.println("Check");
        SwingUtilities.invokeLater(() -> new SetupFrame().setVisible(true));
    }

}