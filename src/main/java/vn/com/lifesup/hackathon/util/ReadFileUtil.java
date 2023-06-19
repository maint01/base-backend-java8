package vn.com.lifesup.hackathon.util;//package com.example.demo.util;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.List;
//
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.DataFormatter;
//import org.apache.poi.ss.usermodel.Row;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//
//import com.example.demo.dto.TimekeepingExelDto;
//import com.example.demo.entity.TimekeepingEntity;
//
//public class ReadFileUtil {
//	public static List<TimekeepingEntity> readBooksFromExcelFile(String excelFilePath)
//			throws IOException, ParseException {
//		List<TimekeepingEntity> listTimeKeepDTO = new ArrayList<>();
//		FileInputStream inputStream = new FileInputStream(new File(excelFilePath));
//
//		Workbook workBook = getWorkbook(inputStream, excelFilePath);
//		Sheet firstSheet = workBook.getSheetAt(0);
//		Iterator<Row> rows = firstSheet.iterator();
//		DataFormatter dataForm = new DataFormatter();
//		while (rows.hasNext()) {
//			Row row = rows.next();
//			if (row.getRowNum() == 0 || row.getRowNum() == 1 || row.getRowNum() == 2 || row.getRowNum() == 3) {
//				continue;
//			}
//
//			Iterator<Cell> cells = row.cellIterator();
//
//			TimekeepingExelDto timeKeepDTO = new TimekeepingExelDto();
//			TimekeepingEntity timekeeping = new TimekeepingEntity();
//
//			while (cells.hasNext()) {
//				Cell cell = cells.next();
//				int columnIndex = cell.getColumnIndex();
//				if (columnIndex > 6 && columnIndex < 12) {
//					for (columnIndex = 7; columnIndex < 12; columnIndex++) {
//						if (getCellValue(cell) == null) {
//							continue;
//						} else {
//							timeKeepDTO.setEndTime(dataForm.formatCellValue(cell));
//						}
//					}
//				}
//				switch (columnIndex) {
//				case 0:
//					timeKeepDTO.setCreateTime(dataForm.formatCellValue(cell));
//					break;
//				case 1:
//					timeKeepDTO.setDay((String) getCellValue(cell));
//					break;
//				case 2:
//					timeKeepDTO.setId(Long.valueOf(getCellValue(cell).toString()));
//					break;
//				case 3:
//					timeKeepDTO.setStaffName((String) getCellValue(cell));
//					break;
////	                    case 4:
////	                    	timeKeepDTO.setPrice((Double) getCellValue(cell));
////	                        break;
////	                    case 5:
////	                    	timeKeepDTO.setPrice((Double) getCellValue(cell));
////	                        break;
//				case 6:
//					timeKeepDTO.setStartTime(dataForm.formatCellValue(cell));
//					break;
//
//				}
//				if (timeKeepDTO.getStartTime() != null) {
//					Date dateStart = new SimpleDateFormat("hh:mm").parse(timeKeepDTO.getStartTime());
//					Instant start = dateStart.toInstant();
//					timekeeping.setTimekeepingStart(start);
//				}
//				if (timeKeepDTO.getEndTime() != null) {
//					Date dateEnd = new SimpleDateFormat("hh:mm").parse(timeKeepDTO.getEndTime());
//					Instant end = dateEnd.toInstant();
//					timekeeping.setTimekeepingEnd(end);
//				}
//				if (timeKeepDTO.getCreateTime() != null) {
//					Date dateCreate = new SimpleDateFormat("dd MMMM yyyy").parse(timeKeepDTO.getCreateTime());
//					Instant create = dateCreate.toInstant();
//					timekeeping.setCreateTime(create);
//				}
//
//				timekeeping.setId(timeKeepDTO.getId());
//
//			}
//			listTimeKeepDTO.add(timekeeping);
//		}
//
//		workBook.close();
//		inputStream.close();
//
//		return listTimeKeepDTO;
//	}
//
//	@SuppressWarnings("incomplete-switch")
//	private static Object getCellValue(Cell cell) {
//
//		switch (cell.getCellType()) {
//		case STRING:
//			return cell.getStringCellValue();
//
//		case BOOLEAN:
//			return cell.getBooleanCellValue();
//
//		case NUMERIC:
//			return cell.getNumericCellValue();
//
//		}
//
//		return null;
//	}
//
//	private static Workbook getWorkbook(FileInputStream inputStream, String excelFilePath) throws IOException {
//		Workbook workbook = null;
//
//		if (excelFilePath.endsWith("xlsx")) {
//			workbook = new XSSFWorkbook(inputStream);
//		} else if (excelFilePath.endsWith("xls")) {
//			workbook = new HSSFWorkbook(inputStream);
//		} else {
//			throw new IllegalArgumentException("The specified file is not Excel file");
//		}
//
//		return workbook;
//	}
//}
