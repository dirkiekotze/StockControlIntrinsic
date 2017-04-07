package intrinsic_plant_equipment.plantequipment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import intrinsic_plant_equipment.plantequipment.async.GetAuditItemsPerChecklistAndVehicle;
import intrinsic_plant_equipment.plantequipment.async.GetItems;
import intrinsic_plant_equipment.plantequipment.helper.Core;
import intrinsic_plant_equipment.plantequipment.helper.IEquipmentPreferences;
import intrinsic_plant_equipment.plantequipment.model.Checklist;
import intrinsic_plant_equipment.plantequipment.model.ChecklistVehicleIds;
import intrinsic_plant_equipment.plantequipment.model.Vehicle;
import intrinsic_plant_equipment.plantequipment.model.VehicleAudit;
import intrinsic_plant_equipment.plantequipment.sqlLite.DbHelper;

public class VehicleAndChecklistSelect extends BaseClass {

    IEquipmentPreferences mPreferences;
    String TAG = VehicleAndChecklistSelect.class.getSimpleName();
    Spinner checklistDropdown;
    Spinner vehicleDropdown;
    Button startEqupmentChecklist;
    Button dailyChecklist;
    TextView rego;
    TextView type;
    TextView kms;
    TextView serviceDue;
    TextView servicePlantLabel;
    TextView servicePlant;
    TextView plantElectTestDateLabel;
    TextView plantElectTestDate;
    EditText currentKms;
    int checklistId = 0;
    int vehicleId = 0;
    Context context;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_vehicle_and_checklist_select);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mPreferences = Core.get().getPreferences();
        context = this;
        populateControls();
        checklistSpinnerLogic();
        vehicleSpinnerLogic();

    }

    public void checklistSpinnerLogic() {

        DbHelper db = null;

        try {
            db = DbHelper.getInstance(this);
            List<Checklist> checklistItems = db.getAllChecklistItems(this);

            final ArrayAdapter<Checklist> adapter = new ArrayAdapter<>(this, R.layout.my_spinner, checklistItems);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            checklistDropdown.setAdapter(adapter);

            checklistDropdown.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Checklist item = adapter.getItem(position);
                            if (item != null) {

                                checklistId = item.getCloudId();
                                mPreferences.setChecklistName(item.getName());
                                mPreferences.setChecklistDate(item.getListDate().split("T")[0]);
                                mPreferences.setChecklistId(item.getCloudId());
                                populateAuditControls();

                            }
                        }

                        public void onNothingSelected(AdapterView<?> parent) {
                            Core.get().showMessage("Nothing selected", context, TAG);
                        }
                    }
            );

        } catch (Exception err) {

            Core.get().showMessage("Error occurred in Checklist Spinner : Message " + err.getMessage(), this, TAG);
        } finally {

            if (db != null) {
                db.close();
            }

        }

    }

    public void doEquipmentChecklist(View view) {

        if (!doValidate()) return;

        //Delete and Add Items
        DbHelper db = null;

        //Sync Logic
        try {

            //Delete Items
            db = DbHelper.getInstance(this);

            //Where Status <> new
            db.deleteItemNotNew(this);

            //Add Items
            GetItems getItems = new GetItems(this, false, false);
            getItems.execute("start");


        } catch (Exception err) {

            Core.get().showMessage("Error occurred in Checklist Sinner : Message " + err.getMessage(), this, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

        }

        //Rest
        try {
            db = DbHelper.getInstance(this);

            //Populate VehicleAuditRecord
            doVehicleAuditLogic();

            //Delete all status <> 'new'
            db.deleteItemAuditStusNotNew(this);

            //Add Audit Items Per Vehicle and Checklist
            GetAuditItemsPerChecklistAndVehicle audit = new GetAuditItemsPerChecklistAndVehicle(this, true);
            audit.execute(new ChecklistVehicleIds(checklistId, vehicleId));

        } catch (Exception err) {

            Core.get().showMessage("Error occurred in doStartContinue : Message " + err.getMessage(), this, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

        }

    }

    private boolean doValidate() {
        if (currentKms.getText().toString().equals("")) {

            Core.get().showMessage("Enter Odometer Reading please", this, TAG);
            return false;
        }

        if (invalidKmsEntered()) {

            Core.get().showMessage("Current Kms less than vehicle kms", this, TAG);
            return false;

        }
        return true;
    }

    private boolean invalidKmsEntered() {

        DbHelper db = null;

        try {
            db = DbHelper.getInstance(this);
            Vehicle vehicle = db.getVehiclePerId(this, vehicleId);

            if (vehicle != null) {

                if (vehicle.getCurrentKms() > Integer.valueOf(currentKms.getText().toString())) {

                    return true;
                } else {
                    return false;
                }
            } else {

                Core.get().showMessage("Unable to extract vehicle details", this, TAG);
                return false;
            }


        } catch (Exception err) {

            Core.get().showMessage("Error occurred in invalidKmsEntered : Message " + err.getMessage(), this, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

        }

        return false;

    }

    private void doVehicleAuditLogic() {

        DbHelper db = null;

        try {
            db = DbHelper.getInstance(this);

            //Only one Audit per ChecklistId allowed.
            db.deleteVehicleAuditPerVehicleId(this, mPreferences.getVehicleId());

            //int vehicleAuditId,int vehicleId,int userId,int checklistId,int kilometers,  String rego, String dateStamp
            VehicleAudit audit = new VehicleAudit(
                    0,                                   //VehicleAuditId
                    mPreferences.getVehicleId(),         //VehicleId
                    mPreferences.getUserId(),            //UserID
                    mPreferences.getChecklistId(),       //ChecklistId
                    Integer.valueOf(currentKms.getText().toString()), //CurrentKms
                    rego.getText().toString(),           //Rego
                    Core.get().getDateNowString());      //CurrentDate

            db.addVehicleAudit(this, audit);

        } catch (Exception err) {

            Core.get().showMessage("Error occurred in doVehicleAuditLogic : Message " + err.getMessage(), this, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

        }
    }

    private String getDateAsString(Date date) {

        DateFormat formatDate = new SimpleDateFormat("EEEE, d MMM yyyy");
        return formatDate.format(date);

    }

    private String getDaysDue(String dateIn, String type) {

        StringBuilder sb = new StringBuilder();
        long daysDiff = 0;
        String dateAsString = "";

        if ((dateIn == null) || (dateIn.equals(""))) {
            return "No data exist";
        } else {
            String serviceDate = dateIn.split(" ")[0];
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date = format.parse(serviceDate);
                daysDiff = Core.get().getDaysFromNowToDate(date);
                dateAsString = getDateAsString(date);
            } catch (ParseException err) {

                Core.get().showMessage("Unable to getDaysDue Message :" + err.getMessage(), this, TAG);
                return "Unable to Calculate";
            }

            sb.append(dateAsString);
            sb.append(" (");
            sb.append(daysDiff);
            if (daysDiff <= 5) {
                if (type.equals("servicePlant")) {
                    servicePlant.setTextColor(ContextCompat.getColor(context, R.color.red));
                } else if (type.equals("plantElecTestDate")) {
                    plantElectTestDate.setTextColor(ContextCompat.getColor(context, R.color.red));
                }

            }
            if (daysDiff == 1) {
                sb.append(" day remain)");
            } else {
                sb.append(" days remain)");
            }

            return sb.toString();
        }

    }

    private String getServiceDueText(int serviceDueKlms, int currentKms) {

        try {
            int kmsRemaining = serviceDueKlms - currentKms;
            StringBuilder str = new StringBuilder();

            str.append(serviceDueKlms);
            str.append(" klms (");
            str.append(kmsRemaining);

            if (kmsRemaining <= 1000) {

                serviceDue.setTextColor(ContextCompat.getColor(context, R.color.red));
            }
            str.append(" klms remain)");
            return str.toString();
        } catch (Exception err) {

            return "Unable to calculate";
        }

    }

    private void populateControls() {

        checklistDropdown = (Spinner) findViewById(R.id.checklistDropdown);
        vehicleDropdown = (Spinner) findViewById(R.id.vehicleDropdown);
        startEqupmentChecklist = (Button) findViewById(R.id.startEqupmentChecklist);
        dailyChecklist = (Button) findViewById(R.id.dailyChecklist);

        Drawable drawCross = ResourcesCompat.getDrawable(getResources(), R.drawable.crossincircle, null);
        Drawable drawCheck = ResourcesCompat.getDrawable(getResources(), R.drawable.checkincircle, null);

        //Check or Cross:
        if (mPreferences.getHadAuditRecords() == 0) {
            startEqupmentChecklist.setCompoundDrawablesWithIntrinsicBounds(null, null, drawCross, null);
            startEqupmentChecklist.setText("Equipment Checklist (" + mPreferences.getChecklistDate() + "), Not Started");
        } else {
            startEqupmentChecklist.setCompoundDrawablesWithIntrinsicBounds(null, null, drawCheck, null);
            startEqupmentChecklist.setText("Equipment Checklist (" + mPreferences.getChecklistDate() + "), Started");
        }

        dailyChecklist.setCompoundDrawablesWithIntrinsicBounds(null, null, drawCross, null);

        rego = (TextView) findViewById(R.id.rego);
        type = (TextView) findViewById(R.id.type);
        kms = (TextView) findViewById(R.id.kms);
        serviceDue = (TextView) findViewById(R.id.serviceDue);
        servicePlant = (TextView) findViewById(R.id.servicePlant);
        servicePlantLabel = (TextView) findViewById(R.id.servicePlantLabel);
        plantElectTestDate = (TextView) findViewById(R.id.plantElectTestDate);
        plantElectTestDateLabel = (TextView) findViewById(R.id.plantElectTestDateLabel);
        currentKms = (EditText) findViewById(R.id.currentKms);
    }

    private void populateAuditControls() {

        if ((checklistId != 0) && (vehicleId != 0)) {

            dailyChecklist.setText("Daily Checklist(" + Core.get().getDateNowString() + "), Not Complete");

            DbHelper db = null;

            try {
                db = DbHelper.getInstance(this);
                Vehicle vehicle = db.getVehiclePerId(this, vehicleId);

                if (vehicle != null) {

                    if (vehicle.getType().toLowerCase().equals("ewp")) {
                        plantElectTestDate.setVisibility(View.VISIBLE);
                        servicePlant.setVisibility(View.VISIBLE);
                        plantElectTestDateLabel.setVisibility(View.VISIBLE);
                        servicePlantLabel.setVisibility(View.VISIBLE);

                    } else {
                        plantElectTestDate.setVisibility(View.GONE);
                        servicePlant.setVisibility(View.GONE);
                        plantElectTestDateLabel.setVisibility(View.GONE);
                        servicePlantLabel.setVisibility(View.GONE);
                    }
                    rego.setText(vehicle.getRego());
                    type.setText(vehicle.getType());
                    kms.setText(String.valueOf(vehicle.getCurrentKms()));
                    serviceDue.setText(getServiceDueText(vehicle.getServiceDueKlms(), vehicle.getCurrentKms()));
                    servicePlant.setText(getDaysDue(vehicle.getServicePlantDate(), "servicePlant"));
                    plantElectTestDate.setText(getDaysDue(vehicle.getPlantElecTestDate(), "plantElecTestDate"));
                }
            } catch (Exception err) {

                Core.get().showMessage("Unable to populate Vehicle Detail Message: " + err.getMessage(), this, TAG);
            } finally {

                if (db != null) {

                    db.close();

                }
            }
        }
    }

    public void vehicleSpinnerLogic() {

        DbHelper db = null;

        try {
            db = DbHelper.getInstance(this);
            List<Vehicle> vehicleItems = db.getAllVehicles(this);

            final ArrayAdapter<Vehicle> adapter = new ArrayAdapter<>(this, R.layout.my_spinner, vehicleItems);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            vehicleDropdown.setAdapter(adapter);

            if(mPreferences.getVehicleId() != 0){
                vehicleDropdown.setSelection(getIndex(vehicleDropdown,mPreferences.getVehicleId()));
            }


            vehicleDropdown.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Vehicle item = adapter.getItem(position);
                            if (item != null) {
                                vehicleId = item.getVehicleId();
                                mPreferences.setVehicleRego(item.getRego());
                                mPreferences.setVehicleId(item.getVehicleId());
                                populateAuditControls();
                            }
                        }

                        public void onNothingSelected(AdapterView<?> parent) {
                            Core.get().showMessage("Nothing selected", context, TAG);
                        }
                    }
            );

        } catch (Exception err) {

            Core.get().showMessage("Error occurred in Vehicle Spinner : Message " + err.getMessage(), this, TAG);
        } finally {

            if (db != null) {
                db.close();
            }

        }


    }

    private int getIndex(Spinner vehicleDropdown, int vehicleId) {

        for (int i = 0; i < vehicleDropdown.getCount() ; i++) {

            Vehicle vehicle = (Vehicle)vehicleDropdown.getItemAtPosition(i);
            if(vehicle != null){
                if(vehicle.getVehicleId() == vehicleId){
                    return i;
                }
            }

        }

        return 0;
    }

    public void doDailyChecklist(View view) {

        Core.get().showMessage("Under Construction", this, TAG);
    }
}
