package com.tf.transfer.business;

import android.content.Context;
import android.util.Log;

import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;
import com.tf.transfer.BuildConfig;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author huangyue
 * @date 2018/11/20 22:50
 * @Description
 */
public class LoadAdManager {

    private static NativeExpressADView exitAppAd;

    public static void loadExitAppAd(Context context) {
        NativeExpressAD nativeExpressAD = new NativeExpressAD(context
                , new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT), BuildConfig.GDTAdIdAppId
                , BuildConfig.GDTAdIdExitAppNative, new NativeExpressAD.NativeExpressADListener() {
            @Override
            public void onADLoaded(List<NativeExpressADView> list) {
                if (list != null && !list.isEmpty()) {
                    exitAppAd = list.get(0);
                    exitAppAd.render();
                }
            }

            @Override
            public void onRenderFail(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onRenderSuccess(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADExposure(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADClicked(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADClosed(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADLeftApplication(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {

            }

            @Override
            public void onNoAD(AdError adError) {

            }
        });
        nativeExpressAD.loadAD(1);
    }

    public static NativeExpressADView getAdView() {
        return exitAppAd;
    }

}
