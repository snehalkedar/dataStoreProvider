package tv.techm.data.provider;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

public class DataProviderReceiver extends BroadcastReceiver {
	static final String TAG = "DataProviderReceiver";
	static final String PREF_DB_VERSION = "db_version";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v(TAG, "  onReceive");
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			Log.v(TAG, "ACTION_BOOT_COMPLETED");
			try {
				SharedPreferences sPrefs = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
				int prefVersion = sPrefs.getInt(PREF_DB_VERSION, 0);

				if(prefVersion != DataProviderHelper.DATABASE_VERSION){
					sPrefs.edit().putInt(PREF_DB_VERSION, DataProviderHelper.DATABASE_VERSION).commit();
					DataProviderHelper dpHelper = DataProviderHelper.getInstance(context);
					dpHelper.getWritableDatabase();
					dpHelper.close();
				}

			} catch (Throwable t) {
				Log.e(TAG, "Error during upgrade attempt", t);
				context.getPackageManager().setComponentEnabledSetting(
						new ComponentName(context, getClass()), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
			}
		}
	}
}
