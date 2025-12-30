package manager;


import exception.AgentNotAvailableException;
import model.DeliveryAgent;
import model.Pincode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class AgentManager {
    private final Map<String, DeliveryAgent> agents = new ConcurrentHashMap<>();
    private final AtomicInteger agentCounter = new AtomicInteger(1);
    

    public DeliveryAgent createAgent(String name, String pincode) {
        return createAgent(name, Set.of(new Pincode(pincode)));
    }


    public DeliveryAgent createAgent(String name, Set<Pincode> pincodes) {
        String agentId = "AGT_" + agentCounter.getAndIncrement();
        DeliveryAgent agent = new DeliveryAgent(agentId, name, pincodes);
        agents.put(agentId, agent);
        System.out.println(" Created agent: " + agent);
        return agent;
    }

    public Optional<DeliveryAgent> getAgent(String agentId) {
        return Optional.ofNullable(agents.get(agentId));
    }

    public List<DeliveryAgent> getAgentsForPincode(Pincode pincode) {
        return agents.values().stream()
                .filter(agent -> agent.canServicePincode(pincode))
                .toList();
    }

    public List<DeliveryAgent> getAllAgents() {
        return new ArrayList<>(agents.values());
    }

    public void addAgentPincode(String agentId, String pincode) {
        DeliveryAgent agent = agents.get(agentId);
        if (agent != null) {
            agent.addServiceablePincode(new Pincode(pincode));
            System.out.println("Added pincode " + pincode + " to agent " + agentId);
        } else {
            throw new AgentNotAvailableException("Agent not found: " + agentId);
        }
    }

    public void removeAgent(String agentId) {
        DeliveryAgent removed = agents.remove(agentId);
        if (removed != null) {
            System.out.println("Removed agent: " + removed.getName());
        }
    }
}