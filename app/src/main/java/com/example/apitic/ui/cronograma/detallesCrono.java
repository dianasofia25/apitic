package com.example.apitic.ui.cronograma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.apitic.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class detallesCrono extends AppCompatActivity {

    FloatingActionButton fab;
    Intent data;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_crono);
        Toolbar toolbarC = findViewById(R.id.toolbarC);
        setSupportActionBar(toolbarC);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = getIntent();

        TextView id = findViewById(R.id.idCrono);
        TextView dateI = findViewById(R.id.iCrono);
        TextView dateF = findViewById(R.id.fCrono);
        TextView state = findViewById(R.id.sCrono);
        TextView note = findViewById(R.id.nCrono);

        TextView titulo = findViewById(R.id.detalleCronoTitulo);

        titulo.setText(data.getStringExtra("titulo"));
        id.setText("ID:\n"+data.getStringExtra("id"));
        dateI.setText("Fecha inicio:\n"+data.getStringExtra("dateI"));
        dateF.setText("Ultima actualizacion:\n"+data.getStringExtra("dateF"));
        state.setText("Estado:\n"+data.getStringExtra("state"));
        note.setText("Nota:\n"+data.getStringExtra("note"));

        fab = (FloatingActionButton) findViewById(R.id.detallesFloatB);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(detallesCrono.this,editCrono.class);
                i.putExtra("titulo",data.getStringExtra("titulo"));
                i.putExtra("id",data.getStringExtra("id"));
                i.putExtra("dateI",data.getStringExtra("dateI"));
                i.putExtra("dateF",data.getStringExtra("dateF"));
                i.putExtra("state",data.getStringExtra("state"));
                i.putExtra("note",data.getStringExtra("note"));
                i.putExtra("cronoId",data.getStringExtra("cronoId"));
                startActivity(i);
                finish();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}