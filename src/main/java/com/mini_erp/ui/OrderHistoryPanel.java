package com.mini_erp.ui;

import com.mini_erp.dao.OrderDAO;
import com.mini_erp.model.Order;
import com.mini_erp.model.OrderLine;
import com.mini_erp.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderHistoryPanel extends JPanel {
    private final CustomerPanel customerPanel;
    private final ProductPanel productPanel;
    private final OrderDAO orderDAO;

    private final DefaultTableModel orderTableModel;
    private final JTable ordersTable = new JTable();

    private final DefaultTableModel orderLinesModel;
    private final JTable orderLinesTable = new JTable();

    public OrderHistoryPanel(CustomerPanel customerPanel, ProductPanel productPanel) {
        this.customerPanel = customerPanel;
        this.productPanel = productPanel;
        this.orderDAO = new OrderDAO();

        setLayout(new BorderLayout());

        // Modèle et tableau des commandes
        orderTableModel = new DefaultTableModel(new Object[]{"ID", "Date", "Montant total"}, 0);
        ordersTable.setModel(orderTableModel);

        // Modèle et tableau des lignes de commande
        orderLinesModel = new DefaultTableModel(new Object[]{"Produit", "Quantité"}, 0);
        orderLinesTable.setModel(orderLinesModel);

        // SplitPane vertical : commandes au-dessus, lignes en-dessous
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(ordersTable), new JScrollPane(orderLinesTable));
        splitPane.setDividerLocation(150);

        JButton loadOrdersButton = new JButton("Charger commandes");
        JPanel topPanel = new JPanel();
        topPanel.add(loadOrdersButton);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        // Chargement des commandes quand on clique
        loadOrdersButton.addActionListener(e -> loadOrders());

        // Affichage des lignes de commande quand on sélectionne une commande
        ordersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showOrderLines();
            }
        });
    }

    private void loadOrders() {
        var selectedCustomer = customerPanel.getSelectedCustomer();
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un client.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            List<Order> orders = orderDAO.getOrdersByCustomer(selectedCustomer.getId());
            orderTableModel.setRowCount(0);
            orderLinesModel.setRowCount(0);
            for (Order o : orders) {
                orderTableModel.addRow(new Object[]{o.getId(), o.getOrderDate(), o.getTotalAmount()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur chargement commandes : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showOrderLines() {
        int row = ordersTable.getSelectedRow();
        if (row < 0) {
            orderLinesModel.setRowCount(0);
            return;
        }
        int orderId = (int) orderTableModel.getValueAt(row, 0);

        try {
            List<OrderLine> lines = orderDAO.getOrderLines(orderId);
            // Précharger la liste des produits pour éviter plusieurs appels
            List<Product> products = productPanel.dao.getProducts(null);
            Map<Integer, String> productIdToName = products.stream()
                    .collect(Collectors.toMap(Product::getId, Product::getName));

            orderLinesModel.setRowCount(0);
            for (OrderLine line : lines) {
                String productName = productIdToName.getOrDefault(line.getProductId(), "Produit #" + line.getProductId());
                orderLinesModel.addRow(new Object[]{productName, line.getQuantity()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement détails commande : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
