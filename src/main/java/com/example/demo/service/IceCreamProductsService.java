package com.example.demo.service;

import com.example.demo.entity.Ice_cream_product;
import com.example.demo.helper.CSVHelper;
import com.example.demo.repository.IceCreamProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Service
public class IceCreamProductsService {

    @Autowired
    IceCreamProductRepo iceCreamProductRepo;

    public void save(MultipartFile file) {
        try {
            List<Ice_cream_product> iceCreamProducts = CSVHelper.csvToMysql(file.getInputStream());
            iceCreamProductRepo.saveAll(iceCreamProducts);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ByteArrayInputStream load() {
        List<Ice_cream_product> iceCreamProducts = iceCreamProductRepo.findAll();
        ByteArrayInputStream in = CSVHelper.mysqltoCSV(iceCreamProducts);
        return in;
    }

    public List<Ice_cream_product> getAllTutorials() {
        return iceCreamProductRepo.findAll();
    };
}

