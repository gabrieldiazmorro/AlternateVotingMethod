import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
/**
 * The Election class
 * 
 * It processes all of the ballots and decides who wins the election.
 * 
 * @author: Gabriel Y.Diaz Morro (@gabrieldiazmorro)
 * @version: 2.0
 * @since 2020-03-04
 */
public class Election {
	private static DynamicSet<Ballot> allBallots; //saves all the ballots
	private static DynamicSet<Ballot> validBallots; //holds the valid ballots
	private static ArrayList<Ballot> ballotPerdedor;  //holds the ballots of an eliminated candidate to then be moved
	private static ArrayList<DynamicSet<Ballot>> candidateBallots; //holds a set of each candidate where they are ranked #1
	private static ArrayList<Candidato> candidates;  //saves the candidates ID and names.
	private static ArrayList<Integer> eliminatedID;  //to keep track of which candidates were eliminated
	public static ArrayList<EliminatedCandidate> output;  //to simplify the output process

	private static String winnerName;  //Saves all of the winner's information
	private static int winnerSize;
	private static int IDWinner;

	private static int invalidBallots; //counts the amount of invalid ballots
	private static int blankBallots; //counts the amount of blank ballots 

	public static void main(String[] args) {

		processInput(); //calls all the methods that process and organize the data
		rounds();   //finds the winner
	}
	/**
	 * Method that calls all the methods that organize and process the data.
	 * @param none
	 * @returns nothing
	 */
	public static void processInput() {
		processCandidates(); //gets all the Candidates and places them in a list
		processBallots();  //process the ballots, and saves them in the proper set and counts the invalid and blank ballots
		organizeBallots(); //separates the ballots for each candidate in a set, where they are ranked #1
	}
	/**
	 * Method that reads the ballots.csv input and stores all the ballots.
	 * It checks if a ballot is valid to save in the proper set.
	 * Counts the amount of blank and invalid ballots.
	 * 
	 * @param none
	 * @returns nothing
	 */
	private static void processBallots() {
		allBallots = new DynamicSet<>();
		validBallots = new DynamicSet<>();
		String row;
		BufferedReader csvReader = null;
		try { 
			csvReader = new BufferedReader(new FileReader("res/ballots.csv"));   //reads file
			while ((row = csvReader.readLine()) != null) {
				// do something with the data
				String[] ballot = row.split(","); //processing the data 
				ArrayList<Vote> votos = new ArrayList<Vote>(); //guarda todo voto que es parte del ballot
				if (ballot.length>=1) {
					for (int i = 1; i < ballot.length; i++) {
						String[] rankeo = ballot[i].split(":"); //divides each ID and rank in the format 5:1 
						Vote votito = new Vote(Integer.parseInt(rankeo[1]), Integer.parseInt(rankeo[0]));
						votos.add(votito);   // adding each vote in the ballot to the list
					}		
					Ballot temp = new Ballot(Integer.parseInt(ballot[0]), votos);
					allBallots.add(temp); //saves the ballot, it doesn't check if its valid
				}
			}
			csvReader.close();
			//Put all valid ballots in the appropriate set
			for (Ballot ballot : allBallots) {
				ballot.validate(); //validates the ballot
				if (ballot.isValid()) {
					validBallots.add(ballot);
				}
				else { //if a ballot is not valid for the count, determine if it's empty or invalid
					if (ballot.getLista().isEmpty()) {  
						blankBallots++;
					}else {
						invalidBallots++;
					}
				}
			}
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if (csvReader != null) {
				try {
					csvReader.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * Method that reads the candidates.csv input and stores all the candidates in a arraylist.
	 * @param none
	 * @returns nothing
	 */
	private static void processCandidates() {
		candidates = new ArrayList<>();
		String row;
		BufferedReader csvReader = null;
		try {
			csvReader = new BufferedReader(new FileReader("res/candidates.csv"));
			while ((row = csvReader.readLine()) != null) {
				String[] candidateInfo = row.split(",");    //create a Candidate with his name and ID
				if (candidateInfo.length>1) {
					Candidato temp = new Candidato(candidateInfo[0],Integer.parseInt(candidateInfo[1]));
					candidates.add(temp);
				}
			}
			csvReader.close();
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if (csvReader != null) {
				try {
					csvReader.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/**
	 * Method that stores all the ballots where a certain candidate is ranked #1 in a DynamicSet. 
	 * The sets are stored in an ArrayList.
	 * It also saves the ID of the candidates that have zero ballots in their set
	 * @param none
	 * @returns nothing
	 */
	public static void organizeBallots() {
		eliminatedID = new ArrayList<Integer>();   //saves the ID of the eliminated candidates
		candidateBallots = new ArrayList<>();      //stores the set of each candidate
		for (Candidato postulante : candidates) {                  //makes a set for each candidate with 
			DynamicSet<Ballot> tempSet = new DynamicSet<Ballot>();   //the ballots where they are ranked #1
			for (Ballot ballot : validBallots) {
				if (postulante.getCandidateID() == ballot.getCandidateByRank(1)) { 
					//gets ballots where the candidate is ranked #1
					tempSet.add(ballot);
				}
			}
			if (tempSet.isEmpty()) {  //if the candidate has no ballots where he #1, he will most likely be eliminated first unless there is a tie
				eliminatedID.add(postulante.getCandidateID());
			}else {
				candidateBallots.add(tempSet);
			}
		}
	}
	/**
	 * Method that runs the elimination rounds to determine the Winner of the election.
	 * It looks if there is a tie, to take the proper action(uses the tied() method).
	 * Finds the candidate with the lowest amount of #1 rank to be eliminated.
	 * After eliminating a candidate it moves his ballots to the appropriate set.
	 * @param none
	 * @returns nothing
	 */
	public static void rounds() {
		ballotPerdedor = new ArrayList<Ballot>(); //saves the ballots of the candidate eliminated
		output = new ArrayList<EliminatedCandidate>(); //saves the eliminated candidates with the amount of #1's they got eliminated with

		//if there is a candidate that had 0 ballots in it's set,  we will remove them before starting the rounds 
		//to save time, and avoid having to remove them later
		if (eliminatedID.size()>0) {
			candidate0Ballots();
		}
		while(!findWinner()) {
			int loserID =0;
			int lowestAmount1=candidateBallots.get(0).size();
			ArrayList<Integer> toBeliminated = new ArrayList<Integer>(); //saves the Candidate to be eliminated
			for (DynamicSet<Ballot> set : candidateBallots) {  //find the lowest amount #1 rank for a candidate
				if (set.size()< lowestAmount1) {
					lowestAmount1 =  set.size();
				}
			}
			for (DynamicSet<Ballot> set : candidateBallots) {
				if (set.size()==lowestAmount1) { //finds the candidates with the lowest amounts of #1
					for (Ballot temp : set) {
						toBeliminated.add(temp.getCandidateByRank(1)); //adds them to the list of the one to be eliminated
						break;  //Break to just get the ID and avoid unnecessary iterations
					}
				}
			}
			if (toBeliminated.size()>1) { //if there are more than one in the list toBeliminate there is a tie
				loserID = tied(toBeliminated, 2);
			}else if(toBeliminated.size()==1) {
				loserID = toBeliminated.get(0); //else there is no tie, we just eliminated the candidate with the lowest amount of #1
			}

			//saves the loser with the amount of #1's he got eliminated with
			for (Candidato can : candidates) {
				EliminatedCandidate holder = new EliminatedCandidate(can.getName(), can.getCandidateID(), lowestAmount1);
				if (can.getCandidateID() == loserID && !eliminatedID.contains(loserID)) {
					output.add(holder);
					break;
				}
			}
			if (!eliminatedID.contains(loserID)) {
				eliminatedID.add(loserID);
			}

			toBeliminated.clear();
			for (DynamicSet<Ballot> set : candidateBallots) {
				for (Ballot papeleta : set) {
					if (papeleta.getCandidateByRank(1)== loserID) { //finds the set of ballots where the eliminated 
						papeleta.eliminate(loserID);                //was #1 rank to move them to the proper set			
						ballotPerdedor.add(papeleta);				//saves the ballots to be moved to appropiate set
						set.remove(papeleta);        
					}else {
						papeleta.eliminate(loserID);                  //remove the loser from all the ballots
					} 
				}
			}

			for (int i = 0; i < candidateBallots.size(); i++) {
				if (candidateBallots.get(i).size()==0) {  //removes the set of the candidate that lost
					candidateBallots.remove(i);			//remove the empty set
					break;
				}
			}
			for (Ballot papel : ballotPerdedor) {
				moveBallot(papel);              //the ballots of the candidates that lost, moved to the appropriate set
			}                                  	// to the set of the candidate that is now ranked #1 on it    
			ballotPerdedor.clear();
			toBeliminated.clear();


		}           //end while loop


		//Finds the amount ballots(where he is ranked #1) the Winner won with.
		winnerSize =0;
		for (DynamicSet<Ballot> set : candidateBallots) {
			for (Ballot ballot : set) {
				if (ballot.getCandidateByRank(1) == IDWinner) {
					winnerSize = set.size();
					break;
				}
			}
		}

		//Gets the Winner's name.
		for (Candidato can : candidates) {
			if (can.getCandidateID()== IDWinner) {
				winnerName = can.getName();
			}
		}
		//Since the winner has been determined, it calls this method to take the result and all the data gathered to be put in the output file.
		outputFile();
	}
	/**
	 * Method that eliminates the candidates with 0 ballots in their set.
	 * It looks if there is a tie, to take the proper action(uses the tied() method).
	 * @param none
	 * @returns nothing
	 */
	public static void candidate0Ballots() {
		ArrayList<Integer> temp = new ArrayList<Integer>(); //list will hold the candidates that have 0 ballots in their set
		for (Integer integer : eliminatedID) {
			temp.add(integer);     //added to a temporary list to manipulate the information without affecting the original
		}
		//if there is only one candidate, then remove him
		if (eliminatedID.size()==1) {   
			for (Integer i : eliminatedID) {
				for (DynamicSet<Ballot> set : candidateBallots) {
					for (Ballot papeleta : set) {
						papeleta.eliminate(i);      //removes the candidate that lost from the ballot
					}
				}
			}
			for (Integer i : eliminatedID) {
				for (Candidato can : candidates) {
					EliminatedCandidate holder = new EliminatedCandidate(can.getName(), can.getCandidateID(), 0);
					if (can.getCandidateID() == i) {
						output.add(holder);
						break;
					}
				}
			}	
		}else {   //if there is more than one candidate in the list there is a tie
			eliminatedID.clear();
			while (!temp.isEmpty()) {
				int loser = tied(temp, 2);
				for (DynamicSet<Ballot> set : candidateBallots) {
					for (Ballot papeleta : set) {
						papeleta.eliminate(loser);      //removes the candidate that lost from the ballot
					}
				}
				for (Candidato can : candidates) {
					EliminatedCandidate holder = new EliminatedCandidate(can.getName(), can.getCandidateID(), 0);
					if (can.getCandidateID() == loser) {
						output.add(holder);
						break;
					}
				}
				eliminatedID.add(loser);
				temp.removeAll(loser);
			}
		}
		temp.clear();
		temp=null;	
	}
	/**
	 * Method that takes care of gathering all the data 
	 * gathered from the roundS(), to then output the results.
	 * @param none
	 * @returns nothing
	 */
	public static void outputFile() {
		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("results.txt"), "utf-8"));
			writer.write("Number of ballots: " + allBallots.size());
			writer.write("\n");
			writer.write("Number of blank ballots: " + blankBallots);
			writer.write("\n");
			writer.write("Number of invalid ballots: " + invalidBallots);
			writer.write("\n");
			for (int i = 0; i < output.size(); i++) {
				writer.write("Round " + (i+1) + ": "+ output.get(i).getName() + " was eliminated with " + output.get(i).getAmountOf1() + " #1's");
				writer.write("\n");
			}
			writer.write("Winner: " + winnerName +  " wins with " +  winnerSize + " #1's");
		} catch (IOException ex) {
			// Report
		} finally {
			try {writer.close();} catch (Exception ex) {/*ignore*/}
		}
	}
	/**
	 * Method that moves the ballot it is given, to the set of the candidate that is ranked #1 on that ballot.
	 * @param papeleta the ballot of a candidate that was eliminated
	 * @returns nothing
	 */
	public static void moveBallot(Ballot papeleta) {
		for (DynamicSet<Ballot> set : candidateBallots) {  //gets a ballot of a candidate that was eliminated
			for (Ballot ballot1 : set) {            //and place the ballot in the proper set of the candidate that is now ranked #1
				if (ballot1.getCandidateByRank(1) == papeleta.getCandidateByRank(1) && !set.isMember(papeleta)) {
					set.add(papeleta);
					break;
				}
			}
		}
	}
	/**
	 * Method that checks if a winner can be determined, if one of the candidates has more than 50% of the votes or
	 * if there is only one candidate left because the rest got eliminated.
	 * @param none
	 * @returns boolean(true or false)
	 */

	public static boolean findWinner() {
		int winnerID = 0;
		int winnerSize =0;
		for (DynamicSet<Ballot> set  : candidateBallots) { //finds the candidate with the highest amount of #1 ranks
			for (Ballot ballot : set) {
				if (set.size() >winnerSize) {
					winnerID= ballot.getCandidateByRank(1);
					winnerSize = set.size();
				}		
			}
		}
		if (winnerSize > (validBallots.size())/2) {   //if a candidate has more than 50% of the vote, he wins 
			IDWinner= winnerID;
			return true;
		}
		if (candidateBallots.size()==1) {      //if there is only one set left it means that the 
			for (DynamicSet<Ballot> set  : candidateBallots) { //candidate left is the winner
				for (Ballot ballot : set) {
					winnerID= ballot.getCandidateByRank(1);
				}
				IDWinner = winnerID;
				return true;
			}
		}	
		return false;
	}

	/**
	 * Method that determines the loser of the candidates tied with the same amount of ballots where they are ranked #1.
	 * It counts the times the tied candidates appear in the rank being compared.
	 * Determines the candidate with the lowest amount of the rank being compared, to be eliminated.
	 * If they are tied in all the ranks, it will eliminate the candidate with the highest ID.
	 * @param none
	 * @returns idLoser or IDhighest, which is the ID of the candidate that lost
	 */
	public static int tied(ArrayList<Integer> toBeliminated, int rankCompared) { 
		//rankedCompared is which rank is being checked, for who has the most of that rank

		if (rankCompared> candidates.size()) { //if the the candidates to be eliminated are tied in every rank
			int IDhighest =toBeliminated.get(0); //If at the end no decision can be made, then, 
			for (Integer Id : toBeliminated) { //among all of those candidates that are still tied, the one
				if (IDhighest< Id) {            //having the current largest ID# is removed.
					IDhighest = Id;
				}
			}
			return IDhighest;
		}

		ArrayList<Integer> countsElim = new ArrayList<Integer>(); //holds the count of each candidate in that rank
		int pos =0;
		for (Integer ID: toBeliminated) { //loops through each candidate that might be eliminated
			int count =0;
			for (DynamicSet<Ballot> set : candidateBallots) {
				//checks all the ballots, to count where the candidate is ranked "rankCompared"
				for (Ballot ballot : set) {
					if (ballot.getCandidateByRank(rankCompared) == ID) {
						count= count+1;
					}
				}
			}
			countsElim.add(pos, count); 
			pos++;
		}
		int idLoser = toBeliminated.get(0); 
		int losercount = countsElim.get(0);
		boolean noTies =  true;
		for (Integer count : countsElim) {
			if (losercount != count) {  //if one of the counts of the candidates to be eliminated
				noTies = false;        //is different, then there is not another tie
			}
		}
		if (!noTies) {        //if they are not tied again, find the loser
			for (int i = 0; i < countsElim.size(); i++) {
				if (losercount> countsElim.get(i)) {   //finds the candidate with the lowest amount of that rank
					idLoser= toBeliminated.get(i);
					losercount=countsElim.get(i);
				}
			}
			return idLoser; //returns the ID of the candidate that lost
		}
		return tied(toBeliminated, rankCompared+1);   //if they are tied again check the next rank down
	}
}
