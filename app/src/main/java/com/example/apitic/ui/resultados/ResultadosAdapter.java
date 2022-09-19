package com.example.apitic.ui.resultados;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apitic.R;
import com.example.apitic.ui.clases.Resultado;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

public class ResultadosAdapter extends RecyclerView.Adapter<ResultadosAdapter.ViewHolder> {

    LayoutInflater inflater;
    ArrayList<Resultado> resultados;

    public ResultadosAdapter(Context context, ArrayList<Resultado> resultados) {
        if(context != null){
            this.inflater = LayoutInflater.from(context);
            this.resultados = resultados;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.resultados_view_layout
                , parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Calendar calendar = Calendar.getInstance();
        int thisMonth = calendar.get(Calendar.MONTH);

        Resultado resultado = resultados.get(position);

        holder.EstadoResultadosE.setText("Ejecutandose");
        holder.CantidadResultadosE.setText(resultado.getStateE());

        String monthh = getMonthForInt(Integer.parseInt(resultado.getMonth()));
        holder.MesResultadosP.setText(monthh+" "+resultado.getYear());
        holder.EstadoResultadosP.setText("Parada de reloj");
        holder.CantidadResultadosP.setText(resultado.getStateP());

        holder.EstadoResultadosF.setText("Finalizado");
        holder.CantidadResultadosF.setText(resultado.getStateF());

    }

    public String getMonthForInt(int num){
        String month = "";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num>=0 && num <=11){
            month = months[num];
        }
        return month;
    }

    @Override
    public int getItemCount() {
        return resultados.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView MesResultadosE,EstadoResultadosE,CantidadResultadosE
                ,MesResultadosP,EstadoResultadosP,CantidadResultadosP
                ,MesResultadosF,EstadoResultadosF,CantidadResultadosF;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            MesResultadosE = itemView.findViewById(R.id.mesResultadosView1);
            EstadoResultadosE = itemView.findViewById(R.id.estadoResultadosView1);
            CantidadResultadosE = itemView.findViewById(R.id.cantidadEstadosView1);
            MesResultadosP = itemView.findViewById(R.id.mesResultadosView2);
            EstadoResultadosP = itemView.findViewById(R.id.estadoResultadosView2);
            CantidadResultadosP = itemView.findViewById(R.id.cantidadEstadosView2);
            MesResultadosF = itemView.findViewById(R.id.mesResultadosView3);
            EstadoResultadosF = itemView.findViewById(R.id.estadoResultadosView3);
            CantidadResultadosF = itemView.findViewById(R.id.cantidadEstadosView3);
            view = itemView;
        }
    }
}
