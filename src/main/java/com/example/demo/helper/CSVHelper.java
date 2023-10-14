package com.example.demo.helper;

import com.example.demo.entity.Ice_cream_product;
import org.apache.commons.csv.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVHelper {

    public static  String TYPE = "text/csv";
    static String[] HEADERs = {"Id", "Name", "Description", "Price", "IsOnSale"};

    public static boolean hasCSVFormat(MultipartFile file){
        if(TYPE.equals(file.getContentType()) ||
            file.getContentType().equals("application/vnd.ms-excel")){
            return true;
        }
        return false;
    }

    public static List<Ice_cream_product> csvToMysql(InputStream is){
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());){
            List<Ice_cream_product> iceCreamlist = new ArrayList<>();
            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for(CSVRecord csvRecord: csvRecords){
                Ice_cream_product iceCreamProduct = new Ice_cream_product(
                        Long.parseLong(csvRecord.get("Id")),
                        csvRecord.get("Name"),
                        csvRecord.get("Description"),
                        Double.parseDouble(csvRecord.get("Price")),
                        Boolean.parseBoolean(csvRecord.get("IsOnSale"))
                );
                iceCreamlist.add(iceCreamProduct);
            }
            return iceCreamlist;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
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
                        String.valueOf(iceCreamProduct.isOnSale())
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
