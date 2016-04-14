package id.pantauharga.android.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import id.pantauharga.android.Konstan;
import id.pantauharga.android.R;
import id.pantauharga.android.aktivitas.Rating;
import id.pantauharga.android.messagebus.MessageAktFrag;
import id.pantauharga.android.modelgson.HargaKomoditasItem;
import id.pantauharga.android.modelgson.HargaKomoditasItemKomparator;
import id.pantauharga.android.parsers.Parseran;

/**
 * Created by Gulajava Ministudio on 11/5/15.
 */
public class FragmentPetaHarga extends Fragment {


    //GOOGLE MAPS
    private SupportMapFragment mapfragment;
    private GoogleMap map;
    private static final int paddingTop_dp = 0;
    private static final int paddingBottom_dp = 250;
    private static int paddingTop_px = 0;
    private static int paddingBottom_px = 0;

    @Bind(R.id.teks_namakomoditas)
    TextView teks_namakomoditas;
    @Bind(R.id.teks_keterangan)
    TextView teks_keterangan;
    @Bind(R.id.teks_lastupdate)
    TextView teks_lastupdate;
    @Bind(R.id.teks_hargakomoditas)
    TextView teks_hargakomoditas;
    @Bind(R.id.teks_alamatlokasi)
    TextView teks_alamatkomoditas;
    @Bind(R.id.teks_rate)
    TextView teks_rate;
    @Bind(R.id.teks_nomortelpon)
    TextView teks_telponkomoditas;
    @Bind(R.id.tombol_navigasi)
    FloatingActionButton btnNavigasi;
    @Bind(R.id.tombol_telepon)
    FloatingActionButton btnTelepon;
    @Bind(R.id.tombol_sms)
    FloatingActionButton btnSms;
    @Bind(R.id.tombol_rating)
    FloatingActionButton btnRating;
    @Bind(R.id.btn_share)
    FloatingActionButton btnShare;


    //untuk menampilkan marker posisi pengguna
    private LatLng myCoordinate = null;
    private CameraPosition myCameraPosition = null;
    private Location myLocation = null;
    private Marker myMarker = null;
    private double myLatitude = 0;
    private double myLongitude = 0;
    private boolean isMapSiap = false;
    private SimpleDateFormat formatDateTime = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
    //DAFTAR HARGA KOMODITAS

    private List<HargaKomoditasItem> mListKomoditasHarga;
    private List<HargaKomoditasItemKomparator> mListKomoHargaKomparator;

    private Map<Marker, HargaKomoditasItemKomparator> hashmapListHarga;

    //JIKA PETA DIPILIH DARI HALAMAN SEBELAH
    private CameraPosition posisikameraklik = null;
    private Marker markerklik = null;


    //NAVIGASI KE GOOGLE MAPS
    private String latpeta = "0";
    private String longipeta = "0";

    private Parseran mParseran;
    private int modeUrutan = Konstan.MODE_TERDEKAT;


    //GEOCODER AMBIL LOKASI DAN POSISI PENGGUNA ALAMATNYA JIKA ADA
    private Geocoder geocoderPengguna;
    private List<Address> addressListPengguna = null;
    private String gecoder_alamat = "";
    private String gecoder_namakota = "";

