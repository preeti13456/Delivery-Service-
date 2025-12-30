package manager;

import model.Order;
import model.OrderStatus;
import model.Pincode;
import model.TimeSlot;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderManager {
    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    private final AtomicInteger orderCounter = new AtomicInteger(1);

    public Order createOrder(String name, String pincode) {
        return createOrder(name, new Pincode(pincode), null);
    }

    public Order createOrder(String name, String pincode, LocalDateTime scheduledTime, int duration) {
        return createOrder(name, new Pincode(pincode), new TimeSlot(scheduledTime, duration));
    }
    public Order createOrder(String name, Pincode pincode, TimeSlot scheduledSlot) {
        String orderId = "ORD_" + orderCounter.getAndIncrement();
        Order order = new Order.Builder()
                .orderId(orderId)
                .name(name)
                .deliveryPincode(pincode)
                .scheduledSlot(scheduledSlot)
                .build();
        orders.put(orderId, order);
        return order;
    }

    public Optional<Order> getOrder(String orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }
    public List<Order> getPendingOrders() {
        return orders.values().stream()
                .filter(order -> order.getStatus() != OrderStatus.DELIVERED)
                .toList();
    }

}
