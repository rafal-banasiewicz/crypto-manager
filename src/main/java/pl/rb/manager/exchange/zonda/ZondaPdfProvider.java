package pl.rb.manager.exchange.zonda;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;
import pl.rb.manager.exchange.zonda.model.ZondaPdfData;
import pl.rb.manager.model.Currency;

import javax.swing.filechooser.FileSystemView;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.stream.Stream;

@Component
class ZondaPdfProvider {

    void createDocument(List<ZondaPdfData> pdfData, String totalSpent) throws FileNotFoundException, DocumentException {
        var document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(FileSystemView.getFileSystemView().getHomeDirectory() + "/MoneySpent.pdf"));
        document.open();
        var fiat = pdfData.stream().findFirst().orElseThrow().fiat();
        document.add(getPLNTable(pdfData, totalSpent, fiat));
        document.close();
    }

    private PdfPTable getPLNTable(List<ZondaPdfData> pdfData, String totalSpent, Currency fiat) {
        PdfPTable table;
        switch (fiat) {
            case PLN -> table = getPLNTable(pdfData, totalSpent);
            case EUR, USD -> table = getOtherCurrencyTable(pdfData, totalSpent, fiat);
            default -> throw new IllegalStateException("Unexpected value: " + fiat);
        }
        return table;
    }

    private PdfPTable getPLNTable(List<ZondaPdfData> pdfData, String totalSpent) {
        PdfPTable table;
        table = new PdfPTable(5);
        addHeader(table);
        addRows(table, pdfData, totalSpent);
        return table;
    }

    private void addHeader(PdfPTable table) {
        Stream.of("Date", "Market", "Amount", "Rate", "Spent in PLN")
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

    private void addRows(PdfPTable table, List<ZondaPdfData> pdfData, String totalSpent) {
        for (ZondaPdfData data : pdfData) {
            table.addCell(createCommonCellStyle(data.date()));
            table.addCell(createCommonCellStyle(data.market()));
            table.addCell(createCommonCellStyle(data.amount()));
            table.addCell(createCommonCellStyle(data.rate()));
            table.addCell(createCommonCellStyle(data.spentAmountInPln()));
        }
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell(createCommonCellStyle("Total Spent:"));
        table.addCell(createCommonCellStyle(totalSpent));
    }

    private PdfPTable getOtherCurrencyTable(List<ZondaPdfData> pdfData, String totalSpent, Currency fiat) {
        PdfPTable table;
        table = new PdfPTable(7);
        addOtherCurrencyTableHeader(table, fiat);
        addOtherCurrencyRows(table, pdfData, totalSpent);
        return table;
    }

    private void addOtherCurrencyTableHeader(PdfPTable table, Currency fiat) {
        Stream.of("Date", "Market", "Amount", "Rate", "NBP " + fiat + " rate",  "Spent in " + fiat, "Spent in PLN")
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

    private void addOtherCurrencyRows(PdfPTable table, List<ZondaPdfData> pdfData, String totalSpent) {
        for (ZondaPdfData data : pdfData) {
            table.addCell(createCommonCellStyle(data.date()));
            table.addCell(createCommonCellStyle(data.market()));
            table.addCell(createCommonCellStyle(data.amount()));
            table.addCell(createCommonCellStyle(data.rate()));
            table.addCell(createCommonCellStyle(data.fiatMultiplier()));
            table.addCell(createCommonCellStyle(String.valueOf(data.spentAmount())));
            table.addCell(createCommonCellStyle(String.valueOf(data.spentAmountInPln())));
        }
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");
        table.addCell("");
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
