package intrinsic_plant_equipment.plantequipment.model;

/**
 * Created by Dirk on 13/03/2017.
 */

public class AuditCombinedData {

    String Name;
    int ItemId;
    int VehicleId;
    String Barcode;
    String Date;
    String Condition;
    String Note;
    String TestBy;
    String TestDueDate;
    int ChecklistId;
    String Condition2;


    public AuditCombinedData(String name, String testDueDate, String testBy, String note, String condition, String date, String barcode, int vehicleId, int itemId, int checklistId,String condition2) {
        Name = name;
        TestDueDate = testDueDate;
        TestBy = testBy;
        Note = note;
        Condition = condition;
        Date = date;
        Barcode = barcode;
        VehicleId = vehicleId;
        ItemId = itemId;
        ChecklistId = checklistId;
        Condition2 = condition2;
    }

    public String getCondition2() {
        return Condition2;
    }

    public int getChecklistId() {
        return ChecklistId;
    }

    public String getName() {
        return Name;
    }

    public String getTestDueDate() {
        return TestDueDate;
    }

    public String getTestBy() {
        return TestBy;
    }

    public String getNote() {
        return Note;
    }

    public String getCondition() {
        return Condition;
    }

    public String getDate() {
        return Date;
    }

    public String getBarcode() {
        return Barcode;
    }

    public int getVehicleId() {
        return VehicleId;
    }

    public int getItemId() {
        return ItemId;
    }
}

