package intrinsic_plant_equipment.plantequipment;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import intrinsic_plant_equipment.plantequipment.helper.Core;
import intrinsic_plant_equipment.plantequipment.helper.IEquipmentPreferences;
import intrinsic_plant_equipment.plantequipment.model.Item;
import intrinsic_plant_equipment.plantequipment.model.ItemAndItemAudit;
import intrinsic_plant_equipment.plantequipment.model.ItemAudit;
import intrinsic_plant_equipment.plantequipment.sqlLite.DbHelper;

public class Scanner extends BaseClass {

    String TAG = Scanner.class.getSimpleName();
    IEquipmentPreferences mPreferences;
    String barcode = "";
    TextView itemDescription;
    TextView endOfLive;
    TextView testDue;
    TextView wrongVehicle;
    Item wrongVehicleItem = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        mPreferences = Core.get().getPreferences();

        Intent i = getIntent();
        String barcode = i.getStringExtra("barcode");

        if (barcode != null) {

            populateControls();
            setPageSize();
            doBarcodeFind(barcode);

            return;
        }


        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.setPrompt("Scan a barcode");
        scanIntegrator.setBeepEnabled(true);
        //scanIntegrator.setOrientationLocked(true);
        scanIntegrator.setBarcodeImageEnabled(true);
        scanIntegrator.initiateScan();

        populateControls();

