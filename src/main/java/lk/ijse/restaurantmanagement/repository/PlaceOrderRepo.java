package lk.ijse.restaurantmanagement.repository;

import javafx.scene.control.Alert;
import lk.ijse.restaurantmanagement.db.DbConnection;
import lk.ijse.restaurantmanagement.model.PlaceOrder;

import java.sql.Connection;
import java.sql.SQLException;

public class PlaceOrderRepo {
    public static boolean placeOrder(PlaceOrder po) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        connection.setAutoCommit(false);

        try {
            boolean isOrderSaved = OrderRepo.save(po.getOrder());
            if (isOrderSaved) {
                boolean isOrderDetailSaved = OrderDetailRepo.save(po.getOdList());
                if (isOrderDetailSaved) {
                        connection.commit();
                        return true;
                }
            }
            connection.rollback();
            return false;
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            connection.rollback();
            return false;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public static boolean placeOrderUpdate(PlaceOrder po) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        connection.setAutoCommit(false);

        try {
            boolean isOrderSaved = OrderRepo.update(po.getOrder());
            if (isOrderSaved) {
                boolean isOrderDetailSaved = OrderDetailRepo.update(po.getOdList());
                if (isOrderDetailSaved) {
                    connection.commit();
                    return true;
                }
            }
            connection.rollback();
            return false;
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            connection.rollback();
            return false;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public static boolean deletePlaceOrder(String orderId) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        connection.setAutoCommit(false);

        try {
            boolean isOrderDetailSaved = OrderDetailRepo.deleteOrderDetails(orderId);
            if (isOrderDetailSaved) {
                boolean isOrderSaved = OrderRepo.deleteOrder(orderId);
                if (isOrderSaved) {
                    connection.commit();
                    return true;
                }
            }
            connection.rollback();
            return false;
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            connection.rollback();
            return false;
        } finally {
            connection.setAutoCommit(true);
        }
    }
}
