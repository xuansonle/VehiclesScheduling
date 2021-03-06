package parser;



import helper.feasibilityHelper;
import model.Deadruntime;
import model.Fahrzeugumlauf;
import model.Servicejourney;
import model.Stoppoint;
import output.Schedule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

/**
 * 
 * Parser Klassen, die nur die Instanzen der Daten einliest,
 * die fuer die Problemstellung unseres Projektes notwendig sind.
 *
 */
public class ProjectReadInWithInitialSolution {

    public File f;
    public String output;
    public String input;
    Date start;
    Date ende;
    public double durchschnittsdistance;
    public double durchschnittsruntime;
    public double maxloading;

    // 1.1 Variablen fuer Streckennetz und Ladeinfrastruktur erstellen
    
    // 1.1.1 Stoppoints
    public HashMap<String, Stoppoint> stoppoints;
    
    // 1.1.2 Servicefahrten
    public HashMap<String, Servicejourney> servicejourneys;
   
    // 1.1.3 Verbindungen (Deadruntime)
    public HashMap<String, Deadruntime> deadruntimes;
    
    // Zeitformat
    public DateFormat zformat;

    
    // sind Verbindungen zwischen zwei Servicefahrten moeglich
    public HashMap<String, Integer> validEdges;
    
    // Menge der Fahrzeugumlaeufe aus der Initialloesung
    public Vector<Fahrzeugumlauf> fahrzeugumlaeufe;
 

