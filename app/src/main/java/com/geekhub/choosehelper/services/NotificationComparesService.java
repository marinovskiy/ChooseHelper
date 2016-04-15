package com.geekhub.choosehelper.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.geekhub.choosehelper.R;
import com.geekhub.choosehelper.models.db.Following;
import com.geekhub.choosehelper.models.db.User;
import com.geekhub.choosehelper.models.network.NetworkCompare;
import com.geekhub.choosehelper.models.network.NetworkUser;
import com.geekhub.choosehelper.screens.activities.DetailsActivity;
import com.geekhub.choosehelper.screens.activities.MainActivity;
import com.geekhub.choosehelper.utils.db.DbUsersManager;
import com.geekhub.choosehelper.utils.firebase.FirebaseConstants;

import java.util.ArrayList;
import java.util.List;

public class NotificationComparesService extends IntentService {

    private Firebase firebase = new Firebase(FirebaseConstants.FB_REF_MAIN)
            .child(FirebaseConstants.FB_REF_COMPARES);

    private boolean mIsNeedToNotify = false;

    public NotificationComparesService() {
        super(NotificationComparesService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("servicetags", "onHandleIntent: ");
        User user = DbUsersManager.getCurrentUser();
        List<String> followings = new ArrayList<>();
        for (Following following : user.getFollowings()) {
            followings.add(following.getUserId());
        }

        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*if (!mIsNeedToNotify) {
                    mIsNeedToNotify = true;
                } else {*/
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        NetworkCompare networkCompare = snapshot.getValue(NetworkCompare.class);
                        if (followings.contains(networkCompare.getUserId())) {
                            new Firebase(FirebaseConstants.FB_REF_MAIN)
                                    .child(FirebaseConstants.FB_REF_USERS)
                                    .child(networkCompare.getUserId())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            NetworkUser networkUser = dataSnapshot.getValue(NetworkUser.class);
                                            showNotification(networkUser.getFullName(), dataSnapshot.getKey());
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {
                                        }
                                    });
                        }
                    //}
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void showNotification(String userName, String compareId) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.icon_notifications)
                        .setContentTitle("New compare")
                        .setContentText(String.format("Hi! %s added new compare. " +
                                "Do you want to see it?", userName))
                        .setAutoCancel(true);

        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.INTENT_KEY_COMPARE_ID, compareId);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(intent);

        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("New compare was added");
        mBuilder.setStyle(inboxStyle);
        Notification notification = mBuilder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(101, notification);
    }

}
