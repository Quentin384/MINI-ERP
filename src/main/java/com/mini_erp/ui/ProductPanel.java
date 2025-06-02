package com.mini_erp.ui;

import com.mini_erp.dao.ProductDAO;
import com.mini_erp.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ProductPanel extends JPanel {
    private JComboBox<String> categoryFilter = new JComboBox<>();
    private JTable productTable = new JTable();
    private DefaultTableModel tableModel;
    private ProductDAO dao = new ProductDAO();

    public ProductPanel() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"ID", "Nom", "Prix", "Catégorie"}, 0);
        productTable.setModel(tableModel);

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Catégorie:"));
        topPanel.add(categoryFilter);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(productTable), BorderLayout.CENTER);

        categoryFilter.addActionListener(e -> loadProducts());

        loadCategories();
        loadProducts();
    }

    private void loadCategories() {
        try {
            List<String> categories = dao.getCategories();
            categoryFilter.removeAllItems();
            for (String cat : categories) {
                categoryFilter.addItem(cat);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement catégories : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadProducts() {
        try {
            String selectedCategory = (String) categoryFilter.getSelectedItem();
            List<Product> products = dao.getProducts(selectedCategory);
            tableModel.setRowCount(0);
            for (Product p : products) {
                tableModel.addRow(new Object[]{p.getId(), p.getName(), p.getPrice(), p.getCategory()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement produits : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Product getSelectedProduct() {
        int row = productTable.getSelectedRow();
        if (row >= 0) {
            int id = (int) tableModel.getValueAt(row, 0);
            String name = (String) tableModel.getValueAt(row, 1);
            double price = (double) tableModel.getValueAt(row, 2);
            String category = (String) tableModel.getValueAt(row, 3);
            return new Product(id, name, price, category);
        }
        return null;
    }
}
