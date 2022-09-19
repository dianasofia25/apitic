package com.example.apitic.ui.manuales;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.apitic.R;
import com.example.apitic.databinding.FragmentManualesBinding;

public class ManualesFragment extends Fragment {

    private FragmentManualesBinding binding;
    CardView C1,C2,C3,C4,C5,C6,C7,C8,C9,C10;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentManualesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        C1 = (CardView) root.findViewById(R.id.Card1);
        C1.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ManualHughesnet.class).
                    putExtra("H1", "1")); // envio de bandera al activity Manual1
            getActivity(); // terminar un fragment (no poderse devolver)
        });

        C2 = (CardView) root.findViewById(R.id.Card2);
        C2.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ManualHughesnet.class).
                    putExtra("H1","2")); // envio de bandera al activity Manual1
            getActivity(); // terminar un fragment (no poderse devolver)
        });

        C3 = (CardView) root.findViewById(R.id.Card3);
        C3.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ManualHughesnet.class).
                    putExtra("H1","3")); // envio de bandera al activity Manual1
            getActivity(); // terminar un fragment (no poderse devolver)
        });

        C4 = (CardView) root.findViewById(R.id.Card4);
        C4.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ManualRadios.class).
                    putExtra("R1","1")); // envio de bandera al activity Manual1
            getActivity(); // terminar un fragment (no poderse devolver)
        });

        C5 = (CardView) root.findViewById(R.id.Card5);
        C5.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ManualAP.class).
                    putExtra("AP1","1")); // envio de bandera al activity Manual1
            getActivity(); // terminar un fragment (no poderse devolver)
        });

        C6 = (CardView) root.findViewById(R.id.Card6);
        C6.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ManualUPS.class).
                    putExtra("UPS1","1")); // envio de bandera al activity Manual1
            getActivity(); // terminar un fragment (no poderse devolver)
        });

        C7 = (CardView) root.findViewById(R.id.Card7);
        C7.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ManualUPS.class).
                    putExtra("UPS1","2")); // envio de bandera al activity Manual1
            getActivity(); // terminar un fragment (no poderse devolver)
        });

        C8 = (CardView) root.findViewById(R.id.Card8);
        C8.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ManualUPS.class).
                    putExtra("UPS1","3")); // envio de bandera al activity Manual1
            getActivity(); // terminar un fragment (no poderse devolver)
        });

        C9 = (CardView) root.findViewById(R.id.Card9);
        C9.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ManualUPS.class).
                    putExtra("UPS1","4")); // envio de bandera al activity Manual1
            getActivity(); // terminar un fragment (no poderse devolver)
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}