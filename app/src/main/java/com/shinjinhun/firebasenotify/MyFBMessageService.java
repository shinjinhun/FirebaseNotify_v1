package com.shinjinhun.firebasenotify;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyFBMessageService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessageService";

    public MyFBMessageService() {
    }

//    @Override
//    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        long mNow;
        Date mDate;
        SimpleDateFormat mFormat = new SimpleDateFormat("MMddhhmmss");

        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);

        String strIdx = mFormat.format(mDate);  // 날짜로 인덱스 지정
        //int numInt = Integer.parseInt(strIdx);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            String messageBody = remoteMessage.getData().get("message") + strIdx;
            String title = remoteMessage.getData().get("title") + strIdx;

            Log.e("jhTest","오레오 버전 이상임 추가 작업이 필요함..");
            Log.e("jhTest","title : " + title );
            Log.e("jhTest","messageBody : " + messageBody );

            sendNotification_v8(Integer.parseInt(strIdx), title, messageBody);

        } else {
            String messageBody = remoteMessage.getData().get("message") + strIdx;
            String title = remoteMessage.getData().get("title") + strIdx;
            sendNotification(Integer.parseInt(strIdx), title, messageBody);
        }
    }


    // 오레오(v8) 이상 실행
    private void sendNotification_v8(int numInt, String title, String messageBody) {

        String channelId = "FCMTest";
        String channelName = "FCMTest Name";
        Resources res = getResources();

        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /*request code*/, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,channelId);

        notificationBuilder.setContentTitle(title)     // 상태바 드래그시 보이는 타이틀
                .setContentText(messageBody)       // 상태바 드래그시 보이는 서브타이틀
                .setTicker(messageBody)    // 상태바 한줄 메시지
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)    // 알림 터치시 반응 후 삭제
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL); // 알림, 사운드 진동 설정

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);

            notificationManager.createNotificationChannel(mChannel);
        }

         notificationManager.notify(numInt, notificationBuilder.build());

    }

    private void sendNotification(int numInt, String title, String messageBody) {

        Log.e("jhTest","메세지 수신");

        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0 /*request code*/, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title+"님 의 메시지입니다.")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(numInt,notificationBuilder.build());
    }
}
