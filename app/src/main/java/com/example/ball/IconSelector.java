package com.example.ball;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;

public class IconSelector {

    private MainActivity mainActivity;
    private ComponentName defaultComponent;
    private ComponentName testComponent;
    private PackageManager packageManager;
    private AlertDialog.Builder builder;

    public IconSelector(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        initialize();
    }

    private void initialize(){
        defaultComponent = new ComponentName(mainActivity.getBaseContext(), "com.example.ball.DefaultAlias");  //拿到默认的组件
        //拿到我注册的别名test组件
        testComponent = new ComponentName(mainActivity.getBaseContext(), "com.example.ball.OtherAlias");

        packageManager = mainActivity.getApplicationContext().getPackageManager();

        final IconAdapter adapter = new IconAdapter(mainActivity);

        builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Choose App Icon:").setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(MainActivity.this, "You pick one icon", Toast.LENGTH_SHORT).show();
                switch (which){
                    case 0:
                        changeDefaultIcon();
                        break;
                    case 1:
                        changeIcon();
                        break;
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", null);
    }

    protected AlertDialog.Builder getBuilder(){
        return builder;
    }

    private void changeIcon() {
        disableComponent(defaultComponent);
        enableComponent(testComponent);
    }

    private void changeDefaultIcon() {
        enableComponent(defaultComponent);
        disableComponent(testComponent);
    }

    /**
     * 启用组件
     *
     * @param componentName
     */
    private void enableComponent(ComponentName componentName) {
        int state = packageManager.getComponentEnabledSetting(componentName);
        if (state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            //已经启用
            return;
        }
        packageManager.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    /**
     * 禁用组件
     *
     * @param componentName
     */
    private void disableComponent(ComponentName componentName) {
        int state = packageManager.getComponentEnabledSetting(componentName);
        if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            //已经禁用
            return;
        }
        packageManager.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

}
