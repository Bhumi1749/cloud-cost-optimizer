package com.cloudcost.optimizer.controller;

import com.cloudcost.optimizer.model.VirtualMachine;
import com.cloudcost.optimizer.model.DecisionScenario;
import com.cloudcost.optimizer.service.VMAnalysisService;
import com.cloudcost.optimizer.service.DecisionImpactService;
import com.cloudcost.optimizer.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vms")
@CrossOrigin(origins = "*")
public class VMController {

    @Autowired
    private VMAnalysisService vmAnalysisService;
    
    @Autowired
    private DecisionImpactService decisionImpactService;
    
    @Autowired
    private ExportService exportService;

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeVMs(@RequestParam String filePath) {
        try {
            List<VirtualMachine> vms = vmAnalysisService.analyzeVMs(filePath);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Analysis completed successfully");
            response.put("totalVMs", vms.size());
            response.put("data", vms);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<VirtualMachine>> getAllVMs() {
        List<VirtualMachine> vms = vmAnalysisService.getAllVMs();
        return ResponseEntity.ok(vms);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearVMs() {
        vmAnalysisService.clearAllVMs();
        Map<String, String> response = new HashMap<>();
        response.put("message", "All VMs cleared successfully");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/simulate")
    public ResponseEntity<?> simulateDecision(@RequestParam String vmId, 
                                              @RequestParam String actionType) {
        try {
            // Find the VM
            VirtualMachine vm = vmAnalysisService.getAllVMs().stream()
                .filter(v -> v.getVmId().equals(vmId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("VM not found: " + vmId));
            
            // Simulate the decision
            DecisionScenario scenario = decisionImpactService.simulateDecision(vm, actionType);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("scenario", scenario);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportToExcel() {
        try {
            List<VirtualMachine> vms = vmAnalysisService.getAllVMs();
            byte[] excelData = exportService.exportToExcel(vms);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "vm-analysis-report.xlsx");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportToPDF() {
        try {
            List<VirtualMachine> vms = vmAnalysisService.getAllVMs();
            byte[] pdfData = exportService.exportToPDF(vms);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "vm-analysis-report.pdf");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(pdfData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/upload")
    public ResponseEntity<?> uploadCSV(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                throw new RuntimeException("Please select a file to upload");
            }
            
            String filename = file.getOriginalFilename();
            if (filename == null || !filename.endsWith(".csv")) {
                throw new RuntimeException("Only CSV files are allowed");
            }
            
            // Read file content
            String csvContent = new String(file.getBytes());
            
            // Analyze uploaded file
            List<VirtualMachine> vms = vmAnalysisService.analyzeUploadedFile(csvContent);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "File uploaded and analyzed successfully");
            response.put("totalVMs", vms.size());
            response.put("data", vms);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}