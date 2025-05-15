package com.example.pratica3mapa;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.time.Instant;

public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inclui o item "Relatório" na lista de opções
        String[] menu = new String[]{
                "Minha casa na cidade natal",
                "Minha casa em Viçosa",
                "Meu departamento",
                "Relatório",          // Novo item adicionado
                "Fechar aplicação"    // Agora é o índice 4
        };

        ArrayAdapter<String> arrayMenu = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, menu);
        setListAdapter(arrayMenu);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String pos = l.getItemAtPosition(position).toString();
        BancoDados banco = BancoDados.getInstance(this);

        if (position == 4) {
            finish();
        } else if (position == 3) {
            Intent intent = new Intent(this, Relatorio.class);
            startActivity(intent);
        } else {
            // Passo 2: Inserir log no banco

            // Obtém id da localização no banco
            int idLocation = banco.getIdLocationByDescricao(pos);

            if (idLocation != -1) {
                ContentValues valores = new ContentValues();
                valores.put("msg", pos);
                valores.put("timestamp", Instant.now().toString() + "");  // concatena com "" para virar String
                valores.put("idlocation", idLocation);

                banco.inserir("Log", valores);
            } else {
                Toast.makeText(this, "Erro: localização não encontrada no banco.", Toast.LENGTH_SHORT).show();
            }

            Intent it = new Intent(this, Mapa.class);
            Toast.makeText(this, pos, Toast.LENGTH_SHORT).show();
            it.putExtra("Local", position);
            startActivity(it);
        }
    }
}
