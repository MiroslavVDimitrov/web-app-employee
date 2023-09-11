package com.miro.employee.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.miro.employee.model.PairEmployee;
import com.miro.employee.service.CSVService;


/* @CrossOrigin(origins = {"http://localhost:8081", "http://localhost:8083"}, maxAge = 3600,
 allowCredentials="true", allowedHeaders="Cookie") */
 @CrossOrigin(value = "*", maxAge = 3600)
@RestController
public class CSVController {

  private final CSVService fileService;

  private static final Logger logger = LoggerFactory.getLogger(CSVController.class);

  public CSVController(CSVService fileService) {
    this.fileService = fileService;
  }
  @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_MODERATOR') or hasRole('ROLE_ADMIN')")
  @PostMapping("/upload")
  public ResponseEntity<List<PairEmployee>> employeeToJson(@RequestParam("file") MultipartFile file,  @RequestHeader Map<String, String> header) throws Exception {
    System.out.println(header.entrySet());
    logger.info(" header : " + header.entrySet());

  
    return new ResponseEntity<>(fileService.determinePair(file), HttpStatus.OK);
  }

}
