package com.mini_erp.ui;

import javax.swing.*;

public class MainFrame extends JFrame {

    private CustomerPanel customerPanel;
    private ProductPanel productPanel;
    private AddOrderPanel addOrderPanel;
    private OrderHistoryPanel orderHistoryPanel;
    private InventoryPanel inventoryPanel;

    public MainFrame() {
        setTitle("Mini ERP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Initialisation des panels
        customerPanel = new CustomerPanel();
        productPanel = new ProductPanel();
        orderHistoryPanel = new OrderHistoryPanel();          // sans paramètre
        addOrderPanel = new AddOrderPanel(orderHistoryPanel); // prend orderHistoryPanel en paramètre
        inventoryPanel = new InventoryPanel();                // nouveau panel d'inventaire

        // Création du panneau à onglets
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Clients", customerPanel);
        tabbedPane.addTab("Produits", productPanel);
        tabbedPane.addTab("Ajouter Commande", addOrderPanel);
        tabbedPane.addTab("Historique Commandes", orderHistoryPanel);
        tabbedPane.addTab("Inventaire", inventoryPanel); // nouveau

        getContentPane().add(tabbedPane);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
