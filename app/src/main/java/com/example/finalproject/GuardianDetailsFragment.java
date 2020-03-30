package com.example.finalproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.finalproject.GuardianNews.GuardianList;

public class GuardianDetailsFragment extends Fragment {

    private AppCompatActivity parentActivity;
    private Bundle dataFromActivity;



    public GuardianDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromActivity = getArguments();

        View result = inflater.inflate(R.layout.fragment_guardian_details, container, false);

        //Show Message
        TextView message = result.findViewById(R.id.tv);
        message.setText("This is the blank Fragment Layout.");

        Button hideButton = result.findViewById(R.id.hide_button);
        hideButton.setOnClickListener((click) -> {
            parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();

        });
        return result;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parentActivity = (AppCompatActivity) context;
    }

}