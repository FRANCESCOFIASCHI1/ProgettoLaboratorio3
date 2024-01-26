HOTELIER - Hotel Advisor Service

Overview
HOTELIER is a simplified version of the famous TripAdvisor service, focusing on managing hotels, user registration, login, search, and review submission functionalities.

Implementation Choices
JSON Handling: Utilizes the GSON library for reading and writing JSON files.
Configuration: Server properties are loaded from a specific configuration file (config.inputServer).
Client-Server Communication: Employs Java I/O for message exchange between clients and the server.
Concurrency: Uses a CachedThreadPool to handle multiple client connections concurrently.
Ranking Algorithm: Calculates hotel rankings based on the timeliness of reviews and review scores.
Data Persistence: Periodically saves user information, rankings, and reviews to ensure data consistency.
Multithreading: Utilizes separate threads for client requests handling and periodic tasks like ranking recalculation and data saving.

Usage
Login/Registration: Users can register or log in to access additional functionalities.
Search: Users can search for hotels by city or view all hotels.
Review Submission: Registered users can submit reviews for hotels.
View Badges: Users can view their latest badges earned.
**Logout**: Allows users to log out from their accounts.
Exit: Terminates the application.

Data Structures
Hotel Maintenance: Hotels, user information, and reviews are stored in JSON files.
User Management: User information is stored in a JSON file containing usernames, passwords, review counts, and login status.
Review Structure: Reviews consist of hotel name, city, overall rating, and ratings for specific categories.
Threading and Synchronization
Server Threads: Uses a CachedThreadPool for managing client connections and separate threads for periodic tasks.
Client Threads: Utilizes a separate thread for receiving server messages (riceveTask).

Installation and Execution
Compile and execute the server and client programs using provided commands.

Contributors
Francesco Fiaschi (636697), Informatics - UNIPI
