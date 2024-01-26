import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Hotel implements Comparable<Hotel>{
    private int id;
    private String name;
    private String description;
    private String city;
    private String phone;
    private List<String> services;
    private Double rate;
    private Map<String, Double> ratings;
    private Double punteggio;

    // Recensioni
    private int nRecensioni = 0;

    private List<Recensioni> recensioni = new ArrayList<>();

    private int posizioneRanking;

    // Costruttore vuoto richiesto per la deserializzazione JSON
    public Hotel() {
    }

    // Getter e setter per ogni campo
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getServices() {
        return services;
    }
    public void setServices(List<String> services) {
        this.services = services;
    }

    public double getRate() {
        return rate;
    }
    public void setRate(double rate) {
        this.rate = Math.round(rate * 10.0) / 10.0;
    }

    public Map<String, Double> getRatings() {
        return ratings;
    }
    public void setRatings(Map<String, Double> ratings) {
        this.ratings = ratings;
    }

    public String toString1(){
        String info = "-----INFO------\n"+"Nome: " + getName() +"\nDescrizione: "+ getDescription() + "\nCity: "+getCity()+"\nPhone: "+getPhone()+"\nServices: "+getServices()+"\nRate: "+getRate()+"\nRatings: "+getRatings();
        return info;
    }

    // Metodo get e set per ottenere numero di recensioni
    public int getnRecensioni() {
        return nRecensioni;
    }
    public void setnRecensioni(int nRecensioni) {
        this.nRecensioni = nRecensioni;
    }

    // punteggio asseganto dall'algoritmo di ranking
    public Double getPunteggio() {
        return punteggio;
    }
    public void setPunteggio(Double punteggio){
        this.punteggio = punteggio;
    }

    public List<Recensioni> getRecensioni() {
        return recensioni;
    }
    public void setRecensioni(Recensioni recensione) {
        this.recensioni.add(recensione);
    }

    //Funzione di comparazione
    public int compareTo(Hotel altro) {
        // Ordina in modo decrescente in base al punteggio
        return Double.compare(altro.getPunteggio(), this.getPunteggio());
    }

    public int getPosizioneRanking() {
        return posizioneRanking;
    }
    public void setPosizioneRanking(int i) {
        this.posizioneRanking = i;
    }
}
