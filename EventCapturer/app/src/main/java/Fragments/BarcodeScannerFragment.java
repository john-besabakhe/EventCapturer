package Fragments;


import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import Interfaces.BarcodeScannerInterface;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class BarcodeScannerFragment extends Fragment implements MessageDialogFragment.MessageDialogListener,
ZXingScannerView.ResultHandler, FormatSelectorDialogFragment.FormatSelectorDialogListener,
CameraSelectorDialogFragment.CameraSelectorDialogListener {


  public BarcodeScannerFragment() {
    // Required empty public constructor
  }

    private static final String TAG = "BarCodeScannerFragment";
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    public static ZXingScannerView mScannerView;
    public static boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
      try {
        mScannerView = new ZXingScannerView(getActivity());
        if (state != null) {
          mFlash = state.getBoolean(FLASH_STATE, false);
          mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
          mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
          mCameraId = state.getInt(CAMERA_ID, -1);
        } else {
          mFlash = true;
          mAutoFocus = true;
          mSelectedIndices = null;
          mCameraId = -1;
        }
        setupFormats();
      } catch (Exception e) {
        Log.e(TAG, e.toString());
      }
      return mScannerView;
    }

    @Override
    public void onCreate(Bundle state) {
      super.onCreate(state);
    }

  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);

  }

  @Override
  public void onResume() {
    super.onResume();
    try {
      mScannerView.setResultHandler(this);
      mScannerView.startCamera(mCameraId);
      mScannerView.setFlash(mFlash);
      mScannerView.setAutoFocus(mAutoFocus);
    } catch (Exception e) {
      Log.e(TAG, e.toString());
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean(FLASH_STATE, mFlash);
    outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
    outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
    outState.putInt(CAMERA_ID, mCameraId);
  }

  @Override
  public void handleResult(Result rawResult) {
    try {
      Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
      r.play();
    } catch (Exception e) {
      Log.e(TAG, e.toString());
    }
    BarcodeScannerInterface result = (BarcodeScannerInterface) getActivity();
    result.onScan(rawResult.getText());

    mScannerView.resumeCameraPreview(this);
  }

  public void closeMessageDialog() {
    closeDialog("scan_results");
  }

  public void closeFormatsDialog() {
    closeDialog("format_selector");
  }

  public void closeDialog(String dialogName) {
  }

  public void setupFormats() {
    List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
    if (mSelectedIndices == null || mSelectedIndices.isEmpty()) {
      mSelectedIndices = new ArrayList<Integer>();
      for (int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++) {
        mSelectedIndices.add(i);
      }
    }

    for (int index : mSelectedIndices) {
      formats.add(ZXingScannerView.ALL_FORMATS.get(index));
    }
    if (mScannerView != null) {
      mScannerView.setFormats(formats);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    mScannerView.stopCamera();
    closeMessageDialog();
    closeFormatsDialog();
  }

  @Override
  public void onDialogPositiveClick(DialogFragment dialog) {
    // Resume the camera
    mScannerView.resumeCameraPreview(this);
  }

  @Override
  public void onCameraSelected(int cameraId) {
    mCameraId = cameraId;
    mScannerView.startCamera(mCameraId);
    mScannerView.setFlash(mFlash);
    mScannerView.setAutoFocus(mAutoFocus);
  }

  @Override
  public void onFormatsSaved(ArrayList<Integer> selectedIndices) {
    mSelectedIndices = selectedIndices;
    setupFormats();
  }
}
