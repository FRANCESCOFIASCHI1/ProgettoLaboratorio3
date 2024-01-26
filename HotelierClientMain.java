import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Properties;
import java.util.Scanner;


public class HotelierClientMain {
    // Dichiarazioni delle variabili di proprietà
    private static String SERVER_HOST;
    private static int SERVER_PORT;
    private static int portaMulticast;
    private static String indirizzoMulticast;
    private static MulticastSocket socket;
    static {
        // Inizializzazione variabili di proprietà
        caricaProprieta();
    }
    
    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);      // Connessione con il server
            Scanner serverInput = new Scanner(socket.getInputStream());     // Input del server
            PrintStream serverOutput = new PrintStream(socket.getOutputStream());       // Outuput del server
            Scanner userInput = new Scanner(System.in)) {       // Input da tastiera
            while(true) {
                // Stampa del Menu
                for (int i=0; i<10; i++){
                    System.out.println(serverInput.nextLine());
                }
                int scelta;
                // Loop per il controllo dell'inserimento di un numero corretto
                while(true) {
                    try{      // Scelta del menu
                        System.out.print("Inserire numero del menu: ");
                        scelta = userInput.nextInt();
                        break;
                    }catch (InputMismatchException e){
                        userInput.nextLine();
                        System.out.println("ERRORE: dati inseriti non validi!!\n");
                    }
                }
                switch (scelta) {
                    case 0: {
                        // Login Utente
                        loginUtente(serverOutput, serverInput, userInput);
                        break;
                    }
                    case 1: {
                        // Registrazione dell'utente
                        registrazioneUtente(serverOutput, serverInput, userInput);
                        break;
                    }
                    case 2:
                        // Mostra i badge dell'utente
                        mostraBadge(serverOutput, serverInput, userInput);
                        break;
                    case 3:
                        // Ricerca e restituisce i dati dell'hotel ricercato di una città
                        cercaHotel(serverOutput, serverInput, userInput);
                        break;
                    case 4:
                        // Ricerca tutti gli hotel in una citta
                        cercaTuttiHotel(serverOutput, serverInput, userInput);
                        break;
                    case 5:
                        // Scrive una recensione
                        recensioni(serverOutput, serverInput, userInput);
                        break;
                    case 6:
                        // Logout dal login
                        logout(serverOutput, serverInput, userInput);
                        break;
                    case 9:
                        // Exit, terminazione del socket
                        serverOutput.println("EXIT");
                        serverInput.close();
                        serverOutput.close();
                        userInput.close();
                        socket.close();
                        System.exit(0);
                    default:
                        // Reinserire il valore
                        serverOutput.println("VALORE NULLO");
                        System.out.println("INSERIRE UN VALORE VALIDO");
                        break;
                }
            }
        } catch (IOException e) {e.printStackTrace();}
    }

    private static void caricaProprieta() {
        Properties prop = new Properties();
        try (FileInputStream input = new FileInputStream("config.inputClient")) {
            // Carica il file di configurazione
            prop.load(input);
        } catch (IOException e) {e.printStackTrace();}

        // Setta le variabili statiche
        SERVER_PORT = Integer.parseInt(prop.getProperty("SERVER_PORT"));
        SERVER_HOST = prop.getProperty("SERVER_HOST");
        portaMulticast = Integer.parseInt(prop.getProperty("portaMulticast"));
        indirizzoMulticast = prop.getProperty("indirizzoMulticast");
    }

    private static void registrazioneUtente(PrintStream serverOutput, Scanner serverInput, Scanner userInput) {
        System.out.println("Registrazione Utente");
        userInput.nextLine();
        System.out.print("Username: ");
        String username = userInput.nextLine();
        System.out.print("Password: ");
        String password = userInput.nextLine();

        // Invia la richiesta di registrazione al server
        serverOutput.println("REGISTRAZIONE");
        serverOutput.println(username);
        serverOutput.println(password);

        // Leggi la risposta del server
        String response = serverInput.nextLine();
        if ("REGISTRAZIONE_SUCCESSO".equals(response)) {
            System.out.println("Registrazione effettuata con successo");
        } else {
            if("REGISTRAZIONE_FALLITA".equals(response))
                System.out.println("Registrazione fallita: Utente già registrato");
            else{
                System.out.println("Registrazione fallita: Password mancante");
            }
        }
    }

    private static void loginUtente(PrintStream serverOutput, Scanner serverInput, Scanner userInput) throws IOException {
        System.out.println("Login Utente");
        userInput.nextLine();
        System.out.print("Username: ");
        String username = userInput.nextLine();
        System.out.print("Password: ");
        String password = userInput.nextLine();

        // Invia la richiesta di login al server
        serverOutput.println("LOGIN");
        serverOutput.println(username);
        serverOutput.println(password);

        // Leggi la risposta del server
        String response = serverInput.nextLine();
        if ("LOGIN_SUCCESSO".equals(response)) {
            System.out.println("Login effettuato con successo");
            iscrizioneMulticast();
        } else {
            System.out.println("Login fallita :(");
        }
    }

    private static void mostraBadge(PrintStream serverOutput, Scanner serverInput, Scanner userInput) throws IOException {
        System.out.println("Mostro badge");
        userInput.nextLine();
        // Invia la richiesta di login al server
        serverOutput.println("BADGE");

        // Leggi la risposta del server
        String response = serverInput.nextLine();
        if ("LOGFAIL".equals(response)) {
            System.out.println("Devi essere loggato");
        } else {
            System.out.println(response);
        }
    }

    private static void cercaHotel(PrintStream serverOutput, Scanner serverInput, Scanner userInput){
        System.out.println("Cerca Hotel  specifico");
        userInput.nextLine();
        System.out.print("Nome hotel: ");
        String nameHotel = userInput.nextLine();
        System.out.print("Città: ");
        String citta = userInput.nextLine();

        // Invia la richiesta di registrazione al server
        serverOutput.println("SEARCH");
        serverOutput.println(nameHotel);
        serverOutput.println(citta);

        // Leggi la risposta del server
        
        
        for(int i=0; i<8; i++){
            String response = serverInput.nextLine();
            if(response.equals("Hotel non trovato")){
                System.out.println(response);
                break;
            }
            System.out.println(response);
        }
    }

    private static void logout(PrintStream serverOutput, Scanner serverInput, Scanner userInput){
        // Leggo i dati da tastiera
        System.out.println("Logout");
        userInput.nextLine();
        System.out.print("Username: ");
        String username = userInput.nextLine();
        serverOutput.println("LOGOUT");
        serverOutput.println(username);

        //leggo risposta dal server
        String response = serverInput.nextLine();
        System.out.println(response);

        // In caso di utente non loggato non devo fare l'uscitra dal gruppo multicast
        if(!response.equals("Errore nella disconnessione dell'utente") && !response.equals("Username Sbagliato") && !response.equals("NON LOGGATO")) {
            // Disiscrizione dal socket multicast
            try {
                InetAddress ia = InetAddress.getByName(indirizzoMulticast);
                socket.leaveGroup(ia);
            } catch (Exception e) {e.printStackTrace();}
        }
    }

    private static void recensioni(PrintStream serverOutput, Scanner serverInput, Scanner userInput) {
        serverOutput.println("RECENSIONE");
        System.out.println("Scrivi una recensione");
        userInput.nextLine();
        // Serve per controllare se c'è il login
        String check = serverInput.nextLine();
        if(check.equals("FAIL")){
            System.out.println("Devi essere loggato");
            return ;
        }
        // Individuazione hotel
        System.out.print("Nome hotel: ");
        String nameHotel = userInput.nextLine();
        System.out.print("Città hotel: ");
        String citta = userInput.nextLine();

        //Punteggi dell'hotel chiesti all'utente
        int rate = leggiInputRecensione(userInput, "Punteggio Totale: ");
        int cleaning = leggiInputRecensione(userInput, "Voto Pulizia: ");
        int position = leggiInputRecensione(userInput, "Voto Posizione: ");
        int services = leggiInputRecensione(userInput, "Voto Servizi: ");
        int quality = leggiInputRecensione(userInput, "Voto Qualità: ");

        // Invio al server i dati da leggere
        serverOutput.println(nameHotel);
        serverOutput.println(citta);
        serverOutput.println(rate);
        serverOutput.println(cleaning);
        serverOutput.println(position);
        serverOutput.println(services);
        serverOutput.println(quality);

        //leggo risposta dal server
        String response = serverInput.nextLine();
        System.out.println(response);
    }

    public static int leggiInputRecensione(Scanner userInput, String richiesta) {
        int letto;
        // Verifica se ivalori inseriti sono corretti
        while(true) {
            try {
                System.out.print(richiesta);
                letto = userInput.nextInt();
                if(letto>=0 && letto<=5)
                    break;
                else {
                    System.out.println("Valore non compreso nell'intervallo [0, 5]\n");
                }
            }catch (InputMismatchException e){
                userInput.nextLine();
                System.out.println("ERRORE: dati inseriti non validi!!\n");
            }
        }
        return letto;
    }
    private static void cercaTuttiHotel(PrintStream serverOutput, Scanner serverInput, Scanner userInput) {
        System.out.println("Cerca tutti gli hotel");
        userInput.nextLine();
        System.out.print("Città: ");
        String citta = userInput.nextLine();

        serverOutput.println("SEARCHALL");
        serverOutput.println(citta);
        //Legge la risposta del server e stampa i dati degli hotels
        while (true) {
            String stamp = serverInput.nextLine();
            if(stamp.equals("STOP"))
                break;
            System.out.println(stamp);
        }
    }

    private static void iscrizioneMulticast() {
        try {
            socket = new MulticastSocket(portaMulticast);
            InetAddress ia = InetAddress.getByName(indirizzoMulticast);
            socket.joinGroup(ia);
            riceveNotifiche(socket);
        } catch (Exception e) {e.printStackTrace();}
    }

    private static void riceveNotifiche(MulticastSocket socket) {
        try {
            // Thread per la ricezione dei messaggi
            Thread riceveTask = new Thread(() -> {
                try {
                    byte[] buf = new byte[100];
                    while (true) {
                        DatagramPacket dp = new DatagramPacket (buf,buf.length);
                        socket.receive(dp);
                        String notifica = new String(dp.getData(), 0, dp.getLength());
                        if(notifica.length() > 0) {
                            System.out.println("Messaggio ricevuto: " + notifica);
                            notifica = "";
                        }
                    }
                } catch (Exception e) {e.printStackTrace();}
            });
            riceveTask.start();
        }catch (Exception e) {e.printStackTrace();}
    }
}
