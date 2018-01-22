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

    private int mTick = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupOledDisplay();
        drawOnScreen();
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

    // Draw on the screen
    private void drawOnScreen(){

        try {
            for (int i = 0; i < mOled.getLcdWidth(); i++) {
                for (int j = 0; j < mOled.getLcdHeight(); j++) {
                    // checkerboard
                    mOled.setPixel(i, j, (i % 2) == (j % 2));
                }
            }
            mOled.show(); // render the pixel data

            // You can also use BitmapHelper to render a bitmap instead of setting pixels manually
        } catch (IOException e) {
            // error setting display
        }
    }
    /**
     * Draws crosshair pattern.
     */
    private void drawCrosshairs() {
        mOled.clearPixels();
        int y = mTick % mOled.getLcdHeight();
        for (int x = 0; x < mOled.getLcdWidth(); x++) {
            mOled.setPixel(x, y, true);
            mOled.setPixel(x, mOled.getLcdHeight() - (y + 1), true);
        }
        int x = mTick % mOled.getLcdWidth();
        for (y = 0; y < mOled.getLcdHeight(); y++) {
            mOled.setPixel(x, y, true);
            mOled.setPixel(mOled.getLcdWidth() - (x + 1), y, true);
        }
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


