package techcompany.service;

import techcompany.entities.Response;
import techcompany.util.Constant;
import techcompany.util.Utils;

import java.nio.ByteBuffer;

public class BalanceService {

    private int balance;

    public BalanceService() {

    }

    // Constructor to initialize balance
    public BalanceService(int initialBalance) {
        this.balance = initialBalance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    // Method to get the current balance
    public int getBalance() {
        return balance;
    }

    // Method to deposit money
    public String deposit(int amount, BalanceService balanceService) {
        if (amount <= 0) {
            return "Vui lòng nhập lại";
        }
        balance = balanceService.getBalance() + amount;
        byte[] bytes = ByteBuffer.allocate(4).putInt((int)balance).array();
        Response response = Utils.saveAndGetData((byte) 0x05, (byte) 01, bytes);
        if(response.errorCode == Constant.SUCCESS) {
            return response.getdata();
        }
        return "lỗi không kết nối được với thẻ";
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