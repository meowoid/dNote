package com.digutsoft.note;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.digutsoft.note.classes.DMMemoTools;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class DMSaveFromWear extends WearableListenerService {
    private static final String dNoteSaveNote = "/dNote-Save-Note";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(dNoteSaveNote)) {
            String wearNoteContent = new String(messageEvent.getData());
            String wearNoteCategory = getString(R.string.sf_wearable);

            DMMemoTools.createCategory(getApplicationContext(), wearNoteCategory);
            switch (DMMemoTools.saveMemo(getApplicationContext(), wearNoteCategory, wearNoteContent)) {
                case 0:
                    Intent intStartView = new Intent(getApplicationContext(), DMMain.class);
                    intStartView.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intStartView.putExtra("mvCategoryName", wearNoteCategory);
                    startActivity(intStartView);
                    break;
                default:
                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification notification;
                    Intent intent = new Intent(getApplicationContext(), DMSaveFrom.class);
                    intent.putExtra(Intent.EXTRA_TEXT, new String(messageEvent.getData()));
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        notification = new NotificationCompat.Builder(getApplicationContext())
                                .setContentTitle(getString(R.string.sf_wearable_fail))
                                .setContentText(getString(R.string.sf_wearable_fail_message))
                                .setTicker(getString(R.string.sf_wearable_fail))
                                .setContentIntent(pendingIntent)
                                .setSmallIcon(R.drawable.ic_launcher)
                                .build();
                    } else {
                        notification = new Notification(R.drawable.ic_launcher, null, System.currentTimeMillis());
                        notification.tickerText = getString(R.string.sf_wearable_fail);
                        notification.setLatestEventInfo(getApplicationContext(), getString(R.string.sf_wearable_fail), getString(R.string.sf_wearable_fail_message), pendingIntent);
                    }

                    notificationManager.notify(1, notification);
                    break;
            }
        }
    }
}
