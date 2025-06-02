package com.mini_erp.ui;

import com.mini_erp.dao.OrderDAO;
import com.mini_erp.model.Order;
import com.mini_erp.model.OrderLine;
import com.mini_erp.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderPanel extends JPanel {
    private final CustomerPanel customerPanel;
    private final ProductPanel productPanel;

    private final DefaultTableModel orderLinesModel;
    private final JTable orderLinesTable = new JTable();

    private final JButton addProductButton = new JButton("Ajouter produit");
    private final JButton submitOrderButton = new JButton("Valider commande");

    private final List<OrderLine> orderLines = new ArrayList<>();

    public OrderPanel(CustomerPanel customerPanel, ProductPanel productPanel) {
        this.customerPanel = customerPanel;
        this.productPanel = productPanel;

        setLayout(new BorderLayout());

        orderLinesModel = new DefaultTableModel(new Object[]{"Produit", "Quantité"}, 0);
        orderLinesTable.setModel(orderLinesModel);

        JPanel topPanel = new JPanel();
        topPanel.add(addProductButton);
        topPanel.add(submitOrderButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(orderLinesTable), BorderLayout.CENTER);

        addProductButton.addActionListener(e -> addProductToOrder());
        submitOrderButton.addActionListener(e -> submitOrder());
    }

    private void addProductToOrder() {
        Product selectedProduct = productPanel.getSelectedProduct();
        if (selectedProduct == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un produit.", "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String qtyStr = JOptionPane.showInputDialog(this, "Quantité :");
        if (qtyStr == null) return;

        int qty;
        try {
            qty = Integer.parseInt(qtyStr);
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantité invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Mise à jour de la quantité en évitant modification pendant l'itération
        boolean found = false;
        for (int i = 0; i < orderLines.size(); i++) {
            OrderLine ol = orderLines.get(i);
            if (ol.getProductId() == selectedProduct.getId()) {
                orderLines.set(i, new OrderLine(ol.getProductId(), ol.getQuantity() + qty));
                found = true;
                break;
            }
        }
        if (!found) {
            orderLines.add(new OrderLine(selectedProduct.getId(), qty));
        }
        refreshOrderLines();
    }

    private void refreshOrderLines() {
        orderLinesModel.setRowCount(0);
        for (OrderLine ol : orderLines) {
            Product p = null;
            try {
                p = productPanel.dao.getProducts(null).stream()
                        .filter(prod -> prod.getId() == ol.getProductId())
                        .findFirst()
                        .orElse(null);
            } catch (SQLException e) {
                // Impossible de récupérer le produit, affichage par défaut
                // Il serait intéressant de logger l'erreur ici
            }
            orderLinesModel.addRow(new Object[]{p != null ? p.getName() : "Produit #" + ol.getProductId(), ol.getQuantity()});
        }
    }

    private void submitOrder() {
        if (orderLines.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucun produit dans la commande.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        var selectedCustomer = customerPanel.getSelectedCustomer();
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Order order = new Order(selectedCustomer.getId(), orderLines);
        OrderDAO orderDAO = new OrderDAO();

        try {
            orderDAO.createOrder(order);
            JOptionPane.showMessageDialog(this, "Commande enregistrée avec succès.");
            orderLines.clear();
            refreshOrderLines();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la création de la commande : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
