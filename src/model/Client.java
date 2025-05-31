package model;

import java.io.Serializable;
import java.time.LocalDate;

@SuppressWarnings("unused")
public class Client extends User implements Serializable {
    private String address;
    private LocalDate dateOfBirth;

    public Client(String username, String password, String firstName, String lastName, 
                 String phoneNumber, String address, LocalDate dateOfBirth) {
        super(username, password, firstName, lastName, phoneNumber);
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String toString() {
        return "Client{" +
                "username='" + getUsername() + '\'' +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", phoneNumber='" + getPhoneNumber() + '\'' +
                ", address='" + address + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
} 