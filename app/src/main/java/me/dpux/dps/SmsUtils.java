package me.dpux.dps;

import android.telephony.SmsManager;
import android.util.Log;

/**
 * Created by Deepak Mishra
 */
public class SmsUtils {

    private static final String TAG = "SmsUtils";

    public static void sendMessage(String[] phoneNumbers, String message){
        try{
            SmsManager smsManager = SmsManager.getDefault();

            for (String number : phoneNumbers){
                smsManager.sendTextMessage(number, null, message, null, null);
            }
        }catch (Exception e){
            Log.e(TAG, "Unhandled error, likely that sending SMS is disabled on device");
        }
    }
}
