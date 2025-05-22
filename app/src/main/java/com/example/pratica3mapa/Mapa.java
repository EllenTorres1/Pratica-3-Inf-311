package com.example.pratica3mapa;
import android.database.Cursor;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Mapa extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private Marker marcadorAtual;
    private FusedLocationProviderClient fusedLocationClient;

    private BancoDados bancoDados;

    private String localSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        bancoDados = BancoDados.getInstance(this);

        Intent intent = getIntent();
        localSelecionado = intent.getStringExtra("local");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        findViewById(R.id.buttonLocAtual).setOnClickListener(this::onClick_MarcarLocalizacaoAtual);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        centralizarLocalInicial(localSelecionado);
    }

    private void centralizarLocalInicial(String local) {
        if (local == null) return;

        switch (local) {
            case "alvinopolis":
                centralizarNoLocal("Cidade Natal");
                gravarLog("Cidade Natal");
                Toast.makeText(this, "Local selecionado: Alvinópolis", Toast.LENGTH_SHORT).show();
                break;
            case "vicosa":
                centralizarNoLocal("Viçosa");
                gravarLog("Viçosa");
                Toast.makeText(this, "Local selecionado: Viçosa", Toast.LENGTH_SHORT).show();
                break;
            case "dpi":
                centralizarNoLocal("DPI/UFV");
                gravarLog("DPI/UFV");
                Toast.makeText(this, "Local selecionado: DPI/UFV", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }


    private void centralizarNoLocal(String descricaoLocal) {
        String[] colunas = {"latitude", "longitude"};
        Cursor c = bancoDados.buscar("Localizacao", colunas, "descricao = ?", new String[]{descricaoLocal}, null);
        if (c.moveToFirst()) {
            double lat = c.getDouble(c.getColumnIndexOrThrow("latitude"));
            double lng = c.getDouble(c.getColumnIndexOrThrow("longitude"));
            LatLng pos = new LatLng(lat, lng);
            if (map != null) {
                map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 16));
            }
        }
        c.close();
    }


    private void gravarLog(String descricaoLocal) {
        int idLocation = bancoDados.buscarIdLocationPorDescricao(descricaoLocal);
        if (idLocation != -1) {
            bancoDados.inserirLog(descricaoLocal, idLocation);
        } else {
            Toast.makeText(this, "Erro: local não encontrado no banco para log", Toast.LENGTH_SHORT).show();
        }
    }


    public void onClick_Alvinopolis(View view) {
        centralizarNoLocal("Cidade Natal");
        gravarLog("Cidade Natal");
    }

    public void onClick_Vicosa(View view) {
        centralizarNoLocal("Viçosa");
        gravarLog("Viçosa");
    }

    public void onClick_DPI(View view) {
        centralizarNoLocal("DPI/UFV");
        gravarLog("DPI/UFV");
    }

    public void onClick_MarcarLocalizacaoAtual(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permissão de localização não concedida", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null && map != null) {
                LatLng posicaoAtual = new LatLng(location.getLatitude(), location.getLongitude());

                if (marcadorAtual != null) {
                    marcadorAtual.remove();
                }

                marcadorAtual = map.addMarker(new MarkerOptions()
                        .position(posicaoAtual)
                        .title("Minha localização atual")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

                map.animateCamera(CameraUpdateFactory.newLatLngZoom(posicaoAtual, 17));


                Cursor c = bancoDados.buscar("Localizacao", new String[]{"latitude", "longitude"}, "descricao = ?", new String[]{"Viçosa"}, null);
                if (c.moveToFirst()) {
                    double latVi = c.getDouble(c.getColumnIndexOrThrow("latitude"));
                    double lngVi = c.getDouble(c.getColumnIndexOrThrow("longitude"));

                    float[] resultado = new float[1];
                    Location.distanceBetween(posicaoAtual.latitude, posicaoAtual.longitude,
                            latVi, lngVi, resultado);
                    int distancia = (int) resultado[0];
                    Toast.makeText(this, "Você está a " + distancia + " metros de Viçosa.", Toast.LENGTH_LONG).show();
                }
                c.close();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                map.setMyLocationEnabled(true);
            }
        } else {
            Toast.makeText(this, "Permissão de localização negada", Toast.LENGTH_SHORT).show();
        }
    }
}
