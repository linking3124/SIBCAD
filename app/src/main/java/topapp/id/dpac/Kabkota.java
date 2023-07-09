package topapp.id.dpac;

/**
 * Created by topapp.id on 02/03/18.
 */

public class Kabkota {
    private String id_kabkota;
    private String nama;

    public Kabkota() {
    }

    public Kabkota(String id_kabkota, String nama) {
        this.id_kabkota = id_kabkota;
        this.nama = nama;
    }

    public void setId_kabkota(String id_kabkota) {
        this.id_kabkota = id_kabkota;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getId_kabkota() {

        return id_kabkota;
    }

    public String getNama() {
        return nama;
    }
}
