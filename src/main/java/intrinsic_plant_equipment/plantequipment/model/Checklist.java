package intrinsic_plant_equipment.plantequipment.model;

/**
 * Created by Dirk on 8/03/2017.
 */

public class Checklist {

    //id INTEGER PRIMARY KEY,CloudId,Name,ListDate
    int Id;
    int CloudId;
    String Name;
    String CheckListDate;

    public Checklist(int cloudId, String date, String name,int id) {
        CloudId = cloudId;
        CheckListDate = date;
        Name = name;
        Id = id;
    }

    public int getId() {
        return Id;
    }

    public String toString() {
        return Name;
    }

    public int getCloudId() {
        return CloudId;
    }

    public String getListDate() {
        return CheckListDate;
    }

    public String getName() {
        return Name;
    }
}
