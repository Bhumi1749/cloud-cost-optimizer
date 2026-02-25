package com.cloudcost.optimizer.model;

import jakarta.persistence.*;

@Entity
@Table(name = "virtual_machines")
public class VirtualMachine {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "vm_id")
    private String vmId;
    
    @Column(name = "cpu_usage")
    private Double cpuUsage;
    
    @Column(name = "ram_usage")
    private Double ramUsage;
    
    @Column(name = "disk_usage")
    private Double diskUsage;
    
    @Column(name = "cost_per_hour")
    private Double costPerHour;
    
    @Column(name = "cluster")
    private Integer cluster;
    
    @Column(name = "recommendation")
    private String recommendation;
    
    @Column(name = "potential_savings")
    private Double potentialSavings;
    
    // Empty constructor (required by JPA)
    public VirtualMachine() {
    }
    
    // Constructor with parameters
    public VirtualMachine(String vmId, Double cpuUsage, Double ramUsage, 
                         Double diskUsage, Double costPerHour) {
        this.vmId = vmId;
        this.cpuUsage = cpuUsage;
        this.ramUsage = ramUsage;
        this.diskUsage = diskUsage;
        this.costPerHour = costPerHour;
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
    
    public Double getCpuUsage() {
        return cpuUsage;
    }
    
    public void setCpuUsage(Double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }
    
    public Double getRamUsage() {
        return ramUsage;
    }
    
    public void setRamUsage(Double ramUsage) {
        this.ramUsage = ramUsage;
    }
    
    public Double getDiskUsage() {
        return diskUsage;
    }
    
    public void setDiskUsage(Double diskUsage) {
        this.diskUsage = diskUsage;
    }
    
    public Double getCostPerHour() {
        return costPerHour;
    }
    
    public void setCostPerHour(Double costPerHour) {
        this.costPerHour = costPerHour;
    }
    
    public Integer getCluster() {
        return cluster;
    }
    
    public void setCluster(Integer cluster) {
        this.cluster = cluster;
    }
    
    public String getRecommendation() {
        return recommendation;
    }
    
    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
    
    public Double getPotentialSavings() {
        return potentialSavings;
    }
    
    public void setPotentialSavings(Double potentialSavings) {
        this.potentialSavings = potentialSavings;
    }
}