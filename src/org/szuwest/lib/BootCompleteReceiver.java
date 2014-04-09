package org.szuwest.lib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompleteReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(action != null && action.equals(Intent.ACTION_BOOT_COMPLETED)){
//       	 	Intent service = new Intent(context, AlarmService.class);
//       	 	context.startService(service);
        }
	}
}
