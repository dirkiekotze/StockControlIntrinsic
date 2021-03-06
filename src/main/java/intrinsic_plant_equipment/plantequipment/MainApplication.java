package intrinsic_plant_equipment.plantequipment;

import android.app.Application;
import android.content.Context;
import org.acra.*;
import org.acra.annotation.*;


/**
 * Created by Dirk on 8/03/2017.
 */

@ReportsCrashes(formUri = "https://collector.tracepot.com/333c6b30",
        mode = ReportingInteractionMode.DIALOG,
        resToastText = R.string.crash_toast_text, // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
        resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. When defined, adds a user text field input with this text resource as a label
        resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.

)

public class MainApplication extends Application {

    private static final String TAG = MainApplication.class.getSimpleName();
    //private CrashLogServiceSender crashLogServiceSender;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }
}
