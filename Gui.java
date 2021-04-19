import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.List;
import java.util.concurrent.Future;

/**
 * Klasse, welche für den Aufbau des GUI zuständig ist
 */
public class Gui extends JFrame implements ActionListener {
    
    private static final long serialVersionUID = 1L;
    JPanel mainTitlePanel;
    JPanel titlePanel;
    JPanel ipPanel;
    JPanel hostPanel;
    JPanel resultPanel;
    JLabel mainTitle;
    JLabel title;
    JLabel ipLabel;
    JLabel subnetzLabel;
    JLabel waitLabel;
    JTextField tripplet1;
    JTextField tripplet2;
    JTextField tripplet3;
    JTextField tripplet4;
    JLabel dot1;
    JLabel dot2;
    JLabel dot3;
    JComboBox<String> subnetz;
    JButton scan;
    JTextArea result;
    JScrollPane scrollbar;
    JCheckBox hostCheckBox;

    /**
     * Konstruktor. Stellt beim starten alle benötigen Elemente für das GUI zusammen
     */
    public Gui() {
        super("IP-Scanner");
        this.setLocation(100, 100);
        this.setSize(700, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Maincontainer
        Container mainContainer = this.getContentPane();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        
        // Maintitle
        mainTitlePanel = new JPanel();
        mainTitle = new JLabel("IP-Scanner");
        mainTitle.setFont(new Font("Arial", Font.PLAIN, 30));
        mainTitlePanel.add(mainTitle);

        // Title
        titlePanel = new JPanel();
        title = new JLabel("Bitte IP-Adresse und Subnetzmaske angeben");
        title.setFont(new Font("Arial", Font.PLAIN, 15));
        titlePanel.add(title);

        // IP-Adress eingabe Zeile
        ipPanel = new JPanel();
       
        ipLabel = new JLabel("IP-Adresse:");
        subnetzLabel = new JLabel("Subnetzmaske:"); 
        
        tripplet1 = new JTextField();
        tripplet1.setColumns(3);
        tripplet2 = new JTextField();
        tripplet2.setColumns(3);
        tripplet3 = new JTextField();
        tripplet3.setColumns(3);
        tripplet4 = new JTextField();
        tripplet4.setColumns(3);

        dot1 = new JLabel(".");
        dot2 = new JLabel(".");
        dot3 = new JLabel(".");

        String[] subnetze = {"8", "16", "24"};
        subnetz = new JComboBox<String>(subnetze);
        subnetz.setSelectedItem("24");

        scan = new JButton("Scannen");
        scan.addActionListener(this);

        ipPanel.add(ipLabel);
        ipPanel.add(tripplet1);
        ipPanel.add(dot1);
        ipPanel.add(tripplet2);
        ipPanel.add(dot2);
        ipPanel.add(tripplet3);
        ipPanel.add(dot3);
        ipPanel.add(tripplet4);
        ipPanel.add(subnetzLabel);
        ipPanel.add(subnetz);    
        ipPanel.add(scan);

        // Hostname
        hostPanel = new JPanel();
        hostCheckBox = new JCheckBox("Hostnamen anzeigen", true);
        hostPanel.add(hostCheckBox);
        
        // Result
        resultPanel = new JPanel();
        // Loader
        ImageIcon loading = new ImageIcon("ajax-loader.gif");
        waitLabel = new JLabel(loading);
        waitLabel.setVisible(false);
        // Textarea für Anzeige des Resultats
        result = new JTextArea(20, 40);
        result.setEditable(false);
        resultPanel.add(waitLabel);
        scrollbar = new JScrollPane(result);
        scrollbar.setVisible(false);
        resultPanel.add(scrollbar);
        resultPanel.add(Box.createVerticalStrut(350));

        mainContainer.add(mainTitlePanel);
        mainContainer.add(titlePanel);
        mainContainer.add(ipPanel);
        mainContainer.add(hostPanel);
        mainContainer.add(resultPanel);

        this.setVisible(true);
    }  

    /**
     * Implementierte Methode des Inferface ActionListener, um bei Klick auf den Suchen Button die Abfrage zu starten und 
     * am Schluss das Resultat anzuzeigen
     * @param e - Event, welcher ausgelöst wurde
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // Anzeige für Resultat ausblenden und Loader einblenden
        scrollbar.setVisible(false);
        result.setText(null);
        waitLabel.setVisible(true);
        
        // Swingworker Objekt mit anonymer Klasse
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            // Implementierte Methode, welche die Baseadresse zusammenstellt und die Abfrage startet.
            // Dies wird in einem separaten Thread durchgeführt
			@Override
			protected Void doInBackground() throws Exception {
                
				IPAdressClass ipClass = new IPAdressClass();
                
                // Baseadresse zusammenstellen
                String baseIp = null;
                switch (subnetz.getSelectedItem().toString()) {
                    case "24":
                        baseIp = tripplet1.getText() + "." + tripplet2.getText() + "." + tripplet3.getText();
                        break;
                    case "16":
                        baseIp = tripplet1.getText() + "." + tripplet2.getText();
                        break;
                    case "8":
                        baseIp = tripplet1.getText();
                        break;
            
                    default:
                        break;
                }
                
                // Abfrage starten und danach jede IP-Adresse in die Textarea schreiben
                List<Future<Host>> resultList = ipClass.getHosts(baseIp, subnetz.getSelectedItem().toString(), hostCheckBox.isSelected());
                for (Future<Host> future : resultList) {
                    try {
                        Host host = future.get();
                        if (host != null) {
                            if (hostCheckBox.isSelected()) {
                                result.append(host.ip_adresse + "\t\t" + host.host_name + "\n");
                            }
                            else {
                                result.append(host.ip_adresse + "\n");
                            }
                            
                        }
                    } catch (Exception ex) {
                        // TODO
                    }
                }
				return null;
			}

            // Implementierte Methode, welche ausgeführt wird, wenn die Methode doInBackground() fertig ist. 
            // Blendet den Loader aus und zeigt das Resultat an.
            @Override
            protected void done() {
                waitLabel.setVisible(false);
                scrollbar.setVisible(true);
            }
            
            
        };
        worker.execute();
    }
    
}
