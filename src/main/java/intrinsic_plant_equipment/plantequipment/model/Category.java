package intrinsic_plant_equipment.plantequipment.model;

/**
 * Created by Dirk on 8/03/2017.
 */

public class Category {

    //id INTEGER PRIMARY KEY,CloudCategoryId,Name,Type
    int CloudCategoryId;
    String Name;
    String Type;
    int Id;

    public Category(int cloudCategoryId, String type, String name,int id) {
        CloudCategoryId = cloudCategoryId;
        Type = type;
        Name = name;
        Id = id;
    }

    public String toString() {
        return Name;
    }

    public int getId() {
        return Id;
    }

    public int getCloudCategoryId() {
        return CloudCategoryId;
    }

    public String getType() {
        return Type;
    }

    public String getName() {
        return Name;
    }
}
