package com.example.marcoycaza.cell_state_detector.Controller;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcoycaza.cell_state_detector.Entity.Celda;
import com.example.marcoycaza.cell_state_detector.R;
import com.example.marcoycaza.cell_state_detector.Service.CeldaDb;
import com.example.marcoycaza.cell_state_detector.Service.CellParameteGetter;
import com.example.marcoycaza.cell_state_detector.Service.CellRegistered;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Handler handler;
    Boolean token;
    private static final String DATABASE_NAME = "celdas_db";
    private CeldaDb celdaDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler(getMainLooper());
        token = false;
        celdaDb = Room.databaseBuilder(getApplicationContext(),
                CeldaDb.class, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
        final Button button = findViewById(R.id.button);
        button.setText("START process");


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (token) {
                    button.setText("START process");
                } else {
                    button.setText("STOP process");
                }

                token = !token;

                callPermissions();

                Log.i("tagge", "here from Main Activity");
            }
        });


    }

    private void addCellSampleData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Celda> cellList = new ArrayList<>();
                Celda celda =new Celda();
                celda.setCellName("Celda_4G_Prueba");
                celda.setEnodBId(75);
                cellList.add(celda);
                celdaDb.celdaRepository().insertCellArray(cellList);
            }
        }) .start();
    }

    public void resetBar() {

        View bar = findViewById(R.id.powerBar);
        LinearLayout vlBar = findViewById(R.id.vlBar);

        bar.setBackgroundColor(getColor(R.color.colorDEF));
        vlBar.getLayoutParams().height=38;
        vlBar.requestLayout();

    }

    public void setBar(int power) {

        View bar = findViewById(R.id.powerBar);
        LinearLayout vlBar = findViewById(R.id.vlBar);

        float size = convertDpToPixel(49f,this);

        if (power <= -120) {
            bar.setBackgroundColor(getColor(R.color.color1));
            vlBar.getLayoutParams().height = (int) size;
        } else if (power <= -115) {
            bar.setBackgroundColor(getColor(R.color.color2));
            vlBar.getLayoutParams().height = (int) size * 2;
        } else if (power <= -110) {
            bar.setBackgroundColor(getColor(R.color.color3));
            vlBar.getLayoutParams().height = (int) size * 3;
        } else if (power <= -105) {
            bar.setBackgroundColor(getColor(R.color.color4));
            vlBar.getLayoutParams().height = (int) size * 4;
        } else if (power <= -100) {
            bar.setBackgroundColor(getColor(R.color.color5));
            vlBar.getLayoutParams().height = (int) size * 5;
        } else if (power <= -95) {
            bar.setBackgroundColor(getColor(R.color.color6));
            vlBar.getLayoutParams().height = (int) size * 6;
        } else if (power <= -90) {
            bar.setBackgroundColor(getColor(R.color.color7));
            vlBar.getLayoutParams().height = (int) size * 7;
        } else if (power <= -85) {
            bar.setBackgroundColor(getColor(R.color.color8));
            vlBar.getLayoutParams().height = (int) size * 8;
        } else if (power <= -80) {
            bar.setBackgroundColor(getColor(R.color.color9));
            vlBar.getLayoutParams().height = (int) size * 9;
        } else if (power <= -0) {
            bar.setBackgroundColor(getColor(R.color.color10));
            vlBar.getLayoutParams().height = (int) size * 10;
        } else {
            resetBar();
        }
        vlBar.requestLayout();
    }

    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public void CellInfoState() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (true) {

                                try {
                                    Thread.sleep(10);

                                    //Declare final variables under the thread concept
                                    final TelephonyManager simpleNetType = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                                    final CellParameteGetter cellMonitor = new CellParameteGetter(getApplication());
                                    final CellRegistered celda = cellMonitor.action_monitor();
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            resetBar();

                                            if (celda != null) {

                                                TextView tvType = findViewById(R.id.netTypeTx);
                                                TextView tvDetails = findViewById(R.id.detailsNetTx);

                                                tvType.setText(celda.getType());
                                                tvDetails.setText("Cell Id: " + celda.getCid() + "\n");
                                                if(celda.getType()=="LTE"){
                                                    tvDetails.setText(tvDetails.getText()+"TAC: "+celda.getLac()+"\n");
                                                } else {
                                                    tvDetails.setText(tvDetails.getText()+"LAC: "+celda.getLac()+"\n");
                                                }
                                                tvDetails.setText(tvDetails.getText()+"Potencia: "+celda.getDbm()+"\n");
                                                if(celda.getType()=="LTE"){
                                                    tvDetails.setText(tvDetails.getText()+"PCI: "+celda.getPci()+"\n");
                                                }else if(celda.getType()=="WCDMA"){
                                                    tvDetails.setText(tvDetails.getText()+"PSC: "+celda.getPsc()+"\n");
                                                }

                                                setBar(celda.getDbm());

                                            } else {
                                                Toast.makeText(MainActivity.this, "Cannot Get info rn", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });




                                } catch (Exception e) {
                                    Log.i("Tagged","toy en el thread");
                                }

                            }

                        }
                    }).start();

        }else{
        callPermissions();
        }

    }

    public void callPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

        Permissions.check(getApplicationContext(), permissions, "This permissions are required ok",
                null/*options*/, new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        CellInfoState();
                    }

                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        super.onDenied(context, deniedPermissions);
                        callPermissions();
                    }
                });

    }

}
