package com.example.projectard.adapters;

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

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> notes;

    public NoteAdapter(List<Note> notes,NoteListener noteListener) {
        this.notes = notes;
        this.noteListener=noteListener;
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
         NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle=itemView.findViewById(R.id.item_textTitle);
             textSubtitle=itemView.findViewById(R.id.item_textSubtitle);
             textDateTime=itemView.findViewById(R.id.item_textDateTime);
             layoutNote=itemView.findViewById(R.id.layoutNote);
        }
        void setNote(Note note){
        textTitle.setText(note.getTitle());
        if(note.getSubtitle().trim().isEmpty())
        {
            textSubtitle.setVisibility(View.GONE);
        }else textSubtitle.setText(note.getSubtitle());
        textDateTime.setText(note.getDateTime());
        }
    }
}
