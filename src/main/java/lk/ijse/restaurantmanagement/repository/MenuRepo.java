package lk.ijse.restaurantmanagement.repository;

import lk.ijse.restaurantmanagement.db.DbConnection;
import lk.ijse.restaurantmanagement.model.Menu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuRepo {

    public static List<Menu> getAll() throws SQLException {
        String sql = "SELECT * FROM Menu";

        PreparedStatement pstm = DbConnection.getInstance().getConnection()
                .prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();

        List<Menu> itemList = new ArrayList<>();
        while (resultSet.next()) {
            String id = resultSet.getString(1);
            String name = resultSet.getString(2);
            String size = resultSet.getString(3);
            String unitPrice = resultSet.getString(4);
            String status = resultSet.getString(5);

            Menu menu = new Menu(id, name, size, unitPrice, status);
            itemList.add(menu);
        }
        return itemList;
    }

    public static List<Menu> searchByMenuName(String menuName) throws SQLException {
        String sql = "SELECT * FROM menu WHERE name LIKE '"+menuName+"%'";
        PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();

        List<Menu> itemList = new ArrayList<>();
        while (resultSet.next()) {
            String id = resultSet.getString(1);
            String name = resultSet.getString(2);
            String size = resultSet.getString(3);
            String unitPrice = resultSet.getString(4);
            String status = resultSet.getString(5);

            Menu menu = new Menu(id, name, size, unitPrice, status);
            itemList.add(menu);
        }
        return itemList;
    }

    public String autoGenerateItemCode() throws SQLException {
        String sql = "SELECT id from menu order by id desc limit 1";
        PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();

        if (resultSet.next()) {
            String id = resultSet.getString("id");
            String numericPart = id.replaceAll("\\D+", "");
            int newId = Integer.parseInt(numericPart) + 1;
            return String.format("M%03d", newId);
        } else {
            return "M001";
        }
    }

    public static boolean save(Menu menu) throws SQLException {
        String sql = "INSERT INTO Menu VALUES(?, ?, ?, ?,?)";
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setObject(1, menu.getId());
        pstm.setObject(2, menu.getName());
        pstm.setObject(3, menu.getSize());
        pstm.setObject(4, menu.getUnitPrice());
        pstm.setObject(5, menu.getStatus());
        return pstm.executeUpdate() > 0;
    }

    public static boolean update(Menu item) throws SQLException {
        String sql = "UPDATE Menu SET name = ?, size = ?, unitPrice = ?,status = ? WHERE id = ?";
        PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);
        pstm.setObject(1, item.getName());
        pstm.setObject(2, item.getSize());
        pstm.setObject(3, item.getUnitPrice());
        pstm.setObject(4, item.getStatus());
        pstm.setObject(5, item.getId());
        return pstm.executeUpdate() > 0;
    }

    public static boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM Menu WHERE id = ?";
        PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);
        pstm.setObject(1, id);
        return pstm.executeUpdate() > 0;
    }
}