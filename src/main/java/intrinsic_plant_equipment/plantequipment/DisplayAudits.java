package intrinsic_plant_equipment.plantequipment;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import intrinsic_plant_equipment.plantequipment.helper.Core;
import intrinsic_plant_equipment.plantequipment.helper.IEquipmentPreferences;
import intrinsic_plant_equipment.plantequipment.model.AuditCombinedData;
import intrinsic_plant_equipment.plantequipment.model.ItemAudit;

/**
 * Created by Dirk on 13/03/2017.
 */

public class DisplayAudits extends BaseAdapter {

    String TAG = DisplayAudits.class.getSimpleName();
    private Activity activity;
    private List<AuditCombinedData> AuditCombinedDataLst;
    protected IEquipmentPreferences mPreferences;
    TextView auditType;
    TextView auditCondition;
    ImageView auditChecked;

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public DisplayAudits(List<AuditCombinedData> auditList, Activity activity) {
        super();
        this.AuditCombinedDataLst = auditList;
        this.activity = activity;
        mPreferences = Core.get().getPreferences();

    }

    @Override
    public int getCount() {

        return AuditCombinedDataLst.size();
    }

    @Override
    public Object getItem(int position) {

        return AuditCombinedDataLst.get(position);

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try{
            if (convertView == null) {
                LayoutInflater inflater = activity.getLayoutInflater();
                convertView = inflater.inflate(R.layout.audit_item_row, null);
            }

            if(AuditCombinedDataLst != null){
                auditType = (TextView) convertView.findViewById(R.id.auditType);
                auditCondition = (TextView) convertView.findViewById(R.id.auditCondition);
                auditChecked = (ImageView) convertView.findViewById(R.id.auditChecked);

                auditType.setText(AuditCombinedDataLst.get(position).getName());
                auditCondition.setText(AuditCombinedDataLst.get(position).getCondition() == null ? "Not Scanned" : AuditCombinedDataLst.get(position).getCondition());

                //Image Display
                if((AuditCombinedDataLst.get(position).getCondition() == null) || (AuditCombinedDataLst.get(position).getCondition().toLowerCase().equals("not scanned"))){
                    auditChecked.setImageResource(R.drawable.crossincircle);
                }
                else if (AuditCombinedDataLst.get(position).getCondition().toLowerCase().equals("scanned")) {
                    auditChecked.setImageResource(R.drawable.checkincircle);

                }
            }

            return convertView;
        }
        catch(Exception err){

            Core.get().showMessage("Unable to create DisplayAdapter Message : " + err.getMessage(),activity, TAG);
            return null;
        }


    }
}
