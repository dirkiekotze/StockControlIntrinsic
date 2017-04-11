package intrinsic_plant_equipment.plantequipment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import intrinsic_plant_equipment.plantequipment.helper.Core;
import intrinsic_plant_equipment.plantequipment.helper.IEquipmentPreferences;
import intrinsic_plant_equipment.plantequipment.model.Category;
import intrinsic_plant_equipment.plantequipment.model.Item;
import intrinsic_plant_equipment.plantequipment.model.ItemAndItemAudit;
import intrinsic_plant_equipment.plantequipment.model.ItemAudit;
import intrinsic_plant_equipment.plantequipment.model.ItemType;
import intrinsic_plant_equipment.plantequipment.model.Vehicle;
import intrinsic_plant_equipment.plantequipment.model.YesNo;
import intrinsic_plant_equipment.plantequipment.sqlLite.DbHelper;

public class EditItem extends BaseClass implements View.OnClickListener {

    String TAG = EditItem.class.getSimpleName();
    IEquipmentPreferences mPreferences;
    String barcodeValue = "";
    TextView itemName;
    TextView vehicle;
    EditText endOfLiveDate;
    EditText testDueDate;
    EditText lastTestBy;
    EditText testReportNumber;
    EditText note;
    TextView barcode;
    Spinner condition2;
    Context context;
    int vehicleId;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    String selectedDate = "";
    int itemId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        mPreferences = Core.get().getPreferences();

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        //From Scanner Page do Update
        Intent i = getIntent();
        barcodeValue = i.getStringExtra("barcode");

        populateControls();
        populateEditValues();

        setDate();

    }

    private void setDate() {

        testDueDate.setOnClickListener(this);
        endOfLiveDate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                if(selectedDate.equals("endOfLiveDate")){
                    endOfLiveDate.setText(dateFormatter.format(newDate.getTime()));
                }
                else if (selectedDate.equals("testDueDate")){
                    testDueDate.setText(dateFormatter.format(newDate.getTime()));
                }

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    private void populateControls() {

        itemName = (TextView) findViewById(R.id.itemName);
        vehicle = (TextView) findViewById(R.id.vehicle);
        barcode = (TextView) findViewById(R.id.barcode);
        testDueDate = (EditText) findViewById(R.id.testDueDate);
        endOfLiveDate = (EditText) findViewById(R.id.endOfLiveDate);
        lastTestBy = (EditText) findViewById(R.id.lastTestBy);
        testReportNumber = (EditText) findViewById(R.id.testReportNumber);
        note = (EditText) findViewById(R.id.note);
        condition2 = (Spinner)findViewById(R.id.condition2);
        context = this;

        barcode.setText(barcodeValue);


    }

    private void populateEditValues() {

        DbHelper db = null;

        try {

            db = DbHelper.getInstance(this);
            ItemAndItemAudit item = db.getItemAndAuditItem(this, barcodeValue, mPreferences.getChecklistId());

            if (item != null) {

                //Set ItemId
                itemId = item.getItemId();
                vehicleId = item.getVehicleId();
                itemName.setText(item.getName());
                vehicle.setText(item.getRego());
                barcode.setText(barcodeValue);
                testDueDate.setText(scrubDate(item.getTestDueDate()));
                endOfLiveDate.setText(scrubDate(item.getEndOfLifeDate()));
                lastTestBy.setText(item.getTestBy());
                testReportNumber.setText(item.getReportNumber());
                note.setText(item.getNote());
                if(item.getCondition2().toLowerCase().equals("pass")){
                    condition2.setSelection(0);
                }
                else{
                    condition2.setSelection(1);
                }


            }




        } catch (Exception err) {

            Core.get().showMessage("Error occurred in doPass : Message " + err.getMessage(), this, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

        }


    }

    private String scrubDate(String date) {

        if(date.equals("")){
            return "";
        }
        else{
            return date.split("T")[0];
        }
    }

    public void doCancel(View view) {

        startActivity(new Intent(this,AuditItems.class));

    }

    @Override
    public void onClick(View v) {

        if (v == testDueDate){
            selectedDate = "testDueDate";
        }
        else if(v == endOfLiveDate){
            selectedDate = "endOfLiveDate";
        }

        fromDatePickerDialog.show();

    }

    public void doEdit(View view) {

        DbHelper db = null;
        String condition = "Scanned";
        int foundOnVehicle = 0;

        try {

            db = DbHelper.getInstance(this);

            //Update AuditItem

            ItemAudit storeAuditRecord = db.getItemAuditPerBarcode(this,barcodeValue);

            if(storeAuditRecord != null){

                if(storeAuditRecord.getCondition().toLowerCase().contains("invalid")){
                    condition = storeAuditRecord.getCondition();
                    foundOnVehicle = storeAuditRecord.getFoundOnVehicleId();
                }
            }



            //Delete Audit Item
            db.deleteItemAuditPerBarcode(this,barcodeValue);

            ItemAudit item = new ItemAudit(
                    itemId,                                                            //CloudId
                    barcodeValue,                                                      //Barcode
                    note.getText().toString(),                                         //Note
                    "Scanned",                                                         //Condition
                    Core.get().getDate(new Date().getTime(), "dd/MM/yyyy"),            //Date
                    mPreferences.getChecklistId(),                                     //ChecklistId
                    vehicleId,                                                         //VehicleId
                    "new",                                                             //Status
                    0,                                                                 //Id
                    condition2.getSelectedItem().toString(),                           //Condition2
                    foundOnVehicle);                                                   //FoundOnVehicle

            db.addItemAudit(this, item);

            //Update Item
            db.updateItemScanned(this,barcodeValue,itemId,endOfLiveDate.getText().toString(),vehicle.getText().toString(), lastTestBy.getText().toString(),testReportNumber.getText().toString(),testDueDate.getText().toString(),Core.get().getDateNowString());

            startActivity(new Intent(this,AuditItems.class));



        } catch (Exception err) {

            Core.get().showMessage("Error occurred in doPass : Message " + err.getMessage(), this, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

        }

    }
}
