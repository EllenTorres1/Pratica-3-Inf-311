package com.example.pratica3mapa;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Relatorio extends ListActivity {

    private BancoDados banco;
    private ArrayList<double[]> coords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        banco = BancoDados.getInstance(this);

        String query = "SELECT Log.id, Log.msg, Log.timestamp, Localizacao.latitude, Localizacao.longitude " +
                "FROM Log INNER JOIN Localizacao ON Log.idlocation = Localizacao.id";

        Cursor cursor = banco.getDb().rawQuery(query, null);

        ArrayList<String> listaLogs = new ArrayList<>();
        coords = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String msg = cursor.getString(cursor.getColumnIndexOrThrow("msg"));
                String timestamp = cursor.getString(cursor.getColumnIndexOrThrow("timestamp"));
                double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("latitude"));
                double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"));

                listaLogs.add(msg + " - " + timestamp);
                coords.add(new double[]{latitude, longitude});
            } while (cursor.moveToNext());
            cursor.close();
        }

        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaLogs));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        double[] latlong = coords.get(position);
        String mensagem = "Latitude: " + latlong[0] + ", Longitude: " + latlong[1];

        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show();
    }
}
