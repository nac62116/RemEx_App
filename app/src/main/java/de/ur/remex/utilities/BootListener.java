package de.ur.remex.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BootListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                // TODO: Inform ExperimentController to set the next alarm.
                Toast toast = Toast.makeText(context, "RemEx App: BootListener", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }
}
