package com.mini_erp.ui;

import com.mini_erp.dao.CustomerDAO;
import com.mini_erp.dao.OrderDAO;
import com.mini_erp.dao.ProductDAO;
import com.mini_erp.model.Customer;
import com.mini_erp.model.OrderLine;
import com.mini_erp.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AddOrderPanel extends JPanel {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private final OrderDAO orderDAO = new OrderDAO();
    private final ProductDAO productDAO = new ProductDAO();

    private JComboBox<Customer> customerComboBox;
    private JComboBox<Product> productComboBox;
    private JSpinner quantitySpinner;
    private JButton addProductButton;
    private JButton submitOrderButton;

    private DefaultTableModel orderLinesModel;
    private JTable orderLinesTable;

    // Liste des lignes de commande en cours
    private final List<OrderLine> currentOrderLines = new ArrayList<>();

    private final OrderHistoryPanel orderHistoryPanel;

    public AddOrderPanel(OrderHistoryPanel orderHistoryPanel) {
        this.orderHistoryPanel = orderHistoryPanel;
        setLayout(new BorderLayout());

        // Panel client
        JPanel clientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clientPanel.add(new JLabel("Client :"));
        customerComboBox = new JComboBox<>();
        customerComboBox.setPreferredSize(new Dimension(300, 25));
        clientPanel.add(customerComboBox);
        add(clientPanel, BorderLayout.NORTH);

        // Panel sélection produit + quantité + bouton ajouter produit
        JPanel productPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        productPanel.add(new JLabel("Produit :"));
        productComboBox = new JComboBox<>();
        productComboBox.setPreferredSize(new Dimension(300, 25));
        productPanel.add(productComboBox);

        productPanel.add(new JLabel("Quantité :"));
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        productPanel.add(quantitySpinner);

        addProductButton = new JButton("Ajouter produit");
        productPanel.add(addProductButton);

        add(productPanel, BorderLayout.CENTER);

        // Table pour afficher les produits ajoutés
        orderLinesModel = new DefaultTableModel(new Object[]{"Produit", "Quantité"}, 0);
        orderLinesTable = new JTable(orderLinesModel);
        JScrollPane scrollPane = new JScrollPane(orderLinesTable);
        scrollPane.setPreferredSize(new Dimension(400, 150));
        add(scrollPane, BorderLayout.EAST);

        // Bouton valider la commande
        submitOrderButton = new JButton("Valider la commande");
        submitOrderButton.setEnabled(false);
        add(submitOrderButton, BorderLayout.SOUTH);

        // Chargement des clients et produits
        loadCustomers();
        loadProducts();

        // Actions
        addProductButton.addActionListener(e -> addProductToOrder());
        submitOrderButton.addActionListener(e -> submitOrder());
    }

    private void loadCustomers() {
        try {
            List<Customer> customers = customerDAO.getAllCustomers();
            DefaultComboBoxModel<Customer> model = new DefaultComboBoxModel<>();
            for (Customer c : customers) {
                model.addElement(c);
            }
            customerComboBox.setModel(model);
            if (!customers.isEmpty()) {
                customerComboBox.setSelectedIndex(0);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur chargement clients : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadProducts() {
        try {
            List<Product> products = productDAO.getProducts(null);
            DefaultComboBoxModel<Product> model = new DefaultComboBoxModel<>();
            for (Product p : products) {
                model.addElement(p);
            }
            productComboBox.setModel(model);
            if (!products.isEmpty()) {
                productComboBox.setSelectedIndex(0);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur chargement produits : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addProductToOrder() {
        Product selectedProduct = (Product) productComboBox.getSelectedItem();
        int quantity = (Integer) quantitySpinner.getValue();

        if (selectedProduct == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this, "La quantité doit être supérieure à 0.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Vérifier si le produit est déjà dans la liste
        boolean found = false;
        for (OrderLine line : currentOrderLines) {
            if (line.getProductId() == selectedProduct.getProdId()) {
                // Mettre à jour la quantité
                line.setQuantity(line.getQuantity() + quantity);
                found = true;
                break;
            }
        }

        if (!found) {
            currentOrderLines.add(new OrderLine(selectedProduct.getProdId(), quantity));
        }

        refreshOrderLinesTable();

        submitOrderButton.setEnabled(!currentOrderLines.isEmpty());
    }

    private void refreshOrderLinesTable() {
        orderLinesModel.setRowCount(0);
        for (OrderLine line : currentOrderLines) {
            Product p = null;
            for (int i = 0; i < productComboBox.getItemCount(); i++) {
                Product prod = productComboBox.getItemAt(i);
                if (prod.getProdId() == line.getProductId()) {
                    p = prod;
                    break;
                }
            }
            String productName = (p != null) ? p.getTitle() : "Produit #" + line.getProductId();
            orderLinesModel.addRow(new Object[]{productName, line.getQuantity()});
        }
    }

    private void submitOrder() {
        Customer selectedCustomer = (Customer) customerComboBox.getSelectedItem();

        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Crée un objet Order avec client et lignes de commande
            com.mini_erp.model.Order order = new com.mini_erp.model.Order(
                    0,
                    selectedCustomer.getId(),
                    null,
                    0, 0, 0,
                    new ArrayList<>(currentOrderLines)
            );

            orderDAO.createOrder(order);

            JOptionPane.showMessageDialog(this,
                    "Commande ajoutée avec succès.",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);

            currentOrderLines.clear();
            refreshOrderLinesTable();
            submitOrderButton.setEnabled(false);
            quantitySpinner.setValue(1);

            // Rafraîchir l'historique des commandes
            orderHistoryPanel.refreshOrders();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur ajout commande : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
