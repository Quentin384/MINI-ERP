package com.mini_erp.ui;

import com.mini_erp.dao.CustomerDAO;
import com.mini_erp.dao.OrderDAO;
import com.mini_erp.dao.ProductDAO;
import com.mini_erp.model.Customer;
import com.mini_erp.model.Order;
import com.mini_erp.model.OrderLine;
import com.mini_erp.model.Product;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

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

        initTopPanel();
        initTables();
        initActions();

        loadCustomersWithOrders();
    }

    private void initTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        customerComboBox = new JComboBox<>();
        customerComboBox.setPreferredSize(new Dimension(300, 25));
        topPanel.add(new JLabel("Client : "));
        topPanel.add(customerComboBox);

        loadOrdersButton = new JButton("Charger commandes");
        topPanel.add(loadOrdersButton);

        add(topPanel, BorderLayout.NORTH);
    }

    private void initTables() {
        // Table des commandes
        orderTableModel = new DefaultTableModel(new Object[]{"ID", "Date", "Montant total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // désactive l'édition directe
            }
        };
        ordersTable = new JTable(orderTableModel);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Table des lignes de commande
        orderLinesModel = new DefaultTableModel(new Object[]{"Produit", "Quantité"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderLinesTable = new JTable(orderLinesModel);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(ordersTable), new JScrollPane(orderLinesTable));
        splitPane.setDividerLocation(150);

        add(splitPane, BorderLayout.CENTER);
    }

    private void initActions() {
        loadOrdersButton.addActionListener(e -> loadOrders());

        ordersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showOrderLines();
            }
        });
    }

    private void loadCustomersWithOrders() {
        try {
            List<Customer> customers = customerDAO.getCustomersWithOrders();
            DefaultComboBoxModel<Customer> model = new DefaultComboBoxModel<>();
            customers.forEach(model::addElement);
            customerComboBox.setModel(model);

            if (!customers.isEmpty()) {
                customerComboBox.setSelectedIndex(0);
            }
        } catch (SQLException e) {
            showError("Erreur chargement clients : " + e.getMessage());
        }
    }

    private void loadOrders() {
        Customer selectedCustomer = (Customer) customerComboBox.getSelectedItem();
        if (selectedCustomer == null) {
            showError("Veuillez sélectionner un client.");
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
        } catch (SQLException e) {
            showError("Erreur chargement commandes : " + e.getMessage());
        }
    }

    private void showOrderLines() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow < 0) {
            orderLinesModel.setRowCount(0);
            return;
        }

        int orderId = (int) orderTableModel.getValueAt(selectedRow, 0);

        try {
            List<OrderLine> lines = orderDAO.getOrderLines(orderId);
            orderLinesModel.setRowCount(0);

            // On récupère les produits via ProductDAO (pas accès direct au dao privé)
            ProductDAO productDAO = new ProductDAO();
            List<Product> products = productDAO.getProducts(null);

            for (OrderLine line : lines) {
                Product product = products.stream()
                        .filter(p -> p.getProdId() == line.getProductId())
                        .findFirst()
                        .orElse(null);

                String productName = product != null ? product.getTitle() : "Produit #" + line.getProductId();

                orderLinesModel.addRow(new Object[]{productName, line.getQuantity()});
            }
        } catch (SQLException e) {
            showError("Erreur chargement détails commande : " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
