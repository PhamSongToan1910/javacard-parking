package techcompany.service;

import techcompany.database;
import techcompany.entities.History;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HistoryService {
    public static List<History> getHistoryList(Connection connect, String idCard) {
        List<History> histories = new ArrayList<>();
        try {
            String sql = "select * from history where idCard = ?";
            assert connect != null;
            PreparedStatement pre = connect.prepareStatement(sql);
            pre.setString(1, idCard);
            ResultSet rs = pre.executeQuery();
            while(rs.next()) {
                int id = rs.getInt(1);
                String timeIn = rs.getString(3);
                String timeOut = rs.getString(4);
                History history = new History(id, idCard, timeIn, timeOut);
                histories.add(history);
            }
            pre.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return histories;
    }

    public static void createHistory(Connection connect, History history) {
        java.sql.Date timeIn = new java.sql.Date(Long.parseLong(history.getTimeIn()));
        try {
            String sql = "INSERT INTO history (id_card, time_in) VALUES\n" +
                    "(?, ?)";
            PreparedStatement pre = connect.prepareStatement(sql);
            pre.setString(1, history.getIdCard());
            pre.setDate(2, timeIn);
            pre.executeUpdate();
            pre.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void updateHistory(Connection connect, History history) {
        java.sql.Date timeIn = new java.sql.Date(Long.parseLong(history.getTimeIn()));
        java.sql.Date timeOut = new java.sql.Date(Long.parseLong(history.getTimeOut()));
        try {
            String sql = "UPDATE history SET time_out = ? WHERE ID = ? and time_in = ?";
            PreparedStatement pre = connect.prepareStatement(sql);
            pre.setDate(1, timeOut);
            pre.setString(2, history.getIdCard());
            pre.setDate(3, timeIn);
            pre.executeUpdate();
            pre.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
