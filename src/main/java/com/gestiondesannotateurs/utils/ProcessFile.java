package com.gestiondesannotateurs.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestiondesannotateurs.entities.Coupletext;
import com.gestiondesannotateurs.entities.Dataset;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.gestiondesannotateurs.controllers.DatasetController.CSV_CONTENT_TYPE;
import static com.gestiondesannotateurs.controllers.DatasetController.JSON_CONTENT_TYPE;

@Component
public class ProcessFile {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public List<Coupletext> processFile(MultipartFile file, Dataset dataset) throws IOException, CsvValidationException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty or null");
        }

//        System.out.println("bugg file1");

        // Save the file and get the path
        String savedFilePath = saveFile(file);
        System.out.println("bugg file2");

        // Process the file and create Coupletext entities
        List<Coupletext> storageDatas = new ArrayList<>();
        String contentType = file.getContentType();

        if (Objects.equals(contentType, CSV_CONTENT_TYPE)) {
            try (CSVReader csvReader = new CSVReader(new InputStreamReader(Files.newInputStream(Paths.get(savedFilePath))))) {
                csvReader.readNext();
                String[] row;
                while ((row = csvReader.readNext()) != null) {
                    if (row.length >= 2) {
                        Coupletext storageData = new Coupletext();
                        storageData.setTextA(row[0]);
                        storageData.setTextB(row[1]);
                        storageData.setDataset(dataset);
                        System.out.println(storageData);
                        storageDatas.add(storageData);
                    }
                }
            } catch (IOException e) {
                Files.deleteIfExists(Paths.get(savedFilePath));
                throw new RuntimeException("Error processing CSV file", e);
            }
        } else if (Objects.equals(contentType, JSON_CONTENT_TYPE)) {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, String>> jsonData = objectMapper.readValue(Files.newInputStream(Paths.get(savedFilePath)), List.class);
            for (Map<String, String> entry : jsonData) {
                Coupletext storageData = new Coupletext();
                storageData.setTextA(entry.get("textA"));
                storageData.setTextB(entry.get("textB"));
                storageData.setDataset(dataset);
                storageDatas.add(storageData);
            }
        } else {
            Files.deleteIfExists(Paths.get(savedFilePath));
            throw new IllegalArgumentException("Unsupported file type: " + contentType);
        }

//        System.out.println("bugg file3");
        return storageDatas;
    }

    private String saveFile(MultipartFile file) throws IOException {
//        System.out.println("save 0");
        System.out.println("Upload dir: " + uploadDir);

        Path uploadPath = Paths.get(uploadDir);
//        System.out.println("save 1");
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
//            System.out.println("save 2");
        }

        Path filePath = uploadPath.resolve(file.getOriginalFilename());
//        System.out.println("save 3");

        Files.copy(file.getInputStream(), filePath);
//        System.out.println("save 4");

        return filePath.toString();
    }
    public String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}