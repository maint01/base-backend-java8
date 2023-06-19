package vn.com.lifesup.hackathon.util.excel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConfigFileImport {
    private int rowHeaderIndex;
    private int startRowIndex;
    private int lastCellIndex;
    private String[] headers;
    private String[] keys;

    public ConfigFileImport(int rowHeaderIndex, int startRowIndex, int lastCellIndex, String[] headers, String[] keys) {
        this.rowHeaderIndex = rowHeaderIndex;
        this.startRowIndex = startRowIndex;
        this.lastCellIndex = lastCellIndex;
        this.headers = headers;
        this.keys = keys;
    }
}

