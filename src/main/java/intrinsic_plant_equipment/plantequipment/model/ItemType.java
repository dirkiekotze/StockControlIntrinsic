package intrinsic_plant_equipment.plantequipment.model;

/**
 * Created by Dirk on 8/03/2017.
 */

public class ItemType {

    //id INTEGER PRIMARY KEY,ItemTypeId,CategoryId,Name,InspectionInterval,InspectionRequired
    int ItemTypeId;
    int CategoryId;
    int InspectionInterval;
    int InspectionRequired;
    String Name;

    public ItemType(int itemTypeId, int inspectionRequired, int inspectionInterval, int categoryId,String name) {
        ItemTypeId = itemTypeId;
        InspectionRequired = inspectionRequired;
        InspectionInterval = inspectionInterval;
        CategoryId = categoryId;
        Name = name;
    }

    public String toString() {
        return Name;
    }

    public String getName() {
        return Name;
    }

    public int getItemTypeId() {
        return ItemTypeId;
    }

    public int getInspectionRequired() {
        return InspectionRequired;
    }

    public int getInspectionInterval() {
        return InspectionInterval;
    }

    public int getCategoryId() {
        return CategoryId;
    }
}
