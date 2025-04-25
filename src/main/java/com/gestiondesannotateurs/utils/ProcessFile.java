package com.gestiondesannotateurs.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestiondesannotateurs.entities.Coupletext;
import com.gestiondesannotateurs.entities.Dataset;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.gestiondesannotateurs.controllers.DatasetController.CSV_CONTENT_TYPE;
import static com.gestiondesannotateurs.controllers.DatasetController.JSON_CONTENT_TYPE;

@Component
public class ProcessFile {

    public static List<Coupletext> processFile(MultipartFile file) throws CsvValidationException, IOException {
        List<Coupletext> storageDatas = new ArrayList<>();
        String contentType = file.getContentType();

        if (Objects.equals(contentType, CSV_CONTENT_TYPE)) {
            try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
                csvReader.readNext();
                String[] row;
                while ((row = csvReader.readNext()) != null) {
                    if (row.length >= 2) {
                        Coupletext storageData = new Coupletext();
                        storageData.setTextA(row[0]);
                        storageData.setTextB(row[1]);
                        System.out.println(storageData);
                        storageDatas.add(storageData);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (Objects.equals(contentType, JSON_CONTENT_TYPE)) {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, String>> jsonData = objectMapper.readValue(file.getInputStream(), List.class);
            for (Map<String, String> entry : jsonData) {
                Coupletext storageData = new Coupletext();
                storageData.setTextA(entry.get("textA"));
                storageData.setTextB(entry.get("textB"));
                storageDatas.add(storageData);
            }
        }

        return storageDatas;
    }
}
