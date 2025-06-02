package com.mini_erp.dao;

import com.mini_erp.db.DatabaseManager;
import com.mini_erp.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> getProducts(String categoryFilter) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, name, price, category FROM products";
        if (categoryFilter != null && !categoryFilter.isEmpty() && !categoryFilter.equals("Tous")) {
            sql += " WHERE category = ?";
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (categoryFilter != null && !categoryFilter.isEmpty() && !categoryFilter.equals("Tous")) {
                stmt.setString(1, categoryFilter);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getString("category")
                    ));
                }
            }
        }
        return products;
    }

    public List<String> getCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM products";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            categories.add("Tous");
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        }
        return categories;
    }
}
