package com.example.pratica3mapa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BancoDados {

    private static BancoDados instance;
    private SQLiteDatabase db;
    private static final String NOME_BANCO = "localizacao_log_db";

    private final String[] SCRIPT_DATABASE_CREATE = {
            "CREATE TABLE IF NOT EXISTS Localizacao (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "descricao TEXT NOT NULL, " +
                    "latitude REAL NOT NULL, " +
                    "longitude REAL NOT NULL);",

            "CREATE TABLE IF NOT EXISTS Log (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "msg TEXT NOT NULL, " +
                    "timestamp TEXT NOT NULL, " +
                    "idlocation INTEGER NOT NULL, " +
                    "FOREIGN KEY (idlocation) REFERENCES Localizacao(id));"
    };

    private BancoDados(Context context) {
        db = context.openOrCreateDatabase(NOME_BANCO, Context.MODE_PRIVATE, null);
        // Cria as tabelas caso não existam
        for (String script : SCRIPT_DATABASE_CREATE) {
            db.execSQL(script);
        }
        Log.i("BANCO_DADOS", "Banco criado ou aberto com sucesso.");

        // Popula a tabela Localizacao se estiver vazia
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Localizacao", null);
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            if (count == 0) {
                popularLocalizacoes();
            }
        }
        cursor.close();
    }

    // Singleton: retorna a instância única do banco
    public static synchronized BancoDados getInstance(Context context) {
        if (instance == null) {
            instance = new BancoDados(context.getApplicationContext());
        }
        return instance;
    }

    // Popula a tabela Localizacao com os três locais fixos
    private void popularLocalizacoes() {
        inserirLocalizacao("Cidade Natal", -19.4333, -42.6300); // Alvinópolis
        inserirLocalizacao("Viçosa", -20.7550, -42.8743);
        inserirLocalizacao("DPI/UFV", -20.7611, -42.8703);
        Log.i("BANCO_DADOS", "Tabela Localizacao populada com dados iniciais.");
    }

    private long inserirLocalizacao(String descricao, double latitude, double longitude) {
        ContentValues valores = new ContentValues();
        valores.put("descricao", descricao);
        valores.put("latitude", latitude);
        valores.put("longitude", longitude);
        long id = db.insert("Localizacao", null, valores);
        Log.i("BANCO_DADOS", "Inserida localizacao " + descricao + " com id: " + id);
        return id;
    }

    // Insere um log na tabela Log
    public long inserirLog(String msg, int idLocation) {
        ContentValues valores = new ContentValues();
        valores.put("msg", msg);

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
        valores.put("timestamp", timestamp);

        valores.put("idlocation", idLocation);

        long id = db.insert("Log", null, valores);
        Log.i("BANCO_DADOS", "Inserido log com id: " + id);
        return id;
    }

    // Busca id da localização pela descricao
    public int buscarIdLocationPorDescricao(String descricao) {
        int id = -1;
        Cursor c = null;
        try {
            c = db.query("Localizacao", new String[]{"id"}, "descricao = ?", new String[]{descricao}, null, null, null);
            if (c.moveToFirst()) {
                id = c.getInt(c.getColumnIndexOrThrow("id"));
            }
        } finally {
            if (c != null) c.close();
        }
        return id;
    }
    public int getIdLocationByDescricao(String descricao) {
        int idLocation = -1;
        Cursor c = db.rawQuery("SELECT id FROM Localizacao WHERE descricao = ?", new String[]{descricao});
        if (c != null) {
            if (c.moveToFirst()) {
                idLocation = c.getInt(c.getColumnIndexOrThrow("id"));
            }
            c.close();
        }
        return idLocation;
    }

    // Insere registro genérico
    public long inserir(String tabela, ContentValues valores) {
        long id = db.insert(tabela, null, valores);
        Log.i("BANCO_DADOS", "Inserido registro com id: " + id);
        return id;
    }

    // Atualiza registro genérico
    public int atualizar(String tabela, ContentValues valores, String where, String[] whereArgs) {
        int count = db.update(tabela, valores, where, whereArgs);
        Log.i("BANCO_DADOS", "Atualizados " + count + " registros.");
        return count;
    }

    // Deleta registro genérico
    public int deletar(String tabela, String where, String[] whereArgs) {
        int count = db.delete(tabela, where, whereArgs);
        Log.i("BANCO_DADOS", "Deletados " + count + " registros.");
        return count;
    }

    // Busca dados genérico
    public Cursor buscar(String tabela, String[] colunas, String where, String[] whereArgs, String orderBy) {
        Cursor c = db.query(tabela, colunas, where, whereArgs, null, null, orderBy);
        Log.i("BANCO_DADOS", "Busca retornou " + c.getCount() + " registros.");
        return c;
    }
    public SQLiteDatabase getDb() {
        return db;
    }


    // Fecha o banco de dados
    public void fechar() {
        if (db != null && db.isOpen()) {
            db.close();
            Log.i("BANCO_DADOS", "Banco fechado.");
        }
    }
}
