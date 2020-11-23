/**
 * The Candidato class
 * 
 * It contains the name and ID of the given candidate that is running in the election.
 * 
 * @author: Gabriel Y.Diaz Morro (@gabrieldiazmorro)
 * @version: 2.0
 * @since 2020-03-04
 */
public class Candidato {
	String name;
	int CandidateID;
	
	public Candidato(String name, int candidateID) {
		this.name = name;
		CandidateID = candidateID;
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
	
}
