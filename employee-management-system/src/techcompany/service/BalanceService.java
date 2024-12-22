package techcompany.service;

public class BalanceService {

    private double balance;

    // Constructor to initialize balance
    public BalanceService(double initialBalance) {
        this.balance = initialBalance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    // Method to get the current balance
    public double getBalance() {
        return balance;
    }

    // Method to deposit money
    public String deposit(double amount) {
        if (amount <= 0) {
            return "Số tiền nạp phải lớn hơn 0.";
        }
        balance += amount;
        return "Nạp tiền thành công. Số dư hiện tại: " + balance + " đ";
    }

    // Method to withdraw money
    public String withdraw(double amount) {
        if (amount <= 0) {
            return "Số tiền trừ phải lớn hơn 0.";
        }
        if (amount > balance) {
            return "Số dư không đủ để trừ số tiền này.";
        }
        balance -= amount;
        return "Trừ tiền thành công. Số dư hiện tại: " + balance + " đ";
    }

}