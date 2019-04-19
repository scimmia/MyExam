package com.scimmia.myexam;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.scimmia.myexam.utils.bean.Question;
//import com.scimmia.myexam.utils.db.DBHelper;
import com.scimmia.myexam.utils.db.DBManager;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;

public class BeginExamActivity extends AppCompatActivity {

    boolean showAnswer;
    boolean showWrong;
    int s;
    int m;
    int c;
    TextView question;
    TextView answer;
    TextView myAnswer;
    ListView listView;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin_exam);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        wrongQuestions = new LinkedList<>();
        question = (TextView) findViewById(R.id.question);
        answer = (TextView) findViewById(R.id.answer);
        myAnswer = (TextView) findViewById(R.id.myanswer);
        listView = (ListView) findViewById(R.id.list_item);
        adapter = new BaseAdapter<String>(
                BeginExamActivity.this, R.layout.the_check_view);
        listView.setAdapter(adapter);
        findViewById(R.id.btn_reanswer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Question question:allQuestions) {
                    question.setMyAnswer("");
                    long[] ids = new long[0];
                    question.setIds(ids);
                }
                questions = allQuestions;
                initData();
                init();
            }
        });
        findViewById(R.id.btn_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyAnswer();
                if (showAnswer){
                    showAnswer = false;
                    answer.setVisibility(View.GONE);
                    myAnswer.setVisibility(View.GONE);
                    ((Button)v).setText("显示答案");
                }else {
                    showAnswer = true;
                    answer.setVisibility(View.VISIBLE);
                    myAnswer.setVisibility(View.VISIBLE);
                    ((Button)v).setText("隐藏答案");
                }
            }
        });
        findViewById(R.id.btn_wrong).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyAnswer();
                if (showWrong){
                    questions = allQuestions;
                    initData();
                    init();

                    showWrong = false;
                    ((Button)v).setText("只看错题");
                }else {
                    wrongQuestions.clear();
                    int wrongs = 0;
                    int wrongm = 0;
                    int wrongc = 0;

                    for (Question question:allQuestions){
                        if (!StringUtils.equals(question.getAnswer(),question.getMyAnswer())){
                            wrongQuestions.add(question);
                            if (StringUtils.equals(question.getTheType(),"单选题")){
                                wrongs++;
                            }else if (StringUtils.equals(question.getTheType(),"多选题")){
                                wrongm++;
                            }else if (StringUtils.equals(question.getTheType(),"判断题")){
                                wrongc++;
                            }
                        }
                    }
                    questions = wrongQuestions;
                    initData();
                    init();

                    showWrong = true;
                    ((Button)v).setText("查看全部");
                    new AlertDialog.Builder(BeginExamActivity.this).setTitle("总结")
                            .setMessage("单选题"+s+"错误"+wrongs+"\n"
                                    +"多选题"+m+"错误"+wrongm+"\n"
                                    +"判断题"+c+"错误"+wrongc+"\n"
                            )
                            .create().show();
                }
            }
        });
        findViewById(R.id.btn_last).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyAnswer();
                if (currentIndex == 0){
                    showToast("已到最前");
                }else {
                    currentIndex--;
                    init();
                }
            }
        });
        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyAnswer();
                if (currentIndex == questions.size()-1){
                    showToast("已到最后");
                }else {
                    currentIndex++;
                    init();
                }
            }
        });

        Intent i = getIntent();
        s = i.getIntExtra("s",10);
        m = i.getIntExtra("m",10);
        c = i.getIntExtra("c",10);
        String choosedExam = i.getStringExtra("choosedExam");

//        DBHelper dbHelper = new DBHelper(BeginExamActivity.this);
        DBManager dbHelper = new DBManager(BeginExamActivity.this);
        allQuestions = dbHelper.quertExam(choosedExam,s,m,c);
        questions = allQuestions;
        initData();
        init();

    }

    public LinkedList<Question> allQuestions;
    public LinkedList<Question> wrongQuestions;
    public LinkedList<Question> questions;
    private int currentIndex;
    private Question currentQ;

    void getMyAnswer(){
        if (currentQ != null){
            StringBuilder myAnswer = new StringBuilder();
            long[] ids = listView.getCheckedItemIds();
            currentQ.setIds(ids);
            for (int i = 0;i<ids.length;i++) {
                switch ((int) ids[i]){
                    case 0:
                        myAnswer.append("A,");
                        break;
                    case 1:
                        myAnswer.append("B,");
                        break;
                    case 2:
                        myAnswer.append("C,");
                        break;
                    case 3:
                        myAnswer.append("D,");
                        break;
                    case 4:
                        myAnswer.append("E,");
                        break;
                    case 5:
                        myAnswer.append("F,");
                        break;
                    case 6:
                        myAnswer.append("G,");
                        break;
                }
            }
            String t = myAnswer.toString();
            if (t.endsWith(",")){
                t = t.substring(0,t.length()-1);
            }
            currentQ.setMyAnswer(t);
            Log.e("myAnswer", t);
        }
    }
    void initData(){
        currentIndex = 0;
        if (questions.size() > 0){
            currentQ = questions.get(currentIndex);
        }else {
            currentQ = new Question();
        }
    }
    void init(){
        if (questions.size() > 0){
            currentQ = questions.get(currentIndex);
            question.setText(currentQ.getTitle());
            answer.setText(currentQ.getAnswer());
            myAnswer.setText(currentQ.getMyAnswer());
            if (!StringUtils.equals(currentQ.getAnswer(),currentQ.getMyAnswer())){
                answer.setTextColor(Color.RED);
            }else {
                answer.setTextColor(Color.BLACK);
            }

            listView.clearChoices();
            if (currentQ.getTheType().equals("多选题")) {
                listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            }else {
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            }
            long[] ids = currentQ.getIds();
            if (ids != null) {
                for (int i = 0; i < ids.length; i++) {
                    listView.setItemChecked((int) ids[i], true);
                }
            }
            adapter.clear();
            adapter.addAll(currentQ.getChooses());
            adapter.notifyDataSetChanged();
        }else {
            showToast("没有题库");
        }
    }

    void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    class BaseAdapter<T> extends ArrayAdapter<T>{

        public BaseAdapter(@NonNull Context context, int resource) {
            super(context, resource);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
    // 再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
            super.onBackPressed();
        } else {
            TOUCH_TIME = System.currentTimeMillis();
            showToast("再按一次退出");
        }
    }
}
