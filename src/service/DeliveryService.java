package service;

import manager.AgentManager;
import manager.DeliveryManager;
import manager.OrderManager;
import strategy.TimeAwareAssignmentStrategy;
import model.Pincode;

import java.util.stream.Collectors;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;

public class DeliveryService {
    private static DeliveryService instance;
    private final OrderManager orderManager;
    private final AgentManager agentManager;
    private final DeliveryManager deliveryManager;

    private DeliveryService() {
        this.orderManager = new OrderManager();
        this.agentManager = new AgentManager();
        this.deliveryManager = new DeliveryManager(
                orderManager, agentManager, new TimeAwareAssignmentStrategy());
    }

    public static synchronized DeliveryService getInstance() {
        if (instance == null) {
            instance = new DeliveryService();
        }
        return instance;
    }
//DeliveryService.java
//Added import of model.Pincode and java.util.stream.Collectors.
//Implemented conversion from Set<String> to Set<Pincode> and delegated to the manager.
    // Order management
    public String createOrder(String name, String pincode) {
        return orderManager.createOrder(name, pincode).getOrderId();
    }

    public String createScheduledOrder(String name, String pincode,
                                       LocalDateTime scheduledTime, int durationMinutes) {
        return orderManager.createOrder(name, pincode, scheduledTime, durationMinutes).getOrderId();
    }

    // Agent management
    public String createAgent(String name, String pincode) {
        return agentManager.createAgent(name, pincode).getAgentId();
    }

    public String createAgent(String name, Set<String> pincodes) {
        // convert Set<String> to Set<Pincode> and delegate to AgentManager
        var pincodeObjects = pincodes.stream()
                .map(Pincode::new)
                .collect(Collectors.toSet());
        return agentManager.createAgent(name, pincodeObjects).getAgentId();
    }

    public void addAgentPincode(String agentId, String pincode) {
        agentManager.addAgentPincode(agentId, pincode);
    }

    // Delivery execution
    public void processDelivery(String orderId) {
        deliveryManager.processDelivery(orderId);
    }

    public void processAllPendingOrders() {
        deliveryManager.processAllPendingOrders();
    }

    public List<String> processAllPendingOrdersBulk() {
        return deliveryManager.processAllPendingOrdersBulk();
    }

    // Getters for testing
    public OrderManager getOrderManager() { return orderManager; }
    public AgentManager getAgentManager() { return agentManager; }
    public DeliveryManager getDeliveryManager() { return deliveryManager; }
}
