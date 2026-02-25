package com.cloudcost.optimizer.service;

import com.cloudcost.optimizer.model.VirtualMachine;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExportService {

    /**
     * Export VM data to Excel (XLSX)
     */
    public byte[] exportToExcel(List<VirtualMachine> vms) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("VM Analysis Report");
        
        // Create header style
        CellStyle headerStyle = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"VM ID", "CPU Usage (%)", "RAM Usage (%)", "Disk Usage (%)", 
                           "Cost/Hour ($)", "Cluster", "Recommendation", "Monthly Savings ($)"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Create data rows
        int rowNum = 1;
        for (VirtualMachine vm : vms) {
            Row row = sheet.createRow(rowNum++);
            
            row.createCell(0).setCellValue(vm.getVmId());
            row.createCell(1).setCellValue(vm.getCpuUsage());
            row.createCell(2).setCellValue(vm.getRamUsage());
            row.createCell(3).setCellValue(vm.getDiskUsage());
            row.createCell(4).setCellValue(vm.getCostPerHour());
            row.createCell(5).setCellValue(vm.getCluster() != null ? vm.getCluster() : 0);
            row.createCell(6).setCellValue(vm.getRecommendation() != null ? vm.getRecommendation() : "N/A");
            row.createCell(7).setCellValue(vm.getPotentialSavings() != null ? vm.getPotentialSavings() : 0.0);
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // Add summary at the bottom
        int summaryRow = rowNum + 2;
        Row totalRow = sheet.createRow(summaryRow);
        Cell summaryLabel = totalRow.createCell(0);
        summaryLabel.setCellValue("Total VMs:");
        Cell summaryValue = totalRow.createCell(1);
        summaryValue.setCellValue(vms.size());
        
        Row savingsRow = sheet.createRow(summaryRow + 1);
        Cell savingsLabel = savingsRow.createCell(0);
        savingsLabel.setCellValue("Total Potential Savings:");
        Cell savingsValue = savingsRow.createCell(1);
        double totalSavings = vms.stream()
            .mapToDouble(vm -> vm.getPotentialSavings() != null ? vm.getPotentialSavings() : 0.0)
            .sum();
        savingsValue.setCellValue("$" + String.format("%.2f", totalSavings));
        
        // Write to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return outputStream.toByteArray();
    }

    /**
     * Export VM data to PDF
     */
    public byte[] exportToPDF(List<VirtualMachine> vms) throws DocumentException {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        PdfWriter.getInstance(document, outputStream);
        document.open();
        
        // Add title
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(
            com.itextpdf.text.Font.FontFamily.HELVETICA, 20, com.itextpdf.text.Font.BOLD, BaseColor.DARK_GRAY);
        Paragraph title = new Paragraph("Cloud Cost Optimization Report", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);
        
        // Add generation date
        com.itextpdf.text.Font dateFont = new com.itextpdf.text.Font(
            com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.NORMAL, BaseColor.GRAY);
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Paragraph date = new Paragraph("Generated: " + dateTime, dateFont);
        date.setAlignment(Element.ALIGN_CENTER);
        date.setSpacingAfter(20);
        document.add(date);
        
        // Add summary statistics
        com.itextpdf.text.Font summaryFont = new com.itextpdf.text.Font(
            com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
        Paragraph summary = new Paragraph("Summary Statistics", summaryFont);
        summary.setSpacingAfter(10);
        document.add(summary);
        
        double totalSavings = vms.stream()
            .mapToDouble(vm -> vm.getPotentialSavings() != null ? vm.getPotentialSavings() : 0.0)
            .sum();
        
        long underUtilized = vms.stream()
            .filter(vm -> vm.getRecommendation() != null && vm.getRecommendation().contains("Under"))
            .count();
        
        long overUtilized = vms.stream()
            .filter(vm -> vm.getRecommendation() != null && vm.getRecommendation().contains("Over"))
            .count();
        
        com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(
            com.itextpdf.text.Font.FontFamily.HELVETICA, 10);
        document.add(new Paragraph("Total VMs Analyzed: " + vms.size(), normalFont));
        document.add(new Paragraph("Under-Utilized VMs: " + underUtilized, normalFont));
        document.add(new Paragraph("Over-Utilized VMs: " + overUtilized, normalFont));
        document.add(new Paragraph("Total Potential Monthly Savings: $" + String.format("%.2f", totalSavings), normalFont));
        document.add(new Paragraph(" "));
        
        // Create table
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        
        // Table headers
        com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(
            com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD, BaseColor.WHITE);
        String[] headers = {"VM ID", "CPU %", "RAM %", "Disk %", "Cost/Hr", "Cluster", "Recommendation", "Savings"};
        
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(BaseColor.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }
        
        // Table data
        com.itextpdf.text.Font dataFont = new com.itextpdf.text.Font(
            com.itextpdf.text.Font.FontFamily.HELVETICA, 9);
        for (VirtualMachine vm : vms) {
            table.addCell(new Phrase(vm.getVmId(), dataFont));
            table.addCell(new Phrase(String.format("%.1f", vm.getCpuUsage()), dataFont));
            table.addCell(new Phrase(String.format("%.1f", vm.getRamUsage()), dataFont));
            table.addCell(new Phrase(String.format("%.1f", vm.getDiskUsage()), dataFont));
            table.addCell(new Phrase(String.format("$%.2f", vm.getCostPerHour()), dataFont));
            table.addCell(new Phrase(String.valueOf(vm.getCluster() != null ? vm.getCluster() : 0), dataFont));
            
            String rec = vm.getRecommendation() != null ? vm.getRecommendation() : "N/A";
            if (rec.length() > 30) rec = rec.substring(0, 30) + "...";
            table.addCell(new Phrase(rec, dataFont));
            
            table.addCell(new Phrase(String.format("$%.2f", vm.getPotentialSavings() != null ? vm.getPotentialSavings() : 0.0), dataFont));
        }
        
        document.add(table);
        
        // Add footer
        document.add(new Paragraph(" "));
        com.itextpdf.text.Font footerFont = new com.itextpdf.text.Font(
            com.itextpdf.text.Font.FontFamily.HELVETICA, 8, com.itextpdf.text.Font.ITALIC, BaseColor.GRAY);
        Paragraph footer = new Paragraph("This report was automatically generated by the AI-Based Cloud Cost Optimization System", footerFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
        
        document.close();
        
        return outputStream.toByteArray();
    }
}