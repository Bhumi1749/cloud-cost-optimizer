package com.cloudcost.optimizer.service;

import com.cloudcost.optimizer.model.VirtualMachine;
import com.cloudcost.optimizer.repository.VirtualMachineRepository;
import com.cloudcost.optimizer.util.CSVReaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VMAnalysisService {

    @Autowired
    private CSVReaderUtil csvReaderUtil;

    @Autowired
    private MLService mlService;

    @Autowired
    private VirtualMachineRepository vmRepository;

    public List<VirtualMachine> analyzeVMs(String csvFilePath) throws Exception {
        // Step 1: Read CSV data
        List<VirtualMachine> vmList = csvReaderUtil.readVMData(csvFilePath);

        // Step 2: Apply ML clustering
        mlService.clusterVMs(vmList);

        // Step 3: Save to database
        vmRepository.saveAll(vmList);

        return vmList;
    }

    public List<VirtualMachine> getAllVMs() {
        return vmRepository.findAll();
    }

    public void clearAllVMs() {
        vmRepository.deleteAll();
    }
    
    public List<VirtualMachine> analyzeUploadedFile(String csvContent) throws Exception {
        // Parse CSV content
        List<VirtualMachine> vmList = new ArrayList<>();
        String[] lines = csvContent.split("\n");
        
        // Skip header (first line)
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;
            
            String[] fields = line.split(",");
            if (fields.length < 5) continue;
            
            VirtualMachine vm = new VirtualMachine();
            vm.setVmId(fields[0].trim());
            vm.setCpuUsage(Double.parseDouble(fields[1].trim()));
            vm.setRamUsage(Double.parseDouble(fields[2].trim()));
            vm.setDiskUsage(Double.parseDouble(fields[3].trim()));
            vm.setCostPerHour(Double.parseDouble(fields[4].trim()));
            
            vmList.add(vm);
        }
        
        // Apply ML clustering
        mlService.clusterVMs(vmList);
        
        // Save to database
        vmRepository.saveAll(vmList);
        
        return vmList;
    }
}