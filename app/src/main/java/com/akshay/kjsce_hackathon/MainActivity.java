package com.akshay.kjsce_hackathon;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.bassaer.chatmessageview.models.Message;
import com.github.bassaer.chatmessageview.models.User;
import com.github.bassaer.chatmessageview.views.ChatView;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class MainActivity extends AppCompatActivity implements AIListener {

    private SpeechRecognizer speechRecognizer;
    ChatView chatView;
    private AIService aiService;
    private AIDataService aiDataService;
    private static final String CLIENT_ACCESS_TOKEN = "7fb6a25c74834dd3afa68ad0c6146f99";
    static User me, you;
    private boolean semaphore = true;
    Bitmap bmp;
    PendingIntent pendingIntent;
    AlarmManager am;
    String givenDateString = "Sat Oct 6 14:57:00 GMT+05:30 2017";
    String to = "shubham.dalvi97@gmail.com";
    String subject = "KJSCEHackathon";
    String messageEmail = "Hello World";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                getApplicationContext().getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,0);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                String msg = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
                Message message = new Message.Builder()
                        .setUser(me)
                        .setRightMessage(true)
                        .setMessageText(msg)
                        .hideIcon(true)
                        .build();
                //Set to chat view
                chatView.send(message);
                if (msg.contains("what") || msg.contains("how") || msg.contains("when") || msg.contains("where") || msg.contains("who")) {
                    try {
                        wolfReq(msg);
                        Log.d("result", msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    executeAI(msg);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
        // setAlarm(givenDateString);
        // sendMail(to,subject,message);
        // playSong("Tutorials for android");

        chatView = (ChatView) findViewById(R.id.chat_view);
        final AIConfiguration config = new AIConfiguration(CLIENT_ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);
        aiDataService = new AIDataService(config);

        int myId = 0;
//User icon
        Bitmap myIcon = BitmapFactory.decodeResource(getResources(), R.drawable.face_2);
//User name
        String myName = "Akshay";

        int yourId = 1;
        Bitmap yourIcon = BitmapFactory.decodeResource(getResources(), R.drawable.face_1);
        String yourName = "Bot";

        me = new User(myId, myName, myIcon);
        you = new User(yourId, yourName, yourIcon);

        /*chatView.setBackgroundColor(Color.BLUE);
        chatView.setEnableSwipeRefresh(true);*/
        chatView.setOptionIcon(R.drawable.ic_action_mic);
        chatView.setSendTimeTextColor(Color.WHITE);
        chatView.setOnClickOptionButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (semaphore == true) {
                    speechRecognizer.startListening(intent);
                    Log.d("a","Recognition Started");
                    semaphore = false;
                } else {
                    aiService.stopListening();
                    Log.d("a","Recognition Stopped");
                    //TODO: remove semaphores
                    semaphore = true;
                }
            }
        });

        chatView.setOnClickSendButtonListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View view) {
                                                      if (TextUtils.isEmpty(chatView.getInputText())) {
                                                          Toast.makeText(getApplicationContext(), "Enter input", Toast.LENGTH_SHORT).show();
                                                      } else {
                                                          //new message
                                                          Message message = new Message.Builder()
                                                                  .setUser(me)
                                                                  .setRightMessage(true)
                                                                  .setMessageText(chatView.getInputText())
                                                                  .hideIcon(true)
                                                                  .build();
                                                          //Set to chat view
                                                          chatView.send(message);
                                                          String msg = chatView.getInputText();
                                                          if (msg.contains("what") || msg.contains("how") || msg.contains("when") || msg.contains("where") || msg.contains("who")) {
                                                              try {
                                                                  wolfReq(msg);
                                                              } catch (Exception e) {
                                                                  e.printStackTrace();
                                                              }
                                                          } else {
                                                              executeAI(chatView.getInputText());
                                                          }
                                                      }

                                                      //Reset edit text
                                                      chatView.setInputText("");
                                                  }
                                              });
    }

    void executeAI(String message) {
        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery(message);
        //aiService.textRequest(aiRequest);
        //final AIResponse aiResponse = aiDataService.request(aiRequest);

        new AsyncTask<AIRequest, Void, AIResponse>() {
            @Override
            protected AIResponse doInBackground(AIRequest... requests) {
                final AIRequest request = requests[0];
                try {
                    final AIResponse response = aiDataService.request(aiRequest);
                    return response;
                } catch (AIServiceException e) {
                }
                return null;
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            protected void onPostExecute(AIResponse response) {
                if (response != null) {
                    // process aiResponse here
                    Result result = response.getResult();

                    // Get parameters
                    String parameterString = "";
                    if (result.getParameters() != null && !result.getParameters().isEmpty()) {
                        for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                            parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
                        }
                    }
                    Message message = new Message.Builder()
                            .setUser(you)
                            .setRightMessage(false)
                            /*.setMessageText("Query:" + result.getResolvedQuery() +
                                    "\nAction: " + result.getAction() +
                                    "\nParameters: " + parameterString + "\nResolvedQuery: " + result.getResolvedQuery()
                                    + "\nResult: " + result.getFulfillment().getSpeech())*/
                            .setMessageText(result.getFulfillment().getSpeech())
                            .hideIcon(true)
                            .build();
                    //Set to chat view
                    chatView.send(message);
                    executeCorrespondingTask(result);
                }
            }
        }.execute(aiRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void executeCorrespondingTask(Result result) {
        if (result.getFulfillment().getSpeech().equals("Sending an Email")) {
            HashMap<String, JsonElement> resultParameters = result.getParameters();
            for (Map.Entry<String, JsonElement> entry : resultParameters.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                if (key.equals("email")) {
                    to = value.toString();
                }
                if (key.equals("message")) {
                    messageEmail = value.toString();
                }
                if (key.equals("subject")) {
                    subject = value.toString();
                }
            }
            sendMail(to, subject, messageEmail);
        }

        if (result.getFulfillment().getSpeech().contains("Setting alarm for")) {
            HashMap<String, JsonElement> resultParameters = result.getParameters();
            String date="\"abc\"", time="\"abc\"";
            for (Map.Entry<String, JsonElement> entry : resultParameters.entrySet()) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                if (key.equals("date")) {
                    date = value.toString();
                }
                if (key.equals("time")) {
                    time = value.toString();
                }
            }
            Log.d("testing0",time+date);
            Log.d("ER","heloo");
            time=time.replaceAll("^\"|\"$","");

            date=date.replaceAll("^\"|\"$","");
            Log.d("Testing1",time);
            Log.d("Testing2",date);
            Log.d("Testing3",time+" "+date);
            setAlarm(time+" "+date);

        }
        /*if (result.getFulfillment().getSpeech().equals("tomail")) {
            to = result.getFulfillment().getSpeech();
        }
        if (result.getFulfillment().getSpeech().equals("subjectemail")) {
            subject = result.getFulfillment().getSpeech();
        }
        if (result.getFulfillment().getSpeech().equals("messageemail")) {
            messageEmail = result.getFulfillment().getSpeech();
            sendMail(to, subject, messageEmail);
        }*/
    }

    @Override
    public void onResult(ai.api.model.AIResponse response) {

    }

    @Override
    public void onError(ai.api.model.AIError error) {
        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setAlarm(String givenDateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
        Date mDate = null;
        try {
            mDate = sdf.parse(givenDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                12345, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am = (AlarmManager) getSystemService(Activity.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, mDate.getTime(),
                pendingIntent);

    }

    public void sendMail(String to, String subject, String message) {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, to);
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, message);
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Select Email client"));
    }

    public void playSong(String youtubeLink) {
        Intent intent = new Intent(Intent.ACTION_SEARCH);
        intent.setPackage("com.google.android.youtube");
        intent.putExtra("query", youtubeLink);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void wolfReq(String s)throws Exception
    {
        String appid="JHG2AQ-KEUPARWGKH";
        HttpTransport httpTransport = new NetHttpTransport();
        HttpRequestFactory requestFactory =
                httpTransport.createRequestFactory();
        GenericUrl url1 = new
                GenericUrl("http://api.wolframalpha.com/v2/query");
        url1.put("input",s);
        url1.put("output","JSON");
        url1.put("appid",appid);
        final HttpRequest request = requestFactory.buildGetRequest(url1);

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                HttpResponse httpResponse = null;

                try {
                    httpResponse = request.execute();
                    InputStream inputStream = httpResponse.getContent();
                    int ch;
                    String result = "";
                    while ((ch = inputStream.read()) != -1) {
                        result+=(char)ch;
                    }

                    httpResponse.disconnect();
                    JSONObject data = new JSONObject(result);
                    result = data.getJSONObject("queryresult").getJSONArray("pods").getJSONObject(1).getJSONArray("subpods").getJSONObject(0).getJSONObject("img").getString ("src");
                    URL urlConnection = new URL(result);
                    HttpURLConnection connection =
                            (HttpURLConnection) urlConnection
                                    .openConnection();
                    connection.setDoInput(true);
                    connection.setConnectTimeout(20000);
                    connection.connect();
                    InputStream input =
                            connection.getInputStream();
                    Bitmap myBitmap =
                            BitmapFactory.decodeStream(input);
                    bmp=myBitmap;
                    connection.disconnect();
                    Log.d("result",bmp.toString());

                    final Message message2 = new Message.Builder()
                            .setUser(you)
                            .setRightMessage(false)
                            .setPicture(myBitmap)
                            .hideIcon(true)
                            .setType(Message.Type.PICTURE)
                            .build();
                    //Set to chat view
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatView.send(message2);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

}
