package strategy;

import model.DeliveryAgent;
import model.DeliveryTask;
import scheduler.DeliveryScheduler;

import java.util.List;
import java.util.Optional;

public class TimeAwareAssignmentStrategy implements AgentAssignmentStrategy{
    private final DeliveryScheduler scheduler = new DeliveryScheduler();

    @Override
    public Optional<AssignmentResult> assignAgent(DeliveryTask task, List<DeliveryAgent> agents) {
        DeliveryAgent bestAgent = scheduler.findBestAgent(agents, task);
        if (bestAgent == null) {
            return Optional.empty();
        }

        DeliveryScheduler.SchedulingResult result = scheduler.canScheduleTask(bestAgent, task);
        if (result.canSchedule) {
            return Optional.of(new AssignmentResult(bestAgent, result.requiredDelayMinutes));
        }

        return Optional.empty();
    }
}
