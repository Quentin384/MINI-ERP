package com.mini_erp.ui;

import javax.swing.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Mini ERP");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        CustomerPanel customerPanel = new CustomerPanel();
        ProductPanel productPanel = new ProductPanel();
        OrderPanel orderPanel = new OrderPanel(customerPanel, productPanel);

        // Ici on passe seulement productPanel au lieu de customerPanel + productPanel
        OrderHistoryPanel orderHistoryPanel = new OrderHistoryPanel(productPanel);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Clients", customerPanel);
        tabbedPane.addTab("Produits", productPanel);
        tabbedPane.addTab("Nouvelle commande", orderPanel);
        tabbedPane.addTab("Historique commandes", orderHistoryPanel);

        add(tabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
