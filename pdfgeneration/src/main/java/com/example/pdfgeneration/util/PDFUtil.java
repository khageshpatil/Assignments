package com.example.pdfgeneration.util;

import com.example.pdfgeneration.exception.PDFGenerationException;
import com.example.pdfgeneration.model.Invoice;
import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

@Component
public class PDFUtil {

    private final TemplateEngine templateEngine;

    public PDFUtil(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public File createPDF(Invoice invoice, String fileName) {
        try {
            //To Create a thymeleaf and set the invoice variable
            Context context = new Context();
            context.setVariable("invoice", invoice);
            
            // Process the HTML template to generate the HTML content
            String htmlContent = templateEngine.process("invoice", context);

            // Create a new file 
            File pdfFile = new File(fileName);
            
            try (OutputStream outputStream = new FileOutputStream(pdfFile)) {
                // Convert the HTML content to PDF and write it to the output stream
                HtmlConverter.convertToPdf(htmlContent, outputStream);
            }

            // Return the generated PDF file
            return pdfFile;
        } catch (Exception e) {
            throw new PDFGenerationException("Error while creating PDF", e);
        }
    }
}