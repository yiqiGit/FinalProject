package com.example.finalproject.yiqiFunction;

import android.content.Context;
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
        dataInfo.setText("Date: " + dataFromActivity.getString(SearchResult.ITEM_DATE));


        TextView imageIdInfo = (TextView)result.findViewById(R.id.idInfoFragChang);
        imageIdInfo.setText("Image ID = " + dataFromActivity.getString(SearchResult.ITEM_IMAGE_ID));

        //show the message
        TextView imageSource = (TextView)result.findViewById(R.id.sourceInfoFragChang);
        imageSource.setText("Image Source = " + dataFromActivity.getString(SearchResult.ITEM_IMAGE_SOURCE_INFO));

        //show the id:
        TextView imageVersion = (TextView)result.findViewById(R.id.serviceInfoFragChang);
        imageVersion.setText("Service Version: " + dataFromActivity.getString(SearchResult.ITEM_IMAGE_VERSION_INFO));

        //show the message
        TextView imageUrl = (TextView)result.findViewById(R.id.imageUrlFragChang);
        imageUrl.setText("Image URL: " + dataFromActivity.getString(SearchResult.ITEM_IMAGE_URL));

        //show the id:
        TextView idView = (TextView)result.findViewById(R.id.idFragChang);
        idView.setText("ID= " + id);

        ImageView imageView = (ImageView)result.findViewById(R.id.picFragChang);

 //       byte[] imageData = dataFromActivity.getByteArray(SearchResult.)
 //       Bitmap stitchBmp = Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888);
  //      stitchBmp.copyPixelsFromBuffer(ByteBuffer.wrap(imageData));


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
