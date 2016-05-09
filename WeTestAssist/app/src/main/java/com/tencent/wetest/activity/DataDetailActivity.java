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

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.tencent.wefpmonitor.R;
import com.tencent.wetest.common.application.WTApplication;
import com.tencent.wetest.common.log.Logger;
import com.tencent.wetest.common.manager.SystemBarTintManager;
import com.tencent.wetest.common.util.ReportUtil;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;


public class DataDetailActivity extends AppCompatActivity {

    private TextView tvDataDetail , tvFilePathName;
    private String mLogName;
    private File mLogFile;
    private String dataContent;

    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_detail);

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

        tvDataDetail = (TextView)findViewById(R.id.dataDetail);



        Intent intent = this.getIntent();

        if( intent != null && intent.hasExtra("filename") ) {
            mLogName = intent.getStringExtra("filename");

        }

        String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wetest";
        mLogFile = new File(fileDir + "/" + mLogName);

        tvFilePathName = (TextView)findViewById(R.id.filePathName);
        tvFilePathName.setText(mLogFile.getAbsolutePath());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dataContent =  ReportUtil.readFileContent(mLogFile);
                } catch (Exception e) {

                    Logger.error("readFileContent error :" + e.toString());
                    e.printStackTrace();
                }

                Message msg = mHandler.obtainMessage();
                mHandler.sendMessage(msg);
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_detail, menu);
        return true;
    }

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

    private Handler mHandler = new ReadFileContentHandler(this);
    static class ReadFileContentHandler extends Handler{
        private final WeakReference<DataDetailActivity> mActivity;
        private final DataDetailActivity mContext;
        public ReadFileContentHandler(DataDetailActivity activity) {
            mActivity = new WeakReference<DataDetailActivity>(activity);
            mContext = activity;
        }
        @Override
        public void handleMessage(Message msg) {

            mContext.tvDataDetail.setText(mContext.dataContent);
        }
    }
}
