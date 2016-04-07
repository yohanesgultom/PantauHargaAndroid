package id.pantauharga.android.aktivitas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import android.support.v7.widget.Toolbar;
import bolts.Continuation;
import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;
import id.pantauharga.android.R;
import id.pantauharga.android.databases.RMLogin;
import id.pantauharga.android.databases.RMrating;
import id.pantauharga.android.dialogs.DialogOkKirimRating;
import id.pantauharga.android.dialogs.DialogPeringatanLoginDulu;
import id.pantauharga.android.internets.Apis;
import id.pantauharga.android.internets.JacksonRequest;
import id.pantauharga.android.internets.Volleys;
import id.pantauharga.android.modelgson.HargaKomoditasLapor;
import id.pantauharga.android.modelgsonkirim.HargaKomoditasKirim;
import id.pantauharga.android.parsers.Parseran;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by Toshiba on 29/03/2016.
 */
public class Rating extends AppCompatActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private ActionBar aksibar;
    //database
    private Realm mRealm;
    private RealmQuery<RMLogin> mRealmQueryLogin;
    private RealmResults<RMLogin> mRealmResultsLogin;
    private RealmQuery<RMrating> mRealmQueryRate;
    private RealmResults<RMrating> mRealmResultsRate;

    private String username = "";
    private ProgressDialog mProgressDialog;

    private RatingBar ratingBar;
    private TextView txtRatingValue;
    private Button btnSubmit;
    private boolean isProsesKirim = false;
    private boolean isAktJalan = true;
    private String dataKirim_hp = "";

    private String datasimpan_rating = "";
    private String datasimpan_id = "";
    private String dataKirim_rating;
    private String datasimpan_harga = "";
    private String datasimpan_lat = "";
    private String datasimpan_lng = "";
    private String datasimpan_nohp = "";
    private String datasimpan_quantity = "";
    private String datasimpan_keterangan = "";
    private String datakirim_nohp = "";
    private String datakirim_nohp1 = "0";
    private Parseran mParseran;
    private String idkomoditas ;
    private int hargakomoditas;
    private String jumlahkomoditas;
    private String latitude;
    private String longitude ;
    private String keterangan ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_rating);
        ButterKnife.bind(Rating.this);
        munculMenuAction(Rating.this);
        mRealm = Realm.getInstance(Rating.this);
        mParseran = new Parseran(Rating.this);
        if (toolbar != null) {
            Rating.this.setSupportActionBar(toolbar);
        }

        aksibar = Rating.this.getSupportActionBar();
        assert aksibar != null;
        aksibar.setDisplayHomeAsUpEnabled(true);
        aksibar.setTitle(R.string.lapor_rating);

        Bundle bundle = Rating.this.getIntent().getExtras();
        idkomoditas = bundle.getString("nama");
        latitude = bundle.getString("lat");
        longitude = bundle.getString("lng");
        datakirim_nohp = bundle.getString("telpon");
        hargakomoditas = bundle.getInt("harga1");
        keterangan = bundle.getString("keterangan");

        //  addListenerOnRatingBar();


        addListenerOnButton();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                Rating.this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addListenerOnButton() {

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        ambilDataRiwayat();
        //if click on me, then display the current rating value.
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                susunJsonKirimHarga();

            }

        });

    }


    private void tampilProgressDialog(String pesan) {

        mProgressDialog = new ProgressDialog(Rating.this);
        mProgressDialog.setMessage(pesan);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(listenerprogresbatal);
        mProgressDialog.show();

    }

    ProgressDialog.OnCancelListener listenerprogresbatal = new ProgressDialog.OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialogInterface) {

            isProsesKirim = false;
            Volleys.getInstance(Rating.this).cancelPendingRequestsNoTag();
        }
    };

    private void susunJsonKirimHarga() {


        //progress dialog
        tampilProgressDialog("Mengirim data laporan...");

        Task.callInBackground(new Callable<String>() {
            @Override
            public String call() throws Exception {

                //{"id": "1034","lat":"-6.217","lng":"106.9","nohp":08123123,"harga":"20000","quantity":"30"}
                HargaKomoditasKirim hargaKomoditasKirim6 = new HargaKomoditasKirim();
                hargaKomoditasKirim6.setId(String.valueOf(idkomoditas));
                hargaKomoditasKirim6.setLat(latitude);
                hargaKomoditasKirim6.setLng(longitude);
                hargaKomoditasKirim6.setNohp(datakirim_nohp);
                hargaKomoditasKirim6.setHarga(String.valueOf(hargakomoditas));
                hargaKomoditasKirim6.setQuantity(jumlahkomoditas);
                hargaKomoditasKirim6.setKeterangan(keterangan);
                hargaKomoditasKirim6.setInputer(datakirim_nohp1);
                hargaKomoditasKirim6.setRating(String.valueOf(ratingBar.getRating()));
                return mParseran.konversiPojoKirimHarga(hargaKomoditasKirim6);
            }
        })

                .continueWith(new Continuation<String, Object>() {
                    @Override
                    public Object then(Task<String> task) throws Exception {

                        String hasiljsons = task.getResult();
                        Log.w("HASIL PARSE CEK", "HASIL PARSE JSON CEK " + hasiljsons);

                        //Toast.makeText(Rating.this, hasiljsons, Toast.LENGTH_SHORT).show();
                        //kirim ke server
                        kirimDataServer(hasiljsons);

                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }

    private void ambilDataRiwayat() {

        mRealmQueryLogin = mRealm.where(RMLogin.class);
        mRealmResultsLogin = mRealmQueryLogin.findAll();

        if (mRealmResultsLogin.size() > 0) {
            RMLogin rmLogin = mRealmResultsLogin.first();
            datakirim_nohp1 = rmLogin.getNohp();
            username = rmLogin.getUsername();
        }
        if(datakirim_nohp1.length() == datakirim_nohp.length()){
            Toast.makeText(Rating.this, "Anda Tidak bisa rate user anda sendiri", Toast.LENGTH_SHORT).show();
            btnSubmit.setEnabled(false);
        }
            mRealmQueryRate = mRealm.where(RMrating.class).equalTo("nohp", datakirim_nohp).equalTo("id",idkomoditas);
            mRealmResultsRate = mRealmQueryRate.findAll();

            if (mRealmResultsRate.size() > 0) {

                RMrating rmDataRiwayat = mRealmResultsRate.first();
                idkomoditas = rmDataRiwayat.getId();
                dataKirim_hp = rmDataRiwayat.getNohp();
                dataKirim_rating = rmDataRiwayat.getRating();
                if (dataKirim_hp.length() > 0) {
                    btnSubmit.setEnabled(false);
                    ratingBar.setRating(Float.parseFloat(dataKirim_rating));
                    Toast.makeText(Rating.this, "Anda sudah rate user ini", Toast.LENGTH_SHORT).show();
                } else {
                    btnSubmit.setEnabled(true);
                }
            }

    }



    //KIRIM DATA KE SERVER
    private void kirimDataServer(String jsonbody) {

        String urls = Apis.getLinkRating();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> parameters = new HashMap<>();

        JacksonRequest<HargaKomoditasLapor> jacksonRequest = Apis.postRequestHargaLapor(
                urls,
                headers,
                parameters,
                jsonbody,
                new Response.Listener<HargaKomoditasLapor>() {
                    @Override
                    public void onResponse(HargaKomoditasLapor response) {

                        Log.w("SUKSES", "SUKSES");
                        if (isAktJalan) {
                            cekHasilRespon(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        error.printStackTrace();
                        Log.w("GAGAL", "GAGAL");
                        if (isAktJalan) {
                            cekHasilRespon(null);
                        }
                    }
                }
        );

        Volleys.getInstance(Rating.this).addToRequestQueue(jacksonRequest);
    }


    //CEK HASIL RESPON DARI VOLLEY
    private void cekHasilRespon(HargaKomoditasLapor hargaKomoditasLapor) {

        if (hargaKomoditasLapor != null) {

            datasimpan_id = hargaKomoditasLapor.getId();
            datasimpan_harga = hargaKomoditasLapor.getHarga() + "";
            datasimpan_lat = hargaKomoditasLapor.getLat() + "";
            datasimpan_lng = hargaKomoditasLapor.getLng() + "";
            datasimpan_nohp = hargaKomoditasLapor.getNohp();
            datasimpan_quantity = hargaKomoditasLapor.getQuantity() + "";
            datasimpan_keterangan = hargaKomoditasLapor.getKeterangan() + "";
            datasimpan_rating = hargaKomoditasLapor.getRating()+"";
            Log.w("HASIL HARGA", datasimpan_id + " harga " + datasimpan_harga + " jumlah "
                    + datasimpan_quantity + " lat " + datasimpan_lat + " lng " + datasimpan_lng + "rating");

            if (datakirim_nohp.length() > 3) {
                //simpan ke database riwayat
                  simpanDatabase(false, true, datasimpan_id, datasimpan_lat,
                          datasimpan_lng, datasimpan_nohp, datasimpan_harga, datasimpan_quantity, datasimpan_keterangan, datasimpan_rating);

            } else {
                //gagal kirim data laporan
                munculSnackbar(R.string.lapor_gagalkirimdata);
                isProsesKirim = false;
                mProgressDialog.dismiss();
            }
        } else {
            //gagal kirim data laporan
            if(datakirim_nohp1.length() < 3) {
                munculSnackbar(R.string.lapor_gagalkirimdata3);
                isProsesKirim = false;
                mProgressDialog.dismiss();
            }
            else{
                munculSnackbar(R.string.lapor_gagalkirimdata);
                isProsesKirim = false;
                mProgressDialog.dismiss();
            }
        }
    }
    //SIMPAN KE DALAM DATABASE
    private void simpanDatabase(boolean isDraft, boolean isKirim, String id,  String lats,
                                String lngs, String nohps,
                                String hargas, String quantitis, String keterangan, String rating) {


        RMrating rmDataRiwayat1 = new RMrating();
        rmDataRiwayat1.setId(id);
        rmDataRiwayat1.setLat(lats);
        rmDataRiwayat1.setLng(lngs);
        rmDataRiwayat1.setNohp(nohps);
        rmDataRiwayat1.setHarga(hargas);
        rmDataRiwayat1.setQuantity(quantitis);
        rmDataRiwayat1.setKeterangan(keterangan);
        rmDataRiwayat1.setRating(rating);
        mRealm.beginTransaction();
        mRealm.copyToRealm(rmDataRiwayat1);
        mRealm.commitTransaction();

        isProsesKirim = false;

        if (isKirim) {

            mProgressDialog.dismiss();
            //tampil dialog data telah dikirim
            tampilDialogBerhasil();
        } else {
            Toast.makeText(Rating.this, R.string.lapor_okkirimdraft, Toast.LENGTH_SHORT).show();
            Rating.this.finish();
        }

    }

    private void munculSnackbar(int resPesan) {

        Snackbar.make(toolbar, resPesan, Snackbar.LENGTH_LONG).setAction("OK", listenersnackbar)
                .setActionTextColor(Rating.this.getResources().getColor(R.color.kuning_indikator)).show();
    }

    View.OnClickListener listenersnackbar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };

    private void tampilDialogBerhasil() {

        DialogOkKirimRating dialogOkKirim = new DialogOkKirimRating();
        dialogOkKirim.setCancelable(false);

        FragmentTransaction fts = Rating.this.getSupportFragmentManager().beginTransaction();
        dialogOkKirim.show(fts, "dialog ok kirim");

    }

    //SET DIALOG OK TERKIRIM OK
    public void setOkTerkirim() {
        Rating.this.finish();
    }
    public void tampilDialogLoginDulu() {

        DialogPeringatanLoginDulu dialogPeringatanLoginDulu = new DialogPeringatanLoginDulu();
        FragmentTransaction fragmentTransaction = Rating.this.getSupportFragmentManager().beginTransaction();
        dialogPeringatanLoginDulu.setCancelable(false);
        dialogPeringatanLoginDulu.show(fragmentTransaction, "dialog login dulu deh");

    }
    //MENAMPILKAN MENU ACTION BAR
    private void munculMenuAction(Context context) {

        try {
            ViewConfiguration config = ViewConfiguration.get(context);
            Field menuKey = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");

            if (menuKey != null) {
                menuKey.setAccessible(true);
                menuKey.setBoolean(config, false);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

