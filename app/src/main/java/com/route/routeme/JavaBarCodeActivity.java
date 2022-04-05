package com.route.routeme;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.util.concurrent.ListenableFuture;
import com.route.barcode.QrCodeAnalyzer;
import com.route.callbacks.QRValueListener;
import com.route.view.BarcodeBoxView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.jvm.internal.Intrinsics;

public class JavaBarCodeActivity extends AppCompatActivity implements QRValueListener {

    private ExecutorService cameraExecutor;
    private BarcodeBoxView barcodeBoxView;
    private com.route.routeme.databinding.ActivityBarcodeScannerBinding binding;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.route.routeme.databinding.ActivityBarcodeScannerBinding.inflate(this.getLayoutInflater());

        this.setContentView(binding.getRoot());

        ExecutorService var2 = Executors.newSingleThreadExecutor();
        Intrinsics.checkNotNullExpressionValue(var2, "Executors.newSingleThreadExecutor()");
        this.cameraExecutor = var2;
        this.barcodeBoxView = new BarcodeBoxView((Context)this);
        BarcodeBoxView var3 = this.barcodeBoxView;
        if (var3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("barcodeBoxView");
        }

        this.addContentView((View)var3, new ViewGroup.LayoutParams(-1, -1));
        ActionBar var10000 = this.getSupportActionBar();
        if (var10000 != null) {
            var10000.setDisplayHomeAsUpEnabled(true);
        }

        checkCameraPermission();


    }

    private final void checkCameraPermission() {
        try {
            String[] requiredPermissions = new String[]{"android.permission.CAMERA"};
            ActivityCompat.requestPermissions((Activity)this, requiredPermissions, 0);
        } catch (IllegalArgumentException var2) {
            this.checkIfCameraPermissionIsGranted();
        }
    }


    private final void checkIfCameraPermissionIsGranted() {
        if (ContextCompat.checkSelfPermission((Context)this, "android.permission.CAMERA") == 0) {
            this.startCamera();
        } else {
            AlertDialog var1 = (new MaterialAlertDialogBuilder((Context)this)).setTitle((CharSequence)"Permission required").setMessage((CharSequence)"This application needs to access the camera to process barcodes").setPositiveButton((CharSequence)"Ok", (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface $noName_0, int $noName_1) {
                    checkCameraPermission();
                }
            })).setCancelable(false).create();
            var1.setCanceledOnTouchOutside(false);
            var1.show();
        }

    }

    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        this.checkIfCameraPermissionIsGranted();
    }

    private final void startCamera() {
        ListenableFuture cameraProviderFuture = ProcessCameraProvider.getInstance((Context)this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                openCamera(cameraProviderFuture);
            }
        }, ContextCompat.getMainExecutor((Context)this));
    }


    private void openCamera(ListenableFuture cameraProviderFuture) {
        ProcessCameraProvider cameraProvider = null;
        try {
            cameraProvider = (ProcessCameraProvider)cameraProviderFuture.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Preview var3 = (new Preview.Builder()).build();
        var3.setSurfaceProvider(binding.previewView.getSurfaceProvider());
        ImageAnalysis imageAnalyzer = (new ImageAnalysis.Builder()).setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
        PreviewView var10006 = binding.previewView;
        float width = (float)var10006.getWidth();
        float height = (float)var10006.getHeight();
        QrCodeAnalyzer qrCodeAnalyzer = new QrCodeAnalyzer(JavaBarCodeActivity.this, barcodeBoxView, width, height);
        qrCodeAnalyzer.setQRValueListener(this);
        imageAnalyzer.setAnalyzer(cameraExecutor, qrCodeAnalyzer);

        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        try {
            cameraProvider.unbindAll();
            // Bind use cases to camera
            cameraProvider.bindToLifecycle(JavaBarCodeActivity.this, cameraSelector, var3, imageAnalyzer);
        } catch (Exception var9) {
            var9.printStackTrace();
        }
    }


    @Override
    public void onQRValueResponse(String value) {
        // It sends response in "FirstFragment.java" which resides in "MainActivity.java", which Opens "DocumentFragment.java"
        Intent intent = getIntent();
        intent.putExtra("key", value);
        setResult(RESULT_OK, intent);
        finish();
    }

}
