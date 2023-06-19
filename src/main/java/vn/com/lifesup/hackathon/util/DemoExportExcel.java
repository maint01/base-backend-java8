package vn.com.lifesup.hackathon.util;

import vn.com.lifesup.hackathon.dto.TestDTO;
import vn.com.lifesup.hackathon.util.excel.ConfigFileExport;
import vn.com.lifesup.hackathon.util.excel.ConfigHeaderExport;
import vn.com.lifesup.hackathon.util.excel.ConfigSubheaderExport;
import vn.com.lifesup.hackathon.util.excel.ConfigSubtitleExport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DemoExportExcel {

    private static final Logger log = LoggerFactory.getLogger(DemoExportExcel.class);

    public static void main(String[] args) {
        // Chuan bi data
        List<TestDTO> listExport = createListTestDTO();
        // Export
        exportExcel(listExport);
    }

    public static void exportExcel(List<TestDTO> listExport) {
        // Config header
        List<ConfigHeaderExport> lstHeaderSheet = new ArrayList<>();
        ConfigHeaderExport columnSheet;
        columnSheet = new ConfigHeaderExport("STT", "stt", "RIGHT", "NUMBER", 2000);
        lstHeaderSheet.add(columnSheet);
        columnSheet = new ConfigHeaderExport("ID", "id", "RIGHT", "NUMBER", 2000);
        lstHeaderSheet.add(columnSheet);
        columnSheet = new ConfigHeaderExport("Mã", "code", "LEFT", "STRING", 4000);
        lstHeaderSheet.add(columnSheet);
        columnSheet = new ConfigHeaderExport("Tên", "name", "LEFT", "STRING", 4000);
        lstHeaderSheet.add(columnSheet);
        columnSheet = new ConfigHeaderExport("Giá trị", "value", "RIGHT", "NUMBER", 2000);
        lstHeaderSheet.add(columnSheet);
        columnSheet = new ConfigHeaderExport("Ngày", "date", "LEFT", "DATE", 4000);
        lstHeaderSheet.add(columnSheet);
        columnSheet = new ConfigHeaderExport("Thời gian", "date", "LEFT", "DATETIME", 8000);
        lstHeaderSheet.add(columnSheet);

        // Config suptitle
        List<ConfigSubtitleExport> lstSuptitleExport = new ArrayList<>();
        ConfigSubtitleExport configSubtitleExport;
        configSubtitleExport = new ConfigSubtitleExport("Suptitle 1.1", 1, 0, 3, "CENTER");
        lstSuptitleExport.add(configSubtitleExport);
        configSubtitleExport = new ConfigSubtitleExport("Suptitle 1.2", 1, 4, 6, "CENTER");
        lstSuptitleExport.add(configSubtitleExport);
        configSubtitleExport = new ConfigSubtitleExport("Suptitle 2", 2, 0, 6, "CENTER");
        lstSuptitleExport.add(configSubtitleExport);
        configSubtitleExport = new ConfigSubtitleExport("Suptitle 3", 3, 0, 6, "CENTER");
        lstSuptitleExport.add(configSubtitleExport);
        configSubtitleExport = new ConfigSubtitleExport("Suptitle 4", 4, 0, 6, "CENTER");
        lstSuptitleExport.add(configSubtitleExport);

        // Config supheader
        List<ConfigSubheaderExport> lstSubheaderExport = new ArrayList<>();
        ConfigSubheaderExport configSubheaderExport;
        configSubheaderExport = new ConfigSubheaderExport("Tổng: ", 8, 0, 0, "LEFT");
        lstSubheaderExport.add(configSubheaderExport);
        configSubheaderExport = new ConfigSubheaderExport("15.8", 8, 4, 0, "RIGHT");
        lstSubheaderExport.add(configSubheaderExport);

        // Config infor
        String sheetName = "Tên sheet";
        String title = "XUẤT FILE TEST";
        ConfigFileExport configFileExport = new ConfigFileExport(
                listExport,
                sheetName,
                title,
                lstSuptitleExport,
                7,
                0,
                6,
                true,
                lstHeaderSheet,
                lstSubheaderExport,
                true
        );
        List<ConfigFileExport> fileExports = new ArrayList<>();
        fileExports.add(configFileExport);
        try {
            byte[] bytes = ExcelUtil.exportExcel(fileExports);
            File file = new File("../HRM-DEMO-TEMP");
            if (!file.exists()) {
                file.mkdirs();
            }
            File fileWrite = new File(file.getPath() + File.separator + "test.xlsx");
            try (FileOutputStream fos = new FileOutputStream(fileWrite)) {
                fos.write(bytes);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static List<TestDTO> createListTestDTO() {
        List<TestDTO> listExport = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TestDTO testDTO = new TestDTO();
            testDTO.setId(Long.valueOf(String.valueOf(i + 1)));
            testDTO.setCode("CODE_" + (i + 1));
            testDTO.setName("NAME_" + (i + 1));
            testDTO.setValue(0.1 + i);
            testDTO.setDate(new Date());
            listExport.add(testDTO);
        }
        return listExport;
    }
}
