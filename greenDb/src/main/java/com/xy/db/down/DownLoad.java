package com.xy.db.down;

import com.xy.db.AdapterViewType;
import com.xy.db.MultiEntry;
import com.xy.db.base.StringHashMapConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

import java.util.HashMap;

@Entity
public class DownLoad implements MultiEntry {
    @Id
    public Long   down_id;
    //下载进度
    public Long taskId;
    //下载地址
    public String serviceUrl;
    //下载本地地址
    public String localPath;
    //下载进度
    public int progress;
    //下载进度
    public int type;
    //下载状态
    public int status = DownType.DOWN_ING.status;
    //请求下载链接的时候的下载头
    @Convert(columnType = String.class, converter = StringHashMapConverter.class)
    public HashMap<String,String> head = new HashMap<>();

    @Generated(hash = 1216179506)
    public DownLoad(Long down_id, Long taskId, String serviceUrl, String localPath,
            int progress, int type, int status, HashMap<String, String> head) {
        this.down_id = down_id;
        this.taskId = taskId;
        this.serviceUrl = serviceUrl;
        this.localPath = localPath;
        this.progress = progress;
        this.type = type;
        this.status = status;
        this.head = head;
    }
    @Generated(hash = 89475367)
    public DownLoad() {
    }
    
    public Long getDown_id() {
        return this.down_id;
    }
    public void setDown_id(Long down_id) {
        this.down_id = down_id;
    }
    public Long getTaskId() {
        return this.taskId;
    }
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    public String getServiceUrl() {
        return this.serviceUrl;
    }
    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }
    public String getLocalPath() {
        return this.localPath;
    }
    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }
    public int getProgress() {
        return this.progress;
    }
    public void setProgress(int progress) {
        this.progress = progress;
    }
    public int getStatus() {
        return this.status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public HashMap<String, String> getHead() {
        return this.head;
    }
    public void setHead(HashMap<String, String> head) {
        this.head = head;
    }

    @Override
    public int getViewType() {
        return AdapterViewType.VIEW_TYPE_CONTENT;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
}