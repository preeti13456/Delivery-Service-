package exception;

public class OrderNotFoundException extends DeliveryException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}