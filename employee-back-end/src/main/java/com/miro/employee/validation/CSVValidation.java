package  com.miro.employee.validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

import org.springframework.web.multipart.MultipartFile;

import com.miro.employee.exception.ReadFileException;
import com.miro.employee.model.Employee;

public class CSVValidation {

    // check file extension  
    // if use file.ContentType  to check file - Mozilla browser trow 400 - bad request - change type to  "application/vnd.ms-excel"
    public static boolean isCSVFormat(MultipartFile file) {
        String isCSV = ".csv";
        StringBuilder fileExt = new StringBuilder();
        for (int i = 4; i > 0; i--) {
            fileExt.append(file.getOriginalFilename().charAt((file.getOriginalFilename().length() - i)));
        }

        if (fileExt.toString().equals(isCSV)) {
            return true;
        } else {
            return false;
        }
    }

    // validate file text in format - int, int, date, date(or NULL)
    public Employee lineToEmployee(String line) {

        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder()
                .parseCaseInsensitive().parseLenient()
                .appendPattern("[yyyy-MM-dd]")
                .appendPattern("[yyyy/MM/dd]")
                .appendPattern("[M/dd/yyyy]")
                .appendPattern("[M/d/yyyy]")
                .appendPattern("[MM/dd/yyyy]")
                .appendPattern("[MMM dd yyyy]");

        DateTimeFormatter formatter = builder.toFormatter(Locale.ENGLISH);

        Employee employee = new Employee();
        line = line.replaceAll(" ","");
        String[] parts = line.split(",");
        if (parts.length < 5) {
            try {
                employee.setEmpId(Integer.parseInt(parts[0]));
            } catch (Exception e) {
                throw new ReadFileException("Empoyee ID - Invalid value /" + parts[0]);
            }
            try {
                employee.setProjectId(Integer.parseInt(parts[1]));
            } catch (Exception e) {
                throw new ReadFileException("Project ID - Invalid value /" + parts[1]);
            }

            try {
                employee.setDateFrom(LocalDate.parse(parts[2], formatter));
            } catch (Exception e) {
                throw new ReadFileException("Start Date - Invalid value /" + parts[2]);
            }

            try {
                if (parts[3].toUpperCase().equals("NULL")) {
                    employee.setDateTo(LocalDate.now());
                } else {
                    employee.setDateTo(LocalDate.parse(parts[3], formatter));
                }

            } catch (Exception e) {
                throw new ReadFileException("End Date - Invalid value /" + parts[3]);
            }
        
              return employee;

        } else
            throw new ReadFileException("Invalid text format /");



      

    }

}
