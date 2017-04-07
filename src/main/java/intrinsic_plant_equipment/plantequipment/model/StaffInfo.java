package intrinsic_plant_equipment.plantequipment.model;

/**
 * Created by Dirk on 31/03/2017.
 */

public class StaffInfo {
    String AccessLevel;
    int StaffId;

    public StaffInfo(String accessLevel, int staffId) {
        AccessLevel = accessLevel;
        StaffId = staffId;
    }

    public String getAccessLevel() {
        return AccessLevel;
    }

    public int getStaffId() {
        return StaffId;
    }
}
