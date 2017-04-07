package intrinsic_plant_equipment.plantequipment.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import intrinsic_plant_equipment.plantequipment.helper.Core;
import intrinsic_plant_equipment.plantequipment.helper.Endpoints;
import intrinsic_plant_equipment.plantequipment.helper.IEquipmentPreferences;
import intrinsic_plant_equipment.plantequipment.model.LoginClass;
import intrinsic_plant_equipment.plantequipment.model.Vehicle;
import intrinsic_plant_equipment.plantequipment.sqlLite.DbHelper;

/**
 * Created by Dirk on 8/03/2017.
 */

public class DeleteAndInsertVehicles extends AsyncTask<String, String, List<Vehicle>> {
    protected IEquipmentPreferences mPreferences;
    String TAG = Login.class.getSimpleName();
    ProgressDialog progress;
    Context context;
    boolean Redirect = false;


    public DeleteAndInsertVehicles(Context context, boolean redirect) {
        progress = Core.get().initiateProgressDialog(context);
        this.context = context;
        mPreferences = Core.get().getPreferences();
        Redirect = redirect;
    }

    @Override
    protected void onPreExecute() {
        try {
            progress.setMessage("Trying to insertvVehicles , please be patient....");
            progress.show();
        } catch (Exception err) {

            Core.get().showMessageShort("Unable to show DeleteAndInsertVehicles progress", context, TAG);

        }
    }

    @Override
    protected void onPostExecute(List<Vehicle> lstVehicles) {

        DbHelper db = null;

        try{
            if (progress.isShowing()) {
                progress.dismiss();
            }

            if (lstVehicles != null) {

                db = new DbHelper(context);
                db.deleteVehicle(context);

                for (int i = 0; i < lstVehicles.size(); i++) {
                    db.addVehicle(context, lstVehicles.get(i));
                }

                if (Redirect) {

                    DeleteAndInsertChecklist chList = new DeleteAndInsertChecklist(context,true);
                    chList.execute("start");

                }
            } else {

                Core.get().showMessage("Unable to DeleteAndInsertVehicles", context, TAG);
            }
        }
        catch(Exception err){

        }
        finally{

            if(db != null){
                db.close();
            }
        }



    }

    @Override
    protected void onProgressUpdate(String... values) {

        Core.get().showMessage(values[0], context, TAG);

    }

    @Override
    protected List<Vehicle> doInBackground(String... start) {

        try {

            mPreferences = Core.get().getPreferences();


            HttpURLConnection myURLConnection = Core.get().setupHttpConnectionGetWithToken(new URL(Endpoints.VEHICLE_URL), mPreferences);

            int responseCode = myURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Reader reader = new InputStreamReader(myURLConnection.getInputStream(), "UTF-8");
                Gson gson = new GsonBuilder().create();
                Type listType = new TypeToken<List<Vehicle>>() {
                }.getType();
                List<Vehicle> vehicleList = (List<Vehicle>) gson.fromJson(reader, listType);
                publishProgress("Success.");
                return vehicleList;

            } else {
                publishProgress("Unable to DeleteAndInsertVehicles .");
                return null;
            }


        } catch (Exception err) {

            publishProgress("Unable to Download Event Vehicles: " + err.getMessage());
        }

        return null;


    }
}
