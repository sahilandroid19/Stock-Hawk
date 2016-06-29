package com.example.android.stockhawk.ui;


import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.stockhawk.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StockDialogFragment extends DialogFragment {

    private DialogInterface mListener;

    public StockDialogFragment() {
        // Required empty public constructor
    }

    public interface DialogInterface{
        void buttonClicked(String text);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.action_dialog, null);
        Typeface regular_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface bold_font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Bold.ttf");
        TextView textView = (TextView) view.findViewById(R.id.content_text);
        textView.setTypeface(regular_font);
        textView.setAlpha(0.6f);
        TextView textView1 = (TextView) view.findViewById(R.id.dialog_title);
        textView1.setTypeface(bold_font);
        textView.setAlpha(0.6f);
        final EditText editText = (EditText) view.findViewById(R.id.dialog_entry);
        final Button button = (Button) view.findViewById(R.id.dialog_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
                mListener = (DialogInterface) getTargetFragment();
                mListener.buttonClicked(editText.getText().toString());
            }
        });
                builder.setView(view);
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
