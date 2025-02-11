package com.android.example.wordlistsql;

public class WordItem {
    private int mid;
    private String mWord;

    public WordItem() {}

    public int getId() {
        return this.mid;
    }

    public void setId(int id) {
        this.mid = id;
    }

    public String getWord() {
        return this.mWord;
    }

    public void setWord(String word) {
        this.mWord = word;
    }
}
