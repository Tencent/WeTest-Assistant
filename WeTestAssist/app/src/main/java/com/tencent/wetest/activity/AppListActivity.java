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

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import com.tencent.wefpmonitor.R;
import com.tencent.wetest.common.adapter.AppListAdapter;
import com.tencent.wetest.common.application.WTApplication;
import com.tencent.wetest.common.manager.ApkManager;
import com.tencent.wetest.common.manager.SystemBarTintManager;
import com.tencent.wetest.common.model.APPInfo;
import com.tencent.wetest.common.model.ApkInfo;
import com.tencent.wetest.common.util.ConstantUtil;

import java.util.ArrayList;
import java.util.List;

public class AppListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView mListView;
    private AppListAdapter mAdapter;
    private ApkInfo mApkinfo;
    private PackageManager mManager;
    private List<APPInfo> baseList;
    private List<APPInfo> adpList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(Color.parseColor("#3367d6"));
            tintManager.setStatusBarTintEnabled(true);
        }

        baseList = ApkManager.getInstalledApp();

        adpList = new ArrayList<APPInfo>();

        mApkinfo = ((WTApplication)getApplication()).getApkInfo();

        mManager = (PackageManager)WTApplication.getContext().getPackageManager();

        mListView = (ListView) findViewById(R.id.applist);

        mAdapter = new AppListAdapter(AppListActivity.this, R.layout.app_list_item, adpList);

        filterAdpList("");

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(onItemClick);

    }

    private void filterAdpList(String text){

        adpList.clear();
        for(APPInfo mapItem : baseList){
            if((mapItem.getAppName()).toString().toLowerCase().contains(text) || text.equals("") ){

                adpList.add(mapItem);
            }
        }

        mAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_app_list, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) AppListActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(AppListActivity.this.getComponentName()));
        }

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filterAdpList(newText);
                    return false;
                }
            });
        }

        return true;
    }

    private AdapterView.OnItemClickListener onItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            @SuppressWarnings("unchecked")
            APPInfo map = (APPInfo) mListView.getItemAtPosition(arg2);

            String name = (String) map.getPackageName();

            PackageInfo info = ApkManager.getPackageInfoByPackageName(name);

            mApkinfo.setAppic(info.applicationInfo.loadIcon(mManager));

            mApkinfo.setAppname(info.applicationInfo.loadLabel(mManager).toString());

            mApkinfo.setPackagename(info.packageName);

            mApkinfo.setVersionName(info.versionName);

            Intent intent = new Intent(AppListActivity.this,MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);  //注意本行的FLAG设置

            intent.putExtra("flag", 0);

            intent.putExtra("refresh", ConstantUtil.UPDATE_MAINPAGE);

            startActivity(intent);

            finish();

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
