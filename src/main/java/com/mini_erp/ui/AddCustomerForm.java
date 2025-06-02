package com.mini_erp.ui;

import com.mini_erp.dao.CustomerDAO;
import com.mini_erp.model.Customer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import javax.swing.*;

public class AddCustomerForm extends JFrame {

    private JTextField firstnameField = new JTextField(15);
    private JTextField lastnameField = new JTextField(15);
    private JTextField address1Field = new JTextField(15);
    private JTextField address2Field = new JTextField(15);
    private JTextField cityField = new JTextField(15);
    private JTextField stateField = new JTextField(15);
    private JTextField zipField = new JTextField(15);
    private JTextField countryField = new JTextField(15);
    private JTextField regionField = new JTextField(15);
    private JTextField emailField = new JTextField(15);
    private JTextField phoneField = new JTextField(15);
    private JTextField creditcardtypeField = new JTextField(15);
    private JTextField creditcardField = new JTextField(15);
    private JTextField creditcardexpirationField = new JTextField(15);
    private JTextField usernameField = new JTextField(15);
    private JTextField passwordField = new JTextField(15);
    private JTextField ageField = new JTextField(15);
    private JTextField incomeField = new JTextField(15);
    private JTextField genderField = new JTextField(15);

    private JButton submitButton = new JButton("Ajouter");

    public AddCustomerForm() {
        setTitle("Ajouter un client");
        setLayout(new GridLayout(21, 2, 5, 5));
        setSize(420, 640);
        setLocationRelativeTo(null);

        add(new JLabel("Prénom :")); add(firstnameField);
        add(new JLabel("Nom :")); add(lastnameField);
        add(new JLabel("Adresse 1 :")); add(address1Field);
        add(new JLabel("Adresse 2 :")); add(address2Field);
        add(new JLabel("Ville :")); add(cityField);
        add(new JLabel("État :")); add(stateField);
        add(new JLabel("Code postal (int) :")); add(zipField);
        add(new JLabel("Pays :")); add(countryField);
        add(new JLabel("Région (smallint) :")); add(regionField);
        add(new JLabel("Email :")); add(emailField);
        add(new JLabel("Téléphone :")); add(phoneField);
        add(new JLabel("Type de CB (int) :")); add(creditcardtypeField);
        add(new JLabel("Carte de crédit :")); add(creditcardField);
        add(new JLabel("Expiration CB :")); add(creditcardexpirationField);
        add(new JLabel("Nom utilisateur :")); add(usernameField);
        add(new JLabel("Mot de passe :")); add(passwordField);
        add(new JLabel("Âge (smallint) :")); add(ageField);
        add(new JLabel("Revenu (int) :")); add(incomeField);
        add(new JLabel("Genre :")); add(genderField);

        add(new JLabel("")); add(submitButton);

        submitButton.addActionListener(this::handleSubmit);

        setVisible(true);
    }

    private void handleSubmit(ActionEvent e) {
        try {
            int zip = Integer.parseInt(zipField.getText().trim());
            short region = Short.parseShort(regionField.getText().trim());
            int creditcardtype = Integer.parseInt(creditcardtypeField.getText().trim());
            short age = Short.parseShort(ageField.getText().trim());
            int income = Integer.parseInt(incomeField.getText().trim());

            Customer customer = new Customer(
                    0,
                    firstnameField.getText().trim(),
                    lastnameField.getText().trim(),
                    address1Field.getText().trim(),
                    address2Field.getText().trim(),
                    cityField.getText().trim(),
                    stateField.getText().trim(),
                    zip,
                    countryField.getText().trim(),
                    region,
                    emailField.getText().trim(),
                    phoneField.getText().trim(),
                    creditcardtype,
                    creditcardField.getText().trim(),
                    creditcardexpirationField.getText().trim(),
                    usernameField.getText().trim(),
                    passwordField.getText().trim(),
                    age,
                    income,
                    genderField.getText().trim()
            );

            CustomerDAO dao = new CustomerDAO();
            dao.addCustomer(customer);

            JOptionPane.showMessageDialog(this, "Client ajouté avec succès.");
            this.dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur : certains champs numériques (zip, région, type CB, âge, revenu) sont invalides.",
                    "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'ajout du client : " + ex.getMessage(),
                    "Erreur base de données", JOptionPane.ERROR_MESSAGE);
        }
    }
}
