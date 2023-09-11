package com.miro.employee.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import com.miro.employee.exception.ReadFileException;
import com.miro.employee.helper.CSVHelper;
import com.miro.employee.model.Employee;
import com.miro.employee.model.PairEmployee;
import com.miro.employee.validation.CSVValidation;

@Service
public class CSVService {

  // Convert List<PairEmployee> to .CSV and return ResponseEntity<Resource>
  public ByteArrayInputStream load(List<PairEmployee> list) {
    ByteArrayInputStream in = CSVHelper.pairEmployeeToCSV(list);
    return in;
  }

  Integer winner = 0;
  String keyWinner = null;

  public List<PairEmployee> determinePair(MultipartFile file) throws IOException {

    List<PairEmployee> longTimeTogether = new ArrayList<>();
    

    BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
    int countRow = 0;
    Map<Integer, List<Employee>> project = new HashMap<>();
    CSVValidation csvValidation = new CSVValidation();
    Employee employee = new Employee();

    // validate file format
    if (!CSVValidation.isCSVFormat(file)) {
      throw new ReadFileException("the file: / " + file.getOriginalFilename() + " / is not CSV format");
    }

    // iterate over a line in the file
    String line;
    while ((line = fileReader.readLine()) != null) {
      countRow++;

      // check input data row and set to Object employee
      try {
        employee = csvValidation.lineToEmployee(line);
      } catch (ReadFileException e) {
        throw new ReadFileException(e.getMessage() + " / at row : " + countRow);
      }

      // sort employee by project
      if (!project.containsKey(employee.getProjectId())) {
        List<Employee> tEmployees = new ArrayList<>();
        tEmployees.add(employee);
        project.put(employee.getProjectId(), tEmployees);
      } else {
        project.get(employee.getProjectId()).add(employee);

      }

    }

    // set Map for pair empoyees in common projects
    Map<String, List<PairEmployee>> hash = new HashMap<>();
    // set Map for pair employees total time
    Map<String, Integer> win = new HashMap<>();

    // iterate over projects
    project.forEach(
        (key, value) -> {

          // iterate over employee
          for (int index = 0; index < value.size(); index++) {
            String keyPair;
            Employee emp = value.get(index);
            // comparing the time of employees
            for (int j = index + 1; j < value.size(); j++) {
              LocalDate min;
              LocalDate max;

              Employee emp2 = value.get(j);

              if (emp.getDateFrom().isAfter(emp2.getDateFrom())) {
                min = emp.getDateFrom();
              } else {
                min = emp2.getDateFrom();
              }

              if (emp.getDateTo().isBefore(emp2.getDateTo())) {
                max = emp.getDateTo();
              } else {
                max = emp2.getDateTo();
              }

              if (min.isBefore(max)) {
                int day = (int) (ChronoUnit.DAYS.between(min, max));
                if (emp.getEmpId() != emp2.getEmpId()) {
                  if (emp.getEmpId() < emp2.getEmpId()) {
                    keyPair = (emp.getEmpId() + "," + emp2.getEmpId()).toString();
                  } else {
                    keyPair = (emp2.getEmpId() + "," + emp.getEmpId()).toString();
                  }

                  // add pair employees to Map and sum total time
                  if (!hash.containsKey(keyPair)) {
                    List<PairEmployee> pairEmployees = new ArrayList<>();
                    PairEmployee pairEmployee = new PairEmployee();

                    pairEmployee.setEmpId1(emp.getEmpId());
                    pairEmployee.setEmpId2(emp2.getEmpId());

                    pairEmployee.setProjectId(String.valueOf(emp.getProjectId()));
                    pairEmployee.setDaysWorked((int) (ChronoUnit.DAYS.between(min, max)));

                    pairEmployees.add(pairEmployee);
                    hash.put(keyPair, pairEmployees);
                    win.put(keyPair, day);
                  } else {
                    PairEmployee pairEmployee = new PairEmployee();

                    pairEmployee.setEmpId1(emp.getEmpId());
                    pairEmployee.setEmpId2(emp2.getEmpId());

                    pairEmployee.setProjectId(String.valueOf(emp.getProjectId()));
                    pairEmployee.setDaysWorked((int) (ChronoUnit.DAYS.between(min, max)));
                    hash.get(keyPair).add(pairEmployee);
                    int daysum = win.get(keyPair) + day;
                    win.replace(keyPair, daysum);
                  }

                }

              }

            }
          }
        });

        // determining the pair with the longest time
    win.forEach(
        (key, value) -> {
          if (winner < value) {
            winner = value;
            keyWinner = key;
          }
        }

    );

    // get projects of winner pair employees
    longTimeTogether = hash.get(keyWinner);

    // get employee ID 1 and employee ID 2
    List<Integer> convertKey = Stream.of(keyWinner.split(","))
        .map(Integer::parseInt)
        .collect(Collectors.toList());

     // add to winner empoyee total time   
    PairEmployee pairTotal = new PairEmployee(convertKey.get(0), convertKey.get(1), "Total:", winner);
    longTimeTogether.add(pairTotal);
  
    return longTimeTogether;
  }

}