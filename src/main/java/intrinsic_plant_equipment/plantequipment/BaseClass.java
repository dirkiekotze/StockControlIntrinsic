package intrinsic_plant_equipment.plantequipment;

import android.support.v7.app.AppCompatActivity;

import intrinsic_plant_equipment.plantequipment.helper.Core;

/**
 * Created by Dirk on 7/04/2017.
 */

public abstract class BaseClass extends AppCompatActivity {

    String TAG = BaseClass.class.getSimpleName();

    @Override
    public void onBackPressed() {

        Core.get().showMessage("Back not allowed.",this,TAG);
    }
}





