package com.droidmarvin.peripheraliointerfacingoleddisplaywithssd1306displaydriver;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.things.contrib.driver.ssd1306.Ssd1306;

import java.io.IOException;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String I2C_OLED_BUS = "I2C1";
    private Ssd1306 mOled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupOledDisplay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyOledDisplay();
    }

    //setup the OLED Display
    private void setupOledDisplay() {
        try {
            mOled = new Ssd1306(I2C_OLED_BUS);
        } catch (IOException e) {
            Log.e(TAG, "Error while opening screen", e);
        }
        Log.d(TAG, "OLED screen activity created");
    }
//Close the display
private void destroyOledDisplay() {
    if (mOled != null) {
        try {
            mOled.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing SSD1306", e);
        } finally {
            mOled = null;
        }
    }
}

}


