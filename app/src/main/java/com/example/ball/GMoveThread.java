package com.example.ball;

import android.os.Handler;
import android.os.Message;

import java.util.IllegalFormatCodePointException;


public class GMoveThread extends Thread {

    private float x, y, acX, acY, vX, vY;
    private final float TIME_STEP_SECOND = 0.05f;
    private final long TIME_STEP_MILIS = 50;
    private final int GMOVE_MSG_CODE = KeysName.GMOVE_MSG_CODE, VELOCITY_CONSTANT = 1000;
    private int screenWidth, screenHeight;

    public Handler mainHanlder;

    public Handler gHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == GMOVE_MSG_CODE){
                float[] a = (float[]) msg.obj;
                acX = a[0];
                acY = a[1];
            }
            super.handleMessage(msg);
        }
    };

    public GMoveThread(float x, float y, float acX, float acY, int width, int height, Handler mainHanlder){
        //pos = new float[2];
        this.x = x;
        this.y = y;
        this.acX = acX;
        this.acY = acY;
        this.screenHeight = height;
        this.screenWidth = width;
        vX = 0f;
        vY = 0f;
        this.mainHanlder = mainHanlder;
    }

    @Override
    public void run() {
        try {
            while (!interrupted()) {
                calculateXY();
                Thread.sleep(TIME_STEP_MILIS);
                updateXY();
            }
        }catch (InterruptedException e){

        }
    }

    private void calculateXY(){
        float vt =  VELOCITY_CONSTANT *acX * TIME_STEP_SECOND;
        x += (vX + vt) * TIME_STEP_SECOND / 2;
        vX = vt;
        if (x < 0){
            x = 0;  vX = 0;
        }
        else if (x > screenWidth){
            x = screenWidth;    vX = 0;
        }

        vt = VELOCITY_CONSTANT * acY * TIME_STEP_SECOND;
        y += (vY + vt) * TIME_STEP_SECOND / 2;
        vY = vt;
        if (y < 0){
            y = 0; vY = 0;
        }
        else if (y > screenHeight){
            y = screenHeight; vY = 0;
        }
    }

    private void updateXY(){

        Message msg = new Message();
        msg.obj = new float[]{x, y};
        msg.what = GMOVE_MSG_CODE;
        mainHanlder.sendMessage(msg);

    }

}
