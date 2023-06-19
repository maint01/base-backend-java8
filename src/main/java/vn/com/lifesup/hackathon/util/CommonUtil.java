package vn.com.lifesup.hackathon.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import vn.com.lifesup.hackathon.dto.base.BaseSearchDTO;
import vn.com.lifesup.hackathon.exception.ServerException;
import vn.com.lifesup.hackathon.security.UserDetails;
import vn.com.lifesup.hackathon.security.jwt.JwtPayload;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@UtilityClass
public class CommonUtil {
    private static final Logger logger = LoggerFactory.getLogger(CommonUtil.class);
    private static final String YYYY_PT = "yyyy";
    private static final String YYYYmm_PT = "yyyyMM";


    public static String convertImageToBase64(String filepath) throws IOException {
        File file = new File(filepath);
        String encodedString = "";
        if (file.exists()) {
            byte[] fileContent = FileUtils.readFileToByteArray(file);
            encodedString = Base64.getEncoder().encodeToString(fileContent);
        }
        return encodedString;
    }

    public static boolean validateImageExtension(String str) {
        // Regex to check valid image file extension.
        if (str.matches("[^\\s]+(.*?)\\.(jpg|jpeg|png|gif|JPG|JPEG|PNG|GIF)$")) {
            return true;
        } else {
            return false;
        }
    }
   public static boolean validateExtension(String str) {
        // Regex to check valid image file extension.
        if (str.toLowerCase().matches("[^\\s]+(.*?)\\.(xlsx|xls|xlsm|pdf|docx|pptx|doc)$")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean validateContract(String str) {
        // Regex to check valid image file extension.
        if (str.toLowerCase().matches("[^\\s]+(.*?)\\.(xlsx|xls|xlsm|pdf|docx|doc)$")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$");
        Matcher matcher = pattern.matcher(email);

        return matcher.find();
    }

    public static boolean validatePhoneNumber(String phoneNo) {
        //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{10}")) return true;
            //validating phone number with -, . or spaces
        else if (phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;
            //validating phone number with extension length from 3 to 5
        else if (phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
            //validating phone number where area code is in braces ()
        else if (phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}")) return true;
        else if (phoneNo.matches("\\(\\d{3}\\)\\d{8}")) return true;
        else if (phoneNo.matches("^\\+(?:[0-9] ?){6,14}[0-9]$")) return true;
        else if (phoneNo.matches("^(0)([9235678]{1}[0-9]{8})$")) return true;
        else if (phoneNo.matches("\\d{11}")) return true;

            //return false if nothing matches the input
        else return false;

    }

    public static boolean isDate(String strDate, Boolean isFullDateTime) {
        if (strDate == null || StringUtils.EMPTY.equals(strDate)) {
            return false;
        }
        try {
            if (isFullDateTime) {
                if (strDate.length() == Constants.DATETIME_FORMAT.length()) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATETIME_FORMAT);
                    simpleDateFormat.setLenient(false);
                    simpleDateFormat.parse(strDate);
                } else {
                    return false;
                }
            } else {
                if (strDate.length() == Constants.DATE_FORMAT.length()) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
                    simpleDateFormat.setLenient(false);
                    simpleDateFormat.parse(strDate);
                } else {
                    return false;
                }
            }
            return true;
        } catch (ParseException e) {
            logger.error("Loi! convertStringToDate: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Convert string date to date
     *
     * @param strDate
     * @param isFullDateTime:true: full date time, false: date sort
     * @return
     */
    public static Date convertStringToDate(String strDate, Boolean isFullDateTime) {
        if (strDate == null || "".equals(strDate)) {
            return null;
        }
        if (isFullDateTime) {
            if (strDate.length() != Constants.DATETIME_FORMAT.length()) {
                return null;
            }
        } else {
            if (strDate.length() != Constants.DATE_FORMAT.length()) {
                return null;
            }
        }
        try {
            Date date;
            SimpleDateFormat simpleDateFormat;
            if (isFullDateTime) {
                simpleDateFormat = new SimpleDateFormat(Constants.DATETIME_FORMAT);
            } else {
                simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
            }
            simpleDateFormat.setLenient(false);
            date = simpleDateFormat.parse(strDate);
            return date;
        } catch (ParseException e) {
            logger.error("Loi! convertStringToDate: " + e.getMessage());
        }
        return null;
    }

    public static Instant convertToInstant(String strDate, boolean isFullDateTime) {
        Date date = convertStringToDate(strDate, isFullDateTime);
        return date == null ? null : date.toInstant();
    }

    public static String convertToString(Instant instant, boolean isFullDateTime) {
        if (instant == null) {
            return StringUtils.EMPTY;
        }
        Date date = new Date(instant.getEpochSecond() * 1000);
        return dateToString(date, isFullDateTime ? Constants.DATETIME_FORMAT : Constants.DATE_FORMAT);
    }

    public static Instant plusDays(Instant date, long to) {
        return date.plus(to, ChronoUnit.DAYS);
    }

    /**
     * Copy du lieu tu bean sang bean moi
     * Luu y chi copy duoc cac doi tuong o ngoai cung, list se duoc copy theo tham chieu
     * <p>
     * Chi dung duoc cho cac bean java, khong dung duoc voi cac doi tuong dang nhu String, Integer, Long...
     *
     * @param source
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T cloneBean(T source) {
        try {
            if (source == null) {
                return null;
            }
            T dto = (T) source.getClass().getConstructor().newInstance();
            BeanUtils.copyProperties(source, dto);
            return dto;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /*
     * Kiem tra Long bi null hoac zero
     *
     * @param value
     * @return
     */
    public boolean isNullOrZero(Long value) {
        return (value == null || value.equals(0L));
    }

    public boolean isNullOrZero(Integer value) {
        return (value == null || value.equals(0));
    }

    /**
     * Upper first character
     *
     * @param input
     * @return
     */
    public String upperFirstChar(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    /**
     * safe equal
     *
     * @param obj1 String
     * @param obj2 String
     * @return boolean
     */
    public boolean safeEqual(String obj1, String obj2) {
        if (obj1 == obj2) return true;
        return ((obj1 != null) && (obj2 != null) && obj1.equals(obj2));
    }

    /**
     * check null or empty
     * Su dung ma nguon cua thu vien StringUtils trong apache common lang
     *
     * @param cs String
     * @return boolean
     */
    public boolean isNullOrEmpty(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Ham nay mac du nhan tham so truyen vao la object nhung gan nhu chi hoat dong cho doi tuong la string
     * Chuyen sang dung isNullOrEmpty thay the
     *
     * @param obj1
     * @return
     */
    @Deprecated
    public boolean isStringNullOrEmpty(Object obj1) {
        return obj1 == null || "".equals(obj1.toString().trim());
    }

    public BigInteger length(BigInteger from, BigInteger to) {
        return to.subtract(from).add(BigInteger.ONE);
    }

    public BigDecimal add(BigDecimal number1, BigDecimal number2, BigDecimal... numbers) {
        List<BigDecimal> realNumbers = Lists.newArrayList(number1, number2);
        if (!ArrayUtils.isEmpty(numbers)) {
            Collections.addAll(realNumbers, numbers);
        }
        return realNumbers.stream()
                .filter(x -> x != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long add(Long number1, Long number2, Long... numbers) {
        List<Long> realNumbers = Lists.newArrayList(number1, number2);
        if (!ArrayUtils.isEmpty(numbers)) {
            Collections.addAll(realNumbers, numbers);
        }
        return realNumbers.stream()
                .filter(Objects::nonNull)
                .reduce(0L, Long::sum);
    }

    /**
     * add
     *
     * @param obj1 BigDecimal
     * @param obj2 BigDecimal
     * @return BigDecimal
     */
    public BigInteger add(BigInteger obj1, BigInteger obj2) {
        if (obj1 == null) {
            return obj2;
        } else if (obj2 == null) {
            return obj1;
        }

        return obj1.add(obj2);
    }


    /**
     * Collect values of a property from an object list instead of doing a for:each then call a getter
     * Consider using stream -> map -> collect of java 8 instead
     *
     * @param source       object list
     * @param propertyName name of property
     * @param returnClass  class of property
     * @return value list of property
     */
    @Deprecated
    public <T> List<T> collectProperty(Collection<?> source, String propertyName, Class<T> returnClass) {
        List<T> propertyValues = Lists.newArrayList();
        try {
            String getMethodName = "get" + upperFirstChar(propertyName);
            for (Object x : source) {
                Class<?> clazz = x.getClass();
                Method getMethod = clazz.getMethod(getMethodName);
                Object propertyValue = getMethod.invoke(x);
                if (propertyValue != null && returnClass.isAssignableFrom(propertyValue.getClass())) {
                    propertyValues.add(returnClass.cast(propertyValue));
                }
            }
            return propertyValues;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Lists.newArrayList();
        }
    }

    /**
     * Collect distinct values of a property from an object list instead of doing a for:each then call a getter
     * Consider using stream -> map -> collect of java 8 instead
     *
     * @param source       object list
     * @param propertyName name of property
     * @param returnClass  class of property
     * @return value list of property
     */
    @Deprecated
    public <T> Set<T> collectUniqueProperty(Collection<?> source, String propertyName, Class<T> returnClass) {
        List<T> propertyValues = collectProperty(source, propertyName, returnClass);
        return Sets.newHashSet(propertyValues);
    }

    public boolean isNullObject(Object obj1) {
        if (obj1 == null) {
            return true;
        }
        if (obj1 instanceof String) {
            return isNullOrEmpty(obj1.toString());
        }
        return false;
    }

    public boolean isCollection(Object ob) {
        return ob instanceof Collection || ob instanceof Map;
    }

    public String makeLikeParam(String s) {
        if (StringUtils.isEmpty(s)) return s;
        s = s.trim().toLowerCase()
                .replace("\\", "\\\\")
                .replace("\\t", "\\\\t")
                .replace("\\n", "\\\\n")
                .replace("\\r", "\\\\r")
                .replace("\\z", "\\\\z")
                .replace("\\b", "\\\\b")
                .replace("&", Constants.DEFAULT_ESCAPE_CHAR + "&")
                .replace("%", Constants.DEFAULT_ESCAPE_CHAR + "%")
                .replace("_", Constants.DEFAULT_ESCAPE_CHAR + "_");
        return "%" + s + "%";
    }

    /**
     * @param date
     * @param format yyyyMMdd, yyyyMMddhhmmss,yyyyMMddHHmmssSSS only
     * @return
     */
    public Integer getDateInt(Date date, String format) {
        if (date == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String dateStr = sdf.format(date);
        return Integer.parseInt(dateStr);
    }

    public Long getDateLong(Date date, String format) {
        if (date == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String dateStr = sdf.format(date);
        return Long.parseLong(dateStr);
    }

    private void resetTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
    }

    public Date getFirstDateOfMonth(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        resetTime(cal);
        return cal.getTime();
    }

    public Date getFirstDayOfQuarter(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) / 3 * 3);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        resetTime(cal);
        return cal.getTime();
    }

    public Date getFirstDayOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //Thang 1 thi calendar.MONTH = 0
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DATE, 1);
        resetTime(cal);
        return cal.getTime();
    }

    public Date getAbsoluteDate(Date date, Integer relativeTime, Object timeType) {
        if (relativeTime == null) return date;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (Constants.TIME_TYPE.DATE.equals(timeType) || Constants.TIME_TYPE.DATE.toString().equals(timeType)) {
            cal.add(Calendar.DATE, relativeTime);
        } else if (Constants.TIME_TYPE.MONTH.equals(timeType) || Constants.TIME_TYPE.MONTH.toString().equals(timeType)) {
            cal.add(Calendar.MONTH, relativeTime);
        } else if (Constants.TIME_TYPE.QUARTER.equals(timeType) || Constants.TIME_TYPE.QUARTER.toString().equals(timeType)) {
            cal.add(Calendar.MONTH, (relativeTime) * 3);
        } else if (Constants.TIME_TYPE.YEAR.equals(timeType) || Constants.TIME_TYPE.YEAR.toString().equals(timeType)) {
            cal.add(Calendar.YEAR, relativeTime);
        }
        return cal.getTime();
    }

    public Date getDatePattern(String date, String pattern) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(date);
    }

    public String formatDatePattern(Integer prdId, String pattern) {
        String result = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date date = sdf.parse(prdId.toString());

            SimpleDateFormat sdf2 = new SimpleDateFormat(pattern);
            result = sdf2.format(date);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return result;
    }

    public String formatQuarterPattern(Integer prdId) {
        String result = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date date = sdf.parse(prdId.toString());

            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
            String result2 = sdf2.format(date);

            result = (date.getMonth() / 3 + 1) + "/" + result2;
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return result;
    }

    public Date add(Date fromDate, int num, int type) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fromDate);
        cal.add(type, num);
        return cal.getTime();
    }

    public String dateToString(Date fromDate, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(fromDate);
    }

    public String dateToStringQuarter(Date fromDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String year = sdf.format(fromDate);
        return (fromDate.getMonth() / 3 + 1) + "/" + year;
    }

    public String getTimeValue(Date date, Integer timeType) {
        SimpleDateFormat YYYY = new SimpleDateFormat(YYYY_PT);
        SimpleDateFormat YYYYMM = new SimpleDateFormat(YYYYmm_PT);
        String value = null;
        if (Constants.TIME_TYPE.YEAR.equals(timeType)) {
            value = YYYY.format(date);
        } else if (Constants.TIME_TYPE.MONTH.equals(timeType)) {
            value = YYYYMM.format(date);
        } else if (Constants.TIME_TYPE.QUARTER.equals(timeType)) {
            value = YYYY.format(date);
            int quarter = date.getMonth() / 3 + 1;
            value = value + "" + quarter;
        }
        return value;
    }

    // ----- start ----
    public Integer transformDateByTimeType(Integer date, Long timeType) {
        if (Constants.TIME_TYPE.DATE.equals(timeType)) {
            return date;
        } else if (Constants.TIME_TYPE.MONTH.equals(timeType)) {
            return date - (date % 100) + 1;
        } else if (Constants.TIME_TYPE.QUARTER.equals(timeType)) {
            return date - (date % 100) + 1;
        } else if (Constants.TIME_TYPE.YEAR.equals(timeType)) {
            return date - (date % 10000) + 101;
        } else {
            throw new ServerException(ErrorCode.NOT_VALID, "timeType");
        }
    }

    public static String convertObjectToStringJson(Object object) {
        String strMess = "";

        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            strMess = gson.toJson(object);
        } catch (Exception var4) {
            logger.error("Loi convertObjectToStringJson ", var4);
        }

        return strMess;
    }

    public static JwtPayload getCurrentUserInfo() {
        Object userDetails = SecurityContextHolder.getContext().getAuthentication().getDetails();
        return ((UserDetails) userDetails).getUser();
    }

    public static String getClientIdFromUser() {
        JwtPayload jwtPayload = getCurrentUserInfo();
        return jwtPayload.getClientId();
    }

    public static String getCurrentUsername() {
        JwtPayload jwtPayload = getCurrentUserInfo();
        return jwtPayload.getUsername();
    }

    public static Pageable getPageable(BaseSearchDTO params) {
        if (!ObjectUtils.isEmpty(params.getPage()) && !ObjectUtils.isEmpty(params.getPageSize())) {
            return PageRequest.of(params.getPage(), params.getPageSize());
        } else {
            return PageRequest.of(0, Integer.MAX_VALUE);
        }
    }

    public static Pageable getPageable(BaseSearchDTO params, Sort sort) {
        if (!ObjectUtils.isEmpty(params.getPage()) && !ObjectUtils.isEmpty(params.getPageSize())) {
            return PageRequest.of(params.getPage(), params.getPageSize(), sort);
        } else {
            return PageRequest.of(0, Integer.MAX_VALUE, sort);
        }
    }

    public static Long convertDateToLong(String strDate) {
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date parseDate = f.parse(strDate);
            return parseDate.getTime();
        } catch (ParseException e) {
            log.error("An exception occurred when parser date");
        }
        return null;
    }

    public static String standardName(String name) {
        if (StringUtils.isBlank(name)) {
            return StringUtils.EMPTY;
        }
        name = name.toLowerCase().replaceAll("\\s", " ");
        StringTokenizer stringTokenizer = new StringTokenizer(name, " ");
        List<String> str = new ArrayList<>();
        String token;
        while (stringTokenizer.hasMoreElements()) {
            token = stringTokenizer.nextToken();
            str.add(String.valueOf(token.charAt(0)).toUpperCase() + token.substring(1));
        }
        return String.join(" ", str);
    }

    public static boolean compareDate(String fromDateStr, String toDateStr, boolean isFullDate, long numberBetween) {
        Date fromDate = convertStringToDate(fromDateStr, isFullDate);
        Date toDate = convertStringToDate(toDateStr, isFullDate);
        if (fromDate != null && toDate != null) {
            if (fromDate.getTime() > toDate.getTime()) {
                return false;
            }
            long dayBetweenInMilliseconds =  toDate.getTime() - fromDate.getTime();
            long dayBetween = TimeUnit.DAYS.convert(dayBetweenInMilliseconds, TimeUnit.MILLISECONDS);
            if (dayBetween > 0 && dayBetween > numberBetween) {
                return false;
            }
        }
        return true;
    }

    public static String generateDocumentPath(String bucket, String fileName) {
        return String.format("%s/%s_%d.%s", bucket,
                FilenameUtils.getBaseName(fileName),
                System.currentTimeMillis(),
                FilenameUtils.getExtension(fileName));
    }

    public static boolean isNumber(String src) {
        if (StringUtils.isEmpty(src)) {
            return false;
        }
        try {
            Double.parseDouble(src);
            return true;
        } catch (NumberFormatException ex) {
            log.error(ex.getMessage(), ex.toString());
            return false;
        }
    }
}
