package intrinsic_plant_equipment.plantequipment;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import intrinsic_plant_equipment.plantequipment.helper.Core;
import intrinsic_plant_equipment.plantequipment.helper.EquipmentPreferences;
import intrinsic_plant_equipment.plantequipment.helper.IEquipmentPreferences;
import intrinsic_plant_equipment.plantequipment.model.LoginClass;

public class Login extends BaseClass {
    String TAG = Login.class.getSimpleName();
    IEquipmentPreferences mPreferences;
    ProgressDialog progress;
    TextView passwordText;
    TextView userNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_login);

        progress = Core.get().initiateProgressDialog(this);
        assignControlls();

        Core.get().setContext(getApplicationContext());
        Core.get().setPrefs(new EquipmentPreferences(getApplicationContext()));

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mPreferences = Core.get().getPreferences();
        resetPreferences();

    }

    private void resetPreferences() {
        mPreferences.setChecklistName("");
        mPreferences.setAccessLevel("");
        mPreferences.setVehicleId(0);
        mPreferences.setHadAuditRecords(0);
        mPreferences.setVehicleRego("");
        mPreferences.setUserId(0);
    }

    private boolean validateInput() {

        boolean retValue = true;

        if(passwordText.getText().toString().equals("")){
            retValue =false;
            Core.get().showMessage("Password Empty",this,TAG);
        }

        if(userNameText.getText().toString().equals("")){
            retValue = false;
            Core.get().showMessage("Username Empty",this,TAG);
        }

        return retValue;
    }

    private void assignControlls() {

        passwordText = (TextView) findViewById(R.id.passwordText);
        userNameText = (TextView) findViewById(R.id.userNameText);
    }

    public void doLogin(View view) {

        Core.get().hideKeyboard(this,this);

        if(Core.get().isConnectedToInternet(this)){
            //Valid Login Values
            if(validateInput()){

                intrinsic_plant_equipment.plantequipment.async.Login login = new intrinsic_plant_equipment.plantequipment.async.Login(this,true);
                login.execute(new LoginClass(userNameText.getText().toString(),passwordText.getText().toString()));

            }
        }
        else{

            Core.get().showMessage("You are not Connected to Internet. Unable to Login",this,TAG);

        }




    }
}
