package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class DeliveryTask {
    private final String taskId;
    private final Order order;
    private final DeliveryAgent agent;
    private TimeSlot actualTimeSlot;
    private LocalDateTime assignedTime;
    private LocalDateTime pickedUpTime;
    private LocalDateTime deliveredTime;
    private int additionalDelayMinutes;

    public DeliveryTask(Order order, DeliveryAgent agent) {
        this.taskId = "TASK_" + System.currentTimeMillis() + "_" + Objects.hash(order, agent);
        this.order = order;
        this.agent = agent;
        this.additionalDelayMinutes = 0;
        this.assignedTime = LocalDateTime.now();
        calculateActualTimeSlot();
    }
    private void calculateActualTimeSlot() {
        if (order.isScheduled()) {
            TimeSlot scheduled = order.getScheduledSlot();
            this.actualTimeSlot = new TimeSlot(
                    scheduled.getStartTime().plusMinutes(additionalDelayMinutes),
                    scheduled.getDurationMinutes()
            );
        } else {
            this.actualTimeSlot = new TimeSlot(LocalDateTime.now(), 30); // Default 30 mins
        }
    }

    public DeliveryAgent getAgent() {
        return agent;
    }

    public int getAdditionalDelayMinutes() {
        return additionalDelayMinutes;
    }

    public LocalDateTime getAssignedTime() {
        return assignedTime;
    }

    public LocalDateTime getDeliveredTime() {
        return deliveredTime;
    }

    public LocalDateTime getPickedUpTime() {
        return pickedUpTime;
    }

    public String getTaskId() {
        return taskId;
    }

    public Order getOrder() {
        return order;
    }

    public TimeSlot getActualTimeSlot() {
        return actualTimeSlot;
    }
    public void addDelay(int delayMinutes) {
        this.additionalDelayMinutes += delayMinutes;
        calculateActualTimeSlot();
    }
    public void markPickedUp() {
        this.pickedUpTime = actualTimeSlot.getStartTime();
        int totalDelay = additionalDelayMinutes;
        if (order.isScheduled()) {
            totalDelay += (int) java.time.Duration.between(
                    order.getScheduledSlot().getStartTime(), pickedUpTime).toMinutes();
        }
        order.markPickedUp(pickedUpTime, totalDelay);
        agent.assignTask(this);
    }
    public void markDelivered() {
        this.deliveredTime = actualTimeSlot.getEndTime();
        order.markDelivered(deliveredTime);
        agent.completeTask(this);
    }

    public boolean hasTimeConflictWith(DeliveryTask other) {
        return this.actualTimeSlot.overlapsWith(other.actualTimeSlot);
    }

}
