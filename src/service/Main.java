package service;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Set;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        DeliveryService service = DeliveryService.getInstance();

        System.out.println("=== COMPREHENSIVE DELIVERY SERVICE TEST ===\n");

        // Test Case 1: Basic orders
        System.out.println("1. BASIC ORDERS TEST");
        service.createOrder("Order A", "560087");
        service.createOrder("Order B", "560088");
        service.createOrder("Order C", "560089");
        service.createOrder("Order D", "560087");

        service.createAgent("AgentA", "560087");
        service.createAgent("AgentB", "560088");
        service.createAgent("AgentC", "560089");
        service.createAgent("Agent D", "560087");

    service.processAllPendingOrdersBulk().forEach(System.out::println);

        // Test Case 2: Multiple pincodes per agent
        System.out.println("\n2. MULTIPLE PINCODES PER AGENT TEST");
        service.createAgent("AgentD", Set.of("560087", "560088", "560089"));
        service.createOrder("Order E", "560087");
        service.createOrder("Order F", "560088");
        service.createOrder("Order G", "560089");

    service.processAllPendingOrdersBulk().forEach(System.out::println);

        // Test Case 3: Scheduled orders with time collisions
        System.out.println("\n3. SCHEDULED ORDERS WITH TIME MANAGEMENT");
        LocalDateTime baseTime = LocalDateTime.of(2025, Month.MARCH, 22, 10, 30);

        // Create overlapping scheduled orders
        String scheduled1 = service.createScheduledOrder("Scheduled Order 1", "560087", baseTime, 30);
        String scheduled2 = service.createScheduledOrder("Scheduled Order 2", "560087", baseTime.plusMinutes(15), 30);
        String scheduled3 = service.createScheduledOrder("Scheduled Order 3", "560087", baseTime.plusMinutes(45), 30);

        service.processDelivery(scheduled1);
        service.processDelivery(scheduled2); // This should get delayed
        service.processDelivery(scheduled3);

        // Test Case 4: Add more capacity and test
        System.out.println("\n4. ADDING MORE AGENTS FOR LOAD BALANCING");
        service.createAgent("AgentE", Set.of("560087", "560088"));

        service.createOrder("Order H", "560087");
        service.createOrder("Order I", "560087");
        service.createOrder("Order J", "560088");

    service.processAllPendingOrdersBulk().forEach(System.out::println);

        System.out.println("\n=== TEST COMPLETED ===");
        }
    }
