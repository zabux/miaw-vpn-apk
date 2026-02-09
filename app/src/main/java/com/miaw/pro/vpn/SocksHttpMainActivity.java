package com.miaw.pro.vpn;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.Vibrator;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.miaw.pro.tunnel.StatisticGraphData;
import egcodes.com.speedtest.*;
import com.miaw.pro.tunnel.StatisticGraphData.DataTransferStats;
import com.miaw.pro.tunnel.config.ConfigParser;
import com.miaw.pro.tunnel.config.ExceptionHandler;
import com.miaw.pro.tunnel.config.Settings;
import com.miaw.pro.tunnel.logger.ConnectionStatus;
import com.miaw.pro.tunnel.logger.SkStatus;
import com.miaw.pro.tunnel.tunnel.TunnelManagerHelper;
import com.miaw.pro.tunnel.tunnel.TunnelUtils;
import com.miaw.pro.vpn.SocksHttpMainActivity;

import com.miaw.pro.vpn.activities.BaseActivity;

import com.miaw.pro.vpn.activities.ConfigGeralActivity;
import com.miaw.pro.vpn.adapter.LogsAdapter;
import com.miaw.pro.vpn.adapter.SpinnerAdapter;
import com.miaw.pro.vpn.util.AESCrypt;
import com.miaw.pro.vpn.util.ConfigUpdate;
import com.miaw.pro.vpn.util.ConfigUtil;
import com.miaw.pro.vpn.util.Utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.json.JSONObject;
import com.miaw.pro.BuildConfig;
import com.miaw.pro.R;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import android.app.NotificationManager;
import android.app.Notification;
import android.graphics.BitmapFactory;
import android.app.NotificationChannel;
import android.widget.AdapterView;
import com.google.android.gms.ads.InterstitialAd;
import com.miaw.pro.vpn.util.ToastUtil;
import android.app.DownloadManager;
import android.support.v7.app.NotificationCompat;
import java.io.UnsupportedEncodingException;
import android.view.LayoutInflater;
import android.database.Cursor;
import android.util.*;

import org.json.*;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.app.Notification;
import android.content.*;
import android.widget.*;
import android.net.*;
import android.media.*;
import android.content.pm.*;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.view.*;
import java.io.*;
import android.util.*;
import android.support.v4.content.ContextCompat;
import com.miaw.pro.tunnel.util.CustomNativeLoader;
import com.miaw.pro.vpn.util.KillThis;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import com.miaw.pro.tunnel.util.skartiprotect;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.SystemClock;
import android.widget.Chronometer.OnChronometerTickListener;
import android.text.Html;
import me.dawson.proxyserver.ui.ProxySettings;
import android.telephony.TelephonyManager;
import android.app.ProgressDialog;
import android.support.design.widget.TextInputEditText;
import android.text.TextWatcher;
import android.text.Editable;
import android.text.InputType;
//impolort egcodes.com.speedtest.*;

public class SocksHttpMainActivity extends BaseActivity
implements View.OnClickListener,  SkStatus.StateListener
				
{
	
	public static final String app_name = "Miaw VPN";
	
	public static final String pckg = "com.miaw.pro";

	private static final String UPDATE_API_URL =
		"https://api.github.com/repos/zabux/miaw-vpn-apk/releases/latest";
    
	private ToastUtil toastutil;
	private boolean isConnected  = true;
    private CoordinatorLayout coordinatorLayout;

	public void updateapp(final boolean showNoUpdateToast) {
		new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						final JSONObject release = fetchLatestRelease();
						if (release == null) {
							if (showNoUpdateToast) {
								runOnUiThread(new Runnable() {
										@Override
										public void run() {
											Toast.makeText(SocksHttpMainActivity.this, "Update check failed", Toast.LENGTH_SHORT).show();
										}
									});
							}
							return;
						}
						int remoteVersionCode = parseRemoteVersionCode(release);
						int currentVersionCode = getPackageManager()
							.getPackageInfo(getPackageName(), 0).versionCode;
						if (remoteVersionCode <= currentVersionCode) {
							if (showNoUpdateToast) {
								runOnUiThread(new Runnable() {
										@Override
										public void run() {
											Toast.makeText(SocksHttpMainActivity.this, "App already up to date", Toast.LENGTH_SHORT).show();
										}
									});
							}
							return;
						}
						runOnUiThread(new Runnable() {
								@Override
								public void run() {
									showUpdateDialog(release);
								}
							});
					} catch (Exception e) {
						// Avoid crashing on update check
					}
				}
			}).start();
	}

	private void showUpdateDialog(JSONObject release) {
		final String apkUrl = findApkUrl(release);
		if (apkUrl == null) {
			Toast.makeText(this, "Update file not found", Toast.LENGTH_SHORT).show();
			return;
		}
		String versionName = release.optString("tag_name", "latest");
		String notes = release.optString("body", "New update available");
		new AlertDialog.Builder(this)
			.setTitle("Update Available")
			.setMessage("Version: " + versionName + "\n\n" + notes)
			.setNegativeButton("Later", null)
			.setPositiveButton("Update", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						downloadAndInstallApk(apkUrl);
					}
				})
			.show();
	}

	private JSONObject fetchLatestRelease() {
		HttpURLConnection conn = null;
		InputStream in = null;
		try {
			URL url = new URL(UPDATE_API_URL);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(15000);
			conn.setRequestProperty("User-Agent", "MiawVPN");
			conn.connect();
			in = conn.getInputStream();
			StringBuilder sb = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			return new JSONObject(sb.toString());
		} catch (Exception e) {
			return null;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ignored) {}
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	private int parseRemoteVersionCode(JSONObject release) {
		String body = release.optString("body", "");
		Matcher m = Pattern.compile("versionCode\\s*[:=]\\s*(\\d+)", Pattern.CASE_INSENSITIVE).matcher(body);
		if (m.find()) {
			try {
				return Integer.parseInt(m.group(1));
			} catch (NumberFormatException ignored) {}
		}
		String tag = release.optString("tag_name", "");
		Matcher t = Pattern.compile("(\\d+)").matcher(tag);
		int last = -1;
		while (t.find()) {
			try {
				last = Integer.parseInt(t.group(1));
			} catch (NumberFormatException ignored) {}
		}
		return last;
	}

	private String findApkUrl(JSONObject release) {
		JSONArray assets = release.optJSONArray("assets");
		if (assets == null) {
			return null;
		}
		for (int i = 0; i < assets.length(); i++) {
			JSONObject asset = assets.optJSONObject(i);
			if (asset == null) {
				continue;
			}
			String name = asset.optString("name", "");
			String url = asset.optString("browser_download_url", "");
			if (name.endsWith(".apk") && url.length() > 0) {
				return url;
			}
		}
		return null;
	}

	private void downloadAndInstallApk(final String apkUrl) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			if (!getPackageManager().canRequestPackageInstalls()) {
				Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
					Uri.parse("package:" + getPackageName()));
				startActivity(intent);
				return;
			}
		}
		final ProgressDialog progress = new ProgressDialog(this);
		progress.setTitle("Downloading Update");
		progress.setMessage("Please wait...");
		progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progress.setIndeterminate(true);
		progress.setCancelable(false);
		progress.show();

		new Thread(new Runnable() {
				@Override
				public void run() {
					HttpURLConnection conn = null;
					InputStream in = null;
					FileOutputStream out = null;
					try {
						URL url = new URL(apkUrl);
						conn = (HttpURLConnection) url.openConnection();
						conn.setConnectTimeout(10000);
						conn.setReadTimeout(30000);
						conn.setRequestProperty("User-Agent", "MiawVPN");
						conn.connect();
						final int total = conn.getContentLength();
						runOnUiThread(new Runnable() {
								@Override
								public void run() {
									progress.setIndeterminate(total <= 0);
									if (total > 0) {
										progress.setMax(total);
									}
								}
							});
						in = conn.getInputStream();

						File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
						if (dir == null) {
							dir = getCacheDir();
						}
						File apkFile = new File(dir, "miaw-vpn-latest.apk");
						out = new FileOutputStream(apkFile);
						byte[] buf = new byte[8192];
						int len;
						int downloaded = 0;
						while ((len = in.read(buf)) > 0) {
							out.write(buf, 0, len);
							downloaded += len;
							final int prog = downloaded;
							if (total > 0) {
								runOnUiThread(new Runnable() {
										@Override
										public void run() {
											progress.setProgress(prog);
										}
									});
							}
						}
						out.flush();

						final File finalApk = apkFile;
						runOnUiThread(new Runnable() {
								@Override
								public void run() {
									progress.dismiss();
									installApk(finalApk);
								}
							});
					} catch (Exception ignored) {
						runOnUiThread(new Runnable() {
								@Override
								public void run() {
									progress.dismiss();
									Toast.makeText(SocksHttpMainActivity.this, "Download failed", Toast.LENGTH_SHORT).show();
								}
							});
					} finally {
						try {
							if (in != null) {
								in.close();
							}
						} catch (IOException ignored) {}
						try {
							if (out != null) {
								out.close();
							}
						} catch (IOException ignored) {}
						if (conn != null) {
							conn.disconnect();
						}
					}
				}
			}).start();
	}

	private void installApk(File apkFile) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			uri = FileProvider.getUriForFile(this,
				getPackageName() + ".fileprovider", apkFile);
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		} else {
			uri = Uri.fromFile(apkFile);
		}
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		startActivity(intent);
	}

	private void setupInterstitial(){

        mInterstitialAd = new InterstitialAd(this);

        mInterstitialAd.setAdUnitId("skartivpn"); // inter ads

        mInterstitialAd.setAdListener(new AdListener() {

                @Override
                public void onAdClosed() {
                    // Code to be executed when the interstitial ad is closed.
                    Toast.makeText(SocksHttpMainActivity.this, "Thank you for donation!! 💙", Toast.LENGTH_SHORT)
                        .show();

                    loadInterstitial();

                }
            });

        loadInterstitial();
    }

    private void loadInterstitial(){

        mInterstitialAd.loadAd(new AdRequest.Builder().build());

    }

	
	
   private void jointelegram () {
//dialog welcome
        final AlertDialog group = new AlertDialog.Builder(this)
            .setView(getLayoutInflater().inflate(R.layout.tele_join,null))
            .setPositiveButton("BUY NOW", null)
            .setNegativeButton("LATER ", null)
            .setCancelable(false)
            .show();

        Button positiveButton = group.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //https://facebook.com/groups/201305111236913/
                   String url1 = "https://t.me/@serv4nn";
                    Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(url1));
                    // intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(Intent.createChooser(intent1, getText(R.string.open_with)));
                    group.dismiss();

                }
            });

        Button setNegativeButton = group.getButton(AlertDialog.BUTTON_NEGATIVE);
        setNegativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    group.dismiss();
                }
			});
		
		
		
		}
