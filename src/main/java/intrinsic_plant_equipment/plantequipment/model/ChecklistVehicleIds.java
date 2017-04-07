package intrinsic_plant_equipment.plantequipment.model;

/**
 * Created by Dirk on 9/03/2017.
 */

public class ChecklistVehicleIds {
    int ChecklistId;
    int VehicleId;

    public ChecklistVehicleIds(int checklistId, int vehicleId) {
        ChecklistId = checklistId;
        VehicleId = vehicleId;
    }

    public int getChecklistId() {
        return ChecklistId;
    }

    public int getVehicleId() {
        return VehicleId;
    }
}
