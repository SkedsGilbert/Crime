package com.south42studios.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.util.Date;

/**
 * Created by Jsin on 10/14/2015.
 */
public class ZoomPhotoFragment extends DialogFragment {
    private static final String TAG = "com.south42studios";
    private static final String ARG_PHOTO = "photo";
    ImageView zoomedPhoto;

    public static ZoomPhotoFragment newInstance(Bitmap bitmap) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_PHOTO, bitmap);

        ZoomPhotoFragment fragment = new ZoomPhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bitmap bitmap = getArguments().getParcelable(ARG_PHOTO);
        if (bitmap == null){
            Log.d(TAG,"Image is null");
        }
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_zoom_photo, null);
        zoomedPhoto = (ImageView) v.findViewById(R.id.dialog_zoom_photos);
        zoomedPhoto.setImageBitmap(bitmap);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Crime Photo!")
                .setCancelable(true).setView(v);
        return builder.create();
    }
}
