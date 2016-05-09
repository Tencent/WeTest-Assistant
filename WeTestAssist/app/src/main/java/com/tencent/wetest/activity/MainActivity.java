/*******************************************************************************
 * Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.tencent.wetest.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.tencent.wefpmonitor.R;
import com.tencent.wetest.common.adapter.TabAdapter;
import com.tencent.wetest.common.application.WTApplication;
import com.tencent.wetest.common.manager.SystemBarTintManager;
import com.tencent.wetest.fragment.HomePageFragment;
import com.tencent.wetest.fragment.LogPageFragment;

import java.util.ArrayList;
import java.util.List;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {


    private ViewPager mTabControl;
    //private PageViewAdapter pageAdapter;
    private List<Fragment> mPageList;

    private Toolbar toolbar;

    private String mNick;
    private String mFace;

    private ImageView user_pic;
    private TextView user_nick;
    private Bitmap bitmap;
    private RelativeLayout textEntryView;
    private long mExitTime;
    private boolean isRunning;

    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.per_main_layout);

        isRunning = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(Color.parseColor("#3367d6"));
            tintManager.setStatusBarTintEnabled(true);
        }

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               onBackPressed();
            }
        });

        ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle(getResources().getString(R.string.app_name_description));

        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.title_performance));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.title_record));
        tabLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        HomePageFragment homepage = new HomePageFragment();
        LogPageFragment logpage = new LogPageFragment();

        List<Fragment> tabList = new ArrayList<Fragment>();
        tabList.add(homepage);
        tabList.add(logpage);

        TabAdapter adapter = new TabAdapter(getSupportFragmentManager(),tabList);

        mTabControl =  (ViewPager) findViewById(R.id.tabContent);
        mTabControl.setAdapter(adapter);
        mTabControl.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mTabControl.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        //Logger.info("MainActivity onResume");
    }

    @Override
    protected void onPause() {
        isRunning = false;
        super.onPause();
    }

    public boolean isRunning(){

        return  isRunning;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if(((WTApplication)WTApplication.getContext()).isTestStart()){
                Toast.makeText(WTApplication.getContext(), getResources().getString(R.string.test_not_complete), Toast.LENGTH_SHORT).show();

            }else{

                if ((System.currentTimeMillis() - mExitTime) > 2000) {

                    Toast.makeText(WTApplication.getContext(), getResources().getString(R.string.one_more_time_exit), Toast.LENGTH_SHORT).show();

                    mExitTime = System.currentTimeMillis();

                } else {

                    //System.exit(0);
//                    ActivityManager am = (ActivityManager)getSystemService (Context.ACTIVITY_SERVICE);
//                    am.restartPackage(getPackageName());
                    finish();


//
//	                	if(((WeFPApplication)WeFPApplication.getContext()).isRoot()){
//
//	                		ProcMemory.getProcMem(-5);

//	    	                FPSManager.disinjectFps();

//	    				}

                    //Logger.info("kill my pid : " + android.os.Process.myPid());
                    android.os.Process.killProcess(android.os.Process.myPid());

                }

            }


            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
