package com.example.finalproject.BBCNews;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.finalproject.R;

/**
 * This class is details fragment class.
 *
 *@author Xiaoting Kong
 *@version 1.0
 */
public class DetailsFragment extends Fragment {

    private Bundle dataFromActivity;
    private long id;
    private AppCompatActivity parentActivity;

    /**
     * Constructor of this DetailsFragment class.
     */
    public DetailsFragment() {
    }

    /**
     * onCreateView method of this DetailsFragment class.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.bbc_activity_details_fragment, container, false);
        dataFromActivity = getArguments();

        String information_f = dataFromActivity.getString(BbcNews.ITEM_INFORMATION);
        String link_f = dataFromActivity.getString(BbcNews.ITEM_LINK);
        TextView message = (TextView) result.findViewById(R.id.TextView_f_1);
        TextView link = (TextView)result.findViewById(R.id.TextView_f_2);
        message.setText(information_f);
        link.setText("Click here : "+link_f);

        Button finishButton = (Button) result.findViewById(R.id.hide_button);
        finishButton.setOnClickListener(clk -> {
            //Tell the parent activity to remove
            parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
            parentActivity.finish();
        });
        return result;
    }

    /**
     * onAttach method of this DetailsFragment class.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        parentActivity = (AppCompatActivity) context;
    }
}
