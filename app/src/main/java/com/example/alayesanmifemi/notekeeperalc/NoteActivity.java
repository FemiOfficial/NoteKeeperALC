package com.example.alayesanmifemi.notekeeperalc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    public static final String NOTE_POSITION = "com.example.alayesanmifemi.notekeeperalcNOTE_POSITION";
    public static final String ORIGINAL_NOTE_COURSE_ID = "com.example.alayesanmifemi.notekeeperalc.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.example.alayesanmifemi.notekeeperalc.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.example.alayesanmifemi.notekeeperalc.ORIGINAL_NOTE_TITLE";
    public static final int POSITION_NOT_SET = -1;
    private NoteInfo note;
    private boolean isNewNote;
    private Spinner spinnerCourses;
    private EditText textNoteTitle;
    private EditText textNoteText;
    private int notePosition;
    private boolean isCancelling;
    private String originalCourseNoteID;
    private String originalNoteTitle;
    private String originalNoteText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinnerCourses = (Spinner) findViewById(R.id.spinner_courses);
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses = new
                ArrayAdapter<CourseInfo>(this, android.R.layout.simple_spinner_item, courses);

        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(adapterCourses);

        readDisplayStateValues();
        if(savedInstanceState==null){
            saveOriginalNoteValue();
        }else {
            restoreOriginalNoteValues(savedInstanceState);
        }

        textNoteTitle = (EditText)findViewById(R.id.text_note_title);
        textNoteText = (EditText)findViewById(R.id.text_note_details);

        if(!isNewNote)
            displayNote(spinnerCourses, textNoteText, textNoteTitle);
    }

    private void restoreOriginalNoteValues(Bundle savedInstanceState) {
        originalCourseNoteID = savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
        originalNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
        originalNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
    }

    private void saveOriginalNoteValue() {
        if(isNewNote)
            return;
        originalCourseNoteID = note.getCourse().getCourseId();
        originalNoteTitle = note.getTitle();
        originalNoteText = note.getText();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isCancelling){
            if(isNewNote) {
                DataManager.getInstance().removeNote(notePosition);
            }else{
               storePrevNoteValues(); 
            }
        }else {
            saveNote();
        }
    }

    private void storePrevNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(originalCourseNoteID);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_NOTE_COURSE_ID, originalCourseNoteID);
        outState.putString(ORIGINAL_NOTE_TEXT, originalNoteText);
        outState.putString(ORIGINAL_NOTE_TITLE,originalNoteTitle);
    }

    private void saveNote() {
        note.setCourse((CourseInfo) spinnerCourses.getSelectedItem());
        note.setText(textNoteText.getText().toString());
        note.setTitle(textNoteTitle.getText().toString());
    }

    private void displayNote(Spinner spinnerCourses, EditText textNoteText, EditText textNoteTitle) {
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIndex = courses.indexOf(note.getCourse());
        spinnerCourses.setSelection(courseIndex);
        textNoteText.setText(note.getText());
        textNoteTitle.setText(note.getTitle());

    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        notePosition = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);
        isNewNote = notePosition == POSITION_NOT_SET;

        if(isNewNote){
            createNewNote();
        }
        note = DataManager.getInstance().getNotes().get(notePosition);
    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        notePosition = dm.createNewNote();
//        note = dm.getNotes().get(notePosition);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendEmail();
            return true;
        }else if(id == R.id.action_send_mail){
            isCancelling = true;
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendEmail() {
        CourseInfo course = (CourseInfo) spinnerCourses.getSelectedItem();
        String subject = textNoteText.getText().toString();
        String text = "Checkout what i learnt in my pluralsight course: \n" +
                textNoteTitle.getText().toString() + "\n\n" + textNoteText.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_TITLE, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);


    }
}
