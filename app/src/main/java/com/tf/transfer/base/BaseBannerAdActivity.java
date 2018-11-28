package com.tf.transfer.base;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;
import com.tf.transfer.BuildConfig;
import com.tf.transfer.R;
import com.tf.transfer.constant.PermissionConstant;
import com.tf.transfer.util.AppUtil;
import com.tf.transfer.util.CustomParamManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangyue
 * @date 2018/11/17 18:53
 * @Description Banner广告基类，用于处理加载Banner广告的逻辑
 */
public abstract class BaseBannerAdActivity extends AppCompatActivity {

    private FrameLayout adContainer;
    private BannerView bannerView;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_base_banner_ad);
        FrameLayout frameLayout = findViewById(R.id.base_ad_main_container);
        frameLayout.addView(getLayoutInflater().inflate(layoutResID, null), ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        adContainer = findViewById(R.id.base_ad_container);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 判断是否允许加载广告
        if (isShowAD())
            checkAndRequestAdPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PermissionConstant.PERMISSION_REQUEST_CODE_FOR_GDT_AD == requestCode) {
            for (int result : grantResults) {
                if (PackageManager.PERMISSION_GRANTED != result) {
                    // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
                    Toast.makeText(this, "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                    return;
                }
            }
            initBannerAd();
        }
    }

    /**
     * 检查加载广点通广告的权限
     */
    public void checkAndRequestAdPermission() {
        List<String> lackedPermission = new ArrayList<>();
        if (!AppUtil.checkPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!AppUtil.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!AppUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // 权限都已经有了，那么直接调用SDK
        if (lackedPermission.size() == 0) {
            initBannerAd();
        } else {
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            ActivityCompat.requestPermissions(this, requestPermissions, PermissionConstant.PERMISSION_REQUEST_CODE_FOR_GDT_AD);
        }
    }

    private void initBannerAd() {
        if (adContainer == null || bannerView != null) return;
        bannerView = new BannerView(this, ADSize.BANNER, BuildConfig.GDTAdIdAppId, BuildConfig.GDTAdIdhomeBanner);
        bannerView.setRefresh(30);
        bannerView.setShowClose(true);
        bannerView.setADListener(new AbstractBannerADListener() {

            @Override
            public void onNoAD(AdError error) {
            }

            @Override
            public void onADReceiv() {
            }

            @Override
            public void onADClosed() {
                super.onADClosed();
                adContainer.removeAllViews();
                bannerView.destroy();
                bannerView = null;
            }

        });
        adContainer.addView(bannerView);
        bannerView.loadAD();
    }

    public boolean isShowAD() {
        // 从自定义参数中获取，默认是false
        return CustomParamManager.isShowAd();
    }
}
