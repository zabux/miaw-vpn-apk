package com.miaw.pro.vpn;


import android.app.Application;
import android.support.v7.app.AppCompatDelegate;
import com.miaw.pro.vpn.preference.SettingsPreference;
import com.miaw.pro.tunnel.logger.SkStatus;
import android.content.Context;
import android.content.pm.PackageInfo;
import com.miaw.pro.vpn.util.Utils;
import android.os.Build;
import android.content.pm.PackageManager;
import android.util.Log;
import com.google.android.gms.ads.MobileAds;
import android.content.res.Configuration;
import android.text.style.UpdateAppearance;
import com.miaw.pro.tunnel.config.Settings;

public class MyApplication extends Application
{
	public static final String PREFS_GERAL = "PREFS_GERAL";
	public static final String ADS_UNITID_INTERSTITIAL_MAIN = "ca-app-pub-7267179889807447/1688022938";
	public static final String ADS_UNITID_BANNER_MAIN = "ca-app-pub-7267179889807447/5080473035";
    public static final String ADS_UNITID_BANNER_ABOUT = "ca-app-pub-7267179889807447/5080473035";
    public static final String ADS_UNITID_BANNER_TEST = "ca-app-pub-7267179889807447/5080473035";
	
	public static final boolean DEBUG = true;
	public static final String UPDATE_LINK = "https://bitbin.it/fT7VZtuM/raw/";
	private static Context app;

	private Settings mConfig;


	/*static {
	 AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
	 }*/

	@Override
	public void onCreate() {
		super.onCreate();
		MobileAds.initialize(this);
		app = getApplicationContext();
		mConfig = new Settings(this);
	}

	@Override
	protected void attachBaseContext(Context base) {

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

	}

	public static Context getApp() {
		return app;
	}

}
