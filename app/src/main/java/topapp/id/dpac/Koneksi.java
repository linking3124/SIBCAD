package topapp.id.dpac;

/**
 * Created by topapp.id on 10/02/18.
 */

public class Koneksi {
    public String getServer(){
        String isi = "http://192.168.1.5/"; //over wifi Rumah 1
        //String isi = "http://10.0.2.2"; //over emulator
        //String isi = "https://topapp.id/";

        return isi;
    }

    public String getUrl()
    {
        String isi = getServer()+"sibcad/API/"; //over online web
        //String isi = getServer()+"API/"; //over online web
        return isi;
    }

    public String getImagesDir(){
        String isi = getServer()+"sibcad/images/";
       //String isi = getServer()+"images/";
        return isi;
    }

}
