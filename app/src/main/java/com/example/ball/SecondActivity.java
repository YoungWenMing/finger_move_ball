package com.example.ball;

import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class SecondActivity extends AppCompatActivity {

    private MyBaseAdapter adapter;
    private ListView listView;
    private SeekBar sizeSetBar;
    private TextView sizeSetTv;

    private static String[] colors = KeysName.colors,
            play_forms = KeysName.play_forms,
            titles = KeysName.titles;

    private String BallColor = KeysName.BallColor,
            PlayForm = KeysName.PlayForm,
            Radius = KeysName.Radius;


    /* Intent that activate this activity */
    private Intent intent;
    private HashMap<Integer, Integer> colorStrings;
    private int color, radius, playForm, selections[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        init();
        getExtraInfo();

        sizeSetTv.setText(Integer.toString( radius));
        handleSeekBar(radius);

        adapter = new MyBaseAdapter(this, selections);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final String[] items = position == 0? play_forms: colors;

                String title = titles[position];
                /*default checkedItem must be adaptive*/

                AlertDialog.Builder builder = new AlertDialog.Builder(SecondActivity.this);
                builder.setTitle(title).setSingleChoiceItems(items, selections[position], new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selections[position] = which;
                        playForm = selections[0];
                        color = selections[1];
                        adapter.notifyDataSetChanged();
                        //Toast.makeText(SecondActivity.this, "you picked " + items[which], Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", null).show();
            }
        });


        Looper.prepare();

        Button button = (Button) findViewById(R.id.Back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putInt(BallColor, color);
                data.putInt(PlayForm, playForm);
                data.putInt(Radius, radius);
                intent.putExtras(data);

                SecondActivity.this.setResult(0, intent);
                SecondActivity.this.finish();
            }
        });


    }
/*
    @Override
    public void onPrepareNavigateUpTaskStack(TaskStackBuilder builder) {
        super.onPrepareNavigateUpTaskStack(builder);
    }

    @Override
    public boolean navigateUpTo(Intent upIntent) {
        return super.navigateUpTo(upIntent);
    }

    @Override
    public boolean onNavigateUp() {
        Bundle data = new Bundle();
        data.putInt(BallColor, color);
        data.putInt(PlayForm, playForm);
        data.putInt(Radius, radius);
        intent.putExtras(data);
        SecondActivity.this.setResult(0, intent);
        navigateUpTo(intent);
        SecondActivity.this.finish();
        return super.onNavigateUp();
    }
*/
    @Override
    public void onBackPressed() {
        Bundle data = new Bundle();
        data.putInt(BallColor, color);
        data.putInt(PlayForm, playForm);
        data.putInt(Radius, radius);
        intent.putExtras(data);
        SecondActivity.this.setResult(0, intent);
        super.onBackPressed();
    }

    @Override
    protected void onPause() {

        /*
        intent.putExtra("ballcolor", color);
        intent.putExtra("play_form", playForm);
        intent.putExtra("radius", radius);

        */

        super.onPause();
    }


    private void getExtraInfo(){
        intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        color = colorStrings.get( bundle.getInt(BallColor, KeysName.colorFirst));
        radius = bundle.getInt(Radius, KeysName.radiusFirst);
        playForm = bundle.getInt(PlayForm, KeysName.playForm);
        //color = intent.getIntExtra("color", Color.WHITE);
        //radius = intent.getIntExtra("radius", 20);
        //playForm = intent.getIntExtra("play_form", 0);
        selections = new int[]{ playForm, color};
    }

    private void init(){
        colorStrings = new HashMap<>();
        colorStrings.put(Color.WHITE, 0);
        colorStrings.put(Color.GREEN, 1);
        colorStrings.put(Color.RED, 2);
        colorStrings.put(Color.BLUE, 3);
        viewInit();
    }

    private void viewInit(){
        listView = (ListView) findViewById(R.id.settingListView);
        sizeSetBar = (SeekBar) findViewById(R.id.size_set_bar);
        sizeSetTv = (TextView) findViewById(R.id.size_value_tv);
    }


    private void handleSeekBar(int initialValue){
        sizeSetBar.setProgress(initialValue);
        sizeSetBar.setOnSeekBarChangeListener(seekBarChangeListener);
    }


    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            SecondActivity.this.radius = progress;
            sizeSetTv.setText(Integer.toString(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //Log.i("MainActivity","onStartTrackingTouch");
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //Log.i("MainActivity","onStopTrackingTouch");
        }
    };
}
