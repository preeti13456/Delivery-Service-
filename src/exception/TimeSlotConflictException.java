package exception;

public class TimeSlotConflictException extends DeliveryException {
    public TimeSlotConflictException(String message) {
        super(message);
    }
}