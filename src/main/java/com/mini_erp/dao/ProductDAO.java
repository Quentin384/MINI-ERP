package com.mini_erp.dao;

import com.mini_erp.db.DatabaseManager;
import com.mini_erp.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> getProducts(Integer categoryFilter) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT prod_id, category, title, actor, price, special, common_prod_id FROM products";
        if (categoryFilter != null) {
            sql += " WHERE category = ?";
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (categoryFilter != null) {
                stmt.setInt(1, categoryFilter);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(new Product(
                            rs.getInt("prod_id"),
                            rs.getInt("category"),
                            rs.getString("title"),
                            rs.getString("actor"),
                            rs.getDouble("price"),
                            rs.getShort("special"),
                            rs.getInt("common_prod_id")
                    ));
                }
            }
        }
        return products;
    }

    public List<Integer> getCategories() throws SQLException {
        List<Integer> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT category FROM products ORDER BY category";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            categories.add(null); // Représente "Tous"
            while (rs.next()) {
                categories.add(rs.getInt("category"));
            }
        }
        return categories;
    }
}
