package com.mini_erp.ui;

import com.mini_erp.dao.CustomerDAO;
import com.mini_erp.model.Customer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class AddCustomerForm extends JFrame {
    private JTextField nameField = new JTextField(20);
    private JTextField emailField = new JTextField(20);
    private JTextField phoneField = new JTextField(20);
    private JButton submitButton = new JButton("Ajouter");

    public AddCustomerForm() {
        setTitle("Ajouter un client");
        setLayout(new GridLayout(4, 2, 5, 5));
        setSize(300, 200);
        setLocationRelativeTo(null);

        add(new JLabel("Nom :"));
        add(nameField);
        add(new JLabel("Email :"));
        add(emailField);
        add(new JLabel("Téléphone :"));
        add(phoneField);
        add(new JLabel());
        add(submitButton);

        submitButton.addActionListener(this::onSubmit);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void onSubmit(ActionEvent e) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nom et Email sont obligatoires.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Customer customer = new Customer(name, email, phone);
        CustomerDAO dao = new CustomerDAO();

        try {
            dao.addCustomer(customer);
            JOptionPane.showMessageDialog(this, "Client ajouté avec succès !");
            dispose();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
