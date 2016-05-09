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
package com.tencent.wetest.common.adapter;

import java.util.List;
import com.tencent.wefpmonitor.R;
import com.tencent.wetest.activity.TestResultsActivity;
import com.tencent.wetest.common.model.LogGroup;
import com.tencent.wetest.common.model.LogItem;
import com.tencent.wetest.common.util.ReportUtil;
import com.tencent.wetest.common.util.ToolUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ResourceAsColor")
public class LogListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<LogGroup> group;

    public LogListAdapter(Context context, List<LogGroup> group) {

        super();
        this.context = context;
        this.group = group;

    }


    public synchronized void  setGroups(List<LogGroup> groups){
        group = groups;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,   View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.log_item_title, null);
        }

        TextView title = (TextView) convertView.findViewById(R.id.txt_title);
        title.setText(getGroup(groupPosition).toString());

        // ImageView image = (ImageView) convertView.findViewById(R.id.expand_icon);

//        if (isExpanded)
//            image.setBackgroundResource(R.drawable.expanded_icon);
//        else
//            image.setBackgroundResource(R.drawable.expand_icon);

        return convertView;
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public Object getGroup(int groupPosition) {
        return group.get(groupPosition).getGroupName();
    }

    public int getGroupCount() {
        return group.size();
    }

    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null ) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item, null);
        }


        final TextView appname = (TextView) convertView
                .findViewById(R.id.appName);
        final TextView time = (TextView) convertView
                .findViewById(R.id.packageName);
        ImageView icon = (ImageView) convertView
                .findViewById(R.id.icon);

        LogItem item = group.get(groupPosition).getChild(childPosition);

        if(item != null){

            final String cTime = group.get(groupPosition).getChild(childPosition).getTime()  ;
            final String cAppname = group.get(groupPosition).getChild(childPosition).getAppname();
            final Drawable img = group.get(groupPosition).getChild(childPosition).getImg();
            //icon.setImageDrawable(img);
            icon.setImageBitmap(ToolUtil.getRoundedCornerBitmap(ToolUtil.drawableToBitmap(img), 10.0f  ));


            time.setText(cTime);
            appname.setText(cAppname);

            // setBackground(img);
            final String filename = group.get(groupPosition).getChild(childPosition).getFilename();

            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context,TestResultsActivity.class);
                    intent.putExtra("filename", filename);
                    intent.putExtra("isLogDetail" , true);
                    context.startActivity(intent);

                }
            });
        }

        return convertView;
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return group.get(groupPosition).getChild(childPosition);
    }

    public int getChildrenCount(int groupPosition) {
        return group.get(groupPosition).getChildSize();
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void updata(List<LogGroup> group) {
        this.group = null;
        this.group = group;
    }


    /**
     * ͼƬת�Ҷ�
     *
     * @param bmSrc
     * @return
     */

    public static Bitmap bitmap2Gray(Bitmap bmSrc)
    {
        int width, height;
        height = bmSrc.getHeight();
        width = bmSrc.getWidth();
        Bitmap bmpGray = null;
        bmpGray = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGray);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmSrc, 0, 0, paint);

        return bmpGray;
    }


}