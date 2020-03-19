package com.example.finalproject.nasaImage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.R;
import java.io.FileInputStream;
import android.content.Context;
import java.io.FileNotFoundException;

public class ItemFragment extends Fragment {

    private ItemViewModel mViewModel;
    private Bundle dataFromResults;
    private AppCompatActivity parentActivity;
    private Bitmap image;

    public static ItemFragment newInstance() {
        return new ItemFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        dataFromResults = getArguments();
        View detailView = inflater.inflate(R.layout.item_fragment, container, false);
        TextView imageTitleText = (TextView) detailView.findViewById(R.id.imageTitle);
        TextView imageDescriptionText = (TextView) detailView.findViewById(R.id.description);
        TextView urlText = (TextView) detailView.findViewById(R.id.url);
        TextView hdUrlLink = (TextView) detailView.findViewById(R.id.hd_url);
        ImageView imageView = (ImageView) detailView.findViewById(R.id.preview);

        imageTitleText.setText(dataFromResults.getString(NasaImageOfTheDay.TITLE_KEY));
        imageDescriptionText.setText((dataFromResults.getString(NasaImageOfTheDay.DESCRIPTION_KEY)));
        urlText.setText(dataFromResults.getString(NasaImageOfTheDay.URL_KEY));
        String hdLink = dataFromResults.getString(NasaImageOfTheDay.HD_URL_KEY);
        String fileName = dataFromResults.getString(NasaImageOfTheDay.FILE_PATH);
        loadFile(fileName);
        imageView.setImageBitmap(image);



        hdUrlLink.setOnClickListener(click->{
            if(hdLink!=null) {
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(hdLink));
                startActivity(browserIntent);
            }
            else{
                Toast.makeText(getContext(), "HD url not available.", Toast.LENGTH_LONG).show();
            }
        });

        return detailView;
    }

    private void loadFile(String fileName){
        FileInputStream fis = null;
        try {
            fis = getActivity().openFileInput(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        image = BitmapFactory.decodeStream(fis);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ItemViewModel.class);
        // TODO: Use the ViewModel
    }

}
