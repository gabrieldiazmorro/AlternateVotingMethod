/**
 * The Vote class
 * 
 * It contains the rank and CandidateID of the given vote that is in a ballot.
 * 
 * @author: Gabriel Y.Diaz Morro (@gabrieldiazmorro)
 * @version: 2.0
 * @since 2020-03-04
 */
public class Vote {
	int rank;
	int CandidateID;
	
	public Vote(int rank, int candidateID) {
		this.rank = rank;
		CandidateID = candidateID;
	}
	
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public int getCandidateID() {
		return CandidateID;
	}
	public void setCandidateID(int candidateID) {
		CandidateID = candidateID;
	}
	//used for testing purposes
	public void printVote() {
		System.out.println("ID:" + CandidateID + " Rank:" + rank);
	}
	
}
