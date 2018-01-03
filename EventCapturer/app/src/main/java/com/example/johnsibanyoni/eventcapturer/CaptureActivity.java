package com.example.johnsibanyoni.eventcapturer;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import Fragments.BarcodeScannerFragment;
import Interfaces.BarcodeScannerInterface;

public class CaptureActivity extends AppCompatActivity implements BarcodeScannerInterface {
  private static final String TAG = "CaptureActivity";
  protected boolean ok = false;
  protected String data = "";
  protected Context c = null;
  protected List<String> barcodes = null;
  private Switch cameraState;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_capture);

    c = getApplicationContext();
  }

  void initVars() {
    barcodes = new ArrayList<String>();

    cameraState = (Switch) findViewById(R.id.settings_cameraFlash);
    cameraState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          BarcodeScannerFragment.mFlash = true;
        } else {
          BarcodeScannerFragment.mFlash = false;
        }
        BarcodeScannerFragment.mScannerView.setFlash(BarcodeScannerFragment.mFlash);
      }
    });
  }

  @Override
  public void onScan(String barcode) {
    try {
      if (!barcode.isEmpty() && !barcodes.contains(barcode)) {
        barcodes.clear();  //then add the current one
        barcodes.add(barcode);
        //save();
      }
    } catch (Exception e) {
      Log.e(TAG, e.getMessage());
    }
  }
}
