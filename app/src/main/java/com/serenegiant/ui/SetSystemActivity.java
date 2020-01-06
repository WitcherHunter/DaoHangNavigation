package com.serenegiant.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.navigation.timerterminal.R;

public class SetSystemActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set__system_);
        initview();
    }

    private void initview() {
        findViewById(R.id.bt_setting_set_sound).setOnClickListener(this);
        findViewById(R.id.bt_setting_set_backlight).setOnClickListener(this);
        findViewById(R.id.bt_setting_set_wifi).setOnClickListener(this);
        findViewById(R.id.bt_setting_set_mobilenet).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_setting_set_sound:
                startActivityForResult(new Intent(Settings.ACTION_SOUND_SETTINGS), 0);
                break;
            case R.id.bt_setting_set_backlight:
                startActivityForResult(new Intent(Settings.ACTION_DISPLAY_SETTINGS), 0);
                break;
            case R.id.bt_setting_set_wifi:
                startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
                break;
            case R.id.bt_setting_set_mobilenet:
                startActivityForResult(new Intent(Settings.ACTION_WIRELESS_SETTINGS), 0);
                break;
            case R.id.btn_back:
                finish();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
