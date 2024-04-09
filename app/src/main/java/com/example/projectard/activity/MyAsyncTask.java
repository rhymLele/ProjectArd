package com.example.projectard.activity;

import android.app.Activity;
import android.os.AsyncTask;

import com.example.projectard.entity.Note;

import java.util.List;

public class MyAsyncTask extends AsyncTask<Void,Void, List<Note>> {
    @Override
    protected List<Note> doInBackground(Void... voids) {
        return null;
    }
    Activity contextParent;

    public MyAsyncTask(Activity contextParent) {
        this.contextParent = contextParent;
    }
}
