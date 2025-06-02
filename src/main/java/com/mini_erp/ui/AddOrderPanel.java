package com.mini_erp.ui;

import com.mini_erp.dao.CustomerDAO;
import com.mini_erp.dao.OrderDAO;
import com.mini_erp.dao.ProductDAO;
import com.mini_erp.model.Customer;
import com.mini_erp.model.OrderLine;
import com.mini_erp.model.Product;

import javax.swing.*;
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
    private JButton addOrderButton;

    public AddOrderPanel() {
        setLayout(new BorderLayout());

        // Panel haut - sélection client
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Client :"));
        customerComboBox = new JComboBox<>();
        customerComboBox.setPreferredSize(new Dimension(300, 25));
        topPanel.add(customerComboBox);
        add(topPanel, BorderLayout.NORTH);

        // Panel centre - sélection produit + quantité
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        centerPanel.add(new JLabel("Produit :"));
        productComboBox = new JComboBox<>();
        productComboBox.setPreferredSize(new Dimension(300, 25));
        centerPanel.add(productComboBox);

        centerPanel.add(new JLabel("Quantité :"));
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        centerPanel.add(quantitySpinner);

        add(centerPanel, BorderLayout.CENTER);

        // Bouton valider commande
        addOrderButton = new JButton("Ajouter la commande");
        add(addOrderButton, BorderLayout.SOUTH);

        // Chargement des données dans les combos
        loadCustomers();
        loadProducts();

        // Action bouton
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

    private void addOrder() {
        Customer selectedCustomer = (Customer) customerComboBox.getSelectedItem();
        Product selectedProduct = (Product) productComboBox.getSelectedItem();
        int quantity = (Integer) quantitySpinner.getValue();

        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un client.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

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

        try {
            List<OrderLine> orderLines = new ArrayList<>();
            orderLines.add(new OrderLine(selectedProduct.getProdId(), quantity));

            orderDAO.addOrder(selectedCustomer.getId(), orderLines);

            JOptionPane.showMessageDialog(this,
                    "Commande ajoutée avec succès.",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);

            quantitySpinner.setValue(1);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur ajout commande : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
