package com.example.firebasedsc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Note> notes = new ArrayList<>();
    NotesAdapter notesAdapter = new NotesAdapter();
    DatabaseReference notesReference;

    RecyclerView recyclerView;
    EditText noteInput;
    ImageButton sendButton;

    String name = "reinhard";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noteInput = findViewById(R.id.noteInput);
        sendButton = findViewById(R.id.sendButton);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(notesAdapter);

        notesReference = FirebaseDatabase.getInstance().getReference().child("notes");
        registerChildEventListener();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteString = noteInput.getText().toString();
                String noteId = notesReference.push().getKey();
                notesReference.child(noteId).setValue(new Note(name, noteString), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        hideKeyboard(MainActivity.this);
                        noteInput.setText("");
                        noteInput.clearFocus();
                    }
                });
            }
        });

    }


    public void registerChildEventListener(){
        notesReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Note note = dataSnapshot.getValue(Note.class);

                String noteId = dataSnapshot.getKey();
                note.setId(noteId);

                notes.add(note);
                notesAdapter.setNotes(notes);
                notesAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(notes.size());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Note note = dataSnapshot.getValue(Note.class);
                String noteId = dataSnapshot.getKey();
                note.setId(noteId);

                int noteIndex = getNoteIndex(noteId);
                notes.remove(noteIndex);
                notes.add(noteIndex, note);
                notesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // Note note = dataSnapshot.getValue(Note.class);
                String noteId = dataSnapshot.getKey();
                int index = getNoteIndex(noteId);
                notes.remove(index);
                notesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Note note = dataSnapshot.getValue(Note.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public int getNoteIndex(String noteId){
        int pos = 0;
        for (Note note: notes){
            if (note.getId().equals(noteId)){
                return pos;
            }
            pos++;
        }

        return -1;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


}
