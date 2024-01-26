public class Utente {
    private String username;

    private String password;

    int nRecensioni = 0;

    String badge = "nessun badge";

    Boolean loggato = false;

    public Utente() {
        // Costruttore vuoto richiesto per la deserializzazione JSON
    }

    public Utente(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Metodi getter e setter
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getBadge() {
        switch(nRecensioni/3) {
            case 0:
                badge = "Nessun Badge";
                return "Nessun Badge";
            case 1:
                badge = "Recensore";
                return "Recensore";
            case 2:
                badge = "Recensore Esperto";
                return "Recensore Esperto";
            case 3:
                badge = "Contributore";
                return "Contributore";
            case 4:
                badge = "Contributore Esperto";
                return "Contributore Esperto";
            default :
                badge = "Contributore Super";
                return "Contributore Super";
        }
    }
    public void setnRecensioni(int nRecensioni) {
        this.nRecensioni = nRecensioni;
    }
    public int getnRecensioni() {
        return nRecensioni;
    }

    public Boolean getLoggato() {
        return loggato;
    }
    public void setLoggato(Boolean loggato) {
        this.loggato = loggato;
    }
}
