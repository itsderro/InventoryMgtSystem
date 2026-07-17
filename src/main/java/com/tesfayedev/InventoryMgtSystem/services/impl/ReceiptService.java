package com.tesfayedev.InventoryMgtSystem.services.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.tesfayedev.InventoryMgtSystem.models.Transaction;
import com.tesfayedev.InventoryMgtSystem.models.TransactionItem;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class ReceiptService {

    public byte[] generateReceiptPdf(Transaction txn) {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()){

            //Receipt size
            Document document = new Document(PageSize.A6,20,20,20,20);
            PdfWriter.getInstance(document,baos);
            document.open();

            //Header
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD,14);
            Paragraph title = new Paragraph("FairPrice Kitintale",titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            //Transaction Details
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA,10);
            document.add(new Paragraph("Txn ID: "+ txn.getId(), normalFont));
            document.add(new Paragraph("Date: "+ txn.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),normalFont));
            document.add(new Paragraph("Price Type: "+ txn.getPriceType(), normalFont));
            document.add(new Paragraph(" ")); //Spacer

            //Line Items Table
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2f,1f,1.5f});

            addTableHeader(table,normalFont,"Item","Qty","Total");

            for (TransactionItem item : txn.getItems()){
                table.addCell(new PdfPCell(new Phrase(item.getProduct().getName(),normalFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(item.getQuantity()),normalFont)));
                table.addCell(new PdfPCell(new Phrase(item.getSubtotal().toString(),normalFont)));
            }
            document.add(table);

            //Total
            document.add(new Paragraph(" "));
            Paragraph total = new Paragraph("Total: "+txn.getTotalPrice()+"UGX",titleFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);

            document.close();
            return baos.toByteArray();
        }catch (Exception e){
            throw new RuntimeException("Failed to generate receipt PDF", e);
        }
    }

    private void addTableHeader(PdfPTable table,Font font,String... headers){
        for (String header : headers){
            PdfPCell cell = new PdfPCell(new Phrase(header,font));
            cell.setBorderWidthBottom(1);
            cell.setBorder(Rectangle.BOTTOM);
            table.addCell(cell);
        }
    }
}
