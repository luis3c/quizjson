package com.api.trivia.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.api.trivia.R;
import com.api.trivia.models.Result;
import com.api.trivia.models.Results;
import com.api.trivia.services.ApiUtils;
import com.api.trivia.services.TriviaService;
import com.api.trivia.utils.Constants;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeActivity extends Activity {

    private LinearLayout contentQuestions;
    private Button sendButton;
    private HashMap<Integer,String> form;
    private TriviaService service;
    private AlertDialog alert;
    private static final String TAG = HomeActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        service = ApiUtils.getUserAPI();
        form = new HashMap<>();
        initViews();
    }

    private void initViews() {
        contentQuestions = findViewById(R.id.content_questions);
        sendButton = findViewById(R.id.send_button);
        alert = new SpotsDialog.Builder().setContext(this).build();
        initEventsViews();
    }

    private void initEventsViews(){
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
        getQuestions();
    }

    private void sendEmail(){
        alert.show();
        String answers = "";
        for(Map.Entry m:form.entrySet()){
            answers += m.getValue();
        }
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        //levy@gmail.com"
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"gerard95@live.com.mx"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Respuestas del quiz");
        intent.putExtra(Intent.EXTRA_TEXT, answers);
        if (intent.resolveActivity(getPackageManager()) != null) {
            form.clear();
            startActivity(intent);
        }
        Toast.makeText(getApplicationContext(), "Correo redactado", Toast.LENGTH_LONG).show();
        alert.dismiss();
    }

    private void getQuestions() {
        alert.show();
        service.getSaveGames(Constants.BASE_AMOUNT,Constants.BASE_CATEGORY,Constants.BASE_DIFFICULTY, Constants.BASE_TYPE).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                createViewQuestions(response.body().getResults());
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.e(TAG,t.getMessage(),t);
                alert.dismiss();
            }
        });
    }

    private void getQuestions(final int amount, final String category, final String difficulty,final String type) {
        alert.show();
        service.getSaveGames(amount,category,difficulty,type).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                createViewQuestions(response.body().getResults());
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.e(TAG,t.getMessage(),t);
                alert.dismiss();
            }
        });
    }

    private void createViewQuestions(Results[] questions) {

        int index = 1;
        for (Results question: questions) {
            TextView title = new TextView(this);
            title.setText(String.format("%s- %s",index,question.getQuestion()));
            title.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            RadioGroup radioGroup=new RadioGroup(this);
            radioGroup.setId(index);
            radioGroup.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            contentQuestions.addView(radioGroup);
            radioGroup.addView(title);
            int subIndex = 10;
            for (String answer:question.getIncorrect_answers()) {
                RadioButton radioButton=new RadioButton(this);
                radioButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                radioButton.setText(answer);
                radioButton.setId(index*subIndex);
                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RadioGroup r = (RadioGroup)view.getParent();
                        TextView t = (TextView) r.getChildAt(0);
                        RadioButton b = (RadioButton) view;
                        form.put(r.getId(),String.format("Question ==> %s Answer ==> %s \n",t.getText(),b.getText()));
                        Log.e(TAG,"Question==>"+t.getText());
                        Log.e(TAG,"answer==>"+b.getText());
                        for(Map.Entry m:form.entrySet()){
                            Log.e(TAG,m.getKey()+" "+m.getValue());
                        }
                    }
                });
                radioGroup.addView(radioButton);
                subIndex ++;
            }
            RadioButton radioButton=new RadioButton(this);
            radioButton.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            radioButton.setText(question.getCorrect_answer());
            radioButton.setId(index*subIndex);
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RadioGroup r = (RadioGroup)view.getParent();
                    TextView t = (TextView) r.getChildAt(0);
                    RadioButton b = (RadioButton) view;
                    form.put(r.getId(),String.format("Question ==> %s Answer ==> %s \n",t.getText(),b.getText()));
                    Log.e(TAG,"Question==>"+t.getText());
                    Log.e(TAG,"answer==>"+b.getText());
                    for(Map.Entry m:form.entrySet()){
                        Log.e(TAG,m.getKey()+" "+m.getValue());
                    }
                }
            });
            radioGroup.addView(radioButton);
            index ++;
        }
        alert.dismiss();
    }

}
