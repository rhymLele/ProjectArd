package com.example.projectard.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import com.example.projectard.receiver.ConnectionReceiver;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteListener {
    public static final int REQUEST_CODE_ADD_NOTE=1;
    public static final int REQUEST_CODE_UPDATE_NOTE=2;
    public static final int REQUEST_CODE_SHOW_NOTE=3;
    private ImageView imageAddNoteMain;
    private RecyclerView noteRecyclerView;
    private List<Note> noteList;
    private NoteAdapter noteAdapter;
    private int noteClickedPosition=-1;
    ConnectionReceiver receiver;
    IntentFilter intentFilter;
    EditText inputSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Create","okls");
        initUI();
        HanldeCLick();
        receiver = new ConnectionReceiver();
        intentFilter = new IntentFilter("com.example.listview2023.SOME_ACTION");
        intentFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(receiver, intentFilter);
//        if(noteAdapter.getItemCount()==0)
        getNote(REQUEST_CODE_SHOW_NOTE, false);




    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
        Log.d("resume","ere");
//        getNote(REQUEST_CODE_SHOW_NOTE);

    }

    private void HanldeCLick() {
        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
            }
        });
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                noteAdapter.cancelTimer();
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(noteList.size() != 0){
                    noteAdapter.searchNote(s.toString());
                }
            }
        });
    }
    private void getNote(final int requestCode, final boolean isNoteDeleted){
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
                    if(requestCode==REQUEST_CODE_SHOW_NOTE){
                        noteList.addAll(notes);
                        noteAdapter.notifyDataSetChanged();
                    }else if(requestCode==REQUEST_CODE_ADD_NOTE)
                    {
                        noteList.add(0,notes.get(0));
                        noteAdapter.notifyItemInserted(0);
                        noteRecyclerView.smoothScrollToPosition(0);
                    }else if(requestCode==REQUEST_CODE_UPDATE_NOTE)
                    {
                        noteList.remove(noteClickedPosition);
                        if(isNoteDeleted){
                            noteAdapter.notifyItemRemoved(noteClickedPosition);

                        }else{
                            noteList.add(noteClickedPosition,notes.get(noteClickedPosition));
                            noteAdapter.notifyItemChanged(noteClickedPosition);
                        }
                    }
            }
        }
        new GetNoteTask().execute();
    }
    private void initUI() {
        inputSearch = findViewById(R.id.inputSearch);
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
        if(resultCode ==REQUEST_CODE_ADD_NOTE && resultCode==RESULT_OK)
        {
            getNote(REQUEST_CODE_ADD_NOTE, false);
        }else if(resultCode ==REQUEST_CODE_UPDATE_NOTE && resultCode==RESULT_OK)
        {
           if(data!=null)
           {
               getNote(REQUEST_CODE_UPDATE_NOTE, data.getBooleanExtra("isNoteDeleted",false));
           }
        }
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition=position;
        Intent intent=new Intent(getApplicationContext(),CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate",true);
        intent.putExtra("note",note);
        startActivityForResult(intent,REQUEST_CODE_UPDATE_NOTE);
    } public static void sort_type(List<Note> list,int option) {

        if(option==1)
        {
            list.sort((o1, o2)
                    -> o1.getTitle().compareTo(
                    o2.getTitle()));
        }else if(option==2)
        {
            list.sort((o1, o2)
                    -> o1.getDateTime().compareTo(
                    o2.getDateTime()));
        }

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.mnuSortTitle)
        {
            sort_type(noteList,2);
            noteAdapter.notifyDataSetChanged();
            Toast.makeText(this,"Sort by name",Toast.LENGTH_SHORT).show();
        }else if(item.getItemId()==R.id.mnuSortDate)
        {
            sort_type(noteList,1);
            noteAdapter.notifyDataSetChanged();
            Toast.makeText(this,"Sort by phone",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.optionmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}