package com.example.apitic.ui.foro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apitic.ui.clases.Pregunta;
import com.example.apitic.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ForoAdapter extends RecyclerView.Adapter<ForoAdapter.ViewHolder> {

    LayoutInflater inflater;
    ArrayList<Pregunta> preguntas;
    ArrayList<String> preguntasId;
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser user = fAuth.getCurrentUser();
    String userEmail;
    {
        assert user != null;
        userEmail = user.getEmail();
    }

    public ForoAdapter(Context context, ArrayList<Pregunta> preguntas, ArrayList<String> preguntasId) {
        if(context != null){
            this.inflater = LayoutInflater.from(context);
            this.preguntas = preguntas;
            this.preguntasId = preguntasId;
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_view_layout
                , parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pregunta pregunta = preguntas.get(position);
        holder.titleP.setText(pregunta.getTitle());
        holder.descriptionP.setText(pregunta.getDescription());
        holder.categoriaP.setText(pregunta.getCategoria());
//        holder.dateP.setText(pregunta.getDateP().toString());
        String preguntaId = preguntasId.get(position);

        ///////// PASAR DE TIMESTAMP EN FIRESTORE A SIMPLEDATEFORMAT ///////
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss aaa");
        String dateTime = simpleDateFormat.format(pregunta.getDateP().getTime());
        holder.dateP.setText(dateTime);

        /////////////// CLIC EN LA UNA ACTIVIDAD PARA VER DATOS ///////////////
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), detallesForo.class);
                i.putExtra("userP",pregunta.getUserP());
                i.putExtra("title",pregunta.getTitle());
                i.putExtra("dateP",dateTime);
                i.putExtra("dateE",pregunta.getDateE());
                i.putExtra("description",pregunta.getDescription());
                i.putExtra("categoria",pregunta.getCategoria());
                i.putExtra("keyP",preguntaId);
                v.getContext().startActivity(i);
            }
        });

        ImageView settingsIcon = holder.view.findViewById(R.id.settingsIconQ);
        if(pregunta.getUserP().equals(userEmail)){ ///// SI EL USUARIO ES EL MISMO QUE EL QUE  HIZO LA PREGUNTA

            holder.titleP.setTextColor(Color.parseColor("#FF7800"));

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
                        Intent i = new Intent(v.getContext(), editForoP.class);
                        i.putExtra("userP",pregunta.getUserP());
                        i.putExtra("title",pregunta.getTitle());
                        i.putExtra("dateP",dateTime);
                        i.putExtra("dateE",pregunta.getDateE());
                        i.putExtra("description",pregunta.getDescription());
                        i.putExtra("categoria",pregunta.getCategoria());
                        i.putExtra("keyP",preguntaId);
                        v.getContext().startActivity(i);
                            return false;
                        }
                    });

                    menu.getMenu().add("Eliminar").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            DocumentReference documentReference = fstore.collection("Foro")
                                    .document(preguntaId); //// REFERENCIA DEL DOCUMENTO

                            documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(v.getContext(), "Pregunta eliminada", Toast.LENGTH_SHORT).show();
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

                    menu.getMenu().add("Responder").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Intent i = new Intent(v.getContext(), detallesForo.class);
                            i.putExtra("userP",pregunta.getUserP());
                            i.putExtra("title",pregunta.getTitle());
                            i.putExtra("dateP",dateTime);
                            i.putExtra("dateE",pregunta.getDateE());
                            i.putExtra("description",pregunta.getDescription());
                            i.putExtra("categoria",pregunta.getCategoria());
                            i.putExtra("keyP",preguntaId);
                            v.getContext().startActivity(i);
                            return false;
                        }
                    });
                    menu.show();
                }
            });
        } else{
            ////// CLIC EN LOS TRES PUNTOS PARA EDITAR O ELIMINAR NOTA /////////
            settingsIcon.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(v.getContext(),v);
                    menu.setGravity(Gravity.END);
                    menu.getMenu().add("Responder").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Intent i = new Intent(v.getContext(), detallesForo.class);
                            i.putExtra("userP",pregunta.getUserP());
                            i.putExtra("title",pregunta.getTitle());
                            i.putExtra("dateP",dateTime);
                            i.putExtra("dateE",pregunta.getDateE());
                            i.putExtra("description",pregunta.getDescription());
                            i.putExtra("categoria",pregunta.getCategoria());
                            i.putExtra("keyP",preguntaId);
                            v.getContext().startActivity(i);
                            return false;
                        }
                    });
                    menu.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return preguntas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleP,dateP,descriptionP,dateE,categoriaP;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleP = itemView.findViewById(R.id.tituloP);
            dateP = itemView.findViewById(R.id.tiempoQ);
            dateE = itemView.findViewById(R.id.editadoDetalles);
            descriptionP = itemView.findViewById(R.id.contenidoP);
            categoriaP =itemView.findViewById(R.id.categoriaP);
            view = itemView;
        }
    }

}
