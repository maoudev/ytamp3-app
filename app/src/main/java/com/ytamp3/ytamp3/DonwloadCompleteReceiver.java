package com.ytamp3.ytamp3;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

public class DonwloadCompleteReceiver extends BroadcastReceiver {
    private TextInputEditText txturl;
    private LinearProgressIndicator indicator;
    private Button btn;

    public DonwloadCompleteReceiver(TextInputEditText txturl, LinearProgressIndicator indicator, Button btn) {
        this.txturl = txturl;
        this.indicator = indicator;
        this.btn = btn;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)){
            Toast.makeText(context,"Descarga Terminada", Toast.LENGTH_LONG).show();
            txturl.setText("");
            indicator.setVisibility(View.INVISIBLE);
            btn.setEnabled(true);
        }
    }

}
