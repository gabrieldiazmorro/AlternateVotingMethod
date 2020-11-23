

import java.util.Iterator;
import java.util.NoSuchElementException;

public class StaticBag implements Bag {

	// elements to be stored in the bag
	public Object[] elements;
	
	// current size
	private int currentSize;
	
	private class BagIterator implements Iterator {
		private int currentPosition;

		@Override
		public boolean hasNext() {
			return this.currentPosition < size();
		}

		@Override
		public Object next() {
			if (this.hasNext()) {
				Object result = elements[this.currentPosition++];
				return result;
			}
			else
				throw new NoSuchElementException();
		}
		
	}

	public StaticBag(int maxSize) {
		if (maxSize < 1)
			throw new IllegalArgumentException("Bag size must be at least 1");
		this.elements = new Object[maxSize];
		this.currentSize = 0;
	}

	@Override
	public void add(Object obj) {
		if (this.size() == this.elements.length)
			throw new IllegalStateException("Trying to insert into a full Bag.");
		else
			// enough space
			this.elements[this.currentSize++] = obj;
	}

	@Override
	public boolean erase(Object obj) {
		for (int i = 0; i < this.size(); i++)
		{
			if (elements[i].equals(obj)) // Found it
			{
				elements[i] = elements[--this.currentSize];
				elements[this.currentSize] = null; // avoid memory leak
				return true;
			}
		}
		// If we're still here, we didn't find the element
		return false;
	}

	@Override
	public int eraseAll(Object obj) {
		int result = 0;
		while (this.erase(obj))
			++result;
			
		return result;
	}

	@Override
	public void clear() {
		for (int i = 0; i < this.size(); i++)
			this.elements[i] = null;
		this.currentSize = 0;

	}

	@Override
	public int size() {
		return this.currentSize;
	}

	@Override
	public int count(Object obj) {
		int counter = 0;
		for (int i = 0; i < this.size(); i++)
		{
			if (this.elements[i].equals(obj))
				counter++;
		}
		return counter;
	}

	@Override
	public boolean isMember(Object obj) {
		return this.count(obj) > 0;
	}

	@Override
	public boolean isEmpty() {
		return this.size() == 0;
	}

	@Override
	public Iterator iterator() {
		return new BagIterator();
	}

	@Override
	public Bag moreFrequentThan(Object obj) {
		Bag temp = new StaticBag(this.currentSize);
		int count = this.count(obj);
		for (Object o : this) {
			if (this.count(o)> count && !temp.isMember(o)) {
				temp.add(o);
				
				
			}
			
		}
		return temp;
	}

}
