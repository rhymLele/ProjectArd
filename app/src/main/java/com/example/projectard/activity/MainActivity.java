package com.example.projectard.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.projectard.R;
import com.example.projectard.adapters.NoteAdapter;
import com.example.projectard.database.NoteDatabases;
import com.example.projectard.entity.Note;
import com.example.projectard.listeners.NoteListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteListener {
    public static final int REQUEST_CODE_ADD_NOTE=1;
    public static final int REQUEST_CODE_UPDATE_NOTE=2;
    private ImageView imageAddNoteMain;
    private RecyclerView noteRecyclerView;
    private List<Note> noteList;
    private NoteAdapter noteAdapter;
    private int noteClickedPosition=-1;
    private ActivityResultLauncher<Intent> activityResultLauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if(o.getResultCode()==RESULT_OK)
                    {
                        Intent i=o.getData();
                        String noteTitle=i.getStringExtra("");
                    }
                }
            }
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Create","okls");
        initUI();
        HanldeCLick();
//        getNote();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("resume","ere");
        getNote();
    }

    private void HanldeCLick() {
        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), CreateNoteActivity.class);
//                activityResultLauncher.launch(intent);
               startActivityForResult(new Intent(getApplicationContext(),CreateNoteActivity.class),REQUEST_CODE_ADD_NOTE);
            }
        });
    }
    private void getNote(){
        @SuppressLint("StaticFieldLeak")
        class GetNoteTask extends AsyncTask<Void,Void, List<Note>>{
            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NoteDatabases
                        .getNoteDatabases(getApplicationContext())
                        .noteDao()
                        .getAllNotes();
            }
            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                Log.d("My Notes",notes.toString());
                if(noteList.size()==0){
                    noteList.addAll(notes);
                    noteAdapter.notifyDataSetChanged();
                }else{
                    noteList.add(0,notes.get(0));
                    noteAdapter.notifyItemInserted(0);
                }
                noteRecyclerView.smoothScrollToPosition(0);
            }
        }
        new GetNoteTask().execute();
    }
    private void initUI() {
        imageAddNoteMain=findViewById(R.id.imageAddNoteMain);
        noteRecyclerView=findViewById(R.id.noteRecyclerView);
        noteRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        noteList=new ArrayList<>();
        noteAdapter=new NoteAdapter(noteList,this);
        noteRecyclerView.setAdapter(noteAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition=position;
        Intent intent=new Intent(getApplicationContext(),CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate",true);
        intent.putExtra("note",note);
        startActivityForResult(intent,REQUEST_CODE_UPDATE_NOTE);
    }
}