package com.scimmia.myexam;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.scimmia.myexam.utils.DebugLog;
import com.scimmia.myexam.utils.db.DBManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ExamActivity extends AppCompatActivity {

    DBManager dbManager;
    ListView listView;
    ArrayAdapter<String> adapter;
    LinkedList<String> mExames;


    TextInputEditText signle;
    TextInputEditText multy;
    TextInputEditText check;

    String choosedExam;
    int maxSignle;
    int maxMulty;
    int maxCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);

        dbManager = new DBManager(ExamActivity.this);
        mExames = dbManager.quertExamTitles();

        signle = (TextInputEditText)findViewById(R.id.text_signle);
        multy = (TextInputEditText)findViewById(R.id.text_mulit);
        check = (TextInputEditText)findViewById(R.id.text_check);

        listView = (ListView) findViewById(R.id.list_item);

        adapter = new BaseAdapter<String>(
                ExamActivity.this, R.layout.the_check_view,mExames);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        findViewById(R.id.btn_choose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long[] ids = listView.getCheckedItemIds();
                if (ids.length < 1){
                    Toast.makeText(getApplicationContext(), "选择至少一个题库", Toast.LENGTH_SHORT).show();
                }else {
                    LinkedList<String> checkedTitile = new LinkedList<>();
                    for (long id : ids) {
                        checkedTitile.add("'" + mExames.get((int) id) + "'");
                    }
                    choosedExam = StringUtils.join(checkedTitile, ",");
                    HashMap<String, Integer> result = dbManager.quertExamTitles(choosedExam);
                    maxSignle = result.get("单选题");
                    maxMulty = result.get("多选题");
                    maxCheck = result.get("判断题");
                    ((TextInputLayout)findViewById(R.id.layout_signle)).setHint("单选题("+maxSignle+")");
                    ((TextInputLayout)findViewById(R.id.layout_mulit)).setHint("多选题("+maxMulty+")");
                    ((TextInputLayout)findViewById(R.id.layout_check)).setHint("判断题("+maxCheck+")");
                    DebugLog.e(choosedExam);
                    findViewById(R.id.layout_choose).setVisibility(View.VISIBLE);
                    findViewById(R.id.btn).setVisibility(View.VISIBLE);
                }
            }
        });
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int s = NumberUtils.toInt(signle.getText().toString(),0);
                int m = NumberUtils.toInt(multy.getText().toString(),0);
                int c = NumberUtils.toInt(check.getText().toString(),0);

                if ( s>maxSignle || s<=0){
                    ((TextInputLayout)findViewById(R.id.layout_signle)).setError("0-"+maxSignle);
                    return;
                }else {
                    ((TextInputLayout)findViewById(R.id.layout_signle)).setError("");
                }
                if (m >maxMulty || m <=0){
                    ((TextInputLayout)findViewById(R.id.layout_mulit)).setError("0-"+maxMulty);
                    return;
                }else {
                    ((TextInputLayout)findViewById(R.id.layout_mulit)).setError("");
                }
                if (c >maxCheck || c <= 0){
                    ((TextInputLayout)findViewById(R.id.layout_check)).setError("0-"+maxCheck);
                    return;
                }else {
                    ((TextInputLayout)findViewById(R.id.layout_check)).setError("");
                }
                Intent intent = new Intent(ExamActivity.this,BeginExamActivity.class);
                intent.putExtra("choosedExam",choosedExam);
                intent.putExtra("s",s);
                intent.putExtra("m",m);
                intent.putExtra("c",c);
                startActivity(intent);
            }
        });
    }

    class BaseAdapter<T> extends ArrayAdapter<T>{

        public BaseAdapter(@NonNull Context context, @LayoutRes int resource,
                           @NonNull List<T> objects) {
            super(context, resource,objects);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
