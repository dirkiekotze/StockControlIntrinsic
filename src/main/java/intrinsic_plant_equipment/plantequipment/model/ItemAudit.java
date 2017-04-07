package intrinsic_plant_equipment.plantequipment.model;

/**
 * Created by Dirk on 8/03/2017.
 */

public class ItemAudit {

    //id INTEGER PRIMARY KEY,CloudId,VehicleId,ChecklistId,Date,Condition,Note,Barcode
    int ItemId;
    int VehicleId;
    int ChecklistId;
    String Date;
    String Condition;
    String Note;
    String Barcode;
    String Status;
    int Id;
    String Condition2;
    int FoundOnVehicleId;

    public ItemAudit(int itemId, String barcode, String note, String condition, String date, int checklistId, int vehicleId, String status,int id,String condition2,int foundOnVehicleId) {
        ItemId = itemId;
        Barcode = barcode;
        Note = note;
        Condition = condition;
        Date = date;
        ChecklistId = checklistId;
        VehicleId = vehicleId;
        Status = status;
        Id = id;
        Condition2 = condition2;
        FoundOnVehicleId = foundOnVehicleId;
    }

    public int getFoundOnVehicleId() {
        return FoundOnVehicleId;
    }

    public String getCondition2() {
        return Condition2;
    }

    public int getId() {
        return Id;
    }

    public String getStatus() {
        return Status;
    }

    public int getItemId() {
        return ItemId;
    }

    public String getBarcode() {
        return Barcode;
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

    public int getChecklistId() {
        return ChecklistId;
    }

    public int getVehicleId() {
        return VehicleId;
    }
}
