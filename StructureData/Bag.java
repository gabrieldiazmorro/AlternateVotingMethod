

public interface Bag extends Iterable {

	public void add(Object obj);
	
	public boolean erase(Object obj);
	
	public int eraseAll(Object obj);
	
	public void clear();
	
	public int size();
	
	public int count(Object obj);
	
	public boolean isMember(Object obj);
	
	public boolean isEmpty();
	
	public Bag moreFrequentThan(Object obj);
}
