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

package com.tencent.wetest.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;

import com.tencent.wetest.common.application.WTApplication;
import com.tencent.wetest.common.log.Logger;
import com.tencent.wetest.common.manager.ApkManager;
import com.tencent.wetest.common.manager.ProcParamManager;
import com.tencent.wetest.common.manager.ProcStatusManager;
import com.tencent.wetest.common.model.ApkInfo;
import com.tencent.wetest.common.model.ProcMemInfo;
import com.tencent.wetest.common.model.Report;
import com.tencent.wetest.common.model.ReportData;
import com.tencent.wetest.common.util.CurrentUtil;
import com.tencent.wetest.common.util.ReportUtil;
import com.tencent.wetest.common.util.SuUtil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.NumberFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

public class StasticService extends Service {

    private ApkInfo apkinfo;
    private Report report;
    private boolean firstData;
    private double firstTotalCpu;
    private double firstProcessCpu;
    private double firstNetworkIn;
    private double firstNetworkOut;

    private boolean flag ;
    private boolean fpsFlag ;

    private Date startTime;
    private final int BUFFSIZE = 4096;
    public byte[] szRecvData = new byte[BUFFSIZE];
    private ReentrantLock lock = new ReentrantLock();

    private ServerSocket server;
    private Socket s;

    private int recevierCurrent = 0;
    private int recevierTemp = 0;
    @Override
    public void onCreate() {

        super.onCreate();

        flag = true;
        fpsFlag = true;
        apkinfo = ((WTApplication)getApplication()).getApkInfo();

//		Thread fpsThread = new Thread(new FPSCollect());
//		fpsThread.start();

        Thread dataThread = new Thread(new DataCollect());
        dataThread.start();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void dataRefresh() {

        try{

            // String pidInfo =   apkinfo.getPackagename() == null ? "-1" : "" + ApkManager.getPidByPackageName(apkinfo.getPackagename())[0];

            //Logger.info("current game is: "+ apkinfo.getPackagename() + "pid is:" + pidInfo);

            if( apkinfo.getPackagename() == null || ApkManager.getPidByPackageName(apkinfo.getPackagename())[0] == -1 ){

                return ;

            }

            Date currTime = new Date();
            if((currTime.getTime() - startTime.getTime()) >= 60000){

                ReportUtil.saveReport();

                startTime = new Date();

                ((WTApplication)WTApplication.getContext()).getReport().getDatalist().clear();
            }

            int pid = ApkManager.getPidByPackageName(apkinfo.getPackagename())[0];
            int uid = ApkManager.getPidByPackageName(apkinfo.getPackagename())[1];


            ProcMemInfo pMem = ProcParamManager.getProcMem(pid);

            if(firstData){

                firstTotalCpu = ProcStatusManager.getTotalCpuTime();

                firstProcessCpu = ProcStatusManager.getProcessCpuTime(pid);

                firstNetworkIn = ProcStatusManager.getNetworkUsage(uid)[0];

                firstNetworkOut = ProcStatusManager.getNetworkUsage(uid)[1];
                firstData = false;

                Thread.sleep(2000);

            }

            ReportData reportdata = new ReportData();
            String tag = ((WTApplication)getApplication()).getTag();
            reportdata.setTag(tag);

            if(ProcStatusManager.getTotalCpuTime() != firstTotalCpu  ){

                double res = (ProcStatusManager.getProcessCpuTime(pid)-firstProcessCpu)*100/(ProcStatusManager.getTotalCpuTime()-firstTotalCpu);

                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMaximumFractionDigits(2);

                if(res > 0 && res <=100)
                    reportdata.setCpu(Double.parseDouble(nf.format(res)));
                else
                    reportdata.setCpu(0);

            }else{
                reportdata.setCpu(0);
            }


            reportdata.setpDalvik(pMem.getDalvik());
            reportdata.setpNative(pMem.getNatived());
            reportdata.setpTotal(pMem.getTotal());

            long netIn = ProcStatusManager.getNetworkUsage(uid)[0];
            long netOut = ProcStatusManager.getNetworkUsage(uid)[1];

            double dnetin = (netIn - firstNetworkIn);
            reportdata.setpNetworUsagekIn(dnetin < 0 || dnetin > 999999 ? 0 : dnetin);
            double dnetout = (netOut - firstNetworkOut);
            reportdata.setpNetworUsagekOut(dnetout < 0 || dnetout > 999999 ? 0 : dnetout);
            firstNetworkIn = netIn;
            firstNetworkOut = netOut;

            Double temperature = CurrentUtil.getTemprature();

            if( temperature != 0 ){

                reportdata.setpTemperature(""+Math.round(temperature * 100));

            }else{

                reportdata.setpTemperature(""+Math.round(recevierTemp * 100));

            }

            Long currentBattValue = CurrentUtil.getValue();

            Long battValue = currentBattValue != null ? currentBattValue : 0;

            long capacity = battValue < 0 ? -battValue : battValue;

            //Logger.debug("capacity is : " + capacity);

            reportdata.setpCurrent(String.valueOf(capacity));


            try{

                lock.lock();

                reportdata.setFps(((WTApplication) WTApplication.getContext()).isRoot()?pMem.getFps():0);


            }catch(Exception e){

                Logger.error(""+e.toString());

            }finally{

                lock.unlock();

            }

            if(report.getBaseTime() != -1){

                reportdata.setTime(report.getBaseTime() + (SystemClock.uptimeMillis() - report.getBaseColock()));

            }else{

                reportdata.setTime(SystemClock.uptimeMillis());

            }

            if(!tag.equals("")){

                ((WTApplication)getApplication()).setTag("");

            }

            report.getDatalist().add(reportdata);

            firstTotalCpu = ProcStatusManager.getTotalCpuTime();
            firstProcessCpu = ProcStatusManager.getProcessCpuTime(pid);

            Message m = handler.obtainMessage();
            m.what = reportdata.getFps();
            handler.sendMessage(m);

        }catch(Exception e){

            Logger.error("dataRfreshError Exception :"+e.toString());
            e.printStackTrace();

        }

    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if(FloatViewService.fps != null){

                if(msg.what >=0 )
                    FloatViewService.fps.setText("FPS : " + (msg.what < 10 ? "0" + msg.what : msg.what));
                else
                    FloatViewService.fps.setText("FPS : --");

            }

        }
    };

    private Handler fps_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FloatViewService.fps.setVisibility(View.VISIBLE);
        }
    };

    class DataCollect implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            startTime = new Date();
            firstData = true;
            report = ((WTApplication)getApplication()).getReport();

            while(flag){

                try {

                    dataRefresh();
                    Thread.sleep(1000);

                } catch (Exception e) {

                    Logger.error("staThreadError:" + e.toString());
                    e.printStackTrace();

                }

            }

            Message m = handler.obtainMessage();
            m.what = -1;
            handler.sendMessage(m);
        }

    }

    public void notifyBatteryChanged(int current , int temp){

        recevierCurrent = current;
        recevierTemp = temp;

    }

    @Override
    public void onDestroy() {

        //SdkConnectManager.getInstance().clear();

        // RealTimeTestManager.getInstance().finishPerformanceTestReq(getApplicationContext());


        flag = false;
        fpsFlag = false;

        try {

            if(s != null)
                s.close();

            if(server != null)
                server.close();

        } catch (IOException e) {

            e.printStackTrace();

        }

        String cmd = "logcat -d -v time > /data/data/com.tencent.wefpmonitor/files/crash.log";
        SuUtil.executeCommand(1, cmd);

        super.onDestroy();

    }

}
