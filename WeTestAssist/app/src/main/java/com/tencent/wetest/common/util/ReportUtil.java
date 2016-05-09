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

package com.tencent.wetest.common.util;

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.SystemClock;

import com.tencent.wefpmonitor.R;
import com.tencent.wetest.common.application.WTApplication;
import com.tencent.wetest.common.log.Logger;
import com.tencent.wetest.common.manager.ApkManager;
import com.tencent.wetest.common.model.JsonResult;
import com.tencent.wetest.common.model.Report;
import com.tencent.wetest.common.model.ReportData;
import com.tencent.wetest.common.model.TestRecord;

import org.json.JSONException;
import org.json.JSONObject;

public class ReportUtil {

    private static String path;
    private static String packageName;
    private static String appName;
    private static Report datasource;
    private static List<ReportData> datas;
    private static int size;
    private static String name;
    private static String savetime;
    private static int TIMEOUT = 15000;
    private static int READ_TIMEOUT = 15000;
    private static int retrytimes = 0;

    public static void clear(String uin) {

        path = WTApplication.getContext().getFilesDir().getPath();
        File f = new File(path + "/" + uin + "fileindex");

        if (f.exists()) {

            try {
                BufferedReader indexreader = new BufferedReader(new FileReader(f));

                String temp;
                while ((temp = indexreader.readLine()) != null) {
                    String[] content = temp.split("/");
                    File tmp = new File(path + "/" + content[0]);
                    if (tmp.exists() && tmp.isFile())
                        tmp.delete();
                }

                indexreader.close();

            } catch (Exception e) {

                e.printStackTrace();
            }

            f.delete();
        }

    }

    public static void delRecord(String name) {

        path = WTApplication.getContext().getFilesDir().getPath();

        File f = new File(path + "/wtIndex");
        String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wetest";
        File tmp = new File(fileDir + "/" + name);
        if(tmp.exists()){
            tmp.delete();
        }
        List<String> content = new ArrayList<String>();

        if (f.exists() && f.isFile()) {
            try {
                BufferedReader indexreader = new BufferedReader(new FileReader(
                        f));
                String br = "";
                while((br=indexreader.readLine()) != null){
                    if(!br.split("/")[0].equals(name))
                        content.add(br);
                }

                indexreader.close();
                if(content.size() != 0){
                    BufferedWriter indexwriter = new BufferedWriter(new FileWriter(
                            f, false));
                    int i = 0;
                    for(String temp:content){

                        if(i == content.size()-1)
                            indexwriter.write(temp );
                        else
                            indexwriter.write(temp +"\t\n");
                        i++;
                    }

                    indexwriter.flush();
                    indexwriter.close();

                }else{

                    f.delete();

                }

            } catch (Exception e) {
                Logger.error("delException:" + e.toString());
                e.printStackTrace();
            }

        }
    }

        public static void updateRecord(File f,String name, String record) {

        path = WTApplication.getContext().getFilesDir().getPath() ;

        List<String> content = new ArrayList<String>();

        if (f.exists() && f.isFile()) {

            try {

                String br = "";
                boolean hasRecord = false;
                BufferedReader indexreader = new BufferedReader(new FileReader(f));

                while((br=indexreader.readLine()) != null){
                    if(br.split("/")[0].equals(name)){

                        content.add(record);
                        hasRecord = true;
                    }else{
                        content.add(br);

                    }
                }

                if(!hasRecord)
                    content.add(record);

                indexreader.close();

                BufferedWriter indexwriter = new BufferedWriter(new FileWriter(f, false));

                if(content.size() == 0 ){

                    indexwriter.write(record);

                }else{
                    int i = 0;
                    for(String temp:content){

                        if(i == content.size()-1){
                            indexwriter.write(temp);
                        }
                        else{
                            indexwriter.write(temp + "\t\n");
                        }
                        i++;
                    }
                }

                indexwriter.flush();
                indexwriter.close();

            } catch (Exception e) {

                Logger.error("updateRecord exception : " + e.toString());
                e.printStackTrace();
            }

        }
    }

