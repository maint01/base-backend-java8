package vn.com.lifesup.hackathon.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class SheetData<T> {
    private String sheetName;
    private String sheetNameNew;
    private List<T> data;
    private Class<T> clazz;
    private Integer ignoreRowHeader;
}
