/**
 * The Ballot class
 * 
 * It stores all of the information of a ballot.
 * 
 * @author: Gabriel Y.Diaz Morro (@gabrieldiazmorro)
 * @version: 2.0
 * @since 2020-03-04
 */
public class Ballot {
	int BallotID;
	ArrayList<Vote> lista;
	boolean valid;

	public Ballot(int ballotID, ArrayList<Vote> lista) {
		BallotID = ballotID;
		this.lista = lista;
		valid = true;
	}
	//Checks if the ballot is valid
	public void validate() {
		//if there are no candidates the ballot is invalid.
		if (lista.isEmpty()) {
			valid = false;
		}
		//If a candidate is ranked higher than the number of candidates it is invalid.
		for (Vote vote : lista) {
			if (vote.getRank()<0 || vote.getRank()> lista.size()) {
				valid = false;
			}
			if (vote.getRank()> lista.size()) {
				valid=false;
			}
		}
		//If a rank or a ID is repeated then the ballot is invalid.
		for (int i = 0; i < lista.size(); i++) {
			for (int j = i+1; j < lista.size(); j++) {
				if (lista.get(i).getRank()== lista.get(j).getRank()) {
					valid = false;
				}
				if (lista.get(i).getCandidateID()== lista.get(j).getCandidateID()) {
					valid = false;
				}
			}
		}
	}

	public int getBallotNum() {  
		return BallotID;    // returns the ballot number
	}
	public int getRankByCandidate(int candidateId) {
		//returns the rank for that candidate
		for (Vote vote : lista) {
			if (vote.CandidateID== candidateId) {
				return vote.getRank();
			}
		}
		//if candidate is not in the ballot
		return -1;
	}
	public int getCandidateByRank(int rank) {
		// candidate with that rank
		if (rank<=0) {
			throw new IllegalArgumentException("That rank is invalid.");
		}
		for (Vote vote : lista) {
			if (vote.getRank()== rank) {
				return vote.getCandidateID();
			}
		}
		//if the rank is not on the ballot
		return -1;
	}
	/**
	 * Method that removes a candidate from the ballot, and then the candidates with a higher rank 
	 * than the one that was eliminated are moved up.
	 * @param none
	 * @returns true if it successfully eliminates a candidate
	 */
	public boolean eliminate(int candidateId) { // eliminates a candidate
		int rankRem=0;
		Vote temp =  new Vote(0,0);
		for (Vote vote : lista) {
			if(vote.CandidateID==candidateId) {
				rankRem =  vote.getRank();
				temp = vote;
			}
		}
		lista.remove(temp);
		if (rankRem>0) {
			for (Vote vote : lista) {
				if (vote.getRank() > rankRem) {
					vote.setRank(vote.getRank()-1);
				}
			}
			return true;
		}
		return false;
	}	
	public void setBallotID(int ballotID) {
		BallotID = ballotID;
	}
	public List<Vote> getLista() {
		return lista;
	}
	public void setLista(ArrayList<Vote> lista) {
		this.lista = lista;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	//used for testing purposes
	public void ballotPrint(){
		System.out.println("The ballot ID is:" + BallotID);
		for (Vote vote : lista) {
			vote.printVote();
		}
	}
}
