package com.mini_erp.dao;

import com.mini_erp.db.DatabaseManager;
import com.mini_erp.model.Order;
import com.mini_erp.model.OrderLine;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    private static final double TAX_RATE = 0.2;

    public void createOrder(Order order) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            double netAmount = 0;
            for (OrderLine line : order.getLines()) {
                double price = getProductPrice(conn, line.getProductId());
                netAmount += price * line.getQuantity();
            }
            double tax = netAmount * TAX_RATE;
            double totalAmount = netAmount + tax;

            String insertOrderSQL = "INSERT INTO orders (customerid, orderdate, netamount, tax, totalamount) VALUES (?, ?, ?, ?, ?) RETURNING orderid";
            int orderId;

            try (PreparedStatement ps = conn.prepareStatement(insertOrderSQL)) {
                ps.setInt(1, order.getCustomerId());
                ps.setDate(2, Date.valueOf(LocalDate.now()));
                ps.setDouble(3, netAmount);
                ps.setDouble(4, tax);
                ps.setDouble(5, totalAmount);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        orderId = rs.getInt("orderid");
                    } else {
                        throw new SQLException("Erreur lors de la création de la commande, aucun ID retourné.");
                    }
                }
            }

            String insertLineSQL = "INSERT INTO orderlines (order_id, product_id, quantity) VALUES (?, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(insertLineSQL)) {
                for (OrderLine line : order.getLines()) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, line.getProductId());
                    ps.setInt(3, line.getQuantity());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // Mise à jour du stock
            for (OrderLine line : order.getLines()) {
                updateInventory(conn, line.getProductId(), line.getQuantity());
            }

            conn.commit();
        } catch (SQLException ex) {
            if (conn != null) conn.rollback();
            throw ex;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    private double getProductPrice(Connection conn, int productId) throws SQLException {
        String sql = "SELECT price FROM products WHERE prod_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("price");
                } else {
                    throw new SQLException("Produit non trouvé: " + productId);
                }
            }
        }
    }

    private void updateInventory(Connection conn, int productId, int quantityOrdered) throws SQLException {
        String updateSQL = "UPDATE inventory SET quan_in_stock = quan_in_stock - ? WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(updateSQL)) {
            ps.setInt(1, quantityOrdered);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }

    public List<Order> getOrdersByCustomer(int customerId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT orderid, orderdate, netamount, tax, totalamount FROM orders WHERE customerid = ? ORDER BY orderdate DESC";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(new Order(
                            rs.getInt("orderid"),
                            customerId,
                            rs.getDate("orderdate").toLocalDate(),
                            rs.getDouble("netamount"),
                            rs.getDouble("tax"),
                            rs.getDouble("totalamount"),
                            null // Les lignes peuvent être chargées séparément
                    ));
                }
            }
        }
        return orders;
    }

    public List<OrderLine> getOrderLines(int orderId) throws SQLException {
        List<OrderLine> lines = new ArrayList<>();
        String sql = "SELECT product_id, quantity FROM orderlines WHERE order_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lines.add(new OrderLine(
                            rs.getInt("product_id"),
                            rs.getInt("quantity")
                    ));
                }
            }
        }
        return lines;
    }
}
