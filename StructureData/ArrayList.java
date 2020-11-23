

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayList<E> implements List<E> {
	
	// private fields
	private int currentSize;
	
	private E elements[];
	
	private class ListIterator<E> implements Iterator<E>{
		
		private int currentPosition;
		
		public ListIterator(){
			this.currentPosition = 0;
		}

		@Override
		public boolean hasNext() {
			return this.currentPosition < size();
		}

		@Override
		public E next() {
			if (this.hasNext()){
				return (E) elements[this.currentPosition++];
			}
			else {
				throw new NoSuchElementException();
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
	
	public ArrayList(){
		this(10);
	}
	public ArrayList(int initialCapacity){
		if (initialCapacity < 1){
			throw new IllegalArgumentException("Capacity must be at least 1.");
		}
		this.currentSize = 0;
		this.elements = (E[]) new Object[initialCapacity];
	}
	


	@Override
	public Iterator<E> iterator() {
		return new ListIterator<E>();
	}

	@Override
	public void add(E obj) {
		if (obj == null){
			throw new IllegalArgumentException("Argument object cannot be null.");
		}
		else {
			if (this.currentSize == this.elements.length){
				reAllocate();
			}
			this.elements[this.currentSize++] = obj;
			return;
		}
	}

	private void reAllocate() {
		// create a new array with the twice the size
		E newElements[] = (E[]) new Object[2*this.elements.length];
		// copy all values into the new array
		for (int i=0; i < this.currentSize; ++i){
			newElements[i] = this.elements[i];
		}
		// replace old elmenets with newElements
		this.elements = newElements;
	}

	@Override
	public void add(int index, E obj) {
		if (obj == null){
			throw new IllegalArgumentException("Argument object cannot be null.");
		}
		if (index == this.size()){
			this.add(obj);
		}
		else if (index >= 0 && index < this.currentSize){
			if (this.currentSize == this.elements.length){
				reAllocate();
			}
			// move everybody one spot to the back
			for (int i=this.currentSize; i > index; --i){
				this.elements[i] = this.elements[i-1];
			}
			// add element at position index
			this.elements[index] = obj;
			this.currentSize++;
		}
		else {
			throw new ArrayIndexOutOfBoundsException();
		}
	}

	@Override
	public boolean remove(E obj) {
		if (obj == null){
			throw new IllegalArgumentException("Argument object cannot be null.");
		}
		// first find obj in the array
		int target = -1;
		for (int i=0; i < this.currentSize; ++i){
			if (this.elements[i].equals(obj)){
				// found it
				target = i;
				break;
			}
		}
		if (target == -1){
			return false;
		}
		else {
			for (int i= target; i < this.currentSize - 1; ++i){
				this.elements[i] = this.elements[i+1];
			}
			// reduce size of list and remove extra last reference
			this.elements[--this.currentSize] = null;
			return true;
		}
	}

	@Override
	public boolean remove(int index) {
		if (index >= 0 && index < this.currentSize){
			for (int i= index; i < this.currentSize - 1; ++i){
				this.elements[i] = this.elements[i+1];
			}
			// reduce size of list and remove extra last reference
			this.elements[--this.currentSize] = null;
			return true;

		}
		else {
			throw new ArrayIndexOutOfBoundsException();
		}
	}

	@Override
	public int removeAll(E obj) {
		int counter = 0;
		while (this.remove(obj)){
			counter++;
		}
		return counter;
	}

	@Override
	public E get(int index) {
		if (index>=0 && index < this.size()){
			return this.elements[index];
		}
		else {
			throw new ArrayIndexOutOfBoundsException();
		}
	}

	@Override
	public E set(int index, E obj) {
		if (obj == null){
			throw new IllegalArgumentException("Object cannot be null");
		}
		if (index>=0 && index < this.size()){
			E temp = this.elements[index];
			this.elements[index] = obj;
			return temp;
		}
		else {
			throw new ArrayIndexOutOfBoundsException();
		}
	}

	@Override
	public E first() {
		if (this.isEmpty()){
			return null;
		}
		else {
			return this.elements[0];
		}
	}

	@Override
	public E last() {
		if (this.isEmpty()){
			return null;
		}
		else {
			return this.elements[this.currentSize-1];
		}
	}

	@Override
	public int firstIndex(E obj) {
		for (int i=0; i < this.currentSize; ++i){
			if (this.elements[i].equals(obj)){
				return i;
			}
		}
		return -1;
	}

	@Override
	public int lastIndex(E obj) {
		for (int i= this.currentSize-1; i >= 0; --i){
			if (this.elements[i].equals(obj)){
				return i;
			}
		}
		return -1;
	}

	@Override
	public int size() {
		return this.currentSize;
	}

	@Override
	public boolean isEmpty() {
		return this.size() == 0;
	}

	@Override
	public boolean contains(E obj) {
		return this.firstIndex(obj) >= 0;
	}

	@Override
	public void clear() {
		for (int i=0; i < this.currentSize; ++i){
			this.elements[i] = null;
		}
		this.currentSize = 0;
	}
	
	public int count(E obj) {
		int count=0;
		for (E e : elements) {
			if (obj.equals(e)) {
				count++;
			}
		}
		return count;
	}

	@Override
	public int replaceAll(E e, E f) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<E> reverse() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean equals(List<E> l) {
		// TODO Auto-generated method stub
		return false;
	}

}
