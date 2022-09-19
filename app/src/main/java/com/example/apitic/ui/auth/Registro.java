package com.example.apitic.ui.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.apitic.R;
import com.example.apitic.ui.clases.Tecnicos;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;

public class Registro extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    boolean PasswordVisible;
    EditText rFullName,rEmail,rPassword1,rPhoneN,rBirthD;
    Button Register;
    ProgressBar progressbar;
    FirebaseAuth fAuth;

    DatabaseReference dbTecnicos;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rFullName = findViewById(R.id.rFullname);
        rEmail = findViewById(R.id.rEmail);
        rPassword1 = findViewById(R.id.editTextPassword1);
        rPhoneN = findViewById(R.id.phoneNumber1);
        rBirthD = findViewById(R.id.rBirthdate);
        Register = findViewById(R.id.BRegister);

        progressbar = findViewById(R.id.progressBar1);
        fAuth = FirebaseAuth.getInstance(); // Autorizacion firebase

        dbTecnicos = FirebaseDatabase.getInstance().getReference("Tecnicos"); // Autorizacion firebase database

        //////////// CLIC EN EL BOTON REGISTRARSE //////////////////
        Register.setOnClickListener(new View.OnClickListener() { // clic en registrarse
            @Override
            public void onClick(View view) {
                String email = rEmail.getText().toString().trim();
                String password = rPassword1.getText().toString().trim();
                String phoneNumber = rPhoneN.getText().toString().trim();
                String name = rFullName.getText().toString().trim();
                String BD = rBirthD.getText().toString().trim();

                if (TextUtils.isEmpty(name)){ // Si el campo de email esta vacio
                    rFullName.setError("Nombre Completo requerido");
                    return;
                }
                if (TextUtils.isEmpty(email)){ // Si el campo de email esta vacio
                    rEmail.setError("Correo requerido");
                    return;
                }
                if (TextUtils.isEmpty(password)){ // Si el campo de contraseña esta vacio
                    rPassword1.setError("Contraseña requerida");
                    return;
                }
                if (password.length() < 8){ // contraseña debe tener 8 caracteres
                    rPassword1.setError("Contraseña debe tener 8 caracteres");
                    rPassword1.setText(null);
                    return;
                }
                if (phoneNumber.length() != 10){
                    rPhoneN.setError("Número invalido");
                    rPhoneN.setText(null);
                }
                if (TextUtils.isEmpty(BD)){ // Si el campo de email esta vacio
                    rBirthD.setError("Fecha requerida");
                    return;
                }
                
                progressbar.setVisibility(View.VISIBLE); // progress bar visible

                //////// REGISTRAR USUARIO CON EMAIL Y CONTRASEÑA EN FIREBASE //////////////
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override // Crear un email y contraseña en Firebase
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){ // si se accede correctamente entonces

                            // AÑADIR A LA BASE DE DATOS EN FIREBASE
                            String id = dbTecnicos.push().getKey(); // obtener un ID unico para cada técnico
                            Tecnicos tecnicos = new Tecnicos(id,name,email,phoneNumber,BD);
                            dbTecnicos.child(id).setValue(tecnicos);

                            //enviar correo de verificacion de email para comprobar que el correo es real
                            FirebaseUser User = fAuth.getCurrentUser();
                            assert User != null;
                            User.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) { // Si se envia el correo de verificacion
                                    Toast.makeText(Registro.this, "Link de verificación enviado.",
                                            Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Registro.this, Login.class));
                                }
                            }).addOnFailureListener(new OnFailureListener() { // si no se envia por error
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Registro.this, "Error! No se pudo enviar el link de verificacion.",
                                            Toast.LENGTH_SHORT).show();
                                    rEmail.setText(null);
                                    rFullName.setText(null);
                                    rPassword1.setText(null);
                                    rPhoneN.setText(null);
                                    progressbar.setVisibility(View.INVISIBLE);
                                }
                            });
                        } else {
                            Toast.makeText(Registro.this, "Este usuario ya existe.",Toast.LENGTH_SHORT).show();
                            rEmail.setText(null);
                            rFullName.setText(null);
                            rPassword1.setText(null);
                            rPhoneN.setText(null);
                            rBirthD.setText(null);
                            progressbar.setVisibility(View.INVISIBLE);
                        }
                    }
                });

            }
        });

        ////// CONTRASEÑA VISIBLE E INVISIBLE //////////
        rPassword1.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if (event.getAction()== MotionEvent.ACTION_UP){
                    if( event.getRawX()>=rPassword1.getRight()-rPassword1.getCompoundDrawables()[Right].getBounds().width()){
                        int selection = rPassword1.getSelectionEnd();
                        if (PasswordVisible){
                            // Ocultar contraseña
                            rPassword1.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.lock_24,0,R.drawable.visibility_off_24,0);
                            rPassword1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            PasswordVisible = false;
                        } else {
                            // Mostrar contraseña
                            rPassword1.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.lock_24,0,R.drawable.visibility_24,0);
                            rPassword1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            PasswordVisible =true;
                        }
                        rPassword1.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        ////// IR DE REGISTRARSE A INGRESAR /////////
        TextView I = findViewById(R.id.Iniciasesion);
        I.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent (Registro.this, Login.class);
                startActivity(intent);
            }
        });

        //////////// MOSTRAR FECHA DE NACIMIENTO EN UN DIALOG PARTE 1////////////
        rBirthD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment(); // Mostrar el DatePicker en un dialog
                datePicker.show(getSupportFragmentManager(), "datePicker");
            }
        });
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

    //////////// MOSTRAR FECHA DE NACIMIENTO EN UN DIALOG PARTE 2////////////
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendario = Calendar.getInstance();
        calendario.set(Calendar.YEAR,year);
        calendario.set(Calendar.MONTH,month);
        calendario.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        String Birthdate = DateFormat.getDateInstance().format(calendario.getTime()); // Fecha elegida
        rBirthD.setText(Birthdate);
    }

}