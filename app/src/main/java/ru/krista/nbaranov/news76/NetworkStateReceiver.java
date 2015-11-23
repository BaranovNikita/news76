package ru.krista.nbaranov.news76;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;


public class NetworkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {


        if (intent.getExtras() != null) {
            final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

            if (ni != null && ni.isConnectedOrConnecting()) {
                Toast.makeText(context, MainActivity.getInstance().getResources().getString(R.string.yes_connection) , Toast.LENGTH_LONG).show();
                MainActivity.getInstance().displayAlert();
            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
                Toast.makeText(context, MainActivity.getInstance().getResources().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
            }
        }
    }
}
