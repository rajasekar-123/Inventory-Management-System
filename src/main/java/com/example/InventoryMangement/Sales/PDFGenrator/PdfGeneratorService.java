package com.example.InventoryMangement.Sales.PDFGenrator;

import com.example.InventoryMangement.Sales.Entity.Invoice;
import com.example.InventoryMangement.Sales.Entity.InvoiceItem;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class PdfGeneratorService {

    private static final String OUTPUT_DIR = "invoices";

    public String generateInvoicePdf(Invoice invoice) {
        try {
            File folder = new File(OUTPUT_DIR);
            if (!folder.exists()) folder.mkdirs();

            String filePath = OUTPUT_DIR + "/INV-" + invoice.getInvoiceNo() + ".pdf";

            Document document = new Document(PageSize.A4, 36, 36, 54, 36);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            Font bold = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
            Font normal = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);

            // 🧾 HEADER
            PdfPTable header = new PdfPTable(new float[]{3, 1});
            header.setWidthPercentage(100);

            PdfPTable left = new PdfPTable(1);

// Load the logo from resources/static folder


            left.addCell(cell("StockZen", bold, Rectangle.NO_BORDER));
            left.addCell(cell("📞 9585594009", normal, Rectangle.NO_BORDER));
            left.addCell(cell("Tamil Nadu", normal, Rectangle.NO_BORDER));

            header.addCell(wrap(left));
            header.addCell(cell(invoice.getInvoiceNo(), bold, Rectangle.NO_BORDER));

            document.add(header);
            document.add(Chunk.NEWLINE);

            // 🧍 BILL TO
            PdfPTable meta = new PdfPTable(new float[]{2, 1});
            meta.setWidthPercentage(100);

            PdfPTable billTo = new PdfPTable(1);
            billTo.addCell(cell("Bill To:", bold, Rectangle.NO_BORDER));
            billTo.addCell(cell(invoice.getCustomer().getName(), normal, Rectangle.NO_BORDER));
            billTo.addCell(cell("📞 " + invoice.getCustomer().getPhone(), normal, Rectangle.NO_BORDER));
            meta.addCell(wrap(billTo));

            PdfPTable info = new PdfPTable(1);
            info.addCell(cell("Invoice: " + invoice.getInvoiceNo(), bold, Rectangle.NO_BORDER));
            info.addCell(cell("Date: " +
                    new SimpleDateFormat("dd-MM-yyyy").format(invoice.getDate()), normal, Rectangle.NO_BORDER));

            meta.addCell(wrap(info));

            document.add(meta);
            document.add(Chunk.NEWLINE);

            // 📦 ITEMS TABLE
            PdfPTable table = new PdfPTable(new float[]{0.5f, 2f, 1f, 1f, 1f, 1f});
            table.setWidthPercentage(100);

            addHeader(table, "SR");
            addHeader(table, "Item");
            addHeader(table, "Qty");
            addHeader(table, "Price");
            addHeader(table, "GST%");
            addHeader(table, "Amount");

            BigDecimal subtotal = BigDecimal.ZERO;
            BigDecimal gstTotal = BigDecimal.ZERO;

            int sr = 1;

            for (InvoiceItem item : invoice.getItems()) {

                BigDecimal amount = BigDecimal.valueOf(item.getTotalWithGst());
                BigDecimal gst = BigDecimal.valueOf(item.getGstAmount());

                subtotal = subtotal.add(BigDecimal.valueOf(item.getTaxableAmount()));
                gstTotal = gstTotal.add(gst);

                table.addCell(cell(sr++ + "", normal, Rectangle.BOX));
                table.addCell(cell(item.getItemName(), normal, Rectangle.BOX));
                table.addCell(cell(item.getQty() + "", normal, Rectangle.BOX));
                table.addCell(cell(String.format("%.2f", item.getPrice()), normal, Rectangle.BOX));
                table.addCell(cell(item.getGstPercentage() + "%", normal, Rectangle.BOX));
                table.addCell(cell(String.format("%.2f", amount.doubleValue()), normal, Rectangle.BOX));
            }

            document.add(table);
            document.add(Chunk.NEWLINE);

            // 💰 TOTAL SECTION
            PdfPTable totals = new PdfPTable(2);
            totals.setWidthPercentage(100);

            // Left: Tax split
            PdfPTable tax = new PdfPTable(1);
            tax.addCell(cell("Tax Details", bold, Rectangle.NO_BORDER));
            tax.addCell(cell("SGST (50%): " + String.format("%.2f", gstTotal.doubleValue() / 2), normal, Rectangle.NO_BORDER));
            tax.addCell(cell("CGST (50%): " + String.format("%.2f", gstTotal.doubleValue() / 2), normal, Rectangle.NO_BORDER));
            tax.addCell(cell("Total GST: " + String.format("%.2f", gstTotal.doubleValue()), normal, Rectangle.NO_BORDER));

            totals.addCell(wrap(tax));

            // Right: Summary
            BigDecimal total = subtotal.add(gstTotal);
            BigDecimal rounded = total.setScale(0, RoundingMode.HALF_UP);
            BigDecimal roundOff = rounded.subtract(total);

            PdfPTable summary = new PdfPTable(2);
            summary.setWidths(new float[]{1, 1});

            summary.addCell(cell("Subtotal", normal, Rectangle.NO_BORDER));
            summary.addCell(cell(String.format("%.2f", subtotal.doubleValue()), normal, Rectangle.NO_BORDER));

            summary.addCell(cell("GST", normal, Rectangle.NO_BORDER));
            summary.addCell(cell(String.format("%.2f", gstTotal.doubleValue()), normal, Rectangle.NO_BORDER));

            summary.addCell(cell("Round Off", normal, Rectangle.NO_BORDER));
            summary.addCell(cell(String.format("%.2f", roundOff.doubleValue()), normal, Rectangle.NO_BORDER));

            summary.addCell(cell("Grand Total", bold, Rectangle.NO_BORDER));
            summary.addCell(cell(String.format("%.2f", rounded.doubleValue()), bold, Rectangle.NO_BORDER));

            totals.addCell(wrap(summary));

            document.add(totals);
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Thanks for doing business with us!", normal));

            document.close();
            return filePath;

        } catch (Exception e) {
            throw new RuntimeException("PDF ERROR: " + e.getMessage());
        }
    }

    private PdfPCell cell(String text, Font font, int border) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setBorder(border);
        c.setPadding(5);
        return c;
    }

    private PdfPCell wrap(PdfPTable table) {
        PdfPCell c = new PdfPCell(table);
        c.setBorder(Rectangle.NO_BORDER);
        return c;
    }

    private void addHeader(PdfPTable table, String title) {
        Font head = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
        PdfPCell h = new PdfPCell(new Phrase(title, head));
        h.setBackgroundColor(new BaseColor(70, 130, 180));
        h.setHorizontalAlignment(Element.ALIGN_CENTER);
        h.setPadding(6);
        table.addCell(h);
    }
}
