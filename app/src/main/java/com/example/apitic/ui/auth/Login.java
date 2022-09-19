package com.example.apitic.ui.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apitic.MainActivity;
import com.example.apitic.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class Login extends AppCompatActivity {

    boolean PasswordVisible;
    EditText lEmail,lPassword;
    Button Login;
    ProgressBar progressbar;
    FirebaseAuth fAuth = FirebaseAuth.getInstance(); // Autorizacion firebase
    DatabaseReference dbTecnicos;
    FirebaseDatabase db;
    String user;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lEmail = findViewById(R.id.lEmail);
        lPassword = findViewById(R.id.lPassword);
        Login = findViewById(R.id.Login);

        progressbar = findViewById(R.id.progressBar2); // Barra de progreso

        if (fAuth.getCurrentUser() != null){ // Si es usuario ya esta loggeado entonces no mostrar Register
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish(); // no puede volver hacia atras
        }

        // SELECCIONAR CLASE Tecnicos
        dbTecnicos = FirebaseDatabase.getInstance().getReference("Tecnicos"); // Autorizacion firebase database
        // SELECCIONAR DE LA CLASE Tecnicos UN DETERMINADO CHILDREN


        ///////// AL DARLE CLIC EN EL BOTON ACCEDER DEL ACTIVITY LOGIN /////////
        Login.setOnClickListener(new View.OnClickListener() { // clic en registrarse
            @Override
            public void onClick(View view) {
                String email = lEmail.getText().toString().trim();
                String password = lPassword.getText().toString().trim();
                Query query = FirebaseDatabase.getInstance().getReference("Tecnicos")
                        .orderByChild("temail").equalTo(email);

                if (TextUtils.isEmpty(email)) { // Si el campo de email esta vacio
                    lEmail.setError("Correo requerido");
                    return;
                }
                if (TextUtils.isEmpty(password)) { // Si el campo de contraseña esta vacio
                    lPassword.setError("Contraseña requerida");
                    return;
                }
                if (password.length() < 8) { // contraseña debe tener 8 caracteres
                    lPassword.setError("Contraseña debe tener 8 caracteres");
                    return;
                }
                progressbar.setVisibility(View.VISIBLE); // progress bar visible

                // Autentificacion de usuarios
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){ // si se accede correctamente entonces
                            Toast.makeText(Login.this, "Accediendo...",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                        } else {
                            /////// USUARIO NO REGISTRADO Y CONTRASEÑA INCORRECTA ////////
                            query.addValueEventListener(dbTecnicos.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        for (DataSnapshot tSnapshot : snapshot.getChildren()){
                                            String email1 = tSnapshot.child("temail").getValue(String.class);
                                            assert email1 != null;
                                            if(!email1.equals(email)){
                                                Toast.makeText(Login.this, "Usuario no registrado",
                                                        Toast.LENGTH_SHORT).show();
                                                lEmail.setText(null);
                                                lPassword.setText(null);
                                            } else {
                                                Toast.makeText(Login.this, "Contraseña incorrecta",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }else{
                                        Toast.makeText(Login.this, "Usuario no registrado",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(Login.this, error.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }));
                            progressbar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

        ////// HACER CONTRASEÑA VISIBLE E INVISIBLE //////////
        lPassword.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Right = 2;
                if (event.getAction()== MotionEvent.ACTION_UP){
                    if( event.getRawX()>=lPassword.getRight()-lPassword.getCompoundDrawables()[Right].getBounds().width()){
                        int selection = lPassword.getSelectionEnd();
                        if (PasswordVisible){
                            // Ocultar contraseña
                            lPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.lock_24,0,R.drawable.visibility_off_24,0);
                            lPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            PasswordVisible = false;
                        } else {
                            // Mostrar contraseña
                            lPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.lock_24,0,R.drawable.visibility_24,0);
                            lPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            PasswordVisible =true;
                        }
                        lPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        // IR DEL ACTIVITY LOGIN AL REGISTRATE
        TextView I = findViewById(R.id.Registrate);
        I.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent (Login.this, Registro.class);
                startActivity(intent);
            }
        });

        // OLVIDO LA CONTRASEÑA?
        TextView F = findViewById(R.id.fPassword);
        F.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText resetMail = new EditText(view.getContext());
                AlertDialog.Builder resetdialog = new AlertDialog.Builder(view.getContext());
                resetdialog.setTitle("Olvidaste la contraseña?");
                resetdialog.setMessage("Ingresa tu email: "); // se requiere email
                resetdialog.setView(resetMail);

                resetdialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Enviar el link de reset al email
                        String mail = resetMail.getText().toString().toLowerCase(Locale.ROOT);
                        if (TextUtils.isEmpty(mail)){
                            resetMail.setError("Ingresa tu email");
                        } else{
                            fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Login.this,"El link para reestablecer la contraseña enviado.\nRevise el SPAM de su correo.", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Login.this,"Hubo un error. Intentelo de nuevo",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

                resetdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Cerrar la ventana de dialogo
                    }
                });
                resetdialog.create().show(); // mostrar el dialogo
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

}