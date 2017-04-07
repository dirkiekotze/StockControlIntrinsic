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
import intrinsic_plant_equipment.plantequipment.model.ItemAudit;
import intrinsic_plant_equipment.plantequipment.sqlLite.DbHelper;

/**
 * Created by Dirk on 8/03/2017.
 */

public class InsertNewAuditsToCloud extends AsyncTask<String, String, String> {
    protected IEquipmentPreferences mPreferences;
    String TAG = InsertNewAuditsToCloud.class.getSimpleName();
    ProgressDialog progress;
    Context context;
    boolean Redirect = false;


    public InsertNewAuditsToCloud(Context context, boolean redirect) {
        progress = Core.get().initiateProgressDialog(context);
        this.context = context;
        Redirect = redirect;
    }

    @Override
    protected void onPreExecute() {
        try {
            progress.setMessage("Uploading ItemAudits to Cloud, please be patient....");
            progress.show();
        } catch (Exception err) {

            Core.get().showMessageShort("Unable to show InserAudits progress", context, TAG);

        }
    }

    @Override
    protected void onPostExecute(String result) {

        DbHelper db = null;

        try {

            db = DbHelper.getInstance(context);

            if (result.equals("success")) {

                Core.get().showMessage("Successfully uploaded ItemAudits to Cloud", context, TAG);

                //Delete Item
                db.deleteItem(context);

                //Delete ItemAudit Records.
                db.deleteItemAudit(context);

                if (progress.isShowing()) {
                    progress.dismiss();
                }

                //Get New Items: Redirect to Audit per Vehicle and Checklist as well
                GetItems addItems = new GetItems(context, false, true);
                addItems.execute("start");


            } else if (result.equals("nothing")) {

                Core.get().showMessage("No ItemAudits to upload to Cloud", context, TAG);
            } else {
                Core.get().showMessage("Unable to upload ItemAudits to Cloud", context, TAG);
            }

            if (progress.isShowing()) {
                progress.dismiss();
            }
        } catch (Exception err) {

            Core.get().showMessage("Unable to upload ItemAudits to Cloud Message : " + err.getMessage(), context, TAG);

            if (progress.isShowing()) {
                progress.dismiss();
            }

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
            List<ItemAudit> itemsAudits = db.getAllItemAuditRecords(context);

            if (itemsAudits != null) {

                Gson gson = new Gson();
                String json = gson.toJson(itemsAudits);
                HttpURLConnection myURLConnection = Core.get().setupHttpConnectionForPostWithoutToken(new URL(Endpoints.ITEM_AUDIT_URL));

                OutputStreamWriter writer = new OutputStreamWriter(myURLConnection.getOutputStream(), "UTF-8");
                writer.write("{\"itemAudit\":" + json + "}");
                writer.close();

                String line;
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        jsonString.append(line);
                    }
                    br.close();
                } catch (Exception err) {

                    publishProgress("Unable to insert new ItemAudits to cloud. Message " + err.getMessage());
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

            publishProgress("Unable to insert new ItemAudits to cloud. Message " + err.getMessage());
            return "error";
        }

    }
}