/*
private void showInterstitial(){
        if (mInterstitialAd.isLoaded()){
            mInterstitialAd.show(); 
        } else {
            loadInterstitial();    
        }                                                     
    }
	*/
    private Button Saputra1;
    private Button Saputra2;
    private Button Saputra3;
    private Button Saputra4;
    private Button Saputra5;
	private Button Saputra6;
	private Button Saputra7;
    
	private ImageView ja123;
	
	
	private boolean mShown, mShown2;
	public String versionName;
	
    private TextInputEditText inputPwServer;
	private TextInputEditText inputPwUser;
	private TextInputEditText inputPwPass;
	
    private Chronometer cmTimer;
	private View view6;
	private View view5;
    private View view4;
	private View view3;
    private View view2;
    private View view1;
    private InterstitialAd mInterstitialAd;
    public static int PICK_FILE = 1;
	private static final String DNS_BIN = "libdns";
	private Process dnsProcess;
	private File filedns;
	private static final String UPDATE_VIEWS = "MainUpdate";
	public static LogsAdapter mAdapter;
	private Settings mConfig;
	private Toolbar toolbar_main;
	private Handler mHandler;
	private ImageView inputPwShowPass;
	private Button starterButton;
    private DrawerLayout drawerLayout;
    
	private AdView adsBannerView;
	private ConfigUtil config;
	private TextView status;
    private TextView connectionStatus;
    private TextView ExternalIP;
	private Spinner serverSpinner;
	private SpinnerAdapter serverAdapter;
	private ArrayList<JSONObject> serverList;
	private SweetAlertDialog nops;
	private SweetAlertDialog progressDialog;
	private RecyclerView logList;
	private BottomSheetBehavior<View> bottomSheetBehavior;
	private View bshl;
    private ImageView iv1;
	private SharedPreferences sp;
	private static final int START_VPN_PROFILE = 2002;
	private TextView bytesIn;
	private TextView bytesOut;
	private TextView SaputraTechOperator;
	//private String[] torrentList = new String[] {
		/*"com.termux",
		"com.tdo.showbox",
		"com.nitroxenon.terrarium",
		"com.pklbox.translatorspro",
		"com.xunlei.downloadprovider",
		"com.epic.app.iTorrent",
		"hu.bute.daai.amorg.drtorrent",
		"com.mobilityflow.torrent.prof",
		"com.brute.torrentolite",
		"com.nebula.swift",
		"tv.bitx.media",
		"com.DroiDownloader",
		"bitking.torrent.downloader",
		"org.transdroid.lite",
		"com.mobilityflow.tvp",
		"com.gabordemko.torrnado",
		"com.frostwire.android",
		"com.vuze.android.remote",
		"com.akingi.torrent",
		"com.utorrent.web",
		"com.paolod.torrentsearch2",
		"com.delphicoder.flud.paid",
		"com.teeonsoft.ztorrent",
		"megabyte.tdm",
		"com.bittorrent.client.pro",
		"com.mobilityflow.torrent",
		"com.utorrent.client",
		"com.utorrent.client.pro",
		"com.bittorrent.client",
		"torrent",
		"com.AndroidA.DroiDownloader",
		"com.indris.yifytorrents",
		"com.delphicoder.flud",
		"com.oidapps.bittorrent",
		"dwleee.torrentsearch",
		"com.vuze.torrent.downloader",
		"megabyte.dm",
		"com.asantos.vip",
		"com.fgrouptech.kickasstorrents",
		"com.jrummyapps.rootbrowser.classic",
		"com.bittorrent.client",
		"com.x.gdf",   
		"co.we.torrent"};*/


	  private void openlogs(){

		if (!(((String) getPackageManager().getApplicationLabel(getApplicationInfo())).equals(SocksHttpMainActivity.app_name) && getPackageName().equals(SocksHttpMainActivity.pckg))) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.HiroDialog);
			builder.setView(getLayoutInflater().inflate(R.layout.layout_spinner_row,null));
			builder.setCancelable(false);
			builder.setPositiveButton("Exit", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface p1, int p2)
					{
						// TODO: Implement this method
						if (android.os.Build.VERSION.SDK_INT >= 21) {
							finishAndRemoveTask();
						} else {
							android.os.Process.killProcess(android.os.Process.myPid());
						}
						System.exit(0);

					}
				});
			builder.show();

		}
	}

    

	@Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
		openlogs();
		toastutil = new ToastUtil(this);
		    super.onCreate(savedInstanceState);
		setContentView(R.layout.drawer_layout);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		//new TorrentDetection(this, torrentList).init();
		mHandler = new Handler();
		mConfig = new Settings(this);
		SharedPreferences prefs = getSharedPreferences(SocksHttpApp.PREFS_GERAL, Context.MODE_PRIVATE);
		boolean showFirstTime = prefs.getBoolean("connect_first_time", true);
		if (showFirstTime)
        {
            SharedPreferences.Editor pEdit = prefs.edit();
            pEdit.putBoolean("connect_first_time", false);
            pEdit.apply();
			Settings.setDefaultConfig(this);
			showBoasVindas();
        }
		//setupInterstitial();
		//doLayout();
		initHomeButtons();
		initBytesInAndOut();
		ensureServerSpinnerInitialized();
		doUpdateLayout();
		jointelegram();
		updateapp(false);
		skartiprotect.CharlieProtect();

	}

	void initBytesInAndOut() {
        bytesIn = (TextView) findViewById(R.id.bytes_in);
        bytesOut = (TextView) findViewById(R.id.bytes_out);
		StatisticGraphData.getStatisticData().setDisplayDataTransferStats(true);
    }

	private void initHomeButtons() {
		Saputra2 = (Button) findViewById(R.id.sk_exit);
		if (Saputra2 != null) {
			Saputra2.setOnClickListener(this);
		}
		Saputra5 = (Button) findViewById(R.id.settings);
		if (Saputra5 != null) {
			Saputra5.setOnClickListener(this);
		}
		Saputra6 = (Button) findViewById(R.id.sk_restore);
		if (Saputra6 != null) {
			Saputra6.setOnClickListener(this);
		}
		Button toolsButton = (Button) findViewById(R.id.tools);
		if (toolsButton != null) {
			toolsButton.setOnClickListener(this);
		}
	}

	private void updateHeaderCallback() {
		DataTransferStats dataTransferStats = StatisticGraphData.getStatisticData().getDataTransferStats();
		bytesIn.setText(Utils.byteCountToDisplaySize(dataTransferStats.getTotalBytesReceived(), false));
		bytesOut.setText(Utils.byteCountToDisplaySize(dataTransferStats.getTotalBytesSent(), false));
	}

    private void doLayout() {
        setContentView(R.layout.activity_main_drawer);
        toolbar_main = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar_main);
    

		// Operator View
        TelephonyManager telephonyManager = ((TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE));
        String simOperatorName = telephonyManager.getSimOperatorName();
        String RegionSim = telephonyManager.getSimCountryIso();
        SaputraTechOperator = (TextView) findViewById(R.id.SaputraTechOperator);
		SaputraTechOperator.setText(simOperatorName + " ( " + RegionSim.toUpperCase() + " ) ");
        

		// set ADS
        adsBannerView = (AdView) findViewById(R.id.adBannerMainView);

        if (!BuildConfig.DEBUG) {
            //adsBannerView.setAdUnitId(SocksHttpApp.ADS_UNITID_BANNER_MAIN);
        }

      /*  if (TunnelUtils.isNetworkOnline(SocksHttpMainActivity.this)) {
            adsBannerView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        if (adsBannerView != null) {
                            adsBannerView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            adsBannerView.loadAd(new AdRequest.Builder()
                                 .build());
		}
*/
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);       
        NavigationView navigationView = (NavigationView) findViewById(R.id.shitstuff);

        PackageInfo pinfo = Utils.getAppInfo(this);
        if (pinfo != null) {
            String version_nome = pinfo.versionName;
            int version_code = pinfo.versionCode;
            String header_text = String.format("v. %s (%d)", version_nome, version_code);
			
			TextView app_info_text1 = (TextView) findViewById(R.id.app_info);
                app_info_text1.setText(header_text);
     
			
            View view = navigationView.getHeaderView(0);
            TextView app_info_text = view.findViewById(R.id.nav_headerAppVersion);
            app_info_text.setText(header_text);
        }
        
		navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                // This method will trigger on item Click of navigation menu
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem)
                {

                    switch (menuItem.getItemId())
                    {
                        case R.id.cfg2:
                            drawerLayout.closeDrawers();
                            updateConfig(false);
                            return true;
							
                            

                        case R.id.cfg1:
                            offlineUpdate();
							break;
							
                        case R.id.close_Drawers:
                            drawerLayout.closeDrawers();                                 
                            return true;

                        case R.id.apn:
                            Intent intent1 = new Intent();
                            overridePendingTransition(R.anim.up_enter,R.anim.up_exit);
                            intent1.setAction("android.settings.APN_SETTINGS");
                            SocksHttpMainActivity.this.startActivity(intent1);
                            overridePendingTransition(R.anim.up_enter,R.anim.up_exit);
                            return true;
                            
                        case R.id.vii:
                            String url4 = "https://t.me/@serv4nn";
                            Intent intent4 = new Intent(Intent.ACTION_VIEW, Uri.parse(url4));
                            // intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(Intent.createChooser(intent4, getText(R.string.open_with)));
                            break;

                        case R.id.miPhoneConfg:
                            PackageInfo app_info = Utils.getAppInfo(getApplicationContext());
                            if (app_info != null) {
                                String aparelho_marca = Build.BRAND.toUpperCase();

                                if (aparelho_marca.equals("SAMSUNG") || aparelho_marca.equals("HUAWEY")) {
                                    Toast.makeText(getApplicationContext(), R.string.error_no_supported, Toast.LENGTH_SHORT)
                                        .show();
                                }
                                else {
                                    try {
                                        Intent in = new Intent(Intent.ACTION_MAIN);
                                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        in.setClassName("com.android.settings", "com.android.settings.RadioInfo");
                                        startActivity(in);
                                    } catch(Exception e) {
                                        Toast.makeText(getApplicationContext(), R.string.error_no_supported, Toast.LENGTH_SHORT)
                                            .show();
                                    }
                                }
                            }
                            break;
                        case R.id.hotspot:
							Intent hostshare2 = new Intent(SocksHttpMainActivity.this, ProxySettings.class);
							startActivity(hostshare2);
							break;

							//SPEEDTEST
                        case R.id.speedtest:
							Intent speedtest2 = new Intent(SocksHttpMainActivity.this, MainActivity.class);
							startActivity(speedtest2);
							overridePendingTransition(R.anim.up_enter,R.anim.up_exit);
                            return true;  
						
						case R.id.update_app:
							drawerLayout.closeDrawers();
							updateapp(true);
							return true;

                    }


					return false;
				}
            });

		view1 = findViewById(R.id.serverSpinner);
        view2 = findViewById(R.id.monsour_stats);
        view3 = findViewById(R.id.bottom_sheet);
        view4 = findViewById(R.id.activity_starterButtonMain);
        view5 = findViewById(R.id.bytes_in);
        view6 = findViewById(R.id.bytes_out);
        
