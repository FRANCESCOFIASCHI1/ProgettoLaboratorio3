<H1>HOTELIER - Servizio di consulenza alberghiera</H1>

<H3>Panoramica</H3>
HOTELIER è una versione semplificata del famoso servizio TripAdvisor, che si concentra sulla gestione degli hotel, sulla registrazione degli utenti, sul login, sulla ricerca e sulle funzionalità di invio delle recensioni.

<H3>Scelte di implementazione</H3>
Gestione di JSON: Utilizza la libreria GSON per leggere e scrivere file JSON.
Configurazione: Le proprietà del server sono caricate da un file di configurazione specifico (config.inputServer).
Comunicazione client-server: Utilizza Java I/O per lo scambio di messaggi tra i client e il server.
Concorrenza: Utilizza un CachedThreadPool per gestire più connessioni client in contemporanea.
Algoritmo di classificazione: Calcola le classifiche degli hotel in base alla tempestività delle recensioni e ai punteggi delle recensioni.
Persistenza dei dati: Salva periodicamente le informazioni sugli utenti, le classifiche e le recensioni per garantire la coerenza dei dati.
Multithreading: Utilizza thread separati per la gestione delle richieste dei clienti e per le attività periodiche come il ricalcolo delle classifiche e il salvataggio dei dati.

<H3>Uso</H3>
0. Login/Registrazione: Gli utenti possono registrarsi o accedere alle funzionalità aggiuntive.
1. Ricerca**: Gli utenti possono cercare gli hotel per città o visualizzare tutti gli hotel.
2. Invio di recensioni: Gli utenti registrati possono inviare recensioni sugli hotel.
3. Vedi i badge: Gli utenti possono visualizzare gli ultimi badge guadagnati.
4. Verifica tutti gli hotel
5. Cancellazione: Consente agli utenti di uscire dal proprio account.
9. Esci: Termina l'applicazione.

<H3>Strutture dati</H3>
Manutenzione degli hotel: Gli hotel, le informazioni sugli utenti e le recensioni sono memorizzati in file JSON.
Gestione degli utenti: Le informazioni sugli utenti sono memorizzate in un file JSON contenente nomi utente, password, numero di recensioni e stato di accesso.
Struttura delle recensioni: Le recensioni sono composte da nome dell'hotel, città, valutazione complessiva e valutazioni per categorie specifiche.

<H3>Filtraggio e sincronizzazione</H3>
Thread del server: Utilizza un CachedThreadPool per gestire le connessioni dei client e thread separati per le attività periodiche.
Thread del client: Utilizza un thread separato per ricevere i messaggi del server (riceveTask).

<H3>Installazione ed esecuzione</H3>
Compilare ed eseguire i programmi server e client usando i comandi forniti.

<H3>Contribuenti</H3>
Francesco Fiaschi (636697), Informatica - UNIPI
