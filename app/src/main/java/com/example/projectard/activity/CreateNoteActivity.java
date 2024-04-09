package com.example.projectard.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.projectard.R;
import com.example.projectard.database.NoteDatabases;
import com.example.projectard.entity.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {
    private ImageView imageBack,imageSave;
    private EditText inputNoteTitle,inputNoteSubTitle,inputNoteText;
    private TextView textDateTime;
    private Note alreadyAvailableNote;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_note);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initUI();
        HanldeCLick();
        if(getIntent().getBooleanExtra("isViewOrUpdate",false)){
            alreadyAvailableNote=(Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }
    }

    private void setViewOrUpdateNote() {
        inputNoteTitle.setText(alreadyAvailableNote.getTitle());
        inputNoteText.setText(alreadyAvailableNote.getNoteText());
        inputNoteSubTitle.setText(alreadyAvailableNote.getSubtitle());
        textDateTime.setText(alreadyAvailableNote.getDateTime());
    }

    private void HanldeCLick() {
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(CreateNoteActivity.this, MainActivity.class);
//                   setResult(RESULT_OK,intent);
//                   finish();
                onBackPressed();
            }
        });
        imageSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveNote();
            }
        });
    }
    private void initUI() {
        imageBack=findViewById(R.id.imageBack);
        inputNoteTitle=findViewById(R.id.inputNoteTitle);
        inputNoteText=findViewById(R.id.inputNote);
        inputNoteSubTitle=findViewById(R.id.inputNoteSubTitle);
        textDateTime=findViewById(R.id.textDateTime);
        textDateTime.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a"
                , Locale.getDefault()).format(new Date()));
        imageSave=findViewById(R.id.imageSave);

    }
    private void SaveNote(){
        if(inputNoteTitle.getText().toString().trim().isEmpty())
        {
            Toast.makeText(this,"Note Title cant be mt",Toast.LENGTH_SHORT).show();
            return;
        }else if(inputNoteSubTitle.getText().toString().trim().isEmpty()&&
                inputNoteText.getText().toString().trim().isEmpty()){
            Toast.makeText(this,"Note cant be mt",Toast.LENGTH_SHORT).show();
            return;
        }
        final Note note=new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setNoteText(inputNoteText.getText().toString());
        note.setSubtitle(inputNoteSubTitle.getText().toString());
        note.setDateTime(textDateTime.getText().toString());
        if(alreadyAvailableNote!=null){
            note.setId(alreadyAvailableNote.getId());
        }
        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void,Void,Void>{

            @Override
            protected Void doInBackground(Void... voids) {
                NoteDatabases.getNoteDatabases(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {

                super.onPostExecute(unused);
                Intent intent=new Intent();
                setResult(RESULT_OK,intent);
                finish();
            }
        }
        new SaveNoteTask().execute();
    }
}