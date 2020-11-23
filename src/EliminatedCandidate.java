/**
 * The EliminatedCandidate class
 * 
 * Used to save all the information needed of an eliminate candidate.
 * 
 * @author: Gabriel Y.Diaz Morro (@gabrieldiazmorro)
 * @version: 2.0
 * @since 2020-03-04
 */
public class EliminatedCandidate {
	String name;
	int CandidateID;
	int amountOf1;
	
	public EliminatedCandidate(String name, int candidateID, int amountOf1) {
		super();
		this.name = name;
		CandidateID = candidateID;
		this.amountOf1 = amountOf1;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCandidateID() {
		return CandidateID;
	}

	public void setCandidateID(int candidateID) {
		CandidateID = candidateID;
	}

	public int getAmountOf1() {
		return amountOf1;
	}

	public void setAmountOf1(int amountOf1) {
		this.amountOf1 = amountOf1;
	}
	//used for testing purposes
	public void printLoser() {
		System.out.println("The loser is " + name + " he lost with #" + amountOf1 + " of 1's");
	}
	
}
