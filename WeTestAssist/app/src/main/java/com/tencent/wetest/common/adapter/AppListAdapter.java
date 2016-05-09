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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tencent.wefpmonitor.R;
import com.tencent.wetest.common.log.Logger;
import com.tencent.wetest.common.model.APPInfo;
import com.tencent.wetest.common.util.ToolUtil;

import java.util.List;


public class AppListAdapter extends ArrayAdapter<APPInfo> {

	private int resource;

	public AppListAdapter(Context context, int resourceId, List<APPInfo> objects) {
		super(context, resourceId, objects);

		resource = resourceId;
	}


	public View getView(int position,View convertView,ViewGroup parent){

		if(convertView == null) {

			LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView =  inflater.inflate(resource, null);
		}

		ImageView view = (ImageView) convertView.findViewById(R.id.icon);
		view.setImageBitmap(ToolUtil.getRoundedCornerBitmap(ToolUtil.drawableToBitmap(this.getItem(position).getIcon()) , 10.0f));

		TextView appName = (TextView) convertView.findViewById(R.id.appName);
		appName.setText(this.getItem(position).getAppName());
		TextView pkgName = (TextView) convertView.findViewById(R.id.packageName);
		pkgName.setText( this.getItem(position).getPackageName() );

//        TextView status = (TextView) mView.findViewById(R.id.status);
//        status.setText(this.getItem(getIsTop()));

		return convertView;
	}

	public void setViewImage(ImageView v, Drawable value){
		v.setImageDrawable(value);
	}
}