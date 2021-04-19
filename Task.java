import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.concurrent.Callable;

/**
 * Klasse, um abzufragen, ob die IP-Adresse erreibar ist und wie der Hostname lautet.
 * Das Interface Callable muss implementiert werden, damit die Abfrage in einem eigenen Thread laufen kann.
 */
public class Task implements Callable<Host> {
    private final String ipAdress;
    private final boolean getHost;

    /**
     * Konstuktor. Setzt die IP-Adresse und ob der Hostname abgefragt werden soll, da die Methode call() keine Parameter erlaubt.
     * @param ipAdress IP-Adresse, die abgefragt werden soll
     * @param getHost - Boolean ob der Hostname auch abgefragt werden soll
     */
    public Task(String ipAdress, boolean getHost) {
        this.ipAdress = ipAdress;
        this.getHost = getHost;
    }

    /**
     * Implementierte Methode des Interfaces Callable, in welcher die Abfrage durchgeführt wird. 
     * @return Objekt vom Typ Host mit IP-Adresse und je nach dem ob gewünscht auch Hostname. Null wenn IP-Adresse nicht erreichbar ist.
     */
    @Override
    public Host call() throws Exception {
         System.out.println("Starting with IP " + ipAdress);
         InetAddress ip = Inet4Address.getByName(ipAdress);
         if (ip.isReachable(50)) {
             if (getHost) {
                String hostName = ip.getHostName();
                System.out.println("Finished IP " + ipAdress);
                return new Host(ipAdress, hostName);
             }
             System.out.println("Finished IP " + ipAdress);
             return new Host(ipAdress);
        }
        System.out.println("Finished IP " + ipAdress);
        return null;
    }
}
