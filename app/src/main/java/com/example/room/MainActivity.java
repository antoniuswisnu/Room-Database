package com.example.room;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.papb24_11.R;
import com.example.room.database.Note;
import com.example.room.database.NoteDao;
import com.example.room.database.NoteRoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private NoteDao mNoteDao;
    private ExecutorService executorService;
    private ListView listView;
    private int id = 0;
    private Button buttonAddNote, buttonEditNote;
    private EditText etTitle, etDesc,etDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.lv1);
        buttonAddNote = findViewById(R.id.btn1);
        buttonEditNote = findViewById(R.id.btn2);
        etTitle = findViewById(R.id.et1);
        etDesc = findViewById(R.id.et2);
        etDate = findViewById(R.id.et3);

        //untuk menjalankan di background
        executorService = Executors.newSingleThreadExecutor();

        NoteRoomDatabase db = NoteRoomDatabase.getDatabase(this);
        mNoteDao = db.noteDao();
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = etTitle.getText().toString();
                String desc = etDesc.getText().toString();
                String date = etDate.getText().toString();
                insertData(new Note(title,desc,date));
                setEmptyField();

            }
        });

        getAllNotes();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Note item = (Note) adapterView.getAdapter().getItem(i);
                id = item.getId();
                etTitle.setText(item.getTitle());
                etDesc.setText(item.getDescription());
                etDate.setText(item.getDate());
            }
        });

        buttonEditNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateData(new Note(id, etTitle.getText().toString(),
                    etDesc.getText().toString(), etDate.getText().toString()));
                id = 0;
                setEmptyField();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Note item = (Note) adapterView.getAdapter().getItem(i);
                deleteData(new Note(item.getId(), item.getTitle(), item.getDescription(), item.getDate()));
                return false;
            }
        });
    }

    private void setEmptyField(){
            etTitle.setText("");
            etDate.setText("");
            etDesc.setText("");
    }

    //function get all note
    private void getAllNotes(){
        mNoteDao.getAllNotes().observe(this,notes -> {
            ArrayAdapter<Note>adapter = new ArrayAdapter<Note>
                (this, android.R.layout.simple_list_item_1, notes);
            listView.setAdapter(adapter);

        });
    }

    //function insert data ke room
    private void insertData(Note note){
        executorService.execute(()->mNoteDao.insert(note));
    }

    //function update data
    private void updateData(Note note){
        executorService.execute(()->mNoteDao.update(note));
    }

    //function delete
    private void deleteData(Note note){
        executorService.execute(()->mNoteDao.delete(note));
    }


}
