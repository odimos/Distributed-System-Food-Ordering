
Master (Server) <-> (Client) Manager, App
Master (Client) <-> (Server) Workers
Master (Server) <-> (Client) Reducer 
Reducer (Server) <-> (Client) Workers
Reducer (Server) <-> (Client) Workers

Ολες οι υλοποιήσεις της σχέσης client - server
γίνονται με την κλάση Client και την κλάση Server
Και στις δύο περιπτώσεις, προκειμένου να διαχιερίζεται την απάντηση αποτελεσματικά το αντικείμενο, 
υπάρχει ο όρος <M extends ResponseHandler> .
Οπότε κάθε αντικείμενο που θα καλέσει την σχέση client - server public Client( Task task, String host, int port, M manager)
θα πρέπει να υλοποιεί και την συνάρτηση responseHandler ή requestHandler, η οποία θα καθορίζει πως διαχειρίζεται την απάντηση που δέχεται.

Οι συνδέσεις πραγματοποιούνται με TCP sockets σε νήματα (Threads)



![Schema](res/schema.jpg)