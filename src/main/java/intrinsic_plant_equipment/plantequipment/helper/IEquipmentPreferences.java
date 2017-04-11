package intrinsic_plant_equipment.plantequipment.helper;

/**
 * Created by Dirk on 8/03/2017.
 */

public interface IEquipmentPreferences  {

    void setVehicleAuditExistId(int vehicleId);

    int getVehicleAuditExistId();

    void setHadAuditRecords(int hadAuditRecords);

    int getHadAuditRecords();

    void setUserId(int userId);

    int getUserId();

    void setVehicleId(int vehicleId);

    int getVehicleId();

    void setChecklistId(int checklistId);

    int getChecklistId();

    void setAccessLevel(String accessLevel);

    String getAccessLevel();

    void setVehicleRego(String rego);

    String getVehicleRego();

    void setChecklistName(String checklistName);

    String getChecklistName();

    void setChecklistDate(String checklistDate);

    String getChecklistDate();

}
