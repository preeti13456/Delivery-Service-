package model;

import java.time.LocalDateTime;

public class TimeSlot {
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final int durationMinutes;

    public TimeSlot(LocalDateTime startTime, int durationMinutes) {
        this.startTime = startTime;
        this.endTime = startTime.plusMinutes(durationMinutes);
        this.durationMinutes = durationMinutes;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
    public boolean overlapsWith(TimeSlot other) {
        return(!this.endTime.isBefore(other.startTime) && !this.startTime.isAfter(other.endTime));
    }
    public TimeSlot withDelay(int delayMinutes) {
        return new TimeSlot(startTime.plusMinutes(delayMinutes), durationMinutes);
    }
}
