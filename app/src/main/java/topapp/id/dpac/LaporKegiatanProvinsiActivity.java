package topapp.id.dpac;

import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import toppapp.id.dpac.R;

/**
 * Created by topapp.id on 11/02/18.
 */

public class LaporKegiatanProvinsiActivity extends AppCompatActivity implements OnMapReadyCallback {

    //get server connection
    Koneksi lo_Koneksi = new Koneksi();
    String isi = lo_Koneksi.getUrl();
    String url_kabkota = isi+"lihat_kabkota.php";
    String url_kec= isi+"lihat_kec.php";
    String url_keldesa= isi+"lihat_keldesa.php";


    private String url = isi + "submit_kegiatan_provinsi.php";
    private static final String TAG = LaporKegiatanProvinsiActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    int success;
    String tag_json_obj = "json_obj_req";

    //Property
    private TextView tvlat, tvlng;
    private EditText etnamakeg, etjmlpeserta, etket;
    private Button btkirim, btbatal;
    private ImageButton btkamera, btgaleri;
    private RadioGroup rgkegiatan;
    private RadioButton rbkegiatan;
    private Spinner spinnerTps, spinnerKec, spinnerKeldesa;

    private String[] daftarTps = {
            "null",
    };

    private String[] defaultKec = {
            "-- Pilih Kampus dulu --",
    };

    private String[] defaultKeldesa = {
            "-- Pilih Gedung dulu --",
    };

    String stridtps;

    ArrayList<String> namaKabkotaList;
    ArrayList<String> idKabkotaList;

    ArrayList<String> namaKecList;
    ArrayList<String> idKecList;

    ArrayList<String> namaKeldesaList;
    ArrayList<String> idKeldesaList;

    String id_calon, etfoto, etwaktu, etlat, etlng, id_kabkota, id_kec, id_keldesa;
    SharedPreferences sharedPreferences;

    public static final String TAG_ID = "id_calon";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_KABKOTA = "nama_kabkota";
    public static final String TAG_DAPIL = "dapil";

    //initiate cam
    private ImageView ivImageCompress, ivImageCompress2, ivImageCompress3, ivImageCompress4;
    private ConnectionDetector cd;
    private Boolean upflag = false;
    private Uri selectedImage = null;
    private Bitmap bitmap, bitmapRotate;
    private ProgressDialog pDialog;
    String imagepath = "";
    String fname, fname2, fname3, fname4; // ini adalah file gambar yg akan di upload (.png)
    File file;

    // Class Location
    LocationManager lm;
    LocationListener locationListener;
    String lat, lng, bestProvider;
    Criteria c = new Criteria();

    private GoogleMap mMap;

    //from upload file tutorial final
    public static final String DATE_FORMAT = "yyyyMMdd_HHmmss";
    public static final String IMAGE_DIRECTORY = "GaleriBCAD";
    private SimpleDateFormat dateFormatter;

    private File sourceFile;
    private File destFile, destFile2, destFile3, destFile4; // ini adalah nama file
    private Uri imageCaptureUri, imageCaptureUri2, imageCaptureUri3, imageCaptureUri4;
    Bitmap bmp, bmp2, bmp3, bmp4;

    private static final String SCHEME_FILE = "file";
    private static final String SCHEME_CONTENT = "content";

