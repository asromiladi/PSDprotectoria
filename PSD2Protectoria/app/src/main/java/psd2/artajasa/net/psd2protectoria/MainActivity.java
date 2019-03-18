package psd2.artajasa.net.psd2protectoria;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import com.protectoria.*;
import com.protectoria.psa.ui.activities.enrollment.EnrollmentActivity;
import com.protectoria.psa.ui.PsaActivity;
import com.protectoria.psa.PsaManager;
import com.protectoria.psa.api.PsaConstants;
import com.protectoria.psa.api.entities.SpaEnrollData;
import com.protectoria.psa.dex.common.data.enums.PsaType;
import com.protectoria.psa.dex.common.ui.PageTheme;
import com.protectoria.psa.dex.common.utils.logger.ExceptionLogger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(PsaConstants.ACTION_AUTHORIZATION_RESULT_EVENT));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ExceptionLogger el = new ExceptionLogger() {
            @Override
            public void exception(String s, Exception e) {

            }

            @Override
            public void setUserIdentificator(String s) {

            }
        };
        PsaManager psaManager = PsaManager.init(this.getApplicationContext(), el);
        psaManager.setPssAddress("127.0.0.1:8123");
        PsaManager.getRequiredPermissions();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("MyFirebaseMessagingServ", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = "test";
                        Log.d("MyFirebaseMessagingServ", msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });


        myRef.setValue("ProtectoriaPSD2 Notification");

        String appPns = "";
        String pubPSS = "MIIBCgKCAQEAxgyacF1NNWTA6rzCrtK60se9fVpTPe3HiDjHB7MybJvNdJZIgZbE"+
                "9k3gQ6cdEYgTOSG823hkJCVHZrcf0/AK7G8Xf/rjhWxccOEXFTg4TQwmhbwys+sY"+
                "/DmGR8nytlNVbha1DV/qOGcqAkmn9SrqW76KK+EdQFpbiOzw7RRWZuizwY3BqRfQ"+
                "Rokr0UBJrJrizbT9ZxiVqGBwUD BQrSpsj3RUuoj90py1E88ExyaHui+jbXNITaPB"+
                "UFJjbas5OOnSLVz6GrBPOD+x0HozAoYuBdoztPRxpjoNIYvgJ72wZ3kOAVPAFb48"+
                "UROL7sqK2P/jwhdd02p/MDBZpMl/+BG+qQIDAQAB";
        String installationId = "9990";

        SpaEnrollData data = new SpaEnrollData(
                database.getApp().getToken(true).toString(), //Firebase InstanceID
                pubPSS, //obtained from OkayThis SDK Docs, exclusive for Android devices
                installationId, //obtained from OkayThis SDK Docs, exclusive for Android devices
                null, //optional for changing auth page appearance
                PsaType.OKAY); //Instructed to use this enum on OkayThis SDK Docs

        PsaManager.getRequiredPermissions();
        PsaManager.startEnrollmentActivity(this, data);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
        }
    };

    protected void onActivityResult(Bundle savedInstanceState) {
        //Log.d("MyFirebaseMessagingServ", resultCode);
        //Toast.makeText(MainActivity.this, resultCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}
