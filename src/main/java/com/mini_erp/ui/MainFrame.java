package com.mini_erp.ui;

import javax.swing.*;

public class MainFrame extends JFrame {

    private CustomerPanel customerPanel;
    private ProductPanel productPanel;
    private AddOrderPanel addOrderPanel;
    private OrderHistoryPanel orderHistoryPanel;

    public MainFrame() {
        setTitle("Mini ERP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Initialisation des panels
        customerPanel = new CustomerPanel();
        productPanel = new ProductPanel();
        addOrderPanel = new AddOrderPanel();
        orderHistoryPanel = new OrderHistoryPanel(productPanel);

        // Création du panneau à onglets
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Clients", customerPanel);
        tabbedPane.addTab("Produits", productPanel);
        tabbedPane.addTab("Ajouter Commande", addOrderPanel);
        tabbedPane.addTab("Historique Commandes", orderHistoryPanel);

        getContentPane().add(tabbedPane);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
