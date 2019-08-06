package com.example.ball;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class IconAdapter extends BaseAdapter {

    private final int icons[] = new int[]{R.mipmap.ic_launcher_round, R.drawable.ico2};
    private final String[] hints = new String[]{"Default Icon", "New Icon"};

    private Context context;

    public IconAdapter(Context context){
        this.context = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = View.inflate(context, R.layout.icon_choice, null);
        ((ImageView) convertView.findViewById(R.id.icon_pos)).setImageResource(icons[position]);
        ((TextView) convertView.findViewById(R.id.hint_pos)).setText(hints[position]);
        return convertView;

        /*
        convertView = View.inflate(context, R.layout.set_list_item, null);
        int selected = selections[position];
        String item = set_item_names[position],
                hint = hint_info_names[position],
                hint_pointer = position == 0? play_forms[selected] : colors[selected];
        ((TextView) convertView.findViewById(R.id.set_item)).setText(item);
        ((TextView) convertView.findViewById(R.id.hint_info)).setText(hint + hint_pointer);
        //((TextView) convertView.findViewById(R.id.set_item)).setText(item);
        return convertView;*/
    }

}
