package com.example.ball;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



/*
*
*
* written by Yang Wenming on May 26, 2019
* */
public class MainActivity extends AppCompatActivity {

    public static final String  X_Position = KeysName.X_Position,
                                Y_Position = KeysName.Y_Position,
                                BallColor = KeysName.BallColor,
                                Radius = KeysName.Radius,
                                CreateTimes = KeysName.CreateTimes,
                                PlayForm = KeysName.PlayForm,
                                EnterTimeString = KeysName.EnterTime;

    private final int FEEL_GRAVITY_MODE = KeysName.FEEL_GRAVITY,
                        FINGER_MOVE_MODE = KeysName.FINGER_MOVE,
                        SETTING_MSG_CODE = KeysName.SETTING_MSG_CODE,
                        GMOVE_MSG_CODE = KeysName.GMOVE_MSG_CODE;

    private int width, height, statudBarHeight, tipsHeight = KeysName.TIPS_HEIGHT, tipsTextSize = KeysName.TIPS_TEXTSIZE;
    private SharedPreferences preferences;

    private int xFirst, yFirst, colorFirst, radiusFirst, createTimes, playForm;
    private final int[] colorCodes = {Color.WHITE, Color.GREEN, Color.RED, Color.BLUE};
    private BallView ball;
    private float gX, gY;

    // sensor stuff
    private SensorManager mSensorManager = null;
    private Sensor accelaroSensor = null;
    private SensorEventListener sensorListener = null;

    private IconSelector iconSelector = null;
    private boolean iconSelectedFlag = false;
    // screen mode
    private boolean firstEnter = true;

