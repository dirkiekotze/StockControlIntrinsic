package intrinsic_plant_equipment.plantequipment.model;

/**
 * Created by Dirk on 10/04/2017.
 */

public class ChecklistAudit {
    int VehicleId;
    int ChecklistId;
    int Records;

    public ChecklistAudit(int vehicleId,int checklistId,int records) {
        VehicleId = vehicleId;
        Records = records;
        ChecklistId = checklistId;
    }

    public int getVehicleId() {
        return VehicleId;
    }

    public int getRecords() {
        return Records;
    }

    public int getChecklistId() {
        return ChecklistId;
    }
}
