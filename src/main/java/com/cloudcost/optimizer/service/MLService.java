package com.cloudcost.optimizer.service;

import com.cloudcost.optimizer.model.VirtualMachine;
import org.springframework.stereotype.Service;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;

@Service
public class MLService {

    public void clusterVMs(List<VirtualMachine> vmList) throws Exception {
        // Create attributes for clustering
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("cpuUsage"));
        attributes.add(new Attribute("ramUsage"));
        attributes.add(new Attribute("diskUsage"));
        
        // Create dataset
        Instances dataset = new Instances("VMData", attributes, vmList.size());
        
        // Add data to dataset
        for (VirtualMachine vm : vmList) {
            double[] values = new double[3];
            values[0] = vm.getCpuUsage();
            values[1] = vm.getRamUsage();
            values[2] = vm.getDiskUsage();
            dataset.add(new DenseInstance(1.0, values));
        }
        
        // Apply K-Means clustering (3 clusters)
        SimpleKMeans kMeans = new SimpleKMeans();
        kMeans.setNumClusters(3);
        kMeans.buildClusterer(dataset);
        
        // Assign clusters and recommendations
        for (int i = 0; i < vmList.size(); i++) {
            int cluster = kMeans.clusterInstance(dataset.instance(i));
            VirtualMachine vm = vmList.get(i);
            vm.setCluster(cluster);
            
            // Generate recommendation based on cluster
            String recommendation = getRecommendation(vm);
            vm.setRecommendation(recommendation);
            
            // Calculate potential savings
            double savings = calculateSavings(vm, recommendation);
            vm.setPotentialSavings(savings);
        }
    }
    
    private String getRecommendation(VirtualMachine vm) {
        double avgUsage = (vm.getCpuUsage() + vm.getRamUsage()) / 2;
        
        if (avgUsage < 30) {
            return "Under-utilized: Consider downsizing or termination";
        } else if (avgUsage > 75) {
            return "Over-utilized: Consider upgrading";
        } else {
            return "Optimal: Keep current configuration";
        }
    }
    
    private double calculateSavings(VirtualMachine vm, String recommendation) {
        if (recommendation.contains("downsizing")) {
            return vm.getCostPerHour() * 0.5 * 730; // 50% savings per month
        } else if (recommendation.contains("termination")) {
            return vm.getCostPerHour() * 730; // Full cost savings per month
        }
        return 0.0;
    }
}