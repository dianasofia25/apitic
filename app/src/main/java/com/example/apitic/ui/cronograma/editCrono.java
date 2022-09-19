package com.example.apitic.ui.cronograma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apitic.MainActivity;
import com.example.apitic.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class editCrono extends AppCompatActivity {

    FirebaseAuth fAuth = FirebaseAuth.getInstance(); // Autorizacion firebase
    FirebaseUser User = fAuth.getCurrentUser();
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    Intent data;
    Spinner estados;
    EditText editTitle,editId,editNoteA;
    TextView editDateI,editDateF;
    ProgressBar pb;
    FloatingActionButton fab;
    String userEmail;
    {
        assert User != null;
        userEmail = User.getEmail();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_crono);
        Toolbar toolbar = findViewById(R.id.toolbarEditCrono);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = getIntent();

        String titulo = data.getStringExtra("titulo");
        String id = data.getStringExtra("id");
        String dateI = data.getStringExtra("dateI");
//        String dateF = data.getStringExtra("dateF"); no es necesario traer este dato
        String state = data.getStringExtra("state");
        String note = data.getStringExtra("note");

        estados = (Spinner) findViewById(R.id.editSCrono_spinner);

        List<String> x = Arrays.asList("Ejecutandose","Parada de reloj", "Finalizado");

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getApplicationContext(),R.layout.simple_spinner,x);
        adapter.setDropDownViewResource(R.layout.simple_dropdown_spinner);
        estados.setAdapter(adapter);

        Calendar currentTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss aaa");
        String dateTime = simpleDateFormat.format(currentTime.getTime());
        editDateF = (TextView) findViewById(R.id.editFCrono);
        editDateF.setText("Ultima actualización: \n"+dateTime);

        editTitle = (EditText) findViewById(R.id.editCronoTitulo);
        editTitle.setText(titulo);
        editId = (EditText) findViewById(R.id.editIdCrono);
        editId.setText(id);
        editDateI = (TextView) findViewById(R.id.editICrono);
        editDateI.setText("Fecha inicio: \n"+dateI);
        editNoteA = (EditText) findViewById(R.id.editNoteCrono);
        editNoteA.setText(note);

        pb = (ProgressBar) findViewById(R.id.progressBarEditCrono);

        fab = (FloatingActionButton) findViewById(R.id.editFloatB);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String addTitle = editTitle.getText().toString();
                String addId = editId.getText().toString();
                String addState = estados.getSelectedItem().toString();
                String addNote = editNoteA.getText().toString();

                if (addTitle.isEmpty() || addId.isEmpty() || addNote.isEmpty()){
                    Toast.makeText(editCrono.this, "Campos vacios. Intente de nuevo", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (addId.length() > 8){ /// ID MINTIC QUE ES EL MAS LARGO
                    Toast.makeText(editCrono.this, "Coloque el ID Beneficiario o ID Mintic", Toast.LENGTH_SHORT).show();
                    return;
                }

                /// no es necesario enviar el user ni la fecha inicio ///
                DocumentReference documentReference = fstore.collection("Cronograma")
                        .document(User.getUid()).collection("miCrono")
                        .document(data.getStringExtra("cronoId")); //// REFERENCIA DEL DOCUMENTO

                Map<String,Object> cronograma = new HashMap<>();
                cronograma.put("title",addTitle);
                cronograma.put("id",addId);
                cronograma.put("finalDate",dateTime);
                cronograma.put("state",addState);
                cronograma.put("note",addNote);

                documentReference.update(cronograma).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(editCrono.this, "Actualización guardada.", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(editCrono.this, "Error! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.VISIBLE);
                    }
                });
                pb.setVisibility(View.VISIBLE);

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(item.getItemId() == android.R.id.home){
            Toast.makeText(this, "Actualizacion no guardada.", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    ////////// HIDE KEYBOARD WHEN SCREEN IS TOUCHED, IT WORKS IN ALL FRAGMENTS ///////////
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

}