package com.project.CmsApplication.Utility;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Component
public class DateFormatter {
    public final String parseDateFormat = "yyyy-MM-dd HH:mm:ss";
    public final String targetDateFormat = "yyyy-MM-dd";

    public String formatDate(String inputDate) throws ParseException {
        String result = null;
        result = new SimpleDateFormat(targetDateFormat).format(new SimpleDateFormat(parseDateFormat).parse(inputDate));
        return result;
    }

    public String getConfigValue() {
        return "";
    }
}
