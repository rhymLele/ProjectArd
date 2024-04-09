package com.example.projectard.listeners;

import com.example.projectard.entity.Note;

public interface NoteListener {
    void onNoteClicked(Note note, int position);
}