    /**
     * Konstruktor: liest die Daten-Datei zeilenweise aus 
     * und speichert die Instanzen in den zuvor erstellten und instanziierten Variablen
     */
	public ProjectReadInWithInitialSolution(String path) {
		
		this.f = new File(path);
		
		BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(f));
        } catch (IOException e) {
            System.out.println(e);
        }

        // 1.2 Zeilen auslesen und in Variablen schreiben

        try {
            String temp = "";
            String BlockBegin;
            String ersteszeichen;

            while (temp != null) // lese Datei zeilenweise aus
            {
                temp = reader.readLine(); // nächste Zeile
                BlockBegin = temp.split(":")[0]; // erster Teil der Zeile bis zum ":"

                if (BlockBegin.equals("$STOPPOINT")) // 1. Relation: Stoppoint
                {

                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen
                    
                    //stoppoints = new HashMap<String, Stoppoint>();
                    
                    while (temp != null && !ersteszeichen.equals("*")) {
                    
                    	/**
                    	Stoppoint neu = new Stoppoint(temp.split(";")[0]);
                    	
                    	stoppoints.put(neu.getId(), neu);
                    	*/
                    	
                        temp = reader.readLine(); // nächste Zeile
                        ersteszeichen = temp.substring(0, 1); // erstes Zeichen
                    } // end while
                    continue;
                } // end if


                if (BlockBegin.equals("$SERVICEJOURNEY")) // 2. Relation: Servicefahrten
                {

                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen
                    
                    servicejourneys = new HashMap<String, Servicejourney>();

                    while (temp != null && !ersteszeichen.equals("*")) {

                        String sfId= (temp.split(";")[0]); // ID
                        String sfFromStopID = (temp.split(";")[2]); // Starthaltestelle
                        String sfToStopID = (temp.split(";")[3]); // Endhaltestelle
                        String sfDepTime = temp.split(";")[4]; // Abfahrtszeit
                        String sfArrTime = temp.split(";")[5]; // Ankunftszeit
                        int sfDistance = Integer.parseInt(temp.split(";")[11]); 
                        
                        Servicejourney neu = new Servicejourney(sfId, sfFromStopID, sfToStopID, sfDepTime, sfArrTime, sfDistance);
                        servicejourneys.put(neu.getId(), neu);

                        temp = reader.readLine(); // nächste Zeile
                        ersteszeichen = temp.substring(0, 1); // erstes Zeichen

                    } // end while
                    continue;
                } // end if

                if (BlockBegin.equals("$DEADRUNTIME")) // 3. Relation: Leerfahrten
                {

                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen
                    
                    deadruntimes = new HashMap<String, Deadruntime>();

                    while (temp != null && !ersteszeichen.equals("*")) {

                        String fromStopID = (temp.split(";")[0]); // ID
                        String toStopID = (temp.split(";")[1]); // ID
                        int distance = Integer.parseInt(temp.split(";")[4]); 
                        int runtime = Integer.parseInt(temp.split(";")[5]); 

                        Deadruntime neu = new Deadruntime(fromStopID, toStopID, distance, runtime);
                        deadruntimes.put(neu.getId(), neu);
                        
                        temp = reader.readLine();
                        ersteszeichen = temp.substring(0, 1); // erstes Zeichen

                    } // end while
                    continue;
                } // end if
                
                if (BlockBegin.equals("$INITIALSTOPPOINT")) // 1. Relation: Stoppoint
                {

                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen
                    
                    stoppoints = new HashMap<String, Stoppoint>();
                    
                    while (temp != null && !ersteszeichen.equals("*")) {
                    
                    	String id = temp.split(";")[0];
                    	String isLoadingstation = temp.split(";")[1];
                    	boolean isLoadingstation1 = false;
                    	if(isLoadingstation.equals("true")){
                    		isLoadingstation1 = true;
                    	}
                    	int frequency = Integer.parseInt(temp.split(";")[2]);
                    	
                    	Stoppoint neu = new Stoppoint(id);
                    	neu.setLadestation(isLoadingstation1);
                    	neu.setFrequency(frequency);
                    	stoppoints.put(neu.getId(), neu);
                    
                        temp = reader.readLine(); // nächste Zeile
                        ersteszeichen = temp.substring(0, 1); // erstes Zeichen
                        
                    } // end while
                    continue;
                } // end if
     
                if (BlockBegin.equals("$Umlauf")) // 4. Relation: Fahrzeugumlaeufe nach der Initialloesung
                {

                    temp = reader.readLine(); // nächste Zeile
                    ersteszeichen = temp.substring(0, 1); // erstes Zeichen
                    
                    fahrzeugumlaeufe = new Vector<Fahrzeugumlauf>();

                    while (temp != null && !ersteszeichen.equals("*")) {

                        String id = (temp.split(";")[0]); // ID
                        String fahrten = (temp.split(";")[1]); // ID
                        fahrten = fahrten.substring(1, fahrten.length()-1);
                        String[] ids = fahrten.split(", ");
                        String stopps = temp.split(";")[2];
                        stopps = stopps.substring(1, stopps.length()-1);
                        String[] idStoppoints = stopps.split(", ");
                        Fahrzeugumlauf neu = new Fahrzeugumlauf(id);
                        for (int i = 0; i < ids.length; i++) {
							if(i % 2 == 0){
								neu.addFahrt(deadruntimes.get(ids[i]));
							}
							else{
								neu.addFahrt(servicejourneys.get(ids[i]));
							}		
						}
                        LinkedList<Stoppoint> laden = new LinkedList<Stoppoint>();
                        for (int i = 0; i < idStoppoints.length; i++) {
							laden.add((stoppoints.get(idStoppoints[i])));
						}
                        
                        neu.setLaden(laden);
                        fahrzeugumlaeufe.add(neu);
                        temp = reader.readLine();
                        

                    } // end while
                    continue;
                } // end if
                
                
                
            } // end outer while
        } catch (IOException e) {
            System.out.println(e);
        }
        
        /**
         * es wird eine Matrix mit moeglichen Verbindungen zwischen zwei Servicefahrten erstellt
         */
        validEdges = new HashMap<String, Integer>();
        
        for (Entry i: servicejourneys.entrySet()){
			for (Entry j: servicejourneys.entrySet()) {
				if(i==j){
					validEdges.put(""+ i.getKey() + j.getKey(), 0);
				}
				else{
					if(feasibilityHelper.zeitpufferZwischenServicefahrten(""+i.getKey(), ""+j.getKey(), deadruntimes, servicejourneys) >= 0){
						validEdges.put(""+ i.getKey() + j.getKey(), 1);
					}
					else{
						validEdges.put(""+ i.getKey() + j.getKey(), 0);
					}
				}
			}
		}   
	}
}

