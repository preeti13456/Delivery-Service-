package model;

import java.util.*;

public class DeliveryAgent {
    private final String agentId;
    private final String name;
    private final Set<Pincode> serviceablePincodes;
    private final List<DeliveryTask> assignedTasks;

    public DeliveryAgent(String agentId, String name, Set<Pincode> pincodes) {
        this.agentId = agentId;
        this.name = name;
        this.serviceablePincodes = new HashSet<>(pincodes);
        this.assignedTasks = new ArrayList<>();
    }

    // Getters
    public String getAgentId() { return agentId; }
    public String getName() { return name; }
    public Set<Pincode> getServiceablePincodes() { return new HashSet<>(serviceablePincodes); }
    public List<DeliveryTask> getAssignedTasks() { return new ArrayList<>(assignedTasks); }

    public boolean canServicePincode(Pincode pincode) {
        return serviceablePincodes.contains(pincode);
    }

    public void addServiceablePincode(Pincode pincode) {
        serviceablePincodes.add(pincode);
    }

    public void assignTask(DeliveryTask task) {
        assignedTasks.add(task);
    }

    public void completeTask(DeliveryTask task) {
        assignedTasks.remove(task);
    }

    public List<TimeSlot> getBusySlots() {
        return assignedTasks.stream()
                .map(DeliveryTask::getActualTimeSlot)
                .filter(Objects::nonNull)
                .toList();
    }

    public boolean isAvailableDuring(TimeSlot slot) {
        return getBusySlots().stream()
                .noneMatch(busySlot -> busySlot.overlapsWith(slot));
    }

    @Override public String toString() {
        return String.format("Agent{id='%s', name='%s', pincodes=%s}",
                agentId, name, serviceablePincodes);
    }
}
