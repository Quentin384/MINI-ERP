package com.mini_erp.ui;

import com.mini_erp.dao.CustomerDAO;
import com.mini_erp.model.Customer;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CustomerPanel extends JPanel {

    private final JButton loadCustomersButton = new JButton("Charger clients");
    private final JTable customersTable = new JTable();
    private final DefaultTableModel tableModel;

    // Liste des clients chargés
    private List<Customer> loadedCustomers = new ArrayList<>();

    public CustomerPanel() {
        setLayout(new BorderLayout());

        // Définition des colonnes (assure-toi que ça correspond à ta classe Customer)
        tableModel = new DefaultTableModel(new Object[]{
            "ID", "Prénom", "Nom", "Adresse 1", "Adresse 2", "Ville", "État", "Code Postal", "Pays", "Région",
            "Email", "Téléphone", "Type CB", "CB", "Expiration CB", "Username", "Password", "Âge", "Revenu", "Genre"
        }, 0);

        customersTable.setModel(tableModel);
        customersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel topPanel = new JPanel();
        topPanel.add(loadCustomersButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(customersTable), BorderLayout.CENTER);

        loadCustomersButton.addActionListener(e -> loadCustomers());
    }

    private void loadCustomers() {
        CustomerDAO dao = new CustomerDAO();
        try {
            loadedCustomers = dao.getAllCustomers();
            tableModel.setRowCount(0);
            for (Customer c : loadedCustomers) {
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

    /**
     * Renvoie le client actuellement sélectionné dans la table,
     * ou null si aucune sélection.
     */
    public Customer getSelectedCustomer() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= loadedCustomers.size()) {
            return null;
        }
        return loadedCustomers.get(selectedRow);
    }
}
