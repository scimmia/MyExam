package com.scimmia.myexam;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.scimmia.myexam.utils.DebugLog;
import com.scimmia.myexam.utils.Global;
import com.scimmia.myexam.utils.GlobalData;
import com.scimmia.myexam.utils.bean.UpdateInfo;
import com.scimmia.myexam.utils.db.UpdateDBTask;
import com.scimmia.myexam.utils.http.HttpDownloadTask;
import com.scimmia.myexam.utils.http.HttpListener;
import com.scimmia.myexam.utils.http.HttpTask;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity{
    private Context _mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _mActivity = MainActivity.this;
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ExamActivity.class);
                startActivity(intent);
            }
        });
        checkUpdate();
    }
    void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


//    @AfterPermissionGranted(RC_Write)
    private void beginDownloadDB(String url, final int dbVersion){
        new HttpDownloadTask(
                _mActivity, url, GlobalData.downNewDBTag,
                GlobalData.newDBFile, new HttpListener() {
            @Override
            public void onSuccess(String tag, String content) {
                new UpdateDBTask(_mActivity, new HttpListener() {
                    @Override
                    public void onSuccess(String tag, String content) {
                        SharedPreferences sp = getSharedPreferences(GlobalData.sp, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt(GlobalData.dbVersion,dbVersion);
                        editor.apply();
                        DebugLog.e("dbversion success: "+dbVersion);
                    }
                }).execute();
            }
        }).execute();
    }

    @AfterPermissionGranted(RC_Write)
    private void checkUpdate(){
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new HttpTask(_mActivity, GlobalData.httpMsg, GlobalData.checkNewAPKVersion, GlobalData.checkNewAPKVersionTag,
                new HttpListener() {
                    @Override
                    public void onSuccess(String tag, String content) {
                        final UpdateInfo updateInfo = new Gson().fromJson(content,UpdateInfo.class);
                        SharedPreferences sp = getSharedPreferences(GlobalData.sp, Context.MODE_PRIVATE);
                        int dbversionNow = sp.getInt(GlobalData.dbVersion,0);
                        if (dbversionNow < updateInfo.getDbversionCode()){
                            beginDownloadDB(updateInfo.getDbUrl(),updateInfo.getDbversionCode());
                        }
                        try {
                            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                            long appVersionCode;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                appVersionCode = packageInfo.getLongVersionCode();
                            } else {
                                appVersionCode = packageInfo.versionCode;
                            }
                            if (appVersionCode < updateInfo.getVersionCode()){
                                new AlertDialog.Builder(_mActivity)
                                        .setTitle("升级信息提示")
                                        .setMessage("发现新版本："+updateInfo.getVersionName()
                                                +"\n更新内容："+updateInfo.getUpdateLog()
                                                +"\n文件下载需要读写权限，\n如有提醒请允许"
                                        )
                                        .setNegativeButton("现在更新", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                beginDownload(updateInfo.getSoftUrl());
                                            }
                                        })
                                        .setPositiveButton("以后再说", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                String t = updateInfo.getVersionName();
                                                if (StringUtils.contains(t,"b")){
                                                    finish();
                                                }
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                }).execute();
        } else {
            EasyPermissions.requestPermissions(this, "从服务器自动更新需要写权限",
                    RC_Write, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }



    private static final int RC_Write = 123;
//    @AfterPermissionGranted(RC_Write)
    private void beginDownload(String url){
        new HttpDownloadTask(
                _mActivity,url , GlobalData.downNewAPKVersionTag,
                GlobalData.newapkFile, new HttpListener() {
            @Override
            public void onSuccess(String tag, String content) {
                installNewApk(GlobalData.newapkFile);
            }
        }).execute();
    }

    private void installNewApk( String apkPath) {
        if (StringUtils.isEmpty(apkPath)) {
            return;
        }
        File file = new File(apkPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);

        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            //provider authorities
            Uri apkUri = FileProvider.getUriForFile(_mActivity, "com.scimmia.myexam.fileprovider", file);
            //Granting Temporary Permissions to a URI
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }
}
