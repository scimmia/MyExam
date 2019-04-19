package com.scimmia.myexam.utils.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.scimmia.myexam.utils.DebugLog;
import com.scimmia.myexam.utils.GlobalData;
import com.scimmia.myexam.utils.bean.Question;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;

import java.util.HashMap;
import java.util.LinkedList;

public class DBManager {
    String dirPath;
    Context mContext;
    SQLiteDatabase db;


    public DBManager(Context context) {
        mContext = context;
        dirPath = GlobalData.DBPath;
        DebugLog.e(dirPath);
    }

    private SQLiteDatabase mDatabase = null;
    public synchronized SQLiteDatabase getDB() {
        mDatabase = SQLiteDatabase.openDatabase(dirPath, null, SQLiteDatabase.OPEN_READONLY);
        return mDatabase;
    }
    String Table_Question = "questions";
    public LinkedList<String> quertExamTitles(){
        // 相当于 select * from students 语句
        LinkedList<String> titles = new LinkedList<>();
        getDB();
        Cursor cursor = mDatabase.query(Table_Question,  new String[]{"title"},
                null, null,
                "title", null, null, null);
        while (cursor.moveToNext()) {
            titles.add(cursor.getString(cursor.getColumnIndex("title")));
        }
        cursor.close();
        return titles;
    }


    public HashMap<String,Integer> quertExamTitles(String titles){
        HashMap<String,Integer> result = new HashMap<String,Integer>();
        result.put("单选题",0);
        result.put("多选题",0);
        result.put("判断题",0);
        getDB();
        Cursor cursor = mDatabase.query(Table_Question,  new String[]{"qtype,count(*) as b"},
                "title in ("+titles+")", null,
                "qtype", null, null, null);
        while (cursor.moveToNext()) {
            result.put(cursor.getString(cursor.getColumnIndex("qtype")),
                    cursor.getInt(cursor.getColumnIndex("b")));
        }
        cursor.close();
        return result;
    }

    public LinkedList<Question> quertExam(String choosedExam,int s, int m, int c){
        String selection = "qtype = ?";
        if (StringUtils.isNoneEmpty(choosedExam)){
            selection += " and title in ("+choosedExam+")";
        }

        LinkedList<Question> questions = new LinkedList<>();
        getDB();
        int index = 0;
        Cursor cursor = mDatabase.query(Table_Question, null,
                selection, new String[]{"单选题"},
//                null, null,
                null, null, "random()", ""+s);
        while (cursor.moveToNext()) {
            index++;
            Question q = getQuestion(cursor);
            q.setTitle("单选题  "+index+".  "+q.getTitle());
            questions.add(q);
        }
        cursor.close();
        cursor = mDatabase.query(Table_Question, null,
                selection, new String[]{"多选题"},
//                null, null,
                null, null, "random()", ""+m);
        index = 0;
        while (cursor.moveToNext()) {
            index++;
            Question q = getQuestion(cursor);
            q.setTitle("多选题  "+index+".  "+q.getTitle());
            questions.add(q);
        }
        cursor.close();
        cursor = mDatabase.query(Table_Question, null,
                selection, new String[]{"判断题"},
//                null, null,
                null, null, "random()", ""+c);
        index = 0;
        while (cursor.moveToNext()) {
            index++;
            Question q = getQuestion(cursor);
            q.setTitle("判断题  "+index+".  "+q.getTitle());
            questions.add(q);
        }
        cursor.close();
        return questions;
    }

    Question getQuestion(Cursor cursor){
        Question q = new Question();
        q.setTitle(cursor.getString(cursor.getColumnIndex("question")));
        q.setTheType(cursor.getString(cursor.getColumnIndex("qtype")));
        q.setAnswer(cursor.getString(cursor.getColumnIndex("answer")));
        q.addChoose(cursor.getString(cursor.getColumnIndex("a")));
        q.addChoose(cursor.getString(cursor.getColumnIndex("b")));
        q.addChoose(cursor.getString(cursor.getColumnIndex("c")));
        q.addChoose(cursor.getString(cursor.getColumnIndex("d")));
        q.addChoose(cursor.getString(cursor.getColumnIndex("e")));
        q.addChoose(cursor.getString(cursor.getColumnIndex("f")));
        q.addChoose(cursor.getString(cursor.getColumnIndex("g")));

        return q;
    }
}
