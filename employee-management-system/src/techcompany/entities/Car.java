package techcompany.entities;

import java.math.BigDecimal;

public class Car {
    private int id;
    private String idCard;
    private String publicKey;
    private String owner;
    private String model;
    private String type;
    private String color;
    private String numberOfCar;
    private BigDecimal balance;
    private String pin;

    public Car() {
    }

    public Car(String idCard, String publicKey, String owner, String model, String type, String color, String numberOfCar, BigDecimal balance, String pin) {
        this.idCard = idCard;
        this.publicKey = publicKey;
        this.owner = owner;
        this.model = model;
        this.type = type;
        this.color = color;
        this.numberOfCar = numberOfCar;
        this.balance = balance;
        this.pin = pin;
    }

    public Car(String owner, String model, String type, String color, String numberOfCar, String pin) {
        this.owner = owner;
        this.model = model;
        this.type = type;
        this.color = color;
        this.numberOfCar = numberOfCar;
        this.pin = pin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNumberOfCar() {
        return numberOfCar;
    }

    public void setNumberOfCar(String numberOfCar) {
        this.numberOfCar = numberOfCar;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    @Override
    public String toString() {
        return this.numberOfCar + "@" + this.model + "@" + this.type + "@" + this.color + "@" + this.owner + "@" + this.pin;
    }
}
