package com.route.barcode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageInfo;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.route.callbacks.QRValueListener;
import com.route.view.BarcodeBoxView;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import kotlin.jvm.internal.Intrinsics;

public class QrCodeAnalyzer implements ImageAnalysis.Analyzer {

    private float scaleX;
    private float scaleY;
    private final Context context;
    private final BarcodeBoxView barcodeBoxView;
    private final float previewViewWidth;
    private final float previewViewHeight;

    private QRValueListener mQRValueListener;

    public void setQRValueListener(QRValueListener qRValueListener) {
        mQRValueListener = qRValueListener;
    }

    public QrCodeAnalyzer(@NotNull Context context, @NotNull BarcodeBoxView barcodeBoxView,
                          float previewViewWidth, float previewViewHeight) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(barcodeBoxView, "barcodeBoxView");
        this.context = context;
        this.barcodeBoxView = barcodeBoxView;
        this.previewViewWidth = previewViewWidth;
        this.previewViewHeight = previewViewHeight;
        this.scaleX = 1.0F;
        this.scaleY = 1.0F;
    }

    private final float translateX(float x) {
        return x * this.scaleX;
    }

    private final float translateY(float y) {
        return y * this.scaleY;
    }

    private final RectF adjustBoundingRect(Rect rect) {
        return new RectF(this.translateX((float)rect.left), this.translateY((float)rect.top), this.translateX((float)rect.right), this.translateY((float)rect.bottom));
    }

    @SuppressLint({"UnsafeOptInUsageError"})
    public void analyze(@NotNull ImageProxy image) {
        Intrinsics.checkNotNullParameter(image, "image");
        Image img = image.getImage();
        if (img != null) {
            this.scaleX = this.previewViewWidth / (float)img.getHeight();
            this.scaleY = this.previewViewHeight / (float)img.getWidth();
            ImageInfo var10001 = image.getImageInfo();
            InputImage var10000 = InputImage.fromMediaImage(img, var10001.getRotationDegrees());
            InputImage inputImage = var10000;
            BarcodeScannerOptions options = (new BarcodeScannerOptions.Builder()).build();
            BarcodeScanner var7 = BarcodeScanning.getClient(options);
            BarcodeScanner scanner = var7;
            scanner.process(inputImage).addOnSuccessListener(new OnSuccessListener() {
                // $FF: synthetic method
                // $FF: bridge method
                public void onSuccess(Object var1) {
                    this.onSuccess((List)var1);
                }

                public final void onSuccess(List barcodes) {
                    Intrinsics.checkNotNullExpressionValue(barcodes, "barcodes");
                    Collection var2 = (Collection)barcodes;
                    if (!var2.isEmpty()) {
                        Iterator var3 = barcodes.iterator();

                        while(var3.hasNext()) {
                            Barcode barcode = (Barcode)var3.next();
                            Context var10000 = QrCodeAnalyzer.this.context;
                            StringBuilder var10001 = (new StringBuilder()).append("Value: ");
                            // Intrinsics.checkNotNullExpressionValue(barcode, "barcode");
                              //Toast.makeText(var10000, (CharSequence)var10001.append(barcode.getRawValue()).toString(), Toast.LENGTH_LONG).show();
                            mQRValueListener.onQRValueResponse(var10001.append(barcode.getRawValue()).toString());
                            Rect var8 = barcode.getBoundingBox();
                            if (var8 != null) {
                                Rect var4 = var8;
                                boolean var6 = false;
                                BarcodeBoxView var9 = QrCodeAnalyzer.this.barcodeBoxView;
                                QrCodeAnalyzer var10 = QrCodeAnalyzer.this;
                                Intrinsics.checkNotNullExpressionValue(var4, "rect");
                                var9.setRect(var10.adjustBoundingRect(var4));
                            }
                        }
                    } else {
                        QrCodeAnalyzer.this.barcodeBoxView.setRect(new RectF());
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i("ERROR", ""+e);
                }
            });
        }

        image.close();
    }
}
