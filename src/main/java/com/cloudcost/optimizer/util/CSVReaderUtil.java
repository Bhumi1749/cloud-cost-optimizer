package com.cloudcost.optimizer.util;

import com.cloudcost.optimizer.model.VirtualMachine;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CSVReaderUtil {

    public List<VirtualMachine> readVMData(String filePath) throws IOException, CsvException {
        List<VirtualMachine> vmList = new ArrayList<>();
        
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> records = reader.readAll();
            
            // Skip header row (first row)
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);
                
                VirtualMachine vm = new VirtualMachine();
                vm.setVmId(record[0]);
                vm.setCpuUsage(Double.parseDouble(record[1]));
                vm.setRamUsage(Double.parseDouble(record[2]));
                vm.setDiskUsage(Double.parseDouble(record[3]));
                vm.setCostPerHour(Double.parseDouble(record[4]));
                
                vmList.add(vm);
            }
        }
        
        return vmList;
    }
}