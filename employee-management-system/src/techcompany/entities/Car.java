package techcompany.entities;

public class Car {
    private int id;
    private String idCard;
    private String publicKey;
    private String owner;
    private String branch;
    private String color;
    private String numberOfCar;
    private String balance;

    public Car() {
    }

    public Car(int id, String idCard, String publicKey, String owner, String branch, String color, String numberOfCar, String balance) {
        this.id = id;
        this.idCard = idCard;
        this.publicKey = publicKey;
        this.owner = owner;
        this.branch = branch;
        this.color = color;
        this.numberOfCar = numberOfCar;
        this.balance = balance;
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

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
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

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
