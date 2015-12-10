package in.appsplanet.wedsource.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import in.appsplanet.wedsource.R;
import in.appsplanet.wedsource.pojo.Promotion;

/**
 * Created by root on 7/11/15.
 */
public class PromotionAdapter extends ArrayAdapter<Promotion> {
    private final Context context;
    private final ArrayList<Promotion> values;

    public PromotionAdapter(Context context, ArrayList<Promotion> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_promotion_item, parent, false);
        TextView name = (TextView) rowView.findViewById(R.id.txtName);
        TextView desc = (TextView) rowView.findViewById(R.id.txtDescription);

        Promotion promotion = values.get(position);
        name.setText(promotion.getName());
        desc.setText(promotion.getDescription());
        return rowView;
    }
}
