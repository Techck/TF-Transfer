package com.tf.transfer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.tf.transfer.R;
import com.tf.transfer.base.BaseActivity;
import com.tf.transfer.fragment.SubTransferListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangyue
 * @date 2018/11/08 16:05
 * @Description
 */
public class TransferListActivity extends BaseActivity {

    public static void start(Activity activity) {
        Intent intent = new Intent(activity, TransferListActivity.class);
        activity.startActivity(intent);
    }

    private List<Fragment> listFragment;
    private String[] title = new String[]{"当前传输", "历史记录"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_list);
        findViewById(R.id.title_bar_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });
        setTitle("传输列表");
        PagerTabStrip tabStrip = (PagerTabStrip) findViewById(R.id.transfer_list_TabStrip);
        tabStrip.setTabIndicatorColorResource(R.color.theme_color);
        listFragment = new ArrayList<>();
        listFragment.add(SubTransferListFragment.create(0));
        listFragment.add(SubTransferListFragment.create(1));
        ViewPager viewPager = (ViewPager) findViewById(R.id.transfer_list_viewpager);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                return listFragment.get(position);
            }

            @Override
            public int getCount() {
                return listFragment.size();
            }

            public CharSequence getPageTitle(int position) {
                return title[position];
            }

        });
    }
}