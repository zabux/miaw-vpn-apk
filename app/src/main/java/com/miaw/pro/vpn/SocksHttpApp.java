package com.miaw.pro.vpn;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import com.google.android.gms.ads.MobileAds;
import com.miaw.pro.tunnel.SocksHttpCore;
import com.miaw.pro.vpn.util.Utils;
import com.miaw.pro.tunnel.config.ExceptionHandler;
import com.miaw.pro.tunnel.util.skartiprotect;

public class SocksHttpApp extends Application
{
	private static final String TAG = SocksHttpApp.class.getSimpleName();
	public static final String PREFS_GERAL = "SocksHttpGERAL";
//	public static final String ADS_UNITID_INTERSTITIAL_MAIN = "ca-app-pub-7267179889807447/1688022938";
//	public static final String ADS_UNITID_BANNER_MAIN = "ca-app-pub-7267179889807447/5080473035";
//	public static final String ADS_UNITID_BANNER_SOBRE = "ca-app-pub-7267179889807447/5080473035";
//	public static final String ADS_UNITID_BANNER_TEST = "ca-app-pub-7267179889807447/5080473035";
//	public static final String APP_FLURRY_KEY = "RQQ8J9Q2N4RH827G32X9";
	private static SocksHttpApp mApp;

	@Override
	public void onCreate()
	{
		skartiprotect.init(this);
		super.onCreate();
		mApp = this;
		SocksHttpCore.init(this);
        MobileAds.initialize(this);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		Utils.overrideFont(getApplicationContext(), "SERIF", "flags/skartivpn.ttf");
	}

	public static SocksHttpApp getApp() {
		return mApp;
	}
}
