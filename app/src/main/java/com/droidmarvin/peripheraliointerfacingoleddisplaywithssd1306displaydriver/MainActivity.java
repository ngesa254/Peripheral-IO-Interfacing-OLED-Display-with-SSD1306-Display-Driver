package com.droidmarvin.peripheraliointerfacingoleddisplaywithssd1306displaydriver;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.things.contrib.driver.ssd1306.BitmapHelper;
import com.google.android.things.contrib.driver.ssd1306.Ssd1306;

import java.io.IOException;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String I2C_OLED_BUS = "I2C1";
    private static final int FPS = 30; // Frames per second on draw thread
    private static final int BITMAP_FRAMES_PER_MOVE = 4; // Frames to show bitmap before moving it

    private Ssd1306 mOled;

    private boolean mExpandingPixels = true;
    private int mTick = 0;
    private int mDotMod = 1;
    private int mBitmapMod = 0;

    private Bitmap mBitmap;
    private Handler mHandler = new Handler();

    private Modes mMode = Modes.BITMAP;

    enum Modes {
        CROSSHAIRS,
        DOTS,
        BITMAP
    }

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

    /**
     * Draws expanding and contracting pixels.
     */
    private void drawExpandingDots() {
        if (mExpandingPixels) {
            for (int x = 0; x < mOled.getLcdWidth(); x++) {
                for (int y = 0; y < mOled.getLcdHeight() && mMode == Modes.DOTS; y++) {
                    mOled.setPixel(x, y, (x % mDotMod) == 1 && (y % mDotMod) == 1);
                }
            }
            mDotMod++;
            if (mDotMod > mOled.getLcdHeight()) {
                mExpandingPixels = false;
                mDotMod = mOled.getLcdHeight();
            }
        } else {
            for (int x = 0; x < mOled.getLcdWidth(); x++) {
                for (int y = 0; y < mOled.getLcdHeight() && mMode == Modes.DOTS; y++) {
                    mOled.setPixel(x, y, (x % mDotMod) == 1 && (y % mDotMod) == 1);
                }
            }
            mDotMod--;
            if (mDotMod < 1) {
                mExpandingPixels = true;
                mDotMod = 1;
            }
        }
    }

    /**
     * Draws a BMP in one of three positions.
     */
    private void drawMovingBitmap() {
        if (mBitmap == null) {
            //mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.flower);
        }
        // Move the bmp every few ticks
        if (mTick % BITMAP_FRAMES_PER_MOVE == 0) {
            mOled.clearPixels();
            // Move the bitmap back and forth based on mBitmapMod:
            // 0 - left aligned
            // 1 - centered
            // 2 - right aligned
            // 3 - centered
            int diff = mOled.getLcdWidth() - mBitmap.getWidth();
            int mult = mBitmapMod == 3 ? 1 : mBitmapMod; // 0, 1, or 2
            int offset = mult * (diff / 2);
            BitmapHelper.setBmpData(mOled, offset, 0, mBitmap, false);
            mBitmapMod = (mBitmapMod + 1) % 4;
        }
    }

    private Runnable mDrawRunnable = new Runnable() {
        /**
         * Updates the display and tick counter.
         */
        @Override
        public void run() {
            // exit Runnable if the device is already closed
            if (mOled == null) {
                return;
            }
            mTick++;
            try {
                switch (mMode) {
                    case DOTS:
                        drawExpandingDots();
                        break;
                    case BITMAP:
                        drawMovingBitmap();
                        break;
                    default:
                        drawCrosshairs();
                        break;
                }
                mOled.show();
                mHandler.postDelayed(this, 1000 / FPS);
            } catch (IOException e) {
                Log.e(TAG, "Exception during screen update", e);
            }
        }
    };

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


