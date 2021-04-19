import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Klasse welche für die Zusammenstellung der IP Adressen sowie die Auslösung der Ping-Befehle zuständig ist.
 */
public class IPAdressClass {
    
    /**
     * Ruft die Methode getIPAdresses() auf, um die IP-Adressen zusammenzustellen und anschliessend die Funktion getReachableHosts(), 
     * um für die IP-Adressen die Ping-Befehle auszuführen.
     * @param baseIp - Basisadresse, mit welcher die abzufragenen IP-Adressen zusammengestellt werden
     * @param range - wie viele Tripplets zur Baseadresse hinzugefügt werden müssen (8er = 3 ,16er = 2 oder 24er = 1)
     * @param getHostName - ob der Hostnamen auch abgefragt werden muss
     * @return Liste vom Typ Future, welche für jede erreichbare IP-Adresse ein Host-Objekt enthält und optional den Hostnamen
     */
    public List<Future<Host>> getHosts(String baseIp, String range, boolean getHostName) {
        ArrayList<String> ipAdresses = new ArrayList<String>();
        switch (range) {
            case "24":
                ipAdresses = getIPAdresses(baseIp, 1);
                break;
            case "16":
                ipAdresses = getIPAdresses(baseIp, 2);
                break;
            case "8":
                ipAdresses = getIPAdresses(baseIp, 3);
                break;
            default:
                break;
        }
        return getReachableHosts(ipAdresses, getHostName);
    }

    /**
     * Rekursive Methode, welche der Basisadresse die benötigten Tripplets anhängt. 
     * @param baseIp - Basisadresse, mit welcher die abzufragenen IP-Adressen zusammengestellt werden
     * @param numberOfTriplets - wie viele Tripplets zur Baseadresse hinzugefügt werden müssen (8er = 3 ,16er = 2 oder 24er = 1)
     * @return Array Liste mit allen IP-Adressen in diesem Subnetz und der angegebenen Baseadresse
     */
    public static ArrayList<String> getIPAdresses(String baseIp, int numberOfTriplets) {
        ArrayList<String> ipAdresses = new ArrayList<String>();
        for (int i = 0; i < 256; i++) {
            String currentIpAdress = baseIp + "." + i;
            if (numberOfTriplets == 1) {
                ipAdresses.add(currentIpAdress);
            } else if (numberOfTriplets > 1) {
                ipAdresses.addAll(getIPAdresses(currentIpAdress, numberOfTriplets - 1));
            }
        }
        numberOfTriplets--;
        return ipAdresses;
    }

    /**
     * Erstellt einen Executorservice und weisst diesem Tasks zu.
     * @param ipAdresses - IP-Adressen die abgefragt werden müssen
     * @param getHost - Ob der Hostname auch abgefragt werden muss
     * @return Liste vom Typ Future, welche für jede erreichbare IP-Adresse ein Host-Objekt enthält und optional den Hostnamen
     */
    public static List<Future<Host>> getReachableHosts(ArrayList<String> ipAdresses, boolean getHost) {
        
        ExecutorService executor = Executors.newFixedThreadPool(255);

        // Für jede IP-Adresse ein Objekt vom Typ Task erstellen und in einer Liste speichern
        List<Task> taskList = new ArrayList<Task>();
        for (String ipAdress : ipAdresses) {
            taskList.add(new Task(ipAdress, getHost));
        }

        List<Future<Host>> resultList = null;
        try {
            // Tasks dem Executorservice zuweisen
            resultList = executor.invokeAll(taskList);
        } catch (Exception e) {
            //TODO: handle exception
        }
        // Executorservice beenden, damit die Threads gekillt werden. 
        executor.shutdown();
        return resultList;
    }
}

