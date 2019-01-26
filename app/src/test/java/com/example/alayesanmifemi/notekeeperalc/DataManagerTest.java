package com.example.alayesanmifemi.notekeeperalc;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Alayesanmi Femi on 25/01/2019.
 */
public class DataManagerTest {
    @Test
    public void createNewNote() throws Exception {
        final DataManager dm = DataManager.getInstance();
        final CourseInfo course = dm.getCourse("android_async");
        final String noteTitle = "Test Note Title";
        final String noteText = "This is the body of the note";

        int noteIndex = dm.createNewNote();
        NoteInfo newNote = dm.getNotes().get(noteIndex);
        newNote.setCourse(course);
        newNote.setTitle(noteTitle);
        newNote.setText(noteText);


        NoteInfo compareNote = dm.getNotes().get(noteIndex);
        assertEquals(compareNote.getText(), noteText);

    }

}