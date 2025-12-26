Distributed System Food Ordering App.

[Demo]("")

![schema](res/schema.jpg)

The back-end is written in java. 
Connections between different nodes use TCP sockets.
Workers can be in different machines.
Multiple connection and WorkLoads can be handled in parallel inside each worker node with multithreaded architecture.
The distrucuted data processing is achieved with a custom MapReduce implementation,
the concurency is achieved with wait/notify.

The ordering app is written with  kotlin coroutines and java threads to achieve the asynchronous 
functionality.
Apps UI is created with Jetpack Compose using Kotlin.


All interactions between nodes follow a client–server model, implemented using shared Client and Server classes.
Master (Server) <-> (Client) App, Manager
Master (Client) <-> (Server) Workers
Master (Server) <-> (Client) Reducer 
Reducer (Server) <-> (Client) Workers
Reducer (Server) <-> (Client) Workers

Technologies
Language: Java
Networking: TCP Sockets
Storage: In-memory only
Serialization: Java Serialization
Concurrency: Threads + wait/notify, Kotlin Coroutines.
No external networking libraries

Ολες οι υλοποιήσεις της σχέσης client - server
γίνονται με την κλάση Client και την κλάση Server
Και στις δύο περιπτώσεις, προκειμένου να διαχιερίζεται την απάντηση αποτελεσματικά το αντικείμενο, 
υπάρχει ο όρος <M extends ResponseHandler> .
Οπότε κάθε αντικείμενο που θα καλέσει την σχέση client - server public Client( Task task, String host, int port, M manager)
θα πρέπει να υλοποιεί και την συνάρτηση responseHandler ή requestHandler, η οποία θα καθορίζει πως διαχειρίζεται την απάντηση που δέχεται.

Οι συνδέσεις πραγματοποιούνται με TCP sockets σε νήματα (Threads)

A Task object starts from the managers cmd or the users app, and an Answer object returns back. 

The information are stored in the form of objects of Store, Product, Sale .
Stores are in JSON format.

Τι αντικείμενα αποθηκεύουν οι WorkerNodes: Stores που έχουν μέσα products και sales
Τι αντικείμενα διακινούνται: Από τον χρήστη Tasks
Προς το χρήστη Answers

Pending logig:
Οταν ένα request τουπου απαιτεί τη συνεργασία των γόρκερς φτάνει από τον πελάτη στο μάστερ, 
ο μάστερ δεν κλείνει τη σύνδεση με τον client, 
πρώτα περιμένει να ολοκληρωθεί η διαδρομή από τους γόρκερς στο ρεντιουσερ κ πισω στο μάστερ,
κ τότε, όταν δηλαδή επιστρέφει ο ρεντιουσερ στον μαστερ, επιστρέφει το αποτέλεσμα πίσω στον ξλαιεντ από
την σύνδεση που παρέμεινε ανοιχτή, κ μετά την κλείνει.

Τα αιτήματα που εκρεμούν διατηρούνται σε μια λήστα που τα αντιστοιχέι με το id του αιτήματος
(όχι το ID του τύπου του αιτήματος, το ID του αιτήματος clientTaskID). Τo id αυτό
χρησιμοποιείται για να μπορεί να αναγνωρίζεται κ εντοπίζεται ποιο αίτημα είναι
αυτό που περιμένει
 public Answer handleRequestFromClient(Task req) {
    ...
    Pending pending = new Pending();
    addPending(taskID, pending);
    ...
    Answer answer = pending.waitForAnswer();
    return answer
 }

 public class InnerMaster implements RequestHandler {
        @Override
        public Answer handleRequestFromClient(Task req) {
            ...
            Pending pending = getPending(taskID);
            ...
            pending.setAnswer(answer); // σε αυτό το σημείο συνεχίζει ο κώδικας που περιμένει παραπάνω
            ...

        }
 }

 public class Pending {
    public Answer answer;
    public boolean isSet = false;

    public synchronized Answer waitForAnswer() {
        while (!isSet) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }
        return answer;
    }

    public synchronized void setAnswer(Answer answer) {
        if (!isSet) {
            this.answer = answer;
            this.isSet = true;
            notify();  // Wake up waiting thread
        }
    }
    
}

H epikoinvnia γίνεταθ με tcp socket ObjectOutputStream, ObjectInputStream

η προστασία από τις ταυτόχρονες προσπάθιες εγραφής επιτυγχάνεται με το synchronized

Τα αιτήματα λειτουργούν με τις μεθόδους wait - notify()
οπότε περιμένουν όταν στέλνει request ο μάστερ στους γουροκερς, ειδοποιούνται να συνεχίσουν
όταν δέχεται ο μάστερ ρικουεστ ο μαστερ απο τον ριντιουσερ

Λειτουργίες manager:
Προσθήκη καταστημάτων: ADD_STORE
Προσθήκη / αφαίρεση διαθέσημων προϊόντων: ADD_PRODUCT / REMOVE_PRODUCT
Αλλαγή του αριθμού διαθέσημων προϊόντων (stock): CHANGE_STOCK
Εμφάνηση των συνολικών πωλήσεων ανά προϊόν: GET_SALES_PER_PRODUCT
(δεν εμφανίζει όσα έχουν 0 πωλήσεις)
Εμφάνηση όλων των συνολικών πωλήδεων των προϊόντων ανά κατηγορία προϊόντων: GET_SALES_PER_PRODUCT_CATEGORY
(δεν εμφανίζει όσα έχουν 0 πωλήσεις)
Εμφάνηση όλων τις συνολικές πωλήσεις των καταστημάτων που ανήκουν στη συγκεκριμένη κατηγορία φαγητού: GET_SALES_PER_FOOD_CATEGORY
(δεν εμφανίζει όσα έχουν 0 πωλήσεις)

Ανάγνωση των δεδομένων καταστημάτων από JSON αρχεία.
Ολα τα δεδομένα που μεταφαίρονται με αυτο τον τρόπο είναι serialisable υποχρεωτικά
Επίσης τα δεδομένα μεταφαίρονται με τη μορφή αντικειμένου κλάσης από το αππ στο σέρβερ, για αυτό
υπάρχει το αναγνωριστικό serialVersionUID ώστε να είναι ίδια η υπογραφή.


How to RUN
important: DONT make requests like search from the app before the server is initialized.
Doing so will lead to master catching the request as soon as it opens before the workers are created,
and returning error. 
Is fou get blocked by this way. Close the app. Close the server java.
And then run first the server and then the app. 
To not accidentaly make request with server blocked, just dont have the app open if the server is closed. 
Run the server, then run the app.
![Schema](res/schema.jpg)