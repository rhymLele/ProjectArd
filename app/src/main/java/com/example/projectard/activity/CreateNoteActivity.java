package com.example.projectard.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.projectard.R;
import com.example.projectard.database.NoteDatabases;
import com.example.projectard.entity.Note;
import com.example.projectard.receiver.AlarmReceiver;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class CreateNoteActivity extends AppCompatActivity {
    private ImageView imageBack,imageSave,imageNote, imageMenu,imgReDe;
    private EditText inputNoteTitle,inputNoteSubTitle,inputNoteText;
    private TextView textDateTime,textWebURL,textReminder;
    LinearLayout layoutWebURL,layoutReminder;
    private Note alreadyAvailableNote;

    private View viewSubtitleIndicator;
    private String selectedNoteColor;
    private  Uri imageUri;
    private String SelectedImagePath;
    private AlertDialog dialogAddUrl,dialogAddReminder;
    private AlertDialog dialogDeleteNote;
    String sngay,sthang,snam;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    Calendar cale;
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
//        if(layoutWebURL.getVisibility()==View.VISIBLE){
//            textWebURL.setText(alreadyAvailableNote.getWebLink());
//        }
        if(alreadyAvailableNote.getWebLink()!=null){
            layoutWebURL.setVisibility(View.VISIBLE);
            textWebURL.setText(alreadyAvailableNote.getWebLink());
        }
        if(alreadyAvailableNote.getReminder()!=null){
            layoutReminder.setVisibility(View.VISIBLE);
            textReminder.setText(alreadyAvailableNote.getReminder());
            Log.d("ReminderDate", "Saved Date: " + textReminder.getText().toString());
        }
    }

    private void HanldeCLick() {
        deleteimageurl();
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
        imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LinearLayout layoutMiscellaneous = findViewById(R.id.layoutMiscellaneous);
                final BottomSheetBehavior<LinearLayout> bottomSBH = BottomSheetBehavior.from(layoutMiscellaneous);
                hideKeyboard();
                if(bottomSBH.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSBH.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else{
                    bottomSBH.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                }
        });
        textReminder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
               showAddReminder();
                return true;
            }
        });
       imgReDe.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               textReminder.setText("");
               layoutReminder.setVisibility(View.GONE);
           }
       });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }
        }
    }

    private void initUI() {
        imageBack=findViewById(R.id.imageBack);
        imageMenu = findViewById(R.id.imageMenu);
        inputNoteTitle=findViewById(R.id.inputNoteTitle);
        inputNoteText=findViewById(R.id.inputNote);
        inputNoteSubTitle=findViewById(R.id.inputNoteSubTitle);
        textDateTime=findViewById(R.id.textDateTime);
        textDateTime.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a"
                , Locale.getDefault()).format(new Date()));
        imageSave=findViewById(R.id.imageSave);
        imageNote=findViewById(R.id.imageNoteee);
        textWebURL=findViewById(R.id.textWebURL);
        imgReDe=findViewById(R.id.deleteReminder);
        textReminder=findViewById(R.id.textDateReminder);
        layoutWebURL=findViewById(R.id.layoutWebURLL);
        layoutReminder=findViewById(R.id.layoutDateReminder);

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
        if(imageUri!=null&&imageNote.getVisibility()==View.VISIBLE) note.setImagePath(imageUri.toString());
        if(alreadyAvailableNote!=null){
            note.setId(alreadyAvailableNote.getId() );
        }

        note.setColor(selectedNoteColor);
        if(layoutWebURL.getVisibility()==View.VISIBLE){
            note.setWebLink(textWebURL.getText().toString());
        }

        if(layoutReminder.getVisibility()==View.VISIBLE){
            note.setReminder(textReminder.getText().toString());
            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
            intent.setAction("MyAction");
            intent.putExtra("time", sngay +"/"+ sthang +"/"+ snam);
            if(inputNoteTitle.getText()!=null)
            {
                intent.putExtra("title",note.getTitle());
            }
            Log.d("ReminderDate", "Saved Date: " + textReminder.getText().toString());
            alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            pendingIntent = PendingIntent.getBroadcast(CreateNoteActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, cale.getTimeInMillis(), pendingIntent);
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
    void deleteimageurl(){
        textWebURL.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                textWebURL.setText("");
                layoutWebURL.setVisibility(View.GONE);
                return false;
            }
        });
        imageNote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                imageUri=null;
                imageNote.setImageURI(null);
                imageNote.setVisibility(View.GONE);
                return false;
            }
        });
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

    public void addImage(){
        launcher.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

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
                selectedNoteColor = "#328536";
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
                bottomSBH.setState(BottomSheetBehavior.STATE_COLLAPSED);
                addImage();
            }
        });
        layoutMiscellaneous.findViewById(R.id.layoutAddUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               bottomSBH.setState(BottomSheetBehavior.STATE_COLLAPSED);
               showAddUrlDialog();
            }
        });
        layoutMiscellaneous.findViewById(R.id.layoutAddReminder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSBH.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showAddReminder();
            }
        });

        if(alreadyAvailableNote != null){
            layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setVisibility(View.VISIBLE);
            layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSBH.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteNoteDialog();
                }
            });
        }
    }

    private void showDeleteNoteDialog(){
        if(dialogDeleteNote == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View v = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,
                    (ViewGroup) findViewById(R.id.layoutdeleteNoteContainer)
            );
            builder.setView(v);
            dialogDeleteNote = builder.create();
            if(dialogDeleteNote.getWindow() != null){
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            v.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    @SuppressLint("StaticFieldLeak")
                    class DeleteNoteTask extends AsyncTask<Void, Void, Void>{
                        @Override
                        protected Void doInBackground(Void... voids) {
                            NoteDatabases.getNoteDatabases(getApplicationContext()).noteDao().
                                    deleteNote(alreadyAvailableNote);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void unused) {
                            super.onPostExecute(unused);
                            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                            intent.putExtra("isNoteDeleted", true);
                            setResult(RESULT_OK,intent);
                            startActivity(intent);
                            finish();

                        }
                    }

                    new DeleteNoteTask().execute();
                }
            });
            v.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDeleteNote.dismiss();
                }
            });
        }
        dialogDeleteNote.show();

    }

    private void setSubtitleIndicatorColor(){
        GradientDrawable gradientD = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientD.setColor(Color.parseColor(selectedNoteColor));
    }
    private void showAddUrlDialog(){
        if(dialogAddUrl==null){
            AlertDialog.Builder builder=new AlertDialog.Builder(CreateNoteActivity.this);
            View view= LayoutInflater.from(this).inflate(R.layout.layout_add_url,(ViewGroup) findViewById(R.id.layoutAddUrlContainer));
            builder.setView(view);
            dialogAddUrl= builder.create();
            if(dialogAddUrl.getWindow()!=null)
            {
                dialogAddUrl.getWindow().setBackgroundDrawable(new ColorDrawable(0));

            }
            final EditText inputURL=view.findViewById(R.id.inputURL);
            inputURL.requestFocus();
            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(inputURL.getText().toString().trim().isEmpty()){
                        Toast.makeText(CreateNoteActivity.this,"Enter URl",Toast.LENGTH_SHORT).show();
                    }else if(!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()){
                         Toast.makeText(CreateNoteActivity.this,"Enter valid URL",Toast.LENGTH_SHORT).show();
                    }else {
                        textWebURL.setText(inputURL.getText().toString());
                        layoutWebURL.setVisibility(View.VISIBLE);
                        dialogAddUrl.dismiss();
                    }
                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogAddUrl.dismiss();
                }
            });
        }dialogAddUrl.show();
    }
    private void showAddReminder() {
        if (dialogAddReminder == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_add_reminder, (ViewGroup) findViewById(R.id.layoutAddReminderContainer));
            builder.setView(view);
            dialogAddReminder = builder.create();

            if (dialogAddReminder.getWindow() != null) {
                dialogAddReminder.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final DatePicker inputDate = view.findViewById(R.id.dateee);
            inputDate.requestFocus();

            view.findViewById(R.id.textAddDate).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(System.currentTimeMillis());
                    calendar.set(Calendar.YEAR, inputDate.getYear());
                    calendar.set(Calendar.MONTH, inputDate.getMonth());
                    calendar.set(Calendar.DAY_OF_MONTH, inputDate.getDayOfMonth());
                    cale=calendar;
                    int ngay = inputDate.getDayOfMonth();
                    int thang = inputDate.getMonth()+1;
                    int nam = inputDate.getYear();

                    sngay = String.valueOf(ngay);
                    sthang = String.valueOf(thang);
                    snam = String.valueOf(nam);



                    textReminder.setText(inputDate.getDayOfMonth() + "/" + (inputDate.getMonth() + 1) + "/" + inputDate.getYear());
                    layoutReminder.setVisibility(View.VISIBLE);
                    Log.d("Notification","savedss");
                    dialogAddReminder.dismiss();
                }
            });

            view.findViewById(R.id.textCancelDate).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pendingIntent != null) {
                        alarmManager.cancel(pendingIntent);
                        pendingIntent.cancel();
                    }
                    dialogAddReminder.dismiss();
                }
            });
        }
        dialogAddReminder.show();
    }
}