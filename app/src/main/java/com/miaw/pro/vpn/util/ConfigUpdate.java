package com.miaw.pro.vpn.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import com.miaw.pro.R;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.miaw.pro.tunnel.logger.SkStatus;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class ConfigUpdate extends AsyncTask<String, String, String> {

    private Context context;
    private OnUpdateListener listener;
    private ProgressDialog progressDialog;
    private boolean isOnCreate;
	private SweetAlertDialog pDialog;

	private String update = "https://bitbin.it/dG0tUXsF/raw/";

    public ConfigUpdate(Context context, OnUpdateListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void start(boolean isOnCreate) {
        this.isOnCreate = isOnCreate;
        execute();
    }

    public interface OnUpdateListener {
        void onUpdateListener(String result);
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            StringBuilder sb = new StringBuilder();
            URL url = new URL(update);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response;

            while ((response = br.readLine()) != null) {
                sb.append(response);
            }
            return sb.toString();
        } catch (Exception e) {
         //   SkStatus.logInfo(e.getMessage());
            return "Error on getting data: " + e.getMessage();
        }
    }

	@Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (!isOnCreate) {
            pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
				.setTitleText("Checking Update...");
			//       progressDialog.setTitle("Checking Update");
			pDialog.show();
			pDialog.setCancelable(true);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (!isOnCreate && pDialog != null) {
            pDialog.dismiss();
        }
        if (listener != null) {
            listener.onUpdateListener(s);
        }
    }
}
