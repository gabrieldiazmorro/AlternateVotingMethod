import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class ElectionBackup {
	public static DynamicSet<Ballot> allBallots;
	public static DynamicSet<Ballot> validBallots;
	public static ArrayList<Ballot> ballotPerdedor;
	public static ArrayList<DynamicSet<Ballot>> candidateBallots; //holds a set of each candidate where they are ranked #1
	public static ArrayList<Candidato> candidates;
	public static ArrayList<Integer> eliminatedID;
	public static ArrayList<EliminatedCandidate> output;

	static String winnerName;
	static int winnerSize;
	static int IDWinner;

	static int invalidBallots;
	static int blankBallots;


	public static void main(String[] args) {

		processInput(); //calls all the methods that process and organize the data
		rounds();   //finds the winner

		System.out.println("There are " + allBallots.size() + " ballots.");
		System.out.println("There are " + invalidBallots + " invalid ballots.");
		System.out.println("There are " + blankBallots + " blank ballots.");
	}

	public static void processInput() {
		processCandidates(); //gets all the Candidates and places them in a list
		for (Candidato contender : candidates) {
			System.out.println(contender.getName()+ " his ID is:"+ contender.getCandidateID());
		}
		processBallots();  //process the ballots, and saves them in the proper set and counts the invalid and blank ballots
		organizeBallots(); //separates the ballots for each candidate, where they are ranked #1
	}

	private static void processBallots() {
		allBallots = new DynamicSet<>();
		validBallots = new DynamicSet<>();
		String row;
		BufferedReader csvReader = null;
		try { 
			csvReader = new BufferedReader(new FileReader("res/ballots2.csv"));   //reads file
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
					temp.ballotPrint();
					allBallots.add(temp); //saves the ballot, it doesn't check if its valid
				}

			}
			csvReader.close();
			//Put all valid ballots in the appropriate set
			for (Ballot ballot : allBallots) {
				ballot.validate();
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

	public static void organizeBallots() {
		eliminatedID = new ArrayList<Integer>();   //saves the ID of the eliminated candidates
		candidateBallots = new ArrayList<>();
		for (Candidato postulado : candidates) {                  //makes a set for each candidate with 
			DynamicSet<Ballot> temp = new DynamicSet<Ballot>();   //the ballots where they are ranked #1
			for (Ballot ballot : validBallots) {
				if (postulado.getCandidateID() == ballot.getCandidateByRank(1)) { 
					//gets ballots where the candidate is ranked #1
					temp.add(ballot);
					//ballot.ballotPrint();
				}
			}
			if (temp.isEmpty()) {  //if the candidate has no ballots where he #1, he will be eliminated first
				eliminatedID.add(postulado.getCandidateID());
			}else {
				candidateBallots.add(temp);
			}
		}
	}
	public static void rounds() {
		//eliminatedID = new ArrayList<Integer>();  //saves the ID of the eliminated candidates
		ballotPerdedor = new ArrayList<Ballot>(); //saves the ballots of the candidate eliminated
		output = new ArrayList<EliminatedCandidate>(); //saves the eliminated candidates with the amount of #1's they got eliminated with

		int count=0;  //counts amount of while loops

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
			count++;

		}           //end while loop

		System.out.println("-----------------****************------------------------------");

		System.out.println("Amount of loops " + count);

		//finds the amount #1 the first candidate won with

		winnerSize =0;
		for (DynamicSet<Ballot> set : candidateBallots) {
			for (Ballot ballot : set) {
				if (ballot.getCandidateByRank(1) == IDWinner) {
					winnerSize = set.size();
					break;
				}
			}
		}
		//gets the name of the winner
		for (Candidato can : candidates) {
			if (can.getCandidateID()== IDWinner) {
				winnerName = can.getName();
			}
		}
		for (EliminatedCandidate candidato : output) {
			candidato.printLoser();
		}

		System.out.println("The Winner ID is " + IDWinner + " and his name is " + winnerName + " and he won with #" + winnerSize + " of 1's");

		/////////////////////////////////////////////////
		for (Integer ballot : eliminatedID) {
			System.out.println(ballot);
		}
		outputFile();

	}
	public static void candidate0Ballots() {
		ArrayList<Integer> temp = new ArrayList<Integer>(); //list will hold the candidates that have 0 ballots in their set
		for (Integer integer : eliminatedID) {
			temp.add(integer);
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
	}
	
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
			System.out.println("The Winner is: " + winnerID);
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
