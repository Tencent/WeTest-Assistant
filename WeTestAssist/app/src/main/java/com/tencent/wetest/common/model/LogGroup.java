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

import java.util.List;

public class LogGroup {  
	
    private String groupName;
    private List<LogItem> groupChild;
  
    public LogGroup() {  
        super();  
    }  
  
    public LogGroup(String groupName, List<LogItem> groupChild) {  
        super();  
        this.groupName = groupName;  
        this.groupChild = groupChild;  
    }  
  
    public void add(LogItem u) { 
        groupChild.add(u);  
    }  
  
    public void remove(LogItem u) {
        groupChild.remove(u);  
    }  
  
    public void remove(int index) {
        groupChild.remove(index);  
    }  
  
    public int getChildSize() {
        return groupChild.size();  
    }  
  
    public LogItem getChild(int index) {
        return index <= (groupChild.size()-1) ? groupChild.get(index) : null;  
    }  
  
    
    public String getGroupName() {  
        return groupName;  
    }  
  
    public void setGroupName(String groupName) {  
        this.groupName = groupName;  
    }  
  
    public List<LogItem> getGroupChild() {  
        return groupChild;  
    }  
  
    public void setGroupChild(List<LogItem> groupChild) {  
        this.groupChild = groupChild;  
    }  
  
}  
