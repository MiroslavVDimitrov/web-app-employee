package com.miro.employee.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import com.miro.employee.model.PairEmployee;


public class CSVHelper {
 
  public static ByteArrayInputStream pairEmployeeToCSV(List<PairEmployee> employees) {
    final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

    try (ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);) {
      for (PairEmployee employee : employees) {
        List<Object> data = Arrays.asList(
            String.valueOf(employee.getEmpId1()),
            employee.getEmpId2(),
            employee.getProjectId(),
            employee.getDaysWorked());

        csvPrinter.printRecord(data);
      }

      csvPrinter.flush();
      return new ByteArrayInputStream(out.toByteArray());
    } catch (IOException e) {
      throw new RuntimeException("fail to parse data from List to CSV file: " + e.getMessage());
    }
  }

}
