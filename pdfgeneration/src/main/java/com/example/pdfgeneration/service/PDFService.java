package com.example.pdfgeneration.service;

import com.example.pdfgeneration.exception.PDFGenerationException;
import com.example.pdfgeneration.model.Invoice;
import com.example.pdfgeneration.util.PDFUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class PDFService {
	
	// Directory where generated PDF files will be stored
    private static final String PDF_STORAGE = "pdf-storage/";

    @Autowired
    private PDFUtil pdfUtil;

    public File generatePDF(Invoice invoice) {
        String fileName = PDF_STORAGE + invoice.getBuyer() + "-" + invoice.getSeller() + ".pdf";
        File file = new File(fileName);

        try {
        	// Check if the PDF file already exists
            if (file.exists()) {
                return file;
            } else {
            	// If the file does not exist, new PDF file is created using the PDFUtil
                return pdfUtil.createPDF(invoice, fileName);
            }
        } catch (Exception e) {
            throw new PDFGenerationException("Failed to generate PDF for invoice: " + invoice, e);
        }
    }
}
