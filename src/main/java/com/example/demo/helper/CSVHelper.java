package com.example.demo.helper;

import com.example.demo.entity.Ice_cream_product;
import org.apache.commons.csv.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.Style;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class CSVHelper {

    public static  String TYPE = "text/csv";
    static String[] HEADERs = {"Skucode", "Name", "Description", "Price", "IsOnSale"};

    public static boolean hasCSVFormat(MultipartFile file){
        if(TYPE.equals(file.getContentType()) ||
            file.getContentType().equals("application/vnd.ms-excel")){
            return true;
        }
        return false;
    }

    public static List<Ice_cream_product> csvToMysql(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {
            Map<Long, Ice_cream_product> iceCreamMap = new HashMap<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            Long previousSkucode = null;

            for (CSVRecord csvRecord : csvRecords) {
                String skucodeStr = csvRecord.get("Skucode").trim();

                if (!skucodeStr.matches("\\d+") || skucodeStr.length() > 3) {
                    // Invalid Skucode, skip this record
                    throw new IllegalArgumentException("Invalid Skucode: " + skucodeStr);
                }

                Long skucode = Long.parseLong(skucodeStr);

                if(skucode < 1 || skucode > 500){
                    throw  new IllegalArgumentException("Skucode out of range: " + skucodeStr);
                }

                if (previousSkucode != null && skucode != previousSkucode + 1 && !skucode.equals(previousSkucode)) {
                    throw new IllegalArgumentException("Invalid Skucode sequence: " + skucodeStr);
                }

                Ice_cream_product iceCreamProduct = iceCreamMap.get(skucode);
                if (iceCreamProduct == null) {
                    iceCreamProduct = new Ice_cream_product();
                    iceCreamProduct.setSkucode(skucode);
                    iceCreamMap.put(skucode, iceCreamProduct);
                }

                String name = csvRecord.get("Name").trim();
                if (!name.isEmpty()) {
                    if(iceCreamProduct.getName() == null){
                        iceCreamProduct.setName(name);
                    }else {
                        throw new IllegalArgumentException ("something wrong");
                    }
                }

                String description = csvRecord.get("Description").trim();
                if (!description.isEmpty()) {
                    if(iceCreamProduct.getDescription() == null){
                        iceCreamProduct.setDescription(description);
                    }else {
                        throw new IllegalArgumentException ("something wrong");
                    }
                }

                String priceStr = csvRecord.get("Price").trim();
                if (!priceStr.isEmpty()) {
                    double price = Double.parseDouble(priceStr);
                    if(iceCreamProduct.getPrice() == 0){
                        iceCreamProduct.setPrice(price);
                    }else {
                        throw new IllegalArgumentException ("something wrong");
                    }
                }

                String isOnSaleStr = csvRecord.get("IsOnSale").trim();
                if (!isOnSaleStr.isEmpty()) {
                    if(iceCreamProduct.getIsOnSale() == null){
                        iceCreamProduct.setIsOnSale(isOnSaleStr);
                    }else {
                        throw new IllegalArgumentException ("something wrong");
                    }
                }

                previousSkucode = skucode;
            }

            return new ArrayList<>(iceCreamMap.values());
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage());
        }
    }


    public static ByteArrayInputStream mysqltoCSV(List<Ice_cream_product> developerTutorialList) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);) {
            for (Ice_cream_product iceCreamProduct : developerTutorialList) {
                List<String> data = Arrays.asList(
                        String.valueOf(iceCreamProduct.getId()),
                        iceCreamProduct.getName(),
                        iceCreamProduct.getDescription(),
                        String.valueOf(iceCreamProduct.getPrice()),
                        String.valueOf(iceCreamProduct.getIsOnSale())
                );

                csvPrinter.printRecord(data);
            }

            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        }
    }
}
