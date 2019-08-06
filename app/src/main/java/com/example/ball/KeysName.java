package com.example.ball;

import android.graphics.Color;

public class KeysName {

    static final String  X_Position = "xPosition",Y_Position = "yPosition",
            BallColor = "ballcolor",Radius = "radius", CreateTimes = "createTimes",
            PlayForm = "play_form", EnterTime = "Entered times: ";

    static final String[] colors = new String[]{"白色", "绿色", "红色", "蓝色"},
            play_forms = new String[]{"重力感应", "手势操作"},
            titles = new String[]{"选择操作方式", "选择小球颜色"};

    static final int xFirst =40, yFirst = 40, radiusFirst = 30, colorFirst = Color.WHITE, playForm = 1,
        FEEL_GRAVITY = 0, FINGER_MOVE = 1, SETTING_MSG_CODE = 0x123, GMOVE_MSG_CODE =0x456,
            TIPS_HEIGHT = 80, TIPS_TEXTSIZE = 20;

}
