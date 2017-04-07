package intrinsic_plant_equipment.plantequipment.model;

/**
 * Created by Dirk on 8/03/2017.
 */

public class Item {

    //id INTEGER PRIMARY KEY,ItemCloudId,ItemTypeId,VehicleId,LastTestStatus,TestBy,ReportNumber,LastVehicleId,Barcode,Name,Rego,Status,RequiresInspection,LastScannedDate,TestDueDate,EndOfLifeDate)");

    int ItemId;
    int ItemTypeId;
    int VehicleId;
    String LastTestStatus;
    String TestBy;
    String ReportNumber;
    int LastVehicleId;
    String Barcode;
    String Name;
    String Rego;
    String Status;
    String RequiresInspection;
    String LastScannedDate;
    String TestDueDate;
    String EndOfLifeDate;
    String Condition2;
    int LastVehicleLocation;

    public Item(int itemId,int itemTypeId,int vehicleId,String lastTestStatus,String testBy,String reportNumber,int lastVehicleId,String barcode,String name,String rego,
            String status,String requiresInspection,String lastScannedDate,String testDueDate,String endOfLifeDate,String condition2,int lastVehicleLocation) {
        ItemId = itemId;
        EndOfLifeDate = endOfLifeDate;
        TestDueDate = testDueDate;
        LastScannedDate = lastScannedDate;
        RequiresInspection = requiresInspection;
        Status = status;
        Rego = rego;
        Name = name;
        Barcode = barcode;
        LastVehicleId = lastVehicleId;
        ReportNumber = reportNumber;
        TestBy = testBy;
        LastTestStatus = lastTestStatus;
        VehicleId = vehicleId;
        ItemTypeId = itemTypeId;
        Condition2 = condition2;
        LastVehicleLocation = lastVehicleLocation;
    }

    public int getLastVehicleLocation() {
        return LastVehicleLocation;
    }

    public String getCondition2() {
        return Condition2;
    }

    public String toString() {
        return Name;
    }

    public int getItemId() {
        return ItemId;
    }

    public String getEndOfLifeDate() {
        return EndOfLifeDate;
    }

    public String getLastScannedDate() {
        return LastScannedDate;
    }

    public String getTestDueDate() {
        return TestDueDate;
    }

    public String getRequiresInspection() {
        return RequiresInspection;
    }

    public String getStatus() {
        return Status;
    }

    public String getRego() {
        return Rego;
    }

    public String getName() {
        return Name;
    }

    public String getBarcode() {
        return Barcode;
    }

    public int getLastVehicleId() {
        return LastVehicleId;
    }

    public String getReportNumber() {
        return ReportNumber;
    }

    public String getTestBy() {
        return TestBy;
    }

    public String getLastTestStatus() {
        return LastTestStatus;
    }

    public int getVehicleId() {
        return VehicleId;
    }

    public int getItemTypeId() {
        return ItemTypeId;
    }
}