    private String namaKomoditas = "";
    private String keteranganKomoditas = "";
    private String formatHargaKomoditas = "0";
    private String alamatKomoditas = "";
    private String teleponKomoditas = "";
    private String latitudeKomoditas = "0";
    private String longitudeKomoditas = "0";
    private String rateKomoditas = "0";
    private Date lastUpdatedKomoditas = new Date();
    private int hargaKomoditas = 0;
    private int typeKomoditas = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_petaharga, container, false);
        ButterKnife.bind(FragmentPetaHarga.this, view);

        mParseran = new Parseran(FragmentPetaHarga.this.getActivity());

        mapfragment = (SupportMapFragment) FragmentPetaHarga.this.getChildFragmentManager().findFragmentById(R.id.map);

        btnNavigasi.setOnClickListener(listenerNavigasi);
        btnTelepon.setOnClickListener(listenerTelepon);
        btnRating.setOnClickListener(listenerRating);
        btnSms.setOnClickListener(listenerSms);
        btnShare.setOnClickListener(listenerShare);

        teks_alamatkomoditas.setVisibility(View.GONE);
        teks_telponkomoditas.setVisibility(View.GONE);
        teks_keterangan.setVisibility(View.GONE);

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(FragmentPetaHarga.this);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(FragmentPetaHarga.this)) {
            EventBus.getDefault().register(FragmentPetaHarga.this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(FragmentPetaHarga.this)) {
            EventBus.getDefault().unregister(FragmentPetaHarga.this);
        }
    }

    public void displayInfo(HargaKomoditasItemKomparator itemKomoditas) {
        namaKomoditas = itemKomoditas.getBarang();
        hargaKomoditas = itemKomoditas.getPrice();
        teleponKomoditas = itemKomoditas.getNohp();
        latitudeKomoditas = itemKomoditas.getLatitude();
        longitudeKomoditas = itemKomoditas.getLongitude();
        keteranganKomoditas = itemKomoditas.getKeterangan();
        rateKomoditas = itemKomoditas.getTotalrating();
        lastUpdatedKomoditas = itemKomoditas.getLastUpdated();
        typeKomoditas = itemKomoditas.getType();
        String type;
        switch (typeKomoditas) {
            case 1:
                type = "(Jual) ";
                break;
            case 2:
                type = "(Beli) ";
                break;
            default:
                type = "(Pantau) ";
                break;

        }

        teks_namakomoditas.setText(type + namaKomoditas);

        formatHargaKomoditas = "Rp " + mParseran.formatAngkaPisah(hargaKomoditas) + ",-";
        teks_hargakomoditas.setText(formatHargaKomoditas);
        if (teleponKomoditas.length() > 4) {
            String teksets = "Telp : " + teleponKomoditas;
            teks_telponkomoditas.setText(teksets);
            teks_telponkomoditas.setVisibility(View.VISIBLE);
            btnTelepon.setVisibility(View.VISIBLE);
            btnSms.setVisibility(View.VISIBLE);
        } else {
            String teksets = "Telp : -";
            teks_telponkomoditas.setText(teksets);
            teks_telponkomoditas.setVisibility(View.GONE);
            btnTelepon.setVisibility(View.GONE);
            btnSms.setVisibility(View.GONE);
        }

        if (keteranganKomoditas != null && !keteranganKomoditas.isEmpty()) {
            teks_keterangan.setText("Keterangan : " + keteranganKomoditas);
            teks_keterangan.setVisibility(View.VISIBLE);
        } else {
            teks_keterangan.setText("Keterangan : -");
            teks_keterangan.setVisibility(View.GONE);
        }
        if (rateKomoditas != null && !rateKomoditas.isEmpty()) {
            teks_rate.setText("Rating User : " + rateKomoditas);
            teks_rate.setVisibility(View.VISIBLE);
        } else {
            teks_rate.setText("Rating User : -");
            teks_rate.setVisibility(View.GONE);
        }
        teks_lastupdate.setText("Last Updated : " + formatDateTime.format(lastUpdatedKomoditas));
    }


    /**
     * LISTENER EVENTBUS PESAN DARI AKTIVITAS UTAMA
     ***/
    public void onEvent(MessageAktFrag messageAktFrag) {

        int kodepesan = messageAktFrag.getKode();

        switch (kodepesan) {
            case Konstan.KODE_LISTBARU:
                mListKomoditasHarga = messageAktFrag.getListHargaKomoditas();
                myLocation = messageAktFrag.getLocation();
                modeUrutan = messageAktFrag.getModelist();

                //segarkan daftars
                cekDaftarHasil();

                break;
        }
    }


    //CEK DAFTAR
    private void cekDaftarHasil() {

        if (mListKomoditasHarga != null && mListKomoditasHarga.size() > 0) {
            //ambil list dan tampilkan ke peta
            parseKomparatorPeta();
        }
    }


    //UBAH MENJADI BENTUK JARAK TERDEKAT DENGAN KOMPARATOR DKK
    private void parseKomparatorPeta() {

        //setel indikator memuat peta

        Task.callInBackground(new Callable<Object>() {
            @Override
            public Object call() throws Exception {

                mListKomoHargaKomparator = mParseran.parseListKomparator(mListKomoditasHarga, myLocation,
                        modeUrutan);

                return null;
            }
        }).continueWith(new Continuation<Object, Object>() {
            @Override
            public Object then(Task<Object> task) throws Exception {

                if (mListKomoHargaKomparator != null && mListKomoHargaKomparator.size() > 0) {

                    Log.w("LOG PETA HARGA", "GET MAP ASYNC");
                    mapfragment.getMapAsync(mOnMapReadyCallback);

                } else {
                    //peta gagal dimuat
                    munculSnackbar(R.string.toastgagalpeta);
                }

                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }


    OnMapReadyCallback mOnMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {

            map = googleMap;
            isMapSiap = true;

            hitungSkalaAtasBawah();
            map.setPadding(0, paddingTop_px, 0, paddingBottom_px);
            map.getUiSettings().setCompassEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);

            setelPetaMarker();

            map.setOnMarkerClickListener(listenermarker);
        }
    };


    private void hitungSkalaAtasBawah() {
        final float scale = getResources().getDisplayMetrics().density;
        //padding atas
        paddingTop_px = (int) (paddingTop_dp * scale + 0.5f);
        //padding bawah
        paddingBottom_px = (int) (paddingBottom_dp * scale + 0.5f);
    }


    //SETEL POSISI SAYA
    public void setelPosisiSayaAwal() {
        try {
            if (myMarker != null) {
                myMarker.remove();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        Log.w("LOKASI", "lokasi saya peta " + myLatitude + " , " + myLongitude);

        myCoordinate = new LatLng(myLatitude, myLongitude);
        myCameraPosition = new CameraPosition.Builder()
                .target(myCoordinate)
                .zoom(16)
                .bearing(0)
                .tilt(0)
                .build();

        myMarker = map.addMarker(new MarkerOptions()
                .position(myCoordinate)
                .title("Posisi Saya")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_lokasi_saya)));

        map.moveCamera(CameraUpdateFactory.newCameraPosition(myCameraPosition));
        myMarker.showInfoWindow();
    }


    public void setelPosisiSayaMenu(Location location) {

        Log.w("LOG PETA HARGA", "SETEL POSISI SAYA MARKER");
        if (location != null) {
            myLocation = location;
            myLatitude = myLocation.getLatitude();
            myLongitude = myLocation.getLongitude();

            Log.w("LOKASI", "lokasi saya peta " + myLatitude + " , " + myLongitude);
            if (isMapSiap) {
                try {
                    setelPosisiSayaAwal();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public void setelPetaMarker() {

        Log.w("LOG PETA HARGA", "SETEL PETA MARKER");
        if (myLocation != null) {
            myLatitude = myLocation.getLatitude();
            myLongitude = myLocation.getLongitude();

            Log.w("LOKASI", "lokasi saya peta " + myLatitude + " , " + myLongitude);

        }

        if (isMapSiap) {

            if (mListKomoHargaKomparator != null && mListKomoHargaKomparator.size() > 0) {
                //setel ke marker
                setelMarkerSemua();

            } else {
                setelPosisiSayaAwal();
                teks_namakomoditas.setText("Nama tidak tersedia");
                teks_hargakomoditas.setText("Harga tidak tersedia");
                teks_alamatkomoditas.setText("Alamat tidak tersedia");
                teks_telponkomoditas.setText("Telepon tidak tersedia");
                teks_keterangan.setText("Tidak ada keterangan");
            }
        }

    }


    //TAMPILKAN KE DALAM MARKER

    /**
     * SETEL DAN PASANG MARKER SEMUA KE PETA *
     */
    private void setelMarkerSemua() {

        try {
            map.clear();
            setelPosisiSayaAwal();
            taskAmbilGeocoder(latitudeKomoditas, longitudeKomoditas);
            int panjangarray = mListKomoHargaKomparator.size();
            HargaKomoditasItemKomparator itemKomoditas;
            hashmapListHarga = new HashMap<>();

            for (int i = 0; i < panjangarray; i++) {
                itemKomoditas = mListKomoHargaKomparator.get(i);
                Marker markeradd = map.addMarker(setMarkerOptions(itemKomoditas));
                hashmapListHarga.put(markeradd, itemKomoditas);
            }

            btnNavigasi.setVisibility(View.VISIBLE);
            displayInfo(mListKomoHargaKomparator.get(0));

        } catch (Exception ex) {
            ex.printStackTrace();
            btnNavigasi.setVisibility(View.GONE);
        }
    }


    private MarkerOptions setMarkerOptions(HargaKomoditasItemKomparator itemKomoditas) {
        int icon;
        String type;
        switch (itemKomoditas.getType()) {
            case 1:
                type = "(J) ";
                icon = R.drawable.ic_sell;
                break;
            case 2:
                type = "(B) ";
                icon = R.drawable.ic_buy;
                break;
            default:
                type = "(P) ";
                icon = R.drawable.ic_pantau;
                break;
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(Double.valueOf(itemKomoditas.getLatitude()), Double.valueOf(itemKomoditas.getLongitude())));
        markerOptions.title(type + itemKomoditas.getBarang());
        markerOptions.icon(BitmapDescriptorFactory.fromResource(icon));
        return markerOptions;
    }

    //SETEL PILIHAN DARI FRAGMENT SEBELAH KE DALAM PETA
    public void setelMarkerSemuaPilihanKlik(int posisiklik) {

        try {
            map.clear();
            setelPosisiSayaAwal();
            taskAmbilGeocoder(latitudeKomoditas, longitudeKomoditas);
            HargaKomoditasItemKomparator itemKomoditas;
            hashmapListHarga = new HashMap<>();

            for (int i = 0; i < mListKomoHargaKomparator.size(); i++) {
                itemKomoditas = mListKomoHargaKomparator.get(i);
                Marker markeradd = map.addMarker(setMarkerOptions(itemKomoditas));
                if (posisiklik == i) {
                    latpeta = itemKomoditas.getLatitude();
                    longipeta = itemKomoditas.getLongitude();
                    posisikameraklik = new CameraPosition.Builder()
                            .target(markeradd.getPosition())
                            .zoom(16)
                            .bearing(0)
                            .tilt(0)
                            .build();

                    map.moveCamera(CameraUpdateFactory.newCameraPosition(posisikameraklik));
                    markerklik = markeradd;
                }
                hashmapListHarga.put(markeradd, itemKomoditas);
            }

            displayInfo(mListKomoHargaKomparator.get(posisiklik));
            btnNavigasi.setVisibility(View.VISIBLE);
            markerklik.showInfoWindow();
        } catch (Exception ex) {
            ex.printStackTrace();
            btnNavigasi.setVisibility(View.GONE);
        }
    }


    //LISTENER KLO MARKER DI KLIK
    GoogleMap.OnMarkerClickListener listenermarker = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            marker.showInfoWindow();
            //tampilkan keterangan marker
            try {
                displayInfo(hashmapListHarga.get(marker));
                taskAmbilGeocoder(latpeta, longipeta);
                btnNavigasi.setVisibility(View.VISIBLE);
            } catch (Exception ex) {
                ex.printStackTrace();
                btnNavigasi.setVisibility(View.GONE);
            }


            return true;
        }
    };


    View.OnClickListener listenerNavigasi = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String kordinatpetakirim = latitudeKomoditas + "," + longitudeKomoditas;
            String myCoordinate = myLatitude + "," + myLongitude;
            String alamatpeta = "http://maps.google.com/maps?saddr=" + myCoordinate + "&daddr=" + kordinatpetakirim + "&mode=driving";

            Log.w("ALAMAT PETA BUKA", "" + alamatpeta);
            Toast.makeText(FragmentPetaHarga.this.getActivity(), "Membuka Google Maps", Toast.LENGTH_SHORT).show();

            try {
                Intent intentpeta = new Intent(Intent.ACTION_VIEW);
                intentpeta.setData(Uri.parse(alamatpeta));
                intentpeta.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                FragmentPetaHarga.this.startActivity(intentpeta);

            } catch (Exception ex) {
                ex.printStackTrace();
                Intent intentpeta = new Intent(Intent.ACTION_VIEW);
                intentpeta.setData(Uri.parse(alamatpeta));
                FragmentPetaHarga.this.startActivity(intentpeta);
            }

        }
    };

    View.OnClickListener listenerTelepon = new View.OnClickListener() {
        public void onClick(View view) {
            if (teleponKomoditas.length() > 4) {
                Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + teleponKomoditas));
                try {
                    startActivity(in);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(FragmentPetaHarga.this.getActivity(), "Call Activity is not founded", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    View.OnClickListener listenerSms = new View.OnClickListener() {
        public void onClick(View view) {
            if (teleponKomoditas.length() > 4) {
                Intent in = new Intent(Intent.ACTION_VIEW);
                in.setType("vnd.android-dir/mms-sms");
                in.putExtra("address", teleponKomoditas);
                in.putExtra("sms_body", "#pantauharga.id");
                try {
                    startActivity(in);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(FragmentPetaHarga.this.getActivity(), "Call Activity is not founded", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    View.OnClickListener listenerRating = new View.OnClickListener() {

        public void onClick(View view) {
            if (teleponKomoditas.length() > 4) {
                try {
                    Intent myintent = new Intent(getActivity(), Rating.class);
                    myintent.putExtra("nama", namaKomoditas);
                    myintent.putExtra("alamat", alamatKomoditas);
                    myintent.putExtra("harga1", hargaKomoditas);
                    myintent.putExtra("telpon", teleponKomoditas);
                    myintent.putExtra("lat", latitudeKomoditas);
                    myintent.putExtra("lng", longitudeKomoditas);
                    myintent.putExtra("keterangan", keteranganKomoditas);
                    startActivity(myintent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(FragmentPetaHarga.this.getActivity(), "Rating error", Toast.LENGTH_SHORT).show();
                }
            }

        }

    };

    View.OnClickListener listenerShare = new View.OnClickListener() {
        public void onClick(View view) {
            GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {

                @Override
                public void onSnapshotReady(Bitmap snapshot) {
                    OutputStream fout = null;
                    String gText = "#pantauharga.id";
                    String filePath = System.currentTimeMillis() + ".jpeg";

                    try {

                        fout = getContext().openFileOutput(filePath,
                                getContext().MODE_WORLD_READABLE);

                        Bitmap bitmap = mark(snapshot);

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
                        fout.flush();
                        fout.close();
                    } catch (FileNotFoundException e) {
                        Log.d("ImageCapture", "FileNotFoundException");
                        Log.d("ImageCapture", e.getMessage());
                        filePath = "";
                    } catch (IOException e) {
                        Log.d("ImageCapture", "IOException");
                        Log.d("ImageCapture", e.getMessage());
                        filePath = "";
                    }

                    openShareImageDialog(filePath);
                }
            };

            map.snapshot(callback);

        }

    };

    public void openShareImageDialog(String filePath) {
        File file = getContext().getFileStreamPath(filePath);

        if (!filePath.equals("")) {
            final ContentValues values = new ContentValues(2);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            final Uri contentUriFile = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            String type;
            switch (typeKomoditas) {
                case 1:
                    type = "Jual ";
                    break;
                case 2:
                    type = "Beli ";
                    break;
                default:
                    type = "Pantau ";
                    break;
            }
            String message = "";

            message += type + namaKomoditas + '\n';
            message += "Harga per Kg " + formatHargaKomoditas + '\n';
            if (typeKomoditas == 1 || typeKomoditas == 2) {
                if (teleponKomoditas.length() > 8) {
                    message += "Hubungi " + teleponKomoditas + '\n';
                }
            }

            message += "#pantauharga.id";
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("image/jpeg");
            intent.putExtra(android.content.Intent.EXTRA_STREAM, contentUriFile);
            intent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(intent, "Share Image"));
        } else {
            //This is a custom class I use to show dialogs...simply replace this with whatever you want to show an error message, Toast, etc.
//            DialogUtilities.showOkDialogWithText(this, R.string.shareImageFailed);
        }
    }

    public Bitmap mark(Bitmap src) {
        int w = src.getWidth();
        int h = src.getHeight();

        Log.w("Bitmap", "Ukuran w x h : " + w + " x " + h);
        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.parseColor(("#AA000000")));

        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        Paint paint = new Paint();
        paint.setTextSize(50);
        paint.setAntiAlias(true);
        paint.setUnderlineText(false);

        //should draw background first,order is important
        int left = 1;
        int right = w - 1;
        int bottom = h - 1;
        int top = bottom - (h / 2);
        canvas.drawRect(left, top, right, bottom, bgPaint);
        Log.w("Bitmap", "Ukuran left, right, bottom, top : " + left + ", " + right + ", " + bottom + ", " + top);

        String type;
        switch (typeKomoditas) {
            case 1:
                type = "Jual ";
                break;
            case 2:
                type = "Beli ";
                break;
            default:
                type = "Pantau ";
                break;

        }

        paint.setColor(Color.RED);
        canvas.drawText(type + namaKomoditas, 20, top + 100, paint);
        paint.setColor(Color.WHITE);
        canvas.drawText("Harga per Kg " + formatHargaKomoditas, 20, top + 160, paint);
        if (typeKomoditas == 1 || typeKomoditas == 2) {
            if (teleponKomoditas.length() > 8) {
                canvas.drawText("Hubungi " + teleponKomoditas, 20, top + 220, paint);
            }
        }

        canvas.drawText("Date " + formatDate.format(lastUpdatedKomoditas), 20, top + 280, paint);
        canvas.drawText("#pantauharga.id", 20, top + 350, paint);
        return result;
    }

    //AMBIL GEOCODER PENGGUNA
    private void ambilGeocoderPengguna(String latitude, String longitude) {

        geocoderPengguna = new Geocoder(FragmentPetaHarga.this.getActivity(), Locale.getDefault());
        double dolatitu = 0;
        double dolongi = 0;

        try {
            dolatitu = Double.valueOf(latitude);
            dolongi = Double.valueOf(longitude);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {


            addressListPengguna = geocoderPengguna.getFromLocation(dolatitu, dolongi, 1);
            if (addressListPengguna.size() > 0) {

                int panjangalamat = addressListPengguna.get(0).getMaxAddressLineIndex();

                if (panjangalamat > 0) {

                    gecoder_alamat = addressListPengguna.get(0).getAddressLine(0);
                    gecoder_namakota = addressListPengguna.get(0).getLocality();

                } else {
                    gecoder_alamat = "";
                    gecoder_namakota = "";
                    alamatKomoditas = "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            gecoder_alamat = "";
            gecoder_namakota = "";
            alamatKomoditas = "";
        }

        Log.w("NAMA KOTA", "NAMA KOTA " + gecoder_alamat + " " + gecoder_namakota);

        if (gecoder_namakota != null && gecoder_namakota.length() > 0) {

            alamatKomoditas = gecoder_namakota;

            if (gecoder_alamat != null && gecoder_alamat.length() > 0) {

                alamatKomoditas = gecoder_alamat + ", " + gecoder_namakota;

            }
        } else {
            alamatKomoditas = "";
        }

    }


    //TASK AMBIL GEOCODER
    private void taskAmbilGeocoder(final String strlatitude, final String strlongitude) {

        Task.callInBackground(new Callable<Object>() {
            @Override
            public Object call() throws Exception {

                ambilGeocoderPengguna(strlatitude, strlongitude);

                return null;
            }
        }).continueWith(new Continuation<Object, Object>() {
            @Override
            public Object then(Task<Object> task) throws Exception {

                if (alamatKomoditas.length() > 4) {
                    Log.w("ALAMAT GABUNGAN TASK", "ALAMAT " + alamatKomoditas);
                    teks_alamatkomoditas.setText(alamatKomoditas);
                    teks_alamatkomoditas.setVisibility(View.VISIBLE);
                } else {
                    teks_alamatkomoditas.setText("-");
                    teks_alamatkomoditas.setVisibility(View.GONE);
                }

                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);


    }


    //MUNCUL SNACKBAR
    private void munculSnackbar(int resPesan) {

        Snackbar.make(teks_namakomoditas, resPesan, Snackbar.LENGTH_LONG).setAction("OK", listenersnackbar)
                .setActionTextColor(FragmentPetaHarga.this.getResources().getColor(R.color.kuning_indikator)).show();
    }

    View.OnClickListener listenersnackbar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };


}
