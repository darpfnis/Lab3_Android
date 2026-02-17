package com.example.lab3;

import android.content.Context;
import java.io.*;

public class FileHelper {
    private static final String FILE_NAME = "flower_orders.txt";

    public static void saveToFile(Context context, String data) throws IOException {
        // MODE_APPEND дозволяє дописувати нові замовлення в кінець файлу [cite: 357]
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_APPEND)) {
            fos.write((data + "\n---\n").getBytes());
        }
    }

    public static String readFromFile(Context context) throws IOException {
        FileInputStream fis = context.openFileInput(FILE_NAME);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }
}