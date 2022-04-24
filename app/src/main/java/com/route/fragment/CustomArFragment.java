package com.route.fragment;

import android.content.Context;
import android.util.Log;

import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;
import com.route.routeme.Sample1;

public class CustomArFragment extends ArFragment {

    public static interface OnCompleteListener {
        public abstract void onComplete();
    }


    private OnCompleteListener mListener;

    public void setOnCompleteListener(OnCompleteListener listener) {
        mListener = listener;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (OnCompleteListener)context;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCompleteListener");
        }
    }

    @Override
    protected Config getSessionConfiguration(Session session) {

        Config config = new Config(session);
        config.setCloudAnchorMode(Config.CloudAnchorMode.ENABLED);
        config.setFocusMode(Config.FocusMode.AUTO);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        session.configure(config);


        this.getArSceneView().setupSession(session);

        //Update session config...
        mListener.onComplete();


        /*if ((((Sample1) getActivity()).setupAugmentedImagesDb(config, session))) {
            Log.d("SetupAugImgDb", "Success");
        } else {
            Log.e("SetupAugImgDb","Faliure setting up db");
        }*/

        return config;
    }


}
