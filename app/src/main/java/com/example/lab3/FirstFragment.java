package com.example.lab3;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.util.ArrayList;

public class FirstFragment extends Fragment {

    private EditText editTextFlower, editTextCustomer, editTextEmail, editTextNotes;
    private RadioGroup colorGroup, priceGroup;
    private Button buttonOK;
    private final String FILE_NAME = "orders.json";
    private String editingOrderId = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        editTextFlower = view.findViewById(R.id.editTextFlower);
        editTextCustomer = view.findViewById(R.id.editTextCustomer);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextNotes = view.findViewById(R.id.editTextNotes);
        colorGroup = view.findViewById(R.id.radioGroupColor);
        priceGroup = view.findViewById(R.id.radioGroupPrice);
        buttonOK = view.findViewById(R.id.buttonOK);

        Button buttonOpenHistory = view.findViewById(R.id.buttonOpenHistory);

        buttonOK.setOnClickListener(v -> validateAndProceed(view));
        buttonOpenHistory.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(getActivity(), HistoryActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null && getActivity().getIntent() != null) {
            android.content.Intent intent = getActivity().getIntent();
            if (intent.getBooleanExtra("isEditing", false)) {
                // Відновлюємо ВСІ дані з Intent
                editingOrderId = intent.getStringExtra("orderId");
                editTextFlower.setText(intent.getStringExtra("flowerName"));
                editTextCustomer.setText(intent.getStringExtra("customerName"));
                editTextEmail.setText(intent.getStringExtra("email"));
                editTextNotes.setText(intent.getStringExtra("notes"));

                // Відновлюємо вибір у RadioGroups
                colorGroup.check(intent.getIntExtra("colorId", -1));
                priceGroup.check(intent.getIntExtra("priceId", -1));

                buttonOK.setText("Update Order");
                // Видаляємо прапорець, щоб при перезапуску форма була чистою
                intent.removeExtra("isEditing");
            }
        }
    }

    private void validateAndProceed(View view) {
        String flower = editTextFlower.getText().toString().trim();
        String customer = editTextCustomer.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String notes = editTextNotes.getText().toString().trim();

        if (TextUtils.isEmpty(flower) || TextUtils.isEmpty(customer) || TextUtils.isEmpty(email) ||
                colorGroup.getCheckedRadioButtonId() == -1 || priceGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "Please fill in all required fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Invalid email address");
            return;
        }

        RadioButton selectedColor = view.findViewById(colorGroup.getCheckedRadioButtonId());
        RadioButton selectedPrice = view.findViewById(priceGroup.getCheckedRadioButtonId());

        String result = "Flower: " + flower + "\n" +
                "Customer: " + customer + "\n" +
                "Color: " + selectedColor.getText() + "\n" +
                "Package: " + selectedPrice.getText() + "\n" +
                "Notes: " + (notes.isEmpty() ? "None" : notes);

        // ОНОВЛЕНО: Передаємо всі параметри в конструктор Order
        Order newOrder = new Order(flower, customer, email, notes,
                colorGroup.getCheckedRadioButtonId(),
                priceGroup.getCheckedRadioButtonId(), result);

        if (editingOrderId != null) {
            newOrder.id = editingOrderId;
        }

        saveOrderToJson(newOrder);
        clearFields();
        editingOrderId = null;
        buttonOK.setText("OK");

        SecondFragment fragment = SecondFragment.newInstance(result);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void saveOrderToJson(Order newOrder) {
        if (getContext() == null) return;
        Gson gson = new Gson();
        ArrayList<Order> ordersList = new ArrayList<>();

        try (FileInputStream fis = getContext().openFileInput(FILE_NAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);

            ArrayList<Order> existing = gson.fromJson(sb.toString(),
                    new TypeToken<ArrayList<Order>>(){}.getType());
            if (existing != null) ordersList.addAll(existing);
        } catch (IOException e) {
            Log.d("File", "Creating new JSON file");
        }

        boolean updated = false;
        if (editingOrderId != null) {
            for (int i = 0; i < ordersList.size(); i++) {
                if (ordersList.get(i).id.equals(editingOrderId)) {
                    ordersList.set(i, newOrder);
                    updated = true;
                    break;
                }
            }
        }

        if (!updated) {
            ordersList.add(newOrder);
        }

        try (FileOutputStream fos = getContext().openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
            fos.write(gson.toJson(ordersList).getBytes());
            Toast.makeText(getContext(), updated ? "Order updated!" : "Order added!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("Error", "Save failed", e);
        }
    }

    private void clearFields() {
        editTextFlower.setText("");
        editTextCustomer.setText("");
        editTextEmail.setText("");
        editTextNotes.setText("");
        colorGroup.clearCheck();
        priceGroup.clearCheck();
    }
}