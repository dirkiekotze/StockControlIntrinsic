package intrinsic_plant_equipment.plantequipment.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import intrinsic_plant_equipment.plantequipment.AuditItems;
import intrinsic_plant_equipment.plantequipment.helper.Core;
import intrinsic_plant_equipment.plantequipment.helper.Endpoints;
import intrinsic_plant_equipment.plantequipment.helper.IEquipmentPreferences;
import intrinsic_plant_equipment.plantequipment.model.ChecklistVehicleIds;
import intrinsic_plant_equipment.plantequipment.model.ItemAudit;
import intrinsic_plant_equipment.plantequipment.sqlLite.DbHelper;

/**
 * Created by Dirk on 8/03/2017.
 */

public class GetAuditItemsPerChecklistAndVehicleAndBarcode extends AsyncTask<ChecklistVehicleIds,String, List<ItemAudit>> {
    protected IEquipmentPreferences mPreferences;
    String TAG = Login.class.getSimpleName();
    ProgressDialog progress;
    Context context;
    boolean Redirect = false;


    public GetAuditItemsPerChecklistAndVehicleAndBarcode(Context context, boolean redirect) {
        progress = Core.get().initiateProgressDialog(context);
        this.context = context;
        mPreferences = Core.get().getPreferences();
        Redirect = redirect;
    }

    @Override
    protected void onPreExecute() {
        try {
            progress.setMessage("Trying to download auditItems, please be patient....");
            progress.show();
        } catch (Exception err) {

            Core.get().showMessageShort("Unable to show audit items progress", context, TAG);

        }
    }

    @Override
    protected void onPostExecute(List<ItemAudit> lstItemAudit) {

        DbHelper db = null;

        try {
            if (progress.isShowing()) {
                progress.dismiss();
            }

            if (lstItemAudit != null) {

                db = new DbHelper(context);

                for (int i = 0; i < lstItemAudit.size(); i++) {
                    db.addItemAudit(context, lstItemAudit.get(i));
                }

                if (Redirect) {

                    context.startActivity(new Intent(context, AuditItems.class));

                }
            } else {

                Core.get().showMessage("Unable to import Audit Items", context, TAG);
            }
        } catch (Exception err) {

            Core.get().showMessage("Unable to import Audit Items Message : " + err.getMessage(), context, TAG);

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
    protected List<ItemAudit> doInBackground(ChecklistVehicleIds... ids) {

        try {

            mPreferences = Core.get().getPreferences();

            HttpURLConnection myURLConnection = Core.get().setupHttpConnectionGetWithToken(new URL(Endpoints.ITEM_AUDIT_URL + "?checklistId=" + ids[0].getChecklistId() + "&vehicleId=" + ids[0].getVehicleId()), mPreferences);

            int responseCode = myURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Reader reader = new InputStreamReader(myURLConnection.getInputStream(), "UTF-8");
                Gson gson = new GsonBuilder().create();
                Type listType = new TypeToken<List<ItemAudit>>() {
                }.getType();
                List<ItemAudit> auditList = gson.fromJson(reader, listType);
                publishProgress("Success.");
                return auditList;

            } else {
                publishProgress("Unable to Download Audit Items.");
                return null;
            }


        } catch (Exception err) {

            publishProgress("Unable to Download Audit Items Message: " + err.getMessage());
        }

        return null;


    }
}
