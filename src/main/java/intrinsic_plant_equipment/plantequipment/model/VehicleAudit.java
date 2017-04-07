package intrinsic_plant_equipment.plantequipment.model;

/**
 * Created by Dirk on 31/03/2017.
 */

public class VehicleAudit {

    int VehicleAuditId;
    int VehicleId;
    int UserId;
    String Rego;
    int ChecklistId;
    String DateStamp;
    int Kilometers;

    public VehicleAudit(int vehicleAuditId,int vehicleId,int userId,int checklistId,int kilometers,  String rego, String dateStamp) {
        Kilometers = kilometers;
        DateStamp = dateStamp;
        ChecklistId = checklistId;
        Rego = rego;
        UserId = userId;
        VehicleId = vehicleId;
        VehicleAuditId = vehicleAuditId;
    }

    public int getVehicleAuditId() {
        return VehicleAuditId;
    }

    public int getKilometers() {
        return Kilometers;
    }

    public int getChecklistId() {
        return ChecklistId;
    }

    public String getDateStamp() {
        return DateStamp;
    }

    public String getRego() {
        return Rego;
    }

    public int getUserId() {
        return UserId;
    }

    public int getVehicleId() {
        return VehicleId;
    }
}
