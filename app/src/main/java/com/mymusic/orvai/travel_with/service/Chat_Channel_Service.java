package com.mymusic.orvai.travel_with.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.mymusic.orvai.travel_with.Activity.Watching_Room;
import com.mymusic.orvai.travel_with.Interface.ServiceCallbacks;
import com.mymusic.orvai.travel_with.URLs.URLs;
import com.mymusic.orvai.travel_with.Utils.GlobalApplication;
import com.mymusic.orvai.travel_with.sharef_mgr.SharedPreferences_M;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * 소켓채널을 통신을 위한 서비스 클래스
 */

public class Chat_Channel_Service extends Service {

    private static final String HOST = "13.125.242.174"; // 테스트를 위한 LocalAddress(Wifi) 주소
    private static final int PORT = 5001;
    SocketChannel socketChannel; // 소켓채널 객체
    OutputStream cos;
    Client_Channel clientChannel;
    Context mCtx;
    private final IBinder mBinder = new MyLocalBinder(); // 액티비티와 서비스의 데이터 교환을 위한 바인더 생성
    public ServiceCallbacks serviceCallbacks; // 액티비티와 서비스의 데이터 교환을 위한 서비스 콜백 생성


    public class MyLocalBinder extends Binder { // 바인드 서비스를 사용하기 위한 메소드
        public Chat_Channel_Service getService() {
            return Chat_Channel_Service.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initService();
    }

    @Override
    public IBinder onBind(Intent intent) {
        mCtx = getApplicationContext();
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver); // 스트리밍 채팅방 생성 메시지를 받기 위한 브로드캐스트 해제
    }

    public void setCallbacks(ServiceCallbacks callbacks) {
        this.serviceCallbacks = callbacks;
    }

    private void initService() {
        registerReceiver(broadcastReceiver, new IntentFilter("CHANNEL_SERVICE"));
        new Thread(() -> (clientChannel = new Client_Channel()).start()).start();
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // 스트리밍 채팅방 생성을 위한 브로드캐스트 리시버 객체 생성
        @Override
        public void onReceive(Context context, Intent intent) {
            String request = intent.getStringExtra("request");
            String room_number = intent.getStringExtra("room_number");
            String room_name = intent.getStringExtra("room_name");
            JSONObject json = new JSONObject();
            json.put("request", request);
            json.put("user_id", SharedPreferences_M.getInstance(mCtx).getUser().getUser_nickname());
            json.put("room_number", room_number);
            json.put("room_name", room_name);
            String raw_data = json.toJSONString();
            Log.d(URLs.TAG, raw_data);
            sendMessage(raw_data);
        }
    };

    public class Client_Channel extends Thread { // OutputStream을 통하여 Byte 메시지를 받기 위한 소켓채널 생성
        Client_Channel() {
            try {
                socketChannel = SocketChannel.open();
                socketChannel.configureBlocking(true);
                socketChannel.connect(new InetSocketAddress(HOST, PORT));
                cos = socketChannel.socket().getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (true) {
                try {
                    ByteBuffer byteBuffer = ByteBuffer.allocate(512); // JVM의 힙 영역에 바이트 버퍼를 생성한다. 메서드의 인수는 버퍼 크기이다. allocate는 힙버퍼, allocateDirect는 다이렉트 버퍼라고 한다.
                    int readByteCount = socketChannel.read(byteBuffer); //데이터받기
                    if (readByteCount == -1) {
                        throw new IOException();
                    }
                    byteBuffer.flip();
                    // Bytebuffer의 flip메소드는 데이터를 기록한 position 값을 limit값으로 전환한다. 즉, 쓰기에서 읽기로 작업이 전환된다. 네티 책 166p 참조.
                    // 네티에서는 flip메소드 없이도 읽기, 쓰기가 동시에 가능하다. 클라이언트는 네티가 없기 때문에 flip 메소드를 써주어야 한다.
                    Charset charset = Charset.forName("UTF-8");
                    String object = charset.decode(byteBuffer).toString();
                    Log.d(URLs.TAG, "Raw메시지: "+object);
                    try {
                        JSONParser parser = new JSONParser();
                        JSONObject job_recv = (JSONObject) parser.parse(object);
                        Log.d(URLs.TAG, "json메시지: "+job_recv);
                        String response = String.valueOf(job_recv.get("response"));
                        String room_number = String.valueOf(job_recv.get("room_number"));
                        switch (response) { // JSON 기반 메시지를 통하여 채팅 방 생성, 제거, 참여, 메시지를 나누는 Switch문
                            case "create_room_ok":
                                serviceCallbacks.create_room_response_ok(SharedPreferences_M.getInstance(mCtx).getUser().getUser_uuid(), room_number);
                                break;
                            case "delete_room_ok":
                                serviceCallbacks.delete_room_response_ok();
                                break;
                            case "join_room_ok":
                                mCtx = getApplicationContext();
                                GlobalApplication.getGlobalApplicationContext().progressOFF();
                                Intent intent = new Intent(mCtx, Watching_Room.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("stream_key", URLs.STREAM_KEY);
                                intent.putExtra("room_number", room_number);
                                intent.putExtra("streaming_start_time", URLs.STREAMING_START_TIME);
                                startActivity(intent);
                                break;

                            case "send_ok":
                                serviceCallbacks.receive_msg(job_recv);
                                break;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    //클라이언트가 비정상적으로 종료됐을 경우
                    try {
                        socketChannel.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    public void sendMessage(final String data) { // 채널과 바인딩 된 액티비티에서 메시지를 보내기 위한 메소드
        new Thread(() -> {
            try {
                cos.write(data.getBytes("UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
