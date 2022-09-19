package com.example.apitic.ui.foro;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apitic.R;
import com.example.apitic.ui.clases.Respuesta;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RespuestaAdapter extends RecyclerView.Adapter<RespuestaAdapter.ViewHolder> {

    Activity activity;

    LayoutInflater inflater;
    ArrayList<Respuesta> respuestas;
    ArrayList<String> respuestasId;
    String preguntaId;
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser user = fAuth.getCurrentUser();
    String userEmail;
    {
        assert user != null;
        userEmail = user.getEmail();
    }
    Intent data;

    public RespuestaAdapter(Activity a,ArrayList<Respuesta> respuestas
            ,ArrayList<String> respuestasId, String preguntaId) {
        if(a != null){
            activity = a;
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.respuestas = respuestas;
            this.respuestasId = respuestasId;
            this.preguntaId = preguntaId;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.answer_view_layout, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Respuesta respuesta = respuestas.get(position);
        holder.userR.setText(respuesta.getUserR());
//        holder.dateR.setText(respuesta.getDateR().toString());
        holder.descriptionR.setText(respuesta.getDescriptionR());
        String respuestaId = respuestasId.get(position);

        ///////// PASAR DE TIMESTAMP EN FIRESTORE A SIMPLEDATEFORMAT ///////
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss aaa");
        String dateTime = simpleDateFormat.format(respuesta.getDateR().getTime());
        holder.dateR.setText(dateTime);

        Calendar currentTime = Calendar.getInstance();
        String dateTimeUpdate = simpleDateFormat.format(currentTime.getTime());

        ImageView settingsIcon = holder.view.findViewById(R.id.settingsIconQ);
        EditText editTextRta = activity.findViewById(R.id.editTextRespuesta);
        ImageView btnEnviar = activity.findViewById(R.id.btnSendRta);
        ImageView btnEnviar2 = activity.findViewById(R.id.btnSendRta2);

        if(respuesta.getUserR().equals(userEmail)){ ///// SI EL USUARIO ES EL MISMO QUE EL QUE  HIZO LA PREGUNTA

            holder.userR.setTextColor(Color.parseColor("#FF7800"));

            if(activity.getClass().getSimpleName().equals("detallesForo")){

                ////// CLIC EN LOS TRES PUNTOS PARA EDITAR O ELIMINAR NOTA /////////
                settingsIcon.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        PopupMenu menu = new PopupMenu(v.getContext(),v);
                        menu.setGravity(Gravity.END);
                        menu.getMenu().add("Editar").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                editTextRta.setText(respuesta.getDescriptionR());
//                            holder.view.setVisibility(View.GONE);

                                System.out.println("HOLAAA: "+activity.getClass().getSimpleName());

                                /////////// TO SET CURSOR ON A EDITTEXT AND DISPLAY KEYBOARD //////////
                                int position = respuesta.getDescriptionR().length();
                                editTextRta.requestFocus();
                                InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                                editTextRta.setSelection(position);

                                btnEnviar.setVisibility(View.GONE);
                                btnEnviar2.setVisibility(View.VISIBLE);

                                btnEnviar2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DocumentReference documentReference = fstore.collection("Foro")
                                                .document(preguntaId)
                                                .collection("Respuestas")
                                                .document(respuestaId); //// REFERENCIA DEL DOCUMENTO

                                        documentReference.update("descriptionR",editTextRta.getText().toString()+" (Editado)"
                                                ,"dateR", getDateFromString(dateTimeUpdate))
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @SuppressLint("SetTextI18n")
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(v.getContext(), "Actualizada", Toast.LENGTH_SHORT).show();
                                                        editTextRta.setText(null);
                                                        btnEnviar.setVisibility(View.VISIBLE);
                                                        btnEnviar2.setVisibility(View.GONE);

                                                        imm.hideSoftInputFromWindow(editTextRta.getWindowToken(), 0);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(v.getContext(), "Error! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                                return false;
                            }

                        });

                        menu.getMenu().add("Eliminar").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                DocumentReference documentReference = fstore.collection("Foro")
                                        .document(preguntaId)
                                        .collection("Respuestas")
                                        .document(respuestaId); //// REFERENCIA DEL DOCUMENTO

                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(v.getContext(), "Respuesta eliminada", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(v.getContext(), "Error! "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });
                        menu.show();
                    }
                });

            } else {
                ///// EN MODO EDITAR PREGUNTAR NO SE PUEDE EDITAR RESPUESTA
                settingsIcon.setVisibility(View.INVISIBLE);
            }
        } else{
            ///// USUARIO DIFERENTE AL QUE HIZO LA PREGUNTA
            settingsIcon.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return respuestas.size();
    }

    public Date getDateFromString(String datetoSaved){
        try {
            @SuppressLint("SimpleDateFormat")
            Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss aaa").parse(datetoSaved);
            return date;
        } catch (ParseException e){ return null; }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userR,dateR,descriptionR;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userR = itemView.findViewById(R.id.usuarioR);
            dateR = itemView.findViewById(R.id.tiempoR);
            descriptionR = itemView.findViewById(R.id.contenidoR);
            view = itemView;
        }
    }
}
