package com.example.apitic.ui.foro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apitic.ui.clases.Pregunta;
import com.example.apitic.R;
import com.example.apitic.databinding.FragmentForoBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ForoFragment extends Fragment {

    private final ArrayList<Pregunta> preguntasList = new ArrayList<>();
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser user = fAuth.getCurrentUser();
    String userEmail;
    {
        assert user != null;
        userEmail = user.getEmail();
    }

    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton fb;
    RecyclerView recyclerView;
    ForoAdapter foroAdapter;
    ImageView imageView;
    TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        com.example.apitic.databinding.FragmentForoBinding binding =
                FragmentForoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = (RecyclerView) root.findViewById(R.id.foroList);
        swipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipeRefreshLayoutF);
        fb = (FloatingActionButton) root.findViewById(R.id.floatBF);
        textView = (TextView)root.findViewById(R.id.textForo);
        imageView = (ImageView) root.findViewById(R.id.imageForo);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(!isInternetAvailable()){
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageResource(R.drawable.ic_wifi_off_24);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("Sin conexión a internet");
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    recyclerView.setVisibility(View.GONE);

                    fb.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity(), "Sin conexión a internet\nIntentelo mas tarde...", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else {
                    setRecyclerView();
                }

                swipeRefreshLayout.setRefreshing(false);

            }
        });

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), addForoP.class));
                getActivity(); // terminar un fragment (no poderse devolver)
            }
        });

        if(!isInternetAvailable()){
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.ic_wifi_off_24);
            textView.setVisibility(View.VISIBLE);
            textView.setText("Sin conexión a internet");
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            recyclerView.setVisibility(View.GONE);

            fb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Sin conexión a internet\nIntentelo mas tarde...", Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            setRecyclerView();
        }

        return root;
    }

    private void setRecyclerView(){

        ArrayList<String> preguntasId = new ArrayList<>();

        Query queryE = fstore.collection("Foro").orderBy("dateP", Query.Direction.DESCENDING).limit(50);

        queryE.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                preguntasList.clear();
                preguntasId.clear();
                assert value != null;
                if(!value.isEmpty()){
                    for (QueryDocumentSnapshot preguntas: value) {
                        Pregunta pregunta = preguntas.toObject(Pregunta.class);
                        preguntasList.add(pregunta);
                        preguntasId.add(preguntas.getId());

                        imageView.setVisibility(View.GONE);
                        textView.setVisibility(View.GONE);

                    }
                } else{
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageResource(R.drawable.ic_error_outline);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("Foro vacío\n\nEmpieza preguntando...");
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                foroAdapter = new ForoAdapter(getContext(),preguntasList,preguntasId);
                recyclerView.setAdapter(foroAdapter);
                recyclerView.setItemAnimator(null);
            }
        });

        preguntasList.clear();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_filter_foro,menu);

        MenuItem item1 = menu.findItem(R.id.action_confiAntena);
        item1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String confiAntena = "Configuracion antena satelital";
                setFilter(confiAntena);
                return false;
            }
        });

        MenuItem item2 = menu.findItem(R.id.action_confiAP);
        item2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String confiAP = "Configuracion access point";
                setFilter(confiAP);
                return false;
            }
        });

        MenuItem item3 = menu.findItem(R.id.action_confiRadio);
        item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String confiRadio = "Configuracion radio";
                setFilter(confiRadio);
                return false;
            }
        });

        MenuItem item4 = menu.findItem(R.id.action_confiMK);
        item4.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String confiRouter = "Configuracion router";
                setFilter(confiRouter);
                return false;
            }
        });

        MenuItem item5 = menu.findItem(R.id.action_confiUPS);
        item5.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String confiUPS = "Configuracion UPS";
                setFilter(confiUPS);
                return false;
            }
        });

        MenuItem item6 = menu.findItem(R.id.action_confiOtro);
        item6.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String confiOtro = "Otro";
                setFilter(confiOtro);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void setFilter(String categoria){

        ArrayList<String> preguntasId = new ArrayList<>();

        Query queryE = fstore.collection("Foro").whereEqualTo("categoria", categoria);

        queryE.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot preguntas: queryDocumentSnapshots) {
                        Pregunta pregunta = preguntas.toObject(Pregunta.class);
                        preguntasList.add(pregunta);
                        preguntasId.add(preguntas.getId());

                        imageView.setVisibility(View.GONE);
                        textView.setVisibility(View.GONE);

                    }
                }else{
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageResource(R.drawable.ic_error_outline);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("No hay preguntas en esta categoria");
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }

                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                foroAdapter = new ForoAdapter(getContext(),preguntasList,preguntasId);
                recyclerView.setAdapter(foroAdapter);
                recyclerView.setItemAnimator(null);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error! "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        preguntasList.clear();
    }

    /////////////// CONEXION A INTERNET /////////////
    public boolean isInternetAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null){
            if(networkInfo.isConnected()){
                return true;
            } else {
                return false;
            }
        } else{
            return false;
        }
    }

}