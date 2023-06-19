package vn.com.lifesup.hackathon.util;


public class Constants {
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";
    public static final String LANG_KEY = "LANG_KEY";
    public static final String DEFAULT_LANG_KEY = "vi";
    public static final String TYPE_NUMBER = "LONG,INTEGER,SHORT,BYTE,INT,DOUBLE,FLOAT";
    public final static String DATE_FORMAT = "dd/MM/yyyy";
    public final static String YYYY_MM_DD = "yyyy-MM-dd";
    public final static String DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public final static String PASSWORD_DEFAULT = "123456Aa@";
    public final static String SCOPE = "read";
    public final static String clientId = "lifesup_hrm";
    public final static String clientSecret = "lifesup_hrm_secret";
    public final static String redirectUrl = "103.226.248.168:8097/hrm-web/login";
    public final static String ERROR = "ERROR";
    public final static String SCHEMA = "nhansu";
    public static final char DEFAULT_ESCAPE_CHAR = '\\';

    /*TABLE_NAME*/
    public static final String TABLE_DEPARTMENT = "DEPARTMENT";
    /*End TABLE_NAME*/

    /*MODULE_NAME*/
    public static final int MODULE_DEPARTMENT_CRATE = 1;
    /*End MODULE_NAME*/

    /*VERSION STATUS*/
    public static final int STATUS_NEW = 0;
    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_DELETE = 2;
    /*End VERSION STATUS*/

    public static final String DEFAULT_PASSWORD = "123qwe!#";

    public interface TIME_TYPE {
        Long DATE = 1L;
        Long MONTH = 2L;
        Long QUARTER = 3L;
        Long YEAR = 4L;
    }

    public interface SPECIAL_CHAR {
        String SPACE = " ";
        String UNDERSCORE = "_";
        String DOT = ".";
        String COMMA = ",";
        String COLON = ":";
        String SLASH = "/";
        String BACKSLASH = "\\";
    }


    public interface FILE_TYPE {
        int IMAGE = 0;
        int CONTRACT_DOCUMENT = 1;
        int ACCEPTANCE_DOCUMENT = 2;
    }

    public static final String SUCCESS_CODE = "00";


}
