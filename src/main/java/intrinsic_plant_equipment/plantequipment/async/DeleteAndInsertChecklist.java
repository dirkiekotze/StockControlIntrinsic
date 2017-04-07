package intrinsic_plant_equipment.plantequipment.async;

import android.app.ProgressDialog;
import android.content.Context;
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

import intrinsic_plant_equipment.plantequipment.helper.Core;
import intrinsic_plant_equipment.plantequipment.helper.Endpoints;
import intrinsic_plant_equipment.plantequipment.helper.IEquipmentPreferences;
import intrinsic_plant_equipment.plantequipment.model.Checklist;
import intrinsic_plant_equipment.plantequipment.model.Vehicle;
import intrinsic_plant_equipment.plantequipment.sqlLite.DbHelper;

/**
 * Created by Dirk on 8/03/2017.
 */

public class DeleteAndInsertChecklist extends AsyncTask<String, String, List<Checklist>> {
    protected IEquipmentPreferences mPreferences;
    String TAG = Login.class.getSimpleName();
    ProgressDialog progress;
    Context context;
    boolean Redirect = false;


    public DeleteAndInsertChecklist(Context context, boolean redirect) {
        progress = Core.get().initiateProgressDialog(context);
        this.context = context;
        mPreferences = Core.get().getPreferences();
        Redirect = redirect;
    }

    @Override
    protected void onPreExecute() {
        try {
            progress.setMessage("Trying to download checklist detail, please be patient....");
            progress.show();
        } catch (Exception err) {

            Core.get().showMessageShort("Unable to show DeleteAndInsertChecklist progress", context, TAG);

        }
    }

    @Override
    protected void onPostExecute(List<Checklist> lstChecklist) {

        DbHelper db = null;

        try{
            if (progress.isShowing()) {
                progress.dismiss();
            }

            if (lstChecklist != null) {

                db = new DbHelper(context);
                db.deleteChecklist(context);

                for (int i = 0; i < lstChecklist.size(); i++) {
                    db.addChecklist(context, lstChecklist.get(i));
                }

                if (Redirect) {

                    DeleteAndInsertItemType itemType = new DeleteAndInsertItemType(context,true);
                    itemType.execute("start");

                }
            } else {

                Core.get().showMessage("Unable to DeleteAndInsertChecklist", context, TAG);
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
    protected List<Checklist> doInBackground(String... start) {

        try {

            mPreferences = Core.get().getPreferences();


            HttpURLConnection myURLConnection = Core.get().setupHttpConnectionGetWithToken(new URL(Endpoints.CHECKLIST_URL), mPreferences);

            int responseCode = myURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Reader reader = new InputStreamReader(myURLConnection.getInputStream(), "UTF-8");
                Gson gson = new GsonBuilder().create();
                Type listType = new TypeToken<List<Checklist>>() {
                }.getType();
                List<Checklist> checklistList = (List<Checklist>) gson.fromJson(reader, listType);
                publishProgress("Success.");
                return checklistList;

            } else {
                publishProgress("Unable to Download DeleteAndInsertChecklist.");
                return null;
            }


        } catch (Exception err) {

            publishProgress("Unable to Download DeleteAndInsertChecklist: " + err.getMessage());
        }

        return null;


    }
}
