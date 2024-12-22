package techcompany.service;

import techcompany.database;
import techcompany.entities.Car;
import techcompany.entities.Response;

import java.sql.*;

public class CarService {

    public static void createCarInfo(Connection connect, Car car) {
        String sql = "INSERT INTO car (id_card, public_key, owner, model, type, color, number_of_car, balance, pin) VALUES\n"
                + "(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        connect = database.connectDb();
        try{
            PreparedStatement pre = connect.prepareStatement(sql);
            pre.setString(1, car.getIdCard());
            pre.setString(2, car.getPublicKey());
            pre.setString(3, car.getOwner());
            pre.setString(4, car.getModel());
            pre.setString(5, car.getType());
            pre.setString(6, car.getColor());
            pre.setString(7, car.getNumberOfCar());
            pre.setBigDecimal(8, car.getBalance());
            pre.setString(9, car.getPin());
            pre.executeUpdate();
            pre.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