    // class for get current location
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null) {
                lat = String.valueOf(loc.getLatitude());
                tvlat.setText(lat);

                lng = String.valueOf(loc.getLongitude());
                tvlng.setText(lng);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            String statusString = "";
            switch (status) {
                case LocationProvider.AVAILABLE:
                    statusString = "available";
                case LocationProvider.OUT_OF_SERVICE:
                    statusString = "out of service";
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    statusString = "temporarily unavailable";
            }

            Toast.makeText(getBaseContext(),
                    provider + " " + statusString,
                    Toast.LENGTH_SHORT).show();


        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getBaseContext(),
                    "Provider: " + provider + " enabled",
                    Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getBaseContext(),
                    "Provider: " + provider + " disabled",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        List<String> locationProviders = lm.getAllProviders();
        for (String provider : locationProviders){
            Log.d("LocationProviders", provider);
        }

        // set the criteria for the best location provider

        c.setAccuracy(Criteria.ACCURACY_FINE);
        //c.setAccuracy(Criteria.ACCURACY_COARSE);
        c.setAltitudeRequired(false);
        c.setBearingRequired(false);
        c.setCostAllowed(true);
        c.setPowerRequirement(Criteria.POWER_HIGH);



        //obj initiate
        cd = new ConnectionDetector(LaporKegiatanProvinsiActivity.this);
        cd = new ConnectionDetector(getApplicationContext());

        namaKabkotaList=  new ArrayList<>();
        idKabkotaList=  new ArrayList<>();

        namaKecList = new ArrayList<>();
        idKecList = new ArrayList<>();

        namaKeldesaList= new ArrayList<>();
        idKeldesaList= new ArrayList<>();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new ReadJSON().execute(url_kabkota);

            }
        });

        file = new File(Environment.getExternalStorageDirectory()
                + "/" + IMAGE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }

        dateFormatter = new SimpleDateFormat(
                DATE_FORMAT, Locale.US);
        showLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //-request for location update using GPS
        // get the best
        bestProvider = lm.getBestProvider(c, true);
        Log.d("LocationProviders", "Best provider is "+ bestProvider);

        // get the last know location
        Location location = lm.getLastKnownLocation(bestProvider);
        if (location != null) Log.d("LocationProviders", location.toString());

        lm.requestLocationUpdates(
                // Ambil lokasi dari BTS
                LocationManager.NETWORK_PROVIDER,

                // Ambil lokasi dari Satelit
                //bestProvider,
                0,
                0,
                locationListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }

        //--remove the location listener--
        lm.removeUpdates(locationListener);
    }

    private void showLayout() {
        setContentView(R.layout.activity_lapor_kegiatan_provinsi);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        tvlat = findViewById(R.id.tvlat);
        tvlng = findViewById(R.id.tvlng);
        etnamakeg = findViewById(R.id.etnamakeg);
        rgkegiatan = findViewById(R.id.rg);

        spinnerKec = findViewById(R.id.spinnerKec);
        spinnerKeldesa= findViewById(R.id.spinnerKeldesa);
        etjmlpeserta = findViewById(R.id.etjmlpeserta);
        etket =findViewById(R.id.etket);
        ivImageCompress = findViewById(R.id.ivImageCompress);
        ivImageCompress2 = findViewById(R.id.ivImageCompress2);
        ivImageCompress3 = findViewById(R.id.ivImageCompress3);
        ivImageCompress4 = findViewById(R.id.ivImageCompress4);
        btkamera = findViewById(R.id.btnCamera);
        btgaleri = findViewById(R.id.btnGallery);
        btkirim = findViewById(R.id.btkirim);
        btbatal = findViewById(R.id.btbatal);
        spinnerTps = findViewById(R.id.spinnerTps);

        // inisialisasi Array Adapter dengan memasukkan daftarTps
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, daftarTps);
        final ArrayAdapter<String> adapterKec = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, defaultKec);
        final ArrayAdapter<String> adapterKeldesa = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, defaultKeldesa);


        // set Array Adapter ke spinner
        spinnerTps.setAdapter(adapter);
        spinnerKec.setAdapter(adapterKec);
        spinnerKeldesa.setAdapter(adapterKeldesa);


        spinnerTps.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                stridtps = spinnerTps.getSelectedItem().toString();
       //         Toast.makeText(getApplicationContext(), "ID Kabkota : "+ idKabkotaList.get(i), Toast.LENGTH_SHORT).show();
                idKecList.clear();
                namaKecList.clear();
                if (i>0) {
                    idKeldesaList.clear();
                    namaKeldesaList.clear();
                    id_kabkota = idKabkotaList.get(i);
                    new ReadJSONKec().execute(url_kec + "?id_kabkota=" + idKabkotaList.get(i));
                    spinnerKeldesa.setAdapter(adapterKeldesa);
                }
//
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });

        spinnerKec.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getApplicationContext(), "ID Kec: "+ idKecList.get(i), Toast.LENGTH_SHORT).show();
                idKeldesaList.clear();
                namaKeldesaList.clear();
                if (i>0) {
                    id_kec = idKecList.get(i);
                    new ReadJSONKeldesa().execute(url_keldesa + "?id_kec=" + idKecList.get(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerKeldesa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i>0){
                    id_keldesa = idKeldesaList.get(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //onclick button
        btkamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCamera();
            }
        });

        btgaleri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickGaleri();
            }
        });

        btbatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // refresh page
                ivImageCompress.setImageResource(android.R.color.transparent);
                ivImageCompress.setVisibility(view.GONE);
                fname = null;
                etnamakeg.setText("");
                // replace with action default checked radio button kegiatan
