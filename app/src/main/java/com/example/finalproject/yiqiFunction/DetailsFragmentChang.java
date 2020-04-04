package com.example.finalproject.yiqiFunction;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.finalproject.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class DetailsFragmentChang extends Fragment {
    private AppCompatActivity parentActivity;

    private Bundle dataFromActivity;

    private long id;


    public DetailsFragmentChang() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataFromActivity = getArguments();
        id = dataFromActivity.getLong(SearchResult.ITEM_ID );

        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.fragment_details_fragment_chang, container, false);


        TextView latInfo = (TextView)result.findViewById(R.id.latFragChang);
        latInfo.setText("Lat = "+ dataFromActivity.getString(SearchResult.ITEM_LAT));


        TextView lonInfo = (TextView)result.findViewById(R.id.lonFragChang);
        lonInfo.setText("Lon = " + dataFromActivity.getString(SearchResult.ITEM_LON));


        TextView dataInfo = (TextView)result.findViewById(R.id.dateFragChang);
        dataInfo.setText(getResources().getString(R.string.dateTextChang) + dataFromActivity.getString(SearchResult.ITEM_DATE));


        TextView imageIdInfo = (TextView)result.findViewById(R.id.idInfoFragChang);
        imageIdInfo.setText(getResources().getString(R.string.imageIdChang) + dataFromActivity.getString(SearchResult.ITEM_IMAGE_ID));

        //show the message
        TextView imageSource = (TextView)result.findViewById(R.id.sourceInfoFragChang);
        imageSource.setText(getResources().getString(R.string.sourceTextChang) + dataFromActivity.getString(SearchResult.ITEM_IMAGE_SOURCE_INFO));

        //show the id:
        TextView imageVersion = (TextView)result.findViewById(R.id.serviceInfoFragChang);
        imageVersion.setText(getResources().getString(R.string.versionTextChang) + dataFromActivity.getString(SearchResult.ITEM_IMAGE_VERSION_INFO));

        //show the message
        TextView imageUrl = (TextView)result.findViewById(R.id.imageUrlFragChang);
        imageUrl.setText("URL: " + dataFromActivity.getString(SearchResult.ITEM_IMAGE_URL));

        //show the id:
        TextView idView = (TextView)result.findViewById(R.id.idFragChang);
        idView.setText("ID= " + id);

        ImageView imageView = (ImageView)result.findViewById(R.id.picFragChang);

        try {
            String str = dataFromActivity.getString(SearchResult.ITEM_PIC);
            String[] arrOfStr = str.split("&");
            File f=new File(arrOfStr[0],arrOfStr[1]);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imageView.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }


        // get the delete button, and add a click listener:
        Button deleteButton = (Button)result.findViewById(R.id.buttonHideChang);
        deleteButton.setOnClickListener( clk -> {
            parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
                parentActivity.finish();
        });
        return result;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parentActivity = (AppCompatActivity)context;
    }

}
