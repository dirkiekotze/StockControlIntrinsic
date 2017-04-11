package intrinsic_plant_equipment.plantequipment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import intrinsic_plant_equipment.plantequipment.async.GetChecklistAuditEntry;
import intrinsic_plant_equipment.plantequipment.async.GetItems;
import intrinsic_plant_equipment.plantequipment.async.InsertNewItemsToCloud;
import intrinsic_plant_equipment.plantequipment.async.InsertNewVehicleAuditToCloud;
import intrinsic_plant_equipment.plantequipment.helper.Core;
import intrinsic_plant_equipment.plantequipment.helper.IEquipmentPreferences;
import intrinsic_plant_equipment.plantequipment.model.AuditCombinedData;
import intrinsic_plant_equipment.plantequipment.model.ChecklistVehicleIds;
import intrinsic_plant_equipment.plantequipment.model.Item;
import intrinsic_plant_equipment.plantequipment.model.ItemAudit;
import intrinsic_plant_equipment.plantequipment.model.Vehicle;
import intrinsic_plant_equipment.plantequipment.sqlLite.DbHelper;

public class AuditItems extends BaseClass {

    String TAG = AuditItems.class.getSimpleName();
    IEquipmentPreferences mPreferences;
    TextView vehicleChecklistDetail;
    Button scanButton;
    Button syncButton;
    Button exitButton;
    Button backButton;
    TableLayout auditTable;
    DbHelper db = null;
    Context context;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit_items);
        mPreferences = Core.get().getPreferences();
        getValues();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        context = this;
        populateControlls();
        displayAuditItems();

    }

    private void getValues() {

        //
        DbHelper db = null;

        try {
            db = DbHelper.getInstance(this);
            List<Item> item = db.getAllItems(this, mPreferences.getVehicleId());
            List<ItemAudit> iAudit = db.getItemAuditPerVehicleId(this, mPreferences.getVehicleId());
            String xx = "Watookal";

        } catch (Exception err) {

            Core.get().showMessage("Error occurred in doBarcodeFind : Message " + err.getMessage(), this, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void displayAuditItems() {

        try {
            //Create the Page
            db = DbHelper.getInstance(this);

            //ToDo:Remove This
            ArrayList<ItemAudit> aItems = db.getAllItemAuditRecords(context);
            List<Item> items = db.getAllItems(context, mPreferences.getVehicleId());

            List<ItemAudit> wrongVehicleAudits = db.getAllItemAuditRecordsWrongVehicle(this, mPreferences.getVehicleId());

            //Normal Audit Records
            List<AuditCombinedData> auditLst = db.getAuditCombinedData(this, mPreferences.getVehicleId(), mPreferences.getChecklistId());

            //Wrong Vehicle Ones
            //List<AuditCombinedData> wrongVehicle = db.getAuditCombinedForWrongVehicleData(this, mPreferences.getVehicleId(), mPreferences.getChecklistId());

            //Do Insert here: Where Status <> new if found.
            if (auditLst != null) {

                for (int i = 0; i < auditLst.size(); i++) {

                    ItemAudit itemAuditExist = db.getItemAuditPerBarcode(this, auditLst.get(i).getBarcode());

                    //Does Not Exist
                    if (itemAuditExist == null) {

                        //int itemId, String barcode, String note, String condition, String date, int checklistId, int vehicleId, String status,int id,String condition2
                        db.addItemAudit(this, new ItemAudit(
                                auditLst.get(i).getItemId(),          //ItemId
                                auditLst.get(i).getBarcode(),         //BarCode
                                auditLst.get(i).getNote() == null ? "" : auditLst.get(i).getNote(),           //Note
                                auditLst.get(i).getCondition() == null ? "Not Scanned" : auditLst.get(i).getCondition(),       //Condition
                                auditLst.get(i).getDate() == null ? Core.get().getDateNowString() : auditLst.get(i).getDate(),            //Date
                                mPreferences.getChecklistId(),         //ChecklistId
                                auditLst.get(i).getVehicleId(),       //VehicleId
                                "",                                   //Status
                                0,                                    //Id
                                auditLst.get(i).getCondition2(),      //Condition2
                                0));                                  //VehicleFrom
                    }

                }
            }

            if ((auditLst != null) || (wrongVehicleAudits != null)) {

                //Create Headers
                //New Table Row
                TableRow tableRowH = new TableRow(this);
                //tableRowH.setId(auditLst.get(0).getItemId() + 1000);

                //Item Description
                TextView itemH = new TextView(this);
                itemH.setTextSize(25);
                itemH.setText("Assigned Items");
                itemH.setPadding(15, 0, 0, 0);
                itemH.setTextColor(ContextCompat.getColor(context, R.color.black_text));
                tableRowH.addView(itemH);

                //Status - Condition
                TextView conditionH = new TextView(this);
                conditionH.setText("Status");
                conditionH.setTextSize(25);
                conditionH.setTextColor(ContextCompat.getColor(context, R.color.black_text));
                tableRowH.addView(conditionH);

                //Condition = Codition2
                TextView condition2H = new TextView(this);
                condition2H.setText("Condition");
                condition2H.setTextSize(25);
                condition2H.setTextColor(ContextCompat.getColor(context, R.color.black_text));
                tableRowH.addView(condition2H);

                //Checked
                TextView checkedH = new TextView(this);
                checkedH.setText("Checked");
                checkedH.setTextSize(25);
                checkedH.setTextColor(ContextCompat.getColor(context, R.color.black_text));
                tableRowH.addView(checkedH);
                auditTable.addView(tableRowH, new TableLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));

                //Green Line Under Heading
                TableRow tableRowUnderline = new TableRow(this);
                Resources res = getResources();
                Drawable line = res.getDrawable(R.drawable.line);
                TextView dummyTextView = new TextView(this);
                dummyTextView.setBackground(line);
                tableRowUnderline.addView(dummyTextView);
                TableRow.LayoutParams the_param;
                the_param = (TableRow.LayoutParams) dummyTextView.getLayoutParams();
                the_param.span = 4;
                dummyTextView.setLayoutParams(the_param);
                auditTable.addView(tableRowUnderline, new TableLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT));


                if (wrongVehicleAudits != null) {
                    for (int i = 0; i < wrongVehicleAudits.size(); i++) {

                        populateTableRowsAuditInfo(wrongVehicleAudits, i);

                    }
                }


                //Create Table Row of Entries
                if (auditLst != null) {
                    for (int i = 0; i < auditLst.size(); i++) {

                        populateTableRows(auditLst, i);


                    }
                }


            }
        } catch (Exception err) {

            Core.get().showMessage("Unable to create displayAuditItems Message : " + err.getMessage(), this, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

        }


    }

    private void populateTableRows(List<AuditCombinedData> auditLst, int i) {
        //New Table Row
        TableRow tableRow = new TableRow(this);
        tableRow.setId(auditLst.get(0).getItemId() + 1000);

        //Item Description
        TextView item = new TextView(this);
        item.setTextSize(20);
        item.setPadding(15, 0, 0, 0);
        item.setText(auditLst.get(i).getName());
        tableRow.addView(item);

        //Status - Condition
        TextView condition = new TextView(this);
        condition.setText(auditLst.get(i).getCondition() == null || auditLst.get(i).getCondition().equals("") ? "Not Scanned" : auditLst.get(i).getCondition());
        condition.setTextSize(20);
        if (auditLst.get(i).getCondition() != null) {
            if (auditLst.get(i).getCondition().toLowerCase().contains("invalid")) {
                condition.setTextColor(ContextCompat.getColor(context, R.color.red));
            }
        }

        tableRow.addView(condition);

        //Condition = Codition2
        TextView condition2 = new TextView(this);
        condition2.setText(auditLst.get(i).getCondition2());
        condition2.setTextSize(20);
        tableRow.addView(condition2);

        //Checked Image
        ImageView auditChecked = new ImageView(this);
        auditChecked.setId(auditLst.get(i).getItemId());

        //Image Display
        if ((auditLst.get(i).getCondition() == null || auditLst.get(i).getCondition().equals("")) || (auditLst.get(i).getCondition().toLowerCase().equals("not scanned"))) {
            auditChecked.setImageResource(R.drawable.crossincircle);
        } else if ((auditLst.get(i).getCondition().toLowerCase().equals("scanned")) || (auditLst.get(i).getCondition().toLowerCase().contains("invalid"))) {
            auditChecked.setImageResource(R.drawable.checkincircle);
            auditChecked.setOnClickListener(handleClickOnChecked(auditChecked));

        }

        auditChecked.setId(auditLst.get(i).getItemId());
        tableRow.addView(auditChecked);

        auditTable.addView(tableRow, new TableLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
    }

    private void populateTableRowsAuditInfo(List<ItemAudit> auditLst, int i) {
        //New Table Row
        TableRow tableRow = new TableRow(this);

        //Item Description
        TextView item = new TextView(this);
        item.setTextSize(20);
        item.setPadding(15, 0, 0, 0);
        item.setText(getItemDescription(auditLst.get(i)));
        tableRow.addView(item);

        //Status - Condition
        TextView condition = new TextView(this);
        condition.setText(auditLst.get(i).getCondition() == null || auditLst.get(i).getCondition().equals("") ? "Not Scanned" : auditLst.get(i).getCondition());
        condition.setTextSize(20);
        if (auditLst.get(i).getCondition() != null) {
            if (auditLst.get(i).getCondition().toLowerCase().contains("invalid")) {
                condition.setTextColor(ContextCompat.getColor(context, R.color.red));
            }
        }

        tableRow.addView(condition);

        //Condition = Codition2
        TextView condition2 = new TextView(this);
        condition2.setText(auditLst.get(i).getCondition2());
        condition2.setTextSize(20);
        tableRow.addView(condition2);

        //Checked Image
        ImageView auditChecked = new ImageView(this);
        auditChecked.setId(auditLst.get(i).getItemId());

        //Image Display
        if ((auditLst.get(i).getCondition() == null || auditLst.get(i).getCondition().equals("")) || (auditLst.get(i).getCondition().toLowerCase().equals("not scanned"))) {
            auditChecked.setImageResource(R.drawable.crossincircle);
        } else if ((auditLst.get(i).getCondition().toLowerCase().equals("scanned")) || (auditLst.get(i).getCondition().toLowerCase().contains("invalid"))) {
            auditChecked.setImageResource(R.drawable.checkincircle);
            auditChecked.setOnClickListener(handleClickOnChecked(auditChecked));

        }

        auditChecked.setId(auditLst.get(i).getItemId());
        tableRow.addView(auditChecked);

        auditTable.addView(tableRow, new TableLayout.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
    }

    private String getItemDescription(ItemAudit itemAudit) {

        DbHelper db = null;

        try {
            db = DbHelper.getInstance(this);
            Item item = db.getItemPerBarcode(this, itemAudit.getBarcode());
            if (item != null) {
                return item.getName();
            }

        } catch (Exception err) {

            Core.get().showMessage("Unable to get getItemDescription : Message " + err.getMessage(), this, TAG);
            return "";

        } finally {

            if (db != null) {
                db.close();
            }
        }

        return null;
    }

    private View.OnClickListener handleClickOnChecked(final ImageView auditChecked) {
        return new View.OnClickListener() {
            public void onClick(View v) {


                try {
                    db = DbHelper.getInstance(context);
                    String barCode = db.getAuditBarcodeFromItemId(context, auditChecked.getId());

                    if (!barCode.equals("")) {

                        Intent i = new Intent(context, EditItem.class);
                        i.putExtra("barcode", barCode);
                        i.putExtra("redirect", true);
                        startActivity(i);
                    } else {

                        Core.get().showMessage("Unable to update, Barcode not found", context, TAG);
                    }
                } catch (Exception err) {

                    Core.get().showMessage("Unable to update, Barcode Error: Message : " + err.getMessage(), context, TAG);
                }


            }


        };

    }

    private void populateControlls() {

        scanButton = (Button) findViewById(R.id.scanButton);
        syncButton = (Button) findViewById(R.id.syncButton);
        exitButton = (Button) findViewById(R.id.exitButton);
        backButton = (Button) findViewById(R.id.backButton);
        auditTable = (TableLayout) findViewById(R.id.auditTable);


        vehicleChecklistDetail = (TextView) findViewById(R.id.vehicleChecklistDetail);
        vehicleChecklistDetail.setText(mPreferences.getVehicleRego() + " (Checklist " + mPreferences.getChecklistName() + ")");

    }

    public void doScan(View view) {

        startActivity(new Intent(this, Scanner.class));

    }

    public void doSync(View view) {

        if (!Core.get().isConnectedToInternet(this)) {

            Core.get().showMessage("You are not Connected to Internet.", this, TAG);
            return;

        }

        uploadDataToServer();

    }

    private void uploadDataToServer() {

        //getNewItems
        DbHelper db = null;

        try {

            GetChecklistAuditEntry checklistAudit = new GetChecklistAuditEntry(this, false);
            checklistAudit.execute(new ChecklistVehicleIds(mPreferences.getChecklistId(), mPreferences.getVehicleId()));


            //Upload Vehicle: Load Vehicles to local DB again
            InsertNewVehicleAuditToCloud vehicleAudit = new InsertNewVehicleAuditToCloud(context, false);
            vehicleAudit.execute("start");

            InsertNewItemsToCloud newItems = new InsertNewItemsToCloud(this, false);
            newItems.execute("start");

        } catch (Exception err) {

            Core.get().showMessage("Error occurred in Checklist Spinner : Message " + err.getMessage(), this, TAG);

        } finally {

            if (db != null) {
                db.close();
            }
        }
    }

    public void doExit(View view) {

        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);

    }

    public void doBack(View view) {

        startActivity(new Intent(this, VehicleAndChecklistSelect.class));
    }

    public void doLogout(View view) {

        startActivity(new Intent(this, Login.class));
    }
}
