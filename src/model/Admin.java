package model;

import java.io.Serializable;

@SuppressWarnings("unused")
public class Admin extends User implements Serializable {
    private static final String ADMIN_SECRET_KEY = "ADMIN123"; 
    public Admin(String username, String password, String firstName, String lastName, String phoneNumber) {
        super(username, password, firstName, lastName, phoneNumber);
    }
    public static boolean isValidAdminKey(String key) {
        return ADMIN_SECRET_KEY.equals(key);
    }
} 