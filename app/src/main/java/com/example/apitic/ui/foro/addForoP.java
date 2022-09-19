package com.example.apitic.ui.foro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.apitic.R;
import com.example.apitic.ui.cronograma.addCrono;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.apitic.databinding.ActivityAddForoPBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class addForoP extends AppCompatActivity {

    private FloatingActionButton fab;
    FirebaseAuth fAuth = FirebaseAuth.getInstance(); // Autorizacion firebase
    FirebaseUser User = fAuth.getCurrentUser();
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    Spinner categorias;
    EditText title,description;
    TextView date,eUser;
    ProgressBar pb;
    String user;
    {
        assert User != null;
        user = User.getEmail();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_foro_p);
        Toolbar toolbar = findViewById(R.id.toolbarAddForo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        categorias = (Spinner) findViewById(R.id.addCateConfig_spinner);

        List<String> x = Arrays.asList("Configuracion antena satelital"
                ,"Configuracion access point", "Configuracion radio", "Configuracion router"
                ,"Configuracion UPS", "Otro");

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getApplicationContext(),R.layout.simple_spinner,x);
        adapter.setDropDownViewResource(R.layout.simple_dropdown_spinner);
        categorias.setAdapter(adapter);

        fab = (FloatingActionButton)findViewById(R.id.fabAddP);
        title = (EditText) findViewById(R.id.addTituloForo);
        description = (EditText) findViewById(R.id.addDescriptionForo);
        date = (TextView) findViewById(R.id.addDateForo);
        eUser = (TextView) findViewById(R.id.addEmailUser);
        eUser.setText("Usuario: \n"+user);
        pb = (ProgressBar) findViewById(R.id.progressBarAddForo);

        Calendar currentTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss aaa");
        String dateTime = simpleDateFormat.format(currentTime.getTime());
        date.setText("Fecha: \n"+dateTime);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dateE = "";
                String addTitle = title.getText().toString();
                String addDescription = description.getText().toString();
                String addCategoria = categorias.getSelectedItem().toString();

                if (addTitle.isEmpty() || addDescription.isEmpty()){
                    Toast.makeText(addForoP.this, "Campos vacios. Intente de nuevo", Toast.LENGTH_SHORT).show();
                    return;
                }

                DocumentReference documentReference = fstore.collection("Foro").document();
                Map<String,Object> preguntaForo = new HashMap<>();
                preguntaForo.put("title",addTitle);
                preguntaForo.put("description",addDescription);
                preguntaForo.put("categoria",addCategoria);
                preguntaForo.put("dateP",getDateFromString(dateTime));
                preguntaForo.put("dateE", dateE);
                preguntaForo.put("userP",user);

                documentReference.set(preguntaForo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(addForoP.this, "Pregunta publicada.", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(addForoP.this, "Error! "+e.getMessage(), Toast.LENGTH_SHORT).show();
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