package com.miaw.pro.vpn;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.miaw.pro.R;
import java.util.Objects;

public class MainActivityWebView extends AppCompatActivity{

    private Toolbar toolbar;
    private WebView webView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout refreshWebView;
	public static String Title = "";
	public static String url_key = "";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        webView = (WebView) findViewById(R.id.webView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        refreshWebView = (SwipeRefreshLayout) findViewById(R.id.webViewRefresh);

        setSupportActionBar(toolbar);   // Set toolbar to action bar
        getSupportActionBar().setTitle(Title);    //Set a Text to Toolbar
		//getSupportActionBar().setSubtitle(url_key);     //Set Url as a Subtitle
		// getSupportActionBar().setDisplayShowTitleEnabled(true);     //Set Title Enable
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*Set a Listener when Click on back icon on toolbar*/
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					/**finish when click on back icon**/
					finishAfterTransition();
				}
			});

        initWebView();
        webView.loadUrl(url_key);
        refreshWebView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
				@Override
				public void onRefresh() {
					refreshWebView.setRefreshing(true);
					webView.reload();
					refreshWebView.setRefreshing(false);
				}
			});
	}

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        /**Zoom Enable**/
		webView.getSettings().setSupportZoom(true);
        /**Enable Build in Controls to Zoom**/
        webView.getSettings().setBuiltInZoomControls(true);
        /**Set Web Chrome Client**/
        webView.setWebChromeClient(new MyWebChromeClient(this));
        /**Set WebView Client**/
        webView.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					super.onPageStarted(view, url, favicon);
					/**Show the Progress when page is loading/starting**/
					progressBar.setVisibility(View.VISIBLE);
				}

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String newURL) {
					/**When Click on any link load the url on webview**/
					webView.loadUrl(newURL);
					url_key = newURL;
					/**Set the current page url to toolbar as a subtitle**/
					Objects.requireNonNull(getSupportActionBar()).setSubtitle(newURL);
					return true;
				}

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					/**visible gone to progressbar when page is loaded**/
					progressBar.setVisibility(View.GONE);
				}

				@Override
				public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
					super.onReceivedError(view, request, error);
					/**visible gone progressbar when there is an error on loading url and show toast**/
					progressBar.setVisibility(View.GONE);
					//Toast.makeText(MainActivityWebView.this, "Error Loading "+h.ContactSupport, Toast.LENGTH_SHORT).show();
				}
			});
        webView.clearCache(true);   /**Clear Cache**/
        webView.clearHistory(); /**Clear History**/
        webView.getSettings().setJavaScriptEnabled(true);   /**Enable JavaScript which enable animation,etc**/
        webView.setHorizontalScrollBarEnabled(true); /**Set Horizontal Scrolling Enable**/
    }



    private class MyWebChromeClient extends WebChromeClient {
        Context context;
        public MyWebChromeClient(Context context) {
			super();
			this.context = context;
		}
    }
	/*
	 @Override
	 public void onBackPressed() {
	 if (webView.canGoBack()) {
	 webView.goBack();
	 } else {
	 super.onBackPressed();
	 }
	 }*/
}
 

