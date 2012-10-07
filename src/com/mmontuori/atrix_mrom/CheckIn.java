package com.mmontuori.atrix_mrom;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CheckIn extends Service {
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		doCheck();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_NOT_STICKY;
	}

	protected void doCheck() {
		try {
			Settings s = new Settings();
			s.loadPrefs(this.getBaseContext());
			if (!s.checkOnBoot) {
				Log.i(Utils.TAG, "skipping on-boot check");
				return;
			}
			Log.i(Utils.TAG, "starting on-boot check");
			String remote_version = Utils.checkForNewVersion(this.getBaseContext(), true);
			if (remote_version != null) {
				NotificationManager notifManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
				Notification note = new Notification(R.drawable.ic_launcher, getText(R.string.alert), System.currentTimeMillis());
				note.setLatestEventInfo(this,
					getText(R.string.alert),
					String.format(getText(R.string.new_version_available).toString(), remote_version),
					PendingIntent.getActivity(this, 0, new Intent(this, CheckIn.class), 0)
				);
				notifManager.notify(2456, note);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			String http = Utils.getData();
			if (http != null) {
				System.err.println("received HTTP data: [" + http + "]");
			}
		}
	}
}