        setPageSize();
    }

    private void populateControls() {
        itemDescription = (TextView) findViewById(R.id.itemDescription);
        endOfLive = (TextView) findViewById(R.id.endOfLive);
        testDue = (TextView) findViewById(R.id.testDue);
        wrongVehicle = (TextView) findViewById(R.id.wrongVehicle);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            //Back Button Pressed
            if (data == null && resultCode == 0) {

                startActivity(new Intent(this, AuditItems.class));
                return;
            }

            super.onActivityResult(requestCode, resultCode, data);
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            /*As an example in order to get the content of what is scanned you can do the following*/
            barcode = scanningResult.getContents().toString();

            if (!barcode.equals("")) {

                doBarcodeFind(barcode);

            }

        } catch (Exception err) {

            Core.get().showMessage("Unable to Scan Message : " + err.getMessage(), this, TAG);

            startActivity(new Intent(this, Scanner.class));

        }

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

    private void doBarcodeFind(String barcodeIn) {

        this.barcode = barcodeIn;
        DbHelper db = null;

        try {
            db = DbHelper.getInstance(this);

            //Get from Items Locally
            Item globalItem = db.getItemPerBarcode(this, barcodeIn);

            if (globalItem != null) {

                //Does it exist in ItemAudit Locally
                ItemAndItemAudit item = db.getItemAndAuditItem(this, barcode, mPreferences.getChecklistId());

                //Should never be null
                if (item == null) {

                    Core.get().showMessage("This item does not exist.",this,TAG);

                } else {

                    //Make Sure ItemAudit: Exist
                    ItemAudit itemAuditExist = db.getItemAuditPerBarcode(this,barcodeIn);

                    if(itemAuditExist == null){

                        //Create Item Audit if not Exist
                        //int itemId, String barcode, String note, String condition, String date, int checklistId, int vehicleId, String status,int id,String condition2,int foundOnVehicleId
                        db.addItemAudit(this, new ItemAudit(
                                globalItem.getItemId(),            //ItemId
                                barcodeIn,                         //Barcode
                                "",                                //Note
                                "Invalid Vehicle:" + mPreferences.getVehicleRego(),                   //Condition
                                Core.get().getDateNowString(),     //Date
                                mPreferences.getChecklistId(),    //ChecklistId
                                globalItem.getVehicleId(),        //VehicleId
                                "",                        //Status
                                0,                                //Id
                                "",                               //Condition2
                                mPreferences.getVehicleId()));    //Found On Vehicle
                    }

                    //set wrongVehicleItem and display error Message
                    doWrongVehicleLogic(barcodeIn, db, item);

                    itemDescription.setText(item.getName());

                    //EndOfLiveDate Calc
                    try {
                        if (item.getEndOfLifeDate() != null || !item.getEndOfLifeDate().equals("")) {

                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            Date endOfLifeDate = format.parse(item.getEndOfLifeDate().split("T")[0]);
                            long endOfLifeDateDiff = Core.get().getDaysFromNowToDate(endOfLifeDate);
                            endOfLive.setText(item.getEndOfLifeDate().split("T")[0]);

                            if (endOfLifeDateDiff <= 0) {

                                endOfLive.setTextColor(ContextCompat.getColor(this, R.color.red));

                            }
                        }
                    } catch (Exception err) {

                        Core.get().showMessage("Unable to calculate EndOfLife days : Message " + err.getMessage(), this, TAG);
                    }

                    //TestDueDate Calc
                    try {
                        if (item.getTestDueDate() != null || !item.getTestDueDate().equals("")) {

                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            Date testDueDate = format.parse(item.getTestDueDate().split("T")[0]);
                            long testDueDateDiff = Core.get().getDaysFromNowToDate(testDueDate);

                            testDue.setText(item.getTestDueDate().split("T")[0]);

                            if (testDueDateDiff <= 0) {
                                testDue.setTextColor(ContextCompat.getColor(this, R.color.red));
                            }

                        }
                    } catch (Exception err) {

                        Core.get().showMessage("Unable to calculate TestDueDate days : Message " + err.getMessage(), this, TAG);
                    }

                }
            } else {

                //Create New Item
                createNewItemLogic();
            }

        } catch (Exception err) {

            Core.get().showMessage("Error occurred in doBarcodeFind : Message " + err.getMessage(), this, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

        }

    }

    private void doWrongVehicleLogic(String barcodeIn, DbHelper db, ItemAndItemAudit item) {

        //Test to see if from correct vehicle
        if (item.getVehicleId() != mPreferences.getVehicleId()) {

            wrongVehicleItem = db.getItemPerBarcode(this, barcodeIn);

            //Display Wrong Vehicle Detail:
            if (wrongVehicleItem != null) {

                wrongVehicle.setText(" Belongs to Vehicle " + wrongVehicleItem.getRego());
                wrongVehicle.setVisibility(View.VISIBLE);
            }

        }
    }

    private void createNewItemLogic() {
        if (mPreferences.getAccessLevel().toLowerCase().contains("admin")) {

            Intent i = new Intent(this, AddItem.class);
            i.putExtra("barcode", barcode);
            startActivity(i);

        } else {

            Core.get().showMessage("Item does not Exist", this, TAG);
        }
    }

    public void doPass(View view) {

        updateCondition("Pass");

        navigateToAuditItems();
    }

    private void navigateToAuditItems() {

        //Clicked on Checkbox: Open Edit
        startActivity(new Intent(this, AuditItems.class));

    }

    public void doFail(View view) {

        updateCondition("Fail");

        navigateToAuditItems();

    }

    private void updateCondition(String type) {
        DbHelper db = null;

        try {

            db = DbHelper.getInstance(this);

            //Test for wrong vehicle record
            if (wrongVehicleItem == null) {

                if ((db.updateItemCondition2(this, barcode, type) == 1)) {

                    Core.get().showMessage("Updated condition", this, TAG);

                } else {

                    Core.get().showMessage("Unable to update condition", this, TAG);

                }

            } else {

                //Update ItemAudit Accordingly: FoundOnVehicleId = CurrentVehicle and Condition = Invalid Vehicle: Rego
                if ((db.updateItemCondition2AndWrongVehicle(this, barcode, type, wrongVehicleItem.getRego(), mPreferences.getVehicleId()) == 1)) {

                    //Todo:Remove Again
                    //List<ItemAudit> wrongVehicleAudits = db.getAllItemAuditRecords(this);
                    Core.get().showMessage("Updated condition", this, TAG);

                } else {

                    Core.get().showMessage("Unable to update condition", this, TAG);

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

    private void setPageSize() {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .90), (int) (height * .30));
    }
}
