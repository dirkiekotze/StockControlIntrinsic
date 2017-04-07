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
import intrinsic_plant_equipment.plantequipment.model.Item;
import intrinsic_plant_equipment.plantequipment.model.ItemType;
import intrinsic_plant_equipment.plantequipment.sqlLite.DbHelper;

/**
 * Created by Dirk on 8/03/2017.
 */

public class DeleteAndInsertItemType extends AsyncTask<String, String, List<ItemType>> {
    protected IEquipmentPreferences mPreferences;
    String TAG = DeleteAndInsertItemType.class.getSimpleName();
    ProgressDialog progress;
    Context context;
    boolean Redirect = false;


    public DeleteAndInsertItemType(Context context, boolean redirect) {
        progress = Core.get().initiateProgressDialog(context);
        this.context = context;
        mPreferences = Core.get().getPreferences();
        Redirect = redirect;
    }

    @Override
    protected void onPreExecute() {
        try {
            progress.setMessage("Trying to download itemType detail, please be patient....");
            progress.show();
        } catch (Exception err) {

            Core.get().showMessageShort("Unable to show DeleteAndInsertItemType progress", context, TAG);

        }
    }

    @Override
    protected void onPostExecute(List<ItemType> lstItemType) {

        DbHelper db = null;

        try{
            if (progress.isShowing()) {
                progress.dismiss();
            }

            if (lstItemType != null) {

                db = new DbHelper(context);
                db.deleteItemType(context);

                for (int i = 0; i < lstItemType.size(); i++) {
                    db.addItemType(context, lstItemType.get(i));
                }

                if (Redirect) {

                    DeleteAndInsertCategories c = new DeleteAndInsertCategories(context,true);
                    c.execute("start");

                }
            } else {

                Core.get().showMessage("Unable to DeleteAndInsertItemTypet", context, TAG);
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
    protected List<ItemType> doInBackground(String... start) {

        try {

            mPreferences = Core.get().getPreferences();


            HttpURLConnection myURLConnection = Core.get().setupHttpConnectionGetWithToken(new URL(Endpoints.ITEM_TYPE_URL), mPreferences);

            int responseCode = myURLConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Reader reader = new InputStreamReader(myURLConnection.getInputStream(), "UTF-8");
                Gson gson = new GsonBuilder().create();
                Type listItemType = new TypeToken<List<ItemType>>() {
                }.getType();
                List<ItemType> itemTypeList = (List<ItemType>) gson.fromJson(reader, listItemType);
                publishProgress("Success.");
                return itemTypeList;

            } else {
                publishProgress("Unable to Download DeleteAndInsertItemTypet.");
                return null;
            }


        } catch (Exception err) {

            publishProgress("Unable to Download DeleteAndInsertItemType: " + err.getMessage());
        }

        return null;


    }
}
