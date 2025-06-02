package com.mini_erp.ui;

import com.mini_erp.dao.CustomerDAO;
import com.mini_erp.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CustomerPanel extends JPanel {
    private JButton loadCustomersButton = new JButton("Charger clients");
    private JTable customersTable = new JTable();
    private DefaultTableModel tableModel;

    public CustomerPanel() {
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"ID", "Nom", "Email", "Téléphone"}, 0);
        customersTable.setModel(tableModel);

        JPanel topPanel = new JPanel();
        topPanel.add(loadCustomersButton);
        JButton addCustomerButton = new JButton("Ajouter client");
        topPanel.add(addCustomerButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(customersTable), BorderLayout.CENTER);

        loadCustomersButton.addActionListener(e -> loadCustomers());
        addCustomerButton.addActionListener(e -> new AddCustomerForm());
    }

    private void loadCustomers() {
        CustomerDAO dao = new CustomerDAO();
        try {
            List<Customer> customers = dao.getAllCustomers();
            tableModel.setRowCount(0);
            for (Customer c : customers) {
                tableModel.addRow(new Object[]{c.getId(), c.getName(), c.getEmail(), c.getPhone()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des clients : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Customer getSelectedCustomer() {
        int row = customersTable.getSelectedRow();
        if (row >= 0) {
            int id = (int) tableModel.getValueAt(row, 0);
            String name = (String) tableModel.getValueAt(row, 1);
            String email = (String) tableModel.getValueAt(row, 2);
            String phone = (String) tableModel.getValueAt(row, 3);
            return new Customer(id, name, email, phone);
        }
        return null;
    }
}
