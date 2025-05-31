import java.awt.*;
import java.io.Serializable;
import java.util.List;
import javax.swing.*;
import model.*;
import util.FileHandler;

@SuppressWarnings("unused")
public class RestaurantApp extends JFrame implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<Client> clients;
    private final List<Admin> admins;
    private final List<Product> menu;
    private  List<Order> orders;
    private User currentUser;


    public RestaurantApp() {
        // data from the txt files  
        clients = FileHandler.loadClients();
        admins = FileHandler.loadAdmins();
        menu = FileHandler.loadMenu();
        orders = FileHandler.loadOrders();


        setTitle("Restaurant Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        showLoginWindow();
    }


    @SuppressWarnings("unused")
    private void showLoginWindow() {

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400); 
        

        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                  
                    Image img = new ImageIcon("RestaurantApp_Java-\\src\\resources\\restaurant_logo.png").getImage();
                    double scale = Math.min((double) getWidth() / img.getWidth(null),(double) getHeight() / img.getHeight(null));
                    int newWidth = (int) (img.getWidth(null) * scale);
                    int newHeight = (int) (img.getHeight(null) * scale);
                    g.drawImage(img, (getWidth() - newWidth) / 2, (getHeight() - newHeight) / 2, newWidth, newHeight, null);
                } catch (Exception e) {
                  
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    int w = getWidth();
                    int h = getHeight();
                    Color color1 = new Color(66, 139, 202);
                    Color color2 = new Color(255, 255, 255);
                    GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, w, h);
                }
            }
        };
        imagePanel.setPreferredSize(new Dimension(400, 600));
        
        // Login form
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        loginPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Welcome Back!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginPanel.add(titleLabel);
        loginPanel.add(Box.createVerticalStrut(30));
        // Username 
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField usernameField = createStyledTextField("Username");
        loginPanel.add(usernameLabel);
        loginPanel.add(Box.createVerticalStrut(5));
        loginPanel.add(usernameField);
        loginPanel.add(Box.createVerticalStrut(10));
        // Password 
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPasswordField passwordField = createStyledPasswordField("Password");
        loginPanel.add(passwordLabel);
        loginPanel.add(Box.createVerticalStrut(5));
        loginPanel.add(passwordField);
        loginPanel.add(Box.createVerticalStrut(10));
        // Buttons 
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        // Login button
        JButton loginButton = createStyledButton("Login", new Color(46, 139, 87));
        loginButton.setMaximumSize(new Dimension(200, 40));
        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        // Register button
        JButton registerButton = createStyledButton("Register", new Color(70, 130, 180));
        registerButton.setMaximumSize(new Dimension(200, 40));
        buttonPanel.add(registerButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        // Admin login 
        JButton adminLoginButton = createStyledButton("Admin Login", new Color(128, 0, 128));
        adminLoginButton.setMaximumSize(new Dimension(200, 40));
        buttonPanel.add(adminLoginButton);
        loginPanel.add(buttonPanel);
        //action listeners
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            Client client = findClient(username);
            if (client != null && client.getPassword().equals(password)) {
                currentUser = client;
                showClientDashboard();
            } 
            else {
                showError("Invalid username or password!");
            }
        });

        adminLoginButton.addActionListener(e -> {
            String username = usernameField.getText();  
            String password = new String(passwordField.getPassword());
            Admin admin = findAdmin(username);
            if (admin != null && admin.getPassword().equals(password)) {
                currentUser = admin;
                showManagerDashboard();
            } else {
                showError("Invalid admin credentials!");
            }
        });

        registerButton.addActionListener(e -> showRegistrationWindow());

        // Added panels to split pane
        splitPane.setLeftComponent(imagePanel);
        splitPane.setRightComponent(loginPanel);

        setContentPane(splitPane);
        setVisible(true);
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(20);
        field.setMaximumSize(new Dimension(300, 40));
        field.setPreferredSize(new Dimension(300, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.putClientProperty("JTextField.placeholderText", placeholder);
        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(20);
        field.setMaximumSize(new Dimension(300, 40));
        field.setPreferredSize(new Dimension(300, 40));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.putClientProperty("JTextField.placeholderText", placeholder);
        return field;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", 
            JOptionPane.ERROR_MESSAGE);
    }

    
     
    private Client findClient(String username) {
        if (username == null || clients == null) {
            return null;
        }
        return clients.stream().filter(c -> c != null && username.equals(c.getUsername())).findFirst().orElse(null);
    }

    
    private Admin findAdmin(String username) {
        if (username == null || admins == null) {
            return null;
        }
        return admins.stream().filter(a -> a != null && username.equals(a.getUsername())).findFirst().orElse(null);
    }

    //registration frame ..
    private void showRegistrationWindow() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));

        // Form fields
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField usernameField = createStyledTextField("Username");

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPasswordField passwordField = createStyledPasswordField("Password");

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        confirmPasswordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPasswordField confirmPasswordField = createStyledPasswordField("Confirm Password");

        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        firstNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField firstNameField = createStyledTextField("First Name");

        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lastNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField lastNameField = createStyledTextField("Last Name");

        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        phoneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField phoneField = createStyledTextField("Phone");

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField addressField = createStyledTextField("Address");

        JLabel dobLabel = new JLabel("Date of Birth (YYYY-MM-DD):");
        dobLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dobLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField dobField = createStyledTextField("Date of Birth (YYYY-MM-DD)");

        JLabel adminKeyLabel = new JLabel("Admin Key (leave empty for client):");
        adminKeyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        adminKeyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField adminKeyField = createStyledTextField("Admin Key (leave empty for client)");

        panel.add(usernameLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(10));

        panel.add(passwordLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(10));

        panel.add(confirmPasswordLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(confirmPasswordField);
        panel.add(Box.createVerticalStrut(10));

        panel.add(firstNameLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(firstNameField);
        panel.add(Box.createVerticalStrut(10));

        panel.add(lastNameLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(lastNameField);
        panel.add(Box.createVerticalStrut(10));

        panel.add(phoneLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(phoneField);
        panel.add(Box.createVerticalStrut(10));

        panel.add(addressLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(addressField);
        panel.add(Box.createVerticalStrut(10));

        panel.add(dobLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(dobField);
        panel.add(Box.createVerticalStrut(10));

        panel.add(adminKeyLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(adminKeyField);
        panel.add(Box.createVerticalStrut(20));

        /* Register button
        JButton registerButton = createStyledButton("Register", new Color(70, 130, 180));
        registerButton.setMaximumSize(new Dimension(200, 40));
        panel.add(registerButton);
        */
        // Scroller
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(400, 600));

        int result = JOptionPane.showConfirmDialog(this, scrollPane, "Register New User",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Validate input fields
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            String dob = dobField.getText().trim();
            String adminKey = adminKeyField.getText().trim();

            // validity of the input heree
            if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || 
                lastName.isEmpty() || phone.isEmpty() || address.isEmpty() || dob.isEmpty()) {
                showError("All fields are required!");
                return;
            }

            // if the user name exisits .. 
            if (findClient(username) != null || findAdmin(username) != null) {
                showError("Username already exists!");
                return;
            }


            if (!password.equals(confirmPassword)) {
                showError("Passwords do not match!");
                return;
            }

            if (password.length() < 6) {
                showError("Password must be at least 6 characters long!");
                return;
            }

            try {
                if (!adminKey.isEmpty()) {
                    if (Admin.isValidAdminKey(adminKey)) {
                        Admin newAdmin = new Admin(username, password, firstName, lastName, phone);
                        admins.add(newAdmin);
                        FileHandler.saveAdmins(admins);
                        JOptionPane.showMessageDialog(this, "Admin registration successful!");
                    } else {
                        showError("Invalid admin key!");
                    }
                } else {
                    Client newClient = new Client(
                            username,
                            password,
                            firstName,
                            lastName,
                            phone,
                            address,
                            java.time.LocalDate.parse(dob)
                    );
                    clients.add(newClient);
                    FileHandler.saveClients(clients);
                    JOptionPane.showMessageDialog(this, "Client registration successful!");
                }
            } catch (java.time.format.DateTimeParseException ex) {
                showError("Invalid date format! Please use YYYY-MM-DD");
            }
        }
    }

    @SuppressWarnings("unused")
    private void showClientDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout());
        
        // Panel bel grid layout 5aliha bel taill haka tbdlhach taw nitsarf fiha 
        JPanel productsPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        productsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        // Prodcuts cards 
        for (Product product : menu) {
            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            card.setBackground(Color.WHITE.darker());
            
            // fazet el image ....(jdida l9dima t3ml bugs 5ali athi for the moment it works )
            JLabel imageLabel = new JLabel();
            if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
                try {
                    ImageIcon originalIcon = new ImageIcon(product.getImagePath());
                    Image scaledImage = originalIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaledImage));
                } catch (Exception e) {
                    imageLabel.setText("No Image");
                    imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                }
            } else {
                imageLabel.setText("No Image");
                imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            }
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(imageLabel);
            card.add(Box.createVerticalStrut(10));
            
            // product name
            JLabel nameLabel = new JLabel(product.getName());
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(nameLabel);
            card.add(Box.createVerticalStrut(5));
            
            // price
            JLabel priceLabel = new JLabel(String.format("$%.2f", product.getPrice()));
            priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            priceLabel.setForeground(new Color(0, 120, 0));
            priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(priceLabel);
            card.add(Box.createVerticalStrut(5));
            
            //  description of the order card 
            JLabel descLabel = new JLabel("<html><body style='text-align:center'>" + product.getDescription() + "</body></html>");
            descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(descLabel);
            card.add(Box.createVerticalStrut(10));
            
            //order button
            JButton orderButton = new JButton("Place Order");
            orderButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            orderButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            orderButton.setBackground(new Color(0, 120, 0));
            orderButton.setForeground(Color.WHITE);
            orderButton.setFocusPainted(false);
            orderButton.setBorderPainted(false);
            orderButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            orderButton.addActionListener(e -> showPlaceOrderDialog(product));
            card.add(orderButton);
            
            productsPanel.add(card);
        }
        
        // Add scroll pane for products
        JScrollPane productsScrollPane = new JScrollPane(productsPanel);
        productsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        productsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dashboard.add(productsScrollPane, BorderLayout.CENTER);
        
        // creation of the order panel 
        JPanel ordersPanel = new JPanel();
        ordersPanel.setLayout(new BoxLayout(ordersPanel, BoxLayout.Y_AXIS));
        ordersPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // title order
        JLabel ordersTitle = new JLabel("Your Orders");
        ordersTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        ordersTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        ordersPanel.add(ordersTitle);
        ordersPanel.add(Box.createVerticalStrut(10));
        
        // orders list 
        JPanel ordersList = new JPanel();
        ordersList.setLayout(new BoxLayout(ordersList, BoxLayout.Y_AXIS));
        ordersList.setBackground(Color.WHITE);
        
        // Add orders
        for (Order order : orders) {
            if (order.getClient().getUsername().equals(currentUser.getUsername())) {
                JPanel orderCard = new JPanel();
                orderCard.setLayout(new BoxLayout(orderCard, BoxLayout.Y_AXIS));
                orderCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(getStatusColor(order.getStatus()), 2),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                orderCard.setBackground(Color.WHITE);
                
                // Add order ID and status
                JLabel orderIdLabel = new JLabel(String.format("Order #%d - %s", 
                    order.getOrderId(), order.getStatus()));
                orderIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                orderCard.add(orderIdLabel);
                orderCard.add(Box.createVerticalStrut(5));
                
                // Add type and total
                JLabel typeLabel = new JLabel(String.format("Type: %s - Total: $%.2f", 
                    order.getType(), order.getTotalAmount()));
                typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                orderCard.add(typeLabel);
                orderCard.add(Box.createVerticalStrut(5));
                
                // Add items
                JLabel itemsLabel = new JLabel("Items: " + String.join(", ", 
                    order.getItems().stream()
                        .map(item -> item.getProduct().getName())
                        .toArray(String[]::new)));
                itemsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                orderCard.add(itemsLabel);
                
                // Add cancel button if order is pending
                if (order.getStatus() == Order.OrderStatus.PENDING) {
                    JButton cancelButton = new JButton("Cancel Order");
                    cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    cancelButton.setBackground(new Color(220, 53, 69));
                    cancelButton.setForeground(Color.WHITE);
                    cancelButton.setFocusPainted(false);
                    cancelButton.setBorderPainted(false);
                    cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    
                    cancelButton.addActionListener(e -> {
                        order.setStatus(Order.OrderStatus.CANCELLED);
                        FileHandler.saveOrders(orders);
                        refreshOrdersPanel();
                    });
                    
                    orderCard.add(Box.createVerticalStrut(10));
                    orderCard.add(cancelButton);
                }
                
                // Add delete button if order is not ready or delivered
                if (order.getStatus() != Order.OrderStatus.READY && order.getStatus() != Order.OrderStatus.DELIVERED) {
                    JButton deleteButton = new JButton("Delete Order");
                    deleteButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    deleteButton.setBackground(new Color(220, 53, 69));
                    deleteButton.setForeground(Color.WHITE);
                    deleteButton.setFocusPainted(false);
                    deleteButton.setBorderPainted(false);
                    deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    
                    deleteButton.addActionListener(e -> {
                        int confirm = JOptionPane.showConfirmDialog(
                            this,
                            "Are you sure you want to delete this order?",
                            "Confirm Deletion",
                            JOptionPane.YES_NO_OPTION
                        );
                        
                        if (confirm == JOptionPane.YES_OPTION) {
                            orders.remove(order);
                            FileHandler.saveOrders(orders);
                            refreshOrdersPanel();
                        }
                    });
                    
                    orderCard.add(Box.createVerticalStrut(5));
                    orderCard.add(deleteButton);
                }
                
                ordersList.add(orderCard);
                ordersList.add(Box.createVerticalStrut(10));
            }
        }
        
        // Add scroll pane for orders
        JScrollPane ordersScrollPane = new JScrollPane(ordersList);
        ordersScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        ordersScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        ordersPanel.add(ordersScrollPane);
        
        // Add refresh button
        JButton refreshButton = new JButton("Refresh Orders");
        refreshButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshButton.setBackground(new Color(0, 120, 0));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setBorderPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        refreshButton.addActionListener(e -> refreshOrdersPanel());
        
        ordersPanel.add(Box.createVerticalStrut(10));
        ordersPanel.add(refreshButton);
        
        // Add panels to dashboard
        dashboard.add(ordersPanel, BorderLayout.EAST);
        
        // Add logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutButton.setBackground(new Color(108, 117, 125));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        logoutButton.addActionListener(e -> {
            this.dispose();
            RestaurantApp app = new RestaurantApp();
            app.setVisible(true);
        });
        
        // Add edit profile button
        JButton editProfileButton = new JButton("Edit Profile");
        editProfileButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        editProfileButton.setBackground(new Color(70, 130, 180));
        editProfileButton.setForeground(Color.WHITE);
        editProfileButton.setFocusPainted(false);
        editProfileButton.setBorderPainted(false);
        editProfileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        editProfileButton.addActionListener(e -> showEditProfileDialog());
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(editProfileButton);
        bottomPanel.add(logoutButton);
        dashboard.add(bottomPanel, BorderLayout.SOUTH);
        
        setContentPane(dashboard);
        revalidate();
        repaint();
    }

    private void refreshOrdersPanel() {
        orders = FileHandler.loadOrders();
        showClientDashboard();
    }

    private void showPlaceOrderDialog(Product product) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Quantity bigger than 1 (sala7tha ! )
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 10, 1);
        JSpinner quantitySpinner = new JSpinner(spinnerModel);

        JComboBox<Order.OrderType> typeCombo = new JComboBox<>(Order.OrderType.values());

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Product: " + product.getName()), gbc);

        gbc.gridy = 1;
        panel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        panel.add(quantitySpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Order Type:"), gbc);
        gbc.gridx = 1;
        panel.add(typeCombo, gbc);

        int result = JOptionPane.showConfirmDialog(this, panel, "Place Order",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            int quantity = (Integer) quantitySpinner.getValue();
            Order.OrderType type = (Order.OrderType) typeCombo.getSelectedItem();

            // Id 5dmtou bel wa9t 
            int orderId = (int) (System.currentTimeMillis() % 1000000);

            Order newOrder = new Order(
                orderId,
                (Client) currentUser,
                type
            );

            // Add product to order
            Order.OrderItem item = new Order.OrderItem(product, quantity);
            newOrder.addItem(item);

            orders.add(newOrder);
            FileHandler.saveOrders(orders);

            //calling it to keep it refreshed 
            showClientDashboard();
            JOptionPane.showMessageDialog(this, "Order placed successfully!");
        }
    }

    private Color getStatusColor(Order.OrderStatus status) {
        return switch (status) {
            case PENDING -> new Color(255, 193, 7);
            case PREPARING -> new Color(70, 130, 180);
            case READY -> new Color(46, 139, 87);
            case DELIVERED -> new Color(108, 117, 125);
            case CANCELLED -> new Color(220, 53, 69);
            default -> Color.BLACK;
        }; // Yellow
        // Blue
        // Green
        // Gray
        // Red
    }

    

    private void showManagerDashboard() {
        ManagerDashboard managerDashboard = new ManagerDashboard();
        managerDashboard.setVisible(true);
        this.dispose();
    }

    private void showEditProfileDialog() {
        if (!(currentUser instanceof Client)) {
            return;
        }
        
        Client client = (Client) currentUser;
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

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Profile",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validate input fields
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String phone = phoneField.getText().trim();
                String address = addressField.getText().trim();
                String dob = dobField.getText().trim();

                if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || 
                    lastName.isEmpty() || phone.isEmpty() || address.isEmpty() || dob.isEmpty()) {
                    showError("All fields are required!");
                    return;
                }

                // Check if username is changed and if it already exists
                if (!username.equals(client.getUsername()) && 
                    (findClient(username) != null || findAdmin(username) != null)) {
                    showError("Username already exists!");
                    return;
                }

                if (password.length() < 6) {
                    showError("Password must be at least 6 characters long!");
                    return;
                }

                // Update client information
                client.setUsername(username);
                client.setPassword(password);
                client.setFirstName(firstName);
                client.setLastName(lastName);
                client.setPhoneNumber(phone);
                client.setAddress(address);
                client.setDateOfBirth(java.time.LocalDate.parse(dob));

                // Save changes
                FileHandler.saveClients(clients);
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
                
                // Refresh the dashboard
                showClientDashboard();
            } catch (java.time.format.DateTimeParseException ex) {
                showError("Invalid date format! Please use YYYY-MM-DD");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RestaurantApp());
    }
} 