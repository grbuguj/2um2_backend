package com.example.api_test.Controller;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.util.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

@RestController
public class BikeApiController {

    private Double getCellDoubleValue(Cell cell) {
        if (cell == null) return null;
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                try {
                    return Double.parseDouble(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return null;
                }
            case FORMULA:
                try {
                    return cell.getNumericCellValue();
                } catch (IllegalStateException e) {
                    try {
                        return Double.parseDouble(cell.getStringCellValue());
                    } catch (NumberFormatException ex) {
                        return null;
                    }
                }
            default:
                return null;
        }
    }

    @GetMapping("/api/stations")
    public List<Map<String, Object>> getStations() {
        List<Map<String, Object>> result = new ArrayList<>();

        try (InputStream is = getClass().getResourceAsStream("/bike-info.xlsx");
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // 헤더 제외

                Cell nameCell = row.getCell(1);
                Cell latCell = row.getCell(4);
                Cell lngCell = row.getCell(5);

                Double lat = getCellDoubleValue(latCell);
                Double lng = getCellDoubleValue(lngCell);

                if (nameCell != null && lat != null && lng != null) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("name", nameCell.getStringCellValue());
                    data.put("lat", lat);
                    data.put("lng", lng);
                    result.add(data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}




