package id.pantauharga.android.internets;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;

import java.util.List;
import java.util.Map;

import id.pantauharga.android.Konstan;
import id.pantauharga.android.modelgson.HargaKomoditasItem;
import id.pantauharga.android.modelgson.HargaKomoditasLapor;
import id.pantauharga.android.modelgson.User;

/**
 * Created by Gulajava Ministudio on 11/6/15.
 */
public class Apis {

    public static int JUMLAH_TIMEOUT = 60000;
    public static int JUMLAH_COBA = 1;
    public static float PENGALI_TIMEOUT = 1;

    public Apis() {
    }


    /**
     * AMBIL LINK
     **/

    //AMBIL HARGA KOMODITAS TERDEKAT
    ///Api/hargaall.json
    public static String getLinkHargaKomoditas() {
        return Konstan.ALAMATSERVER + "/Api/hargaall.json";
    }//rating

    public static String getLinkRating() {
        return Konstan.ALAMATSERVER + "/Api/rating.json";
    }

    //LAPORKAN HARGA KOMODITAS
    ///Api/input.json
    public static String getLinkLaporHargaKomoditas() {
        return Konstan.ALAMATSERVER + "/Api/input.json";
    }

    public static String getLinkPesanKomoditas() {
        return Konstan.ALAMATSERVER + "/Api/inputRequest.json";
    }

    public static String getLinkDeleteRiwayat() {
        return Konstan.ALAMATSERVER + "/Api/deleteInput.json";
    }

    //AMBIL DAFTAR KOMODITAS
    ///Api/comodityall.json
    public static String getLinkDaftarKomoditas() {
        return Konstan.ALAMATSERVER + "/Api/comodityall.json";
    }

    public static String getLinkUpdateUser() {
        return Konstan.ALAMATSERVER + "/Api/updateUser.json";
    }

    public static String getLinkVerifyUser() {
        return Konstan.ALAMATSERVER + "/Api/google.json";
    }


    private static DefaultRetryPolicy getRetryPolicy() {

        return new DefaultRetryPolicy(JUMLAH_TIMEOUT, JUMLAH_COBA, PENGALI_TIMEOUT);
    }

    /**
     * AMBIL REQUEST VOLLEY UNTUK KIRIM KE SERVERS
     **/

    //AMBIL HARGA KOMODITAS TERDEKAT
    public static JacksonRequestArray<HargaKomoditasItem> postRequestHargaKomoditasSekitars(
            String urls,
            Map<String, String> headers,
            Map<String, String> params,
            String jsonbodystr,
            Response.Listener<List<HargaKomoditasItem>> listenerok,
            Response.ErrorListener listenergagal
    ) {

        headers.put(Konstan.TAG_HEADERCONTENTIPE, Konstan.HEADER_JSONTYPE);

        JacksonRequestArray<HargaKomoditasItem> jacksonRequestArray = new JacksonRequestArray<>(
                Request.Method.POST,
                urls,
                HargaKomoditasItem.class,
                headers,
                params,
                jsonbodystr,
                listenerok,
                listenergagal
        );

        jacksonRequestArray.setRetryPolicy(getRetryPolicy());

        return jacksonRequestArray;
    }


    //LAPORKAN HARGA KOMODITAS
    public static JacksonRequest<HargaKomoditasLapor> postRequestHargaLapor(
            String urls,
            Map<String, String> headers,
            Map<String, String> params,
            String jsonbodystr,
            Response.Listener<HargaKomoditasLapor> listenerok,
            Response.ErrorListener listenergagal
    ) {

        headers.put(Konstan.TAG_HEADERCONTENTIPE, Konstan.HEADER_JSONTYPE);

        JacksonRequest<HargaKomoditasLapor> jacksonRequest = new JacksonRequest<>(
                Request.Method.POST,
                urls,
                HargaKomoditasLapor.class,
                headers,
                params,
                jsonbodystr,
                listenerok,
                listenergagal
        );

        jacksonRequest.setRetryPolicy(getRetryPolicy());

        return jacksonRequest;
    }


    //AMBIL DAFTAR KOMODITAS
    public static StrRekuestGet getRequestDaftarKomoditas(String urls, Response.Listener<String> listenerok,
                                                          Response.ErrorListener listenergagal) {

        StrRekuestGet strRekuestGet = new StrRekuestGet(
                Request.Method.GET,
                urls,
                null,
                listenerok,
                listenergagal
        );

        strRekuestGet.setRetryPolicy(getRetryPolicy());

        return strRekuestGet;
    }

    //UPDATE USER
    public static JacksonRequest<User> postRequestUpdateUser(
            String urls,
            Map<String, String> headers,
            Map<String, String> params,
            String jsonbodystr,
            Response.Listener<User> listenerok,
            Response.ErrorListener listenergagal
    ) {

        headers.put(Konstan.TAG_HEADERCONTENTIPE, Konstan.HEADER_JSONTYPE);

        JacksonRequest<User> jacksonRequest = new JacksonRequest<>(
                Request.Method.POST,
                urls,
                User.class,
                headers,
                params,
                jsonbodystr,
                listenerok,
                listenergagal
        );

        jacksonRequest.setRetryPolicy(getRetryPolicy());

        return jacksonRequest;
    }

    //AMBIL DAFTAR KOMODITAS
    public static StrRekuestGet getRequestGoogle(String urls, Response.Listener<String> listenerok,
                                                          Response.ErrorListener listenergagal) {

        StrRekuestGet strRekuestGet = new StrRekuestGet(
                Request.Method.GET,
                urls,
                null,
                listenerok,
                listenergagal
        );

        strRekuestGet.setRetryPolicy(getRetryPolicy());

        return strRekuestGet;
    }
}
