package vn.com.lifesup.hackathon.util.enumerate;

public enum ColCellType {
    TYPE_STRING("STRING"),
    TYPE_DATE("DATE"),
    TYPE_CURRENCY("CURRENCY"),
    TYPE_DOUBLE("DOUBLE"),
    TYPE_INTEGER("INTEGER"),
    TYPE_FORMULA("FORMULA"),
;
    final String label;
    ColCellType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
