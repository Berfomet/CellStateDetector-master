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

    Handler handler2;
    Handler handler;
    Boolean token;
    private static final String DATABASE_NAME = "celdas_db";
    private CeldaDb celdaDb;
    CellRegistered cellToken;

    //Constructor
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //inicialización de variables
        handler = new Handler(getMainLooper());
        token = false;
        handler2=new Handler(getMainLooper());
        final TextView tvDetails = findViewById(R.id.detailsNetTx);
        celdaDb = Room.databaseBuilder(getApplicationContext(),
                CeldaDb.class, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build(); //inicialización de la db de persistencia

        //declaración de elementos visuales
        final Button button = findViewById(R.id.button);
        final Button btnInfo = findViewById(R.id.btnInfo);

        button.setText("START process");
        addCellSampleData();

        //boton encargado de incializar o parar la ejecución del proceso encargado de recopilar la
        //información de la celda.
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

        //botón donde se inicializa la consulta la db y la muestra del nombre de la ultima celda captada.
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //si el token es falso y existe un token en caché
                if (!token&&cellToken!=null) {

                    //es probable que se pueda omitir este try, por precaución se debería conservar.
                    try {

                        switch (cellToken.getType()) {
                            case "UMTS": //aún no tengo data de prueba UMTS ni GSM
                                break;

                            case "LTE": // se inicializa la muestra de data de LTE
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //se hace el query cogiendo el enodBId guardado en el caché
                                        //y consultando a la db para que devuelva el correspondiente
                                        final Celda cellShown;
                                        cellShown = celdaDb.celdaRepository().
                                                fetchOneCeldabyEnodBId(cellToken.getCid());
                                        //Se comprueba que la consulta haya retornado algo
                                        if(cellShown!=null) {
                                            handler2.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    tvDetails.setText(tvDetails.getText() + "Nombre Estación: " + cellShown.getCellName());
                                                }
                                            });
                                        } else { //En caso no existe, se informa al usuario
                                            Toast.makeText(MainActivity.this,
                                                    "No cell found in the database",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).start();
                        }
                        tvDetails.setText(tvDetails.getText() + "");
                    } catch (Exception e) {
                    e.printStackTrace();
                }
                //En caso el token es verdadero (la app sigue cogiendo data) se informa al usuario
                } else if (token) {
                    Toast.makeText(MainActivity.this,
                            "Info reception must be at halt",
                            Toast.LENGTH_SHORT).show();
                }else{ //En caso no se haya recopilado nada de info se informa al usuario
                    Toast.makeText(MainActivity.this,
                            "No cell stored in cache",
                            Toast.LENGTH_SHORT).show();
                }
                Log.i("tagge", "here from Main Activity");
            }
        });

    }

    //metodo para añadir data de testeo en la db, ignorar. en el futuro se eliminará
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

    //metodo para resetear la barra indicadora de la potencia de la celda.
    public void resetBar() {

        View bar = findViewById(R.id.powerBar);
        LinearLayout vlBar = findViewById(R.id.vlBar);

        bar.setBackgroundColor(getColor(R.color.colorDEF));
        vlBar.getLayoutParams().height=38;
        vlBar.requestLayout();

    }

    //Metodo que permite manipular la barra que muestra información visual sobre la potencia
    public void setBar(int power) {

        View bar = findViewById(R.id.powerBar);
        LinearLayout vlBar = findViewById(R.id.vlBar);

        //en un futuro la idea es que no sea 49f, sino un valor que este en relación a la resolución
        //de la pantalla del dispositivo
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

    //java parsea el codigo en pixeles, por lo tanto, es necesario tener un conversor de dpi a px
    //para poder mantener una relación coherente entre el codigo y la resolución de la pantalla.
    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().
                densityDpi /
                DisplayMetrics.DENSITY_DEFAULT);
    }

    //Thread paralelo responsable de correr los metodos encargados de recopilar la información de
    //de la celda a la que el celular se encuentra conectado
    public void CellInfoState() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //cambiado while por do/while ya que el while presentaba problemas al
                            //momento de para el loop. Por otra parte, do/while es una herramienta
                            //mucho más facil de entender y de manipular.
                            do {

                                try {
                                    Thread.sleep(10);

                                    //Se extraen los parametros de la celda mediante el metodo de
                                    //la clase CellParameterGetter.
                                    final CellParameteGetter cellMonitor =
                                            new CellParameteGetter(getApplication());
                                    final CellRegistered celda = cellMonitor.action_monitor();
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            resetBar();

                                            if (celda != null) {

                                                //en caso exista una celda, se muestran los valores
                                                //en la pantalla
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
                                                cellToken = celda;

                                            }

                                            //en caso no exista celda, se muestra en pantalla
                                            else {
                                                Toast.makeText(MainActivity.this, "Cannot Get info rn", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    });




                                } catch (Exception e) {
                                    Log.i("Tagged","toy en el thread");
                                }

                            } while (token==true);

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
