package com.scimmia.myexam.utils.bean;

import java.util.LinkedList;

public class Question {
    String theType;
    String title;
    String answer;
    String myAnswer;
    LinkedList<String> chooses;
    long[] ids;

    public Question() {
        chooses = new LinkedList<>();
    }

    public long[] getIds() {
        return ids;
    }

    public void setIds(long[] ids) {
        this.ids = ids;
    }

    public String getTheType() {
        return theType;
    }

    public void setTheType(String theType) {
        this.theType = theType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getMyAnswer() {
        return myAnswer;
    }

    public void setMyAnswer(String myAnswer) {
        this.myAnswer = myAnswer;
    }

    public LinkedList<String> getChooses() {
        return chooses;
    }

    public void addChoose(String choose){
        if (choose != null && choose.length() > 0){
            chooses.add(choose);
        }
    }

    @Override
    public String toString() {
        return "Question{" +
                "theType='" + theType + '\'' +
                ", title='" + title + '\'' +
                ", answer='" + answer + '\'' +
                ", myAnswer='" + myAnswer + '\'' +
                ", chooses=" + chooses +
                '}';
    }
}
