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

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.wefpmonitor.R;
import com.tencent.wetest.common.application.WTApplication;
import com.tencent.wetest.common.log.Logger;
import com.tencent.wetest.common.manager.SystemBarTintManager;
import com.tencent.wetest.common.model.TestRecord;
import com.tencent.wetest.common.util.ConstantUtil;
import com.tencent.wetest.common.util.ReportUtil;
import com.tencent.wetest.common.util.SuUtil;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;

import java.io.File;
import java.lang.ref.WeakReference;

public class TestResultsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppCompatButton dataDetailButton;
    private String mLogName;
    private File mLogFile;
    private LinearLayout testResultsLayout;
    private TestRecord mRecord;
    private boolean mDelLock = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_results);

        testResultsLayout = (LinearLayout) findViewById(R.id.test_results_Layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(onBackClick);

        Intent intent = this.getIntent();

        if( intent != null && intent.hasExtra("filename") ) {
            mLogName = intent.getStringExtra("filename");
        }
        else {
            Snackbar.make(testResultsLayout, this.getResources().getString(R.string.no_result), Snackbar.LENGTH_SHORT).show();
            toolbar.performClick();
            return;
        }

        if(!intent.getBooleanExtra("isLogDetail" ,false)) {

            if (!intent.getBooleanExtra("unexpectStop", false)) {

                Snackbar.make(testResultsLayout,this.getResources().getString(R.string.game_exit), Snackbar.LENGTH_SHORT).show();

            }

        }

        String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wetest";
        mLogFile = new File(fileDir + "/" + mLogName);


        dataDetailButton = (AppCompatButton) findViewById(R.id.btn_dataDetail);

        dataDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestResultsActivity.this , DataDetailActivity.class);
                intent.putExtra("filename" , mLogName);
                startActivity(intent);
            }
        });

        if (mLogName != null) {

            mRecord = ReportUtil.readTestRecordByFile(mLogName);

            if (mLogFile.exists()) {

                dataDetailButton.setText(getResources().getString(R.string.show_data_detail));
                dataDetailButton.setEnabled(true);
                dataDetailButton.getBackground().setAlpha(255);

            } else {
                dataDetailButton.setText(getResources().getString(R.string.no_data_detail));
                dataDetailButton.setEnabled(false);
                dataDetailButton.getBackground().setAlpha(20);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(Color.parseColor("#3367d6"));
            tintManager.setStatusBarTintEnabled(true);
        }

        TextView phone_manu = (TextView) findViewById(R.id.test_phone_manu_content);
        phone_manu.setText(android.os.Build.MODEL);

        TextView test_time = (TextView)findViewById(R.id.test_time_content);
        test_time.setText(mRecord.getTestTime() + "S");


        TextView test_app = (TextView) findViewById(R.id.test_app_name_content);
        test_app.setText(mRecord.getAppName());


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_del) {

            if(mDelLock)
                Toast.makeText(TestResultsActivity.this, getResources().getString(R.string.executing), Toast.LENGTH_SHORT).show();
            else {

                Builder del_b = new AlertDialog.Builder(TestResultsActivity.this);
                del_b.setTitle(getResources().getString(R.string.confirm));
                del_b.setMessage(getResources().getString(R.string.del_record));
                del_b.setPositiveButton(getResources().getString(R.string.btnOK), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    mDelLock = true;
                                    ReportUtil.delRecord(mLogName);

                                } catch (Exception e) {

                                    Logger.error("onDelException: " + e.toString());
                                    String cmd = "logcat -d -v time > /data/data/com.tencent.wefpmonitor/files/crash.log";
                                    SuUtil.executeCommand(1, cmd);

                                }
                                mDelLock = false;

                                Message m = mDelHandler.obtainMessage();
                                mDelHandler.sendMessage(m);
                            }
                        }).start();

                    }

                });

                del_b.setNegativeButton(getResources().getString(R.string.cancel), null);
                AlertDialog del_d = del_b.create();
                del_d.getWindow().setType(WindowManager.LayoutParams.LAST_SUB_WINDOW);

                del_d.show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Handler mDelHandler = new DelHandler(this);
    static class DelHandler extends Handler{
        private final WeakReference<TestResultsActivity> mActivity;
        private final TestResultsActivity mContext;
        public DelHandler(TestResultsActivity activity) {
            mActivity = new WeakReference<TestResultsActivity>(activity);
            mContext = activity;
        }
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);

            Toast.makeText(mContext, mContext.getResources().getString(R.string.record_already_deleted),Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(mContext,MainActivity.class);

            intent.putExtra("flag", 1);

            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

            intent.putExtra("refresh", ConstantUtil.UPDATE_LOGPAGE);

            mContext.startActivity(intent);

            mContext.finish();
        }
    }

    private View.OnClickListener onBackClick = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {

            ((WTApplication) WTApplication.getContext()).setTestStart(false);

            Intent intent = new Intent(TestResultsActivity.this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("flag", 1);
            startActivity(intent);

            finish();

        }
    };

}
