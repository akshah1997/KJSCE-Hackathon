package com.akshay.kjsce_hackathon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.bassaer.chatmessageview.models.Message;
import com.github.bassaer.chatmessageview.models.User;
import com.github.bassaer.chatmessageview.views.ChatView;

public class MainActivity extends AppCompatActivity {

    ChatView chatView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chatView = (ChatView) findViewById(R.id.chat_view);

        // firstTest():
    }

    void firstTest() {
        //User id
        int myId = 0;
//User icon
        Bitmap myIcon = BitmapFactory.decodeResource(getResources(), R.drawable.face_2);
//User name
        String myName = "Akshay";

        int yourId = 1;
        Bitmap yourIcon = BitmapFactory.decodeResource(getResources(), R.drawable.face_1);
        String yourName = "Bot";

        final User me = new User(myId, myName, myIcon);
        final User you = new User(yourId, yourName, yourIcon);

        Message message1 = new Message.Builder()
                .setUser(me) // Sender
                .setRightMessage(true) // This message Will be shown right side.
                .setMessageText("Hello!")//Message contents
                .build();
        Message message2 = new Message.Builder()
                .setUser(you) // Sender
                .setRightMessage(false) // This message Will be shown left side.
                .setMessageText("What's up?") //Message contents
                .setPicture(yourIcon)
                .setType(Message.Type.PICTURE)
                .build();

        Message message3 = new Message.Builder()
                .setUser(you) // Sender
                .setRightMessage(false) // This message Will be shown left side.
                .setMessageText("What's up?") //Message contents
                .setType(Message.Type.LINK)
                .build();

        chatView.setOnClickSendButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new message
                Message message = new Message.Builder()
                        .setUser(me)
                        .setRightMessage(true)
                        .setMessageText(chatView.getInputText())
                        .hideIcon(true)
                        .build();
                //Set to chat view
                chatView.send(message);
                //Reset edit text
                chatView.setInputText("");
            }
        });
        chatView.send(message1);
        chatView.receive(message2);
        chatView.receive(message3);
        message1.hideIcon(true);
        message2.hideIcon(true);
    }
}
