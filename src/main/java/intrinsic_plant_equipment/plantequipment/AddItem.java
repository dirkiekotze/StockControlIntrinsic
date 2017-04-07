package intrinsic_plant_equipment.plantequipment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
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
import intrinsic_plant_equipment.plantequipment.model.AuditCombinedData;
import intrinsic_plant_equipment.plantequipment.model.Category;
import intrinsic_plant_equipment.plantequipment.model.Item;
import intrinsic_plant_equipment.plantequipment.model.ItemAudit;
import intrinsic_plant_equipment.plantequipment.model.ItemType;
import intrinsic_plant_equipment.plantequipment.model.Vehicle;
import intrinsic_plant_equipment.plantequipment.model.YesNo;
import intrinsic_plant_equipment.plantequipment.sqlLite.DbHelper;

public class AddItem extends BaseClass implements View.OnClickListener {

    IEquipmentPreferences mPreferences;
    String TAG = AddItem.class.getSimpleName();
    Spinner vehicleDropdown;
    Spinner categoryDropdown;
    Spinner itemTypeDropdown;
    Spinner requiresInspectionDropdown;
    Spinner condition2;
    EditText endOfLiveDate;
    EditText testDueDate;
    EditText lastScannedDate;
    EditText lastTestBy;
    EditText testReportNumber;
    EditText note;
    TextView itemTypeLabel;
    TextView barcode;
    int vehicleId = 0;
    int categoryId = 0;
    int itemTypeId = 0;
    String requiresInspection = "";
    Context context;
    String barcodeValue = "";
    String name;
    String rego;
    private DatePickerDialog fromDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    String selectedDate = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        mPreferences = Core.get().getPreferences();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        //Redirect From Scanner Page
        Intent i = getIntent();
        barcodeValue = i.getStringExtra("barcode");
        getValues(barcodeValue);

