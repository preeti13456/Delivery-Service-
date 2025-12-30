package strategy;

import model.DeliveryAgent;
import model.DeliveryTask;

import java.util.List;
import java.util.Optional;

//for the safer side i'm implementing two strategies whichever work with extensiblke testcases we can take that
public interface AgentAssignmentStrategy {

    Optional<AssignmentResult> assignAgent(DeliveryTask task, List<DeliveryAgent> agents);

    class AssignmentResult {
        public final DeliveryAgent agent;
        public final int delayMinutes;

        public AssignmentResult(DeliveryAgent agent, int delayMinutes) {
            this.agent = agent;
            this.delayMinutes = delayMinutes;
        }
    }
}
