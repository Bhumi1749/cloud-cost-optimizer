package com.cloudcost.optimizer.repository;

import com.cloudcost.optimizer.model.DecisionScenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DecisionScenarioRepository extends JpaRepository<DecisionScenario, Long> {
    
    // Find all scenarios for a specific VM
    List<DecisionScenario> findByVmId(String vmId);
    
    // Find all simulated scenarios (not actually applied)
    List<DecisionScenario> findBySimulated(Boolean simulated);
    
    // Find scenarios by action type
    List<DecisionScenario> findByActionType(String actionType);
}