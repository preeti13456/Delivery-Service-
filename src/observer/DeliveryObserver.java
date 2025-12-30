package observer;

import model.DeliveryTask;
import model.OrderStatus;

public interface DeliveryObserver {
    void onDeliveryUpdate(DeliveryTask task, OrderStatus previousStatus, OrderStatus newStatus);
}