//                etlokasikec.setText("");
//                etlokasikel.setText("");
                etjmlpeserta.setText("");
                etket.setText("");
            }
        });

        btkirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kirimLaporan();
            }
        });
    }

    private void ambilDataKec(final int i) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new ReadJSON().execute(url_kec+"?id_kabkota="+i);
            }
        });
    }

    private void kirimLaporan() {
        // get datetime
        Date currentTime = Calendar.getInstance().getTime();
        // convert to dateTime format
        SimpleDateFormat date24Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // get session
        sharedPreferences = getSharedPreferences(LoginActivity.my_shared_preferences, Context.MODE_PRIVATE);
        id_calon = getIntent().getStringExtra(TAG_ID);

        int selectId = rgkegiatan.getCheckedRadioButtonId();
        rbkegiatan = findViewById(selectId);

        String strnamakeg = etnamakeg.getText().toString();
        String strjeniskeg = rbkegiatan.getText().toString();
        String strlokasikabkota = id_kabkota;
        String strlokasikec = id_kec;
        String strlokasikeldesa = id_keldesa;
        String strjmlpeserta = etjmlpeserta.getText().toString();
        String strket = etket.getText().toString();
        String strwaktu = date24Format.format(currentTime);
        String strfoto = fname;
        String strfoto2 = fname2;
        String strfoto3 = fname3;
        String strfoto4 = fname4;
        String strlat = lat;
        String strlng = lng;

        System.out.println("ID Calon : " + id_calon + " Nama keg : " + strnamakeg + " Lokasi kabkota : " + strlokasikabkota+ " Lokasi kecamatan : " + strlokasikec + " Lokasi kelurahan : " + strlokasikeldesa + " Jumlah peserta : " + strjmlpeserta + " Tanggal : " + strwaktu + "Jenis keg : " + strjeniskeg + " Lat : " + strlat + " Lng : " + strlng + " Foto1 : " + strfoto + " Foto2 : " +strfoto2+ " Foto3 : " +strfoto3+ " Foto4 : " +strfoto4+ " Ket : " + strket);

        // pengecekkan form tidak boleh kosong
        if ((strnamakeg).equals("") || (strjeniskeg.equals("")) || (strlokasikabkota == null) || (strlokasikec == null) || (strlokasikeldesa == null) || strjmlpeserta.equals("") || strfoto == null) {

            Toast.makeText(getApplicationContext(), "Form tidak boleh kosong", Toast.LENGTH_SHORT).show();
        } else if (lat == null || lng == null){
            Toast.makeText(getApplicationContext(), "Mohon menunggu, Aplikasi sedang membaca lokasi anda. ", Toast.LENGTH_SHORT).show();
            Toast.makeText(getApplicationContext(), "Pastikan koneksi internet dan GPS anda sudah diaktfikan. ", Toast.LENGTH_SHORT).show();
        } else {
            // upload gambar dilakukan saat tombol kirim ditekan
            if (cd.isConnectingToInternet()) {
                if (!upflag) {
                    Toast.makeText(getApplicationContext(), "Anda belum mengambil gambar..!", Toast.LENGTH_LONG).show();
                } else {
                    // lakukan upload gambar
                    saveFile(bitmapRotate, destFile);

                    if (ivImageCompress2.getDrawable()!=null){
                        Log.i(TAG, "kirimLaporan: menyimpan gambar ke-2");
                        saveFile2(bitmapRotate, destFile2);

                    } if (ivImageCompress3.getDrawable()!=null){
                        Log.i(TAG, "kirimLaporan: menyimpan gambar ke-3");
                        saveFile3(bitmapRotate, destFile3);

                    } if (ivImageCompress4.getDrawable()!=null){
                        Log.i(TAG, "kirimLaporan: menyimpan gambar ke-4");
                        saveFile4(bitmapRotate, destFile4);
                    }

                    // agar tidak error saat parameter==null
                    if (strfoto2==null) strfoto2="";
                    if (strfoto3==null) strfoto3="";
                    if (strfoto4==null) strfoto4="";

                    //tu lakukan insert data kecelakaan
                    saveToServer(id_calon, strnamakeg, strlokasikabkota, strlokasikec, strlokasikeldesa, strjmlpeserta, strwaktu, strjeniskeg, strlat, strlng, strfoto, strfoto2, strfoto3, strfoto4, strket);

                    // uncoment utk selesaikan activity

                    finish();
                    restartFirstActivity();

                }
            } else {
                Toast.makeText(getApplicationContext(), "Tidak ada koneksi internet !", Toast.LENGTH_LONG).show();
            }
        }

    } // end of kirimLaporan()

    private void restartFirstActivity()
    {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName() );

        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(i);
    }

    private void saveToServer(final String id_calon, final String nama_keg, final String lokasi_kabkota, final String lokasi_kec, final String lokasi_kel, final String jumlah_peserta, final String tanggal, final String jenis_keg, final String lat, final String lng, final String foto, final String foto2, final String foto3, final String foto4, final String ket){
//        pDialog = new ProgressDialog(this);
//        pDialog.setCancelable(false);
//        pDialog.setMessage("Loading ...");
        //pDialog.show();
        //showDialog();



        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Submit Suara Response : "+response.toString());
              //  hideDialog();
//                pDialog.dismiss();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // check for error node in json
                    if(success == 1){
                        Toast.makeText(getApplicationContext(), "Laporan berhasil dikirim", Toast.LENGTH_LONG).show();
                        // Yeah sukses, koding aksi berikutnya di sini
                    } else if (success == 0){
                        //oh tidak seesuatu yang buruk terjadi
//                        Toast.makeText(getApplicationContext(),
//                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e){
                    //JSON Error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Submit Kegiatan Error: "+error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to submit suara url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_calon", id_calon);
                params.put("nama", nama_keg);
                params.put("id_kabkota", lokasi_kabkota);
                params.put("id_kec", lokasi_kec);
                params.put("id_keldesa", lokasi_kel);
                params.put("jumlah_peserta", jumlah_peserta);
                params.put("tanggal", tanggal);
                params.put("jenis", jenis_keg);
                params.put("lat", lat);
                params.put("lng", lng);
                params.put("foto", foto);
                params.put("foto2", foto2);
                params.put("foto3", foto3);
                params.put("foto4", foto4);
                params.put("keterangan", ket);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            switch (requestCode) {
                case 101:
                    if (resultCode == Activity.RESULT_OK) {
                                upflag = true;
                        Log.d(TAG + ".PICK_CAMERA_IMAGE", "Selected image uri path :" + imageCaptureUri);

                        //ivImage.setImageURI(imageCaptureUri);
                        bmp = decodeFile(destFile);
                        ivImageCompress.setVisibility(View.VISIBLE);
                        ivImageCompress.setImageBitmap(bmp);
                    }
                    break;
                case 103:
                    if (resultCode == Activity.RESULT_OK) {
                        upflag = true;
                        Log.d(TAG + ".PICK_CAMERA_IMAGE", "Selected image uri path :" + imageCaptureUri);

                        //ivImage.setImageURI(imageCaptureUri);
                        bmp2 = decodeFile2(destFile);
                        ivImageCompress2.setVisibility(View.VISIBLE);
                        ivImageCompress2.setImageBitmap(bmp2);
                    }
                    break;
                case 104:
                    if (resultCode == Activity.RESULT_OK) {
                        upflag = true;
                        Log.d(TAG + ".PICK_CAMERA_IMAGE", "Selected image uri path :" + imageCaptureUri);

                        //ivImage.setImageURI(imageCaptureUri);
                        bmp3 = decodeFile3(destFile);
                        ivImageCompress3.setVisibility(View.VISIBLE);
                        ivImageCompress3.setImageBitmap(bmp3);
                    }
                    break;
                case 105:
                    if (resultCode == Activity.RESULT_OK) {
                        upflag = true;
                        Log.d(TAG + ".PICK_CAMERA_IMAGE", "Selected image uri path :" + imageCaptureUri);

                        //ivImage.setImageURI(imageCaptureUri);
                        bmp4 = decodeFile4(destFile);
                        ivImageCompress4.setVisibility(View.VISIBLE);
                        ivImageCompress4.setImageBitmap(bmp4);
                    }
                    break;
                case 102:
                    if (resultCode == Activity.RESULT_OK){
                        upflag = true;

                        Uri uriPhoto = data.getData();
                        Log.d(TAG + ".PICK_GALLERY_IMAGE", "Selected image uri path :" + uriPhoto.toString());

                        ivImageCompress.setVisibility(View.VISIBLE);
                        ivImageCompress.setImageURI(uriPhoto);

                        sourceFile = new File(getPathFromGooglePhotosUri(uriPhoto));


                        destFile = new File(file, "kegiatansatu_"
                                + dateFormatter.format(new Date()).toString() + ".png");




                        Log.d(TAG, "Source File Path :" + sourceFile);

                        try {
                            copyFile(sourceFile, destFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        bmp = decodeFile(destFile);
//                        try {
//
//                            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                            bitmap = getResizedBitmap(bmp, 50);
//                            //bmp = decodeFile(destFile);
//                            ivImageCompress.setVisibility(View.VISIBLE);
//                            ivImageCompress.setImageBitmap(bitmap);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }
                    break;
                case 106:
                    if (resultCode == Activity.RESULT_OK){
                        upflag = true;

                        Uri uriPhoto = data.getData();
                        Log.d(TAG + ".PICK_GALLERY_IMAGE", "Selected image uri path :" + uriPhoto.toString());

                        ivImageCompress2.setVisibility(View.VISIBLE);
                        ivImageCompress2.setImageURI(uriPhoto);

                        sourceFile = new File(getPathFromGooglePhotosUri(uriPhoto));


                        destFile2 = new File(file, "kegiatandua_"
                                + dateFormatter.format(new Date()).toString() + ".png");




                        Log.d(TAG, "Source File Path :" + sourceFile);

                        try {
                            copyFile(sourceFile, destFile2);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        bmp2 = decodeFile2(destFile2);
//                        try {
//
//                            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                            bitmap = getResizedBitmap(bmp, 50);
//                            //bmp = decodeFile(destFile);
//                            ivImageCompress.setVisibility(View.VISIBLE);
//                            ivImageCompress.setImageBitmap(bitmap);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }
                    break;
                case 107:
                    if (resultCode == Activity.RESULT_OK){
                        upflag = true;

                        Uri uriPhoto = data.getData();
                        Log.d(TAG + ".PICK_GALLERY_IMAGE", "Selected image uri path :" + uriPhoto.toString());

                        ivImageCompress3.setVisibility(View.VISIBLE);
                        ivImageCompress3.setImageURI(uriPhoto);

                        sourceFile = new File(getPathFromGooglePhotosUri(uriPhoto));


                        destFile3 = new File(file, "kegiatantiga_"
                                + dateFormatter.format(new Date()).toString() + ".png");




                        Log.d(TAG, "Source File Path :" + sourceFile);

                        try {
                            copyFile(sourceFile, destFile3);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        bmp3 = decodeFile3(destFile3);
//                        try {
//
//                            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                            bitmap = getResizedBitmap(bmp, 50);
//                            //bmp = decodeFile(destFile);
//                            ivImageCompress.setVisibility(View.VISIBLE);
//                            ivImageCompress.setImageBitmap(bitmap);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }
                    break;
                case 108:
                    if (resultCode == Activity.RESULT_OK){
                        upflag = true;

                        Uri uriPhoto = data.getData();
                        Log.d(TAG + ".PICK_GALLERY_IMAGE", "Selected image uri path :" + uriPhoto.toString());

                        ivImageCompress4.setVisibility(View.VISIBLE);
                        ivImageCompress4.setImageURI(uriPhoto);

                        sourceFile = new File(getPathFromGooglePhotosUri(uriPhoto));


                        destFile4 = new File(file, "kegiatanempat_"
                                + dateFormatter.format(new Date()).toString() + ".png");




                        Log.d(TAG, "Source File Path :" + sourceFile);

                        try {
                            copyFile(sourceFile, destFile4);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        bmp4 = decodeFile4(destFile4);
//                        try {
//
//                            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                            bitmap = getResizedBitmap(bmp, 50);
//                            //bmp = decodeFile(destFile);
//                            ivImageCompress.setVisibility(View.VISIBLE);
//                            ivImageCompress.setImageBitmap(bitmap);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    //    In some mobiles image will get rotate so to correting that this code will help us
    private int getImageOrientation() {
        final String[] imageColumns = {MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.ORIENTATION};
        final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";

        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imageColumns, null, null, imageOrderBy);

        if (cursor.moveToFirst()) {
            int orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));
            System.out.println("orientation===" + orientation);
            cursor.close();
            return orientation;
        } else {
            return 0;
        }
    }

    //    Saving file to the mobile internal memory
    private void saveFile(Bitmap sourceUri, File destination) {
        if (destination.exists()) destination.delete();

        try {
            FileOutputStream out = new FileOutputStream(destFile);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Log.i(TAG, "saveFile: Berhasil menyimpan gambar ke-1");
            if (cd.isConnectingToInternet()) {
                new DoFileUpload().execute();
            } else {
                Toast.makeText(getApplicationContext(), "Tidak ada koneksi internet..", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveFile2(Bitmap sourceUri, File destination) {
        if (destination.exists()) destination.delete();

        try {
            FileOutputStream out = new FileOutputStream(destFile2);
            bmp2.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Log.i(TAG, "saveFile: Berhasil menyimpan gambar ke-2");
            if (cd.isConnectingToInternet()) {
                new DoFileUpload2().execute();
            } else {
                Toast.makeText(getApplicationContext(), "Tidak ada koneksi internet..", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveFile3(Bitmap sourceUri, File destination) {
        if (destination.exists()) destination.delete();

        try {
            FileOutputStream out = new FileOutputStream(destFile3);
            bmp3.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Log.i(TAG, "saveFile: Berhasil menyimpan gambar ke-3");
            if (cd.isConnectingToInternet()) {
                new DoFileUpload3().execute();
            } else {
                Toast.makeText(getApplicationContext(), "Tidak ada koneksi internet..", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveFile4(Bitmap sourceUri, File destination) {
        if (destination.exists()) destination.delete();

        try {
            FileOutputStream out = new FileOutputStream(destFile4);
            bmp4.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            Log.i(TAG, "saveFile: Berhasil menyimpan gambar ke-4");
            if (cd.isConnectingToInternet()) {
                new DoFileUpload4().execute();
            } else {
                Toast.makeText(getApplicationContext(), "Tidak ada koneksi internet..", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap decodeFile(File f) {
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int IMAGE_MAX_SIZE = 1024;
        int scale = 1;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Width :" + b.getWidth() + " Height :" + b.getHeight());

        fname = "kegiatansatu_"
                + dateFormatter.format(new Date()).toString() + ".png";
        destFile = new File(file, fname);

        return b;
    }

    private Bitmap decodeFile2(File f) {
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int IMAGE_MAX_SIZE = 1024;
        int scale = 1;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Width :" + b.getWidth() + " Height :" + b.getHeight());

        fname2 = "kegiatandua_"
                + dateFormatter.format(new Date()).toString() + ".png";
        destFile2 = new File(file, fname2);

        return b;
    }

    private Bitmap decodeFile3(File f) {
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int IMAGE_MAX_SIZE = 1024;
        int scale = 1;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Width :" + b.getWidth() + " Height :" + b.getHeight());

        fname3 = "kegiatantiga_"
                + dateFormatter.format(new Date()).toString() + ".png";
        destFile = new File(file, fname3);

        return b;
    }

    private Bitmap decodeFile4(File f) {
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int IMAGE_MAX_SIZE = 1024;
        int scale = 1;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Width :" + b.getWidth() + " Height :" + b.getHeight());

        fname4 = "kegiatanempat_"
                + dateFormatter.format(new Date()).toString() + ".png";
        destFile = new File(file, fname4);

        return b;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        try {
//            // Customise the styling of the base map using a JSON object defined
//            // in a raw resource file.
//            boolean success = googleMap.setMapStyle(
//                    MapStyleOptions.loadRawResourceStyle(
//                            this, R.raw.style_json));
//
//            if (!success) {
//                Log.e(TAG, "Style parsing failed.");
//            }
//        } catch (Resources.NotFoundException e) {
//            Log.e(TAG, "Can't find style. Error: ", e);
//        }

        mMap.setMinZoomPreference(16);


        // Position the map's camera near Sydney, Australia.
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-6.556176,107.749168)));

        mMap.setMyLocationEnabled(true);
    }

    class DoFileUpload extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(LaporKegiatanProvinsiActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage("Mohon menunggu, sedang mengupload gambar ke-1..");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

        try {
                // Set your file path here
                FileInputStream fstrm = new FileInputStream(destFile);
                // Set your server page url (and the file title/description)
                HttpFileUpload hfu = new HttpFileUpload(isi+"file_upload_kegiatan.php", "ftitle", "fdescription", fname);
                upflag = hfu.Send_Now(fstrm);
            } catch (FileNotFoundException e) {
                // Error: File not found
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
//            if (pDialog.isShowing()) {
                pDialog.dismiss();
//            }
            if (upflag) {
                Toast.makeText(getApplicationContext(), "Upload gambar berhasil", Toast.LENGTH_LONG).show();
                Log.i(TAG, "onPostExecute: Upload gambar ke-1 berhasil");

            } else {
                Toast.makeText(getApplicationContext(), "Sayangnya gambar tidak bisa diupload..", Toast.LENGTH_LONG).show();
            }
        }
    }

    class DoFileUpload2 extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(LaporKegiatanProvinsiActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage("Mohon menunggu, sedang mengupload gambar ke-2..");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                // Set your file path here
                FileInputStream fstrm = new FileInputStream(destFile2);
                // Set your server page url (and the file title/description)
                HttpFileUpload hfu = new HttpFileUpload(isi+"file_upload_kegiatan.php", "ftitle", "fdescription", fname2);
                upflag = hfu.Send_Now(fstrm);
            } catch (FileNotFoundException e) {
                // Error: File not found
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
//            if (pDialog.isShowing()) {
                pDialog.dismiss();
//            }
            if (upflag) {
                Toast.makeText(getApplicationContext(), "Upload gambar ke-2 berhasil", Toast.LENGTH_LONG).show();
                Log.i(TAG, "onPostExecute: Upload gambar ke-2 berhasil");

            } else {
                Toast.makeText(getApplicationContext(), "Sayangnya gambar ke-2 tidak bisa diupload..", Toast.LENGTH_LONG).show();
            }
        }
    }

    class DoFileUpload3 extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(LaporKegiatanProvinsiActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage("Mohon menunggu, sedang mengupload gambar ke-3..");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                // Set your file path here
                FileInputStream fstrm = new FileInputStream(destFile3);
                // Set your server page url (and the file title/description)
                HttpFileUpload hfu = new HttpFileUpload(isi+"file_upload_kegiatan.php", "ftitle", "fdescription", fname3);
                upflag = hfu.Send_Now(fstrm);
            } catch (FileNotFoundException e) {
                // Error: File not found
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
//            if (pDialog.isShowing()) {
                pDialog.dismiss();
//            }
            if (upflag) {
                Toast.makeText(getApplicationContext(), "Upload gambar ke-3 berhasil", Toast.LENGTH_LONG).show();
                Log.i(TAG, "onPostExecute: Upload gambar ke-3 berhasil");
            } else {
                Toast.makeText(getApplicationContext(), "Sayangnya gambar ke-3 tidak bisa diupload..", Toast.LENGTH_LONG).show();
            }
        }
    }

    class DoFileUpload4 extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(LaporKegiatanProvinsiActivity.this);
            pDialog.setCancelable(false);
            pDialog.setMessage("Mohon menunggu, sedang mengupload gambar ke-4..");
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                // Set your file path here
                FileInputStream fstrm = new FileInputStream(destFile4);
                // Set your server page url (and the file title/description)
                HttpFileUpload hfu = new HttpFileUpload(isi+"file_upload_kegiatan.php", "ftitle", "fdescription", fname4);
                upflag = hfu.Send_Now(fstrm);
            } catch (FileNotFoundException e) {
                // Error: File not found
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
//            if (pDialog.isShowing()) {
                pDialog.dismiss();
//            }
            if (upflag) {
                Toast.makeText(getApplicationContext(), "Upload gambar ke-4 berhasil", Toast.LENGTH_LONG).show();
                Log.i(TAG, "onPostExecute: Upload gambar ke-4 berhasil");
            } else {
                Toast.makeText(getApplicationContext(), "Sayangnya gambar ke-4 tidak bisa diupload..", Toast.LENGTH_LONG).show();
            }
        }
    }

//    private void clickCamera() {
//        Intent cameraintent = new Intent(
//                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraintent, 101);
//
//    }

    private void clickCamera(){
        destFile = new File(file, "kegiatan1_"
                + dateFormatter.format(new Date()).toString() + ".png");
        destFile2 = new File(file, "kegiatan2_"
                + dateFormatter.format(new Date()).toString() + ".png");
        destFile3 = new File(file, "kegiatan3_"
                + dateFormatter.format(new Date()).toString() + ".png");
        destFile4 = new File(file, "kegiatan4_"
                + dateFormatter.format(new Date()).toString() + ".png");
        imageCaptureUri = Uri.fromFile(destFile);

        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureUri);

        if (ivImageCompress.getDrawable()==null){
            Log.i(TAG, "clickCamera: ImageView1 kosong, jalankan requestCode 101");
            startActivityForResult(intentCamera, 101);
            Toast.makeText(getApplicationContext(), "Sisa gambar yang dapat diambil: 4", Toast.LENGTH_SHORT).show();
        } else if (ivImageCompress2.getDrawable()==null){
            Log.i(TAG, "clickCamera: ImageView2 kosong, jalankan requestCode 103");
            startActivityForResult(intentCamera, 103);
            Toast.makeText(getApplicationContext(), "Sisa gambar yang dapat diambil: 3", Toast.LENGTH_SHORT).show();
        } else if (ivImageCompress3.getDrawable()==null){
            Log.i(TAG, "clickCamera: ImageView3 kosong, jalankan requestCode 104");
            startActivityForResult(intentCamera, 104);
            Toast.makeText(getApplicationContext(), "Sisa gambar yang dapat diambil: 2", Toast.LENGTH_SHORT).show();
        } else if (ivImageCompress4.getDrawable()==null){
            Log.i(TAG, "clickCamera: ImageView4 kosong, jalankan requestCode 105");
            startActivityForResult(intentCamera, 105);
            Toast.makeText(getApplicationContext(), "Sisa gambar yang dapat diambil: 1", Toast.LENGTH_SHORT).show();
        }


    }

    private void clickGaleri(){
        //        fname = "kegiatanPascagub_"
//                + dateFormatter.format(new Date()).toString() + ".png";
//        destFile = new File(file, fname);
//
//        imageCaptureUri = Uri.fromFile(destFile);

        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");

//        intent.setAction(Intent.ACTION_GET_CONTENT);

        if (ivImageCompress.getDrawable()==null){
            Log.i(TAG, "clickCamera: ImageView1 kosong, jalankan requestCode 102");
            startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 102);
            Toast.makeText(getApplicationContext(), "Sisa gambar yang dapat diambil: 4", Toast.LENGTH_SHORT).show();
        } else if (ivImageCompress2.getDrawable()==null){
            Log.i(TAG, "clickCamera: ImageView2 kosong, jalankan requestCode 106");
            startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 106);
            Toast.makeText(getApplicationContext(), "Sisa gambar yang dapat diambil: 3", Toast.LENGTH_SHORT).show();
        } else if (ivImageCompress3.getDrawable()==null){
            Log.i(TAG, "clickCamera: ImageView3 kosong, jalankan requestCode 107");
            startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 107);
            Toast.makeText(getApplicationContext(), "Sisa gambar yang dapat diambil: 2", Toast.LENGTH_SHORT).show();
        } else if (ivImageCompress4.getDrawable()==null){
            Log.i(TAG, "clickCamera: ImageView4 kosong, jalankan requestCode 108");
            startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 108);
            Toast.makeText(getApplicationContext(), "Sisa gambar yang dapat diambil: 1", Toast.LENGTH_SHORT).show();
        }


    }



    // Class getting dropdown from database
    class ReadJSON extends AsyncTask<String, Integer, String>{

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(LaporKegiatanProvinsiActivity.this);
            pDialog.setMessage("Mohon menunggu, sedang mengambil data Kampus..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
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
                int i;
                for (i = 0; i < jsonArray.length(); i++) {
                    JSONObject kabkotaObject = jsonArray.getJSONObject(i);
//                    arrayList.add(new Kabkota(
//                            kabkotaObject.getString("id_kabkota"),
//                            kabkotaObject.getString("nama")
//                    ));
                    if (i==0){
                        namaKabkotaList.add("Silahkan Pilih Kampus");
                        idKabkotaList.add("0");
                    }
                    namaKabkotaList.add(kabkotaObject.getString("nama"));
                    idKabkotaList.add(kabkotaObject.getString("id_kabkota"));
                    System.out.println("Data Kampus telah dimasukkan");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            CustomListAdapter adapter = new CustomListAdapter(
//                    getApplicationContext(), R.layout.custom_list_layout, arrayList
//            );
//            lv.setAdapter(adapter);

            spinnerTps.setAdapter(new ArrayAdapter<>(LaporKegiatanProvinsiActivity.this, android.R.layout.simple_spinner_dropdown_item, namaKabkotaList));
        }
    }

    class ReadJSONKec extends AsyncTask<String, Integer, String>{

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(LaporKegiatanProvinsiActivity.this);
            pDialog.setMessage("Mohon menunggu, sedang mengambil data Gedung..");
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

                int i;
                for (i = 0; i < jsonArray.length(); i++) {
                    JSONObject kabkotaObject = jsonArray.getJSONObject(i);
                    if (i==0){
                        namaKecList.add("Silahkan Pilih Gedung");
                        idKecList.add("0");
                    }
                    namaKecList.add(kabkotaObject.getString("nama"));
                    idKecList.add(kabkotaObject.getString("id_kec"));
                    System.out.println("Data Gedung "+kabkotaObject.getString("nama")+" dimasukkan");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            spinnerKec.setAdapter(new ArrayAdapter<>(LaporKegiatanProvinsiActivity.this, android.R.layout.simple_spinner_dropdown_item, namaKecList));
        }
    }

    class ReadJSONKeldesa extends AsyncTask<String, Integer, String>{

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(LaporKegiatanProvinsiActivity.this);
            pDialog.setMessage("Mohon menunggu, sedang mengambil data Ruangan..");
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

                int i;
                for (i = 0; i < jsonArray.length(); i++) {
                    JSONObject kabkotaObject = jsonArray.getJSONObject(i);
                    if (i==0){
                        namaKeldesaList.add("Silahkan Pilih Ruangan");
                        idKeldesaList.add("0");
                    }
                    namaKeldesaList.add(kabkotaObject.getString("nama"));
                    idKeldesaList.add(kabkotaObject.getString("id_keldesa"));
                    System.out.println("Data Ruangan "+kabkotaObject.getString("nama")+" dimasukkan");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            spinnerKeldesa.setAdapter(new ArrayAdapter<>(LaporKegiatanProvinsiActivity.this, android.R.layout.simple_spinner_dropdown_item, namaKeldesaList));
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


    // compress bitmap image from gallery
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    /**
     * This is useful when an image is available in sdcard physically.
     *
     * @param uriPhoto
     * @return
     */
    public String getPathFromUri(Uri uriPhoto) {
        if (uriPhoto == null)
            return null;

        if (SCHEME_FILE.equals(uriPhoto.getScheme())) {
            return uriPhoto.getPath();
        } else if (SCHEME_CONTENT.equals(uriPhoto.getScheme())) {
            final String[] filePathColumn = {MediaStore.MediaColumns.DATA,
                    MediaStore.MediaColumns.DISPLAY_NAME};
            Cursor cursor = null;
            try {
                cursor = getContentResolver().query(uriPhoto, filePathColumn, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    final int columnIndex = (uriPhoto.toString()
                            .startsWith("content://com.google.android.gallery3d")) ? cursor
                            .getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                            : cursor.getColumnIndex(MediaStore.MediaColumns.DATA);

                    // Picasa images on API 13+
                    if (columnIndex != -1) {
                        String filePath = cursor.getString(columnIndex);
                        if (!TextUtils.isEmpty(filePath)) {
                            return filePath;
                        }
                    }
                }
            } catch (IllegalArgumentException e) {
                // Nothing we can do
                Log.d(TAG, "IllegalArgumentException");
                e.printStackTrace();
            } catch (SecurityException ignored) {
                Log.d(TAG, "SecurityException");
                // Nothing we can do
                ignored.printStackTrace();
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }
        return null;
    }

    /**
     * This is useful when an image is not available in sdcard physically but it displays into photos application via google drive(Google Photos) and also for if image is available in sdcard physically.
     *
     * @param uriPhoto
     * @return
     */

    public String getPathFromGooglePhotosUri(Uri uriPhoto) {
        if (uriPhoto == null)
            return null;

        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uriPhoto, "r");
            FileDescriptor fd = pfd.getFileDescriptor();
            input = new FileInputStream(fd);

            String tempFilename = getTempFilename(this);
            output = new FileOutputStream(tempFilename);

            int read;
            byte[] bytes = new byte[4096];
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }
            return tempFilename;
        } catch (IOException ignored) {
            // Nothing we can do
        } finally {
            closeSilently(input);
            closeSilently(output);
        }
        return null;
    }

    public static void closeSilently(Closeable c) {
        if (c == null)
            return;
        try {
            c.close();
        } catch (Throwable t) {
            // Do nothing
        }
    }

    private static String getTempFilename(Context context) throws IOException {
        File outputDir = context.getCacheDir();
        File outputFile = File.createTempFile("image", "tmp", outputDir);
        return outputFile.getAbsolutePath();
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }

}
