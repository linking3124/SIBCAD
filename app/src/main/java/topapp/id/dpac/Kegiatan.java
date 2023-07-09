package topapp.id.dpac;

/**
 * Created by topapp.id on 11/02/18.
 */

public class Kegiatan {
    private String foto;
    private String nama_keg;
    private String tanggal_keg;
    private String nama_keldesa;
    private String status;

    public Kegiatan (String foto, String nama_keg, String tanggal_keg, String nama_keldesa, String status){
        this.foto = foto;
        this.nama_keg = nama_keg;
        this.tanggal_keg = tanggal_keg;
        this.nama_keldesa = nama_keldesa;
        this.status = status;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNama_keg() {
        return nama_keg;
    }

    public String getNama_keldesa() {
        return nama_keldesa;
    }

    public void setNama_keldesa(String nama_keldesa) {
        this.nama_keldesa = nama_keldesa;
    }

    public void setNama_keg(String nama_keg) {
        this.nama_keg = nama_keg;
    }

    public String getTanggal_keg() {
        return tanggal_keg;
    }

    public void setTanggal_keg(String tanggal_keg) {
        this.tanggal_keg = tanggal_keg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
