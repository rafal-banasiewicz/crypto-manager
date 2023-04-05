package pl.rb.manager.exchange.binance;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;
import pl.rb.manager.exchange.binance.model.BinancePdfData;

import javax.swing.filechooser.FileSystemView;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.stream.Stream;

@Component
class BinancePdfProvider {

    void createDocument(List<BinancePdfData> pdfData, String feeSpent, String totalSpent) throws FileNotFoundException, DocumentException {
        var document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(FileSystemView.getFileSystemView().getHomeDirectory() + "/MoneySpent.pdf"));
        document.open();
        document.add(getTable(pdfData, feeSpent, totalSpent));
        document.close();
    }

    private PdfPTable getTable(List<BinancePdfData> pdfData, String feeSpent, String totalSpent) {
        PdfPTable table = new PdfPTable(10);
        addHeader(table);
        addRows(table, pdfData, feeSpent, totalSpent);
        return table;
    }

    private void addHeader(PdfPTable table) {
        Stream.of("Date", "Status", "Market", "Amount", "Rate", "Currency Exchange Rate", "Fee", "Fee in PLN", "Spent", "Spent in PLN")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setVerticalAlignment(Element.ALIGN_CENTER);
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private void addRows(PdfPTable table, List<BinancePdfData> pdfData, String feeSpent, String totalSpent) {
        for (BinancePdfData data : pdfData) {
            table.addCell(createCommonCellStyle(data.date()));
            table.addCell(createCommonCellStyle(data.status()));
            table.addCell(createCommonCellStyle(data.market()));
            table.addCell(createCommonCellStyle(data.amount()));
            table.addCell(createCommonCellStyle(data.rate()));
            table.addCell(createCommonCellStyle(data.fiatMultiplier()));
            table.addCell(createCommonCellStyle(data.fee()));
            table.addCell(createCommonCellStyle(data.feeInPLN()));
            table.addCell(createCommonCellStyle(data.spentAmount()));
            table.addCell(createCommonCellStyle(data.spentAmountInPln()));
        }
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell(createCommonCellStyle("Total Spent on fees:"));
        table.addCell(createCommonCellStyle(feeSpent));
        table.addCell(createCommonCellStyle("Total Spent:"));
        table.addCell(createCommonCellStyle(totalSpent));
    }

    private PdfPCell createCommonCellStyle(Object body) {
        var header = new PdfPCell();
        header.setVerticalAlignment(Element.ALIGN_CENTER);
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setPhrase(new Phrase(body.toString()));
        return header;
    }
}
