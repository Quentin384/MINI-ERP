package com.mini_erp.ui;

import com.mini_erp.dao.ProductDAO;
import com.mini_erp.model.Product;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ProductPanel extends JPanel {
    private JComboBox<Integer> categoryFilter = new JComboBox<>();
    private JTable productTable = new JTable();
    private DefaultTableModel tableModel;
    private ProductDAO dao = new ProductDAO();

    public ProductPanel() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"ID", "Catégorie", "Titre", "Acteur", "Prix", "Spécial", "ID Commun"}, 0);
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
            List<Integer> categories = dao.getCategories();
            categoryFilter.removeAllItems();
            categoryFilter.addItem(null); // Pour "Tous"
            for (Integer cat : categories) {
                if (cat != null) {
                    categoryFilter.addItem(cat);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement catégories : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadProducts() {
        try {
            Integer selectedCategory = (Integer) categoryFilter.getSelectedItem();
            List<Product> products = dao.getProducts(selectedCategory);
            tableModel.setRowCount(0);
            for (Product p : products) {
                tableModel.addRow(new Object[]{
                        p.getProdId(),
                        p.getCategory(),
                        p.getTitle(),
                        p.getActor(),
                        p.getPrice(),
                        p.getSpecial(),
                        p.getCommonProdId()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement produits : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Product getSelectedProduct() {
        int row = productTable.getSelectedRow();
        if (row >= 0) {
            int id = (int) tableModel.getValueAt(row, 0);
            int category = (int) tableModel.getValueAt(row, 1);
            String title = (String) tableModel.getValueAt(row, 2);
            String actor = (String) tableModel.getValueAt(row, 3);
            double price = (double) tableModel.getValueAt(row, 4);
            short special = (short) tableModel.getValueAt(row, 5);
            int commonProdId = (int) tableModel.getValueAt(row, 6);
            return new Product(id, category, title, actor, price, special, commonProdId);
        }
        return null;
    }
}
