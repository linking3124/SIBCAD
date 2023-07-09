package topapp.id.dpac;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import toppapp.id.dpac.R;

/**
 * Created by topapp.id on 11/02/18.
 */

public class DaftarKegiatanProvinsiActivity extends AppCompatActivity {

    private Button btTerima, btTolak;
    private ProgressDialog pDialog;
    public static final String TAG_ID = "id_calon";
    public static final String TAG_KATEGORI= "kategori";
    SharedPreferences sharedPreferences;
    String id_calon, kategori, id_kegiatan, status;

    ArrayList<Kegiatan> arrayList;
    ListView lv;

    //get server connection
    Koneksi koneksi = new Koneksi();

    String service_api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_kegiatan_provinsi);
        arrayList=  new ArrayList<>();
        lv = (ListView) findViewById(R.id.listView);

        // get session
        sharedPreferences = getSharedPreferences(LoginActivity.my_shared_preferences, Context.MODE_PRIVATE);
        id_calon = getIntent().getStringExtra(TAG_ID);
        kategori = getIntent().getStringExtra(TAG_KATEGORI);

        if (kategori.equals("pengawas")) {
            service_api = koneksi.getUrl() + "lihat_kegiatan_provinsi.php";
        } else if (kategori.equals("office boy")){
            service_api = koneksi.getUrl() + "lihat_kegiatan_kabkota.php";
        } else {
            Toast.makeText(getApplicationContext(), "Undefined kategori (" +kategori+ ")", Toast.LENGTH_SHORT).show();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new ReadJSON().execute(service_api+"?id_calon="+id_calon);
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    class ReadJSON extends AsyncTask<String, Integer, String>{

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(DaftarKegiatanProvinsiActivity.this);
            pDialog.setMessage("Mohon menunggu, sedang mengambil data..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return readURL(params[0]);
        }

        @Override
        protected void onPostExecute(String content) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray jsonArray = jsonObject.getJSONArray("results");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject kegiatanObject = jsonArray.getJSONObject(i);
                    arrayList.add(new Kegiatan(
                            kegiatanObject.getString("foto"),
                            kegiatanObject.getString("nama"),
                            kegiatanObject.getString("tanggal"),
                            kegiatanObject.getString("status"),
                            kegiatanObject.getString("nama_keldesa")



                    ));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            CustomListAdapter adapter = new CustomListAdapter(
                    getApplicationContext(), R.layout.custom_list_layout, arrayList
            );
            lv.setAdapter(adapter);
        }
    }

    private static String readURL(String theUrl){
        StringBuilder content = new StringBuilder();
        try {
            // create url object
            URL url = new URL(theUrl);
            // create a urlconnection object
            URLConnection urlConnection = url.openConnection();
            // wrap the urlconnection via the bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null){
                content.append(line + "\n");
            }
            bufferedReader.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return content.toString();
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
