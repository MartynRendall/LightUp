package com.rendall.martyn.lightup.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rendall.martyn.lightup.Constants;
import com.rendall.martyn.lightup.R;
import com.rendall.martyn.lightup.http.ExtensionSocketAPI;
import com.rendall.martyn.lightup.http.ServiceGenerator;
import com.rendall.martyn.lightup.model.Controller;
import com.rendall.martyn.lightup.model.ExtensionSocket;
import com.rendall.martyn.lightup.services.ControllerOffService;
import com.rendall.martyn.lightup.services.ControllerOnService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ControlFragment extends Fragment /*implements Callback<Controller>*/ {

    ExtensionSocketAPI service;
    ExtensionSocketAPI localService;

    LinearLayout main = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_control, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        service = ServiceGenerator.createServiceRemote(ExtensionSocketAPI.class);
        localService = ServiceGenerator.createServiceLocal(ExtensionSocketAPI.class);

        main = (LinearLayout) getView().findViewById(R.id.control_frame);

        // TODO cache the extensions retrieved from the server and display them on app start up, as a background task
        // then check with the server to pull in an updated list

        getExtensionSockets();

        getActivity().sendBroadcast(new Intent("com.rendall.martyn.lightup.broadcast.ACTION_SET_UP_GEOFENCE"));

        Call<List<ExtensionSocket>> allExtensionSockets = localService.getAlExtensionSockets();

        allExtensionSockets.enqueue(new Callback<List<ExtensionSocket>>() {
            @Override
            public void onResponse(Call<List<ExtensionSocket>> call, Response<List<ExtensionSocket>> response) {

                for (ExtensionSocket extensionSocket : response.body()) {
                    Log.d("LOCAL", extensionSocket.getName());
                }
            }


            @Override
            public void onFailure(Call<List<ExtensionSocket>> call, Throwable t) {
                Log.e("LOCAL", "Failed to find local devices");
            }
        });
    }

    public void getExtensionSockets() {

        service.getAlExtensionSockets2()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<List<ExtensionSocket>, Observable<ExtensionSocket>>() {
                    @Override
                    public Observable<ExtensionSocket> call(List<ExtensionSocket> extensionSockets) {
                        return Observable.from(extensionSockets);
                    }
                })
                .subscribe(new Subscriber<ExtensionSocket>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ExtensionSocket extensionSockets) {
                        generateButtons(extensionSockets);
                    }
                });
    }

    private void generateButtons(final ExtensionSocket extensionSocket) {
        Log.w("TEST_DATA", extensionSocket.getName());

        TextView tv = new TextView(getActivity());
        tv.setText(extensionSocket.getName().toUpperCase());
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);

        Button onBtn = new Button(getActivity());
        onBtn.setLayoutParams(params);
        onBtn.setText("ON");
        onBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent controllerOnIntent = new Intent(getActivity(), ControllerOnService.class);
                controllerOnIntent.putExtra(Constants.CONTROLLER_ID, extensionSocket.getController().getControllerId());
                getActivity().startService(controllerOnIntent);
            }
        });

        Button offBtn = new Button(getActivity());
        offBtn.setLayoutParams(params);
        offBtn.setText("OFF");
        offBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent controllerOffIntent = new Intent(getActivity(), ControllerOffService.class);
                controllerOffIntent.putExtra(Constants.CONTROLLER_ID, extensionSocket.getController().getControllerId());
                getActivity().startService(controllerOffIntent);
            }
        });

        main.addView(tv);
        main.addView(linearLayout);
        linearLayout.addView(onBtn);
        linearLayout.addView(offBtn);
    }

//    private void generateButtons(List<ExtensionSocket> extensionSockets) {
//
//        LinearLayout main = (LinearLayout) getView().findViewById(R.id.control_frame);
//
//        for (final ExtensionSocket extensionSocket : extensionSockets) {
//
//            TextView tv = new TextView(getActivity());
//            tv.setText(extensionSocket.getName().toUpperCase());
//            tv.setGravity(Gravity.CENTER);
//            tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//
//            LinearLayout linearLayout = new LinearLayout(getActivity());
//            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
//
//
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
//
//            Button onBtn = new Button(getActivity());
//            onBtn.setLayoutParams(params);
//            onBtn.setText("ON");
//            onBtn.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    Intent controllerOnIntent = new Intent(getActivity(), ControllerOnService.class);
//                    controllerOnIntent.putExtra(Constants.CONTROLLER_ID, extensionSocket.getController().getControllerId());
//                    getActivity().startService(controllerOnIntent);
//                }
//            });
//
//            Button offBtn = new Button(getActivity());
//            offBtn.setLayoutParams(params);
//            offBtn.setText("OFF");
//            offBtn.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    Intent controllerOffIntent = new Intent(getActivity(), ControllerOffService.class);
//                    controllerOffIntent.putExtra(Constants.CONTROLLER_ID, extensionSocket.getController().getControllerId());
//                    getActivity().startService(controllerOffIntent);
//                }
//            });
//
//            main.addView(tv);
//            main.addView(linearLayout);
//            linearLayout.addView(onBtn);
//            linearLayout.addView(offBtn);
//        }
//    }

//    @Override
//    public void onResponse(Call<Controller> call, Response<Controller> response) {
//
//        if (response.isSuccess()) {
//            showToast("Success");
//        } else {
//            showToast("Failed: " + response.code());
//        }
//    }
//
//    @Override
//    public void onFailure(Call<Controller> call, Throwable t) {
//        showToast("Had a failure");
//
//    }
//
//    private void showToast(String message) {
//        if (isAdded()) {
//            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
//        }
//    }
}
