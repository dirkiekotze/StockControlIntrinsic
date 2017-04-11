package intrinsic_plant_equipment.plantequipment.helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Dirk on 8/03/2017.
 */

public class Core {

    private static Core instance = null;
    String TAG = Core.class.getSimpleName();
    private Context context;
    private IEquipmentPreferences equipmentPreferences;

    private Core() {
    }

    public static Core get() {

        if (instance == null) {
            instance = new Core();
        }

        return instance;
    }

    public void hideKeyboard(Context context, Activity activity) {

        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getCurrentFocus().getWindowToken(), 0);

    }

    public static String getDateNowString() {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = formatter.format(new Date().getTime());
        return formattedDate;
    }

    public static long getDaysFromNowToDate(Date dateIn) {

        long diff = dateIn.getTime() - new Date().getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long elapsedDays = diff / daysInMilli;
        return elapsedDays;

    }

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public IEquipmentPreferences getPreferences() {

        return equipmentPreferences;
    }

    public String convertDatePickerDateToMilliseconds(String date) {

        if (date.equals("")) {
            return "";
        } else {
            String[] splitDate = date.split("-");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.valueOf(splitDate[2]), Integer.valueOf(splitDate[1]), Integer.valueOf(splitDate[0]));
            return String.valueOf(calendar.getTimeInMillis());
        }


    }

    public void logMessage(String text, String tag) {
        String message = text;
        Log.e(tag, message);
    }

    public void setPrefs(IEquipmentPreferences equipmentPreferences) {

        this.equipmentPreferences = equipmentPreferences;
    }

    public ProgressDialog initiateProgressDialog(Context context) {
        ProgressDialog progress;
        progress = new ProgressDialog(context);
        progress.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        return progress;
    }

    public void showMessage(String text, Context ctx, String tag) {
        String message = text;
        Log.e(tag, message);
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    public void showMessageShort(String text, Context ctx, String tag) {
        String message = text;
        Log.e(tag, message);
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }
        else{
            return false;
        }
    }

    //For Extracting User Info: In order to create OauthToken
    public HttpURLConnection setupHttpConnectionForPostWithoutToken(URL myURL) throws IOException {
        HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
        myURLConnection.setRequestMethod("POST");
        myURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        myURLConnection.setDoOutput(true);
        myURLConnection.setDoInput(true);
        return myURLConnection;
    }

    public HttpURLConnection setupHttpConnectionGetWithToken(URL myURL, IEquipmentPreferences mPreferences) throws IOException {
        HttpURLConnection myURLConnection = null;
        myURLConnection = (HttpURLConnection) myURL.openConnection();
        myURLConnection.setRequestMethod("GET");
        myURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        return myURLConnection;
    }
}