    public static void delFile(String name) {
        path = WTApplication.getContext().getFilesDir().getPath();
        File f = new File(path + "/" + name);

        if (f.exists() && f.isFile()) {
            f.delete();
        }
    }

    public static boolean  checkFile(String name) {
        path = WTApplication.getContext().getFilesDir().getPath();
        File f = new File(path + "/" + name);
        return f.exists();

    }

    @SuppressLint("SimpleDateFormat")
    public static synchronized  String saveReport() {

        try {

            path = WTApplication.getContext().getFilesDir().getPath() ;
            String versionName = ((WTApplication)WTApplication.getContext()).getApkinfo().getVersionName();

            packageName =  ((WTApplication)WTApplication.getContext()).getApkinfo().getPackagename();
            appName = ((WTApplication)WTApplication.getContext()).getApkinfo().getAppname();;
            datasource =  ((WTApplication)WTApplication.getContext()).getReport();

            if (datasource != null)
                datas = datasource.getDatalist();

            size = datas.size();

            Date now = new Date();

            long timeStart = -1;
            long timeEnd = -1;
            boolean hasBaseTime = false;

            if(datasource.getBaseTime() == -1){

                hasBaseTime = false;

                timeEnd  = (new Date()).getTime();

                if(timeEnd == -1){

                    timeEnd = now.getTime();

                    timeStart = (timeEnd - (SystemClock.uptimeMillis() - datasource.getBaseColock())) + (datasource.getTimeStart() - datasource.getBaseColock());

                }else{

                    timeStart = (timeEnd - (SystemClock.uptimeMillis() - datasource.getBaseColock())) + (datasource.getTimeStart() - datasource.getBaseColock());

                    ((WTApplication)WTApplication.getContext()).getReport().setBaseTime(timeEnd);
                    ((WTApplication)WTApplication.getContext()).getReport().setBaseColock(SystemClock.uptimeMillis());

                }



            }else{

                hasBaseTime = true;

                timeStart = datasource.getBaseTime() + datasource.getTimeStart() - datasource.getBaseColock();

                timeEnd = datasource.getBaseTime() + SystemClock.uptimeMillis() - datasource.getBaseColock();

            }

            File f ;
            File indexfile = new File(path + "/wtIndex");


            if(((WTApplication)WTApplication.getContext()).getCurrTestFile() == null){

                name = "wt" + now.getTime();

                String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wetest";
                ToolUtil.createDir(fileDir);

                f = new File(fileDir + "/" + name);
                ((WTApplication)WTApplication.getContext()).setCurrTestFile(f);

            }else{
                f = ((WTApplication)WTApplication.getContext()).getCurrTestFile();
                name = f.getName();

            }

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String isRoot = ((WTApplication)WTApplication.getContext()).isRoot()?"1":"0";

            String content_index = name + "/" + 	formatter.format(timeEnd) + "/" + appName.replaceFirst("\\s+", "") + "/" + packageName
                    + "/" + (timeEnd - timeStart) / 1000
                    + "/" + timeStart + "/"
                    + timeEnd +"/" + versionName + "/" + isRoot + "/"
                    + "";

            ((WTApplication) WTApplication.getContext()).getTestReports().add(name);

            if (!f.exists()) {
                f.createNewFile();
            }

            if (!indexfile.exists())
                indexfile.createNewFile();

            JsonResult json_res = readFileReport(f);

            JSONObject cpu = new JSONObject();

            JSONObject natived = new JSONObject();
            JSONObject dalvik = new JSONObject();
            JSONObject total = new JSONObject();

            JSONObject networkIn = new JSONObject();
            JSONObject networkOut = new JSONObject();

            JSONObject fps = new JSONObject();


            JSONObject time = new JSONObject();

            JSONObject tag = new JSONObject();

            JSONObject temperature = new JSONObject();

            JSONObject current = new JSONObject();

            for (ReportData data : datas) {


                if( !hasBaseTime ){

                    long gap = data.getTime() - datasource.getTimeStart();

                    long offset = gap > 0 ? gap : 0;

                    json_res.getContent_f_time().put((timeStart + offset)/1000);

                    //Logger.debug("dataTime is " + formatter.format(timeStart + offset));

                }else{

                    json_res.getContent_f_time().put((data.getTime())/1000);

                    //Logger.debug("dataTime is " + formatter.format(data.getTime()));

                }

                json_res.getContent_f_cpu().put(data.getCpu());


                json_res.getContent_f_native().put(data.getpNative());
                json_res.getContent_f_dalvik().put(data.getpDalvik());
                json_res.getContent_f_total().put(data.getpTotal());



                json_res.getContent_f_networkIn().put(data.getpNetworUsagekIn());
                json_res.getContent_f_networkOut().put(data.getpNetworUsagekOut());
                json_res.getContent_f_Fps().put(data.getFps());

                json_res.getContent_f_Tag().put(data.getTag());

                json_res.getContent_f_temperature().put(data.getpTemperature());

                json_res.getContent_f_current().put(data.getpCurrent());

            }

            cpu.put("cpu", json_res.getContent_f_cpu());

            natived.put("native", json_res.getContent_f_native());
            dalvik.put("dalvik", json_res.getContent_f_dalvik());
            total.put("total", json_res.getContent_f_total());

            networkIn.put("networkIn", json_res.getContent_f_networkIn());
            networkOut.put("networkOut", json_res.getContent_f_networkOut());
            time.put("time", json_res.getContent_f_time());

            fps.put("fps", json_res.getContent_f_Fps());
            tag.put("tag", json_res.getContent_f_Tag());

            temperature.put("temperature", json_res.getContent_f_temperature());
            current.put("current", json_res.getContent_f_current());

            BufferedWriter writer = new BufferedWriter(new FileWriter(f, false));

            writer.append(cpu.toString());
            writer.newLine();

            writer.append(natived.toString());
            writer.newLine();

            writer.append(dalvik.toString());
            writer.newLine();

            writer.append(total.toString());
            writer.newLine();

            writer.append(networkIn.toString());
            writer.newLine();

            writer.append(networkOut.toString());
            writer.newLine();

            writer.append(time.toString());
            writer.newLine();

            writer.append(fps.toString());
            writer.newLine();

            writer.append(tag.toString());

            writer.flush();
            writer.close();

            updateRecord(indexfile ,name, content_index);

        } catch (Exception e) {
            Logger.error("report save exception :" + e.toString());
            e.printStackTrace();
        }

        return name;

    }

