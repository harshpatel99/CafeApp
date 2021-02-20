package com.hash.cafeapp;

import android.app.Application;

public class Caffeinate extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Utils.setDefaultFont(this, "DEFAULT", "fonts/productsans.ttf");
        Utils.setDefaultFont(this, "MONOSPACE", "fonts/productsans.ttf");
        Utils.setDefaultFont(this, "SERIF", "fonts/productsans.ttf");
        Utils.setDefaultFont(this, "SANS_SERIF", "fonts/productsans.ttf");

        /*Utils.overrideFont(getApplicationContext(), "SERIF", "fonts/productsans.ttf");
        Utils.overrideFont(getApplicationContext(), "DEFAULT", "fonts/productsans.ttf");
        Utils.overrideFont(getApplicationContext(), "SANS_SERIF", "fonts/productsans.ttf");
        Utils.overrideFont(getApplicationContext(), "MONOSPACE", "fonts/productsans.ttf");*/
    }
}
