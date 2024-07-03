package lk.ijse.restaurantmanagement.repository;

import lk.ijse.restaurantmanagement.db.DbConnection;
import lk.ijse.restaurantmanagement.model.OrderDetail;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDetailRepo {
    public static boolean save(List<OrderDetail> odList) throws SQLException {
        for (OrderDetail od : odList) {
            if (!save(od)) {
                return false;
            }
        }
        return true;
    }

    private static boolean save(OrderDetail od) throws SQLException {
        String sql = "INSERT INTO Order_details VALUES(?, ?, ?,?)";
        PreparedStatement pstm = DbConnection.getInstance().getConnection()
                .prepareStatement(sql);
        pstm.setString(1, od.getOrderId());
        pstm.setString(2, od.getItemId());
        pstm.setInt(3, od.getQty());
        pstm.setDouble(4, od.getUnitPrice());

        return pstm.executeUpdate() > 0;
    }


    public static List<OrderDetail> searchByOrderid(String orderId) throws SQLException {
        String sql = "select * from Order_details where orderId=?";
        PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);
        pstm.setString(1, orderId);
        ResultSet resultSet = pstm.executeQuery();
        List<OrderDetail> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(
                    new OrderDetail(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getInt(3),
                            resultSet.getDouble(4)
                    ));
        }
        return list;
    }

    public static boolean update(List<OrderDetail> odList) throws SQLException {
        boolean isDelete=deleteOrderDetails(odList.get(0).getOrderId());
        if (isDelete){
            for (OrderDetail od : odList) {
                if (!save(od)) {
                    return false;
                }
            }
            return true;
        }else {
            return false;
        }
    }

    public static boolean deleteOrderDetails(String orderId) throws SQLException {
        String sql = "delete from order_details where orderid=?";
        PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);
        pstm.setString(1, orderId);
        return pstm.executeUpdate()>0;
    }
}

