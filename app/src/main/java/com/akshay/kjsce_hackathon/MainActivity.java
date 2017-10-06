package com.akshay.kjsce_hackathon;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.bassaer.chatmessageview.models.Message;
import com.github.bassaer.chatmessageview.models.User;
import com.github.bassaer.chatmessageview.views.ChatView;
import com.google.gson.JsonElement;

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
    User me, you;
    private boolean semaphore = true;

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
                        AndroidUtils.wolfReq(msg);
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
                                                                  AndroidUtils.wolfReq(msg);
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
                            .setMessageText("Query:" + result.getResolvedQuery() +
                                    "\nAction: " + result.getAction() +
                                    "\nParameters: " + parameterString + "\nResolvedQuery: " + result.getResolvedQuery()
                                    + "\nResult: " + result.getFulfillment().getSpeech())
                            .hideIcon(true)
                            .build();
                    //Set to chat view
                    chatView.send(message);
                }
            }
        }.execute(aiRequest);
    }

    @Override
    public void onResult(ai.api.model.AIResponse response) {
        Result result = response.getResult();

        // Get parameters
        String parameterString = "";
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
        }

        Message msg = new Message.Builder()
                .setUser(me)
                .setRightMessage(true)
                .setMessageText(result.getResolvedQuery())
                .hideIcon(true)
                .build();
        //Set to chat view
        chatView.send(msg);
        //Reset edit text
        chatView.setInputText("");
        msg.setIconVisibility(false);

        Message message = new Message.Builder()
                .setUser(you)
                .setRightMessage(false)
                .setMessageText("Query:" + result.getResolvedQuery() +
                        "\nAction: " + result.getAction() +
                        "\nParameters: " + parameterString + "\nResolvedQuery: " + result.getResolvedQuery()
                        + "\nResult: " + result.getFulfillment().getSpeech())
                .hideIcon(true)
                .build();
        //Set to chat view
        chatView.send(message);
        message.setIconVisibility(false);
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

}
