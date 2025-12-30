package scheduler;


import model.DeliveryAgent;
import model.DeliveryTask;
import model.TimeSlot;

import java.time.LocalDateTime;
import java.util.*;

public class DeliveryScheduler {
    private static final int MAX_DELAY_MINUTES = 120; // 2 hours max delay

    public static class SchedulingResult {
        public final boolean canSchedule;
        public final int requiredDelayMinutes;
        public final LocalDateTime adjustedStartTime;

        public SchedulingResult(boolean canSchedule, int delayMinutes, LocalDateTime adjustedTime) {
            this.canSchedule = canSchedule;
            this.requiredDelayMinutes = delayMinutes;
            this.adjustedStartTime = adjustedTime;
        }
    }

    public SchedulingResult canScheduleTask(DeliveryAgent agent, DeliveryTask newTask) {
        if (!agent.canServicePincode(newTask.getOrder().getDeliveryPincode())) {
            return new SchedulingResult(false, 0, null);
        }

        List<DeliveryTask> currentTasks = agent.getAssignedTasks();
        TimeSlot newSlot = newTask.getActualTimeSlot();

        // Check for immediate availability
        if (isSlotAvailable(agent, newSlot)) {
            return new SchedulingResult(true, 0, newSlot.getStartTime());
        }

        // Find next available slot with delay
        return findNextAvailableSlot(agent, newTask);
    }

    private boolean isSlotAvailable(DeliveryAgent agent, TimeSlot slot) {
        return agent.getBusySlots().stream()
                .noneMatch(busySlot -> busySlot.overlapsWith(slot));
    }

    private SchedulingResult findNextAvailableSlot(DeliveryAgent agent, DeliveryTask task) {
        TimeSlot originalSlot = task.getActualTimeSlot();
        LocalDateTime currentTime = originalSlot.getStartTime();
        int delay = 0;

        while (delay <= MAX_DELAY_MINUTES) {
            delay += 15; // Check in 15-minute increments
            TimeSlot delayedSlot = originalSlot.withDelay(delay);

            if (isSlotAvailable(agent, delayedSlot)) {
                return new SchedulingResult(true, delay, delayedSlot.getStartTime());
            }
        }

        return new SchedulingResult(false, 0, null);
    }

    public List<DeliveryAgent> findAvailableAgents(List<DeliveryAgent> agents, DeliveryTask task) {
        return agents.stream()
                .filter(agent -> canScheduleTask(agent, task).canSchedule)
                .toList();
    }

    public DeliveryAgent findBestAgent(List<DeliveryAgent> agents, DeliveryTask task) {
        return agents.stream()
                .filter(agent -> agent.canServicePincode(task.getOrder().getDeliveryPincode()))
                .min((a1, a2) -> {
                    int delay1 = canScheduleTask(a1, task).requiredDelayMinutes;
                    int delay2 = canScheduleTask(a2, task).requiredDelayMinutes;
                    return Integer.compare(delay1, delay2);
                })
                .orElse(null);
    }
}