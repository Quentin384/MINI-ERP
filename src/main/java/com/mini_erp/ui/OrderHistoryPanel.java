package com.mini_erp.ui;

import com.mini_erp.dao.CustomerDAO;
import com.mini_erp.dao.OrderDAO;
import com.mini_erp.model.Customer;
import com.mini_erp.model.Order;
import com.mini_erp.model.OrderLine;
import com.mini_erp.model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class OrderHistoryPanel extends JPanel {

    private final ProductPanel productPanel;
    private final OrderDAO orderDAO = new OrderDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();

    private JComboBox<Customer> customerComboBox;
    private JButton loadOrdersButton;

    private DefaultTableModel orderTableModel;
    private JTable ordersTable;

    private DefaultTableModel orderLinesModel;
    private JTable orderLinesTable;

    public OrderHistoryPanel(ProductPanel productPanel) {
        this.productPanel = productPanel;
        setLayout(new BorderLayout());

        // Panel top avec combo client + bouton charger
        JPanel topPanel = new JPanel();

        customerComboBox = new JComboBox<>();
        customerComboBox.setPreferredSize(new Dimension(300, 25));
        topPanel.add(new JLabel("Client : "));
        topPanel.add(customerComboBox);

        loadOrdersButton = new JButton("Charger commandes");
        topPanel.add(loadOrdersButton);

        add(topPanel, BorderLayout.NORTH);

        // Table commandes
        orderTableModel = new DefaultTableModel(new Object[]{"ID", "Date", "Montant total"}, 0);
        ordersTable = new JTable(orderTableModel);

        // Table lignes commandes
        orderLinesModel = new DefaultTableModel(new Object[]{"Produit", "Quantité"}, 0);
        orderLinesTable = new JTable(orderLinesModel);

        // Split pane vertical
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(ordersTable), new JScrollPane(orderLinesTable));
        splitPane.setDividerLocation(150);
        add(splitPane, BorderLayout.CENTER);

        // Actions
        loadOrdersButton.addActionListener(e -> loadOrders());

        ordersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showOrderLines();
            }
        });

        loadCustomersWithOrders();
    }

    private void loadCustomersWithOrders() {
        try {
            // Charger tous les clients ayant au moins une commande
            List<Customer> customers = customerDAO.getCustomersWithOrders();
            DefaultComboBoxModel<Customer> model = new DefaultComboBoxModel<>();
            for (Customer c : customers) {
                model.addElement(c);
            }
            customerComboBox.setModel(model);

            if (!customers.isEmpty()) {
                customerComboBox.setSelectedIndex(0);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur chargement clients : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadOrders() {
        Customer selectedCustomer = (Customer) customerComboBox.getSelectedItem();
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un client.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            List<Order> orders = orderDAO.getOrdersByCustomer(selectedCustomer.getId());
            orderTableModel.setRowCount(0);
            orderLinesModel.setRowCount(0);
            for (Order o : orders) {
                orderTableModel.addRow(new Object[]{
                        o.getId(),
                        o.getOrderDate(),
                        o.getTotalAmount()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur chargement commandes : " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
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

            List<Product> products = productPanel.dao.getProducts(null);

            for (OrderLine line : lines) {
                Product p = products.stream()
                        .filter(prod -> prod.getId() == line.getProductId())
                        .findFirst()
                        .orElse(null);

                String productName = p != null ? p.getName() : "Produit #" + line.getProductId();

                orderLinesModel.addRow(new Object[]{productName, line.getQuantity()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur chargement détails commande : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
