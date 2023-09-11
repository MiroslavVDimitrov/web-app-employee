package com.miro.employee.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PairEmployee {

    int empId1;
    int empId2;
    String  projectId;
    int daysWorked;

    
}