package model;


import java.time.LocalDateTime;

public class Order {
    private final String orderId;
    private final String name;
    private final Pincode deliveryPincode;
    private final TimeSlot scheduleSlot;
    private OrderStatus status;
    private LocalDateTime actualPickupTime;
    private LocalDateTime actualDeliverTime;
    private int delayInMinutes;

    Order(Builder builder) {
        this.orderId = builder.orderId;
        this.name = builder.name;
        this.deliveryPincode = builder.deliveryPincode;
        this.scheduleSlot = builder.scheduledSlot;
        this.status = OrderStatus.CREATED;
        this.delayInMinutes = 0;

    }

    public boolean isScheduled() {
        return this.scheduleSlot != null;
    }

    public LocalDateTime getActualPickupTime() {
        return actualPickupTime;
    }

    public void assign() {
        this.status = OrderStatus.ASSIGNED;
    }

    public void markPickedUp(LocalDateTime pickupTime, int delayInMinutes) {
        this.status = OrderStatus.PICKED_UP;
        this.actualPickupTime = pickupTime;
        this.delayInMinutes = delayInMinutes;
    }

    public void markDelivered(LocalDateTime deliveryTime) {
        this.status = OrderStatus.DELIVERED;
        this.actualDeliverTime = deliveryTime;
    }

    public void markDelayed() {
        this.status = OrderStatus.DELAYED;
    }

    public String getName() {
        return name;
    }

    public int getDelayInMinutes() {
        return delayInMinutes;
    }

    public LocalDateTime getActualDeliverTime() {
        return actualDeliverTime;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Pincode getDeliveryPincode() {
        return deliveryPincode;
    }

    public String getOrderId() {
        return orderId;
    }


    public TimeSlot getScheduledSlot() {
        return scheduleSlot;
    }


    public static class Builder {
        private String orderId;
        private String name;
        private Pincode deliveryPincode;
        private TimeSlot scheduledSlot;

        public Builder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder deliveryPincode(Pincode pincode) {
            this.deliveryPincode = pincode;
            return this;
        }

        public Builder scheduledSlot(TimeSlot slot) {
            this.scheduledSlot = slot;
            return this;
        }

        public Builder scheduledSlot(LocalDateTime time, int duration) {
            this.scheduledSlot = new TimeSlot(time, duration);
            return this;
        }
        public Order build() {
            if (orderId == null || name == null || deliveryPincode == null) {
                throw new IllegalStateException("Missing required fields");
            }
            return new Order(this);
        }
    }
    }

