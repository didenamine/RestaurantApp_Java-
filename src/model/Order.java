package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {
    private final int orderId;
    private Client client;
    private List<OrderItem> items;
    private double totalAmount;
    private LocalDateTime orderTime;
    public enum OrderStatus {
        PENDING,
        PREPARING,
        READY,
        DELIVERED,
        CANCELLED
    }

    private OrderStatus status;
    private OrderType type;

    public enum OrderType {
        HOME_DELIVERY,
        ON_SITE_PICKUP,
        TAKE_AWAY
    }

    public Order(int orderId, Client client, OrderType type) {
        this.orderId = orderId;
        this.client = client;
        this.items = new ArrayList<>();
        this.orderTime = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
        this.type = type;
        calculateTotal();
    }

    private void calculateTotal() {
        this.totalAmount = items.stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum();
    }

    public void addItem(OrderItem item) {
        if (item != null) {
            items.add(item);
            calculateTotal();
        }
    }

    public void removeItem(OrderItem item) {
        if (item != null) {
            items.remove(item);
            calculateTotal();
        }
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }

    public void setItems(List<OrderItem> items) {
        if (items != null) {
            this.items = new ArrayList<>(items);
            calculateTotal();
        }
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        if (status != null) {
            this.status = status;
        }
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        if (type != null) {
            this.type = type;
        }
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", client=" + (client != null ? client.getUsername() : "null") +
                ", totalAmount=" + totalAmount +
                ", orderTime=" + orderTime +
                ", status=" + status +
                ", type=" + type +
                '}';
    }
    public static class OrderItem implements Serializable {
        private Product product;
        private int quantity;
        public OrderItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
        public Product getProduct() {
            return product;
        }
        public void setProduct(Product product) {
            this.product = product;
        }
        public int getQuantity() {
            return quantity;
        }
        public void setQuantity(int quantity) {
            if (quantity > 0) {
                this.quantity = quantity;
            }
        }
    }
} 