package com.playerhub.utils;

import com.playerhub.ui.dashboard.home.Events;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {


//    public static Date StringToDate(String strDate, String format) throws ModuleException {
//        Date dtReturn = null;
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
//        try {
//            dtReturn = simpleDateFormat.parse(strDate);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return dtReturn;
//    }

    public static boolean check(String first, String second) {

        return first.toLowerCase().equalsIgnoreCase(second.toLowerCase());

    }

    public static String convertDateToString(Date date, String format) {
        String dateStr = null;
        DateFormat df = new SimpleDateFormat(format);
        try {
            dateStr = df.format(date);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return dateStr;
    }

    public static Date convertStringToDate(String dateStr, String format) {
        Date date = null;
        DateFormat df = new SimpleDateFormat(format);
        try {
            date = df.parse(dateStr);
        } catch (Exception ex) {
            System.out.println(ex);
        }
        return date;
    }


    public static List<Events> getListOfEvents() {

        List<Events> list = new ArrayList<>();

        Events events = new Events();
        events.setDates("18 SEP");
        events.setTime("09:30 AM");
        events.setName("Euro Beach Soccer League 2018");
        events.setName2("Disney trip");
        events.setName3("Orlando,FL,USA");
        list.add(events);

        events = new Events();
        events.setDates("3 SEP");
        events.setTime("01:30 AM");
        events.setName("Youth Olympic Futsual Tournaments");
        events.setName2("Tournament trip");
        events.setName3("Orlando,FL,USA");
        list.add(events);

        events = new Events();
        events.setDates("25 AUG");
        events.setTime("10:00 AM");
        events.setName("IFA Club World Cup UAE 2018");
        events.setName2("Practice trip");
        events.setName3("Orlando,FL,USA");
        list.add(events);

        return list;

    }
}
