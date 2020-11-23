

import java.util.Iterator;

public class DynamicBag implements Bag {
	
	private StaticBag bag;
	private int maxSize;

	public DynamicBag(int initialCapacity) {
		this.bag = new StaticBag(initialCapacity);
		this.maxSize = initialCapacity;
	}

	@Override
	public Iterator iterator() {
		return this.bag.iterator();
	}

	@Override
	public void add(Object obj) {
		if (this.bag.size() == this.maxSize) // Current bag is full
		{
			// Allocate a new array with 2x the capacity
			this.maxSize *= 2;
			StaticBag newBag = new StaticBag(this.maxSize);
			// Copy existing elements into the new bag
			for (Object curObj : this.bag)
				newBag.add(curObj);
			// Empty out old bag and replace it with the new one
			this.bag.clear();
			this.bag = null;
			this.bag = newBag;
		}
		// Add the object that was sent
		this.bag.add(obj);

	}

	@Override
	public boolean erase(Object obj) {
		return this.bag.erase(obj);
	}

	@Override
	public int eraseAll(Object obj) {
		return this.bag.eraseAll(obj);
	}

	@Override
	public void clear() {
		this.bag.clear();
		this.maxSize = 0;
	}

	@Override
	public int size() {
		return this.bag.size();
	}

	@Override
	public int count(Object obj) {
		return this.bag.count(obj);
	}

	@Override
	public boolean isMember(Object obj) {
		return this.bag.isMember(obj);
	}

	@Override
	public boolean isEmpty() {
		return this.bag.isEmpty();
	}
 
	@Override
	public Bag moreFrequentThan(Object obj) {
		
		return this.bag.moreFrequentThan(obj);
	}

}
