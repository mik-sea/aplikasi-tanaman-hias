package com.kelompok1.aplikasitanamanhias.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.Toast;

import com.kelompok1.aplikasitanamanhias.adapter.BungaAdapter;
import com.kelompok1.tanamanhias.R;
import com.kelompok1.aplikasitanamanhias.model.ModelMain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class KategoriBunga extends AppCompatActivity {

    List<ModelMain> modelMain = new ArrayList<>();
    BungaAdapter bungaAdapter;
    RecyclerView rvListBunga;
    SearchView searchTanaman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kategori_bunga);

        //set transparent statusbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        rvListBunga = findViewById(R.id.rvListBunga);
        searchTanaman = findViewById(R.id.searchTanaman);

        //transparent background searchview
        int searchPlateId = searchTanaman.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchTanaman.findViewById(searchPlateId);
        if (searchPlate != null) {
            searchPlate.setBackgroundColor(Color.TRANSPARENT);
        }

        searchTanaman.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchTanaman.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                bungaAdapter.getFilter().filter(newText);
                return true;
            }
        });

        rvListBunga.setLayoutManager(new LinearLayoutManager(this));
        rvListBunga.setHasFixedSize(true);

        //get data json
        getNamaTanaman();

    }

    private void getNamaTanaman() {
        try {
            InputStream stream = getAssets().open("tanaman_hiasbunga.json");
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            String strContent = new String(buffer, StandardCharsets.UTF_8);
            try {
                JSONObject jsonObject = new JSONObject(strContent);
                JSONArray jsonArray = jsonObject.getJSONArray("daftar_tanaman");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    ModelMain dataApi = new ModelMain();
                    dataApi.setNama(object.getString("nama"));
                    dataApi.setDeskripsi(object.getString("deskripsi"));
                    dataApi.setBudidaya(object.getString("budidaya"));
                    dataApi.setImage(object.getString("image_url"));
                    modelMain.add(dataApi);
                }
                bungaAdapter = new BungaAdapter(this, modelMain);
                rvListBunga.setAdapter(bungaAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException ignored) {
            Toast.makeText(KategoriBunga.this, "Ups, ada yang tidak beres. " +
                    "Coba ulangi beberapa saat lagi.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window window = activity.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        if (on) {
            layoutParams.flags |= bits;
        } else {
            layoutParams.flags &= ~bits;
        }
        window.setAttributes(layoutParams);
    }

}