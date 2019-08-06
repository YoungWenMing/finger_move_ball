package com.example.ball;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyBaseAdapter extends BaseAdapter {

    private Context context;
    private int[] selections;
    private static String[]
            colors = new String[]{"白色", "绿色","红色", "蓝色"},
            play_forms = new String[]{"重力感应", "手势操作"},
            set_item_names = new String[]{"操作设置", "颜色设置"},
            hint_info_names = new String[]{"您选择的操作方式是", "您选择的颜色是"};

    public MyBaseAdapter(Context context, int[] selections){
        this.context = context;
        this.selections = selections;
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
        convertView = View.inflate(context, R.layout.set_list_item, null);
        int selected = selections[position];
        String item = set_item_names[position],
                hint = hint_info_names[position],
                hint_pointer = position == 0? play_forms[selected] : colors[selected];
        ((TextView) convertView.findViewById(R.id.set_item)).setText(item);
        ((TextView) convertView.findViewById(R.id.hint_info)).setText(hint + hint_pointer);
        //((TextView) convertView.findViewById(R.id.set_item)).setText(item);
        return convertView;
    }
}
