package com.miaw.pro.vpn.activities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import com.miaw.pro.tunnel.config.Settings;

import static android.content.pm.PackageManager.GET_META_DATA;

/**
 * Created by Pankaj on 03-11-2017.
 */
public abstract class BaseActivity extends AppCompatActivity
{
	public static int mTheme = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		resetTitles();
	}

	protected void resetTitles() {
		try {
			ActivityInfo info = getPackageManager().getActivityInfo(getComponentName(), GET_META_DATA);
			if (info.labelRes != 0) {
				setTitle(info.labelRes);
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}
}
