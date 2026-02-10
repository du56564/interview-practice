package interview.lld.designpattern.behavioral;

/*
The Template Method Design Pattern is a behavioral design pattern that defines the skeleton of an algorithm in a base class,
but allows subclasses to override specific steps of the algorithm without changing its overall structure.
Example: export reports in different formats — such as CSV, PDF, and Excel.

 */

import java.util.Arrays;
import java.util.List;
import java.util.Map;

class ReportData {
    public List<String> getHeaders() {
        return Arrays.asList("ID", "Name", "Value");
    }

    public List<Map<String, Object>> getRows() {
        return Arrays.asList(
                Map.of("ID", 1, "Name", "Item A", "Value", 100.0),
                Map.of("ID", 2, "Name", "Item B", "Value", 150.5),
                Map.of("ID", 3, "Name", "Item C", "Value", 75.25)
        );
    }
}

abstract class AbstractReportExporter {
    public final void exportReport(ReportData data, String filePath) {
        prepareData(data);
        openFile(filePath);
        writeHeader(data);
        writeDataRows(data);
        writeFooter(data);
        closeFile(filePath);
        System.out.println("Export complete: " + filePath);
    }

    protected void prepareData(ReportData data) { // Hook method
        System.out.println("Preparing report data (common step)...");
    }

    protected void openFile(String filePath) { // Hook method
        System.out.println("Opening file '" + filePath);
    };

    protected abstract void writeHeader(ReportData data);

    protected abstract void writeDataRows(ReportData data);

    protected void writeFooter(ReportData data) { // Hook method
        System.out.println("Writing footer (default: no footer).");
    }

    protected void closeFile(String filePath) { // Hook method
        System.out.println("Closing file '" + filePath);
    };
}

class CsvReportExporter extends AbstractReportExporter {
    //prepareData() not overridden - default will be used
    //openFile() not overridden - default will be used

    @Override
    protected void writeHeader(ReportData data) {
        System.out.println("CSV: Writing header: " + String.join(",", data.getHeaders()));
    }

    @Override
    protected void writeDataRows(ReportData data) {
        System.out.println("CSV: Writing data rows...");
        for (Map<String, Object> row : data.getRows()) {
            System.out.println("CSV: " + row.values());
        }
    }

    // writeFooter() not overridden - default will be used
    // closeFile() not overridden - default will be used
}

class PdfReportExporter extends AbstractReportExporter {
    //prepareData() not overridden - default will be used
    //openFile() not overridden - default will be used

    @Override
    protected void writeHeader(ReportData data) {
        System.out.println("PDF: Writing header: " + String.join(",", data.getHeaders()));
    }

    @Override
    protected void writeDataRows(ReportData data) {
        System.out.println("PDF: Writing data rows...");
        for (Map<String, Object> row : data.getRows()) {
            System.out.println("PDF: " + row.values());
        }
    }

    // writeFooter() not overridden - default will be used
    // closeFile() not overridden - default will be used
}




public class TemplatePattern {
    static void main() {
        ReportData data = new ReportData();

        AbstractReportExporter csvExporter = new CsvReportExporter();
        csvExporter.exportReport(data, "sales_report");

        System.out.println();

        AbstractReportExporter pdfExporter = new PdfReportExporter();
        pdfExporter.exportReport(data, "financial_summary");
    }

}
