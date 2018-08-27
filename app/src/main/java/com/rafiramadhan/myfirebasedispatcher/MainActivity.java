package com.rafiramadhan.myfirebasedispatcher;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnSetScheduler, btnCancelScheduler;

    FirebaseJobDispatcher mDispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSetScheduler = findViewById(R.id.btnSetScheduler);
        btnCancelScheduler = findViewById(R.id.btnCancelScheduler);

        btnSetScheduler.setOnClickListener(this);
        btnCancelScheduler.setOnClickListener(this);

        mDispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSetScheduler:
                startDispatcher();
                Toast.makeText(this, "Dispatcher Created", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnCancelScheduler:
                cancelDispatcher();
                Toast.makeText(this, "Dispatcher Canceled", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private String DISPATCHER_TAG = "mydispatcher";
    private String CITY = "Surabaya";

    public void startDispatcher(){
        Bundle myExtrasBundle = new Bundle();
        myExtrasBundle.putString(MyJobService.EXTRAS_CITY,CITY);

        Job myJob = mDispatcher.newJobBuilder()
                // kelas service yang akan dipanggil
                .setService(MyJobService.class)
                // unique tag untuk identifikasi job
                .setTag(DISPATCHER_TAG)
                // one-off job
                // true job tersebut akan diulang, dan false job tersebut tidak diulang
                .setRecurring(true)
                // until_next_boot berarti hanya sampai next boot
                // forever berarti akan berjalan meskipun sudah reboot
                .setLifetime(Lifetime.UNTIL_NEXT_BOOT)
                // waktu trigger 0 sampai 60 detik
                .setTrigger(Trigger.executionWindow(0, 60))
                // overwrite job dengan tag sama
                .setReplaceCurrent(true)
                // set waktu kapan akan dijalankan lagi jika gagal
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // set kondisi dari device
                .setConstraints(
                        // hanya berjalan saat ada koneksi yang unmetered (contoh Wifi)
                        Constraint.ON_UNMETERED_NETWORK,
                        // hanya berjalan ketika device di charge
                        Constraint.DEVICE_CHARGING

                        // berjalan saat ada koneksi internet
//                        Constraint.ON_ANY_NETWORK

                        // berjalan saat device dalam kondisi idle
                        //Constraint.DEVICE_IDLE
                )
                .setExtras(myExtrasBundle)
                .build();

        mDispatcher.mustSchedule(myJob);
    }

    public void cancelDispatcher() {

        mDispatcher.cancel(DISPATCHER_TAG);
    }
}
