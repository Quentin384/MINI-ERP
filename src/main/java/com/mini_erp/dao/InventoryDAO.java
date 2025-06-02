package com.mini_erp.dao;

import com.mini_erp.db.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {

    public List<String> getProductsToRestock(int threshold) throws SQLException {
        List<String> products = new ArrayList<>();
        String sql = "SELECT p.name FROM products p JOIN inventory i ON p.id = i.product_id WHERE i.quan_in_stock < ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, threshold);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(rs.getString("name"));
                }
            }
        }
        return products;
    }
}
