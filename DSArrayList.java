
/**
 * 
 * @author dsclass 2016
 * implementation of arraylist
 *  a resizable array of objects
 *  really just array with extra functionality
 */
public class DSArrayList< J > {
	/**
	 * the backing array stores the objects (references really)
	 */
	private J[] jays; // array of type j must specify the type when instantiated
	private int size; // number of elements in DSArrayList 
	/**
	 * constructor
	 */
	public DSArrayList(){
		jays = (J[]) (new Object[10]); // new backing array
		size = 0;
	}

	// insert something at end of arraylist
	public J add(J thingToAdd){
		if(jays.length == size) // too many entries in array
		{
			// resize array
			int currentLength = jays.length;
			int newLength = 2*currentLength;
			J[] newJays = (J[]) (new Object[newLength]);
			// copy old array eis new array
			for(int i = 0; i < currentLength; i++)
				newJays[i] = jays[i];
			jays = newJays; // changes the reference of jays -> newJays
		}

		jays[size] = thingToAdd;
		size++;
		return thingToAdd;
	} 


	/**
	 * returns specific item from array
	 * @param index The index of the item to be returned
	 */
	public J get(int index){
		return jays[index];
	}

	public J pop(){
		remove(size-1);
		return(jays[size-1]);
	} // returns last item in list

	public int getSize()
	{
		return size;
	}
	public void remove(int index){
		// removes element at index and shifts all others up
		//**********************************************
		// if it is the last element being removed
		J[] newJays = null;
		for(int i = 0; i < size-1; i++){
			if (i != index){
				newJays[i] = jays[i];
			}
			else
				newJays[i] = jays[i+1];
		}
		jays = newJays;
	} 

	public void set(int index, J value){
		jays[index] = value;
	} // set item to particular value at index

	public void insert(int idx, J thingToAdd) 
	// insert an item into the arrayList at the specified location
	{
		J item = thingToAdd;
		int place = idx;
		for(int i = 0; i < size; i++)
		{
			if(i == place){
				J[] newArray = (J[]) (new Object[i+1]);
				newArray = copy(jays, newArray , 0, i);
				add(item);
				newArray = copy(jays, newArray, i+1, size);
				jays = newArray;
			}
		}
	}
	
	public J[] toArray(){
		J[] rv = (J[]) new Object[size];
		// copy into this new array
		for(int i = 0; i < size; i++){
			rv[i] = jays[i];
		}
		return rv;
	}
	
	// copies all of the elements from start index up to stop from arr1 to arr2
	public J[] copy(J[] arr1, J[] arr2, int start, int stop)
	{
		for(int i = start; i <= stop; i++){
			arr2[i] = arr1[i];
		}
		 return arr2;
	}
}


