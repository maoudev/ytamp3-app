package com.ytamp3.ytamp3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.ytamp3.ytamp3.model.Song;
import com.ytamp3.ytamp3.model.SongRequest;
import com.ytamp3.ytamp3.network.YtApiAdapter;

import java.util.regex.Pattern;

import okhttp3.internal.concurrent.Task;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 100;
    Context context = this;

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE);

        }

        TextInputEditText txturl = findViewById(R.id.txturl);
        Button btndownload = findViewById(R.id.btndownload);
        LinearProgressIndicator indicator = findViewById(R.id.pidownload);
        indicator.setVisibility(View.INVISIBLE);

        registerReceiver(new DonwloadCompleteReceiver(txturl, indicator, btndownload), new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        btndownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regex = "^(https?:\\/\\/)?(www\\.)?(youtube\\.com|youtu\\.be|m\\.youtube\\.com)\\/(watch\\?v=|embed\\/|v\\/|.+\\?v=)?([a-zA-Z0-9_-]{11})(\\S*)?$";

                if (txturl.getText().toString().trim().isEmpty() || !Pattern.matches(regex, txturl.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Por favor, asegúrate de ingresar una URL válida antes de continuar.", Toast.LENGTH_SHORT).show();
                } else {
                    btndownload.setEnabled(false);

                    String url = txturl.getText().toString();
                    indicator.setVisibility(View.VISIBLE);

                    Call<Song> call = YtApiAdapter.getYtApiService().getSong(new SongRequest(url));
                    call.enqueue(new Callback<Song>() {
                        @Override
                        public void onResponse(Call<Song> call, Response<Song> response) {
                            if (response.isSuccessful()) {
                                Song song = response.body();
                                if (song != null) {
                                    String finalUrl = getString(R.string.url) + song.getFile() + ".mp3";
                                    String outputName = song.getFile() + ".mp3";

                                    downloadSong(finalUrl, outputName, song.getFile());
                                } else {
                                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    btndownload.setEnabled(true);
                                    txturl.setText("");
                                    indicator.setVisibility(View.INVISIBLE);
                                }
                            } else {
                                txturl.setText("");
                                btndownload.setEnabled(true);
                                indicator.setVisibility(View.INVISIBLE);
                                Toast.makeText(MainActivity.this, "Petición erronea", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Song> call, Throwable throwable) {
                            Toast.makeText(MainActivity.this, "Error De Conexión", Toast.LENGTH_SHORT).show();
                            indicator.setVisibility(View.INVISIBLE);
                            btndownload.setEnabled(true);
                            txturl.setText("");
                        }
                    });



                }
            }
        });
    }

    private void checkSelfPermission() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE);

        }
    }


    public void downloadSong(String url, String outPutFileName, String songName) {
        try {
            Uri parsedUri=  Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(parsedUri)
                    .setMimeType("audio/mpeg")
                    .setTitle(songName)
                    .setDescription("Descargando: " + songName)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, outPutFileName);

            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al iniciar la descarga", Toast.LENGTH_SHORT).show();
        }
    }

}