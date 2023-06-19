package vn.com.lifesup.hackathon.util;

import vn.com.lifesup.hackathon.dto.response.ApiResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.multipart.MultipartFile;
import vn.com.lifesup.hackathon.util.excel.*;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

@Log4j2
public class ExcelUtil {

    private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    public static final String ERROR_WHEN_EXPORT_EXCEL = "Error when export excel";
    public static final String LEFT = "LEFT";
    public static final String RIGHT = "RIGHT";
    public static final String CENTER = "CENTER";
    public static final String DATE = "DATE";
    public static final String FULL_DATE = "FULL_DATE";
    public static final String MONEY = "MONEY";
    public static final String FONT_TIMES_NEW_ROMAN = "Times new roman";

    public static ApiResponse<Object> importExcel(MultipartFile file, ConfigFileImport config, Consumer<Map<String, String>> function) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            for (Sheet sheet : workbook) {
                int indexRow = -1;
                DataFormatter df = new DataFormatter();
                Row rowHeader = sheet.getRow(config.getRowHeaderIndex());
                if (rowHeader == null) {
                    return ApiResponse.invalid(MessageUtil.getMessage("error.import-file.template.invalid"));
                }
                int indexCell = -1;
                for (Cell cell : rowHeader) {
                    indexCell++;
                    if (indexCell <= config.getLastCellIndex()) {
                        cell.setCellType(CellType.STRING);
                        if (!config.getHeaders()[indexCell].equals(cell.getStringCellValue().trim())) {
                            return ApiResponse.invalid(MessageUtil.getMessage("error.import-file.template.invalid"));
                        }
                    }
                }
                if (indexCell != config.getLastCellIndex()) {
                    return ApiResponse.invalid(MessageUtil.getMessage("error.import-file.template.invalid"));
                }

                int lastRowNum = sheet.getLastRowNum();
                indexRow = config.getStartRowIndex();
                Row row;
                for (; indexRow <= lastRowNum; indexRow++) {
                    row = sheet.getRow(indexRow);
                    if (row == null) {
                        continue;
                    }
                    Map<String, String> stringMap = new LinkedHashMap<>();
                    indexCell = -1;
                    for (Cell cell : row) {
                        indexCell++;
                        if (indexCell <= config.getLastCellIndex()) {
                            CellType cellType = cell.getCellTypeEnum();
                            if( cellType == CellType.NUMERIC ) {
                                if( org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell) ) {
                                    Double doubleValue = (Double) cell.getNumericCellValue();
                                    Date date = DateUtil.getJavaDate(doubleValue);
                                    stringMap.put(config.getKeys()[cell.getColumnIndex()], simpleDateFormat.format(date));
                                } else {
                                    stringMap.put(config.getKeys()[cell.getColumnIndex()],
                                            df.formatCellValue(cell).trim()
                                    .replace(",", StringUtils.EMPTY));
                                }
                            } else {
                                stringMap.put(config.getKeys()[cell.getColumnIndex()], df.formatCellValue(cell).trim());
                            }
                        }
                    }
                    function.accept(stringMap);
                }
                break;
            }
        } catch (Exception e) {
            logger.error("Loi! importExcel: " + e.getMessage(), e);
            return ApiResponse.serverError();
        }
        return ApiResponse.success(null);
    }


    public static byte[] setMessErrors(byte[] bytesFile, ConfigFileImport config, List<String> listRowMess) {
        try {
            Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(bytesFile));

            //<editor-fold defaultstate="collapsed" desc="Declare style">
            Font xSSFFont = workbook.createFont();
            xSSFFont.setFontName("Times New Roman");
            xSSFFont.setFontHeightInPoints((short) 14);
            xSSFFont.setBold(true);
            xSSFFont.setColor(IndexedColors.BLACK.index);

            CellStyle cellStyleTitle = workbook.createCellStyle();
            cellStyleTitle.setAlignment(HorizontalAlignment.CENTER);
            cellStyleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleTitle.setFillForegroundColor(IndexedColors.WHITE.index);
            cellStyleTitle.setFont(xSSFFont);

            Font xSSFFontHeader = workbook.createFont();
            xSSFFontHeader.setFontName("Times New Roman");
            xSSFFontHeader.setFontHeightInPoints((short) 10);
            xSSFFontHeader.setColor(IndexedColors.BLACK.index);
            xSSFFontHeader.setBold(true);

            Font xSSFFontRedHeader = workbook.createFont();
            xSSFFontRedHeader.setFontName("Times New Roman");
            xSSFFontRedHeader.setFontHeightInPoints((short) 10);
            xSSFFontRedHeader.setColor(IndexedColors.RED.index);
            xSSFFontRedHeader.setBold(true);

            Font subTitleFont = workbook.createFont();
            subTitleFont.setFontName("Times New Roman");
            subTitleFont.setFontHeightInPoints((short) 10);
            subTitleFont.setColor(IndexedColors.BLACK.index);

            Font subHeaderFont = workbook.createFont();
            subHeaderFont.setFontName("Times New Roman");
            subHeaderFont.setFontHeightInPoints((short) 10);
            subHeaderFont.setColor(IndexedColors.BLACK.index);

            Font rowDataFont = workbook.createFont();
            rowDataFont.setFontName("Times New Roman");
            rowDataFont.setFontHeightInPoints((short) 10);
            rowDataFont.setColor(IndexedColors.BLACK.index);

            Font rowDataFontBold = workbook.createFont();
            rowDataFontBold.setFontName("Times New Roman");
            rowDataFontBold.setFontHeightInPoints((short) 10);
            rowDataFontBold.setColor(IndexedColors.BLACK.index);
            rowDataFontBold.setBold(true);

            CellStyle cellStyleHeader = workbook.createCellStyle();
            cellStyleHeader.setAlignment(HorizontalAlignment.CENTER);
            cellStyleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleHeader.setBorderLeft(BorderStyle.THIN);
            cellStyleHeader.setBorderBottom(BorderStyle.THIN);
            cellStyleHeader.setBorderRight(BorderStyle.THIN);
            cellStyleHeader.setBorderTop(BorderStyle.THIN);
            cellStyleHeader.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
            cellStyleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyleHeader.setWrapText(true);
            cellStyleHeader.setFont(xSSFFontHeader);

            CellStyle cellStyleRedHeader = workbook.createCellStyle();
            cellStyleRedHeader.setAlignment(HorizontalAlignment.CENTER);
            cellStyleRedHeader.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleRedHeader.setBorderLeft(BorderStyle.THIN);
            cellStyleRedHeader.setBorderBottom(BorderStyle.THIN);
            cellStyleRedHeader.setBorderRight(BorderStyle.THIN);
            cellStyleRedHeader.setBorderTop(BorderStyle.THIN);
            cellStyleRedHeader.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
            cellStyleRedHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyleRedHeader.setWrapText(true);
            cellStyleRedHeader.setFont(xSSFFontRedHeader);

            CellStyle cellStyleLeft = workbook.createCellStyle();
            cellStyleLeft.setAlignment(HorizontalAlignment.LEFT);
            cellStyleLeft.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleLeft.setBorderLeft(BorderStyle.THIN);
            cellStyleLeft.setBorderBottom(BorderStyle.THIN);
            cellStyleLeft.setBorderRight(BorderStyle.THIN);
            cellStyleLeft.setBorderTop(BorderStyle.THIN);
            cellStyleLeft.setWrapText(true);
            cellStyleLeft.setFont(rowDataFont);

            CellStyle cellStyleNotBorderLeft = workbook.createCellStyle();
            cellStyleNotBorderLeft.setAlignment(HorizontalAlignment.LEFT);
            cellStyleNotBorderLeft.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleNotBorderLeft.setWrapText(true);
            cellStyleNotBorderLeft.setFont(rowDataFont);

            CellStyle cellStyleLeftBold = workbook.createCellStyle();
            cellStyleLeftBold.setAlignment(HorizontalAlignment.LEFT);
            cellStyleLeftBold.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleLeftBold.setBorderLeft(BorderStyle.THIN);
            cellStyleLeftBold.setBorderBottom(BorderStyle.THIN);
            cellStyleLeftBold.setBorderRight(BorderStyle.THIN);
            cellStyleLeftBold.setBorderTop(BorderStyle.THIN);
            cellStyleLeftBold.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
            cellStyleLeftBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyleLeftBold.setWrapText(true);
            cellStyleLeftBold.setFont(rowDataFontBold);

            CellStyle cellStyleRight = workbook.createCellStyle();
            cellStyleRight.setAlignment(HorizontalAlignment.RIGHT);
            cellStyleRight.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleRight.setBorderLeft(BorderStyle.THIN);
            cellStyleRight.setBorderBottom(BorderStyle.THIN);
            cellStyleRight.setBorderRight(BorderStyle.THIN);
            cellStyleRight.setBorderTop(BorderStyle.THIN);
            cellStyleRight.setWrapText(true);
            cellStyleRight.setFont(rowDataFont);

            CellStyle cellStyleRightBold = workbook.createCellStyle();
            cellStyleRightBold.setAlignment(HorizontalAlignment.RIGHT);
            cellStyleRightBold.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleRightBold.setBorderLeft(BorderStyle.THIN);
            cellStyleRightBold.setBorderBottom(BorderStyle.THIN);
            cellStyleRightBold.setBorderRight(BorderStyle.THIN);
            cellStyleRightBold.setBorderTop(BorderStyle.THIN);
            cellStyleRightBold.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
            cellStyleRightBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyleRightBold.setWrapText(true);
            cellStyleRightBold.setFont(rowDataFontBold);

            CellStyle cellStyleCenter = workbook.createCellStyle();
            cellStyleCenter.setAlignment(HorizontalAlignment.CENTER);
            cellStyleCenter.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleCenter.setBorderLeft(BorderStyle.THIN);
            cellStyleCenter.setBorderBottom(BorderStyle.THIN);
            cellStyleCenter.setBorderRight(BorderStyle.THIN);
            cellStyleCenter.setBorderTop(BorderStyle.THIN);
            cellStyleCenter.setWrapText(true);
            cellStyleCenter.setFont(rowDataFont);

            CellStyle cellStyleCenterBold = workbook.createCellStyle();
            cellStyleCenterBold.setAlignment(HorizontalAlignment.CENTER);
            cellStyleCenterBold.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleCenterBold.setBorderLeft(BorderStyle.THIN);
            cellStyleCenterBold.setBorderBottom(BorderStyle.THIN);
            cellStyleCenterBold.setBorderRight(BorderStyle.THIN);
            cellStyleCenterBold.setBorderTop(BorderStyle.THIN);
            cellStyleCenterBold.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
            cellStyleCenterBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyleCenterBold.setWrapText(true);
            cellStyleCenterBold.setFont(rowDataFontBold);

            XSSFFont starFont = (XSSFFont) workbook.createFont();
            starFont.setFontHeightInPoints((short) 10);
            starFont.setColor(IndexedColors.RED.getIndex());
            starFont.setBold(true);
            //</editor-fold>

            Sheet sheet = workbook.getSheetAt(0);

            //<editor-fold defaultstate="collapsed" desc="Build column result">
            Row rowHeader = sheet.getRow(config.getRowHeaderIndex());
            Cell cellHeader = rowHeader.createCell(config.getLastCellIndex() + 1);
            cellHeader.setCellValue("Kết quả");
            cellHeader.setCellStyle(cellStyleRedHeader);
            sheet.setColumnWidth(config.getLastCellIndex() + 1, 20000);
            //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Fill data">
            if (listRowMess != null && !listRowMess.isEmpty()) {
                int indexRow = -1;
                int indexRowMess = -1;
                for (Row row : sheet) {
                    indexRow++;
                    if (indexRow >= config.getStartRowIndex()) {
                        indexRowMess++;
                        Cell cell = row.createCell(config.getLastCellIndex() + 1);
                        cell.setCellValue(listRowMess.get(indexRowMess));
                        cell.setCellStyle(cellStyleNotBorderLeft);
                    }
                }
            }
            //</editor-fold>

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] bytes = outputStream.toByteArray();
            outputStream.flush();
            outputStream.close();
            workbook.close();
            return bytes;
        } catch (Exception e) {
            logger.error("Loi! setMessErrors: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Create file name
     *
     * @param originalFilename
     * @param date
     * @return
     */
    public static String createFileName(String originalFilename, Date date) {
        originalFilename = originalFilename.replaceAll("[\\\\/:*?\"<>|%]", "");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return originalFilename + "_" + calendar.get(Calendar.HOUR_OF_DAY) + ""
                + calendar.get(Calendar.MINUTE) + "" + calendar.get(Calendar.SECOND)
                + "" + calendar.get(Calendar.MILLISECOND);
    }

    public static byte[] exportExcel(List<ConfigFileExport> config) {
        try {
            XSSFWorkbook wb_template = new XSSFWorkbook();
            Workbook workbook = new SXSSFWorkbook(wb_template, 500);

            //<editor-fold defaultstate="collapsed" desc="Declare style">
            Font xSSFFont = workbook.createFont();
            xSSFFont.setFontName("Times New Roman");
            xSSFFont.setFontHeightInPoints((short) 20);
            xSSFFont.setBold(true);
            xSSFFont.setColor(IndexedColors.BLACK.index);

            CellStyle cellStyleTitle = workbook.createCellStyle();
            cellStyleTitle.setAlignment(HorizontalAlignment.CENTER);
            cellStyleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleTitle.setFillForegroundColor(IndexedColors.WHITE.index);
            cellStyleTitle.setFont(xSSFFont);

            Font xSSFFontHeader = workbook.createFont();
            xSSFFontHeader.setFontName("Times New Roman");
            xSSFFontHeader.setFontHeightInPoints((short) 10);
            xSSFFontHeader.setColor(IndexedColors.BLACK.index);
            xSSFFontHeader.setBold(true);

            Font subTitleFont = workbook.createFont();
            subTitleFont.setFontName("Times New Roman");
            subTitleFont.setFontHeightInPoints((short) 10);
            subTitleFont.setColor(IndexedColors.BLACK.index);

            Font subHeaderFont = workbook.createFont();
            subHeaderFont.setFontName("Times New Roman");
            subHeaderFont.setFontHeightInPoints((short) 10);
            subHeaderFont.setColor(IndexedColors.BLACK.index);

            Font rowDataFont = workbook.createFont();
            rowDataFont.setFontName("Times New Roman");
            rowDataFont.setFontHeightInPoints((short) 10);
            rowDataFont.setColor(IndexedColors.BLACK.index);

            Font rowDataFontBold = workbook.createFont();
            rowDataFontBold.setFontName("Times New Roman");
            rowDataFontBold.setFontHeightInPoints((short) 10);
            rowDataFontBold.setColor(IndexedColors.BLACK.index);
            rowDataFontBold.setBold(true);

            CellStyle cellStyleHeader = workbook.createCellStyle();
            cellStyleHeader.setAlignment(HorizontalAlignment.CENTER);
            cellStyleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleHeader.setBorderLeft(BorderStyle.THIN);
            cellStyleHeader.setBorderBottom(BorderStyle.THIN);
            cellStyleHeader.setBorderRight(BorderStyle.THIN);
            cellStyleHeader.setBorderTop(BorderStyle.THIN);
            cellStyleHeader.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
            cellStyleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyleHeader.setWrapText(true);
            cellStyleHeader.setFont(xSSFFontHeader);

            CellStyle cellStyleLeft = workbook.createCellStyle();
            cellStyleLeft.setAlignment(HorizontalAlignment.LEFT);
            cellStyleLeft.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleLeft.setBorderLeft(BorderStyle.THIN);
            cellStyleLeft.setBorderBottom(BorderStyle.THIN);
            cellStyleLeft.setBorderRight(BorderStyle.THIN);
            cellStyleLeft.setBorderTop(BorderStyle.THIN);
            cellStyleLeft.setWrapText(true);
            cellStyleLeft.setFont(rowDataFont);

            CellStyle cellStyleLeftBold = workbook.createCellStyle();
            cellStyleLeftBold.setAlignment(HorizontalAlignment.LEFT);
            cellStyleLeftBold.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleLeftBold.setBorderLeft(BorderStyle.THIN);
            cellStyleLeftBold.setBorderBottom(BorderStyle.THIN);
            cellStyleLeftBold.setBorderRight(BorderStyle.THIN);
            cellStyleLeftBold.setBorderTop(BorderStyle.THIN);
            cellStyleLeftBold.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
            cellStyleLeftBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyleLeftBold.setWrapText(true);
            cellStyleLeftBold.setFont(rowDataFontBold);

            CellStyle cellStyleRight = workbook.createCellStyle();
            cellStyleRight.setAlignment(HorizontalAlignment.RIGHT);
            cellStyleRight.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleRight.setBorderLeft(BorderStyle.THIN);
            cellStyleRight.setBorderBottom(BorderStyle.THIN);
            cellStyleRight.setBorderRight(BorderStyle.THIN);
            cellStyleRight.setBorderTop(BorderStyle.THIN);
            cellStyleRight.setWrapText(true);
            cellStyleRight.setFont(rowDataFont);

            CellStyle cellStyleRightBold = workbook.createCellStyle();
            cellStyleRightBold.setAlignment(HorizontalAlignment.RIGHT);
            cellStyleRightBold.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleRightBold.setBorderLeft(BorderStyle.THIN);
            cellStyleRightBold.setBorderBottom(BorderStyle.THIN);
            cellStyleRightBold.setBorderRight(BorderStyle.THIN);
            cellStyleRightBold.setBorderTop(BorderStyle.THIN);
            cellStyleRightBold.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
            cellStyleRightBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyleRightBold.setWrapText(true);
            cellStyleRightBold.setFont(rowDataFontBold);

            CellStyle cellStyleCenter = workbook.createCellStyle();
            cellStyleCenter.setAlignment(HorizontalAlignment.CENTER);
            cellStyleCenter.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleCenter.setBorderLeft(BorderStyle.THIN);
            cellStyleCenter.setBorderBottom(BorderStyle.THIN);
            cellStyleCenter.setBorderRight(BorderStyle.THIN);
            cellStyleCenter.setBorderTop(BorderStyle.THIN);
            cellStyleCenter.setWrapText(true);
            cellStyleCenter.setFont(rowDataFont);

            CellStyle cellStyleCenterBold = workbook.createCellStyle();
            cellStyleCenterBold.setAlignment(HorizontalAlignment.CENTER);
            cellStyleCenterBold.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleCenterBold.setBorderLeft(BorderStyle.THIN);
            cellStyleCenterBold.setBorderBottom(BorderStyle.THIN);
            cellStyleCenterBold.setBorderRight(BorderStyle.THIN);
            cellStyleCenterBold.setBorderTop(BorderStyle.THIN);
            cellStyleCenterBold.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
            cellStyleCenterBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyleCenterBold.setWrapText(true);
            cellStyleCenterBold.setFont(rowDataFontBold);
            //</editor-fold>

            for (ConfigFileExport item : config) {
                Sheet sheet = workbook.createSheet(item.getSheetName());

                //<editor-fold defaultstate="collapsed" desc="Build title">
                // Title
                Row rowMainTitle = sheet.createRow(item.getCellTitleIndex());
                Cell mainCellTitle = rowMainTitle.createCell(0);
                mainCellTitle.setCellValue(item.getTitle() == null ? "" : item.getTitle());
                mainCellTitle.setCellStyle(cellStyleTitle);
                sheet.addMergedRegion(
                        new CellRangeAddress(item.getCellTitleIndex(), item.getCellTitleIndex(), 0,
                                item.getMergeTitleEndIndex()));
                // Sub title
                if (item.getSuptitle() != null && !item.getSuptitle().isEmpty()) {
                    for (ConfigSubtitleExport suptitle : item.getSuptitle()) {
                        int indexSubTitle = suptitle.getIndexRow();
                        Row rowSubTitle = sheet.getRow(indexSubTitle);
                        if (rowSubTitle == null) {
                            rowSubTitle = sheet.createRow(indexSubTitle);
                        }
                        Cell cellTitle = rowSubTitle.createCell(suptitle.getIndexCell());
                        cellTitle.setCellValue(suptitle.getSubtitleName());
                        CellStyle cellStyleSubTitle = workbook.createCellStyle();
                        if ("CENTER".equals(suptitle.getAlign())) {
                            cellStyleSubTitle.setAlignment(HorizontalAlignment.CENTER);
                        }
                        if ("LEFT".equals(suptitle.getAlign())) {
                            cellStyleSubTitle.setAlignment(HorizontalAlignment.LEFT);
                        }
                        if ("RIGHT".equals(suptitle.getAlign())) {
                            cellStyleSubTitle.setAlignment(HorizontalAlignment.RIGHT);
                        }
                        cellStyleSubTitle.setVerticalAlignment(VerticalAlignment.CENTER);
                        cellStyleSubTitle.setFont(subTitleFont);
                        cellTitle.setCellStyle(cellStyleSubTitle);
                        sheet.addMergedRegion(
                                new CellRangeAddress(indexSubTitle, indexSubTitle, suptitle.getIndexCell(),
                                        suptitle.getMergeTitleEndIndex()));
                    }
                }
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Build header">
                int totalRowSupheader = 0;
                if (item.isCreatHeader()) {
                    int index = -1;
                    Cell cellHeader;
                    Row rowHeader = sheet.createRow(item.getStartRow());
                    rowHeader.setHeight((short) 500);
                    // Header
                    for (ConfigHeaderExport header : item.getHeader()) {
                        cellHeader = rowHeader.createCell(index + 1);
                        cellHeader.setCellValue(header.getHeaderName());
                        cellHeader.setCellStyle(cellStyleHeader);
                        sheet.setColumnWidth(index + 1, header.getWidth());
                        index++;
                    }
                    // Sub header
                    if (item.getSupheader() != null && !item.getSupheader().isEmpty()) {
                        Map<Integer, Integer> rowMap = new HashMap<>();
                        for (ConfigSubheaderExport supheader : item.getSupheader()) {
                            int indexSubHeader = supheader.getIndexRow();
                            rowMap.put(indexSubHeader, indexSubHeader);
                            Row rowSubHeader = sheet.getRow(indexSubHeader);
                            CellStyle cellStyleSubHeader = workbook.createCellStyle();
                            cellStyleSubHeader.setVerticalAlignment(VerticalAlignment.CENTER);
                            cellStyleSubHeader.setFillForegroundColor(IndexedColors.AQUA.index);
                            cellStyleSubHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                            cellStyleSubHeader.setFont(subHeaderFont);
                            if (rowSubHeader == null) {
                                rowSubHeader = sheet.createRow(indexSubHeader);
                                for (int i = 0; i < item.getHeader().size(); i++) {
                                    rowSubHeader.createCell(i).setCellStyle(cellStyleSubHeader);
                                }
                            }
                            Cell cellSubHeader = rowSubHeader.createCell(supheader.getIndexCell());
                            cellSubHeader.setCellValue(supheader.getSubheaderName());
                            if ("CENTER".equals(supheader.getAlign())) {
                                cellStyleSubHeader.setAlignment(HorizontalAlignment.CENTER);
                            }
                            if ("LEFT".equals(supheader.getAlign())) {
                                cellStyleSubHeader.setAlignment(HorizontalAlignment.LEFT);
                            }
                            if ("RIGHT".equals(supheader.getAlign())) {
                                cellStyleSubHeader.setAlignment(HorizontalAlignment.RIGHT);
                            }
                            cellSubHeader.setCellStyle(cellStyleSubHeader);
                            if (supheader.getMergeHeaderEndIndex() != 0) {
                                sheet.addMergedRegion(
                                        new CellRangeAddress(indexSubHeader, indexSubHeader, supheader.getIndexCell(),
                                                supheader.getMergeHeaderEndIndex()));
                            }
                        }
                        totalRowSupheader = rowMap.size();
                    }
                }
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Fill data">
                if (item.getLstData() != null && !item.getLstData().isEmpty()) {
                    //init mapColumn
                    Object firstRow = item.getLstData().get(0);
                    Map<String, Field> mapField = new HashMap<>();
                    for (ConfigHeaderExport header : item.getHeader()) {
                        for (Field f : firstRow.getClass().getDeclaredFields()) {
                            f.setAccessible(true);
                            if (f.getName().equals(header.getFieldName())) {
                                mapField.put(header.getFieldName(), f);
                            }
                        }
                        if (firstRow.getClass().getSuperclass() != null) {
                            for (Field f : firstRow.getClass()
                                    .getSuperclass().getDeclaredFields()) {
                                f.setAccessible(true);
                                if (f.getName().equals(header.getFieldName())) {
                                    mapField.put(header.getFieldName(), f);
                                }
                            }
                        }
                    }
                    //fillData
                    Row row;
                    List lstData = item.getLstData();
                    List<ConfigHeaderExport> lstHeader = item.getHeader();
                    int startRow = item.getStartRow();
                    Boolean isRowBold;
                    for (int i = 0; i < lstData.size(); i++) {
                        row = sheet.createRow(i + startRow + 1 + totalRowSupheader);
                        row.setHeight((short) -1);
                        Cell cell;
                        int j = 0;
                        isRowBold = false;
                        for (int e = 0; e < lstHeader.size(); e++) {
                            ConfigHeaderExport head = lstHeader.get(e);
                            String header = head.getFieldName();
                            String align = head.getAlign();
                            Object obj = lstData.get(i);
                            Field f = mapField.get(header);
                            String value = "";
                            if (f != null) {
                                Object tempValue = f.get(obj);
                                if (tempValue instanceof Date) {
                                    if ("DATE".equals(head.getStyleFormat())) {
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                        value = tempValue == null ? "" : simpleDateFormat.format((Date) tempValue);
                                    } else if ("DATETIME".equals(head.getStyleFormat())) {
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                        value = tempValue == null ? "" : simpleDateFormat.format((Date) tempValue);
                                    } else {
                                        value = tempValue == null ? "" : tempValue.toString();
                                    }
                                } else {
                                    value = tempValue == null ? "" : tempValue.toString();
                                }
                            }
                            cell = row.createCell(j);
                            if ("NUMBER".equals(head.getStyleFormat())) {
                                if (!"".equals(value.trim())) {
                                    cell.setCellValue(Double.valueOf(value));
                                } else {
                                    cell.setCellValue(value);
                                }
                                if (e == 0 && item.isAutoGenNo()) {
                                    cell.setCellValue(i + 1);
                                }
                            } else if ("CURRENCY".equals(head.getStyleFormat())) {
                                if (!"".equals(value.trim())) {
                                    cell.setCellValue(formatVNDCurrency(new BigDecimal(value)));
                                } else {
                                    cell.setCellValue(value);
                                }
                            } else {
                                if (value.length() > 32767) {
                                    cell.setCellValue(value.substring(0, 32766));
                                } else {
                                    cell.setCellValue(value);
                                }
                                if (e == 0 && "".equals(value.trim())) {
                                    isRowBold = true;
                                }
                            }
                            if ("CENTER".equals(align)) {
                                if (isRowBold) {
                                    cell.setCellStyle(cellStyleCenterBold);
                                } else {
                                    cell.setCellStyle(cellStyleCenter);
                                }
                            }
                            if ("LEFT".equals(align)) {
                                if (isRowBold) {
                                    cell.setCellStyle(cellStyleLeftBold);
                                } else {
                                    cell.setCellStyle(cellStyleLeft);
                                }
                            }
                            if ("RIGHT".equals(align)) {
                                if (isRowBold) {
                                    cell.setCellStyle(cellStyleRightBold);
                                } else {
                                    cell.setCellStyle(cellStyleRight);
                                }
                            }
                            j++;
                        }
                    }
                }
                //</editor-fold>
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] bytes = outputStream.toByteArray();
            outputStream.flush();
            outputStream.close();
            workbook.close();
            return bytes;
        } catch (Exception e) {
            logger.error("Loi! exportExcel: " + e.getMessage(), e);
            return null;
        }
    }

    public static byte[] exportExcel(InputStream is, List<ConfigFileExport> config) {
        try {
            XSSFWorkbook wb_template = new XSSFWorkbook(is);
            Workbook workbook = new SXSSFWorkbook(wb_template, 500);

            //<editor-fold defaultstate="collapsed" desc="Declare style">
            Font xSSFFont = workbook.createFont();
            xSSFFont.setFontName("Times New Roman");
            xSSFFont.setFontHeightInPoints((short) 20);
            xSSFFont.setBold(true);
            xSSFFont.setColor(IndexedColors.BLACK.index);

            CellStyle cellStyleTitle = workbook.createCellStyle();
            cellStyleTitle.setAlignment(HorizontalAlignment.CENTER);
            cellStyleTitle.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleTitle.setFillForegroundColor(IndexedColors.WHITE.index);
            cellStyleTitle.setFont(xSSFFont);

            Font xSSFFontHeader = workbook.createFont();
            xSSFFontHeader.setFontName("Times New Roman");
            xSSFFontHeader.setFontHeightInPoints((short) 10);
            xSSFFontHeader.setColor(IndexedColors.BLACK.index);
            xSSFFontHeader.setBold(true);

            Font subTitleFont = workbook.createFont();
            subTitleFont.setFontName("Times New Roman");
            subTitleFont.setFontHeightInPoints((short) 10);
            subTitleFont.setColor(IndexedColors.BLACK.index);

            Font subHeaderFont = workbook.createFont();
            subHeaderFont.setFontName("Times New Roman");
            subHeaderFont.setFontHeightInPoints((short) 10);
            subHeaderFont.setColor(IndexedColors.BLACK.index);

            Font rowDataFont = workbook.createFont();
            rowDataFont.setFontName("Times New Roman");
            rowDataFont.setFontHeightInPoints((short) 10);
            rowDataFont.setColor(IndexedColors.BLACK.index);

            Font rowDataFontBold = workbook.createFont();
            rowDataFontBold.setFontName("Times New Roman");
            rowDataFontBold.setFontHeightInPoints((short) 10);
            rowDataFontBold.setColor(IndexedColors.BLACK.index);
            rowDataFontBold.setBold(true);

            CellStyle cellStyleHeader = workbook.createCellStyle();
            cellStyleHeader.setAlignment(HorizontalAlignment.CENTER);
            cellStyleHeader.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleHeader.setBorderLeft(BorderStyle.THIN);
            cellStyleHeader.setBorderBottom(BorderStyle.THIN);
            cellStyleHeader.setBorderRight(BorderStyle.THIN);
            cellStyleHeader.setBorderTop(BorderStyle.THIN);
            cellStyleHeader.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.index);
            cellStyleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyleHeader.setWrapText(true);
            cellStyleHeader.setFont(xSSFFontHeader);

            CellStyle cellStyle = style(workbook);

            CellStyle cellStyleLeftBold = workbook.createCellStyle();
            cellStyleLeftBold.setAlignment(HorizontalAlignment.LEFT);
            cellStyleLeftBold.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleLeftBold.setBorderLeft(BorderStyle.THIN);
            cellStyleLeftBold.setBorderBottom(BorderStyle.THIN);
            cellStyleLeftBold.setBorderRight(BorderStyle.THIN);
            cellStyleLeftBold.setBorderTop(BorderStyle.THIN);
            cellStyleLeftBold.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
            cellStyleLeftBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyleLeftBold.setWrapText(true);
            cellStyleLeftBold.setFont(rowDataFontBold);

            CellStyle cellStyleRight = workbook.createCellStyle();
            cellStyleRight.setAlignment(HorizontalAlignment.RIGHT);
            cellStyleRight.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleRight.setBorderLeft(BorderStyle.THIN);
            cellStyleRight.setBorderBottom(BorderStyle.THIN);
            cellStyleRight.setBorderRight(BorderStyle.THIN);
            cellStyleRight.setBorderTop(BorderStyle.THIN);
            cellStyleRight.setWrapText(true);
            cellStyleRight.setFont(rowDataFont);

            CellStyle cellStyleRightBold = workbook.createCellStyle();
            cellStyleRightBold.setAlignment(HorizontalAlignment.RIGHT);
            cellStyleRightBold.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleRightBold.setBorderLeft(BorderStyle.THIN);
            cellStyleRightBold.setBorderBottom(BorderStyle.THIN);
            cellStyleRightBold.setBorderRight(BorderStyle.THIN);
            cellStyleRightBold.setBorderTop(BorderStyle.THIN);
            cellStyleRightBold.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
            cellStyleRightBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyleRightBold.setWrapText(true);
            cellStyleRightBold.setFont(rowDataFontBold);

            CellStyle cellStyleCenter = workbook.createCellStyle();
            cellStyleCenter.setAlignment(HorizontalAlignment.CENTER);
            cellStyleCenter.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleCenter.setBorderLeft(BorderStyle.THIN);
            cellStyleCenter.setBorderBottom(BorderStyle.THIN);
            cellStyleCenter.setBorderRight(BorderStyle.THIN);
            cellStyleCenter.setBorderTop(BorderStyle.THIN);
            cellStyleCenter.setWrapText(true);
            cellStyleCenter.setFont(rowDataFont);

            CellStyle cellStyleCenterBold = workbook.createCellStyle();
            cellStyleCenterBold.setAlignment(HorizontalAlignment.CENTER);
            cellStyleCenterBold.setVerticalAlignment(VerticalAlignment.CENTER);
            cellStyleCenterBold.setBorderLeft(BorderStyle.THIN);
            cellStyleCenterBold.setBorderBottom(BorderStyle.THIN);
            cellStyleCenterBold.setBorderRight(BorderStyle.THIN);
            cellStyleCenterBold.setBorderTop(BorderStyle.THIN);
            cellStyleCenterBold.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.index);
            cellStyleCenterBold.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyleCenterBold.setWrapText(true);
            cellStyleCenterBold.setFont(rowDataFontBold);
            //</editor-fold>
            int sheetIndex = -1;
            for (ConfigFileExport item : config) {
                sheetIndex ++;
                Sheet sheet = getOrCreateSheet(item.getSheetName(), sheetIndex, workbook);

                //<editor-fold defaultstate="collapsed" desc="Fill data">
                if (item.getLstData() != null && !item.getLstData().isEmpty()) {
                    //fillData
                    Row row;
                    List lstData = item.getLstData();
                    int startRow = item.getStartRow();
                    int startColumn = item.getStartColumn();
                    Cell cell;
                    for (int i = 0; i < lstData.size(); i++) {
                        row = sheet.getRow(i + startRow);
                        if (row == null) {
                            row = sheet.createRow(i + startRow);
                        }
                        row.setHeight((short) -1);
                        Object[] rowData = (Object[]) lstData.get(i);
                        int j = 0;
                        for (int e = 0; e < rowData.length; e++) {
                            Object tempValue = rowData[e];
                            String value = "";
                            if (tempValue != null) {
                                if (tempValue instanceof Date) {
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                    value = tempValue == null ? "" : simpleDateFormat.format((Date) tempValue);
                                } else {
                                    value = tempValue == null ? "" : tempValue.toString();
                                }
                            }
                            cell = row.createCell(e + startColumn);
                            cell.setCellValue(value);
                            cell.setCellStyle(cellStyle);
                            j++;
                        }
                    }
                }
                //</editor-fold>
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            byte[] bytes = outputStream.toByteArray();
            outputStream.flush();
            outputStream.close();
            workbook.close();
            return bytes;
        } catch (Exception e) {
            logger.error("Loi! exportExcel: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Dinh dang tien te
     *
     * @param currency
     * @return
     */
    public static String formatVNDCurrency(BigDecimal currency) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        if (currency == null) {
            currency = new BigDecimal(0);
        }
        return formatter.format(currency);
    }

    /**
     * Dinh dang tien te
     *
     * @param currency
     * @return
     */
    public static String formatVNDCurrency(Double currency) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        if (currency == null) {
            currency = 0d;
        }
        return formatter.format(currency);
    }

    /**
     * Dinh dang tien te
     *
     * @param currency
     * @return
     */
    public static String formatVNDCurrency(Long currency) {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        if (currency == null) {
            currency = 0L;
        }
        return formatter.format(currency);
    }

    public static byte[] downloadResource(String resourcePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(resourcePath);
        try (InputStream inputStream = resource.getInputStream()) {
            return IOUtils.toByteArray(inputStream);
        }
    }

    private static ByteArrayResource returnToResource(Workbook workbook) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            workbook.write(bos);
            return new ByteArrayResource(bos.toByteArray());
        } catch (IOException e) {
        log.error(e.getMessage(), e);
        throw new RuntimeException(ERROR_WHEN_EXPORT_EXCEL);
        }
    }

    private static Map<String, CellStyle> mapStyle(Workbook workbook) {
        Map<String, CellStyle> maps = new HashMap<>();
        maps.put(LEFT, styleLeft(workbook));
        maps.put(RIGHT, styleRight(workbook));
        maps.put(CENTER, styleCenter(workbook));
        maps.put(DATE, styleDate(workbook));
        maps.put(FULL_DATE, styleFullDate(workbook));
        maps.put(MONEY, styleMoney(workbook));
        return maps;
    }

    private static CellStyle style(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = getFontDefault(workbook);
        cellStyle.setFont(font);
        cellStyle.setWrapText(false);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        fullBorder(cellStyle);
        return cellStyle;
    }

    private static CellStyle styleLeft(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = getFontDefault(workbook);
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        fullBorder(cellStyle);
        return cellStyle;
    }

    private static CellStyle styleRight(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = getFontDefault(workbook);
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        fullBorder(cellStyle);
        return cellStyle;
    }

    private static CellStyle styleCenter(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = getFontDefault(workbook);
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        fullBorder(cellStyle);
        return cellStyle;
    }

    private static CellStyle styleMoney(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = getFontDefault(workbook);
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("##,##0.00\\ \"\";\\-#,##0.00\\ \"\""));
        fullBorder(cellStyle);
        return cellStyle;
    }

    private static CellStyle styleDate(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = getFontDefault(workbook);
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy"));
        fullBorder(cellStyle);
        return cellStyle;
    }

    private static CellStyle styleFullDate(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        Font font = getFontDefault(workbook);
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd/MM/yyyy HH:mm:ss"));
        fullBorder(cellStyle);
        return cellStyle;
    }

    private static void fullBorder(CellStyle cellStyle) {
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
    }

    private static Font getFontDefault(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontName(FONT_TIMES_NEW_ROMAN);
        font.setFontHeightInPoints((short) 12);
        return font;
    }

    private static Sheet getOrCreateSheet(String sheetName, int sheetIndex, Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        if (sheet != null) {
            workbook.setSheetName(sheetIndex, sheetName);
            return sheet;
        }
        int numberOfSheets = workbook.getNumberOfSheets();
        if (numberOfSheets > sheetIndex + 1) {
            sheet = workbook.getSheetAt(sheetIndex);
            workbook.setSheetName(sheetIndex, sheetName);
            return sheet;
        }
        sheet = workbook.createSheet(sheetName);
        return sheet;
    }
}
