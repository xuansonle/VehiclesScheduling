package start;

import heuristic.variableNeighborhoodSearch;
import output.Schedule;
import parser.ProjectReadInWithInitialSolution;

public class StartAfterInitialSolution {

	public static void main(String[] args) {
		
		ProjectReadInWithInitialSolution test = new ProjectReadInWithInitialSolution("/Users/nicolasmaeke/gitproject/simultane-planung/simultane-planung/data/full_sample_real_433_SF_207_stoppoints.txt");
		
		
		for (int i = 0; i < test.fahrzeugumlaeufe.size(); i++) {
			System.out.println(test.fahrzeugumlaeufe.get(i).toString());
		}
		
		Schedule solution = new Schedule(test.fahrzeugumlaeufe, test.stoppoints);
		Double initialkosten = solution.berechneKosten();
		System.out.println(initialkosten);
		System.out.println();
		
		Schedule solution2 = null;
		
		int counter = 0;
		double neueKosten = initialkosten;
		do {
			variableNeighborhoodSearch verbesserung = new variableNeighborhoodSearch(test.fahrzeugumlaeufe, test.validEdges, test.deadruntimes, test.servicejourneys, test.stoppoints);
			verbesserung.bestImprovement();
			solution2 = new Schedule(test.fahrzeugumlaeufe, test.stoppoints);
			neueKosten = solution2.berechneKosten();
			counter ++;
			System.err.println(counter);
		} while (counter < 100);
		
		
		
		for (int i = 0; i < test.fahrzeugumlaeufe.size(); i++) {
			System.out.println(test.fahrzeugumlaeufe.get(i).toString());
		}
		System.out.println(neueKosten);
		System.out.println();
		System.out.println(initialkosten - neueKosten);
		
	}

}