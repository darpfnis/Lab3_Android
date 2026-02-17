package com.example.lab3;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);

        TextView historyContent = findViewById(R.id.textViewHistoryContent);
        Button btnClear = findViewById(R.id.buttonClearHistory);

        loadHistory(historyContent);

        btnClear.setOnClickListener(v -> {
            try {
                FileOutputStream fos = openFileOutput("orders.txt", MODE_PRIVATE);
                fos.close();

                historyContent.setText("History cleared.");
                Toast.makeText(this, "All records deleted!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Error clearing history", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadHistory(TextView textView) {
        try {
            FileInputStream fis = openFileInput("orders.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

            if (sb.length() > 0) {
                textView.setText(sb.toString());
            } else {
                textView.setText("No orders");
            }
            br.close();
        } catch (Exception e) {
            textView.setText("Error. File hasn't been created");
        }
    }
}