    private static  JsonResult readFileReport(File f) throws JSONException, IOException{

        BufferedReader reader = new BufferedReader(new FileReader(f));
        JsonResult res = new JsonResult();
        String br ;
        if((br = reader.readLine()) != null){
            JSONObject cpu = new JSONObject(br);

            JSONObject natived = new JSONObject(reader.readLine());
            JSONObject dalvik = new JSONObject(reader.readLine());
            JSONObject total = new JSONObject(reader.readLine());
            JSONObject networkIn = new JSONObject(reader.readLine());
            JSONObject networkOut = new JSONObject(reader.readLine());
            JSONObject time = new JSONObject(reader.readLine());
            JSONObject fps = new JSONObject(reader.readLine());
            JSONObject tag = new JSONObject(reader.readLine());

            res.setContent_f_cpu(cpu.getJSONArray("cpu"));
            res.setContent_f_dalvik(dalvik.getJSONArray("dalvik"));

            res.setContent_f_native( natived.getJSONArray("native"));
            res.setContent_f_total(total.getJSONArray("total"));
            res.setContent_f_networkIn(networkIn.getJSONArray("networkIn"));
            res.setContent_f_networkOut(networkOut.getJSONArray("networkOut"));
            res.setContent_f_Fps(fps.getJSONArray("fps"));
            res.setContent_f_time( time.getJSONArray("time"));
            res.setContent_f_Tag( tag.getJSONArray("tag"));


        }

        reader.close();
        return res;
    }


