package com.example.lab3;

import android.content.Context;
import android.view.*;
import android.widget.*;
import java.util.ArrayList;

public class OrderAdapter extends ArrayAdapter<Order> {
    private final HistoryActivity activity;
    private final ArrayList<Order> orders;

    public OrderAdapter(HistoryActivity activity, ArrayList<Order> orders) {
        super(activity, R.layout.item_order, orders);
        this.activity = activity;
        this.orders = orders;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_order, parent, false);
        }

        Order order = getItem(position);
        TextView info = convertView.findViewById(R.id.textViewOrderInfo);
        Button btnEdit = convertView.findViewById(R.id.buttonEdit);
        Button btnDelete = convertView.findViewById(R.id.buttonDelete);

        info.setText(order.flowerName + "\n" + order.id.substring(0, 8)); // Показуємо назву та частину ID

        btnDelete.setOnClickListener(v -> activity.deleteOrder(order.id));
        btnEdit.setOnClickListener(v -> activity.editOrder(order));

        return convertView;
    }
}