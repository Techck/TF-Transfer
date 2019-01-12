package com.tf.transfer.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.hwangjr.rxbus.RxBus;
import com.tencent.stat.StatService;

/**
 * @author huangyue
 * @date 2018/11/15 23:03
 * @Description
 */

public class BaseFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBus.get().register(this);
    }

    @Override
    public void onDestroy() {
        RxBus.get().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.trackBeginPage(getContext(), getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        StatService.trackEndPage(getContext(), getClass().getSimpleName());
    }
}
