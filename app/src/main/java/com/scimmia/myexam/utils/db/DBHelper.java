//package com.scimmia.myexam.utils.db;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//
//import com.scimmia.myexam.utils.bean.Question;
//
//import java.util.LinkedList;
//
//public class DBHelper {
//    private final Context mContext;
//    private SQLiteDatabase mDatabase = null;
//    public DBHelper(Context context) {
//        this.mContext = context;
//    }
//    public synchronized SQLiteDatabase getDB() {
//        SQLiteDbHelper helper = new SQLiteDbHelper(mContext);
//        mDatabase = helper.getReadableDatabase();
//        return mDatabase;
//    }
//
//    public synchronized void close() {
//        if(mDatabase != null)
//            mDatabase.close();
//    }
//
//    public LinkedList<Question> quertExam(int s, int m, int c){
//        // 相当于 select * from students 语句
//        LinkedList<Question> questions = new LinkedList<>();
//        getDB();
//        int index = 0;
//        Cursor cursor = mDatabase.query(SQLiteDbHelper.Table_Question, null,
//                "qtype = ?", new String[]{"单选题"},
////                null, null,
//                null, null, "random()", ""+s);
//        while (cursor.moveToNext()) {
//            index++;
//            Question q = getQuestion(cursor);
//            q.setTitle("单选题  "+index+".  "+q.getTitle());
//            questions.add(q);
//        }
//        cursor.close();
//        cursor = mDatabase.query(SQLiteDbHelper.Table_Question, null,
//                "qtype = ?", new String[]{"多选题"},
////                null, null,
//                null, null, "random()", ""+m);
//        index = 0;
//        while (cursor.moveToNext()) {
//            index++;
//            Question q = getQuestion(cursor);
//            q.setTitle("多选题  "+index+".  "+q.getTitle());
//            questions.add(q);
//        }
//        cursor.close();
//        cursor = mDatabase.query(SQLiteDbHelper.Table_Question, null,
//                "qtype = ?", new String[]{"判断题"},
////                null, null,
//                null, null, "random()", ""+c);
//        index = 0;
//        while (cursor.moveToNext()) {
//            index++;
//            Question q = getQuestion(cursor);
//            q.setTitle("判断题  "+index+".  "+q.getTitle());
//            questions.add(q);
//        }
//        cursor.close();
//        return questions;
//    }
//
//    Question getQuestion(Cursor cursor){
//        Question q = new Question();
//        q.setTitle(cursor.getString(cursor.getColumnIndex("question")));
//        q.setTheType(cursor.getString(cursor.getColumnIndex("qtype")));
//        q.setAnswer(cursor.getString(cursor.getColumnIndex("answer")));
//        q.addChoose(cursor.getString(cursor.getColumnIndex("a")));
//        q.addChoose(cursor.getString(cursor.getColumnIndex("b")));
//        q.addChoose(cursor.getString(cursor.getColumnIndex("c")));
//        q.addChoose(cursor.getString(cursor.getColumnIndex("d")));
//        q.addChoose(cursor.getString(cursor.getColumnIndex("e")));
//        q.addChoose(cursor.getString(cursor.getColumnIndex("f")));
//        q.addChoose(cursor.getString(cursor.getColumnIndex("g")));
//
//        return q;
//    }
//}
