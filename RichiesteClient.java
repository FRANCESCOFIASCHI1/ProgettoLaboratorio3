import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class RichiesteClient implements Runnable {
    private final Socket clientSocket;

    public RichiesteClient(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        String usernameLog = "";
        try (Scanner in = new Scanner(new InputStreamReader(clientSocket.getInputStream()));
            PrintStream out = new PrintStream(clientSocket.getOutputStream(), true)) {
            
            // Loop per le richieste del server
            while(true){
                menu(out);
                String richiesta = in.nextLine();
                // Differenziazione delle diverse funzionalità
                switch (richiesta) {
                    case "REGISTRAZIONE":
                        gestisciRegistrazione(in, out);
                        break;
                    case "LOGIN":
                        usernameLog = gestisciLogin(in, out);
                        break;
                    case "BADGE":
                        gestisciBadge(out, usernameLog);
                        break;
                    case "SEARCH":
                        gestisciRicerca(in, out);
                        break;
                    case "LOGOUT":
                        gestisciLogOut(in, out, usernameLog);
                        break;
                    case "RECENSIONE":
                        gestisciRecensione(in, out, usernameLog);
                        break;
                    case "SEARCHALL":
                        gestisciSearchAll(in, out, usernameLog);
                        break;
                    case "EXIT":
                        clientSocket.close();
                        return;
                    default:
                        System.out.println("Richiesta non valida");
                        break;
                }
            }
        } catch (IOException e) {e.printStackTrace();}
        finally {
            // Disconneto automaticamente quando si chiude la connessione
            HotelierServerMain.logOut(usernameLog);
        }
    }

    private void gestisciRegistrazione(Scanner in, PrintStream out) throws IOException {
        // Inserimento di username e password
        String username = in.nextLine();
        String password = in.nextLine();

        // Gestione dei codici della registrazione
        int registrazioneRiuscita = HotelierServerMain.registrazioneUtente(username, password);
        switch(registrazioneRiuscita){
            case 1:
                out.print("PASSWORD_MANCANTE\n");
                break;
            case 2:
                out.print("REGISTRAZIONE_FALLITA\n");
                break;
            case 3:
                out.print("REGISTRAZIONE_SUCCESSO\n");
                break;
        }
    }
    
    private String gestisciLogin(Scanner in, PrintStream out) throws IOException {
        // Login di un utente registrato
        String username = in.nextLine();
        String password = in.nextLine();
        
        boolean registrazioneRiuscita = HotelierServerMain.loginUtente(username, password);
        if (registrazioneRiuscita) {

            out.print("LOGIN_SUCCESSO\n");
        } else {
            out.print("LOGIN_FALLITO\n");
        }
        return username;
    }

    private void gestisciBadge(PrintStream out, String usernameLog){
        // Stampa del badge associato all'utente
        String badge = HotelierServerMain.showMyBadges(usernameLog);
        out.println(badge);
    }

    private void gestisciRicerca(Scanner in, PrintStream out){
        // Ricerca hotel con nome e città
        String nameHotel = in.nextLine();
        String citta = in.nextLine();
        
        String info = HotelierServerMain.searchHotel(nameHotel, citta);
        out.println(info);
    }

    public static void menu(PrintStream out){
        // Menù delle opzioni che l'utente può effettuare
        out.println("-----------------------");
        out.println("MENU");
        out.println("0. LogIn");
        out.println("1. Registrati");
        out.println("2. Mostra il mio badge");
        out.println("3. Cerca Hotel");
        out.println("4. Cerca tutti gli Hotel");
        out.println("5. Inserisci Recensione");
        out.println("6. LogOut");
        out.println("9. Exit");
        }

    private void gestisciLogOut(Scanner in, PrintStream out, String usernameLog){
        String username = in.nextLine();
        String esito;
        if(usernameLog.equals("")) {
            esito = "NON LOGGATO";
        }
        else {
            if(usernameLog.equals(username)) {
                esito = HotelierServerMain.logOut(username);
            }
            else{
                esito = "Username Sbagliato";       // L'username non corrisponde a quello loggato in questa connessione
            }
        }
        out.println(esito);     // Invio dell'esito del logout
    }

    public static void gestisciRecensione(Scanner in, PrintStream out, String usernameLog) {
        // Quest'azione può essere effettuata solo se si è loggati
        if(!HotelierServerMain.loggato(usernameLog)) {      // Contollo se l'utente è loggato
            out.println("FAIL");
            return;
        }
        else{
            out.println("LOG");
        }
        String nameHotel = in.nextLine();
        String citta = in.nextLine();
        int rate = in.nextInt();
        int cleaning = in.nextInt();
        int position = in.nextInt();
        int services = in.nextInt();
        int quality = in.nextInt();
        in.nextLine();
        
        String esito = HotelierServerMain.recensione(usernameLog, nameHotel, citta, rate,cleaning,position,services,quality);
        out.println(esito);     // Restituisco al client la stringa che mi indica l'esito
    }

    public static void gestisciSearchAll(Scanner in, PrintStream out, String usernameLog) {
        // Cerca tutti gli hotel in una città
        String citta = in.nextLine();
        List<Hotel> listHotel = HotelierServerMain.listHotelRanking(citta);
        if(listHotel == null || listHotel.isEmpty()) {      // Controllo della presenza della città nelle città ammesse
            out.println("Città non ammessa");
        }
        for (Hotel hotel: listHotel) {
            out.print(hotel.toString1() + "\n");
        }
        out.println("STOP");        //Segnale di stop
    }
}
