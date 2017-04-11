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
import java.util.Date;
import java.util.List;

import intrinsic_plant_equipment.plantequipment.AuditItems;
import intrinsic_plant_equipment.plantequipment.VehicleAndChecklistSelect;
import intrinsic_plant_equipment.plantequipment.helper.Core;
import intrinsic_plant_equipment.plantequipment.helper.Endpoints;
import intrinsic_plant_equipment.plantequipment.helper.IEquipmentPreferences;
import intrinsic_plant_equipment.plantequipment.model.Category;
import intrinsic_plant_equipment.plantequipment.model.ChecklistVehicleIds;
import intrinsic_plant_equipment.plantequipment.model.ItemAudit;
import intrinsic_plant_equipment.plantequipment.sqlLite.DbHelper;

/**
 * Created by Dirk on 8/03/2017.
 */

public class GetAuditItemsPerChecklistAndVehicle extends AsyncTask<ChecklistVehicleIds, String, List<ItemAudit>> {
    protected IEquipmentPreferences mPreferences;
    String TAG = Login.class.getSimpleName();
    ProgressDialog progress;
    Context context;
    boolean Redirect = false;


    public GetAuditItemsPerChecklistAndVehicle(Context context, boolean redirect) {
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
        String conditionValue = "";
        int foundOnVehicle = 0;

        try {
            if (progress.isShowing()) {
                progress.dismiss();
            }

            if (lstItemAudit != null) {

                db = new DbHelper(context);
                mPreferences.setHadAuditRecords(1);

                for (int i = 0; i < lstItemAudit.size(); i++) {

                    ItemAudit exist = db.getItemAuditPerBarcode(context, lstItemAudit.get(i).getBarcode());

                    //Add Only once that does not exist already.
                    if (exist == null) {

                        if (lstItemAudit.get(i).getVehicleId() == mPreferences.getVehicleId()) {
                            conditionValue = lstItemAudit.get(i).getCondition();
                        } else {
                            conditionValue = "Invalid VehicleId:" + lstItemAudit.get(i).getVehicleId() + " - Found on : " + mPreferences.getVehicleRego();
                            foundOnVehicle = mPreferences.getVehicleId();
                        }

                        db.addItemAudit(context, new ItemAudit(
                                lstItemAudit.get(i).getItemId(),        //ItemId
                                lstItemAudit.get(i).getBarcode(),       //BarCode
                                lstItemAudit.get(i).getNote(),          //Note
                                conditionValue,                         //Condition
                                Core.get().getDateNowString(),          //Date
                                mPreferences.getChecklistId(),          //ChecklistId
                                lstItemAudit.get(i).getVehicleId(),     //Vehicle
                                "new",                                  //Status
                                0,                                      //Id
                                lstItemAudit.get(i).getCondition2(),    //Condition2
                                foundOnVehicle));                       //FoundOnVehicle

                        foundOnVehicle = 0;

                    }

                }

                if (Redirect) {

                    context.startActivity(new Intent(context, AuditItems.class));

                }
            } else {

                mPreferences.setHadAuditRecords(0);
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
