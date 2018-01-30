package start;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import heuristic.variableNeighborhoodSearch;
import model.Fahrzeugumlauf;
import model.Servicejourney;
import model.Stoppoint;
import output.Schedule;
import parser.ProjectReadInWithInitialSolution;

public class StartAfterInitialSolution {

	public static void main(String[] args) {
		
		ProjectReadInWithInitialSolution test = new ProjectReadInWithInitialSolution("/Users/nicolasmaeke/gitproject/simultane-planung/simultane-planung/data/full_sample_real_433_SF_207_stoppoints_initialloesung.txt");
		
		//System.out.println(test.validEdges);
		
		for (int i = 0; i < test.global.getUmlaufplan().size(); i++) {
			if(!test.global.isFeasible(test.global.getUmlaufplan().get(i))){
				System.err.println("Is not Feasible!");
			}
		}
		
		for (int i = 0; i < test.global.getUmlaufplan().size(); i++) {
			System.out.println(test.global.getUmlaufplan().get(i).toString());
		}
		int numberOfLoadingStations = 0;
		
		/**
		for (Map.Entry e: test.stoppoints.entrySet()){
			Stoppoint i1 = test.stoppoints.get(e.getKey());
			if (i1.isLadestation()) {
				numberOfLoadingStations ++;
			}
		}
		*/
		
		Schedule globalSolution = test.global;
		numberOfLoadingStations = globalSolution.getAnzahlLadestationen();
		Double initialCost = globalSolution.berechneKosten();
		System.out.println(initialCost);
		System.out.println(numberOfLoadingStations);
		System.out.println();
		
		Schedule shakingSolution = null;
		Schedule localSolution = test.local;
		
		for (int i = 0; i < test.global.getUmlaufplan().size(); i++) {
			if(!test.global.isFeasible(test.global.getUmlaufplan().get(i))){
				System.err.println("Is not Feasible!");
			}
		}
		
		int counter = 0;
		double globalCost = initialCost;
		do {
			

			variableNeighborhoodSearch verbesserung = new variableNeighborhoodSearch(localSolution, test.validEdges, test.deadruntimes, test.servicejourneys);
			
			
			shakingSolution = verbesserung.shaking();
			
			int anzahlSFNach = 0;
			for (int i = 0; i < globalSolution.getUmlaufplan().size(); i++) {
				for (int j = 0; j < globalSolution.getUmlaufplan().get(i).size(); j++) {
					if(globalSolution.getUmlaufplan().get(i).getFahrten().get(j) instanceof Servicejourney){
						anzahlSFNach++;
					}
				}
			}
			if(anzahlSFNach != 433){
				System.out.println(anzahlSFNach);
			}
			//System.out.println(anzahlSFNach);
			
			
			localSolution = verbesserung.bestImprovement(14, shakingSolution);
			int anzahlSFNach1 = 0;
			for (int i = 0; i < globalSolution.getUmlaufplan().size(); i++) {
				for (int j = 0; j < globalSolution.getUmlaufplan().get(i).size(); j++) {
					if(globalSolution.getUmlaufplan().get(i).getFahrten().get(j) instanceof Servicejourney){
						anzahlSFNach1++;
					}
				}
			}
			if(anzahlSFNach1 != 433){
				System.out.println(anzahlSFNach);
			}
			double localCost = localSolution.berechneKosten();
			if(localCost < globalCost){
				globalCost = localCost;
				System.out.println("global aktualisiert!");
				Vector<Fahrzeugumlauf> copy = new Vector<Fahrzeugumlauf>();
				for (int i = 0; i < localSolution.getUmlaufplan().size(); i++) {
					Fahrzeugumlauf neu = new Fahrzeugumlauf(localSolution.getUmlaufplan().get(i).getId());
					neu.addFahrten(localSolution.getUmlaufplan().get(i).getFahrten());
					neu.setLaden(new LinkedList<Stoppoint>(localSolution.getUmlaufplan().get(i).getLaden()));
					copy.add(neu);
				}
				HashMap<String, Stoppoint> stoppoints = new  HashMap<String, Stoppoint>();
				
				for (Map.Entry e: test.global.getStoppoints().entrySet()){
					Stoppoint neu = new Stoppoint(test.global.getStoppoints().get(e.getKey()).getId());
					stoppoints.put(test.global.getStoppoints().get(e.getKey()).getId(), neu);
				}
				Schedule globalCopy = new Schedule(copy, test.servicejourneys, test.deadruntimes, stoppoints);
				
				globalCopy.berechneFrequenzen();
				for (int i = 0; i < globalCopy.getUmlaufplan().size(); i++) {
					if(!globalCopy.isFeasible(globalCopy.getUmlaufplan().get(i))){
						System.err.println("Is not Feasible!");
					}
				}
				globalSolution = globalCopy;
			}
			int anzahlSFNach2 = 0;
			for (int i = 0; i < globalSolution.getUmlaufplan().size(); i++) {
				for (int j = 0; j < globalSolution.getUmlaufplan().get(i).size(); j++) {
					if(globalSolution.getUmlaufplan().get(i).getFahrten().get(j) instanceof Servicejourney){
						anzahlSFNach2++;
					}
				}
			}
			if(anzahlSFNach2 != 433){
				System.out.println();
			}
			System.out.println("global:" + anzahlSFNach2);
			int anzahlSFNach3 = 0;
			for (int i = 0; i < localSolution.getUmlaufplan().size(); i++) {
				for (int j = 0; j < localSolution.getUmlaufplan().get(i).size(); j++) {
					if(localSolution.getUmlaufplan().get(i).getFahrten().get(j) instanceof Servicejourney){
						anzahlSFNach3++;
					}
				}
			}
			if(anzahlSFNach3 != 433){
				System.out.println();
			}
			System.out.println("local:" + anzahlSFNach3);
			counter ++;
			System.err.println(counter);

		} while (counter < 500);
		
		globalSolution.berechneFrequenzen();
		globalSolution.setAnzahlLadestationen();
		numberOfLoadingStations = globalSolution.getAnzahlLadestationen();
		
		for (Map.Entry e: globalSolution.getStoppoints().entrySet()){
			Stoppoint i1 = globalSolution.getStoppoints().get(e.getKey());
			System.out.println(""+ i1.isLadestation() + i1.getFrequency());
		}
		
		for (int i = 0; i < globalSolution.getUmlaufplan().size(); i++) {
			if(!globalSolution.isFeasible(globalSolution.getUmlaufplan().get(i))){
				System.err.println("Is not Feasible!");
			}
		}
		
	
		int anzahlUmlaeufe = 0;
		
		for (int i = 0; i < globalSolution.getUmlaufplan().size(); i++) {
			System.out.println(globalSolution.getUmlaufplan().get(i).toString());
			System.out.println(globalSolution.getUmlaufplan().get(i).getLadenString());
			System.out.println(globalSolution.getUmlaufplan().get(i).getStellen());
			anzahlUmlaeufe ++;
		}
		
		System.out.println(globalCost);
		System.out.println(anzahlUmlaeufe);
		System.out.println(initialCost - globalCost);
		System.out.println(numberOfLoadingStations);
		
		int anzahlSF = 0;
		List<String> sf = new LinkedList<String>();
		for (int i = 0; i < globalSolution.getUmlaufplan().size(); i++) {
			for (int j = 0; j < globalSolution.getUmlaufplan().get(i).getFahrten().size(); j++) {
				if(globalSolution.getUmlaufplan().get(i).getFahrten().get(j) instanceof Servicejourney){
					anzahlSF++;
					if(sf.contains(globalSolution.getUmlaufplan().get(i).getFahrten().get(j).getId())){
						System.out.println("Fehler"+ globalSolution.getUmlaufplan().get(i).getFahrten().get(j).getId());
					}
					else{
						sf.add(globalSolution.getUmlaufplan().get(i).getFahrten().get(j).getId());
					}
				}
			}	
		}
		System.out.println(anzahlSF);
		
	}
	
	
}
