package intrinsic_plant_equipment.plantequipment.model;

/**
 * Created by Dirk on 8/03/2017.
 */

public class Vehicle {

    //id INTEGER PRIMARY KEY,CloudId,Rego
    int VehicleId;
    String Rego;
    int Id;
    String Type;
    int ServiceDueKlms;
    String ServicePlantDate;
    String PlantElecTestDate;
    int CurrentKms;


    public Vehicle(int vehicleId, String rego,int id,String type,int serviceDueKlms,String servicePlantDate,String plantElecTestDate,int currentKms) {
        VehicleId = vehicleId;
        Rego = rego;
        Id = id;
        Type = type;
        ServicePlantDate = servicePlantDate;
        PlantElecTestDate = plantElecTestDate;
        CurrentKms = currentKms;
        ServiceDueKlms = serviceDueKlms;

    }

    public int getCurrentKms() {
        return CurrentKms;
    }

    public String getType() {
        return Type;
    }

    public String getPlantElecTestDate() {
        return PlantElecTestDate;
    }

    public String getServicePlantDate() {
        return ServicePlantDate;
    }

    public int getServiceDueKlms() {
        return ServiceDueKlms;
    }

    public int getId() {
        return Id;
    }

    public String toString() {
        return Rego;
    }

    public int getVehicleId() {
        return VehicleId;
    }

    public String getRego() {
        return Rego;
    }
}
