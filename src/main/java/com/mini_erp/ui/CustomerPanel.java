package com.mini_erp.ui;

import com.mini_erp.dao.CustomerDAO;
import com.mini_erp.model.Customer;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CustomerPanel extends JPanel {

    private JButton loadCustomersButton = new JButton("Charger clients");
    private JTable customersTable = new JTable();
    private DefaultTableModel tableModel;

    public CustomerPanel() {
        setLayout(new BorderLayout());

        // Ajout de toutes les colonnes pour correspondre à ta classe Customer
        tableModel = new DefaultTableModel(new Object[]{
            "ID", "Prénom", "Nom", "Adresse 1", "Adresse 2", "Ville", "État", "Code Postal", "Pays", "Région",
            "Email", "Téléphone", "Type CB", "CB", "Expiration CB", "Username", "Password", "Âge", "Revenu", "Genre"
        }, 0);
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
                tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getFirstname(),
                    c.getLastname(),
                    c.getAddress1(),
                    c.getAddress2(),
                    c.getCity(),
                    c.getState(),
                    c.getZip(),
                    c.getCountry(),
                    c.getRegion(),
                    c.getEmail(),
                    c.getPhone(),
                    c.getCreditcardtype(),
                    c.getCreditcard(),
                    c.getCreditcardexpiration(),
                    c.getUsername(),
                    c.getPassword(),
                    c.getAge(),
                    c.getIncome(),
                    c.getGender()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des clients : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
