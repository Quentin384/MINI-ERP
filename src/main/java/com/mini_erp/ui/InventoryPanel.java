package com.mini_erp.ui;

import com.mini_erp.db.DatabaseManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class InventoryPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    public InventoryPanel() {
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new Object[]{"ID Produit", "Quantité en stock", "Ventes"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton refreshButton = new JButton("Rafraîchir");
        refreshButton.addActionListener(e -> loadInventory());

        add(refreshButton, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        loadInventory();
    }

    private void loadInventory() {
        model.setRowCount(0);
        String sql = "SELECT prod_id, quan_in_stock, sales FROM inventory ORDER BY prod_id";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int prodId = rs.getInt("prod_id");
                int stock = rs.getInt("quan_in_stock");
                int sales = rs.getInt("sales");
                model.addRow(new Object[]{prodId, stock, sales});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement de l'inventaire : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
