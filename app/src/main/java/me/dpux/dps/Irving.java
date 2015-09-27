package me.dpux.dps;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by Deepak Mishra on 3/28/15.
 */
public class Irving {

    private static final String TAG = "Irving";
    private static SharedPreferences sharedpreferences;
    private static SortedSet<String> latestAvailableDates = new TreeSet<String>();
    private static String DPS_PREFS = "DPS_PREFS";
    private static String LATEST_DATE_KEY = "LATEST_DATE_KEY";
    private static String NO_DATE = "No dates availabe";
    private static String NO_UPDATE_COUNT_KEY = "NO_UPDATE_COUNT_KEY";

    static final int SILENT_TIME = 5;  //number of silent intervals defined in PollingService.POLLING_PERIOD


    public static void main(Context context) throws IOException {

        String[] monthsToTest = { "092015", "102015","112015", "122015","012016"};     //list of months you want to check : mmYYYY format
        String[] phoneNumbers = {"1112223333", "4445555666"};   //numbers to send sms

        for(String month : monthsToTest) {
            start(month);
        }

        //TODO: send sms when we dont have any available dates
        if(latestAvailableDates.isEmpty()){
            Toast.makeText(context, "No dates available in given months", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "No dates available in given months");
            return;
        }

        String latestDate = latestAvailableDates.first();
        String latestDateInLastPoll = null;
        int noUpdateCount = 0;

        sharedpreferences = context.getSharedPreferences(DPS_PREFS, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(LATEST_DATE_KEY)) {
            latestDateInLastPoll = sharedpreferences.getString(LATEST_DATE_KEY, NO_DATE);
        }
        if (sharedpreferences.contains(NO_UPDATE_COUNT_KEY)) {
            noUpdateCount = sharedpreferences.getInt(NO_UPDATE_COUNT_KEY, 0);
        }

        String date, month, readableDate;
        date = latestDate.substring(6, 8);
        month = latestDate.substring(4, 6);
        if(month.equals("01")){
            month = "January";
        } else if(month.equals("02")){
            month = "February";
        } else if(month.equals("03")){
            month = "March";
        } else if(month.equals("04")){
            month = "April";
        } else if(month.equals("05")){
            month = "May";
        } else if(month.equals("06")){
            month = "June";
        } else if(month.equals("07")){
            month = "July";
        } else if(month.equals("08")){
            month = "August";
        } else if(month.equals("09")){
            month = "September";
        } else if(month.equals("10")){
            month = "October";
        } else if(month.equals("11")){
            month = "November";
        } else if(month.equals("12")){
            month = "December";
        } else {
            month = "Unknown";
        }

        readableDate = month + " " + date;

        if (latestDate.equals(latestDateInLastPoll)) {
            //notify if SILENT_TIME is over
            if (noUpdateCount > SILENT_TIME) {

                SmsUtils.sendMessage(phoneNumbers, "No updates. Still : "+readableDate);
                Log.i(TAG,"SMS sent for no updates");

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt(NO_UPDATE_COUNT_KEY, 0);   //reset timer
                editor.commit();
            } else {
                //dont notify
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putInt(NO_UPDATE_COUNT_KEY, noUpdateCount + 1);
                editor.commit();
                Log.i(TAG, "No SMS sent as there are no updates");
            }

        } else {
            //send sms and commit new date
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(LATEST_DATE_KEY, latestDate);
            editor.commit();
            SmsUtils.sendMessage(phoneNumbers, "Latest date updated : "+readableDate);
            Log.i(TAG, "SMS sent with updated time");
        }

    }

    static void start(String monthAndYear) throws IOException{
        String currDate = null, prevDate = null, prevPrevDate = null, date_ymd = null;

        final String startDate = "01";
        final String endDate = "31";

        //assumption is that monthAndYear will be of format mmYYYY
        String month = monthAndYear.substring(0,2);
        int year = Integer.parseInt(monthAndYear.substring(2));

        try {
            int currMonth = Integer.parseInt(month);
            if (currMonth > 2) {
                currDate = String.valueOf(year) + month + startDate;
                prevDate = String.valueOf(year) + String.format("%01d",currMonth-1) + startDate;
                prevPrevDate = String.valueOf(year) + String.format("%01d",currMonth-2) + startDate;
                date_ymd = String.valueOf(year) + String.format("%01d",currMonth-1) + endDate;
            } else if (currMonth  == 1){
                currDate = String.valueOf(year) + month + startDate;
                prevDate = String.valueOf(year-1) + String.format("%01d",12) + startDate;
                prevPrevDate = String.valueOf(year-1) + String.format("%01d",11) + startDate;
                date_ymd = String.valueOf(year-1) + String.format("%01d",12) + endDate;
            } else if(currMonth == 2){
                currDate = String.valueOf(year) + month + startDate;
                prevDate = String.valueOf(year) + String.format("%01d", 01) + startDate;
                prevPrevDate = String.valueOf(year-1) + String.format("%01d",12) + startDate;
                date_ymd = String.valueOf(year) + String.format("%01d",01) + endDate;
            }

        } catch (Exception e){
            e.printStackTrace();
        }



        Document doc = Jsoup.connect("https://booknow.securedata-trans.com/1qecsc5v/")   //Update path parameter here
                .data("selection-form", "yes")
                .data("d", "appointplus210")
                .data("page", "10")
                .data("m", "2")
                .data("type", "17")
                .data("auth", "yes")
                .data("action", "log_in")
                .data("customer_id", "1613082")       //update
                .data("customer_location_id", "551")     //update
                .data("day_name", "any")
                .data("location_id", "551")      //update
                .data("id", "551")        //update
                .data("headquarters_id", "2")       //update
                .data("service_id", "2")
                .data("e_id", "all")
                .data("next_date", currDate)
                .data("prev_date", prevPrevDate)
                .data("starting_date", prevDate)
                .data("date_ymd", date_ymd)
                .data("view_prev_month", "no")
                .data("view_next_month", "yes")
                .referrer("https://booknow.securedata-trans.com/1qecsc5v/")
                .userAgent("Mozilla")
                .cookie("PHPSESSID", "k2qtfgkehht9705kp6p0c1ft21")        //update
                .post();

        Elements links = doc.select("a[id=cv-leftnav-item-calendar-available-id]");

        for (Element link : links) {
            String jsValue = link.attr("href");
            jsValue = jsValue.replaceFirst("javascript:dosubmit", "");
            String date = jsValue.split(",")[0];
            date = date.replaceAll("[(']", "");

            Log.i(TAG, date);
            latestAvailableDates.add(date);


        }
        Log.d(TAG, "Done with : "+monthAndYear);
    }

}
