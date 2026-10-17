package com.segadroid.parkingescapepuzzle;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebViewAssetLoader;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class MainActivity extends AppCompatActivity {
    private WebViewAssetLoader assetLoader;
    WebView browser;
    AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private RewardedAd mRewardedAd;
    SharedPreferences sharedpreferences;
    boolean ispro = false;
    boolean intCanShow = false;
    boolean bannerCanShow = false;

    private Handler initAdHandler = new Handler();
    private boolean initAdLoopRunning = false;
    private Runnable initAdRunnable;

    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context context) {
            this.mContext = context;
        }

        @JavascriptInterface
        public void hideAndroidBanner() {
            bannerCanShow = false;
        }

        @JavascriptInterface
        public void showAndroidBanner() {
            bannerCanShow = true;
        }

        @JavascriptInterface
        public void landscape() {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        @JavascriptInterface
        public void portrait() {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        @JavascriptInterface
        public void showAd() {
            intCanShow = true;
        }

        @JavascriptInterface
        public void showRewardedAd() {
            MainActivity.this.showRewardedAd();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        assetLoader = new WebViewAssetLoader.Builder()
                .setDomain("game.local")
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        browser = findViewById(R.id.webView);
        browser.setWebChromeClient(new WebChromeClient());

        WebSettings settings = browser.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        browser.addJavascriptInterface(new WebAppInterface(this), "Android");

        browser.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                browser.setVisibility(View.VISIBLE);
                if (ispro) {
                    browser.loadUrl("javascript:integration.itsads(1)");
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                WebResourceResponse response = assetLoader.shouldInterceptRequest(request.getUrl());
                if (response != null) {
                    return response;
                }
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                browser.setVisibility(View.GONE);
                if (!url.contains("http")) {
                    view.loadUrl(url);
                    return false;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        });

        browser.loadUrl(getGame());

        MobileAds.initialize(this, initializationStatus -> {});

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        loadAd();
        loadRewardedAd();
    }

    private String getGame() {
        return "https://game.local/assets/info/file.html";
    }

    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        mInterstitialAd = null;
                        loadAd();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        mInterstitialAd = null;
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mInterstitialAd = null;
            }
        });

        mAdView = findViewById(R.id.adView);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {}
            @Override
            public void onAdClosed() {}
            @Override
            public void onAdLoaded() {}
            @Override
            public void onAdOpened() {}
        });

        sharedpreferences = getApplicationContext().getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        if (sharedpreferences.contains("ispro")) {
            ispro = sharedpreferences.getBoolean("ispro", false);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!ispro) {
                    startInitAdLoop();
                    browser.loadUrl("javascript:integration.itsads(0)");
                } else {
                    browser.loadUrl("javascript:integration.itsads(1)");
                }
            }
        }, 5000);
    }

    private void loadRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917", adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                mRewardedAd = rewardedAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mRewardedAd = null;
            }
        });
    }

    public void showInterstitial() {
        runOnUiThread(() -> {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(MainActivity.this);
            }
        });
    }

    public void showRewardedAd() {
        runOnUiThread(() -> {
            if (mRewardedAd != null) {
                mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        browser.post(() -> browser.evaluateJavascript("window.adClosed();", null));
                        mRewardedAd = null;
                        loadRewardedAd();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        browser.post(() -> browser.evaluateJavascript("window.adClosed();", null));
                        mRewardedAd = null;
                        loadRewardedAd();
                    }
                });
                mRewardedAd.show(MainActivity.this, rewardItem -> {
                    browser.post(() -> browser.evaluateJavascript("window.rewardGranted();", null));
                });
            } else {
                browser.post(() -> browser.evaluateJavascript("window.rewardGranted(); window.adClosed();", null));
            }
        });
    }

    public void showBanner() {
        if (mAdView.getVisibility() != View.VISIBLE) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            mAdView.setVisibility(View.VISIBLE);
        }
    }

    public void hideBanner() {
        mAdView.setVisibility(View.GONE);
    }

    public void initAd() {
        if (intCanShow) {
            showInterstitial();
            intCanShow = false;
        }
        if (bannerCanShow) {
            showBanner();
        } else {
            hideBanner();
        }
    }

    private void startInitAdLoop() {
        if (initAdLoopRunning) {
            return;
        }
        initAdLoopRunning = true;
        initAdRunnable = new Runnable() {
            @Override
            public void run() {
                initAd();
                initAdHandler.postDelayed(this, 1000);
            }
        };
        initAdHandler.post(initAdRunnable);
    }

    private void stopInitAdLoop() {
        initAdLoopRunning = false;
        if (initAdRunnable != null) {
            initAdHandler.removeCallbacks(initAdRunnable);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!ispro && initAdRunnable != null) {
            startInitAdLoop();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopInitAdLoop();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView webView = findViewById(R.id.webView);
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void launchMarket() {
        try {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    public void launchSharer(String text, String subject) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        String fullText = text + "\n\n" + "https://play.google.com/store/apps/details?id=" + getPackageName() + "\n\n";
        intent.putExtra(Intent.EXTRA_TEXT, fullText);
        startActivity(Intent.createChooser(intent, "Choose one"));
    }

    public void launchTextSharer(String text, String subject) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(intent, "Choose one"));
    }
}
