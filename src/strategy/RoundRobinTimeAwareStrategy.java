package strategy;

import model.DeliveryAgent;
import model.DeliveryTask;
import scheduler.DeliveryScheduler;

import java.util.List;
import java.util.Optional;

public class RoundRobinTimeAwareStrategy implements AgentAssignmentStrategy {
    private final DeliveryScheduler scheduler = new DeliveryScheduler();
    private int currentIndex = 0;

    @Override
    public Optional<AssignmentResult> assignAgent(DeliveryTask task, List<DeliveryAgent> agents) {
        if (agents.isEmpty()) return Optional.empty();

        for (int i = 0; i < agents.size(); i++) {
            DeliveryAgent agent = agents.get((currentIndex + i) % agents.size());
            if (agent.canServicePincode(task.getOrder().getDeliveryPincode())) {
                DeliveryScheduler.SchedulingResult result = scheduler.canScheduleTask(agent, task);
                if (result.canSchedule) {
                    currentIndex = (currentIndex + i + 1) % agents.size();
                    return Optional.of(new AssignmentResult(agent, result.requiredDelayMinutes));
                }
            }
        }
        return Optional.empty();
    }
}
