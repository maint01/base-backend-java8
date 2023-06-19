package vn.com.lifesup.hackathon.util.excel;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConfigFileExport {

    private List lstData;
    private String sheetName;
    private String title;
    private List<ConfigSubtitleExport> suptitle;
    private Integer startRow;
    private Integer startColumn;
    private Integer cellTitleIndex;
    private Integer mergeTitleEndIndex;
    private boolean creatHeader;
    private List<ConfigHeaderExport> header;
    private List<ConfigSubheaderExport> supheader;
    private boolean autoGenNo;

    public ConfigFileExport(List lstData, String sheetName, String title, List<ConfigSubtitleExport> suptitle, int startRow, int cellTitleIndex, int mergeTitleEndIndex, boolean creatHeader, List<ConfigHeaderExport> header, List<ConfigSubheaderExport> supheader, boolean autoGenNo) {
        this.lstData = lstData;
        this.sheetName = sheetName;
        this.title = title;
        this.suptitle = suptitle;
        this.startRow = startRow;
        this.cellTitleIndex = cellTitleIndex;
        this.mergeTitleEndIndex = mergeTitleEndIndex;
        this.creatHeader = creatHeader;
        this.header = header;
        this.supheader = supheader;
        this.autoGenNo = autoGenNo;
    }

    public ConfigFileExport(List lstData, String sheetName, int startRow, int startColumn) {
        this.lstData = lstData;
        this.sheetName = sheetName;
        this.startRow = startRow;
        this.startColumn = startColumn;
        this.creatHeader = false;
    }
}
