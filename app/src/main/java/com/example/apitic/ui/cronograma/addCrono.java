package com.example.apitic.ui.cronograma;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import com.example.apitic.R;
import com.example.apitic.ui.auth.Login;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class addCrono extends AppCompatActivity {

    FirebaseAuth fAuth = FirebaseAuth.getInstance(); // Autorizacion firebase
    FirebaseUser User = fAuth.getCurrentUser();
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    Spinner estados;
    EditText title,id,noteA;
    TextView dateI,dateF;
    FloatingActionButton fab;
    ProgressBar pb;
    String user;
    {
        assert User != null;
//        user = User.getEmail();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_crono);
        Toolbar toolbar = findViewById(R.id.toolbarAddCrono);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        estados = (Spinner) findViewById(R.id.addSCrono_spinner);

        List<String> x = Arrays.asList("Ejecutandose","Parada de reloj", "Finalizado");

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getApplicationContext(),R.layout.simple_spinner,x);
        adapter.setDropDownViewResource(R.layout.simple_dropdown_spinner);
        estados.setAdapter(adapter);

        Calendar currentTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss aaa");
        String dateTime = simpleDateFormat.format(currentTime.getTime());
        dateI = (TextView) findViewById(R.id.addICrono);
        dateI.setText("Fecha inicio: \n"+dateTime);

        title = (EditText) findViewById(R.id.addCronoTitulo);
        id = (EditText) findViewById(R.id.addIdCrono);
        dateF = (TextView) findViewById(R.id.addFCrono);
        dateF.setText("Ultima actualización: \n"+dateTime);
        noteA = (EditText) findViewById(R.id.addNoteCrono);

        pb = (ProgressBar) findViewById(R.id.progressBarAddCrono);

        fab = (FloatingActionButton) findViewById(R.id.addFloatB);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String addTitle = title.getText().toString();
                String addId = id.getText().toString();
                String addState = estados.getSelectedItem().toString();
                String addNote = noteA.getText().toString();

                if (addTitle.isEmpty() || addId.isEmpty() || addNote.isEmpty()){
                    Toast.makeText(addCrono.this, "Campos vacios. Intente de nuevo", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (addId.length() > 8){ /// ID MINTIC QUE ES EL MAS LARGO
                    Toast.makeText(addCrono.this, "Coloque el ID Beneficiario o ID Mintic", Toast.LENGTH_SHORT).show();
                    return;
                }

                DocumentReference documentReference = fstore.collection("Cronograma")
                        .document(User.getUid()).collection("miCrono").document();
                Map<String,Object> cronograma = new HashMap<>();
                cronograma.put("title",addTitle);
                cronograma.put("id",addId);
                cronograma.put("initialDate",getDateFromString(dateTime));
                cronograma.put("finalDate",dateTime);
                cronograma.put("state",addState);
                cronograma.put("note",addNote);
                cronograma.put("user",user);

                documentReference.set(cronograma).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(addCrono.this, "Actividad guardada.", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(addCrono.this, "Error! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.VISIBLE);
                    }
                });
                pb.setVisibility(View.VISIBLE);

            }
        });

    }

    public Date getDateFromString(String datetoSaved){
        try {
            @SuppressLint("SimpleDateFormat")
            Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss aaa").parse(datetoSaved);
            return date;
        } catch (ParseException e){ return null; }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(item.getItemId() == android.R.id.home){
            Toast.makeText(this, "Actividad no guardada.", Toast.LENGTH_SHORT).show();
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