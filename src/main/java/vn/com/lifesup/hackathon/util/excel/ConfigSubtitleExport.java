package vn.com.lifesup.hackathon.util.excel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigSubtitleExport {

    private String subtitleName;
    private int indexRow;
    private int indexCell;
    private int mergeTitleEndIndex;
    private String align;

    public ConfigSubtitleExport(String subtitleName, int indexRow, int indexCell, int mergeTitleEndIndex, String align) {
        this.subtitleName = subtitleName;
        this.indexRow = indexRow;
        this.indexCell = indexCell;
        this.mergeTitleEndIndex = mergeTitleEndIndex;
        this.align = align;
    }
}
