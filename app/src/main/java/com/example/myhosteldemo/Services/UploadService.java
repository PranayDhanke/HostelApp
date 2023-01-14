package com.example.myhosteldemo.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.myhosteldemo.R;
import com.example.myhosteldemo.Resources;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class UploadService extends Service {

    public static String CHANNELID = "Upload Notification" ;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notiIIntent = new Intent(this , Resources.class) ;
        PendingIntent pendingIntent = PendingIntent.getActivity(this , 0 , notiIIntent , 0) ;

        Uri uri = intent.getParcelableExtra("Uri") ;
        String type = intent.getStringExtra("Type") ;
        String size = intent.getStringExtra("Size") ;

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            NotificationChannel service = new NotificationChannel(CHANNELID , CHANNELID , NotificationManager.IMPORTANCE_HIGH) ;
            NotificationManager manager = getSystemService(NotificationManager.class) ;
            manager.createNotificationChannel(service);
        }

        Notification notification = new NotificationCompat.Builder(this , CHANNELID)
                .setContentTitle("Uploading....")
                .setContentText("Type = " + type + "\tSize = "+ size)
                .setSmallIcon(R.drawable.information)
                .setContentIntent(pendingIntent)
                .build() ;

        uploadToStorage(uri , type , size) ;

        startForeground(1 , notification);

        return START_NOT_STICKY ;
    }

    private void updateNotification(long total , long complete){
        NotificationChannel service = null ;
        NotificationManager manager = null;

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            service = new NotificationChannel(CHANNELID , CHANNELID , NotificationManager.IMPORTANCE_HIGH) ;
            manager = getSystemService(NotificationManager.class) ;
            manager.createNotificationChannel(service);
        }

        Intent notiIIntent = new Intent(this , Resources.class) ;
        PendingIntent pendingIntent = PendingIntent.getActivity(this , 0 , notiIIntent , 0) ;

            Notification notification = new NotificationCompat.Builder(this, CHANNELID)
                    .setContentTitle("Uploading....")
                    .setContentText("Total = " + total + "\tuploaded = " + complete)
                    .setSmallIcon(R.drawable.information)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .setSilent(true)
                    .build();

        startForeground(1 , notification);

    }

    private void notifySuccess(){
        NotificationChannel service = null ;
        NotificationManager manager = null;

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            service = new NotificationChannel(CHANNELID , CHANNELID , NotificationManager.IMPORTANCE_HIGH) ;
            manager = getSystemService(NotificationManager.class) ;
            manager.createNotificationChannel(service);
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNELID)
                .setContentTitle("Success")
                .setContentText("File uploaded successfully...")
                .setSmallIcon(R.drawable.information)
                .build();

        if(manager != null){
            manager.notify(1 , notification);
        }
    }

    private void notifyFailure(){
        NotificationChannel service = null ;
        NotificationManager manager = null;

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            service = new NotificationChannel(CHANNELID , CHANNELID , NotificationManager.IMPORTANCE_HIGH) ;
            manager = getSystemService(NotificationManager.class) ;
            manager.createNotificationChannel(service);
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNELID)
                .setContentTitle("Fail")
                .setContentText("File failed to upload...")
                .setSmallIcon(R.drawable.error)
                .build();

        if(manager != null){
            manager.notify(1 , notification);
        }
    }

    private void uploadToStorage(Uri uri , String type , String size) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference() ;
        String key = ref.child("Resources/Computer/").push().getKey() ;
        StorageReference storage = FirebaseStorage.getInstance().getReference().child("Resources/Computer/").child(key) ;

        UploadTask uploadTask = storage.putFile(uri) ;

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                updateNotification(snapshot.getTotalByteCount() , snapshot.getBytesTransferred()) ;
            }
        }) ;

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful()){
                                Map<String , String> map = new HashMap<>() ;
                                map.put("Title" , "Android development") ;
                                map.put("Desc" , "To learn about android") ;
                                map.put("Url" , task.getResult().toString()) ;
                                map.put("Size" , size) ;
                                map.put("Type" , type) ;
                                ref.child("Resources/Computer/").child(key).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(UploadService.this, "File uploaded to storage succesfully", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toast.makeText(UploadService.this, "Failed to upload file " + task.getException(), Toast.LENGTH_LONG).show();
                                            stopForeground(true);
                                            notifyFailure();
                                        }
                                    }
                                }) ;

                                stopForeground(true);
                                notifySuccess();
                            }
                            else{
                                Toast.makeText(UploadService.this, "Failed to upload file " + task.getException(), Toast.LENGTH_LONG).show();
                                stopForeground(true);
                                notifyFailure();
                            }

                        }
                    }) ;
                }
                else{
                    Toast.makeText(UploadService.this, "Failed to upload file " + task.getException(), Toast.LENGTH_LONG).show();
                    stopForeground(true);
                    notifyFailure();
                }
            }
        }) ;
    }


}
