package intrinsic_plant_equipment.plantequipment.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import intrinsic_plant_equipment.plantequipment.helper.Core;
import intrinsic_plant_equipment.plantequipment.helper.Endpoints;
import intrinsic_plant_equipment.plantequipment.helper.IEquipmentPreferences;
import intrinsic_plant_equipment.plantequipment.model.Item;
import intrinsic_plant_equipment.plantequipment.model.VehicleAudit;
import intrinsic_plant_equipment.plantequipment.sqlLite.DbHelper;

/**
 * Created by Dirk on 8/03/2017.
 */

public class InsertNewVehicleAuditToCloud extends AsyncTask<String, String, String> {
    protected IEquipmentPreferences mPreferences;
    String TAG = InsertNewVehicleAuditToCloud.class.getSimpleName();
    ProgressDialog progress;
    Context context;
    boolean Redirect = false;


    public InsertNewVehicleAuditToCloud(Context context, boolean redirect) {
        progress = Core.get().initiateProgressDialog(context);
        this.context = context;
        Redirect = redirect;
    }

    @Override
    protected void onPreExecute() {
        try {
            progress.setMessage("Uploading Items to Cloud, please be patient....");
            progress.show();
        } catch (Exception err) {

            Core.get().showMessageShort("Unable to show InsertNewItemsToCloud progress", context, TAG);

        }
    }

    @Override
    protected void onPostExecute(String result) {

        DbHelper db = null;

        try {

            db = DbHelper.getInstance(context);

            if (progress.isShowing()) {
                progress.dismiss();
            }

            if (result.equals("success")) {

                Core.get().showMessage("Successfully uploaded Items to Cloud", context, TAG);

                DeleteAndInsertVehicles updateVehicles = new DeleteAndInsertVehicles(context,false);
                updateVehicles.execute("start");


            } else {
                Core.get().showMessage("Unable to upload VehicleAudit to Cloud", context, TAG);
            }
        } catch (Exception err) {

            Core.get().showMessage("Unable to upload VehicleAudit to Cloud Message : " + err.getMessage(), context, TAG);

        } finally {

            if (db != null) {
                db.close();
            }
        }


    }

    @Override
    protected void onProgressUpdate(String... values) {

        Core.get().showMessage(values[0], context, TAG);

    }

    @Override
    protected String doInBackground(String... start) {

        DbHelper db = null;
        StringBuilder jsonString = new StringBuilder();

        try {

            db = new DbHelper(context);
            List<VehicleAudit> vehicleAuditLst = db.getAllVehicleAudits(context);

            if (vehicleAuditLst != null) {

                Gson gson = new Gson();
                String json = gson.toJson(vehicleAuditLst);
                HttpURLConnection myURLConnection = Core.get().setupHttpConnectionForPostWithoutToken(new URL(Endpoints.VEHICLE_AUDIT_URL));

                OutputStreamWriter writer = new OutputStreamWriter(myURLConnection.getOutputStream(), "UTF-8");
                writer.write("{\"auditItem\":" + json + "}");
                writer.close();

                String line;
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        jsonString.append(line);
                    }
                    br.close();
                } catch (Exception err) {

                    publishProgress("Unable to insert new VehicleAudit record to cloud. Message " + err.getMessage());
                    return "error";

                } finally {

                    if (db != null) {

                        db.close();
                    }
                }
                myURLConnection.disconnect();

                return "success";

            } else {

                return "nothing";

            }
        } catch (Exception err) {

            publishProgress("Unable to insert new VehicleAudit record to cloud. Message " + err.getMessage());
            return "error";
        }

    }
}
