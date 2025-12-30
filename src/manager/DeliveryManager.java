package manager;

import exception.AgentNotAvailableException;
import exception.OrderNotFoundException;
import model.DeliveryAgent;
import model.DeliveryTask;
import model.Order;
import model.OrderStatus;
import observer.DeliveryObserver;
import observer.LoggingDeliveryObserver;
import strategy.AgentAssignmentStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;

public class DeliveryManager {
    private final OrderManager orderManager;
    private final AgentManager agentManager;
    private final AgentAssignmentStrategy assignmentStrategy;
    private final List<DeliveryObserver> observers = new ArrayList<>();
    private final Map<String, DeliveryTask> deliveryTasks = new ConcurrentHashMap<>();

    public DeliveryManager(OrderManager orderManager, AgentManager agentManager,
                           AgentAssignmentStrategy assignmentStrategy) {
        this.orderManager = orderManager;
        this.agentManager = agentManager;
        this.assignmentStrategy = assignmentStrategy;
        addObserver(new LoggingDeliveryObserver());
    }

    public void addObserver(DeliveryObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(DeliveryTask task, OrderStatus oldStatus, OrderStatus newStatus) {
        observers.forEach(observer -> observer.onDeliveryUpdate(task, oldStatus, newStatus));
    }

    public DeliveryTask assignOrder(String orderId) {
        Order order = orderManager.getOrder(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        List<DeliveryAgent> agents = agentManager.getAgentsForPincode(order.getDeliveryPincode());
        if (agents.isEmpty()) {
            throw new AgentNotAvailableException("No agents for pincode: " + order.getDeliveryPincode());
        }

        DeliveryTask task = new DeliveryTask(order, null); // Temporary task for scheduling

        AgentAssignmentStrategy.AssignmentResult result = assignmentStrategy.assignAgent(task, agents)
                .orElseThrow(() -> new AgentNotAvailableException("No available agents for time slot"));

        // Create actual task with assigned agent and delay
        DeliveryTask actualTask = new DeliveryTask(order, result.agent);
        if (result.delayMinutes > 0) {
            actualTask.addDelay(result.delayMinutes);
            order.markDelayed();
        }

        deliveryTasks.put(actualTask.getTaskId(), actualTask);

        OrderStatus oldStatus = order.getStatus();
        order.assign();
        notifyObservers(actualTask, oldStatus, order.getStatus());

        return actualTask;
    }

    public void processDelivery(String orderId) {
        DeliveryTask task = assignOrder(orderId);

        OrderStatus oldStatus = task.getOrder().getStatus();
        task.markPickedUp();
        notifyObservers(task, oldStatus, task.getOrder().getStatus());

        task.markDelivered();
        notifyObservers(task, OrderStatus.PICKED_UP, task.getOrder().getStatus());
    }

    public void processAllPendingOrders() {
        orderManager.getPendingOrders().forEach(order -> {
            if (order.getStatus() == OrderStatus.CREATED) {
                try {
                    processDelivery(order.getOrderId());
                } catch (Exception e) {
                    System.out.println("Failed to process order " + order.getName() + ": " + e.getMessage());
                }
            }
        });
    }

    /**
     * Process pending orders in bulk and return human-friendly status messages.
     * This method avoids external callers iterating the driver; it maps each order to
     * a sequence of assignment/pickup/delivery operations and returns the result messages.
     */
    public List<String> processAllPendingOrdersBulk() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("hh:mm a, MMM dd, yyyy");
        return orderManager.getPendingOrders().stream()
                .filter(order -> order.getStatus() == OrderStatus.CREATED)
                .map(order -> {
                    try {
                        DeliveryTask task = assignOrder(order.getOrderId());

                        // perform pickup
                        task.markPickedUp();
                        String pickupMsg = task.getAgent().getName() + " has picked up " + task.getOrder().getName();
                        if (task.getPickedUpTime() != null) {
                            pickupMsg += " at " + fmt.format(task.getPickedUpTime());
                        }

                        // perform delivery
                        task.markDelivered();
                        String deliveryMsg = task.getAgent().getName() + " has delivered " + task.getOrder().getName() +
                                " to " + task.getOrder().getDeliveryPincode().getCode();
                        if (task.getDeliveredTime() != null) {
                            deliveryMsg += " at " + fmt.format(task.getDeliveredTime());
                        }

                        return List.of(pickupMsg, deliveryMsg);
                    } catch (Exception e) {
                        return List.of("Failed to process order " + order.getName() + ": " + e.getMessage());
                    }
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
