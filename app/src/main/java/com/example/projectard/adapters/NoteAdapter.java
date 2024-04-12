package com.example.projectard.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectard.R;
import com.example.projectard.entity.Note;
import com.example.projectard.listeners.NoteListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> notes;

    private Timer timer;
    private List<Note> notesSource;

    public NoteAdapter(List<Note> notes,NoteListener noteListener) {
        this.notes = notes;
        this.noteListener=noteListener;
        notesSource = notes;
    }
    private NoteListener noteListener;
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.item_container_note,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder,int position) {
        Note curNote=notes.get(position);
        holder.setNote(notes.get(position));
        holder.layoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteListener.onNoteClicked(curNote,holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static  class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView textTitle,textSubtitle,textDateTime;
        LinearLayout layoutNote;
        RoundedImageView roundedImageView;
         NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle=itemView.findViewById(R.id.item_textTitle);
             textSubtitle=itemView.findViewById(R.id.item_textSubtitle);
             textDateTime=itemView.findViewById(R.id.item_textDateTime);
             layoutNote=itemView.findViewById(R.id.layoutNote);
             roundedImageView=itemView.findViewById(R.id.imageNote);

        }
        void setNote(Note note){
            textTitle.setText(note.getTitle());
            if(note.getSubtitle().trim().isEmpty())
            {
                textSubtitle.setVisibility(View.GONE);
            }else textSubtitle.setText(note.getSubtitle());
            textDateTime.setText(note.getDateTime());

            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();
            if(note.getColor() != null){
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            }else{
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }
            if(note.getImagePath()!=null)
            {
                roundedImageView.setImageURI(Uri.parse(note.getImagePath()));
                roundedImageView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void searchNote(final String searchKeyword){

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(searchKeyword.trim().isEmpty()){
                    notes = notesSource;
                }
                else{
                    ArrayList<Note> temp = new ArrayList<>();
                    for(Note n : notesSource){
                        if(n.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                        || n.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase())
                        || n.getNoteText().toLowerCase().contains(searchKeyword.toLowerCase())
                        ){
                            temp.add(n);
                        }
                    }
                    notes = temp;
                }

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500);

    }

    public void cancelTimer(){
        if(timer != null){
            timer.cancel();
        }
    }

}
