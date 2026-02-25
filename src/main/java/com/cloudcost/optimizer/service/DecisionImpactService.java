package com.cloudcost.optimizer.service;

import com.cloudcost.optimizer.model.DecisionScenario;
import com.cloudcost.optimizer.model.VirtualMachine;
import com.cloudcost.optimizer.repository.DecisionScenarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DecisionImpactService {

    @Autowired
    private DecisionScenarioRepository scenarioRepository;

    /**
     * Simulate the impact of a decision on a VM
     * @param vm - The virtual machine
     * @param actionType - DOWNSCALE, UPSCALE, SCHEDULE, TERMINATE
     * @return DecisionScenario with predicted impact
     */
    public DecisionScenario simulateDecision(VirtualMachine vm, String actionType) {
        DecisionScenario scenario = new DecisionScenario();
        
        // Capture current state
        scenario.setVmId(vm.getVmId());
        scenario.setCurrentCost(vm.getCostPerHour());
        scenario.setCurrentCpu(vm.getCpuUsage());
        scenario.setCurrentRam(vm.getRamUsage());
        scenario.setActionType(actionType);
        
        // Simulate based on action type
        switch (actionType.toUpperCase()) {
            case "DOWNSCALE":
                simulateDownscale(scenario, vm);
                break;
            case "UPSCALE":
                simulateUpscale(scenario, vm);
                break;
            case "SCHEDULE":
                simulateSchedule(scenario, vm);
                break;
            case "TERMINATE":
                simulateTerminate(scenario, vm);
                break;
            default:
                throw new IllegalArgumentException("Invalid action type: " + actionType);
        }
        
        // Calculate overall impact
        calculateImpact(scenario);
        
        // Assess risk
        assessRisk(scenario, vm);
        
        // Generate recommendation
        generateRecommendation(scenario);
        
        // Generate explainable reasoning
        generateReasoning(scenario, vm);
        
        // Save scenario
        return scenarioRepository.save(scenario);
    }

    /**
     * DOWNSCALE: Reduce VM size (50% resources, 50% cost)
     */
    private void simulateDownscale(DecisionScenario scenario, VirtualMachine vm) {
        // Predict new resource allocation (50% reduction)
        scenario.setPredictedCpu(vm.getCpuUsage() * 0.5);
        scenario.setPredictedRam(vm.getRamUsage() * 0.5);
        scenario.setPredictedCost(vm.getCostPerHour() * 0.5);
        
        // Performance impact: if current usage is low, minimal impact
        // if current usage is high, significant impact
        double avgUsage = (vm.getCpuUsage() + vm.getRamUsage()) / 2;
        if (avgUsage < 30) {
            scenario.setPerformanceImpact(-5.0); // Minimal impact
        } else if (avgUsage < 60) {
            scenario.setPerformanceImpact(-25.0); // Moderate impact
        } else {
            scenario.setPerformanceImpact(-60.0); // High impact
        }
    }

    /**
     * UPSCALE: Increase VM size (200% resources, 200% cost)
     */
    private void simulateUpscale(DecisionScenario scenario, VirtualMachine vm) {
        scenario.setPredictedCpu(vm.getCpuUsage() * 2.0);
        scenario.setPredictedRam(vm.getRamUsage() * 2.0);
        scenario.setPredictedCost(vm.getCostPerHour() * 2.0);
        
        // Performance improvement
        double avgUsage = (vm.getCpuUsage() + vm.getRamUsage()) / 2;
        if (avgUsage > 75) {
            scenario.setPerformanceImpact(40.0); // High improvement
        } else if (avgUsage > 50) {
            scenario.setPerformanceImpact(15.0); // Moderate improvement
        } else {
            scenario.setPerformanceImpact(5.0); // Minimal improvement (already optimal)
        }
    }

    /**
     * SCHEDULE: Run VM only during business hours (12h/day = 50% cost)
     */
    private void simulateSchedule(DecisionScenario scenario, VirtualMachine vm) {
        scenario.setPredictedCpu(vm.getCpuUsage());
        scenario.setPredictedRam(vm.getRamUsage());
        scenario.setPredictedCost(vm.getCostPerHour() * 0.5); // 50% time = 50% cost
        
        // Performance impact depends on workload type
        // For dev/test: minimal impact
        // For production: high risk
        scenario.setPerformanceImpact(-10.0); // Assume some downtime impact
    }

    /**
     * TERMINATE: Shut down VM completely (0% cost, 100% performance loss)
     */
    private void simulateTerminate(DecisionScenario scenario, VirtualMachine vm) {
        scenario.setPredictedCpu(0.0);
        scenario.setPredictedRam(0.0);
        scenario.setPredictedCost(0.0);
        scenario.setPerformanceImpact(-100.0); // Complete shutdown
    }

    /**
     * Calculate cost and performance impact
     */
    private void calculateImpact(DecisionScenario scenario) {
        // Cost impact (positive = savings, negative = additional cost)
        double costSavingsPerHour = scenario.getCurrentCost() - scenario.getPredictedCost();
        double monthlySavings = costSavingsPerHour * 730; // 730 hours/month
        scenario.setCostImpact(monthlySavings);
    }

    /**
     * Assess risk level based on multiple factors
     */
    private void assessRisk(DecisionScenario scenario, VirtualMachine vm) {
        double riskScore = 0.0;
        
        // Factor 1: Current utilization (higher utilization = higher risk to downscale)
        double avgUsage = (vm.getCpuUsage() + vm.getRamUsage()) / 2;
        if (scenario.getActionType().equals("DOWNSCALE")) {
            if (avgUsage > 70) {
                riskScore += 40; // High risk
            } else if (avgUsage > 40) {
                riskScore += 20; // Medium risk
            } else {
                riskScore += 5; // Low risk
            }
        }
        
        // Factor 2: Performance impact (higher negative impact = higher risk)
        double perfImpact = Math.abs(scenario.getPerformanceImpact());
        if (perfImpact > 50) {
            riskScore += 35;
        } else if (perfImpact > 20) {
            riskScore += 15;
        } else {
            riskScore += 5;
        }
        
        // Factor 3: Cost impact vs performance tradeoff
        if (scenario.getCostImpact() < 0 && scenario.getPerformanceImpact() < 0) {
            riskScore += 20; // Increasing cost AND reducing performance = bad
        }
        
        // Set risk score and level
        scenario.setRiskScore(Math.min(riskScore, 100.0));
        
        if (riskScore < 30) {
            scenario.setRiskLevel("LOW");
        } else if (riskScore < 60) {
            scenario.setRiskLevel("MEDIUM");
        } else {
            scenario.setRiskLevel("HIGH");
        }
    }

    /**
     * Generate final recommendation
     */
    private void generateRecommendation(DecisionScenario scenario) {
        String riskLevel = scenario.getRiskLevel();
        double costImpact = scenario.getCostImpact();
        double perfImpact = scenario.getPerformanceImpact();
        
        // Calculate confidence (inverse of risk)
        double confidence = 100 - scenario.getRiskScore();
        scenario.setConfidenceScore(confidence);
        
        // Decision logic
        if (riskLevel.equals("LOW") && costImpact > 0) {
            scenario.setRecommendation("SAFE_TO_APPLY");
        } else if (riskLevel.equals("MEDIUM") && costImpact > 50) {
            scenario.setRecommendation("APPLY_WITH_CAUTION");
        } else if (riskLevel.equals("HIGH") || perfImpact < -50) {
            scenario.setRecommendation("NOT_RECOMMENDED");
        } else {
            scenario.setRecommendation("APPLY_WITH_CAUTION");
        }
    }

    /**
     * Generate human-readable reasoning (Explainable AI)
     */
    private void generateReasoning(DecisionScenario scenario, VirtualMachine vm) {
        StringBuilder reasoning = new StringBuilder();
        
        reasoning.append("Analysis for VM ").append(vm.getVmId()).append(": ");
        
        // Current state
        double avgUsage = (vm.getCpuUsage() + vm.getRamUsage()) / 2;
        reasoning.append("Current avg utilization: ").append(String.format("%.1f%%", avgUsage)).append(". ");
        
        // Action impact
        reasoning.append("Action '").append(scenario.getActionType()).append("' will ");
        if (scenario.getCostImpact() > 0) {
            reasoning.append("save $").append(String.format("%.2f", scenario.getCostImpact())).append("/month ");
        } else {
            reasoning.append("cost $").append(String.format("%.2f", Math.abs(scenario.getCostImpact()))).append("/month more ");
        }
        
        // Performance impact
        if (scenario.getPerformanceImpact() < 0) {
            reasoning.append("but may reduce performance by ").append(String.format("%.1f%%", Math.abs(scenario.getPerformanceImpact()))).append(". ");
        } else {
            reasoning.append("and improve performance by ").append(String.format("%.1f%%", scenario.getPerformanceImpact())).append(". ");
        }
        
        // Risk assessment
        reasoning.append("Risk level: ").append(scenario.getRiskLevel()).append(" (score: ").append(String.format("%.0f", scenario.getRiskScore())).append("/100). ");
        
        // Recommendation reasoning
        switch (scenario.getRecommendation()) {
            case "SAFE_TO_APPLY":
                reasoning.append("Low risk and good cost savings make this a safe choice.");
                break;
            case "APPLY_WITH_CAUTION":
                reasoning.append("Moderate risk - monitor performance after applying.");
                break;
            case "NOT_RECOMMENDED":
                reasoning.append("High risk of performance degradation. Consider alternatives.");
                break;
        }
        
        scenario.setReasoning(reasoning.toString());
    }
}