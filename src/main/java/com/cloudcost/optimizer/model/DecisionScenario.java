package com.cloudcost.optimizer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "decision_scenarios")
public class DecisionScenario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "vm_id")
    private String vmId;
    
    @Column(name = "current_cost")
    private Double currentCost;
    
    @Column(name = "current_cpu")
    private Double currentCpu;
    
    @Column(name = "current_ram")
    private Double currentRam;
    
    @Column(name = "action_type")
    private String actionType; // DOWNSCALE, UPSCALE, SCHEDULE, TERMINATE
    
    @Column(name = "predicted_cost")
    private Double predictedCost;
    
    @Column(name = "predicted_cpu")
    private Double predictedCpu;
    
    @Column(name = "predicted_ram")
    private Double predictedRam;
    
    @Column(name = "cost_impact")
    private Double costImpact; // Savings (positive) or additional cost (negative)
    
    @Column(name = "performance_impact")
    private Double performanceImpact; // % change in performance
    
    @Column(name = "risk_level")
    private String riskLevel; // LOW, MEDIUM, HIGH
    
    @Column(name = "risk_score")
    private Double riskScore; // 0-100
    
    @Column(name = "recommendation")
    private String recommendation; // SAFE_TO_APPLY, APPLY_WITH_CAUTION, NOT_RECOMMENDED
    
    @Column(name = "reasoning", length = 1000)
    private String reasoning; // Explainable AI reasoning
    
    @Column(name = "confidence_score")
    private Double confidenceScore; // 0-100
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "simulated")
    private Boolean simulated; // true if simulation, false if actually applied
    
    // Constructors
    public DecisionScenario() {
        this.createdAt = LocalDateTime.now();
        this.simulated = true;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getVmId() {
        return vmId;
    }
    
    public void setVmId(String vmId) {
        this.vmId = vmId;
    }
    
    public Double getCurrentCost() {
        return currentCost;
    }
    
    public void setCurrentCost(Double currentCost) {
        this.currentCost = currentCost;
    }
    
    public Double getCurrentCpu() {
        return currentCpu;
    }
    
    public void setCurrentCpu(Double currentCpu) {
        this.currentCpu = currentCpu;
    }
    
    public Double getCurrentRam() {
        return currentRam;
    }
    
    public void setCurrentRam(Double currentRam) {
        this.currentRam = currentRam;
    }
    
    public String getActionType() {
        return actionType;
    }
    
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
    
    public Double getPredictedCost() {
        return predictedCost;
    }
    
    public void setPredictedCost(Double predictedCost) {
        this.predictedCost = predictedCost;
    }
    
    public Double getPredictedCpu() {
        return predictedCpu;
    }
    
    public void setPredictedCpu(Double predictedCpu) {
        this.predictedCpu = predictedCpu;
    }
    
    public Double getPredictedRam() {
        return predictedRam;
    }
    
    public void setPredictedRam(Double predictedRam) {
        this.predictedRam = predictedRam;
    }
    
    public Double getCostImpact() {
        return costImpact;
    }
    
    public void setCostImpact(Double costImpact) {
        this.costImpact = costImpact;
    }
    
    public Double getPerformanceImpact() {
        return performanceImpact;
    }
    
    public void setPerformanceImpact(Double performanceImpact) {
        this.performanceImpact = performanceImpact;
    }
    
    public String getRiskLevel() {
        return riskLevel;
    }
    
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
    
    public Double getRiskScore() {
        return riskScore;
    }
    
    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }
    
    public String getRecommendation() {
        return recommendation;
    }
    
    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
    
    public String getReasoning() {
        return reasoning;
    }
    
    public void setReasoning(String reasoning) {
        this.reasoning = reasoning;
    }
    
    public Double getConfidenceScore() {
        return confidenceScore;
    }
    
    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Boolean getSimulated() {
        return simulated;
    }
    
    public void setSimulated(Boolean simulated) {
        this.simulated = simulated;
    }
}