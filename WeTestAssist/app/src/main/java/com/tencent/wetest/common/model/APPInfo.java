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

package com.tencent.wetest.common.model;

import android.graphics.drawable.Drawable;

import java.text.Collator;

public class APPInfo implements  Comparable{

    private String packageName;
    private CharSequence appName;
    private Drawable icon;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public CharSequence getAppName() {
        return appName;
    }

    public void setAppName(CharSequence appName) {
        this.appName = appName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }



    @Override
    public int compareTo(Object another) {

        return   Collator.getInstance(java.util.Locale.CHINA).compare(appName.toString() , ((APPInfo)another).getAppName().toString());

    }
}
