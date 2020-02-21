package com.example.firebasedsc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Note> notes;
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

        notesReference = FirebaseDatabase.getInstance().getReference().child("notes");

        noteInput = findViewById(R.id.noteInput);
        sendButton = findViewById(R.id.sendButton);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(notesAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noteString = noteInput.getText().toString();
                String noteId = notesReference.push().getKey();
                notesReference.child(noteId).setValue(new Note(name, noteString));
            }
        });

        notesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<Note> notes = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String noteId = snapshot.getKey();
                    Note note = snapshot.getValue(Note.class);
                    note.setId(noteId);
                    
                    Toast.makeText(getApplicationContext(), note.getName(), Toast.LENGTH_SHORT).show();
                    notes.add(note);
                }

                notesAdapter.setNotes(notes);
                notesAdapter.notifyItemInserted(notes.size());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
