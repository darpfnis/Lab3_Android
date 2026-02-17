package com.example.lab3;

import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    private final ArrayList<Order> ordersList = new ArrayList<>();
    private OrderAdapter adapter;
    private final String FILE_NAME = "orders.json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);

        ListView listView = findViewById(R.id.listViewHistory);
        TextView emptyText = findViewById(R.id.textViewEmpty);
        Button buttonClear = findViewById(R.id.buttonClearHistory);

        loadOrders();

        adapter = new OrderAdapter(this, ordersList);
        listView.setAdapter(adapter);

        listView.setEmptyView(emptyText);

        updateClearButtonVisibility(buttonClear);

        if (buttonClear != null) {
            buttonClear.setOnClickListener(v -> {
                clearAll();
                updateClearButtonVisibility(buttonClear);
            });
        }
        Button buttonCreateNew = findViewById(R.id.buttonCreateNew);

        buttonCreateNew.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, MainActivity.class);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void loadOrders() {
        File file = new File(getFilesDir(), FILE_NAME);
        if (!file.exists()) return;

        try (FileInputStream fis = openFileInput(FILE_NAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);

            ArrayList<Order> loaded = new Gson().fromJson(sb.toString(),
                    new TypeToken<ArrayList<Order>>(){}.getType());

            if (loaded != null) {
                ordersList.clear();
                ordersList.addAll(loaded);
            }
        } catch (IOException e) {
            android.util.Log.e("HistoryActivity", "Error loading", e);
        }
    }

    private void updateClearButtonVisibility(Button btn) {
        if (btn == null) return;
        if (ordersList.size() > 1) {
            btn.setVisibility(android.view.View.VISIBLE);
        } else {
            btn.setVisibility(android.view.View.GONE);
        }
    }

    public void deleteOrder(String id) {
        ordersList.removeIf(o -> o.id.equals(id));
        saveOrders();
        adapter.notifyDataSetChanged();

        updateClearButtonVisibility(findViewById(R.id.buttonClearHistory));

        Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show();
    }

    public void editOrder(Order order) {
        android.content.Intent intent = new android.content.Intent(this, MainActivity.class);
        intent.putExtra("isEditing", true);
        intent.putExtra("orderId", order.id);
        intent.putExtra("flowerName", order.flowerName);
        intent.putExtra("customerName", order.customerName);
        intent.putExtra("email", order.email);
        intent.putExtra("notes", order.notes);
        intent.putExtra("colorId", order.colorId);
        intent.putExtra("priceId", order.priceId);

        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void saveOrders() {
        try (FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE)) {
            fos.write(new Gson().toJson(ordersList).getBytes());
        } catch (IOException e) {
            Log.e("HistoryActivity", "Error saving orders", e); // Robust logging
        }
    }

    private void clearAll() {
        ordersList.clear();
        saveOrders();
        adapter.notifyDataSetChanged();
    }
}