package intrinsic_plant_equipment.plantequipment.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import intrinsic_plant_equipment.plantequipment.helper.Core;
import intrinsic_plant_equipment.plantequipment.helper.Endpoints;
import intrinsic_plant_equipment.plantequipment.helper.EquipmentPreferences;
import intrinsic_plant_equipment.plantequipment.helper.IEquipmentPreferences;
import intrinsic_plant_equipment.plantequipment.model.LoginClass;
import intrinsic_plant_equipment.plantequipment.model.StaffInfo;

import static java.lang.System.err;

/**
 * Created by Dirk on 8/03/2017.
 */

public class Login extends AsyncTask<LoginClass, String, StaffInfo> {
    protected IEquipmentPreferences mPreferences;
    String TAG = Login.class.getSimpleName();
    ProgressDialog progress;
    Context context;
    boolean Redirect = false;

    public Login(Context context,boolean redirect) {
        progress = Core.get().initiateProgressDialog(context);
        this.context = context;
        mPreferences = Core.get().getPreferences();
        Redirect = redirect;
    }

    @Override
    protected void onPreExecute() {
        try {
            progress.setMessage("Trying to Login, please be patient....");
            progress.show();
        } catch (Exception err) {

            Core.get().showMessageShort("Unable to show login progress", context, TAG);

        }
    }

    @Override
    protected void onPostExecute(StaffInfo staffInfo) {

        try{
            if (progress.isShowing()) {
                progress.dismiss();
            }

            if(staffInfo != null){

                mPreferences.setAccessLevel(staffInfo.getAccessLevel());
                mPreferences.setUserId(staffInfo.getStaffId());
                if(Redirect){

                    DeleteAndInsertVehicles vehiclesAsync = new DeleteAndInsertVehicles(context,true);
                    vehiclesAsync.execute("start");

                }
            }
            else{

                Core.get().showMessage("Unable to Login",context,TAG);
            }
        }
        catch(Exception err){

            Core.get().showMessage("Unable to Login",context,TAG);

        }

    }

    @Override
    protected void onProgressUpdate(String... values) {

        Core.get().showMessage(values[0], context, TAG);

    }

    @Override
    protected StaffInfo doInBackground(LoginClass... login) {

        try {


            Gson gson = new Gson();
            String json = gson.toJson(login[0]);
            StringBuilder returnValue = new StringBuilder();

            HttpURLConnection myURLConnection = Core.get().setupHttpConnectionForPostWithoutToken(new URL(Endpoints.LOGIN_URL));
            myURLConnection.connect();
            OutputStreamWriter writer = new OutputStreamWriter(myURLConnection.getOutputStream(), "UTF-8");
            writer.write(json);
            writer.close();

            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
            while ((line = br.readLine()) != null) {
                returnValue.append(line).append('\n');
            }
            br.close();

            myURLConnection.disconnect();

            JSONObject userObj;
            userObj = new JSONObject(returnValue.toString());
            StaffInfo staffInfo = new StaffInfo(userObj.getString("access_level"), userObj.getInt("staff_id"));
            return staffInfo;


        } catch (Exception err) {

            publishProgress("Unable to Login : " + err.getMessage());
            return null;
        }


    }
}