    private GMoveThread gMoveThread;
    private Handler gHandler;
    private Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == SETTING_MSG_CODE){
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);

                Bundle bundle = new Bundle();
                bundle.putInt(PlayForm, playForm);
                bundle.putInt(BallColor, colorFirst);
                bundle.putInt(Radius, radiusFirst);


                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
            }else if (msg.what == GMOVE_MSG_CODE){
                float xy[] = (float[]) msg.obj;
                ball.onGravityMove(xy[0], xy[1]);
            }
            super.handleMessage(msg);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setPreferences();
        getScreenParams();

        ball = new BallView(this);
        //setContentView(ball);
    }

    @Override
    protected void onResume() {
        super.onResume();

        selectIcon();
        resetScreen();
        if (playForm == FEEL_GRAVITY_MODE) {
            registerSensor();
            beginGMoveCalculation();
        }
    }


    /*
    * when under the gravity mode, the sensor Listener should be unregistered
    * and the calculating thread will be interrupted and thus be terminated
    * */
    @Override
    protected void onPause() {
        super.onPause();
        if (playForm == FEEL_GRAVITY_MODE){
            mSensorManager.unregisterListener(sensorListener);
            sensorListener = null;
            stopGMoveCalculation();
        }
    }

    /*
    * when exiting the application , current status must be stored permanently
    * with preference editor, such modification can be committed
    * */
    @Override
    protected void onStop() {

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(X_Position, xFirst);
        editor.putInt(Y_Position, yFirst);
        editor.putInt(BallColor, colorFirst);
        editor.putInt(Radius, radiusFirst);
        editor.putInt(CreateTimes, createTimes);
        editor.putInt(PlayForm, playForm);
        editor.apply();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Bundle bundle = data.getExtras();
        if (requestCode == 0 && resultCode == 0 && bundle != null){
            colorFirst = colorCodes[bundle.getInt(BallColor, 0)];
            radiusFirst = bundle.getInt(Radius, radiusFirst);
            playForm = bundle.getInt(PlayForm, playForm);
            ball.postSetting(colorFirst, radiusFirst);
        }
    }

    private void selectIcon(){
        if (createTimes > 1 || iconSelectedFlag)    return;
        iconSelector = new IconSelector(MainActivity.this);
        iconSelectedFlag = true;
        iconSelector.getBuilder().show();
    }

    private void getScreenParams(){
        WindowManager manager = this.getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);

        Resources resources = this.getResources();
        int statudBarId = resources.getIdentifier("status_bar_height", "dimen", "android");
        statudBarHeight = statudBarId == 0? 0: resources.getDimensionPixelSize(statudBarId);

        width = metrics.widthPixels;
        height = metrics.heightPixels - statudBarHeight - tipsHeight;// - navibarHeight;
    }

    /*
    * set the view group for the first time or other time respectively
    * */
    private void resetScreen(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.base_linear_layout);
        layout.removeAllViews();
        if (firstEnter){
            layout.addView(generateTips());
            firstEnter = false;
        }else {
            height += tipsHeight;
            tipsHeight = 0;
        }
        layout.addView(ball);
    }


    private TextView generateTips(){
        TextView view = new TextView(MainActivity.this);
        view.setText(EnterTimeString + createTimes);
        view.setBackgroundColor(Color.LTGRAY);
        view.setWidth(width);
        view.setHeight(tipsHeight);
        view.setTextSize(tipsTextSize);
        view.setPadding(0,0,0,0);
        return view;
    }

    /*
    * read initial values to set preferences of this ball-moving activity from
    * a sharedPreferences
    * */
    private void setPreferences(){
        preferences =  getSharedPreferences("ball_preferences", MODE_PRIVATE);

        xFirst = preferences.getInt(X_Position, KeysName.xFirst);
        yFirst = preferences.getInt(Y_Position, KeysName.yFirst);
        colorFirst = preferences.getInt(BallColor, KeysName.colorFirst);
        radiusFirst = preferences.getInt(Radius, KeysName.radiusFirst);
        playForm = preferences.getInt(PlayForm, KeysName.playForm);
        createTimes = preferences.getInt(CreateTimes, 0) + 1;
    }

    /*
    * register the acceleration sensor for this activity
    * */
    private void registerSensor(){
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelaroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gX = 0f;
        gY = 0f;
        sensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float Xt = - event.values[SensorManager.AXIS_X - 1];
                float Yt = event.values[SensorManager.AXIS_Y - 1];

                if (Math.abs(Xt - gX) > 0.005 || Math.abs(Yt - gY) > 0.005){
                    Message msg = new Message();
                    msg.what = GMOVE_MSG_CODE;
                    msg.obj = new float[]{gX = Xt, gY = Yt};
                    gHandler.sendMessage(msg);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        mSensorManager.registerListener(sensorListener, accelaroSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /*
    * open another thread to calculate the movement
    * caused by gravity*/
    private void beginGMoveCalculation(){
        gMoveThread = new GMoveThread(xFirst, yFirst, gX, gY, width, height, handler);
        gHandler = gMoveThread.gHandler;
        gMoveThread.start();
    }

    /*
    * interrupt the gMoveThread to stop the calculation when
    * this activity is paused
    * */
    private void stopGMoveCalculation(){
        gMoveThread.interrupt();
        gHandler = null;
        gMoveThread = null;
    }

    class BallView extends View {

        private int x = xFirst, y = yFirst;
        private Paint paint;
        private int radius = radiusFirst;
        private int paintColor = colorFirst;

        private boolean isMoved;

        // deal with long-press event
        private int lastX, lastY;
        private long lastEventTime;
        private static final int STAY_RANGE = 5;
        private static final long PRESS_LONG_THRESHOLD = 1000;

        public BallView(Context context) {
            super(context);

            paint = new Paint();
            // erase sawtooth
            paint.setAntiAlias(true);
            // set color
            paint.setColor(paintColor);

        }

        /*
        public BallView(Context context, int x, int y, int color, int radius) {
            super(context);
            this.x = x;
            this.y = y;
            this.paintColor = color;
            this.radius = radius;
            paint = new Paint();
            // erase sawtooth
            paint.setAntiAlias(true);
            // set color
            paint.setColor(paintColor);
        }*/


        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(Color.BLACK);
            //setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
            correctXY();
            updateXY();
            canvas.drawCircle(x, y, radius, paint);
        }


        /*
        * after we get result from the setting activity
        * the ball's color or radius may be changed
        * so update these two params and draw it again
        * */
        protected void postSetting(int colorNew, int radiusNew){
            radius = radiusNew;
            paintColor = colorNew;
            paint.setColor(paintColor);
            postInvalidate();
        }

        private void updateXY(){
            MainActivity.this.yFirst = y;
            MainActivity.this.xFirst = x;
        }

        private void correctXY(){
            if (x < radius)     x = radius;
            else if (x > width - radius)    x = width - radius;

            if (y < radius)     y = radius ;
            else if (y > height -  radius )   y = height - radius;
        }

        /*
        * receive new coordinates from result of calculation
        * */
        private void onGravityMove(float x, float y){
            int x0 = this.x, y0 = this.y;
            this.x = (int) x;
            this.y = (int) y;
            if (x0 != this.x || y0 != this.y)   postInvalidate();
        }

/*
        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            return super.dispatchTouchEvent(event);
        }
*/
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int  tempX = (int) event.getX(),
                tempY = (int) event.getY();
            long time = event.getEventTime();

            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    lastX = tempX;
                    lastY = tempY;
                    lastEventTime = time;
                    isMoved = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (! isMoved ){
                        // reset the moved flag
                        if (Math.abs(tempX - lastX) > STAY_RANGE ||
                                Math.abs(tempY - lastY) > STAY_RANGE)
                            isMoved = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (!isMoved && (time - lastEventTime) > PRESS_LONG_THRESHOLD) {
                        Message msg = new Message();
                        msg.what = SETTING_MSG_CODE;
                        handler.sendMessage(msg);
                    }
                    break;
            }
            /*only under finger mode can the TouchEvent
            * trigger postInvalidate method*/
            if (playForm == FINGER_MOVE_MODE){
                x = tempX; y = tempY;
                postInvalidate();
            }else if (playForm == FEEL_GRAVITY_MODE){
                // do nothing if TouchEvent but long press event happen under gravity mode
            }

            return true;
        }
    }
}
