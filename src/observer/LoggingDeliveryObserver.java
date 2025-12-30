package observer;

import model.DeliveryTask;
import model.OrderStatus;

import java.time.format.DateTimeFormatter;

public class LoggingDeliveryObserver implements DeliveryObserver {
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("hh:mm a, MMM dd, yyyy");

    @Override
    public void onDeliveryUpdate(DeliveryTask task, OrderStatus previousStatus, OrderStatus newStatus) {
        String agentName = task.getAgent().getName();
        String orderName = task.getOrder().getName();
        String pincode = task.getOrder().getDeliveryPincode().getCode();
        switch (newStatus) {
            case ASSIGNED:
                System.out.printf("✓ %s assigned to %s for pincode %s%n",
                        orderName, agentName, pincode);
                if (task.getAdditionalDelayMinutes() > 0) {
                    System.out.printf("  ⚠ Delivery delayed by %d minutes%n",
                            task.getAdditionalDelayMinutes());
                }
                break;

            case PICKED_UP:
                String pickupTime = formatTime(task.getPickedUpTime());
                System.out.printf(" %s has picked up %s at %s%n",
                        agentName, orderName, pickupTime);
                break;
            case DELIVERED:
                String deliveryTime = formatTime(task.getDeliveredTime());
                System.out.printf(" %s has delivered %s to %s at %s%n",
                        agentName, orderName, pincode, deliveryTime);
                break;

            case DELAYED:
                System.out.printf("%s delivery delayed by %d minutes%n",
                        orderName, task.getAdditionalDelayMinutes());
                break;
            default:
                // Handle CREATED, IN_TRANSIT and others gracefully
                if (task.getPickedUpTime() != null && task.getDeliveredTime() == null) {
                    System.out.printf(" %s is in transit for %s%n", agentName, orderName);
                } else if (task.getDeliveredTime() != null) {
                    System.out.printf(" %s has delivered %s to %s at %s%n",
                            agentName, orderName, pincode, formatTime(task.getDeliveredTime()));
                } else {
                    System.out.printf("Status update for %s: %s -> %s%n", orderName, previousStatus, newStatus);
                }
        }
    }

    private String formatTime(java.time.LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(TIME_FORMATTER);
    }
    }