        populateControls();
        setDate();
        categorySpinnerLogic();
        vehicleSpinnerLogic();
        RequiresInspectionSpinnerLogic();
    }

    private void setDate() {

        testDueDate.setOnClickListener(this);
        endOfLiveDate.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                if (selectedDate.equals("endOfLiveDate")) {
                    endOfLiveDate.setText(dateFormatter.format(newDate.getTime()));
                } else if (selectedDate.equals("testDueDate")) {
                    testDueDate.setText(dateFormatter.format(newDate.getTime()));
                }

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

    }

    private void RequiresInspectionSpinnerLogic() {

        DbHelper db = null;

        try {
            db = DbHelper.getInstance(this);
            List<YesNo> yesNoLst = new ArrayList<>();

            YesNo yesNo = new YesNo("Yes");
            yesNoLst.add(yesNo);

            YesNo noYes = new YesNo("No");
            yesNoLst.add(noYes);

            final ArrayAdapter<YesNo> adapter = new ArrayAdapter<>(this, R.layout.my_spinner, yesNoLst);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            requiresInspectionDropdown.setAdapter(adapter);

            requiresInspectionDropdown.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            YesNo item = adapter.getItem(position);
                            if (item != null) {
                                requiresInspection = item.getType();

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

    private void categorySpinnerLogic() {

        DbHelper db = null;

        try {
            db = DbHelper.getInstance(this);
            List<Category> categoryItem = db.getAllCategories(this);

            final ArrayAdapter<Category> adapter = new ArrayAdapter<Category>(this, R.layout.my_spinner, categoryItem);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categoryDropdown.setAdapter(adapter);

            categoryDropdown.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Category item = adapter.getItem(position);
                            if (item != null) {
                                categoryId = item.getCloudCategoryId();
                                //name = item.getName();
                                itemTypeSpinnerLogic(categoryId);

                            }
                        }

                        public void onNothingSelected(AdapterView<?> parent) {
                            Core.get().showMessage("Nothing selected", context, TAG);
                        }
                    }
            );

        } catch (Exception err) {

            Core.get().showMessage("Error occurred in Category Spinner : Message " + err.getMessage(), this, TAG);
        } finally {

            if (db != null) {
                db.close();
            }

        }

    }

    private void itemTypeSpinnerLogic(int categoryId) {

        itemTypeLabel.setVisibility(View.VISIBLE);
        itemTypeDropdown.setVisibility(View.VISIBLE);

        DbHelper db = null;

        try {
            db = DbHelper.getInstance(this);
            List<ItemType> itemType = db.getItemTypesPerCategoryId(context, categoryId);

            final ArrayAdapter<ItemType> adapter = new ArrayAdapter<ItemType>(this, R.layout.my_spinner, itemType);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            itemTypeDropdown.setAdapter(null);
            itemTypeDropdown.setAdapter(adapter);

            itemTypeDropdown.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            ItemType item = adapter.getItem(position);
                            if (item != null) {
                                itemTypeId = item.getItemTypeId();
                                name = item.getName();

                            }
                        }

                        public void onNothingSelected(AdapterView<?> parent) {
                            Core.get().showMessage("Nothing selected", context, TAG);
                        }
                    }
            );

        } catch (Exception err) {

            Core.get().showMessage("Error occurred in ItemType Spinner : Message " + err.getMessage(), this, TAG);
        } finally {

            if (db != null) {
                db.close();
            }

        }

    }

    private void populateControls() {

        vehicleDropdown = (Spinner) findViewById(R.id.vehicleDropdown);
        categoryDropdown = (Spinner) findViewById(R.id.categoryDropdown);
        itemTypeDropdown = (Spinner) findViewById(R.id.itemTypeDropdown);
        condition2 = (Spinner) findViewById(R.id.condition2);
        requiresInspectionDropdown = (Spinner) findViewById(R.id.requiresInspectionDropdown);
        itemTypeLabel = (TextView) findViewById(R.id.itemTypeLabel);
        barcode = (TextView) findViewById(R.id.barcode);
        testDueDate = (EditText) findViewById(R.id.testDueDate);
        endOfLiveDate = (EditText) findViewById(R.id.endOfLiveDate);
        lastTestBy = (EditText) findViewById(R.id.lastTestBy);
        testReportNumber = (EditText) findViewById(R.id.testReportNumber);
        note = (EditText) findViewById(R.id.note);
        context = this;

        barcode.setText(barcodeValue);


    }

    public void vehicleSpinnerLogic() {

        DbHelper db = null;

        try {
            db = DbHelper.getInstance(this);
            List<Vehicle> vehicleItems = db.getAllVehicles(this);

            final ArrayAdapter<Vehicle> adapter = new ArrayAdapter<>(this, R.layout.my_spinner, vehicleItems);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            vehicleDropdown.setAdapter(adapter);

            vehicleDropdown.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Vehicle item = adapter.getItem(position);
                            if (item != null) {
                                vehicleId = item.getVehicleId();
                                rego = item.getRego();

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

    public void doStartContinue(View view) {

        insertItem();

        insertAudit();

        backToAuditPage();


    }

    private void insertAudit() {

        DbHelper db = null;

        try {
            db = DbHelper.getInstance(this);

            ItemAudit item = new ItemAudit(
                    0,                                                                 //CloudId
                    barcodeValue,                                                      //Barcode
                    note.getText().toString(),                                         //Note
                    "Scanned",                                                         //Condition
                    Core.get().getDateNowString(),                                     //Date
                    mPreferences.getChecklistId(),                                     //ChecklistId
                    vehicleId,                                                         //VehicleId
                    "new",                                                             //Status
                    0,                                                                 //Id
                    condition2.getSelectedItem().toString(),                           //Condition2
                    0);                                                                 //FoundOnVehicleId

            db.addItemAudit(this, item);


        } catch (Exception err) {

            Core.get().showMessage("Error occurred in insertAudit : Message " + err.getMessage(), this, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

            backToAuditPage();

        }
    }

    private void insertItem() {

        DbHelper db = null;

        try {
            db = DbHelper.getInstance(this);

            Item item = new Item(
                    0,                                                                 //ItemId
                    itemTypeId,                                                        //ItemTypeId
                    vehicleId,                                                         //VehicleId
                    "",                                                                //LastTestStatus
                    lastTestBy.getText().toString(),                                   //LastTestBy
                    testReportNumber.getText().toString(),                             //ReportNumber
                    vehicleId,                                                         //VehicleId
                    barcodeValue,                                                      //Barcode
                    name,                                                              //Name
                    rego,                                                              //Rego
                    "new",                                                             //Status
                    requiresInspectionDropdown.getSelectedItem().toString(),           //RequiresInspection
                    Core.get().getDateNowString(),                                     //LastScannedDate
                    testDueDate.getText().toString(),                                  //TestDueDate
                    endOfLiveDate.getText().toString(),                                //End Of Life Date
                    "",                                                                //Condition2
                    0);                                                                //LastVehicleLocation

            db.addItem(this, item,"new");


        } catch (Exception err) {

            Core.get().showMessage("Error occurred in nsertItem : Message " + err.getMessage(), this, TAG);

        } finally {

            if (db != null) {
                db.close();
            }


        }
    }

    private void backToAuditPage() {

        try {

            startActivity(new Intent(this, AuditItems.class));

        } catch (Exception err) {

            Core.get().showMessage("Error occurred in doStartContinue : Message " + err.getMessage(), this, TAG);

        } finally {

        }

    }

    public void doCancel(View view) {

        startActivity(new Intent(this,AuditItems.class));
    }

    private void getValues(String barcode) {

        //
        DbHelper db = null;

        try {
            db = DbHelper.getInstance(this);
            Item item = db.getItemPerBarcode(this, barcode);
            ItemAudit iAudit = db.getItemAuditPerBarcode(this, barcode);

        } catch (Exception err) {

            Core.get().showMessage("Error occurred in doBarcodeFind : Message " + err.getMessage(), this, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

        }
    }

    @Override
    public void onClick(View v) {
        if (v == testDueDate) {
            selectedDate = "testDueDate";
        } else if (v == endOfLiveDate) {
            selectedDate = "endOfLiveDate";
        } else if (v == lastScannedDate) {
            selectedDate = "lastScannedDate";
        }
        fromDatePickerDialog.show();
    }
}
