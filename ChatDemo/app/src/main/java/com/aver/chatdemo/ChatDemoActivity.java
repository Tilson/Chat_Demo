package com.aver.chatdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ChatDemoActivity extends Activity {

    private static Handler mHandler = new Handler();
    private Thread mThread;
    private Thread commThread;
    private Socket clientSocket;
    private String tmp;
    private TextView chatMsgView;
    private EditText chatUserView;
    private EditText chatEditView;
    private Button snedBtn;

    private String SERVER_IP = "192.168.43.95";
    int serverPort = 3669;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_demo);

        chatMsgView = findViewById(R.id.msgListView);
        chatUserView = findViewById(R.id.user);
        chatEditView = findViewById(R.id.msg);
        snedBtn = findViewById(R.id.send);

        snedBtn.setOnClickListener(sendBtnLink);

        mThread = new Thread(readData);
        mThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
        if (commThread != null) {
            commThread.interrupt();
            commThread = null;
        }
    }

    private Button.OnClickListener sendBtnLink = new Button.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (clientSocket ==  null) return;
            if (clientSocket.isConnected()) {
                if (commThread != null) {
                    commThread.interrupt();
                    commThread = null;
                }
                commThread = new Thread(multiThread);
                commThread.start();
            }
        }
    };

    private Runnable multiThread = new Runnable() {
        public void run() {
            BufferedWriter mBufWriter;
            try {
                mBufWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                mBufWriter.write(chatUserView.getText() + ": " + chatEditView.getText() + "\n");
                mBufWriter.flush();
            } catch (IOException e) {
                e.getStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chatEditView.setText("");
                }
            });
        }
    };

    private Runnable readData = new Runnable() {
        public void run() {
            InetAddress serverIp;
            try {
                serverIp = InetAddress.getByName(SERVER_IP);
                clientSocket = new Socket(serverIp, serverPort);

                BufferedReader mBuffer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                while (clientSocket.isConnected()) {
                    tmp = mBuffer.readLine();
                    if (tmp != null) {
                        mHandler.post(updateMsg);
                    }
                }
            } catch (IOException e) {
                e.getStackTrace();
            }
        }
    };

    private Runnable updateMsg = new Runnable() {
        public void run() {
            chatMsgView.append(tmp + "\n");
        }
    };
}
