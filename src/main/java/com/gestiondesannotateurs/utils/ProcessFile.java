package com.gestiondesannotateurs.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gestiondesannotateurs.entities.Coupletext;
import com.gestiondesannotateurs.entities.Dataset;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

import static com.gestiondesannotateurs.controllers.DatasetController.CSV_CONTENT_TYPE;
import static com.gestiondesannotateurs.controllers.DatasetController.JSON_CONTENT_TYPE;
import static com.gestiondesannotateurs.controllers.DatasetController.XLSX_CONTENT_TYPE ;
@Component
public class ProcessFile {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public List<Coupletext> processFile(MultipartFile file, Dataset dataset) throws IOException, CsvValidationException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty or null");
        }

        String savedFilePath = saveFile(file);

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
        } else if (Objects.equals(contentType, XLSX_CONTENT_TYPE)) {
            try (Workbook workbook = new XSSFWorkbook(Files.newInputStream(Paths.get(savedFilePath)))) {
                Sheet sheet = workbook.getSheetAt(0);
                boolean skipHeader = true;
                for (Row row : sheet) {
                    if (skipHeader) {
                        skipHeader = false;
                        continue;
                    }
                    if (row.getPhysicalNumberOfCells() >= 2) {
                        Coupletext storageData = new Coupletext();
                        storageData.setTextA(getCellValueAsString(row.getCell(0)));
                        storageData.setTextB(getCellValueAsString(row.getCell(1)));
                        storageData.setDataset(dataset);
                        storageDatas.add(storageData);
                    }
                }
            } catch (IOException e) {
                Files.deleteIfExists(Paths.get(savedFilePath));
                throw new RuntimeException("Error processing XLSX file", e);
            }
        } else {
            Files.deleteIfExists(Paths.get(savedFilePath));
            throw new IllegalArgumentException("Unsupported file type: " + contentType);
        }

        return storageDatas;
    }

    private String saveFile(MultipartFile file) throws IOException {
        System.out.println("Upload dir: " + uploadDir);

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), filePath);

        return filePath.toString();
    }

    public static String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    // Helper method to get Excel cell value as text
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        // Since all data is text, force string conversion
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue();
    }
}