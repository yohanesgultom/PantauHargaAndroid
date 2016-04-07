package id.pantauharga.android.databases;

import io.realm.RealmObject;

/**
 * Created by Toshiba on 01/04/2016.
 */
public class RMrating extends RealmObject {
    private String id;
    private String namakomoditas;
    private String lat;
    private String lng;
    private String alamatkomoditas;
    private String nohp;
    private String harga;
    private String quantity;
    private String keterangan;
    private String rating;
    public RMrating() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamakomoditas() {
        return namakomoditas;
    }

    public void setNamakomoditas(String namakomoditas) {
        this.namakomoditas = namakomoditas;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAlamatkomoditas() {
        return alamatkomoditas;
    }

    public void setAlamatkomoditas(String alamatkomoditas) {
        this.alamatkomoditas = alamatkomoditas;
    }

    public String getNohp() {
        return nohp;
    }

    public void setNohp(String nohp) {
        this.nohp = nohp;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

}
