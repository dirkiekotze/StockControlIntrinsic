package intrinsic_plant_equipment.plantequipment.model;

/**
 * Created by Dirk on 8/03/2017.
 */

public class LoginClass {

    String Username;
    String Password;


    public LoginClass(String username, String password) {
        Username = username;
        Password = password;

    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }
}
