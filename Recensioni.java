import java.util.HashMap;
import java.util.Map;

public class Recensioni{
    String nameHotel;
    String citta;
    double rate = 0;
    Map<String, Integer> ratings = new HashMap<>();

    // Variabile per attualità recensione

    private long tempCreazione = System.currentTimeMillis();

    public Recensioni() {
    }

    public Recensioni(String nameHotel, String citta, double rate, Integer cleaning, Integer position, Integer services, Integer quality) {
        this.nameHotel = nameHotel;
        this.citta = citta;
        this.rate = rate;
        this.ratings.put("cleaning", cleaning);
        this.ratings.put("position", position);
        this.ratings.put("service", services);
        this.ratings.put("quality", quality);
    }

    public String getNameHotel(){
        return this.nameHotel;
    }
    public void setNameHotel(String nameHotel) {
        this.nameHotel = nameHotel;
    }

    public String getCitta(){
        return this.citta;
    }
    public void setCitta(String citta) {
        this.citta = citta;
    }

    public double getRate(){
        return this.rate;
    }
    public void setRate(double rate) {
        this.rate = rate;
    }

    public Map<String, Integer> getRatings() {
        return this.ratings;
    }

    public void setRatings(Integer cleaning, Integer position, Integer services, Integer quality) {
        this.ratings.put("cleaning", cleaning);
        this.ratings.put("position", position);
        this.ratings.put("service", services);
        this.ratings.put("quality", quality);
    }

    public long getTempCreazione(){
        return tempCreazione;
    }
}
