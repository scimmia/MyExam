package com.scimmia.myexam.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by lk on 2017/8/12.
 */
public interface GlobalData  {
    String BusDBInited = "BusDBInited";

    int upDir = 2;
    int downDir = 1;

    String goWork = "1";
    String goHome = "2";
    String goNormal = "3";

    String httpMsg = "正在检查...";
    String downMsg = "下载中...";
    String updateMsg = "升级中...";
    String sp = "sp";
    String dbVersion = "dbVersion";

    String baseFolder = Environment.getExternalStorageDirectory().getPath()+ File.separator+"download"+ File.separator;
    String updateFolder = baseFolder;
    String newDBFile = updateFolder + "t.zip";
    String newapkFile = updateFolder + "tiku.apk";
    String checkNewAPKVersionTag = "checkNewAPKVersionTag";
    String downNewAPKVersionTag = "下载文件";
    String downNewDBTag = "下载数据";
    String checkNewAPKVersion = "https://dev.tencent.com/u/yantaibus/p/main/git/raw/master/t.json";
    String DBPath = "/data/data/com.scimmia.myexam/exam.db";
}
