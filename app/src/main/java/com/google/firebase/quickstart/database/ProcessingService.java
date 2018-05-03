package com.google.firebase.quickstart.database;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.LongBinaryOperator;

/**
 * Created by mayurphadte on 13/03/18.
 */

public class ProcessingService extends IntentService{
    String user_id="";
    public ProcessingService(){
        super("ProcessingService");

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        user_id = intent.getStringExtra("userid");
        Log.e("user_id is ",user_id);
        return super.onStartCommand(intent, flags, startId);

    }

    int restart_flag=0;
    int run_loop=1;
    String hash;
    Loop current_Thread;
    Long currid=0L;
    Long num_devices=0L;
    class restartThread extends Thread{
        public void run()
        {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference rest = database.getReference("restart");


            rest.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e("restart","changed");
                    restart_flag=1;
                    run_loop=0;
                    //get new device id's
                    DatabaseReference dev_num = database.getReference("num_devices");
                    dev_num.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            num_devices= (Long) dataSnapshot.getValue();

                            Log.e("new Num_devices is + ", Integer.toString(num_devices.intValue()));
                            DatabaseReference dev_id = database.getReference("status/"+user_id+"/id");
                            dev_id.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    String type=dataSnapshot.getValue().getClass().getName();
                                    //Log.e("Type",dataSnapshot.getValue().getClass().getName());
                                    if (type=="java.lang.String")
                                        Log.d("ID:",(String) dataSnapshot.getValue());
                                    else if (type=="java.lang.Long")
                                    {
                                        Long temp=(Long) dataSnapshot.getValue();
                                        currid=temp;
                                        //Log.e("ID",Integer.toString(temp.intValue()));
                                    }
                                    //Log.e("new dev_id is + ", Integer.toString(currid.ingetValue()));
                                    run_loop=1;
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });



                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    /*class getIDThread extends Thread{
        public void run()
        {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference rest = database.getReference("status/");


            rest.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e("restart","changed");
                    restart_flag=1;
                    run_loop=0;
                    //get new device id's

                    run_loop=1;
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    class restartThread extends Thread{
        public void run()
        {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();

            DatabaseReference rest = database.getReference("restart");


            rest.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e("restart","changed");
                    restart_flag=1;
                    run_loop=0;
                    //get new device id's

                    run_loop=1;
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
*/

    class loopThread extends Thread {
        public void run() {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("hash");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.e("hi", "changed");

                    if (dataSnapshot.getValue().toString().length() == 32) {
                        hash = dataSnapshot.getValue().toString();
                        run_loop = 1;
                        Log.e("New Hash value is: ", hash);


                    }
                    //Log.e("Hi", dataSnapshot.getValue().toString());


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
    class Loop extends Thread {
        public void run() {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            while (true) {
               // if (run_loop == 1) {
                    loop_run();
                   // if (answer=="")
                     //   database.getReference("result").setValue("not found");

                //}
            }
        }

        public void loop_run() {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            long startTime=0;
            long endTime = 0;
            Long counter = 0L;
            //Log.e("Hi", "sdasdsdaaaaaaaaaaaa");
            //Log.e("restart value is ", Integer.toString(restart_flag));
            while (restart_flag == 1) {

                Log.e("DEVICES","num_devices is : "+num_devices);
                counter=0L  ;
                if (num_devices!=0) {
                    restart_flag = 0;
                    startTime = System.currentTimeMillis();
                    for (char alphabet1 = 'a'; alphabet1 <= 'z'; alphabet1++) {
                        Log.e("running", "loop " + alphabet1 + " with currID " + currid + " and num_devices "  + num_devices);
                        for (char alphabet2 = 'a'; alphabet2 <= 'z'; alphabet2++) {
                            for (char alphabet3 = 'a'; alphabet3 <= 'z'; alphabet3++) {
                                for (char alphabet4 = 'a'; alphabet4 <= 'z'; alphabet4++) {
                                    //Log.d("..", MD5(String.valueOf(alphabet1) + String.valueOf(alphabet2) + String.valueOf(alphabet3) + String.valueOf(alphabet4)));

                                    if ((counter % (num_devices) == currid)) {
                                        //Log.d("Hi",String.valueOf(alphabet1) + String.valueOf(alphabet2) + String.valueOf(alphabet3) + String.valueOf(alphabet4));
                                        //counter = 0;
                                        if (MD5(String.valueOf(alphabet1) + String.valueOf(alphabet2) + String.valueOf(alphabet3) + String.valueOf(alphabet4)).equals(hash)) {
                                            Log.e("Found at", String.valueOf(alphabet1) + String.valueOf(alphabet2) + String.valueOf(alphabet3) + String.valueOf(alphabet4));

                                            endTime = System.currentTimeMillis();
                                            database.getReference("result").setValue(String.valueOf(alphabet1) + String.valueOf(alphabet2) + String.valueOf(alphabet3) + String.valueOf(alphabet4));
                                            database.getReference("time").setValue(String.valueOf(endTime - startTime) + " ms");
                                           // return String.valueOf(alphabet1)+String.valueOf(alphabet2)+String.valueOf(alphabet3)+String.valueOf(alphabet4);
                                            //this.stopSelf();
                                        }
                                    } else {
                                        //counter = 1;
                                    }
                                    counter++;
                                    if (restart_flag == 1) {
                                        Log.e("Broke: ", "Loop:");
                                        break;
                                    }
                                }
                                if (restart_flag == 1) break;
                            }
                            if (restart_flag == 1) break;
                        }
                        if (restart_flag == 1) break;
                    }
                }

            }
            //long elapsedTime = endTime - startTime;
            //Log.e("Time: ",String.valueOf(elapsedTime));
           // return "";
        }
    }
    //}



    @Override
    protected void onHandleIntent(Intent intent){
        String string_to_be_converted_to_MD5 = "555555";
        //Log.d("md5:", MD5_Hash_String);
        //Log.d("md5:", "Hi");
        //Hello

        restartThread R= new restartThread();
        R.start();
        loopThread L= new loopThread();
        L.start();
        Loop LOOP= new Loop();
        LOOP.start();
        current_Thread=LOOP;



        //Log.d("Hi", MD5("wxyz"));
        //Log.d("Hi", "a7c3c2aa70d99921f9fb23ac87382997");

    }
    public String MD5(String md5) {

        try {

            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");

            byte[] array = md.digest(md5.getBytes());

            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < array.length; ++i) {

                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));

            }

            return sb.toString();

        } catch (java.security.NoSuchAlgorithmException e) {

        }

        return null;

    }

}