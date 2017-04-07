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

import intrinsic_plant_equipment.plantequipment.VehicleAndChecklistSelect;
import intrinsic_plant_equipment.plantequipment.helper.Core;
import intrinsic_plant_equipment.plantequipment.helper.Endpoints;
import intrinsic_plant_equipment.plantequipment.helper.IEquipmentPreferences;
import intrinsic_plant_equipment.plantequipment.model.ChecklistVehicleIds;
import intrinsic_plant_equipment.plantequipment.model.Item;
import intrinsic_plant_equipment.plantequipment.model.ItemAudit;
import intrinsic_plant_equipment.plantequipment.sqlLite.DbHelper;

/**
 * Created by Dirk on 8/03/2017.
 */

public class GetItems extends AsyncTask<String, String, List<Item>> {
    protected IEquipmentPreferences mPreferences;
    String TAG = Login.class.getSimpleName();
    ProgressDialog progress;
    Context context;
    boolean Redirect = false;
    boolean DoAuditImport = false;


    public GetItems(Context context, boolean redirect,boolean doAuditImport) {
        progress = Core.get().initiateProgressDialog(context);
        this.context = context;
        mPreferences = Core.get().getPreferences();
        Redirect = redirect;
        DoAuditImport = doAuditImport;
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
    protected void onPostExecute(List<Item> lstItem) {

        DbHelper db = null;

        try {

            if (lstItem != null) {

                db = new DbHelper(context);

                //Insert them all
                for (int i = 0; i < lstItem.size(); i++) {

                    db.addItem(context, lstItem.get(i), "existing");

                }

                if (progress.isShowing()) {
                    progress.dismiss();
                }

                if (Redirect) {

                    context.startActivity(new Intent(context, VehicleAndChecklistSelect.class));

                }

                //For Sync:
                if(DoAuditImport){
                    GetAuditItemsPerChecklistAndVehicle audit = new GetAuditItemsPerChecklistAndVehicle(context,true);
                    audit.execute(new ChecklistVehicleIds(mPreferences.getChecklistId(),mPreferences.getVehicleId()));

                }
            } else {

                if (progress.isShowing()) {
                    progress.dismiss();
                }

                Core.get().showMessage("Unable to import Items", context, TAG);
            }
        } catch (Exception err) {

            if (progress.isShowing()) {
                progress.dismiss();
            }

            Core.get().showMessage("Unable to import Items Message : " + err.getMessage(), context, TAG);

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
    protected List<Item> doInBackground(String... start) {

        try {

            mPreferences = Core.get().getPreferences();

            HttpURLConnection myURLConnection = Core.get().setupHttpConnectionGetWithToken(new URL(Endpoints.ITEM_URL), mPreferences);

            int responseCode = myURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Reader reader = new InputStreamReader(myURLConnection.getInputStream(), "UTF-8");
                Gson gson = new GsonBuilder().create();
                Type listType = new TypeToken<List<Item>>() {
                }.getType();
                List<Item> itemList = gson.fromJson(reader, listType);
                publishProgress("Success.");
                return itemList;

            } else {
                publishProgress("Unable to Download Items.");
                return null;
            }


        } catch (Exception err) {

            publishProgress("Unable to Download Items Message: " + err.getMessage());
        }

        return null;


    }
}
