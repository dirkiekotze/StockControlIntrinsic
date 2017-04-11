package intrinsic_plant_equipment.plantequipment.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Dirk on 8/03/2017.
 */

public class EquipmentPreferences implements IEquipmentPreferences {

    private final SharedPreferences mSharedPrefs;

    public EquipmentPreferences(final Context context) {
        this.mSharedPrefs = context.getSharedPreferences(Constants.DEFAULT_SETTINGS, Context.MODE_PRIVATE);
    }


    @Override
    public void setVehicleAuditExistId(int vehicleId) {
        final SharedPreferences.Editor edit = mSharedPrefs.edit();
        edit.putInt(Constants.VEHICLE_AUDIT_ID, vehicleId);
        edit.commit();
    }

    @Override
    public int getVehicleAuditExistId() {
        return mSharedPrefs.getInt(Constants.VEHICLE_AUDIT_ID,0);
    }

    @Override
    public void setHadAuditRecords(int hadAuditRecords) {
        final SharedPreferences.Editor edit = mSharedPrefs.edit();
        edit.putInt(Constants.HAD_PREVIOUS_AUDIT_RECORDS, hadAuditRecords);
        edit.commit();
    }

    @Override
    public int getHadAuditRecords() {
        return mSharedPrefs.getInt(Constants.HAD_PREVIOUS_AUDIT_RECORDS,0);
    }

    @Override
    public void setUserId(int userId) {
        final SharedPreferences.Editor edit = mSharedPrefs.edit();
        edit.putInt(Constants.USER_ID, userId);
        edit.commit();
    }

    @Override
    public int getUserId() {
        return mSharedPrefs.getInt(Constants.USER_ID,0);
    }


    @Override
    public void setVehicleId(int vehicleId) {
        final SharedPreferences.Editor edit = mSharedPrefs.edit();
        edit.putInt(Constants.VEHICLE_ID, vehicleId);
        edit.commit();
    }

    @Override
    public int getVehicleId() {
        return mSharedPrefs.getInt(Constants.VEHICLE_ID,0);
    }

    @Override
    public void setChecklistId(int checklistId) {
        final SharedPreferences.Editor edit = mSharedPrefs.edit();
        edit.putInt(Constants.CHECKLIST_ID, checklistId);
        edit.commit();
    }

    @Override
    public int getChecklistId() {
        return mSharedPrefs.getInt(Constants.CHECKLIST_ID,0);
    }

    @Override
    public void setAccessLevel(String accessLevel) {
        final SharedPreferences.Editor edit = mSharedPrefs.edit();
        edit.putString(Constants.ACCESS_LEVEL, accessLevel);
        edit.commit();
    }

    @Override
    public String getAccessLevel() {
        return mSharedPrefs.getString(Constants.ACCESS_LEVEL, "");
    }

    @Override
    public void setVehicleRego(String rego) {
        final SharedPreferences.Editor edit = mSharedPrefs.edit();
        edit.putString(Constants.REGO, rego);
        edit.commit();
    }

    @Override
    public String getVehicleRego() {
        return mSharedPrefs.getString(Constants.REGO, "");
    }

    @Override
    public void setChecklistName(String checklistName) {
        final SharedPreferences.Editor edit = mSharedPrefs.edit();
        edit.putString(Constants.CHECKLIST, checklistName);
        edit.commit();
    }

    @Override
    public String getChecklistName() {
        return mSharedPrefs.getString(Constants.CHECKLIST, "");
    }

    @Override
    public void setChecklistDate(String checklistDate) {
        final SharedPreferences.Editor edit = mSharedPrefs.edit();
        edit.putString(Constants.CHECKLIST_DATE, checklistDate);
        edit.commit();
    }

    @Override
    public String getChecklistDate() {
        return mSharedPrefs.getString(Constants.CHECKLIST_DATE, "");
    }
}
