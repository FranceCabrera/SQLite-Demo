package com.example.sqlitedatabasedemo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    EditText studentId, studentName, studentSemGrade;
    Button addRecord, deleteRecord, editRecord, viewRecord, viewAllRecord;
    SQLiteDatabase db;
    Cursor cursor;
    AlertDialog.Builder builder;
    StringBuffer buffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        addStudentRecord();
        deleteStudentRecord();
        editStudentRecord();
        viewStudentRecord();
        viewAllStudentRecords();
    }

    public void init() {
        studentId = findViewById(R.id.etStudentID);
        studentName = findViewById(R.id.etStudentName);
        studentSemGrade = findViewById(R.id.etStudentSemGrade);
        addRecord = findViewById(R.id.btnAddRecord);
        deleteRecord = findViewById(R.id.btnDeleteRecord);
        editRecord = findViewById(R.id.btnEditRecord);
        viewRecord = findViewById(R.id.btnViewRecord);
        viewAllRecord = findViewById(R.id.btnViewAllRecord);
        builder = new AlertDialog.Builder(this);
        db = openOrCreateDatabase("StudentDB", Context.MODE_PRIVATE, null);
        db.execSQL("DROP TABLE IF EXISTS tbl_students");
        db.execSQL("CREATE TABLE IF NOT EXISTS tbl_student (student_id INTEGER PRIMARY KEY AUTOINCREMENT, student_name TEXT, student_semGrade INTEGER)");
        studentId.setEnabled(false);
        studentName.requestFocus();
    }

    public void displayMessage(String title, String message) {
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public void clearEntries() {
        studentName.setText("");
        studentId.setText("");
        studentSemGrade.setText("");
        studentName.requestFocus();
    }

    public void addStudentRecord() {
        addRecord.setOnClickListener(v -> {
            if (studentName.getText().toString().isEmpty() || studentSemGrade.getText().toString().isEmpty()) {
                displayMessage("Error!", "Please Complete the Entries");
                return;
            }
            db.execSQL("INSERT INTO tbl_student (student_name, student_semGrade) VALUES ('" + studentName.getText().toString() + "', '" + studentSemGrade.getText().toString() + "')");
            displayMessage("Information!", "Student Record has been successfully added");
            clearEntries();
        });
    }

    public void deleteStudentRecord() {
        deleteRecord.setOnClickListener(v -> {
            if (studentId.getText().toString().isEmpty()) {
                studentId.setEnabled(true);
                displayMessage("Information!", "Please Enter a Student ID");
                studentId.requestFocus();
                return;
            }
            cursor = db.rawQuery("SELECT * FROM tbl_student WHERE student_id =" + Integer.parseInt(studentId.getText().toString()), null);
            if (cursor.moveToFirst()) {
                db.execSQL("DELETE FROM tbl_student WHERE student_id =" + Integer.parseInt(studentId.getText().toString()));
                displayMessage("Information!", "Student Record has been successfully deleted!");
            } else {
                displayMessage("Error!", "Invalid Student ID");
            }
            studentId.setEnabled(false);
            clearEntries();
        });
    }

    public void editStudentRecord() {
        editRecord.setOnClickListener(v -> {
            if (studentId.getText().toString().isEmpty()) {
                studentId.setEnabled(true);
                displayMessage("Information!", "Please Enter a Student ID");
                editRecord.setText("SAVE");
                studentId.requestFocus();
                return;
            }
            cursor = db.rawQuery("SELECT * FROM tbl_student WHERE student_id =" + Integer.parseInt(studentId.getText().toString()), null);
            if (cursor.moveToFirst()) {
                db.execSQL("UPDATE tbl_student SET student_name='" + studentName.getText().toString() + "', student_semGrade='" + studentSemGrade.getText().toString() + "' WHERE student_id='" + Integer.parseInt(studentId.getText().toString()) + "'");
                displayMessage("Information!", "Student Record has been successfully modified!");
            } else {
                displayMessage("Error!", "Invalid Student ID");
            }
            editRecord.setText("EDIT");
            studentId.setEnabled(false);
            clearEntries();
        });
    }

    public void viewStudentRecord() {
        viewRecord.setOnClickListener(v -> {
            if (studentId.getText().toString().isEmpty()) {
                studentId.setEnabled(true);
                displayMessage("Information!", "Please Enter a Student ID");
                studentId.requestFocus();
                return;
            }
            cursor = db.rawQuery("SELECT * FROM tbl_student WHERE student_id =" + Integer.parseInt(studentId.getText().toString()), null);
            if (cursor.moveToFirst()) {
                buffer = new StringBuffer();
                buffer.append("Student ID: " + cursor.getString(0) + "\n");
                buffer.append("Student Name: " + cursor.getString(1) + "\n");
                buffer.append("Student SemGrade: " + cursor.getString(2) + "\n---------");
                displayMessage("Student Record", buffer.toString());
            } else {
                displayMessage("Error!", "Invalid Student ID");
            }
            studentId.setEnabled(false);
            clearEntries();
        });
    }

    public void viewAllStudentRecords() {
        viewAllRecord.setOnClickListener(v -> {
            cursor = db.rawQuery("SELECT * FROM tbl_student", null);
            if (cursor.getCount() == 0) {
                displayMessage("Information", "No Student Records Found!");
                return;
            }
            buffer = new StringBuffer();
            while (cursor.moveToNext()) {
                buffer.append("Student ID: " + cursor.getString(0) + "\n");
                buffer.append("Student Name: " + cursor.getString(1) + "\n");
                buffer.append("Student SemGrade: " + cursor.getString(2) + "\n----------\n");
            }
            displayMessage("Student Records", buffer.toString());
        });
    }
}