inputPwShowPass = (ImageView) findViewById(R.id.activity_mainInputShowPassImageButton);
		inputPwShowPass.setOnClickListener(this);
		inputPwServer = (TextInputEditText) findViewById(R.id.activity_mainInputPasswordServerEdit);
		inputPwUser = (TextInputEditText) findViewById(R.id.activity_mainInputPasswordUserEdit);
		inputPwPass = (TextInputEditText) findViewById(R.id.activity_mainInputPasswordPassEdit);
	
	
		final SharedPreferences prefsTxt = mConfig.getPrefsPrivate();
		inputPwServer.setText(prefsTxt.getString(Settings.SERVIDOR_KEY, ""));
		inputPwUser.setText(prefsTxt.getString(Settings.USUARIO_KEY, ""));
		inputPwPass.setText(prefsTxt.getString(Settings.SENHA_KEY, ""));
		inputPwServer.addTextChangedListener(new TextWatcher() {

				public void afterTextChanged(Editable s) {
					if(!s.toString().isEmpty()) {
						prefsTxt.edit().putString(Settings.SERVIDOR_KEY, s.toString()).apply();
					}
				}

				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

				public void onTextChanged(CharSequence s, int start, int before, int count) {}
			});
		inputPwUser.addTextChangedListener(new TextWatcher() {

				public void afterTextChanged(Editable s) {
					if(!s.toString().isEmpty()) {
						prefsTxt.edit().putString(Settings.USUARIO_KEY, s.toString()).apply();
					}
				}

				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

				public void onTextChanged(CharSequence s, int start, int before, int count) {}
			});
		inputPwPass.addTextChangedListener(new TextWatcher() {

				public void afterTextChanged(Editable s) {
					if(!s.toString().isEmpty()) {
						prefsTxt.edit().putString(Settings.SENHA_KEY, s.toString()).apply();
					}
				}

				public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

				public void onTextChanged(CharSequence s, int start, int before, int count) {}
			});	
			
        
        Saputra2 = (Button) findViewById(R.id.sk_exit);
       Saputra2.setOnClickListener(this);
        
        Saputra5 = (Button) findViewById(R.id.settings);
        Saputra5.setOnClickListener(this);
        Saputra6 = (Button) findViewById(R.id.sk_restore);
		Saputra6.setOnClickListener(this);
        //Saputra7 = (Button) findViewById(R.id.sk_admin);
       // Saputra7.setOnClickListener(this);
        
        
		
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        Vibrator vb = (Vibrator)   getSystemService(Context.VIBRATOR_SERVICE);
		
        cmTimer = (Chronometer) findViewById(R.id.cmTimer);
        
		starterButton = (Button) findViewById(R.id.activity_starterButtonMain);
		starterButton.setOnClickListener(this);
		status = (TextView) findViewById(R.id.monsour_stats);
      connectionStatus = (TextView) findViewById(R.id.connection_status);      
		config = new ConfigUtil(this);
		serverSpinner = (Spinner) findViewById(R.id.serverSpinner);
		serverList = new ArrayList<>();
		serverAdapter = new SpinnerAdapter(this, R.id.serverSpinner, serverList);
		serverSpinner.setAdapter(serverAdapter);
		loadServer();
		updateConfig(true);
        
        
		SharedPreferences sPrefs = mConfig.getPrefsPrivate();
		sPrefs.edit().putBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, false).apply();
		sPrefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_PROXY).apply();
		TextView configVer = (TextView) findViewById(R.id.skarti_config);
		configVer.setText(config.getVersion());
		View bottomSheet = findViewById(R.id.bottom_sheet); 
		this.bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
		this.bshl = findViewById(R.id.bshl);
        this.iv1 = (ImageView)findViewById(R.id.ivLogsDown);
		serverSpinner.setSelection(sPrefs.getInt("LastSelectedServer", 0));
        serverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

                @Override
                public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
                    SharedPreferences sPrefs = mConfig.getPrefsPrivate();
                    SharedPreferences.Editor edit = sPrefs.edit();
                    edit.putInt("LastSelectedServer", p3).apply();
                }

                @Override
                public void onNothingSelected(AdapterView<?> p1) {
                }
            });
		
        cmTimer.setOnChronometerTickListener(new OnChronometerTickListener(){
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    long time = SystemClock.elapsedRealtime() - chronometer.getBase();
                    int h   = (int)(time /3600000);
                    int m = (int)(time - h*3600000)/60000;
                    int s= (int)(time - h*3600000- m*60000)/1000 ;
                    String t = (h < 10 ? "0"+h: h)+"h:"+(m < 10 ? "0"+m: m)+"m:"+ (s < 10 ? "0"+s: s)+"s";
                    chronometer.setText(t);
                }
            });
        cmTimer.setBase(SystemClock.elapsedRealtime());
        cmTimer.setText("00h:00m:00s");
        
            
            
        View persistentbottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);

        final BottomSheetBehavior behavior = BottomSheetBehavior.from(persistentbottomSheet);
		behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() { 
				@Override 
				public void onStateChanged(@NonNull View view, int i) { 
					switch (i){ 
						case BottomSheetBehavior.STATE_HIDDEN:
                            break;
                        case BottomSheetBehavior.STATE_EXPANDED:
                            break;
                        case BottomSheetBehavior.STATE_COLLAPSED: 
                            break;
                        case BottomSheetBehavior.STATE_DRAGGING:
                            break;
                        case BottomSheetBehavior.STATE_SETTLING:
                            behavior.setHideable(false);
                            break;
					} 
				} 
				@Override 
				public void onSlide(@NonNull View view, float slideOffset) { 

				} 
			});
			
		bshl.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					if (behavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                    {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        //btnBottomSheet.setText(R.string.close);
                        iv1.animate().setDuration(500).rotation(180);
                    }
                    else
                    {
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        //btnBottomSheet.setText(R.string.expand);
                        iv1.animate().setDuration(500).rotation(0);
                    }
                }
            });
			

		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		mAdapter = new LogsAdapter(layoutManager, this);
		logList = (RecyclerView) findViewById(R.id.recyclerLog);
		logList.setAdapter(mAdapter);
		logList.setLayoutManager(layoutManager);
		mAdapter.scrollToLastPosition();
		boolean isRunning = SkStatus.isTunnelActive();
        if (isRunning) {
            serverSpinner.setEnabled(false);
        } else {
            serverSpinner.setEnabled(true);
        }
	}

    
    

	private void doUpdateLayout() {
		SharedPreferences prefs = mConfig.getPrefsPrivate();
		//AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
		boolean isRunning = SkStatus.isTunnelActive();
		int tunnelType = prefs.getInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT);

		setStarterButton(starterButton, this);

		AppCompatRadioButton sshDirectRadio =
			(AppCompatRadioButton) findViewById(R.id.activity_mainSSHDirectRadioButton);
		AppCompatRadioButton sshProxyRadio =
			(AppCompatRadioButton) findViewById(R.id.activity_mainSSHProxyRadioButton);

		switch (tunnelType) {
			case Settings.bTUNNEL_TYPE_SSH_DIRECT:
				if (sshDirectRadio != null) {
					sshDirectRadio.setChecked(true);
				}
				break;

			case Settings.bTUNNEL_TYPE_SSH_PROXY:
				if (sshProxyRadio != null) {
					sshProxyRadio.setChecked(true);
				}
				break;
		}
		SharedPreferences sPrefs = mConfig.getPrefsPrivate();
		if (prefs.getBoolean(Settings.CONFIG_PROTEGER_KEY, false)) {

			if (prefs.getBoolean(Settings.CONFIG_INPUT_PASSWORD_KEY, false)) {
				loginVisibility = View.VISIBLE;

				if (inputPwServer != null && inputPwUser != null && inputPwPass != null) {
					inputPwServer.setText(mConfig.getPrivString(Settings.SERVIDOR_KEY));
					inputPwUser.setText(mConfig.getPrivString(Settings.USUARIO_KEY));
					inputPwPass.setText(mConfig.getPrivString(Settings.SENHA_KEY));

					inputPwServer.setEnabled(!isRunning);
					inputPwUser.setEnabled(!isRunning);
					inputPwPass.setEnabled(!isRunning);
					if (inputPwShowPass != null) {
						inputPwShowPass.setEnabled(!isRunning);
					}
				}

				//inputPwPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			}
		}

        if (serverSpinner != null) {
			if (isRunning) {
				serverSpinner.setEnabled(false);
			} else {
				serverSpinner.setEnabled(true);
			}
		}
	}

	private void ensureServerSpinnerInitialized() {
		if (serverSpinner == null) {
			serverSpinner = (Spinner) findViewById(R.id.serverSpinner);
		}
		if (serverSpinner == null) {
			return;
		}
		if (config == null) {
			config = new ConfigUtil(this);
		}
		if (serverList == null) {
			serverList = new ArrayList<>();
		}
		if (serverAdapter == null) {
			serverAdapter = new SpinnerAdapter(this, R.id.serverSpinner, serverList);
			serverSpinner.setAdapter(serverAdapter);
			loadServer();
		}
		if (serverSpinner.getOnItemSelectedListener() == null) {
			serverSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
					@Override
					public void onItemSelected(AdapterView<?> p1, View p2, int p3, long p4) {
						SharedPreferences sPrefs = mConfig.getPrefsPrivate();
						SharedPreferences.Editor edit = sPrefs.edit();
						edit.putInt("LastSelectedServer", p3).apply();
					}

					@Override
					public void onNothingSelected(AdapterView<?> p1) {
					}
				});
		}
	}



	private void setStarterButton(Button starterButton, SocksHttpMainActivity p1) {
	}

	int loginVisibility = View.VISIBLE;

	private synchronized void doSaveData() {
		try {
			SharedPreferences prefs = mConfig.getPrefsPrivate();
			SharedPreferences.Editor edit = prefs.edit();
			
			edit.apply();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void loadServerData() {
		try {
			SharedPreferences prefs = mConfig.getPrefsPrivate();
			SharedPreferences.Editor edit = prefs.edit();
			int pos1 = serverSpinner.getSelectedItemPosition();

			String ssh_server = config.getServersArray().getJSONObject(pos1).getString("ServerIP");
			String remote_proxy = config.getServersArray().getJSONObject(pos1).getString("ProxyIP");
			String proxy_port = config.getServersArray().getJSONObject(pos1).getString("ProxyPort");
            String ssh_user = config.getServersArray().getJSONObject(pos1).getString("ServerUser");
            String ssh_password = config.getServersArray().getJSONObject(pos1).getString("ServerPass");
			String ssh_port = config.getServersArray().getJSONObject(pos1).getString("ServerPort");
			String ssl_port = config.getServersArray().getJSONObject(pos1).getString("SSLPort");
			String payload = config.getServersArray().getJSONObject(pos1).getString("Payload");
			String sni = config.getServersArray().getJSONObject(pos1).getString("SNI");
			
			
			
            edit.putString(Settings.USUARIO_KEY, inputPwUser.getEditableText().toString());
			edit.putString(Settings.SENHA_KEY, inputPwPass.getEditableText().toString());
			edit.putString(Settings.SERVIDOR_KEY, inputPwServer.getEditableText().toString());
			edit.putString(Settings.PROXY_IP_KEY, remote_proxy);
			edit.putString(Settings.PROXY_PORTA_KEY, proxy_port);
			
			boolean sslType = config.getServersArray().getJSONObject(pos1).getBoolean("isSSL");

			boolean sslpayload = config.getServersArray().getJSONObject(pos1).getBoolean("isPayloadSSL");

			boolean inject = config.getServersArray().getJSONObject(pos1).getBoolean("isInject");

			boolean direct = config.getServersArray().getJSONObject(pos1).getBoolean("isDirect");

			
			
            //SSH DIRECT
			if (direct)
			{
				prefs.edit().putBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, false).apply();
				prefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_DIRECT).apply();

				prefs.edit().putString(Settings.SERVIDOR_KEY, ssh_server).apply();
				prefs.edit().putString(Settings.SERVIDOR_PORTA_KEY, ssh_port).apply();

				prefs.edit().putString(Settings.PROXY_IP_KEY, remote_proxy).apply();
				prefs.edit().putString(Settings.PROXY_PORTA_KEY, proxy_port).apply();
				prefs.edit().putString(Settings.CUSTOM_PAYLOAD_KEY, payload).apply();
			}

             //SSH PROXY
			if (inject)
			{
				prefs.edit().putBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, false).apply();
				prefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_PROXY).apply();

				prefs.edit().putString(Settings.SERVIDOR_KEY, ssh_server).apply();
				prefs.edit().putString(Settings.SERVIDOR_PORTA_KEY, ssh_port).apply();

				prefs.edit().putString(Settings.PROXY_IP_KEY, remote_proxy).apply();
				prefs.edit().putString(Settings.PROXY_PORTA_KEY, proxy_port).apply();

				prefs.edit().putString(Settings.CUSTOM_PAYLOAD_KEY, payload).apply();
			}
			
		
            //SSH SSL
			if (sslType)
			{
				prefs.edit().putBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, true).apply();
				prefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSH_SSL).apply();

				prefs.edit().putString(Settings.SERVIDOR_KEY, ssh_server).apply();
				prefs.edit().putString(Settings.SERVIDOR_PORTA_KEY, ssl_port).apply();

				prefs.edit().putString(Settings.PROXY_IP_KEY, remote_proxy).apply();
				prefs.edit().putString(Settings.PROXY_PORTA_KEY, proxy_port).apply();

				prefs.edit().putString(Settings.CUSTOM_PAYLOAD_KEY, payload).apply();
				prefs.edit().putString(Settings.CUSTOM_SNI, sni).apply();

			}
			//SSL PAYLOAD
			if (sslpayload)
			{
				prefs.edit().putBoolean(Settings.PROXY_USAR_DEFAULT_PAYLOAD, false).apply();
				prefs.edit().putInt(Settings.TUNNELTYPE_KEY, Settings.bTUNNEL_TYPE_SSL_PAYLOAD).apply();

				prefs.edit().putString(Settings.SERVIDOR_KEY, ssh_server).apply();
				prefs.edit().putString(Settings.SERVIDOR_PORTA_KEY, ssl_port).apply();

				prefs.edit().putString(Settings.CUSTOM_PAYLOAD_KEY, payload).apply();
				prefs.edit().putString(Settings.CUSTOM_SNI, sni).apply();

			}
			
			
			edit.apply();

		} catch (Exception e) {
			SkStatus.logInfo(e.getMessage());
		}
	}

	private void loadServer() {
		try {
			if (serverList.size() > 0) {
				serverList.clear();
				serverAdapter.notifyDataSetChanged();
			}
			for (int i = 0; i < config.getServersArray().length(); i++) {
				JSONObject obj = config.getServersArray().getJSONObject(i);
				serverList.add(obj);
				serverAdapter.notifyDataSetChanged();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateConfig(final boolean isOnCreate) {
		new ConfigUpdate(this, new ConfigUpdate.OnUpdateListener() {
				@Override
				public void onUpdateListener(String result) {
					try {
						if (!result.contains("Error on getting data")) {
							String json_data = AESCrypt.decrypt(config.PASSWORD, result);
							if (isNewVersion(json_data)) {
								newUpdateDialog(result);
							} else {
								if (!isOnCreate) {
									noUpdateDialog();
								}
							}
						} else if(result.contains("Error on getting data") && !isOnCreate){
							errorUpdateDialog(result);
						}
					} catch (Exception e) {
						SkStatus.logInfo(e.getMessage());
					}
				}
			}).start(isOnCreate);
	}

	private boolean isNewVersion(String result) {
		try {
			String current = config.getVersion();
			String update = new JSONObject(result).getString("Version");
			return config.versionCompare(update, current);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

    private void welcomeNotif(){

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); 
        Notification.Builder notification = new Notification.Builder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setChannelId(getPackageName() + "Config.json");
            createNotification(notificationManager, getPackageName() + "Config.json");
        }

        notification.setContentTitle(getString(R.string.app_name))
            .setContentText(("Config Berhasil DiPerbaharui"))
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_bell_updated))
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(Notification.PRIORITY_HIGH)
            .setShowWhen(true)
            .setSmallIcon(R.drawable.ic_bell_notify);
        notificationManager.notify(4130,notification.getNotification());
    }

    private void createNotification(NotificationManager notificationManager, String id)
    {
        NotificationChannel mNotif = new NotificationChannel(id, "Config.json", NotificationManager.IMPORTANCE_HIGH);
        mNotif.setShowBadge(true);
        notificationManager.createNotificationChannel(mNotif);
        // TODO: Implement this method
    }
    
	private void newUpdateDialog(final String result) throws JSONException, GeneralSecurityException{


		String json_data = AESCrypt.decrypt(config.PASSWORD, result);
		String notes = new JSONObject(json_data).getString("ReleaseNotes");
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflate = inflater.inflate(R.layout.help, (ViewGroup) null);
        AlertDialog.Builder builer = new AlertDialog.Builder(this); 
		builer.setView(inflate);
		ImageView iv = inflate.findViewById(R.id.icon);
		TextView title = inflate.findViewById(R.id.title);
		TextView ms = inflate.findViewById(R.id.message);
		TextView bubu = inflate.findViewById(R.id.positiveTxt);
		iv.setImageResource(R.drawable.ic_bell_updated);
		title.setText("Update Tersedia!");
        ms.setText(notes);
        bubu.setText("Update Now");
		final AlertDialog alert = builer.create(); 
        alert.setCanceledOnTouchOutside(false);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.getWindow().setGravity(Gravity.CENTER); 
		alert.getWindow().getAttributes().windowAnimations = R.style.dialog;
        alert.show();
        bubu.setOnClickListener(new View.OnClickListener() { 

				@Override 
                public void onClick(View v) { 

                    // TODO: Implement this method
					welcomeNotif();
                    try
                    {
						alert.dismiss();
                        File file = new File(getFilesDir(), "Config.json");
                        OutputStream out = new FileOutputStream(file);
                        out.write(result.getBytes());
                        out.flush();
                        out.close();
                        restart_app();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }});

        alert.show();
    }

	private void noUpdateDialog() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflate = inflater.inflate(R.layout.help, (ViewGroup) null);
        AlertDialog.Builder builer = new AlertDialog.Builder(this); 
        builer.setView(inflate); 
        ImageView iv = inflate.findViewById(R.id.icon);
		TextView title = inflate.findViewById(R.id.title);
		TextView ms = inflate.findViewById(R.id.message);
		TextView bubu = inflate.findViewById(R.id.positiveTxt);
		iv.setImageResource(R.drawable.src_images_mine_shareicon);
        title.setText("Versi Terbaru");
        ms.setText("Kamu Telah Menggunakan Versi Terbaru.");
        bubu.setText("KEMBALI");
        final AlertDialog alert = builer.create(); 
        alert.setCanceledOnTouchOutside(false);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.getWindow().setGravity(Gravity.CENTER); 
		alert.getWindow().getAttributes().windowAnimations = R.style.dialog;
        alert.show();
        bubu.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View v) { 
                    try
                    {
						alert.dismiss();

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }});

        alert.show();
	}

	private void errorUpdateDialog(String error) {

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflate = inflater.inflate(R.layout.help, (ViewGroup) null);
        AlertDialog.Builder builer = new AlertDialog.Builder(this); 
        builer.setView(inflate); 
        ImageView iv = inflate.findViewById(R.id.icon);
		TextView title = inflate.findViewById(R.id.title);
		TextView ms = inflate.findViewById(R.id.message);
		TextView bubu = inflate.findViewById(R.id.positiveTxt);
		iv.setImageResource(R.drawable.src_images_mine_shareicon);
        title.setText("Terjadi Kesalahan");
        ms.setText("Silahkan Periksa Koneksi Internet Anda.\nThanks You");
        bubu.setText("BACK");
        final AlertDialog alert = builer.create(); 
        alert.setCanceledOnTouchOutside(false);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.getWindow().setGravity(Gravity.CENTER); 
		alert.getWindow().getAttributes().windowAnimations = R.style.dialog;
        alert.show();
        bubu.setOnClickListener(new View.OnClickListener() { 
                @Override 
                public void onClick(View v) { 
                    try
                    {
						alert.dismiss();

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }});

        alert.show();


	}
	
	private void restart_app() {
		Context context = SocksHttpApp.getApp();
		PackageManager packageManager = context.getPackageManager();
		Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
		ComponentName componentName = intent.getComponent();
		Intent mainIntent = Intent.makeRestartActivityTask(componentName);
		context.startActivity(mainIntent);
		Runtime.getRuntime().exit(0);
	}

    private void snack(String msg) {
        Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_SHORT).show();
    }
	
	void showSnackBar(String message)
    {
        View customPos = (View) findViewById(R.id.coordinator); 
        final Snackbar snac = Snackbar.make(customPos, message, Snackbar.LENGTH_LONG); 
        snac.getView().setBackgroundColor(ContextCompat.getColor(customPos.getContext(), R.color.white)); 
        snac.setActionTextColor(getResources().getColor(R.color.white)); 
        customPos = snac.getView(); 
        TextView setTxtGravity = (TextView) customPos.findViewById(android.support.design.R.id.snackbar_text); 
        setTxtGravity.setTextAlignment(customPos.TEXT_ALIGNMENT_CENTER); 
        snac.show(); 
	}
    
    

    public void offlineUpdate() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE);
    }
	
	
	

    private String importer(Uri uri)
    {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try
        {
            reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));

            String line = "";
            while ((line = reader.readLine()) != null)
            {
                builder.append(line);
            }
            reader.close();
        }
        catch (IOException e) {e.printStackTrace();}
        return builder.toString();
    } 

	public void startOrStopTunnel(Activity activity) {
		if (SkStatus.isTunnelActive()) {
            SharedPreferences prefs = mConfig.getPrefsPrivate();
			TunnelManagerHelper.stopSocksHttp(activity);
            iv1.animate().setDuration(500).rotation(0);
            cmTimer.stop();
            cmTimer.setText("00h:00m:00s");
		}
		else {
			launchVPN();
            cmTimer.setBase(SystemClock.elapsedRealtime());
            cmTimer.start();
		}
	}

	private void launchVPN() {
		Intent intent = VpnService.prepare(this);

        if (intent != null) {
            SkStatus.updateStateString("USER_VPN_PERMISSION", "", R.string.state_user_vpn_permission,
									   ConnectionStatus.LEVEL_WAITING_FOR_USER_INPUT);
            // Start the query
            try {
                startActivityForResult(intent, START_VPN_PROFILE);
            } catch (ActivityNotFoundException ane) {
                SkStatus.logError(R.string.no_vpn_support_image);
            }
        } else {
            onActivityResult(START_VPN_PROFILE, Activity.RESULT_OK, null);
        }
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE)
        {
            if (resultCode == RESULT_OK) {
                try {
                    Uri uri = data.getData();
                    String intentData = importer(uri);
                    //String cipter = AESCrypt.decrypt(ConfigUtil.PASSWORD, intentData);
                    File file = new File(getFilesDir(), "Config.json");
                    OutputStream out = new FileOutputStream(file);
                    out.write(intentData.getBytes());
                    out.flush();
                    out.close();
                    restart_app();
                    welcomeNotif();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
		if (requestCode == START_VPN_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
				SharedPreferences prefs = mConfig.getPrefsPrivate();

				if (!TunnelUtils.isNetworkOnline(this)) {
					Toast.makeText(this, "Tidak Ada Koneksi Internet !", 0).show();
				} else
					TunnelManagerHelper.startSocksHttp(this);
			}
		}}


	public void setStarterButton(Button starterButton, Activity activity) {
		String state = SkStatus.getLastState();
		boolean isRunning = SkStatus.isTunnelActive();

		if (starterButton != null) {
			int resId;

			SharedPreferences prefsPrivate = new Settings(activity).getPrefsPrivate();

			if (ConfigParser.isValidadeExpirou(prefsPrivate
											   .getLong(Settings.CONFIG_VALIDADE_KEY, 0))) {
				resId = R.string.expired;
				starterButton.setEnabled(false);

				if (isRunning) {
					startOrStopTunnel(activity);
				}
			}
			else if (prefsPrivate.getBoolean(Settings.BLOQUEAR_ROOT_KEY, false) &&
					 ConfigParser.isDeviceRooted(activity)) {
				resId = R.string.blocked;
				starterButton.setEnabled(false);

				Toast.makeText(activity, R.string.error_root_detected, Toast.LENGTH_SHORT)
					.show();

				if (isRunning) {
					startOrStopTunnel(activity);
				}
			}
			else if (SkStatus.SSH_INICIANDO.equals(state)) {
				resId = R.string.stop;
				starterButton.setEnabled(false);
			}
			else if (SkStatus.SSH_PARANDO.equals(state)) {
				resId = R.string.state_stopping;
				starterButton.setEnabled(false);
				StatisticGraphData.getStatisticData().getDataTransferStats().stop();
			}
			else {
				resId = isRunning ? R.string.stop : R.string.start;
				starterButton.setEnabled(true);

			}


		}
	}


   
    
	@Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);

    }


  
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }
    
    private boolean isMostrarSenha = false;

    @Override
    public void onClick(View p1)
    {
        switch (p1.getId()) {
            case R.id.activity_starterButtonMain:
                doSaveData();
                loadServerData();
                startOrStopTunnel(this);
                mShown = false;
                mShown2 = false;
                break;

			case R.id.tools:
				offlinez(p1);
				break;


                
            
                
            case R.id.sk_exit:
                exit();
                break;
                
            case R.id.settings:
                Intent intentSettings = new Intent(SocksHttpMainActivity.this, ConfigGeralActivity.class);
                startActivity(intentSettings);
                overridePendingTransition(R.anim.up_enter,R.anim.up_exit);
                break;

            case R.id.sk_restore:
                drawerLayout.closeDrawers();        
                sk_restore();
                break;

			case R.id.update_app:
				updateapp(true);
				break;


							case R.id.activity_mainInputShowPassImageButton:
				isMostrarSenha = !isMostrarSenha;
				if (isMostrarSenha) {
					SharedPreferences prefs = mConfig.getPrefsPrivate();
					inputPwPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					inputPwShowPass.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.show1));
				}
				else {
					SharedPreferences prefs = mConfig.getPrefsPrivate();
					inputPwPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					inputPwShowPass.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.show2));
				}
				break;
			
		
        }

    }
    
    
    
    
    
    public void exit() {
			// mostra opção para sair
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.HiroDialog).
            create();
        dialog.setTitle(getString(R.string.attention));
        dialog.setMessage(getString(R.string.alert_exit));

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.
                                                                    string.exit),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    Utils.exitAll(SocksHttpMainActivity.this);
                }
            }
        );

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.
                                                                    string.minimize),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // minimiza app
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                }
            }
        );
        dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface p1, int p2)
                {
                    p1.dismiss();
                }
            });
        dialog.show();
    
}





    
    private void sk_restore()
    {
        nops = new SweetAlertDialog(SocksHttpMainActivity.this, SweetAlertDialog.WARNING_TYPE);
        nops.setTitleText("Peringatan !!");
        nops.setContentText("Apakah anda yakin ingin menghapus config yang sudah ada ?? \n Klik OK untuk menghapus");
        nops.setCancelText("Batal");
        nops.setConfirmText("Lanjutkan");
        nops.showCancelButton(true);
        nops.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    nops.cancel();
                }
            });
        nops.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    try {
                        // clearing app data
                        String packageName = getApplicationContext().getPackageName();
                        Runtime runtime = Runtime.getRuntime();
                        runtime.exec("pm clear "+packageName);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        nops.show();
    }


    
    public void settings(View v)
    {
        Intent intentSettings = new Intent(SocksHttpMainActivity.this, ConfigGeralActivity.class);
        //intentSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentSettings);
    }
  
    
	

	protected void showBoasVindas() {
		new AlertDialog.Builder(this, R.style.HiroDialog)
            . setTitle(R.string.attention)
            . setMessage(R.string.first_start_msg)
			. setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface di, int p) {
					// ok
				}
			})
			. setCancelable(false)
            . show();
	}

	@Override
	public void updateState(final String state, String msg, int localizedResId, final ConnectionStatus level, Intent intent)
	{
		mHandler.post(new Runnable() {
				@Override
				public void run() {
					doUpdateLayout();
					if(SkStatus.isTunnelActive()){  
						if(level.equals(ConnectionStatus.LEVEL_CONNECTED)) {
							if (!mShown){
								Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_SHORT).show();
								mShown = true;
							}
							status.setText(R.string.state_connected);
                            status.setTextColor(Color.parseColor("#ff63a742"));
                            connectionStatus.setText(" STOP");

                            inputPwShowPass.setEnabled(false); 
							inputPwServer.setEnabled(false);
							inputPwUser.setEnabled(false); 
							inputPwPass.setEnabled(false); 
							

							if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
								bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
							}


						}
						if(level.equals(ConnectionStatus.LEVEL_NOTCONNECTED)){

							status.setText(R.string.state_disconnected);
                            connectionStatus.setText("START");


						}
						if(level.equals(ConnectionStatus.LEVEL_CONNECTING_SERVER_REPLIED)){
							status.setText( R.string.state_auth);


						}
						if(level.equals(ConnectionStatus.LEVEL_CONNECTING_NO_SERVER_REPLY_YET)){
							status.setText(R.string.state_connecting);
							status.setTextColor(Color.parseColor("#ff00c5d6"));

							inputPwShowPass.setEnabled(true); 
                            inputPwServer.setEnabled(true);
							inputPwUser.setEnabled(true); 
							inputPwPass.setEnabled(true); 
							if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
								bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
							}




						}
						if(level.equals(ConnectionStatus.UNKNOWN_LEVEL)){
							if (!mShown2){
								Toast.makeText(getApplicationContext(),"Disconnect",Toast.LENGTH_SHORT).show();
								mShown2 = true;
							}		
							status.setText(R.string.state_disconnected);
                            status.setTextColor(Color.parseColor("#FFFF0000")); 
                            connectionStatus.setText("START");
							
							inputPwShowPass.setEnabled(true); 
							inputPwServer.setEnabled(true);
							inputPwUser.setEnabled(true); 
							inputPwPass.setEnabled(true); 
						}
						
							


							if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
								bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

							}

						}

					
					if(level.equals(ConnectionStatus.LEVEL_NONETWORK)){
						status.setText(R.string.state_nonetwork);
                        connectionStatus.setText("MENGHUBUNGKAN");

					}
					if(level.equals(ConnectionStatus.LEVEL_AUTH_FAILED)){
						status.setText(R.string.state_auth_failed);
                        connectionStatus.setText("RECONNECT");
					}

				}
			});
		switch (state) {
			case SkStatus.SSH_CONECTADO:
				// carrega ads banner
				if (adsBannerView != null && TunnelUtils.isNetworkOnline(SocksHttpMainActivity.this)) {
					adsBannerView.setAdListener(new AdListener() {
							@Override
							public void onAdLoaded() {
								if (adsBannerView != null && !isFinishing()) {
									adsBannerView.setVisibility(View.VISIBLE);
								}
							}
						});
					adsBannerView.postDelayed(new Runnable() {
							@Override
							public void run() {
								// carrega ads interestitial
								AdsManager.newInstance(getApplicationContext())
									.loadAdsInterstitial();
								// ads banner
								if (adsBannerView != null && !isFinishing()) {
									adsBannerView.loadAd(new AdRequest.Builder()
														 .build());
								}
							}
						}, 5000);
				}
				break;
		}
	}


	/**
	 * Recebe locais Broadcast
	 */

	private BroadcastReceiver mActivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null)
                return;

            if (action.equals(UPDATE_VIEWS) && !isFinishing()) {
				doUpdateLayout();
			}
        }
    };


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			
			case R.id.miExit:
				if (Build.VERSION.SDK_INT >= 16) {
					finishAffinity();
				}

				System.exit(0);
				break;
		}

		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onBackPressed() {
	

		if (SkStatus.isTunnelActive()) {


			new SweetAlertDialog(SocksHttpMainActivity.this, SweetAlertDialog.WARNING_TYPE)
				.setTitleText("Disconnect VPN")
				.setContentText("Are you sure you want to disconnect?")
				.setConfirmText("Disconnect")
				.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {

					@Override
					public void onClick(SweetAlertDialog sweetAlertDialog)
					{
						// TODO: Implement this method
						TunnelManagerHelper.stopSocksHttp(SocksHttpMainActivity.this);

						sweetAlertDialog.dismiss();

					}})
				.setCancelText("Cancel")
				.showCancelButton(true)
				.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener(){

					@Override
					public void onClick(SweetAlertDialog sweetAlertDialog)
					{
						sweetAlertDialog.dismiss();
					}


				})

				.show();
		}

}

    
	@Override
    public void onResume() {
        super.onResume();
	//	showInterstitial();
		SharedPreferences sPrefs = mConfig.getPrefsPrivate();
        int server = sPrefs.getInt("LastSelectedServer", 0);
		ensureServerSpinnerInitialized();
		if (serverSpinner != null) {
			serverSpinner.setSelection(server);
		}


		

        if (adsBannerView != null) {
            adsBannerView.resume();
        }
		SkStatus.addStateListener(this);
		if (adsBannerView != null) {
			adsBannerView.resume();
		}
		new Timer().schedule(new TimerTask()
			{
				@Override
				public void run()
				{
					runOnUiThread(new Runnable()
						{
							@Override
							public void run()
							{
								updateHeaderCallback();
							}
						});
				}
			}, 0,1000);
    }

	@Override
	protected void onPause()
	{
		super.onPause();
		doSaveData();
		SkStatus.removeStateListener(this);
		if (adsBannerView != null) {
			adsBannerView.pause();
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		SharedPreferences prefs = mConfig.getPrefsPrivate();
        SharedPreferences.Editor edit = prefs.edit();
        int server = serverSpinner.getSelectedItemPosition();
        edit.putInt("LastSelectedServer", server);
        edit.apply();

		if (adsBannerView != null) {
			adsBannerView.destroy();
		}
	}

	public static void updateMainViews(Context context) {
		Intent updateView = new Intent(UPDATE_VIEWS);
		LocalBroadcastManager.getInstance(context)
			.sendBroadcast(updateView);
	}

	
	
	
	
	
    
	public void Changelogs1(View v)
	{
        this.nops =  new SweetAlertDialog(SocksHttpMainActivity.this, SweetAlertDialog.SUCCESS_TYPE);
        this.nops.setTitleText("Message");
        this.nops.setContentText(this.config.geNote());
        this.nops.show();

    }


	public void offlinez(View v)
    {
        if (drawerLayout == null) {
			drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
		}
		if (drawerLayout != null) {
			drawerLayout.openDrawer(GravityCompat.START);
			return;
		}

		// Fallback for layouts without drawer (e.g. activity_home)
		PopupMenu menu = new PopupMenu(this, v);
		menu.getMenu().add(0, R.id.settings, 0, "SETTINGS");
		menu.getMenu().add(0, R.id.update_app, 1, "UPDATE APP");
		menu.getMenu().add(0, R.id.sk_restore, 1, "RESTORE");
		menu.getMenu().add(0, R.id.sk_exit, 2, "EXIT");
		menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					switch (item.getItemId()) {
						case R.id.settings:
							Intent intentSettings = new Intent(SocksHttpMainActivity.this, ConfigGeralActivity.class);
							startActivity(intentSettings);
							overridePendingTransition(R.anim.up_enter,R.anim.up_exit);
							return true;
						case R.id.update_app:
							updateapp(true);
							return true;
						case R.id.sk_restore:
							sk_restore();
							return true;
						case R.id.sk_exit:
							exit();
							return true;
						default:
							return false;
					}
				}
			});
		menu.show();
	}

	public void showExitDialog() {
		
        }
        }
