package com.example.pdfgeneration;

import com.example.pdfgeneration.model.Invoice;
import com.example.pdfgeneration.service.PDFService;
import com.example.pdfgeneration.util.PDFUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PDFServiceTest {

    @InjectMocks
    private PDFService pdfService;

    @Mock
    private PDFUtil pdfUtil;

    private Invoice invoice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        invoice = new Invoice();
        invoice.setBuyer("Vedant Computers");
        invoice.setSeller("XYZ Pvt. Ltd.");
    }

    @Test
    void testGeneratePDF_whenPDFExists() {
        String fileName = "pdf-storage/Vedant Computers-XYZ Pvt. Ltd..pdf";
        File existingFile = new File(fileName);
        // Create the file to simulate existing PDF
        try {
            existingFile.createNewFile();
        } catch (Exception e) {
            fail("Failed to create mock PDF file");
        }

        File result = pdfService.generatePDF(invoice);

        // Verify that the existing file was returned
        assertEquals(existingFile, result);
        existingFile.delete(); // Cleanup
    }

    @Test
    void testGeneratePDF_whenPDFDoesNotExist() {
        String fileName = "pdf-storage/Vedant Computers-XYZ Pvt. Ltd..pdf";
        File nonExistingFile = new File(fileName);
        // Make sure the file doesn't exist
        nonExistingFile.delete();

        // Mock the PDFUtil to return the new file when createPDF is called
        when(pdfUtil.createPDF(invoice, fileName)).thenReturn(nonExistingFile);

        File result = pdfService.generatePDF(invoice);

        // Verify that the createPDF method was called
        verify(pdfUtil).createPDF(invoice, fileName);
        // Verify that the new file was returned
        assertEquals(nonExistingFile, result);
    }
}
