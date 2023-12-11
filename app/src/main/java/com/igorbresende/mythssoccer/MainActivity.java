package com.igorbresende.mythssoccer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private EditText inputText;
    private Button sortButton;
    private TextView resultText;
    private Button shareButton;
    private final Handler handler = new Handler();
    private static final int DELAY_MILLIS = 150;
    private static final int NUMBEROFSORTS = 20;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = findViewById(R.id.inputText);
        sortButton = findViewById(R.id.sortButton);
        resultText = findViewById(R.id.resultText);
        shareButton = findViewById(R.id.shareButton);


        sortButton.setOnClickListener(v -> {
            hideKeyboard(v);


            for (int i = 0; i < NUMBEROFSORTS; i++) {
                final int finalIndex = i;
                handler.postDelayed(() -> {
                    displayFinalResult();
                }, i * DELAY_MILLIS);
            }

        });

        // Share button onClickListener
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareText();
            }
        });
    }

    private void populateList(String enteredText, Pattern pattern, List<String> list) {
        Matcher matcher = pattern.matcher(enteredText);
        while (matcher.find()) {
            String player = matcher.group(2);
            list.add(player);
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void displayIntermediateSortResults(int sortIndex) {
        String intermediateMessage = "Sorting... (" + sortIndex + "/20)";
        resultText.setText(intermediateMessage);
    }

    private void displayFinalResult() {
        String enteredText = inputText.getText().toString();

        String regex5 = "([0-9]+)(.*5️⃣( *|| ️*)⭐)";
        String regex4 = "([0-9]+)(.*4️⃣( *|| ️*)⭐)";
        String regex3 = "([0-9]+)(.*(3️⃣|3️⃣️)( *|| ️*)⭐)";
        String regex2 = "([0-9]+)(.*2️⃣( *|| ️*)⭐)";
        String regex1 = "([0-9]+)(.*1️⃣( *|| ️*)⭐)";
        String regexG = "([0-9]+)(.*G.M)";
        Pattern pattern5 = Pattern.compile(regex5);
        Pattern pattern4 = Pattern.compile(regex4);
        Pattern pattern3 = Pattern.compile(regex3);
        Pattern pattern2 = Pattern.compile(regex2);
        Pattern pattern1 = Pattern.compile(regex1);
        Pattern patternG = Pattern.compile(regexG);

        List<String> fiveStars = new ArrayList<>();
        List<String> fourStars = new ArrayList<>();
        List<String> threeStars = new ArrayList<>();
        List<String> twoStars = new ArrayList<>();
        List<String> oneStars = new ArrayList<>();
        List<String> goalKeepers = new ArrayList<>();

        populateList(enteredText, pattern5, fiveStars);
        populateList(enteredText, pattern4, fourStars);
        populateList(enteredText, pattern3, threeStars);
        populateList(enteredText, pattern2, twoStars);
        populateList(enteredText, pattern1, oneStars);
        populateList(enteredText, patternG, goalKeepers);

        if (fiveStars.size() > 2 && fourStars.size() > 2 && threeStars.size() > 2 && twoStars.size() > 2 && oneStars.size() > 2) {
            shareButton.setVisibility(View.VISIBLE);
            sortButton.setVisibility(View.INVISIBLE);
        } else {
            Toast.makeText(getApplicationContext(), "Algo deu errado,\nverifique o texto e tente novamente", Toast.LENGTH_SHORT).show();
        }

        int numberOfTeams = fiveStars.size();
        boolean willUseGK = numberOfTeams == goalKeepers.size();
        if (!willUseGK) {
            goalKeepers = new ArrayList<>();
        }
        Random random = new Random();

        List<List<String>> teams = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            List<String> team = new ArrayList<>();
            for (List<String> playerList : Arrays.asList(fiveStars, fourStars, threeStars, twoStars, oneStars, goalKeepers)) {
                if (!playerList.isEmpty()) {
                    team.add(playerList.remove(random.nextInt(playerList.size())));
                }
            }
            teams.add(team);
        }

        StringBuilder finalMessage = new StringBuilder();
        for (int i = 0; i < teams.size(); i++) {
            if (teams.get(i).size() > 0)
                finalMessage.append("\n----- Time ").append(i + 1).append(" -----\n");
            for (String player : teams.get(i)) {
                finalMessage.append(player).append("\n");
            }
        }

        resultText.setText(finalMessage.toString());
    }


    private void shareText() {
        String textToShare = resultText.getText().toString();

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
        shareIntent.setType("text/plain");

        // Check if WhatsApp is installed
        if (isWhatsAppInstalled()) {
            shareIntent.setPackage("com.whatsapp");
        }

        startActivity(Intent.createChooser(shareIntent, "Share using"));
    }

    private boolean isWhatsAppInstalled() {
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");

        return getPackageManager().resolveActivity(whatsappIntent, 0) != null;
    }

}
