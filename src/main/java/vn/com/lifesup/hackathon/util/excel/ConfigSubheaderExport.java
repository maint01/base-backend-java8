package vn.com.lifesup.hackathon.util.excel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigSubheaderExport {

    private String subheaderName;
    private int indexRow;
    private int indexCell;
    private int mergeHeaderEndIndex;
    private String align;

    public ConfigSubheaderExport(String subheaderName, int indexRow, int indexCell, int mergeHeaderEndIndex, String align) {
        this.subheaderName = subheaderName;
        this.indexRow = indexRow;
        this.indexCell = indexCell;
        this.mergeHeaderEndIndex = mergeHeaderEndIndex;
        this.align = align;
    }
}
