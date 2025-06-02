package com.mini_erp.dao;

import com.mini_erp.db.DatabaseManager;
import com.mini_erp.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(new Customer(
                    rs.getInt("customerid"),
                    rs.getString("firstname"),
                    rs.getString("lastname"),
                    rs.getString("address1"),
                    rs.getString("address2"),
                    rs.getString("city"),
                    rs.getString("state"),
                    rs.getInt("zip"),
                    rs.getString("country"),
                    rs.getShort("region"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("creditcardtype"),          // String ici
                    rs.getString("creditcard"),
                    rs.getString("creditcardexpiration"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getShort("age"),
                    rs.getInt("income"),
                    rs.getString("gender")
                ));
            }
        }

        return customers;
    }

    public void addCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (firstname, lastname, address1, address2, city, state, zip, country, region, email, phone, creditcardtype, creditcard, creditcardexpiration, username, password, age, income, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customer.getFirstname());
            stmt.setString(2, customer.getLastname());
            stmt.setString(3, customer.getAddress1());
            stmt.setString(4, customer.getAddress2());
            stmt.setString(5, customer.getCity());
            stmt.setString(6, customer.getState());
            stmt.setInt(7, customer.getZip());
            stmt.setString(8, customer.getCountry());
            stmt.setShort(9, customer.getRegion());
            stmt.setString(10, customer.getEmail());
            stmt.setString(11, customer.getPhone());
            stmt.setString(12, customer.getCreditcardtype());   // String ici
            stmt.setString(13, customer.getCreditcard());
            stmt.setString(14, customer.getCreditcardexpiration());
            stmt.setString(15, customer.getUsername());
            stmt.setString(16, customer.getPassword());
            stmt.setShort(17, customer.getAge());
            stmt.setInt(18, customer.getIncome());
            stmt.setString(19, customer.getGender());

            stmt.executeUpdate();
        }
    }
}
