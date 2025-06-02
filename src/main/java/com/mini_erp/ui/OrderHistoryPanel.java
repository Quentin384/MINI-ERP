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

public class OrderHistoryPanel extends JPanel {
    private CustomerPanel customerPanel;
    private DefaultTableModel orderTableModel;
    private JTable ordersTable = new JTable();

    private DefaultTableModel orderLinesModel;
    private JTable orderLinesTable = new JTable();

    private OrderDAO orderDAO = new OrderDAO();
    private ProductPanel productPanel;

    public OrderHistoryPanel(CustomerPanel customerPanel, ProductPanel productPanel) {
        this.customerPanel = customerPanel;
        this.productPanel = productPanel;

        setLayout(new BorderLayout());

        orderTableModel = new DefaultTableModel(new Object[]{"ID", "Date", "Montant total"}, 0);
        ordersTable.setModel(orderTableModel);

        orderLinesModel = new DefaultTableModel(new Object[]{"Produit", "Quantité"}, 0);
        orderLinesTable.setModel(orderLinesModel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(ordersTable), new JScrollPane(orderLinesTable));
        splitPane.setDividerLocation(150);

        JButton loadOrdersButton = new JButton("Charger commandes");
        JPanel topPanel = new JPanel();
        topPanel.add(loadOrdersButton);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        loadOrdersButton.addActionListener(e -> loadOrders());

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
            orderLinesModel.setRowCount(0);
            for (OrderLine line : lines) {
                Product p = productPanel.dao.getProducts(null).stream()
                        .filter(prod -> prod.getId() == line.getProductId())
                        .findFirst().orElse(null);
                String productName = p != null ? p.getName() : "Produit #" + line.getProductId();
                orderLinesModel.addRow(new Object[]{productName, line.getQuantity()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur chargement détails commande : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
