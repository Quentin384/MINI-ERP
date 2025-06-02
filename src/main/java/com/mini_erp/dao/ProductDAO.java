package com.mini_erp.dao;

import com.mini_erp.db.DatabaseManager;
import com.mini_erp.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public List<Product> getProducts(Integer category) throws SQLException {
        String sql = "SELECT * FROM products";
        if (category != null) {
            sql += " WHERE category = ?";
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (category != null) {
                ps.setInt(1, category);
            }

            try (ResultSet rs = ps.executeQuery()) {
                List<Product> products = new ArrayList<>();
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
                return products;
            }
        }
    }

    public List<Integer> getCategories() throws SQLException {
        String sql = "SELECT DISTINCT category FROM products ORDER BY category";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Integer> categories = new ArrayList<>();
            while (rs.next()) {
                categories.add(rs.getInt("category"));
            }
            return categories;
        }
    }
}
