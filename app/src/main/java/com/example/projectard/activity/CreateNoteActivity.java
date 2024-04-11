package com.example.projectard.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.projectard.R;
import com.example.projectard.database.NoteDatabases;
import com.example.projectard.entity.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {
    private ImageView imageBack,imageSave,imageNote;
    private EditText inputNoteTitle,inputNoteSubTitle,inputNoteText;
    private TextView textDateTime;
    private Note alreadyAvailableNote;

    private View viewSubtitleIndicator;
    private String selectedNoteColor;
    private  Uri imageUri;
    private String SelectedImagePath;
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

        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndicator);
        selectedNoteColor = "#333333";
        initMiscellaneous();
        setSubtitleIndicatorColor();
    }

    private void setViewOrUpdateNote() {
        inputNoteTitle.setText(alreadyAvailableNote.getTitle());
        inputNoteText.setText(alreadyAvailableNote.getNoteText());
        inputNoteSubTitle.setText(alreadyAvailableNote.getSubtitle());
        textDateTime.setText(alreadyAvailableNote.getDateTime());
        if(alreadyAvailableNote.getImagePath()!=null){
            imageNote.setImageURI(Uri.parse(alreadyAvailableNote.getImagePath()));
            imageNote.setVisibility(View.VISIBLE);
        }
    }

    private void HanldeCLick() {
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        imageNote=findViewById(R.id.imageNoteee);

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
        if(imageUri!=null) note.setImagePath(imageUri.toString());
        if(alreadyAvailableNote!=null){
            note.setId(alreadyAvailableNote.getId() );
        }

        note.setColor(selectedNoteColor);

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
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                setResult(RESULT_OK,intent);
                startActivity(intent);
                finish();
//                Intent intent=new Intent();
//                setResult(RESULT_OK,intent);
//                finish();
            }
        }
        new SaveNoteTask().execute();
    }
    private ActivityResultLauncher<PickVisualMediaRequest> launcher=registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri o) {
            if(o==null)
            {
                Toast.makeText(CreateNoteActivity.this,"no",Toast.LENGTH_SHORT).show();
            }else
            {
                imageUri = o;
                int flag = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                getApplicationContext().getContentResolver().takePersistableUriPermission(imageUri, flag);
                imageNote.setVisibility(View.VISIBLE);
                imageNote.setImageURI(o);
                Toast.makeText(CreateNoteActivity.this,"no "+SelectedImagePath,Toast.LENGTH_SHORT).show();
//                Glide.with(getApplicationContext()).load(o).into(imageNote);
            }
        }
    });
    private void initMiscellaneous(){
        final LinearLayout layoutMiscellaneous = findViewById(R.id.layoutMiscellaneous);
        final BottomSheetBehavior<LinearLayout> bottomSBH = BottomSheetBehavior.from(layoutMiscellaneous);
        layoutMiscellaneous.findViewById(R.id.textMiscellaneous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSBH.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSBH.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else{
                    bottomSBH.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        final ImageView imgV1 = layoutMiscellaneous.findViewById(R.id.imageColor1);
        final ImageView imgV2 = layoutMiscellaneous.findViewById(R.id.imageColor2);
        final ImageView imgV3 = layoutMiscellaneous.findViewById(R.id.imageColor3);
        final ImageView imgV4 = layoutMiscellaneous.findViewById(R.id.imageColor4);
        final ImageView imgV5 = layoutMiscellaneous.findViewById(R.id.imageColor5);

        layoutMiscellaneous.findViewById(R.id.ViewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#333333";
                imgV1.setImageResource(R.drawable.ic_done);
                imgV2.setImageResource(0);
                imgV3.setImageResource(0);
                imgV4.setImageResource(0);
                imgV5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        layoutMiscellaneous.findViewById(R.id.ViewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#fdbe3b";
                imgV1.setImageResource(0);
                imgV2.setImageResource(R.drawable.ic_done);
                imgV3.setImageResource(0);
                imgV4.setImageResource(0);
                imgV5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        layoutMiscellaneous.findViewById(R.id.ViewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#ff4842";
                imgV1.setImageResource(0);
                imgV2.setImageResource(0);
                imgV3.setImageResource(R.drawable.ic_done);
                imgV4.setImageResource(0);
                imgV5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        layoutMiscellaneous.findViewById(R.id.ViewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#3a52fc";
                imgV1.setImageResource(0);
                imgV2.setImageResource(0);
                imgV3.setImageResource(0);
                imgV4.setImageResource(R.drawable.ic_done);
                imgV5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });

        layoutMiscellaneous.findViewById(R.id.ViewColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#000000";
                imgV1.setImageResource(0);
                imgV2.setImageResource(0);
                imgV3.setImageResource(0);
                imgV4.setImageResource(0);
                imgV5.setImageResource(R.drawable.ic_done);
                setSubtitleIndicatorColor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
            }
        });
    }

    private void setSubtitleIndicatorColor(){
        GradientDrawable gradientD = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientD.setColor(Color.parseColor(selectedNoteColor));
    }
}