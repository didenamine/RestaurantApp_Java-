import java.awt.*;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import model.*;
import util.FileHandler;

public class ManagerDashboard extends JFrame {
    private List<Client> clients;
    private final List<Product> menu;
    private List<Order> orders;

    public ManagerDashboard() {
        //dataa        
        clients = FileHandler.loadClients();
        menu = FileHandler.loadMenu();
        orders = FileHandler.loadOrders();


        setTitle("Restaurant Manager Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);


        JTabbedPane tabbedPane = new JTabbedPane();
        //new trick tabs ...
        tabbedPane.addTab("Clients", createClientsPanel());
        tabbedPane.addTab("Menu", createMenuPanel());
        tabbedPane.addTab("Orders", createOrdersPanel());


        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Add logout button at the bottom
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            this.dispose();new RestaurantApp();
        });
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(logoutButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);
    }

    private JPanel createClientsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        

        JList<Client> clientList = new JList<>(new DefaultListModel<>());
        DefaultListModel<Client> listModel = (DefaultListModel<Client>) clientList.getModel();
        clients.forEach(listModel::addElement);
        
        JScrollPane scrollPane = new JScrollPane(clientList);
        panel.add(scrollPane, BorderLayout.CENTER);
        

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Client");
        JButton editButton = new JButton("Edit Client");
        JButton deleteButton = new JButton("Delete Client");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        addButton.addActionListener(e -> {
            showAddClientDialog();
            // Refresh the client list
            listModel.clear();
            clients = FileHandler.loadClients();
            clients.forEach(listModel::addElement);
        });
        
        // Edit button action
        editButton.addActionListener(e -> {
            Client selectedClient = clientList.getSelectedValue();
            if (selectedClient != null) {
                showEditClientDialog(selectedClient);
                // Refresh the client list
                listModel.clear();
                clients = FileHandler.loadClients();
                clients.forEach(listModel::addElement);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a client to edit.");
            }
        });
        
        // Delete button action
        deleteButton.addActionListener(e -> {
            Client selectedClient = clientList.getSelectedValue();
            if (selectedClient != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this client?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    clients.remove(selectedClient);
                    listModel.removeElement(selectedClient);
                    FileHandler.saveClients(clients);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a client to delete.");
            }
        });
        
        return panel;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Menu list
        JList<Product> menuList = new JList<>(new DefaultListModel<>());
        DefaultListModel<Product> listModel = (DefaultListModel<Product>) menuList.getModel();
        menu.forEach(listModel::addElement);
        
        JScrollPane scrollPane = new JScrollPane(menuList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Product");
        JButton editButton = new JButton("Edit Product");
        JButton deleteButton = new JButton("Delete Product");
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add button action
        addButton.addActionListener(e-> showAddProductDialog());
        
        // Edit button action
        editButton.addActionListener(e -> {
            Product selectedProduct = menuList.getSelectedValue();
            if (selectedProduct != null) {
                showEditProductDialog(selectedProduct);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a product to edit.");
            }
        });
        
        // Delete button action
        deleteButton.addActionListener(e -> {
            Product selectedProduct = menuList.getSelectedValue();
            if (selectedProduct != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete this product?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    menu.remove(selectedProduct);
                    listModel.removeElement(selectedProduct);
                    FileHandler.saveMenu(menu);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a product to delete.");
            }
        });
        
        return panel;
    }

    private JPanel createOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // TOtal rev
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel totalRevenueLabel = new JLabel("Total Revenue: $0.00");
        topPanel.add(totalRevenueLabel);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Orders list
        JList<Order> orderList = new JList<>(new DefaultListModel<>());
        DefaultListModel<Order> listModel = (DefaultListModel<Order>) orderList.getModel();
        

        Runnable refreshOrderList = () -> {
            //Fixed to keep it updating 
            listModel.clear();
            orders = FileHandler.loadOrders(); // Reload orders from file
            if (orders != null) {
                        //keeping the last version of each one 
                java.util.Map<Integer, Order> latestOrders = new java.util.HashMap<>();
                orders.forEach(o -> latestOrders.put(o.getOrderId(), o));

                latestOrders.values().forEach(listModel::addElement);
                
                // to calc the revnue if it's only ready .. we can add if it's prepared too but not that logical 
                double totalRevenue = latestOrders.values().stream()
                    .filter(o -> o.getStatus() == Order.OrderStatus.READY).mapToDouble(Order::getTotalAmount).sum();
                totalRevenueLabel.setText(String.format("Total Revenue: $%.2f", totalRevenue));

            }
        };
        //to call the order tab 
        refreshOrderList.run();
        
        // Custom renderer for orders
        orderList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                Order order = (Order) value;
                String text = String.format("Order #%d - Client: %s - Type: %s - Status: %s - Total: $%.2f",
                    order.getOrderId(),
                    order.getClient().getUsername(),
                    order.getType(),
                    order.getStatus(),
                    order.getTotalAmount());
                return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(orderList);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel();
        JButton updateStatusButton = new JButton("Update Status");
        
        buttonPanel.add(updateStatusButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Update status button action
        updateStatusButton.addActionListener(e -> {
            Order selectedOrder = orderList.getSelectedValue();
            if (selectedOrder != null) {
                showUpdateStatusDialog(selectedOrder);
                refreshOrderList.run(); // Refresh after status update
            } else {
                JOptionPane.showMessageDialog(this, "Please select an order to update.");
            }
        });
        
        return panel;
    }

    private void showAddClientDialog() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField firstNameField = new JTextField(20);
        JTextField lastNameField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JTextField dobField = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        panel.add(firstNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        panel.add(lastNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        panel.add(addressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Date of Birth (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        panel.add(dobField, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Client",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Client newClient = new Client(
                        usernameField.getText(),
                        new String(passwordField.getPassword()),
                        firstNameField.getText(),
                        lastNameField.getText(),
                        phoneField.getText(),
                        addressField.getText(),
                        java.time.LocalDate.parse(dobField.getText())
                );

                clients.add(newClient);
                FileHandler.saveClients(clients);
                JOptionPane.showMessageDialog(this, "Client added successfully!");
            } catch (java.time.format.DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format! Please use YYYY-MM-DD");
            }
        }
    }

    private void showEditClientDialog(Client client) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField usernameField = new JTextField(client.getUsername(), 20);
        JPasswordField passwordField = new JPasswordField(client.getPassword(), 20);
        JTextField firstNameField = new JTextField(client.getFirstName(), 20);
        JTextField lastNameField = new JTextField(client.getLastName(), 20);
        JTextField phoneField = new JTextField(client.getPhoneNumber(), 20);
        JTextField addressField = new JTextField(client.getAddress(), 20);
        JTextField dobField = new JTextField(client.getDateOfBirth().toString(), 20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1;
        panel.add(firstNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1;
        panel.add(lastNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        panel.add(addressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Date of Birth (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        panel.add(dobField, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Client",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                // Update all client fields
                client.setUsername(usernameField.getText());
                client.setPassword(new String(passwordField.getPassword()));
                client.setFirstName(firstNameField.getText());
                client.setLastName(lastNameField.getText());
                client.setPhoneNumber(phoneField.getText());
                client.setAddress(addressField.getText());
                client.setDateOfBirth(java.time.LocalDate.parse(dobField.getText()));
                
                FileHandler.saveClients(clients);
                JOptionPane.showMessageDialog(this, "Client updated successfully!");
            } catch (java.time.format.DateTimeParseException ex ) {
                JOptionPane.showMessageDialog(this, "Invalid date format! Please use YYYY-MM-DD");
            }
        }
    }

    private void showAddProductDialog() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(20);
        JTextField descriptionField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JComboBox<Product.ProductType> typeCombo = new JComboBox<>(Product.ProductType.values());
        JTextField imagePathField = new JTextField(20);
        imagePathField.setEditable(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        panel.add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        panel.add(typeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Image:"), gbc);
        gbc.gridx = 1;
        panel.add(imagePathField, gbc);

        JButton browseButton = new JButton("Browse...");
        gbc.gridx = 2;
        panel.add(browseButton, gbc);

        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                @Override
                public boolean accept(java.io.File f) {
                    if (f.isDirectory()) return true;
                    String name = f.getName().toLowerCase();
                    return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                           name.endsWith(".png") || name.endsWith(".gif");
                }
                @Override
                public String getDescription() {
                    return "Image Files (*.jpg, *.jpeg, *.png, *.gif)";
                }
            });

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File selectedFile = fileChooser.getSelectedFile();
                try {

                    java.io.File productsDir = new java.io.File("src/resources/products");
                    if (!productsDir.exists()) {
                        productsDir.mkdirs();
                    }

                    java.io.File destFile = new java.io.File(productsDir, selectedFile.getName());
                    java.nio.file.Files.copy(selectedFile.toPath(), destFile.toPath(), 
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);


                    imagePathField.setText(destFile.getPath());
                } catch (IOException ex) {
                  JOptionPane.showMessageDialog(this, "Error copying image file: " + ex.getMessage());
                }
            }
        });

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Product",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Product newProduct = new Product(
                        nameField.getText(),
                        descriptionField.getText(),
                        Double.parseDouble(priceField.getText()),
                        (Product.ProductType) typeCombo.getSelectedItem(),
                        imagePathField.getText()
                );

                menu.add(newProduct);
                FileHandler.saveMenu(menu);
                
                JTabbedPane tabbedPane = (JTabbedPane) getContentPane().getComponent(0);
                JPanel menuPanel = (JPanel) tabbedPane.getComponentAt(1);
                JScrollPane scrollPane = (JScrollPane) menuPanel.getComponent(0);
                JList<Product> menuList = (JList<Product>) scrollPane.getViewport().getView();
                DefaultListModel<Product> listModel = (DefaultListModel<Product>) menuList.getModel();
                
                // Add the new product to the list model
                listModel.addElement(newProduct);
                
                JOptionPane.showMessageDialog(this, "Product added successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid price!");
            }
        }
    }

    private void showEditProductDialog(Product product) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(product.getName(), 20);
        JTextField descriptionField = new JTextField(product.getDescription(), 20);
        JTextField priceField = new JTextField(String.valueOf(product.getPrice()), 20);
        JComboBox<Product.ProductType> typeCombo = new JComboBox<>(Product.ProductType.values());
        typeCombo.setSelectedItem(product.getType());
        JTextField imagePathField = new JTextField(product.getImagePath(), 20);
        imagePathField.setEditable(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        panel.add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        panel.add(typeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Image:"), gbc);
        gbc.gridx = 1;
        panel.add(imagePathField, gbc);

        JButton browseButton = new JButton("Browse...");
        gbc.gridx = 2;
        panel.add(browseButton, gbc);

        browseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                @Override
                public boolean accept(java.io.File f) {
                    if (f.isDirectory()) return true;
                    String name = f.getName().toLowerCase();
                    return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                           name.endsWith(".png") || name.endsWith(".gif");
                }
                @Override
                public String getDescription() {
                    return "Image Files (*.jpg, *.jpeg, *.png, *.gif)";
                }
            });

            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                java.io.File selectedFile = fileChooser.getSelectedFile();
                try {
                    // Create products directory if it doesn't exist
                    java.io.File productsDir = new java.io.File("src/resources/products");
                    if (!productsDir.exists()) {
                        productsDir.mkdirs();
                    }

                    // Copy the selected file to the products directory
                    java.io.File destFile = new java.io.File(productsDir, selectedFile.getName());
                    java.nio.file.Files.copy(selectedFile.toPath(), destFile.toPath(), 
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                    // Update the image path field
                    imagePathField.setText(destFile.getPath());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error copying image file: " + ex.getMessage());
                }
            }
        });

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Product",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                product.setName(nameField.getText());
                product.setDescription(descriptionField.getText());
                product.setPrice(Double.parseDouble(priceField.getText()));
                product.setType((Product.ProductType) typeCombo.getSelectedItem());
                product.setImagePath(imagePathField.getText());
                FileHandler.saveMenu(menu);
                JOptionPane.showMessageDialog(this, "Product updated successfully!");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid price!");
            }
        }
    }

    private void showUpdateStatusDialog(Order order) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<Order.OrderStatus> statusCombo = new JComboBox<>(Order.OrderStatus.values());
        statusCombo.setSelectedItem(order.getStatus());

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        panel.add(statusCombo, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update Order Status",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            // Update the order status
            Order.OrderStatus newStatus = (Order.OrderStatus) statusCombo.getSelectedItem();
            order.setStatus(newStatus);
            
            // Save the updated orders list
            FileHandler.saveOrders(orders);
            
            // Refresh the orders list
            JTabbedPane tabbedPane = (JTabbedPane) getContentPane().getComponent(0);
            JPanel ordersPanel = (JPanel) tabbedPane.getComponentAt(2);
            JScrollPane scrollPane = (JScrollPane) ordersPanel.getComponent(0);
            JList<Order> orderList = (JList<Order>) scrollPane.getViewport().getView();
            DefaultListModel<Order> listModel = (DefaultListModel<Order>) orderList.getModel();
            
            // Update the list model
            listModel.clear();
            orders.forEach(listModel::addElement);
            
            JOptionPane.showMessageDialog(this, "Order status updated successfully!");
        }
    }
} 