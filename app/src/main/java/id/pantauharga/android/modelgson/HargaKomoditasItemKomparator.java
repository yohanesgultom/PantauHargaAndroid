package id.pantauharga.android.modelgson;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by Gulajava Ministudio on 11/6/15.
 */
public class HargaKomoditasItemKomparator {

    private String barang = "";
    private String latitude = "";
    private String longitude = "";
    private String nohp = "";
    private int price = 0;
    private String jaraklokasi = "";
    private String description = "";
    private Date lastUpdated = new Date();

    public HargaKomoditasItemKomparator() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getJaraklokasi() {
        return jaraklokasi;
    }

    public void setJaraklokasi(String jaraklokasi) {
        this.jaraklokasi = jaraklokasi;
    }

    //KOMPARATOR BERDASARKAN LOKASI TERDEKAT
    public static Comparator<HargaKomoditasItemKomparator> komparatorJarakTerdekat = new Comparator<HargaKomoditasItemKomparator>() {
        @Override
        public int compare(HargaKomoditasItemKomparator hargaKomoditasItem1, HargaKomoditasItemKomparator hargaKomoditasItem2) {


            double dojarak1 = Double.valueOf(hargaKomoditasItem1.getJaraklokasi());
            double dojarak2 = Double.valueOf(hargaKomoditasItem2.getJaraklokasi());

            int intjarak1 = (int) dojarak1;
            int intjarak2 = (int) dojarak2;

            return intjarak1 - intjarak2;
        }
    };


    //KOMPARATOR BERDASARKAN HARGA TERMURAH
    public static Comparator<HargaKomoditasItemKomparator> komparatorHargaMurah = new Comparator<HargaKomoditasItemKomparator>() {
        @Override
        public int compare(HargaKomoditasItemKomparator hargaKomoditasItem1, HargaKomoditasItemKomparator hargaKomoditasItem2) {

            int intharga1 = hargaKomoditasItem1.getPrice();
            int intharga2 = hargaKomoditasItem2.getPrice();

            return intharga1 - intharga2;
        }
    };

    //KOMPARATOR BERDASARKAN HARGA TERMAHAL
    public static Comparator<HargaKomoditasItemKomparator> komparatorHargaMahal = new Comparator<HargaKomoditasItemKomparator>() {
        @Override
        public int compare(HargaKomoditasItemKomparator hargaKomoditasItem1, HargaKomoditasItemKomparator hargaKomoditasItem2) {

            int intharga1 = hargaKomoditasItem1.getPrice();
            int intharga2 = hargaKomoditasItem2.getPrice();

            return intharga2 - intharga1;
        }
    };


}


































