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

    private JComboBox<Customer> customerComboBox;
    private JComboBox<Product> productComboBox;
    private JSpinner quantitySpinner;
    private JButton addProductButton;
    private JButton removeProductButton;
    private JButton addOrderButton;

    private DefaultTableModel orderLinesModel;
    private JTable orderLinesTable;

    private final List<OrderLine> orderLines = new ArrayList<>();

    public AddOrderPanel() {
        setLayout(new BorderLayout());

        // Panel sélection client
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Client :"));
        customerComboBox = new JComboBox<>();
        customerComboBox.setPreferredSize(new Dimension(300, 25));
        topPanel.add(customerComboBox);
        add(topPanel, BorderLayout.NORTH);

        // Panel sélection produit + quantité + bouton ajout produit
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        centerPanel.add(new JLabel("Produit :"));
        productComboBox = new JComboBox<>();
        productComboBox.setPreferredSize(new Dimension(250, 25));
        centerPanel.add(productComboBox);

        centerPanel.add(new JLabel("Quantité :"));
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        centerPanel.add(quantitySpinner);

        addProductButton = new JButton("Ajouter produit");
        centerPanel.add(addProductButton);

        removeProductButton = new JButton("Supprimer produit");
        centerPanel.add(removeProductButton);

        add(centerPanel, BorderLayout.CENTER);

        // Table pour afficher les produits sélectionnés
        orderLinesModel = new DefaultTableModel(new Object[]{"Produit", "Quantité"}, 0);
        orderLinesTable = new JTable(orderLinesModel);
        JScrollPane scrollPane = new JScrollPane(orderLinesTable);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        add(scrollPane, BorderLayout.SOUTH);

        // Bouton final ajouter commande
        addOrderButton = new JButton("Valider la commande");
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(addOrderButton);
        add(bottomPanel, BorderLayout.PAGE_END);

        // Chargement données
        loadCustomers();
        loadProducts();

        // Action boutons
        addProductButton.addActionListener(e -> addProductToOrder());
        removeProductButton.addActionListener(e -> removeSelectedProduct());
        addOrderButton.addActionListener(e -> addOrder());
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
            ProductDAO productDAO = new ProductDAO();
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
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un produit.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this,
                    "La quantité doit être supérieure à 0.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Vérifier si produit déjà ajouté -> incrémenter quantité
        boolean found = false;
        for (int i = 0; i < orderLines.size(); i++) {
            OrderLine ol = orderLines.get(i);
            if (ol.getProductId() == selectedProduct.getProdId()) {
                ol.setQuantity(ol.getQuantity() + quantity);
                orderLinesModel.setValueAt(ol.getQuantity(), i, 1);
                found = true;
                break;
            }
        }
        if (!found) {
            OrderLine newLine = new OrderLine(selectedProduct.getProdId(), quantity);
            orderLines.add(newLine);
            orderLinesModel.addRow(new Object[]{selectedProduct.getTitle(), quantity});
        }
    }

    private void removeSelectedProduct() {
        int selectedRow = orderLinesTable.getSelectedRow();
        if (selectedRow >= 0) {
            orderLines.remove(selectedRow);
            orderLinesModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un produit à supprimer dans la liste.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addOrder() {
        Customer selectedCustomer = (Customer) customerComboBox.getSelectedItem();

        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un client.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (orderLines.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez ajouter au moins un produit à la commande.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            orderDAO.addOrder(selectedCustomer.getId(), orderLines);

            JOptionPane.showMessageDialog(this,
                    "Commande ajoutée avec succès.",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);

            // Reset formulaire
            orderLines.clear();
            orderLinesModel.setRowCount(0);
            quantitySpinner.setValue(1);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur ajout commande : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
