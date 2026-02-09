package com.miaw.pro.vpn;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import com.miaw.pro.tunnel.SocksHttpService;
import com.miaw.pro.R;
import java.util.Timer;
import java.util.TimerTask;

public class TorrentDetection
{

	int UNINSTALL_REQUEST_CODE = 1;
	private Context context;
	private String[] items;

	public TorrentDetection(Context c, String[] i) {
		context = c;
		items = i;
	}

	private boolean check(String uri)
	{
        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try
		{
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
		catch (PackageManager.NameNotFoundException e)
		{
            app_installed = false;
        }
        return app_installed;
    }

	void check() {
		for (int i=0;i < items.length ;i++)
		{
			if(check(items[i])){
				alert(items[i]);
				break;
			}
		}
	}

	public void init() {
		final Handler handler = new Handler();
		Timer timer = new Timer();
		TimerTask doAsynchronousTask = new TimerTask() {
			@Override
			public void run()
			{
				handler.post(new Runnable() {
						public void run()
						{
							check();
						}
					});
			}
		};
		timer.schedule(doAsynchronousTask, 0, 3000);
	}

	void alert(String app) {
		if (SocksHttpService.isRunning)
		{
			context.stopService(new Intent(context, SocksHttpService.class));
		}

		new AlertDialog.Builder(context)
			.setCancelable(false)
			.setIcon(R.drawable.ic_launcher)
			.setTitle("Torrent App Detected")
			.setMessage("Bang!!! Huli ka balbon.\nDetected Torrenting App Installed!\n"+app)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface p1, int p2)
				{

					System.exit(0);
					// TODO: Implement this method
				}
			}).create().show();
	}
}
