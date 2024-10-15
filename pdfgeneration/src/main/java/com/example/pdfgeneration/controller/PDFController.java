package com.example.pdfgeneration.controller; 

import com.example.pdfgeneration.model.Invoice;
import com.example.pdfgeneration.service.PDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/api/pdf")
public class PDFController {

    @Autowired
    private PDFService pdfService;

    @PostMapping("/generate")
    public ResponseEntity<FileSystemResource> generatePDF(@RequestBody Invoice invoice) {
    	 // To Generate the PDF file using the provided invoice data
        File pdfFile = pdfService.generatePDF(invoice);

        FileSystemResource resource = new FileSystemResource(pdfFile);
        
     // response headers for the file download
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + pdfFile.getName());
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);//set content type of file
        
        // To Return a ResponseEntity with the PDF file as the body and the headers

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}