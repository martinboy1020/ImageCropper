package com.example.imagecropper.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class CheckConnectStatusManager {

    public static boolean checkGpsEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } else {
            return false;
        }
    }

//    public boolean checkBluetoothEnable() {
//
//        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        if (bluetoothAdapter == null) {
//            // device can not support ble.
//            return false;
//        } else {
//            if (bluetoothAdapter.isEnabled()) {
//                return true;
//            } else {
//                return false;
//            }
//        }
//
//    }

    public boolean checkInternet(Context context) {
        if (checkConnectType(context) == ConnectivityManager.TYPE_MOBILE) {
            return checkMobileConnected(context);
        } else {
            return checkWifiEnable(context);
        }
    }

    private static int checkConnectType(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (manager != null) {
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.getType();
            } else {
                return -1;
            }
        } else {
            return -1;
        }

    }

    private boolean checkMobileConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;

        if (manager != null) {
            networkInfo = manager.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            if (networkInfo.isConnected()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean checkWifiEnable(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager != null) {
            if (wifiManager.isWifiEnabled()) {
                if(checkInternetConnect(context)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    private boolean checkInternetConnect(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

        if (manager != null) {
            NetworkInfo info = manager.getActiveNetworkInfo();

            if (info == null || !info.isConnected()) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }

    }

    public static boolean checkNetWorkConnect(Context context) {

        if (checkConnectType(context) == ConnectivityManager.TYPE_MOBILE) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = null;
            if (manager != null) {
                networkInfo = manager.getActiveNetworkInfo();
            }
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                if (networkInfo.isConnected()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if (wifiManager != null) {
                if (wifiManager.isWifiEnabled()) {
                    ConnectivityManager manager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

                    if (manager != null) {
                        NetworkInfo info = manager.getActiveNetworkInfo();

                        if (info == null || !info.isConnected()) {
                            return false;
                        } else {
                            return true;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

    }
//    private class NetworkStateReceiver extends BroadcastReceiver {
//
//        private LocationManager locationManager;
//
//        private boolean gps_enabled = false;
//        private boolean network_enabled = false;
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//
//            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//            if(manager != null) {
//                NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
//                if (null != activeNetwork) {
//                    if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI
//                            || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
//                        onConnectionListener.internetConnectListener(true);
//                    }
//                } else {
//                    networkNegativeListener.listener();
//                }
//            }
//
//
//            if (action.equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
//                if (locationManager == null) {
//                    locationManager = (LocationManager) mAct.getSystemService(Context.LOCATION_SERVICE);
//                }
//
//                try {
//                    gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                try {
//                    network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                if (!gps_enabled && !network_enabled && gpsListener != null) {
//                    gpsListener.listener();
//                }
//            }
//
//            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
//                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
//                switch (state) {
//                    case BluetoothAdapter.STATE_OFF:
//                        if (bluetoothListener != null) {
//                            bluetoothListener.listener();
//                        }
//                        break;
//                    case BluetoothAdapter.STATE_ON:
//
//                        break;
//                }
//            }
//        }
//    }

}