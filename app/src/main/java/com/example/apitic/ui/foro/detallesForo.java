package com.example.apitic.ui.foro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import com.example.apitic.ui.clases.Respuesta;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.apitic.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class detallesForo extends AppCompatActivity {

    FloatingActionButton fab;
    Intent data;
    TextView userP,dateP,descriptionP,titleP,dateE,categoriaP;
    RecyclerView recyclerView;
    private final ArrayList<Respuesta> respuestasList = new ArrayList<>();
    RespuestaAdapter respuestasAdapter;
    EditText editTextResputa;
    TextView cantidadRespuestas;
    ImageView imageEnviar;
    ProgressBar pb;
    SwipeRefreshLayout swipeRefreshLayout;

    FirebaseAuth fAuth = FirebaseAuth.getInstance(); // Autorizacion firebase
    FirebaseUser User = fAuth.getCurrentUser();
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    String userEmail;
    {
        assert User != null;
        userEmail = User.getEmail();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_foro);
        Toolbar toolbar = findViewById(R.id.toolbarDetallesForo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Calendar currentTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss aaa");
        String dateTime = simpleDateFormat.format(currentTime.getTime());

        data = getIntent();

        userP = (TextView)findViewById(R.id.userQR);
        dateP = (TextView)findViewById(R.id.tiempoQR);
        titleP = (TextView)findViewById(R.id.tituloPR);
        descriptionP = (TextView)findViewById(R.id.contenidoPR);
        dateE = (TextView)findViewById(R.id.editadoDetalles);
        categoriaP = (TextView) findViewById(R.id.categoriaDetalles);

        userP.setText(data.getStringExtra("userP"));
        dateP.setText(data.getStringExtra("dateP"));
        titleP.setText(data.getStringExtra("title"));
        descriptionP.setText(data.getStringExtra("description"));
        categoriaP.setText(data.getStringExtra("categoria"));

        if(!data.getStringExtra("dateE").isEmpty()){
            dateE.setVisibility(View.VISIBLE);
            dateE.setText(data.getStringExtra("dateE"));
        }

        pb = (ProgressBar)findViewById(R.id.progressBarAddRta);
        editTextResputa = (EditText)findViewById(R.id.editTextRespuesta);
        imageEnviar = (ImageView)findViewById(R.id.btnSendRta);
        imageEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String respuestaEdit = editTextResputa.getText().toString().trim();
                if(TextUtils.isEmpty(respuestaEdit)){
                    Toast.makeText(detallesForo.this, "Respuesta vacia", Toast.LENGTH_SHORT).show();
                    return;
                }
                editTextResputa.setText(null);

                DocumentReference documentReference = fstore.collection("Foro")
                        .document(data.getStringExtra("keyP")).collection("Respuestas").document();
                Map<String,Object> respuestaForo = new HashMap<>();
                respuestaForo.put("descriptionR",respuestaEdit);
                respuestaForo.put("dateR",getDateFromString(dateTime));
                respuestaForo.put("userR",userEmail);

                documentReference.set(respuestaForo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(detallesForo.this, "Respuesta publicada.", Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.INVISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(detallesForo.this, "Error! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.VISIBLE);
                    }
                });
                editTextResputa.setText(null);
                pb.setVisibility(View.VISIBLE);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.respuestasList);
        setRecyclerView();

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayoutFR);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setRecyclerView();
                swipeRefreshLayout.setRefreshing(false);
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

    private void setRecyclerView(){

        ArrayList<String> respuestasId = new ArrayList<>();

        Query query = fstore.collection("Foro")
                .document(data.getStringExtra("keyP"))
                .collection("Respuestas").orderBy("dateR",Query.Direction.ASCENDING);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                respuestasList.clear();
                respuestasId.clear();
                if(!value.isEmpty()){
                    for(QueryDocumentSnapshot respuestas: value){
                        Respuesta respuesta = respuestas.toObject(Respuesta.class);
                        respuestasList.add(respuesta);
                        respuestasId.add(respuestas.getId());
                    }
                }

                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(detallesForo.this));
                respuestasAdapter = new RespuestaAdapter(detallesForo.this,respuestasList
                        ,respuestasId,data.getStringExtra("keyP"));
                recyclerView.setAdapter(respuestasAdapter);
                recyclerView.setItemAnimator(null);

                cantidadRespuestas = (TextView) findViewById(R.id.cantidadRta);
                if(respuestasAdapter.getItemCount() == 1){
                    cantidadRespuestas.setText(respuestasAdapter.getItemCount()+" Respuesta");
                } else {
                    cantidadRespuestas.setText(respuestasAdapter.getItemCount()+" Respuestas");
                }
            }
        });
        respuestasList.clear();
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