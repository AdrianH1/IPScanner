/**
 * Klasse mit IP-Adresse und Hostname
 */
public class Host {

    String ip_adresse;
    String host_name;

    public Host (String ip) {
        this.ip_adresse = ip;
    }

    public Host(String ip, String host){
        this.host_name = host;
        this.ip_adresse = ip;
    }
}
