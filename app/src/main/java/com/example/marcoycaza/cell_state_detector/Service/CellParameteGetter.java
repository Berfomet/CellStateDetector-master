package com.example.marcoycaza.cell_state_detector.Service;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;


public class CellParameteGetter {

    private TelephonyManager telephonyManager;
    private Application application;

    public CellParameteGetter(Application application) {
        this.application = application;
        this.telephonyManager = (TelephonyManager) application.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
    }

    @SuppressLint("MissingPermission")
    public CellRegistered action_monitor() {

        Log.d("tagge", "here from Cell ParameterGetter Class");

        final CellRegistered cReg = new CellRegistered();

        try {
                cReg.setType(getNetworkClass(telephonyManager.getNetworkType()));

                //En base al tipo de tecnología que capta el celular de la celda a la que esta conectada
            // te deriva a la parte donde se mostrará la información correspondiente
                switch (cReg.getType()){
                    case "GSM":

                        CellInfoGsm cellInfoGsm =
                                (CellInfoGsm) telephonyManager.getAllCellInfo().get(0);
                        CellIdentityGsm identityGsm = cellInfoGsm.getCellIdentity();
                        cReg.setDbm(cellInfoGsm.getCellSignalStrength().getDbm());
                        cReg.setCid(identityGsm.getCid());
                        cReg.setLac(identityGsm.getLac());
                        break;

                    case "WCDMA":

                        CellInfoWcdma cellinfoWcdma =
                                (CellInfoWcdma) telephonyManager.getAllCellInfo().get(0);
                        CellIdentityWcdma identityWcdma = cellinfoWcdma.getCellIdentity();
                        cReg.setDbm(cellinfoWcdma.getCellSignalStrength().getDbm());
                        cReg.setCid(identityWcdma.getCid()&0xffff);
                        cReg.setLac(identityWcdma.getLac());
                        cReg.setPsc(identityWcdma.getPsc());
                        break;

                    case "LTE":

                        CellInfoLte cellInfoLte =
                                (CellInfoLte) telephonyManager.getAllCellInfo().get(0);
                        CellIdentityLte identityLte = cellInfoLte.getCellIdentity();
                        cReg.setDbm(cellInfoLte.getCellSignalStrength().getDbm());
                        cReg.setCid(identityLte.getCi()/256);
                        cReg.setLac(identityLte.getTac());
                        cReg.setPci(identityLte.getPci());
                        break;

                    default:

                        return null;
                }

                return cReg;

            } catch (Exception e) {
                Toast.makeText(application.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        return cReg;
    }

    //metodo para obtener el tipo de tecnologia a la cual tu celular esta conectado.
    private String getNetworkClass(int networkType) {

        switch (networkType) {

            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "GSM";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "WCDMA";
            /*
             * Above API level 7, make sure to set android:targetSdkVersion
             * to appropriate level to use these
             */
            case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                return "LTE";
            // Unknown
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            default:
                return "UNKNOWN";
        }

    }

}
