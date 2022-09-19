package com.example.apitic.ui.foro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

import com.example.apitic.R;
import com.example.apitic.ui.clases.Respuesta;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class editForoP extends AppCompatActivity {

    FloatingActionButton fab;
    ProgressBar pb;
    TextView userEdit, dateEditI,dateEditE,cantidadRespuestas;
    EditText editP, editC;
    Spinner categoriaEdit;
    Intent data;
    RecyclerView recyclerView;
    private final ArrayList<Respuesta> respuestasList = new ArrayList<>();
    RespuestaAdapter respuestasAdapter;

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
        setContentView(R.layout.activity_edit_foro_p);
        Toolbar toolbar = findViewById(R.id.toolbarEditForoP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = getIntent();

        userEdit = (TextView) findViewById(R.id.userEditP);
        dateEditI = (TextView) findViewById(R.id.tiempoEditP);
        editC = (EditText)findViewById(R.id.contenidoEditP);
        editP = (EditText) findViewById(R.id.tituloEditP);
        dateEditE = (TextView)findViewById(R.id.editadoEdit);
        categoriaEdit = (Spinner)findViewById(R.id.categoriaEditP);

        List<String> x = Arrays.asList("Configuracion antena satelital"
                ,"Configuracion access point", "Configuracion radio", "Configuracion router"
                ,"Configuracion UPS", "Otro");

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getApplicationContext(),R.layout.simple_spinner,x);
        adapter.setDropDownViewResource(R.layout.simple_dropdown_spinner);
        categoriaEdit.setAdapter(adapter);

        userEdit.setText(data.getStringExtra("userP"));
        dateEditI.setText(data.getStringExtra("dateP"));
        editP.setText(data.getStringExtra("title"));
        editC.setText(data.getStringExtra("description"));

        /////// Set cursor and display keyboard //////
        int position = data.getStringExtra("description").length();
        editC.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        editC.setSelection(position);

        if(!data.getStringExtra("dateE").isEmpty()){
            dateEditE.setVisibility(View.VISIBLE);
            dateEditE.setText(data.getStringExtra("dateE"));
        }

        recyclerView = (RecyclerView) findViewById(R.id.respuestasListEditP);
        setRecyclerView();

        Calendar currentTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss aaa");
        String dateTimeE = simpleDateFormat.format(currentTime.getTime());

        fab = (FloatingActionButton) findViewById(R.id.fabEditForoP);
        pb = (ProgressBar) findViewById(R.id.progressBarEditP);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String addEditP = editP.getText().toString();
                String addEditC = editC.getText().toString();
                String addEditCategoria = categoriaEdit.getSelectedItem().toString();

                if (addEditP.isEmpty() || addEditC.isEmpty()){
                    Toast.makeText(editForoP.this, "Campos vacios. Intente de nuevo", Toast.LENGTH_SHORT).show();
                    return;
                }

                DocumentReference documentReference = fstore.collection("Foro")
                        .document(data.getStringExtra("keyP"));

                Map<String,Object> pregunta = new HashMap<>();
                pregunta.put("title",addEditP);
                pregunta.put("description",addEditC);
                pregunta.put("dateE",dateTimeE);
                pregunta.put("categoria",addEditCategoria);

                documentReference.update(pregunta).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(editForoP.this, "Actualización guardada.", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(editForoP.this, "Error! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        pb.setVisibility(View.VISIBLE);
                    }
                });

                pb.setVisibility(View.VISIBLE);

            }
        });
    }

    ///////////////// MENU DE OPCIONES //////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cancel_foro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            onBackPressed();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cancelForo) {
            Toast.makeText(editForoP.this, "Actualización cancelada", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(editForoP.this, detallesForo.class);
            i.putExtra("userP",data.getStringExtra("userP"));
            i.putExtra("title",data.getStringExtra("title"));
            i.putExtra("dateP",data.getStringExtra("dateP"));
            i.putExtra("dateE",data.getStringExtra("dateE"));
            i.putExtra("description",data.getStringExtra("description"));
            i.putExtra("categoria",data.getStringExtra("categoria"));
            i.putExtra("keyP",data.getStringExtra("keyP"));
            startActivity(i);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
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
                recyclerView.setLayoutManager(new LinearLayoutManager(editForoP.this));
                respuestasAdapter = new RespuestaAdapter(editForoP.this,respuestasList
                        ,respuestasId,data.getStringExtra("keyP"));
                recyclerView.setAdapter(respuestasAdapter);
                recyclerView.setItemAnimator(null);

                cantidadRespuestas = (TextView) findViewById(R.id.cantidadRtaEditP);
                if(respuestasAdapter.getItemCount() == 1){
                    cantidadRespuestas.setText(respuestasAdapter.getItemCount()+" Respuesta");
                } else {
                    cantidadRespuestas.setText(respuestasAdapter.getItemCount()+" Respuestas");
                }
            }
        });
        respuestasList.clear();
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