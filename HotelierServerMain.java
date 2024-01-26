import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class HotelierServerMain {
    // Creazione delle variabili di proprietà
    private static int PORT;
    private static String indirizzoMulticast;
    private static int portaMulticast;
    private static String fileUtentiPath;
    private static String fileHotelPath;
    private static long intervalloAggiornamento;
    static {
        // Inizializzazione delle variabili di proprietà dal file di configurazione
        caricaProprieta();
    }
    // Strutture dati per utenti, ranking, citta ammesse e gli hotel
    private static List<Utente> utentiRegistrati = new ArrayList<>();        //Per conoscere gli utenti, non cuncurrent dato che solo il server la esegue
    private static ConcurrentHashMap<String, String> firstRanking = new ConcurrentHashMap<>();       // Per conoscere i primi classificati
    private static Set<String> cittaAmmesse = new HashSet<>();          // Set delle città gestite dal servizio
    // Caricamento della lista degli hotel dal file json in una lista con tipo Hotel tramite il metodo 'CaricamentoHotel'
    static ConcurrentLinkedQueue<Hotel> listHotel = CaricamentoHotel(fileHotelPath);
    
    public static void main(String[] args) {
        // Visualizzazione delle proprietà caricate
        System.out.println("PORT: "+ PORT);
        System.out.println("fileUtentiPath: "+ fileUtentiPath);
        System.out.println("fileHotelPath: "+ fileHotelPath);
        System.out.println("indirizzoMulticast: "+ indirizzoMulticast);
        System.out.println("portaMulticast: "+ portaMulticast);
        System.out.println("intervalloAggiornamento: "+ intervalloAggiornamento);
        // Inizializzazione del file utente e delle città ammesse
        inizializzaFileUtenti();    // Inizializzazione file Utenti.json
        inizializzaCittaAmmesse();      // Inizializza il set di città ammesse

        // Eseguo un thread con un timer per il ricalcolo del ranking e il salvataggio degli hotel e degli utenti
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                for(String citta: cittaAmmesse) {
                    listHotelRanking(citta);
                }
                salvaHotelSuFile();
                salvaUtentiSuFile();
            }
        };
        timer.scheduleAtFixedRate(task, 0, intervalloAggiornamento);

        // Threadpool per gestire le richieste dei client in modo concorrente
        ExecutorService threadPool = Executors.newCachedThreadPool();

        // Qui si gestiscono le richieste dei client mandandole come task nel threadpool
        // Creiamo il ServerSocket e ci mettimo in ascolto sulla porta indicata da PORT
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server in ascolto sulla porta " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Runnable user = new RichiesteClient(clientSocket);
                threadPool.execute(user);
            }
        } catch (IOException e) {e.printStackTrace();}

        finally {
            //Disconnetto tutti gli utenti alla chiusura del server
            for(Utente utente: utentiRegistrati) {
                logOut(utente.getUsername());
            }
        }
    }

    private static void caricaProprieta() {
        Properties prop = new Properties();
        try (FileInputStream input = new FileInputStream("config.inputServer")) {
            // Carica il file di configurazione
            prop.load(input);
        } catch (IOException e) {e.printStackTrace();}
        // Caricamento Proprietà nelle variabili statiche
        PORT = Integer.parseInt(prop.getProperty("PORT"));
        indirizzoMulticast = prop.getProperty("indirizzoMulticast");
        portaMulticast = Integer.parseInt(prop.getProperty("portaMulticast"));
        fileUtentiPath = prop.getProperty("fileUtentiPath");
        fileHotelPath = prop.getProperty("fileHotelPath");
        intervalloAggiornamento = Integer.parseInt(prop.getProperty("intervalloAggiornamento"));
    }

    private static void inizializzaCittaAmmesse() {
        // Inizializza il set delle città ammesse basandosi sugli hotel caricati
        for(Hotel hotel: listHotel) {
            if(!cittaAmmesse.contains(hotel.getCity())) {
                cittaAmmesse.add(hotel.getCity());
            }
        }
    }

    private static ConcurrentLinkedQueue<Hotel> CaricamentoHotel(String filePath) {
        // Carica la lista degli hotel dal file json specificato
        ConcurrentLinkedQueue<Hotel> listHotel =  new ConcurrentLinkedQueue<>();
        try {
            Reader reader = new FileReader(filePath);
            // Leggi il file JSON e converti in una lista di Hotel
            Type hotelListType = new TypeToken<ConcurrentLinkedQueue<Hotel>>(){}.getType();     // Definizione del tipo di
            listHotel = new Gson().fromJson(reader, hotelListType);
        } catch(FileNotFoundException e) {e.printStackTrace();}
        // Restituzione della lista degli hotel
        return listHotel;
    }

    private static void inizializzaFileUtenti() {
        // inizializzazione del file degli utenti, creandone uno se non esiste
        File fileUtenti = new File(fileUtentiPath);

        if (!fileUtenti.exists()) {
            try {
                fileUtenti.createNewFile();
                /*Gson gson = new Gson();
                String jsonUtenti = gson.toJson(fileUtenti);
                
                // Scrivi il JSON nel file
                try (PrintWriter writer = new PrintWriter(fileUtenti)) {
                    writer.write(jsonUtenti);
                    writer.close();
                } catch (FileNotFoundException e) {e.printStackTrace();}
                */
                System.out.println("File utenti creato con successo");
            } catch (IOException e) {e.printStackTrace();}
        }
        else {
            // Chiamata alla funzione di caricamento degli utenti registrati
            utentiRegistrati = caricaUtentiDaFile();
        }
    }

    private static List<Utente> caricaUtentiDaFile() {
        File fileUtenti = new File(fileUtentiPath);
        if (fileUtenti.exists()) {
            try {
            Reader reader = new FileReader(fileUtenti);
            Type utentiListType = new TypeToken<List<Utente>>(){}.getType();     // Definizione del tipo di Utente
            List<Utente> tempListUtenti = new Gson().fromJson(reader, utentiListType);      // Variabile di appoggio per sloggare tutti gli utenti
            for(Utente utente: tempListUtenti) {
                utente.setLoggato(false);       //sloggo tutti gli utenti per prevenire errori
            }
            // Restituisce la lista degli utenti
            return tempListUtenti;
        } catch(FileNotFoundException e) {e.printStackTrace();}
        }
        // Restituisce l'inizializzazione di una nuova lista vuota
        return new ArrayList<>();
    }

    private static void salvaUtentiSuFile() {
        Gson gson = new Gson();
        String jsonUtenti = gson.toJson(utentiRegistrati);
        // Scrivi il JSON nel file
        try (PrintWriter writer = new PrintWriter(fileUtentiPath)) {
            writer.write(jsonUtenti);
            writer.close();
        } catch (FileNotFoundException e) {e.printStackTrace();}
    }

    private static void salvaHotelSuFile() {
        Gson gson = new Gson();
        String jsonHotel = gson.toJson(listHotel);
        // Scrivi il JSON nel file
        try (PrintWriter writer = new PrintWriter("Hotels.json")) {
            writer.write(jsonHotel);
            writer.close();
        } catch (FileNotFoundException e) {e.printStackTrace();}
    }

    // Aggiunta della fase di registrazione
    public static int registrazioneUtente(String username, String password) {
        if(password.isEmpty()){
            System.out.println("Password mancante");
            return 1;       //Codice password mancante
        }
        if(utentiRegistrati != null) {
            // Verifica se l'utente è già registrato
            for (Utente utente : utentiRegistrati) {
                if (utente.getUsername().equals(username)) {
                    System.out.println("Utente già registrato");
                    return 2;       //Codice username già in uso
                }
            }
        }
        else{
            utentiRegistrati = new ArrayList<Utente>();     //Inizializza array list
        }
        // Registra l'utente
        utentiRegistrati.add(new Utente(username,password));
        System.out.println("Utente registrato con successo");
        return 3;        //Codice registrazione avvenuta
    }

    // Aggiunta della fase di login
    public static boolean loginUtente(String username, String password) {
        if (utentiRegistrati == null) {
            System.out.println("Utente non trovato");
            return false;
        }
        for (Utente utente: utentiRegistrati) {
            if (utente.getUsername().equals(username) && utente.getPassword().equals(password)) {
                if(utente.getLoggato()) {
                    System.out.println("Login già effettuato");
                    return false;
                }
                utente.setLoggato(true);
                System.out.println("Login effettuato con successo");
                return true;
            }
        }
    
        // Utente non trovato o password errata
        System.out.println("Login fallito");
        return false;
    }

    // Funzione che restituisce il badge associato
    public static String showMyBadges(String username){
        for(Utente utente: utentiRegistrati){
            if(utente.getUsername().equals(username) && utente.getLoggato()) {
                return utente.getBadge();
            }
        }
        return "Utente non trovato";
    }

    public static String searchHotel(String nameHotel, String citta){
        for(Hotel hotel: listHotel){
            if(hotel.getName().equals(nameHotel) && hotel.getCity().equals(citta)){
                return hotel.toString1();
            }
        }
        return "Hotel non trovato";
    }

    public static String logOut(String username){
        for(Utente utente: utentiRegistrati){
            if(utente.getUsername().equals(username) && utente.getLoggato()){
                utente.setLoggato(false);
                System.out.println("Utente " + username + " è stato disconnesso");
                return "Disconnessione avvenuta con successo";
            }
        }
        return "Errore nella disconnessione dell'utente";
    }

    public static String recensione(String username, String nameHotel, String citta, int rate, int cleaning, int position, int services, int quality) {
        // Variabili per il calcolo della media delle recensioni di un hotel e delle varie categorie
        int sommmaTot = 0;
        int sommaClean = 0;
        int sommaPosi = 0;
        int sommaServ = 0;
        int sommaQuali = 0;
        // Ricerca dellìhotel specifico
        for(Hotel hotel: listHotel) {
            if(hotel.getName().equals(nameHotel) && hotel.getCity().equals(citta)) {
                // Creo recensione
                Recensioni recensione = new Recensioni(nameHotel, citta, rate, cleaning, position, services, quality);

                hotel.setRecensioni(recensione);        // Aggiunge la recensione all'hotel
                hotel.setnRecensioni(hotel.getnRecensioni()+1);     // Aumento il numero delle recensioni effettuate a quell'hotel

                System.out.println(hotel.getRecensioni().toString());
                for(Recensioni recensioni: hotel.getRecensioni()){
                    sommmaTot += recensioni.getRate();
                    sommaClean += recensioni.getRatings().get("cleaning");
                    sommaPosi += recensioni.getRatings().get("position");
                    sommaServ += recensioni.getRatings().get("service");
                    sommaQuali += recensioni.getRatings().get("quality");
                }

                // Nuovi voti generali dell'Hotel e aggiornamento
                int numRecensioni = hotel.getnRecensioni();
                hotel.setRate((double)sommmaTot/numRecensioni);
                Map<String,Double> mediaRatings = new HashMap<>();

                mediaRatings.put("cleaning", (Math.round(((double)sommaClean/(double)numRecensioni *10.0)) / 10.0));
                mediaRatings.put("position", (Math.round(((double)sommaPosi/(double)numRecensioni *10.0)) / 10.0));
                mediaRatings.put("service", (Math.round(((double)sommaServ/(double)numRecensioni *10.0)) / 10.0));
                mediaRatings.put("quality", (Math.round(((double)sommaQuali/(double)numRecensioni *10.0)) / 10.0));
                hotel.setRatings(mediaRatings);

                // Aggiorno il numero di recensioni dell'utente
                for(Utente utente: utentiRegistrati) {
                    if (utente.getUsername().equals(username)) {
                        utente.setnRecensioni(utente.getnRecensioni()+1);
                        utente.getBadge(); // Ricalcolo del badge associato all'utente
                    }
                }
                // Ricalcolo del ranking di quella città
                listHotelRanking(citta);
                // Salva sul file json le modifiche effettuate
                salvaHotelSuFile();
                // Restituzione del messaggio
                return "Recensione salvata con successo";
            }
        }
        // Restituzione del messaggio in caso di errore
        return "Recensione non salvata";
    }

    public static Boolean loggato(String username) {
        for(Utente utente: utentiRegistrati) {
            if(utente.getUsername().equals(username) && utente.getLoggato()) {
                return true;
            }
        }
        return false;
    }

    //Funzione che crea lista di città locali
    public static List<Hotel> localListRanking(String citta){
        List<Hotel> listLocalHotel = new ArrayList<>();
        for(Hotel hotel: listHotel) {
            if(hotel.getCity().equals(citta)){
                listLocalHotel.add(hotel);
            }
        }
        return listLocalHotel;
    }
    
    //Funzione che calcola il punteggio e ordina la lista
    public static List<Hotel> listHotelRanking(String citta) {
        if(!cittaAmmesse.contains(citta)) {
            List<Hotel> error = new ArrayList<>();
            return error;
        }
        List<Hotel> listLocalHotel = localListRanking(citta);
        for(Hotel hotel: listLocalHotel) {
            double somma = 0;
            for(Recensioni rec: hotel.getRecensioni()) {
                double diffTempo = (double)1/(double)((int)System.currentTimeMillis() - (int)rec.getTempCreazione()*10);
                somma += rec.getRate()*(diffTempo/(hotel.getnRecensioni()*10));       //Somma rate in base all'attualità della recensione, maggiore è il numero più attuale
            }
            // Imposto il punteggio relativo all'hotel in base ad numero di recensioni attualità e qualità delle recensioni e in base alla media di generale dell'hotel
            hotel.setPunteggio(hotel.getnRecensioni()*0.2 + somma*0.6 + hotel.getRate()*0.2);
        }
        Collections.sort(listLocalHotel);

        // Scrivo la posizione nel ranking negli hotel
        int i = 1;
        for(Hotel hotel: listLocalHotel) {
            hotel.setPosizioneRanking(i);
            i++;
        }
        // Invio notifiche
        Hotel first = listLocalHotel.get(0);        // Prendo il primo classificato
        if(firstRanking.containsKey(citta)) {
            // Devo inviare la notifica del cambiamento del primo classificato
            if(!first.getName().equals(firstRanking.get(citta))) {
                // Invio nella notifica
                firstRanking.put(citta, first.getName());       // Aggiungo alla map il primo classificato
                invioNotifiche("Primo classificato nel ranking di: " + citta +" cambiato, Ora è: " + first.getName());
            }
        }
        else{
            firstRanking.put(citta, first.getName());       // Aggiungo alla map il primo classificato
        }
        return listLocalHotel;
    }

    public static void invioNotifiche(String notifica) {
        try {
            InetAddress group = InetAddress.getByName(indirizzoMulticast);
            MulticastSocket socket = new MulticastSocket(portaMulticast);

            // Uniscito al gruppo di multicast
            socket.joinGroup(group);

            // Invio dei messaggi su multicast
            byte[] data = notifica.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, group, portaMulticast);
            socket.send(packet);

            // Esco dal gruppo di multicast
            socket.leaveGroup(group);
            socket.close();
        } catch (Exception e) {e.printStackTrace();}
    }
}
