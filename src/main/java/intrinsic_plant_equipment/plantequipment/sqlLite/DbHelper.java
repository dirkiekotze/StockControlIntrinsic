package intrinsic_plant_equipment.plantequipment.sqlLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import intrinsic_plant_equipment.plantequipment.helper.Core;
import intrinsic_plant_equipment.plantequipment.model.AuditCombinedData;
import intrinsic_plant_equipment.plantequipment.model.Category;
import intrinsic_plant_equipment.plantequipment.model.Checklist;
import intrinsic_plant_equipment.plantequipment.model.Item;
import intrinsic_plant_equipment.plantequipment.model.ItemAndItemAudit;
import intrinsic_plant_equipment.plantequipment.model.ItemAudit;
import intrinsic_plant_equipment.plantequipment.model.ItemType;
import intrinsic_plant_equipment.plantequipment.model.Vehicle;
import intrinsic_plant_equipment.plantequipment.model.VehicleAudit;

/**
 * Created by Dirk on 8/03/2017.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static DbHelper mInstance = null;
    String TAG = DbHelper.class.getSimpleName();

    public DbHelper(Context context) {
        super(context, "equipment", null, 102);
    }

    public static DbHelper getInstance(Context ctx) {

        if (mInstance == null) {
            mInstance = new DbHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS Category(id INTEGER PRIMARY KEY,CloudCategoryId,Name,Type)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Vehicle(id INTEGER PRIMARY KEY,VehicleId,Rego,Type,ServiceDueKlms,ServicePlantDate,PlantElecTestDate,CurrentKms)");
        db.execSQL("CREATE TABLE IF NOT EXISTS VehicleAudit(id INTEGER PRIMARY KEY,VehicleAuditId, VehicleId,UserId,Rego,ChecklistId,DateStamp,Kilometers)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Checklist(id INTEGER PRIMARY KEY,CloudId,Name,ListDate)");
        db.execSQL("CREATE TABLE IF NOT EXISTS ItemType(id INTEGER PRIMARY KEY,ItemTypeId,CategoryId,Name,InspectionInterval,InspectionRequired)");
        db.execSQL("CREATE TABLE IF NOT EXISTS Item(id INTEGER PRIMARY KEY,ItemId,ItemTypeId,VehicleId,LastTestStatus,TestBy,ReportNumber,LastVehicleId,Barcode,Name,Rego,Status,RequiresInspection,LastScannedDate,TestDueDate,EndOfLifeDate,Condition2,LasVehicleId)");
        db.execSQL("CREATE TABLE IF NOT EXISTS ItemAudit(id INTEGER PRIMARY KEY,ItemId,VehicleId,ChecklistId,Date,Condition,Note,Barcode,Status,Condition2,FoundOnVehicleId)");
        db.execSQL("CREATE TABLE IF NOT EXISTS AuditCombined(id INTEGER PRIMARY KEY,name, testDueDate,testBy,note,condition,[date],barcode,vehicleId,itemCloudId)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS Category");
        db.execSQL("DROP TABLE IF EXISTS Vehicle");
        db.execSQL("DROP TABLE IF EXISTS VehicleAudit");
        db.execSQL("DROP TABLE IF EXISTS Checklist");
        db.execSQL("DROP TABLE IF EXISTS ItemType");
        db.execSQL("DROP TABLE IF EXISTS Item");
        db.execSQL("DROP TABLE IF EXISTS ItemAudit");
        db.execSQL("DROP TABLE IF EXISTS AuditCombined");

        onCreate(db);

    }

    /*Category*/
    public void deleteCategory(Context context) {

        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            String sql = "Delete from Category";
            db.execSQL(sql);

        } catch (Exception err) {

            Core.get().showMessage("Unable to do deleteCategory", context, TAG);

        } finally {
            if (db != null) {
                db.close();
            }
        }


    }

    public void addCategory(Context context, Category category) {

        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            //id INTEGER PRIMARY KEY,CloudCategoryId,Name,Type
            String sql = "INSERT INTO Category (id,CloudCategoryId,Name,Type) VALUES(null," + category.getId() + ",'" + category.getName() + "','" + category.getType() + "')";
            db.execSQL(sql);
        } catch (Exception err) {

            Core.get().showMessage("Unable to do addCategory Message : " + err.getMessage(), context, TAG);

        } finally {
            if (db != null) {
                db.close();
            }
        }

    }

    public List<Category> getAllCategories(Context context) {

        List<Category> categoryLst = new ArrayList<Category>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getWritableDatabase();

            String sql = "SELECT CloudCategoryId,Name,Type FROM [Category] order by [name]";

            cursor = db.rawQuery(sql, null);
            int cnt = 0;

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    //int cloudCategoryId, String type, String name
                    Category c = new Category(cursor.getInt(0), cursor.getString(1), cursor.getString(2), 0);
                    categoryLst.add(c);
                    cnt++;
                } while (cursor.moveToNext());

                return categoryLst;
            }

        } catch (Exception err) {

            Core.get().showMessage("Unable to extract getAllCategoriesMessage: " + err.getMessage(), context, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    /*Vehicle*/
    public void deleteVehicle(Context context) {

        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            String sql = "Delete from Vehicle";
            db.execSQL(sql);

        } catch (Exception err) {

            Core.get().showMessage("Unable to do deleteVehicle", context, TAG);

        } finally {
            if (db != null) {
                db.close();
            }
        }


    }

     public Vehicle getVehiclePerId(Context context, int vehicleId) {

        List<Vehicle> vehicleLst = new ArrayList<Vehicle>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        Vehicle v = null;

        try {
            db = getWritableDatabase();

            String sql = "SELECT VehicleId,Rego,Type,ServiceDueKlms,ServicePlantDate,PlantElecTestDate,CurrentKms FROM vehicle where vehicleId = " + vehicleId;

            cursor = db.rawQuery(sql, null);
            int cnt = 0;

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    //int cloudId, String date, String name
                    v = new Vehicle(cursor.getInt(0), cursor.getString(1), 0, cursor.getString(2), cursor.getInt(3), cursor.getString(4), cursor.getString(5), cursor.getInt(6));
                    cnt++;
                } while (cursor.moveToNext());

                return v;
            }

        } catch (Exception err) {

            Core.get().showMessage("Unable to extract getVehiclePerId Message: " + err.getMessage(), context, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    public void addVehicle(Context context, Vehicle vehicle) {

        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put("VehicleId", vehicle.getVehicleId());
            cv.put("Rego", vehicle.getRego());
            cv.put("Type", vehicle.getType());
            cv.put("ServiceDueKlms", vehicle.getServiceDueKlms());
            cv.put("ServicePlantDate", vehicle.getServicePlantDate());
            cv.put("PlantElecTestDate", vehicle.getPlantElecTestDate());
            cv.put("CurrentKms", vehicle.getCurrentKms());

            long retValue = db.insertOrThrow("[Vehicle]", null, cv);

        } catch (Exception err) {

            Core.get().showMessage("Unable to do addVehicle Message : " + err.getMessage(), context, TAG);

        } finally {
            if (db != null) {
                db.close();
            }
        }

    }

    public List<Vehicle> getAllVehicles(Context context) {

        List<Vehicle> vehicleLst = new ArrayList<Vehicle>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getWritableDatabase();

            String sql = "SELECT VehicleId,Rego,Type,ServiceDueKlms,ServicePlantDate,PlantElecTestDate,CurrentKms FROM vehicle order by rego";

            cursor = db.rawQuery(sql, null);
            int cnt = 0;

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    //int cloudId, String date, String name
                    Vehicle v = new Vehicle(cursor.getInt(0), cursor.getString(1), 0, cursor.getString(2), cursor.getInt(3), cursor.getString(4), cursor.getString(5), cursor.getInt(6));
                    vehicleLst.add(v);
                    cnt++;
                } while (cursor.moveToNext());

                return vehicleLst;
            }

        } catch (Exception err) {

            Core.get().showMessage("Unable to extract getAllChecklistItems Message: " + err.getMessage(), context, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    /*VehicleAudit*/
    public void deleteVehicleAudit(Context context,int checklistId) {

        SQLiteDatabase db = null;

        try {

            db = this.getWritableDatabase();
            db.delete("VehicleAudit", "checklistId = " + checklistId, null);


        } catch (Exception err) {

            Core.get().showMessage("Unable to do deleteVehicleAudit", context, TAG);

        } finally {
            if (db != null) {
                db.close();
            }
        }

    }

    public void deleteVehicleAuditPerVehicleId(Context context,int vehicleId) {

        SQLiteDatabase db = null;

        try {

            db = this.getWritableDatabase();
            db.delete("VehicleAudit", "vehicleId = " + vehicleId, null);


        } catch (Exception err) {

            Core.get().showMessage("Unable to do deleteVehicleAudit", context, TAG);

        } finally {
            if (db != null) {
                db.close();
            }
        }

    }

    public void addVehicleAudit(Context context, VehicleAudit audit) {

        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            //id,VehicleAuditId, VehicleId,UserId,Rego,ChecklistId,DateStamp,Kilometers
            ContentValues cv = new ContentValues();
            cv.put("VehicleAuditId", audit.getVehicleAuditId());
            cv.put("VehicleId", audit.getVehicleId());
            cv.put("UserId", audit.getUserId());
            cv.put("Rego", audit.getRego());
            cv.put("ChecklistId", audit.getChecklistId());
            cv.put("DateStamp", audit.getDateStamp());
            cv.put("Kilometers", audit.getKilometers());

            long retValue = db.insertOrThrow("vehicleAudit", null, cv);

        } catch (Exception err) {

            Core.get().showMessage("Unable to do addVehicleAudit Message : " + err.getMessage(), context, TAG);

        } finally {
            if (db != null) {
                db.close();
            }
        }

    }

    public List<VehicleAudit> getAllVehicleAudits(Context context) {

        List<VehicleAudit> vehicleAuditLst = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getWritableDatabase();

            String sql = "SELECT VehicleAuditId, VehicleId,UserId,ChecklistId,Kilometers,Rego,DateStamp FROM vehicleAudit";

            cursor = db.rawQuery(sql, null);
            int cnt = 0;

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    //int vehicleAuditId,int vehicleId,int userId,int checklistId,int kilometers,  String rego, String dateStamp
                    VehicleAudit va = new VehicleAudit(
                            cursor.getInt(0),       //VehicleAuditId
                            cursor.getInt(1),       //VehicleId
                            cursor.getInt(2),       //UserId
                            cursor.getInt(3),       //ChecklistId
                            cursor.getInt(4),       //Kilometers
                            cursor.getString(5),    //Rego
                            cursor.getString(6));   //DateStamp
                    vehicleAuditLst.add(va);
                    cnt++;
                } while (cursor.moveToNext());

                return vehicleAuditLst;
            }

        } catch (Exception err) {

            Core.get().showMessage("Unable to extract getAllVehicleAudits Message: " + err.getMessage(), context, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    public VehicleAudit getVehicleAuditPerVehicleId(Context context,int vehicleId) {

        SQLiteDatabase db = null;
        Cursor cursor = null;
        VehicleAudit va = null;

        try {
            db = getWritableDatabase();

            String sql = "SELECT VehicleAuditId, VehicleId,UserId,Rego,ChecklistId,DateStamp,Kilometers FROM vehicleAudit where vehicleId = " + vehicleId;

            cursor = db.rawQuery(sql, null);
            int cnt = 0;

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    //int vehicleAuditId,int vehicleId,int userId,int checklistId,int kilometers,  String rego, String dateStamp
                    va = new VehicleAudit(
                            cursor.getInt(0),
                            cursor.getInt(1),
                            cursor.getInt(2),
                            cursor.getInt(3),
                            cursor.getInt(4),
                            cursor.getString(5),
                            cursor.getString(6));
                    cnt++;
                } while (cursor.moveToNext());

                return va;
            }

        } catch (Exception err) {

            Core.get().showMessage("Unable to extract getVehicleAuditPerVehicleId Message: " + err.getMessage(), context, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    /*Checklist*/
    public void deleteChecklist(Context context) {

        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            String sql = "Delete from Checklist";
            db.execSQL(sql);

        } catch (Exception err) {

            Core.get().showMessage("Unable to do deleteChecklist", context, TAG);

        } finally {
            if (db != null) {
                db.close();
            }
        }


    }

    public List<Checklist> getAllChecklistItems(Context context) {

        List<Checklist> checkLst = new ArrayList<Checklist>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getWritableDatabase();

            String sql = "SELECT CloudId,ListDate,[Name] FROM [Checklist] order by [name]";

            cursor = db.rawQuery(sql, null);
            int cnt = 0;

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    //int cloudId, String date, String name
                    Checklist l = new Checklist(cursor.getInt(0), cursor.getString(1), cursor.getString(2), 0);
                    checkLst.add(l);
                    cnt++;
                } while (cursor.moveToNext());

                return checkLst;
            }

        } catch (Exception err) {

            Core.get().showMessage("Unable to extract getAllChecklistItems Message: " + err.getMessage(), context, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    public void addChecklist(Context context, Checklist checkList) {

        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            //id INTEGER PRIMARY KEY,CloudId,Name,ListDate
            String sql = "INSERT INTO Checklist (id,CloudId,Name,ListDate) VALUES(null," + checkList.getId() + ",'" + checkList.getName() + "','" + checkList.getListDate() + "')";
            db.execSQL(sql);
        } catch (Exception err) {

            Core.get().showMessage("Unable to do addChecklist Message : " + err.getMessage(), context, TAG);

        } finally {
            if (db != null) {
                db.close();
            }
        }

    }

    /*ItemType*/
    public void deleteItemType(Context context) {

        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            String sql = "Delete from ItemType";
            db.execSQL(sql);

        } catch (Exception err) {

            Core.get().showMessage("Unable to do deleteItemType", context, TAG);

        } finally {
            if (db != null) {
                db.close();
            }
        }


    }

    public void addItemType(Context context, ItemType itemType) {

        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            //id INTEGER PRIMARY KEY,ItemTypeId,CategoryId,Name,InspectionInterval,InspectionRequired
            String sql = "INSERT INTO ItemType (id,ItemTypeId,CategoryId,Name,InspectionInterval,InspectionRequired) VALUES(null," + itemType.getItemTypeId() + "," + itemType.getCategoryId() + ",'" + itemType.getName() + "'," + itemType.getInspectionInterval() + "," + itemType.getInspectionRequired() + ")";
            Core.get().logMessage(sql, TAG);
            db.execSQL(sql);
        } catch (Exception err) {

            Core.get().showMessage("Unable to do addItemType Message : " + err.getMessage(), context, TAG);

        } finally {
            if (db != null) {
                db.close();
            }
        }

    }

    public List<ItemType> getItemTypesPerCategoryId(Context context, int categoryId) {

        List<ItemType> itemTypeLst = new ArrayList<ItemType>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = getWritableDatabase();

            String sql = "SELECT ItemTypeId,InspectionRequired,InspectionInterval,CategoryId,Name FROM [ItemType] where CategoryId = " + categoryId + " order by [name]";

            cursor = db.rawQuery(sql, null);
            int cnt = 0;

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    //int itemTypeId, int inspectionRequired, int inspectionInterval, int categoryId,String name
                    ItemType it = new ItemType(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3), cursor.getString(4));
                    itemTypeLst.add(it);
                    cnt++;
                } while (cursor.moveToNext());

                return itemTypeLst;
            }

        } catch (Exception err) {

            Core.get().showMessage("Unable to extract getItemTypesPerCategoryId Message: " + err.getMessage(), context, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    /*Item*/
    public void deleteItem(Context context) {

        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            String sql = "Delete from Item";
            db.execSQL(sql);

        } catch (Exception err) {

            Core.get().showMessage("Unable to do deleteItem", context, TAG);

        } finally {
            if (db != null) {
                db.close();
            }
        }


    }

    public void deleteItemNotNew(Context context) {

        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            String sql = "Delete from Item where status <> 'new'";
            db.execSQL(sql);

        } catch (Exception err) {

            Core.get().showMessage("Unable to do deleteItem", context, TAG);

        } finally {
            if (db != null) {
                db.close();
            }
        }


    }

    public void deleteItemNew(Context context) {

        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            String sql = "Delete from Item where status = 'new'";
            db.execSQL(sql);

        } catch (Exception err) {

            Core.get().showMessage("Unable to do deleteItem", context, TAG);

        } finally {
            if (db != null) {
                db.close();
            }
        }


    }

    public void addItem(Context context, Item item, String status) {

        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();

            ContentValues cv = new ContentValues();
            cv.put("ItemId", item.getItemId());
            cv.put("ItemTypeId", item.getItemTypeId());
            cv.put("VehicleId", item.getVehicleId());
            cv.put("LastScannedDate", item.getLastScannedDate() == null ? "" : item.getLastScannedDate());
            cv.put("EndOfLifeDate", item.getEndOfLifeDate() == null ? "" : item.getEndOfLifeDate());
            cv.put("TestDueDate", item.getTestDueDate() == null ? "" : item.getTestDueDate());
            cv.put("LastTestStatus", item.getLastTestStatus());
            cv.put("TestBy", item.getTestBy());
            cv.put("ReportNumber", item.getReportNumber());
            cv.put("LasVehicleId", item.getLastVehicleId());
            cv.put("Barcode", item.getBarcode());
            cv.put("Name", item.getName());
            cv.put("Rego", item.getRego());
            if (!status.equals("")) {
                cv.put("Status", status);
            } else {
                cv.put("Status", item.getStatus());
            }

            long retValue = db.insertOrThrow("[Item]", null, cv);

            if (retValue == -1) {

                Core.get().showMessage("Unable to addItem", context, TAG);

            }

            //INSERT INTO Item(                id,ItemCloudId ,ItemTypeId,VehicleId,LastScannedDate,LastTestStatus,TestBy,ReportNumber,LastVehicleId,Barcode,EndOfLifeDate,Name,Rego,Status,TestDueDate)
//            String sql = "INSERT INTO [Item] (id,ItemId                  ,ItemTypeId                  ,VehicleId                  ,LastScannedDate                    ,LastTestStatus                    ,TestBy                    ,ReportNumber                     ,LastVehicleId                  ,Barcode                    ,EndOfLifeDate                    ,Name                     ,Rego                    ,Status    ,TestDueDate) " +
//                    " VALUES(                  null," + item.getItemId() + "," + item.getItemTypeId() + "," + item.getVehicleId() + ",'" + item.getLastScannedDate() + "','" + item.getLastTestStatus() + "','" + item.getTestBy() + "','" + item.getReportNumber() + "'," + item.getLastVehicleId() + ",'" + item.getBarcode() + "','" + item.getEndOfLifeDate() + "',' " + item.getName() + "','" + item.getRego() + "','existing','" + item.getTestDueDate() + "')";
//            Core.get().logMessage(sql, TAG);
//            db.execSQL(sql);
        } catch (Exception err) {

            Core.get().showMessage("Unable to do addItem Message : " + err.getMessage(), context, TAG);

        } finally {
            if (db != null) {
                db.close();
            }
        }

    }

    public Item getItemPerBarcode(Context context, String barcode) {

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getWritableDatabase();
            Item item;

            //id,ItemCloudId,ItemTypeId,VehicleId,LastTestStatus,TestBy,ReportNumber,LastVehicleId,Barcode,Name,Rego,Status,RequiresInspection,LastScannedDate,TestDueDate,EndOfLifeDate
            String sql = "SELECT ItemId,ItemTypeId,VehicleId,LastTestStatus,TestBy,ReportNumber,LastVehicleId,Barcode,Name,Rego,Status,RequiresInspection,LastScannedDate,TestDueDate,EndOfLifeDate,Condition2,LastVehicleId " +
                    " FROM Item WHERE Barcode = '" + barcode + "'";

            cursor = db.rawQuery(sql, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    item = new Item(cursor.getInt(0),     //ItemCloudId
                            cursor.getInt(1),       //ItemTypeId
                            cursor.getInt(2),       //VehicleId
                            cursor.getString(3),    //LastTestStatus
                            cursor.getString(4),    //TestBy
                            cursor.getString(5),    //ReportNumber
                            cursor.getInt(6),       //LastVehicleId
                            cursor.getString(7),    //Barcode
                            cursor.getString(8),     //Name
                            cursor.getString(9),     //Rego
                            cursor.getString(10),    //Status
                            cursor.getString(11),    //RequiresInspection
                            cursor.getString(12),    //LastScannedDate
                            cursor.getString(13),    //TestDate
                            cursor.getString(14),    //EndOfLifeDate
                            cursor.getString(15),    //Condition2
                            cursor.getInt(16));      //LasVehicleId

                } while (cursor.moveToNext());

                return item;
            }

        } catch (Exception err) {

            Core.get().showMessage("Unable to get getItemPerBarcode : " + err.getMessage(), context, TAG);
            return null;

        } finally {

            if (db != null) {
                db.close();
            }

            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    public List<Item> getNewItems(Context context) {

        SQLiteDatabase db = null;
        Cursor cursor = null;
        List<Item> itemLst = new ArrayList<>();

        try {
            db = this.getWritableDatabase();
            Item item;

            //id,ItemCloudId,ItemTypeId,VehicleId,LastTestStatus,TestBy,ReportNumber,LastVehicleId,Barcode,Name,Rego,Status,RequiresInspection,LastScannedDate,TestDueDate,EndOfLifeDate
            String sql = "SELECT ItemId,ItemTypeId,VehicleId,LastTestStatus,TestBy,ReportNumber,LastVehicleId,Barcode,Name,Rego,Status,RequiresInspection,LastScannedDate,TestDueDate,EndOfLifeDate,Condition2,LastVehicleId " +
                    " FROM Item WHERE status = 'new'";

            cursor = db.rawQuery(sql, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    item = new Item(cursor.getInt(0), //ItemId
                            cursor.getInt(1),       //ItemTypeId
                            cursor.getInt(2),       //VehicleId
                            cursor.getString(3),    //LastTestStatus
                            cursor.getString(4),    //TestBy
                            cursor.getString(5),    //ReportNumber
                            cursor.getInt(6),       //LastVehicleId
                            cursor.getString(7),    //Barcode
                            cursor.getString(8),     //Name
                            cursor.getString(9),     //Rego
                            cursor.getString(10),    //Status
                            cursor.getString(11),    //RequiresInspection
                            cursor.getString(12),    //LastScannedDate
                            cursor.getString(13),    //TestDate
                            cursor.getString(14),    //EndOfLifeDate
                            cursor.getString(15),    //Condition2
                            cursor.getInt(16));      //LasVehicleId

                    itemLst.add(item);

                } while (cursor.moveToNext());

                return itemLst;
            }

        } catch (Exception err) {

            Core.get().showMessage("Unable to get getNewItems : " + err.getMessage(), context, TAG);
            return null;

        } finally {

            if (db != null) {
                db.close();
            }

            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    public List<Item> getAllItems(Context context, int vehicleId) {

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getWritableDatabase();
            Item item;

            List<Item> itemLst = new ArrayList<>();

            //id,ItemCloudId,ItemTypeId,VehicleId,LastTestStatus,TestBy,ReportNumber,LastVehicleId,Barcode,Name,Rego,Status,RequiresInspection,LastScannedDate,TestDueDate,EndOfLifeDate
            String sql = "SELECT ItemId,ItemTypeId,VehicleId,LastTestStatus,TestBy,ReportNumber,LastVehicleId,Barcode,Name,Rego,Status,RequiresInspection,LastScannedDate,TestDueDate,EndOfLifeDate,Condition2,LastVehicleId " +
                    " FROM Item where vehicleId = " + vehicleId;

            cursor = db.rawQuery(sql, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    item = new Item(cursor.getInt(0),     //ItemCloudId
                            cursor.getInt(1),       //ItemTypeId
                            cursor.getInt(2),       //VehicleId
                            cursor.getString(3),    //LastTestStatus
                            cursor.getString(4),    //TestBy
                            cursor.getString(5),    //ReportNumber
                            cursor.getInt(6),       //LastVehicleId
                            cursor.getString(7),    //Barcode
                            cursor.getString(8),     //Name
                            cursor.getString(9),     //Rego
                            cursor.getString(10),    //Status
                            cursor.getString(11),    //RequiresInspection
                            cursor.getString(12),    //LastScannedDate
                            cursor.getString(13),    //TestDate
                            cursor.getString(14),    //EndOfLifeDate
                            cursor.getString(15),    //Condition2
                            cursor.getInt(16));      //LasVehicleId

                    itemLst.add(item);

                } while (cursor.moveToNext());

                return itemLst;
            }

        } catch (Exception err) {

            Core.get().showMessage("Unable to get getAllItems : " + err.getMessage(), context, TAG);
            return null;

        } finally {

            if (db != null) {
                db.close();
            }

            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    public List<Item> getAllItems(Context context) {

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getWritableDatabase();
            Item item;

            List<Item> itemLst = new ArrayList<>();

            //id,ItemCloudId,ItemTypeId,VehicleId,LastTestStatus,TestBy,ReportNumber,LastVehicleId,Barcode,Name,Rego,Status,RequiresInspection,LastScannedDate,TestDueDate,EndOfLifeDate
            String sql = "SELECT ItemId,ItemTypeId,VehicleId,LastTestStatus,TestBy,ReportNumber,LastVehicleId,Barcode,Name,Rego,Status,RequiresInspection,LastScannedDate,TestDueDate,EndOfLifeDate,Condition2,LastVehicleId " +
                    " FROM Item ";

            cursor = db.rawQuery(sql, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    item = new Item(cursor.getInt(0),     //ItemCloudId
                            cursor.getInt(1),       //ItemTypeId
                            cursor.getInt(2),       //VehicleId
                            cursor.getString(3),    //LastTestStatus
                            cursor.getString(4),    //TestBy
                            cursor.getString(5),    //ReportNumber
                            cursor.getInt(6),       //LastVehicleId
                            cursor.getString(7),    //Barcode
                            cursor.getString(8),     //Name
                            cursor.getString(9),     //Rego
                            cursor.getString(10),    //Status
                            cursor.getString(11),    //RequiresInspection
                            cursor.getString(12),    //LastScannedDate
                            cursor.getString(13),    //TestDate
                            cursor.getString(14),    //EndOfLifeDate
                            cursor.getString(15),    //Condition2
                            cursor.getInt(16));      //LasVehicleId

                    itemLst.add(item);

                } while (cursor.moveToNext());

                return itemLst;
            }

        } catch (Exception err) {

            Core.get().showMessage("Unable to get getAllItems : " + err.getMessage(), context, TAG);
            return null;

        } finally {

            if (db != null) {
                db.close();
            }

            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    public int updateItemCondition2(Context context, String barcode, String condition2) {

        SQLiteDatabase db = null;
        try {

            db = this.getWritableDatabase();

            String sql = "Update ItemAudit set Condition = 'Scanned', Condition2 = '" + condition2 + "' where Barcode = '" + barcode + "'";
            db.execSQL(sql);
            return 1;
        } catch (Exception err) {

            Core.get().showMessage("Unable to extract updateItemCondition2 Message: " + err.getMessage(), context, TAG);
            return 0;
        } finally {

            if (db != null) {
                db.close();
            }
        }
    }

    public int updateItemCondition2AndWrongVehicle(Context context, String barcode, String condition2,String rego,int vehicleId) {

        SQLiteDatabase db = null;
        try {

            db = this.getWritableDatabase();

            String sql = "Update ItemAudit set Condition = 'Invalid Vehicle: " + rego  + "', Condition2 = '" + condition2 + "', FoundOnVehicleId = " + vehicleId  + " where Barcode = '" + barcode + "'";
            db.execSQL(sql);
            return 1;
        } catch (Exception err) {

            Core.get().showMessage("Unable to extract updateItemCondition2 Message: " + err.getMessage(), context, TAG);
            return 0;
        } finally {

            if (db != null) {
                db.close();
            }
        }
    }

    public int updateItemScanned(Context context, String barcode,int itemId, String endOfLiveDate, String rego, String testBy, String reportNumber, String testDueDate, String lastScannedDate) {

        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("endOfLifeDate", endOfLiveDate);
            cv.put("testBy", testBy);
            cv.put("reportNumber", reportNumber);
            cv.put("testDueDate", testDueDate);
            cv.put("lastScannedDate", lastScannedDate);
            cv.put("rego", rego);
            cv.put("status","new");
            if (db.update("[Item]", cv, "barcode= '" + barcode + "'", null) > 0) {
                return 1;
            } else {
                Core.get().showMessage("Unable to extract updateItem: ", context, TAG);
                return 0;
            }
        } catch (Exception err) {

            Core.get().showMessage("Unable to extract updateItemCondition2 Message: " + err.getMessage(), context, TAG);
            return 0;

        } finally {

            if (db != null) {
                db.close();
            }
        }


    }

    /*ItemAudit*/
    public void deleteItemAudit(Context context) {

        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            String sql = "Delete from ItemAudit";
            db.execSQL(sql);

        } catch (Exception err) {

            Core.get().showMessage("Unable to do deleteItemAudit", context, TAG);

        } finally {
            if (db != null) {
                db.close();
            }
        }


    }

    public void deleteItemAuditPerBarcode(Context context, String barcode) {

        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            String sql = "Delete from ItemAudit where barcode = '" + barcode + "'";
            db.execSQL(sql);

        } catch (Exception err) {

            Core.get().showMessage("Unable to do deleteItemAuditPerBarcode", context, TAG);

        } finally {
            if (db != null) {
                db.close();
            }
        }


    }

    public void deleteItemAuditStusNotNew(Context context) {

        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            String sql = "delete from ItemAudit where Status <> 'new'";
            db.execSQL(sql);

        } catch (Exception err) {

            Core.get().showMessage("Unable to do deleteItemAuditStusNotNew", context, TAG);

        } finally {
            if (db != null) {
                db.close();
            }
        }


    }

    public void addItemAudit(Context context, ItemAudit audit) {

        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put("itemId", audit.getItemId());
            cv.put("barcode", audit.getBarcode());
            cv.put("note", audit.getNote());
            cv.put("condition", audit.getCondition());
            cv.put("[date]", audit.getDate());
            cv.put("checklistId", audit.getChecklistId());
            cv.put("vehicleId", audit.getVehicleId());
            cv.put("status", "new");
            cv.put("FoundOnVehicleId", audit.getFoundOnVehicleId());
            cv.put("condition2", audit.getCondition2());

            long retValue = db.insert("ItemAudit", null, cv);

            if (retValue == -1) {

                Core.get().showMessage("Unable to addItemAudit", context, TAG);
            }

            //id INTEGER PRIMARY KEY,CloudId,VehicleId,ChecklistId,Date,Condition,Note,Barcode,Status
//            String sql = "INSERT INTO ItemAudit (id,ItemId,VehicleId,ChecklistId,[Date],Condition,Note,Status,Barcode) " +
//                    " VALUES(null," + audit.getItemId() + "," + audit.getVehicleId() + "," + audit.getChecklistId() + ",'" + audit.getDate()
//                    + "','" + audit.getCondition() + "', '" + audit.getNote() + "','new','" + audit.getBarcode() + "')";
//            db.execSQL(sql);


        } catch (Exception err) {

            Core.get().showMessage("Unable to do addItemAudit Message : " + err.getMessage(), context, TAG);

        } finally {
            if (db != null) {
                db.close();
            }
        }

    }

    public ArrayList<ItemAudit> getItemAuditPerVehicleId(Context context, int vehicleId) {

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            ArrayList<ItemAudit> lstAudit = new ArrayList<ItemAudit>();
            db = this.getWritableDatabase();

            //id INTEGER PRIMARY KEY,CloudId,VehicleId,ChecklistId,Date,Condition,Note,Barcode,Status

            String sql = "SELECT ItemId,Barcode,Note,Condition,[Date],ChecklistId,VehicleId,Status,Id,Condition2,FoundOnVehicleId " +
                    " FROM ItemAudit WHERE VehicleId = " + vehicleId;
            cursor = db.rawQuery(sql, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    //int cloudId   , String barcode     , String note        , String condition  , String date        , int checklistId , int vehicleId , String status,int id
                    ItemAudit audit = new ItemAudit(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getInt(5), cursor.getInt(6), cursor.getString(7), cursor.getInt(8), cursor.getString(9),cursor.getInt(10));
                    lstAudit.add(audit);
                } while (cursor.moveToNext());

                return lstAudit;
            }

        } catch (Exception err) {

            Core.get().showMessage("Unable to get getAuditPerVehicleChecklist : " + err.getMessage(), context, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    public String getAuditBarcodeFromItemId(Context context, int itemId) {

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            ArrayList<ItemAudit> lstAudit = new ArrayList<ItemAudit>();
            db = this.getWritableDatabase();

            String sql = "SELECT Barcode FROM ItemAudit WHERE itemId = " + itemId;
            cursor = db.rawQuery(sql, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    return cursor.getString(0);
                } while (cursor.moveToNext());


            }

        } catch (Exception err) {

            Core.get().showMessage("Unable to get getAuditPerVehicleChecklist : " + err.getMessage(), context, TAG);
            return "";

        } finally {

            if (db != null) {
                db.close();
            }

            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    public ArrayList<ItemAudit> getAllItemAuditRecords(Context context) {

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            ArrayList<ItemAudit> lstAudit = new ArrayList<ItemAudit>();
            db = this.getWritableDatabase();

            //id INTEGER PRIMARY KEY,CloudId,VehicleId,ChecklistId,Date,Condition,Note,Barcode,Status

            String sql = "SELECT ItemId,Barcode,Note,Condition,[Date],ChecklistId,VehicleId,Status,Id,Condition2,FoundOnVehicleId FROM ItemAudit order by ItemId";
            cursor = db.rawQuery(sql, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    //int cloudId   , String barcode     , String note        , String condition  , String date        , int checklistId , int vehicleId , String status,int id
                    ItemAudit audit = new ItemAudit(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getInt(5), cursor.getInt(6), cursor.getString(7), cursor.getInt(8), cursor.getString(9),cursor.getInt(10));
                    lstAudit.add(audit);
                } while (cursor.moveToNext());

                return lstAudit;
            }

        } catch (Exception err) {

            Core.get().showMessage("Unable to get getAllItemAuditRecords : " + err.getMessage(), context, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    public ArrayList<ItemAudit> getAllItemAuditRecordsWrongVehicle(Context context,int vehicleId) {

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            ArrayList<ItemAudit> lstAudit = new ArrayList<ItemAudit>();
            db = this.getWritableDatabase();

            //id INTEGER PRIMARY KEY,CloudId,VehicleId,ChecklistId,Date,Condition,Note,Barcode,Status

            String sql = "SELECT ItemId,Barcode,Note,Condition,[Date],ChecklistId,VehicleId,Status,Id,Condition2,FoundOnVehicleId FROM ItemAudit where FoundOnVehicleId = " + vehicleId + "  order by ItemId";
            cursor = db.rawQuery(sql, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    //int cloudId   , String barcode     , String note        , String condition  , String date        , int checklistId , int vehicleId , String status,int id
                    ItemAudit audit = new ItemAudit(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getInt(5), cursor.getInt(6), cursor.getString(7), cursor.getInt(8), cursor.getString(9),cursor.getInt(10));
                    lstAudit.add(audit);
                } while (cursor.moveToNext());

                return lstAudit;
            }

        } catch (Exception err) {

            Core.get().showMessage("Unable to get getAllItemAuditRecords : " + err.getMessage(), context, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }


    public ItemAudit getItemAuditPerBarcode(Context context, String barcode) {

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getWritableDatabase();
            ItemAudit audit;

            //id INTEGER PRIMARY KEY,CloudId,VehicleId,ChecklistId,Date,Condition,Note,Barcode,Status

            String sql = "SELECT ItemId,Barcode,Note,Condition,[Date],ChecklistId,VehicleId,Status,Id,Condition2,FoundOnVehicleId " +
                    " FROM ItemAudit WHERE Barcode = '" + barcode + "'";
            cursor = db.rawQuery(sql, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    //int cloudId   , String barcode     , String note        , String condition  , String date        , int checklistId , int vehicleId , String status,int id
                    audit = new ItemAudit(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getInt(5), cursor.getInt(6), cursor.getString(7), cursor.getInt(8), cursor.getString(9),cursor.getInt(10));

                } while (cursor.moveToNext());

                return audit;
            }

        } catch (Exception err) {

            Core.get().showMessage("Unable to get getItemAuditPerBarcode : " + err.getMessage(), context, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    //Audit Combined
    public void deleteItemAuditCombined(Context context) {

        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            String sql = "Delete from AuditCombined";
            db.execSQL(sql);

        } catch (Exception err) {

            Core.get().showMessage("Unable to do deleteAuditCombined", context, TAG);

        } finally {
            if (db != null) {
                db.close();
            }
        }


    }

    public void deleteItemAuditNotNew(Context context) {

        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            String sql = "Delete from AuditCombined where status != 'New'";
            db.execSQL(sql);

        } catch (Exception err) {

            Core.get().showMessage("Unable to do deleteItemAuditCombined", context, TAG);

        } finally {
            if (db != null) {
                db.close();
            }
        }


    }

    public ArrayList<AuditCombinedData> getAuditCombinedData(Context context, int vehicleId, int checklistId) {

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            ArrayList<AuditCombinedData> lstProducts = new ArrayList<AuditCombinedData>();
            db = this.getWritableDatabase();
            String sql = "SELECT i.name,i.TestDueDate,i.TestBy,ia.Note,ia.Condition,ia.Date,i.BarCode,i.VehicleId,i.ItemId,ia.ChecklistId,ia.Condition2 " +
                    " FROM Item i left join ItemAudit ia on ia.BarCode = i.BarCode " +
                    " where i.VehicleId = " + vehicleId + " and (ia.checklistId = " + checklistId + " or ia.checklistId is null) order by ia.Condition desc,i.name";
            Core.get().logMessage(sql, TAG);
            cursor = db.rawQuery(sql, null);

            Date date = new Date();

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    //String name, String testDueDate, String testBy, String note, String condition, String date, String barcode, int vehicleId, int itemCloudId
                    AuditCombinedData product = new AuditCombinedData(
                            cursor.getString(0),   //Name
                            cursor.getString(1),   //Test Due Date
                            cursor.getString(2),   //Test By
                            cursor.getString(3),   //Note
                            cursor.getString(4),   //Condition
                            cursor.getString(5),   //Date
                            cursor.getString(6),   //BarCode
                            cursor.getInt(7),      //VehicleId
                            cursor.getInt(8),     //ItemCloudId
                            cursor.getInt(9),   //ChecklistId
                            cursor.getString(10)); //Condition2
                    lstProducts.add(product);
                } while (cursor.moveToNext());

                return lstProducts;
            }

        } catch (Exception err) {

            Core.get().showMessage("Unable to get getAuditCombinedData : " + err.getMessage(), context, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    public ArrayList<AuditCombinedData> getAuditCombinedForWrongVehicleData(Context context, int vehicleId, int checklistId) {

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            ArrayList<AuditCombinedData> lstProducts = new ArrayList<AuditCombinedData>();
            db = this.getWritableDatabase();
            String sql = "SELECT DISTINCT i.name,i.TestDueDate,i.TestBy,ia.Note,ia.Condition,ia.Date,i.BarCode,i.VehicleId,i.ItemId,ia.ChecklistId,ia.Condition2 " +
                    " FROM Item i inner join ItemAudit ia on ia.BarCode = i.BarCode and i.ItemId = ai.ItemId " +
                    " where ia.FoundOnVehicleId = " + vehicleId + " and ia.ChecklistId = " + checklistId;
            Core.get().logMessage(sql, TAG);
            cursor = db.rawQuery(sql, null);

            Date date = new Date();

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    //String name, String testDueDate, String testBy, String note, String condition, String date, String barcode, int vehicleId, int itemCloudId
                    AuditCombinedData product = new AuditCombinedData(
                            cursor.getString(0),   //Name
                            cursor.getString(1),   //Test Due Date
                            cursor.getString(2),   //Test By
                            cursor.getString(3),   //Note
                            cursor.getString(4),   //Condition
                            cursor.getString(5),   //Date
                            cursor.getString(6),   //BarCode
                            cursor.getInt(7),      //VehicleId
                            cursor.getInt(8),     //ItemCloudId
                            cursor.getInt(9),   //ChecklistId
                            cursor.getString(10)); //Condition2
                    lstProducts.add(product);
                } while (cursor.moveToNext());

                return lstProducts;
            }

        } catch (Exception err) {

            Core.get().showMessage("Unable to get getAuditCombinedData : " + err.getMessage(), context, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    /*Other*/
    public ItemAndItemAudit getItemAndAuditItem(Context context, String barcode, int checklistId) {

        SQLiteDatabase db = null;
        Cursor cursor = null;
        ItemAndItemAudit item;

        try {
            db = getWritableDatabase();

            String sql = "SELECT i.Name,ia.Date,ia.Condition,ia.Note,i.RequiresInspection,i.Barcode,i.VehicleId,i.LastScannedDate," +
                    " i.ItemId,i.Rego,i.ReportNumber,i.TestBy,i.TestDueDate,i.EndOfLifeDate,ia.Condition2 " +
                    " FROM Item i left join ItemAudit ia on ia.Barcode = i.Barcode " +
                    " where i.Barcode =  '" + barcode + "' and (ia.checklistId = " + checklistId + " or ia.checklistId is null)";

            cursor = db.rawQuery(sql, null);
            int cnt = 0;

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    //name,date,condition,note,requiresInspection,barcode, int vehicleId, String lastScannedDate, int itemCloudId, String rego, String reportNumber, String testBy, String testDueDate, String endOfLifeDate
                    item = new ItemAndItemAudit(
                            cursor.getString(0),  //Name
                            cursor.getString(1),  //Date
                            cursor.getString(2),  //Condition
                            cursor.getString(3),  //Note
                            cursor.getString(4),  //RequiresInspection
                            cursor.getString(5),  //Barcode
                            cursor.getInt(6),     //VehicleId
                            cursor.getString(7),  //LastScannedDate
                            cursor.getInt(8),     //ItemId
                            cursor.getString(9),  //Rego
                            cursor.getString(10), //ReportNumber
                            cursor.getString(11), //TestBy
                            cursor.getString(12), //TestDueDate
                            cursor.getString(13), //EndOfLifeDate
                            cursor.getString(14));//Condition2

                    cnt++;
                } while (cursor.moveToNext());

                return item;
            } else {
                return null;
            }

        } catch (Exception err) {

            Core.get().showMessage("Unable to extract getItemAndAuditItem Message: " + err.getMessage(), context, TAG);

        } finally {

            if (db != null) {
                db.close();
            }

            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }


}
