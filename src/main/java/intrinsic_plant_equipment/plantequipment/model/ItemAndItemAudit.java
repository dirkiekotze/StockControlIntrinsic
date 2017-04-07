package intrinsic_plant_equipment.plantequipment.model;

/**
 * Created by Dirk on 14/03/2017.
 */

public class ItemAndItemAudit {
    String Name;
    String EndOfLifeDate;
    String TestDueDate;
    String TestBy;
    String ReportNumber;
    String Rego;
    int ItemId;
    String LastScannedDate;
    int VehicleId;
    String Barcode;
    String RequiresInspection;
    String Note;
    String Condition;
    String Date;
    String Condition2;

    public ItemAndItemAudit(String name, String date, String condition, String note, String requiresInspection, String barcode, int vehicleId, String lastScannedDate, int itemId, String rego, String reportNumber, String testBy, String testDueDate, String endOfLifeDate,String condition2) {
        Name = name;
        Date = date;
        Condition = condition;
        Note = note;
        RequiresInspection = requiresInspection;
        Barcode = barcode;
        VehicleId = vehicleId;
        LastScannedDate = lastScannedDate;
        ItemId = itemId;
        Rego = rego;
        ReportNumber = reportNumber;
        TestBy = testBy;
        TestDueDate = testDueDate;
        EndOfLifeDate = endOfLifeDate;
        Condition2 = condition2;
    }

    public String getCondition2() {
        return Condition2;
    }

    public String getName() {
        return Name;
    }

    public String getDate() {
        return Date;
    }

    public String getCondition() {
        return Condition;
    }

    public String getNote() {
        return Note;
    }

    public String getRequiresInspection() {
        return RequiresInspection;
    }

    public String getBarcode() {
        return Barcode;
    }

    public int getVehicleId() {
        return VehicleId;
    }

    public String getLastScannedDate() {
        return LastScannedDate;
    }

    public int getItemId() {
        return ItemId;
    }

    public String getRego() {
        return Rego;
    }

    public String getReportNumber() {
        return ReportNumber;
    }

    public String getTestBy() {
        return TestBy;
    }

    public String getTestDueDate() {
        return TestDueDate;
    }

    public String getEndOfLifeDate() {
        return EndOfLifeDate;
    }
}
