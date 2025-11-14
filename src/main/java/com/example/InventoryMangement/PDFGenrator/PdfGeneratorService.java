package com.example.InventoryMangement.PDFGenrator;

import com.example.InventoryMangement.Entity.Invoice;
import com.example.InventoryMangement.Entity.InvoiceItem;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;

@Service
public class PdfGeneratorService {

    public String generateInvoicePdf(Invoice invoice) {
        try {
            // ✅ Create folder if it doesn't exist
            File folder = new File("invoices");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // ✅ File path (cross-platform)
            String filePath = "invoices/" + invoice.getInvoiceNo() + ".pdf";

            // ✅ Create Document
            Document document = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // ✅ Title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Invoice - " + invoice.getInvoiceNo(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // spacer line

            // ✅ Customer Info
            document.add(new Paragraph("Customer: " + invoice.getCustomer().getName()));
            document.add(new Paragraph("Date: " + invoice.getDate()));
            document.add(new Paragraph("Status: " + invoice.getStatus()));
            document.add(new Paragraph(" "));
            document.add(new LineSeparator());

            // ✅ Table Header
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            addTableHeader(table, "Product");
            addTableHeader(table, "Quantity");
            addTableHeader(table, "Price (INR)");
            addTableHeader(table, "Total (INR)");

            // ✅ Table Rows
            double grandTotal = 0.0;
            for (InvoiceItem item : invoice.getItems()) {
                double lineTotal = item.getQty() * item.getPrice() - item.getDiscount();
                grandTotal += lineTotal;

                table.addCell(item.getItemName());
                table.addCell(String.valueOf(item.getQty()));
                table.addCell(String.format("%.2f", item.getPrice()));
                table.addCell(String.format("%.2f", lineTotal));
            }

            document.add(table);

            // ✅ Total Summary
            Paragraph totalPara = new Paragraph("Total Amount: ₹" + String.format("%.2f", grandTotal),
                    new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
            totalPara.setAlignment(Element.ALIGN_RIGHT);
            document.add(totalPara);

            document.add(new Paragraph("\nThank you for your purchase!", new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC)));

            // ✅ Close document
            document.close();

            System.out.println("✅ PDF generated successfully: " + new File(filePath).getAbsolutePath());
            return filePath;

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage());
        }
    }

    // 🔹 Helper method for header styling
    private void addTableHeader(PdfPTable table, String headerText) {
        Font headFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        PdfPCell header = new PdfPCell(new Phrase(headerText, headFont));
        header.setBackgroundColor(BaseColor.DARK_GRAY);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setPadding(8);
        table.addCell(header);
    }
}