    public static  String readFileContent(File f) throws JSONException, IOException{

        BufferedReader reader = new BufferedReader(new FileReader(f));
        String res = "";

        String br = "";
        while (( br = reader.readLine()) != null){

            res += br + "\n";
        }

        reader.close();
        return res;
    }

    public static List<HashMap<String, Object>> readReportList() {
        path = WTApplication.getContext().getFilesDir().getPath();
        int i = 0;
        File indexfile = new File(path + "/wtIndex");
        if (indexfile.exists()) {
            String temp = null;

            PackageManager pm = (PackageManager) WTApplication.getContext()
                    .getPackageManager();

            List<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

            try {
                BufferedReader indexreader = new BufferedReader(new FileReader(indexfile));
                // indexreader.readLine();
                while ((temp = indexreader.readLine()) != null) {
                    String[] content = temp.split("/");

                    PackageInfo pi = ApkManager.getPackageInfoByPackageName(content[3]);

                    HashMap<String, Object> map = new HashMap<String, Object>();

                    if(pi != null)
                        map.put("icon", pi.applicationInfo.loadIcon(pm));
                    else{
                        Resources resources = WTApplication.getContext().getResources();
                        Drawable drawable = resources.getDrawable(R.drawable.logo);
                        map.put("icon", drawable);
                    }


                    CharSequence appname = content[2];

                    CharSequence time = content[1];
                    map.put("appName", time);
                    map.put("packageName", appname);
                    map.put("filename", content[0]);
                    items.add(map);
                }

                indexreader.close();
                return items;
            } catch (Exception e) {

                Logger.error("readReportList ");
                e.printStackTrace();
            }
        }
        return null;
    }


    public static List<TestRecord> readReportRecordList(String uin) {
        path = WTApplication.getContext().getFilesDir().getPath();
        File indexfile = new File(path + "/" + uin + "fileindex");

        if (indexfile.exists()) {
            String temp = null;
            // Logger.info("readReportRecordList:"+path + "/" + uin + "fileindex");
            List<TestRecord> items = new ArrayList<TestRecord>();

            try {
                BufferedReader indexreader = new BufferedReader(new FileReader(
                        indexfile));
                // indexreader.readLine();
                while ((temp = indexreader.readLine()) != null) {

                    String[] content = temp.split("/");
                    TestRecord record  = new TestRecord();
                    record.setLogfile(content[0]);
                    record.setSaveTime(content[1]);
                    record.setAppName(content[2]);
                    record.setPackageName(content[3]);
                    record.setTestTime(content[4]);
                    record.setTimeStart(content[5]);
                    record.setTimeEnd(content[6]);
                    record.setVersionName(content[7]);
                    record.setRootTag(Integer.parseInt(content[8].trim()));

                    items.add(record);
                }

                indexreader.close();
                return items;
            } catch (Exception e) {
                Logger.info("readReportRecordList:Exception");
                e.printStackTrace();
            }
        }

        return null;
    }

    public static TestRecord readTestRecordByFile( String logfile) {
        path = WTApplication.getContext().getFilesDir().getPath();
        File indexfile = new File(path + "/wtIndex");
        TestRecord res = null;

        if (indexfile.exists()) {

            String temp = null;

            try {

                BufferedReader indexreader = new BufferedReader(new FileReader(indexfile));

                while ((temp = indexreader.readLine()) != null) {

                    String[] content = temp.split("/");

                    if (content[0].equals(logfile)) {

                        res = new TestRecord();
                        res.setLogfile(content[0]);
                        res.setSaveTime(content[1]);
                        res.setAppName(content[2]);
                        res.setPackageName(content[3]);

                        res.setTestTime(content[4]);
                        res.setTimeStart(content[5]);
                        res.setTimeEnd(content[6]);
                        res.setVersionName(content[7]);
                        res.setRootTag(Integer.parseInt(content[8].trim()));

                    }

                }

                indexreader.close();

            } catch (Exception e) {

                Logger.error("commit exception:"+e.getMessage());

            }
        }

        return res;
    }



}
