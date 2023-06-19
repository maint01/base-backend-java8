package vn.com.lifesup.hackathon.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

public class HeaderLocaleResolver implements LocaleResolver {

    private Locale defaultLocale;

    private String headerName;

    public static final String LOCALE_REQUEST_ATTRIBUTE_NAME = "LANG_KEY.LOCALE";

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        parseAngularHeaderIfNecessary(request);
        return (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, (locale != null ? locale : defaultLocale));
    }

    public void setDefaultLocale(Locale locale) {
        this.defaultLocale = locale;
    }

    public void setHeaderLangName(String headerName) {
        this.headerName = headerName;
    }

    private void parseAngularHeaderIfNecessary(HttpServletRequest request) {
        if (request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME) == null) {
            Locale locale;
            String localeStr = request.getHeader(headerName);
            if (localeStr == null) locale = defaultLocale;
            else {
                locale = Locale.forLanguageTag(localeStr);
            }

            request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, locale);
        }
    }

}
