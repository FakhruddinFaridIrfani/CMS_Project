package com.project.CmsApplication.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DateFormatter {
    public final String parseDateFormat = "yyyy-MM-dd HH:mm:ss";
    public final String targetDateFormat = "yyyy-MM-dd";

    public String formatDate(String inputDate) throws ParseException {
        String result = null;
        result = new SimpleDateFormat(targetDateFormat).format(new SimpleDateFormat(parseDateFormat).parse(inputDate));
        return result;
    }
}
