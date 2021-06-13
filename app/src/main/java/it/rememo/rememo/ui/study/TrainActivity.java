package it.rememo.rememo.ui.study;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;

import it.rememo.rememo.R;
import it.rememo.rememo.databinding.ActivityTestBinding;
import it.rememo.rememo.databinding.ActivityTrainBinding;

public class TrainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTrainBinding binding = ActivityTrainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.success);
        mp.start();

    }
}