package id.pantauharga.android.modelgson;

import java.util.Date;

/**
 * Created by Gulajava Ministudio on 11/6/15.
 */
public class HargaKomoditasItem {

    private String barang = "";
    private String latitude = "";
    private String longitude = "";
    private String nohp = "";
    private int price = 0;
    private int type = 0 ;
    private String keterangan="";
    private String lastUpdated="";
    public HargaKomoditasItem() {
    }

    public String getBarang() {
        return barang;
    }

    public void setBarang(String barang) {
        this.barang = barang;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


    public String getNohp() {
        return nohp;
    }

    public void setNohp(String nohp) {
        this.nohp = nohp;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getLastUpdated() {
        return lastUpdated;  }

    public void setLastUpdated(String lastUpdated)
    {
        this.lastUpdated = lastUpdated;
    }
}


































