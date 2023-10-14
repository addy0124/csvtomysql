package com.example.demo.controller;

import com.example.demo.entity.Ice_cream_product;
import com.example.demo.helper.CSVHelper;
import com.example.demo.response.FileUploadResponse;
import com.example.demo.service.IceCreamProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@Controller
@RequestMapping("/api/csv")
public class IceCreamController {
    @Autowired
    IceCreamProductsService iceCreamProductsService;

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file")MultipartFile file){
        String message = "";
        if(CSVHelper.hasCSVFormat(file)){
            try{
                iceCreamProductsService.save(file);
                message = "Upload the file successfully: " + file.getOriginalFilename();
                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("api/csv/download/")
                        .path(file.getOriginalFilename())
                        .toUriString();
                return ResponseEntity.status(HttpStatus.OK).body(new FileUploadResponse(message, fileDownloadUri));
            }catch (Exception e){
                message = "Could not upload the file: "+ file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new FileUploadResponse(message, ""));
            }
        }
        message = "Please upload a csv file: "+ file.getOriginalFilename() + "!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new FileUploadResponse(message, ""));
    }

    @GetMapping("/IceCreamProduct")
    public ResponseEntity<List<Ice_cream_product>> getAllIceCreamProduct() {
        try {
            List<Ice_cream_product> iceCreamProducts = iceCreamProductsService.getAllTutorials();
            if(iceCreamProducts.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(iceCreamProducts, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        InputStreamResource file = new InputStreamResource(iceCreamProductsService.load());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }


}
