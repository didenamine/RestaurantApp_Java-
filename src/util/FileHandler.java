package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import model.*;

public class FileHandler {
    private static final String CLIENTS_FILE = "clients.txt";
    private static final String ADMINS_FILE = "admins.txt";
    private static final String ORDERS_FILE = "orders.txt";
    private static final String MENU_FILE = "menu.txt";

    public static void saveClients(List<Client> clients) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CLIENTS_FILE))) {
            // we turn it into bites bytes here
            oos.writeObject(clients); 
        } catch (IOException e) {
        }
    }
    public static List<Client> loadClients() {
        List<Client> clients = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CLIENTS_FILE))) {
            clients = (List<Client>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
        }
        return clients;
    }
    public static void saveAdmins(List<Admin> admins) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ADMINS_FILE))) {
            oos.writeObject(admins);
        } catch (IOException e) {
        }
    }
    public static List<Admin> loadAdmins() {
        List<Admin> admins = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ADMINS_FILE))) {
            admins = (List<Admin>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
        }
        return admins;
    }
    public static void saveOrders(List<Order> orders) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ORDERS_FILE))) {
            oos.writeObject(orders);
        } catch (IOException e) {
        }
    }

    public static List<Order> loadOrders() {
        List<Order> orders = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ORDERS_FILE))) {
            orders = (List<Order>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
        }
        return orders;
    }
    public static void saveMenu(List<Product> menu) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(MENU_FILE))) {
            oos.writeObject(menu);
        } catch (IOException e) {
        }
    }
    public static List<Product> loadMenu() {
        List<Product> menu = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(MENU_FILE))) {
            menu = (List<Product>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
        }
        return menu;
    }
} 