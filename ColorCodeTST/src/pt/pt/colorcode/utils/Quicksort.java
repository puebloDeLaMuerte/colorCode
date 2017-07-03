package pt.pt.colorcode.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class Quicksort  {

	// taken and adapted from http://www.vogella.com/tutorials/JavaAlgorithmsQuicksort/article.html
	// well, thanks!
	
	private SortElement[] elements;
    private int length;
    
    
    public String[] sortHashMapByValue(LinkedHashMap<String, Integer> data) {
    	
    	if (data ==null || data.size()==0){
            return null;
        }
    	
    	SortElement[] vals = new SortElement[data.size()];
    	
    	int i=0;
    	for( Map.Entry<String, Integer> e : data.entrySet() ) {
    		
    		vals[i] = new SortElement(e.getKey(), e.getValue());
    		i++;
    		
    		System.out.println(e.getKey() + ": " + e.getValue());
    	}
    	
    	
    	System.out.println("\n\n\n");
    	
    	sort(vals);
    	
    	String[] ret = new String[vals.length];
    	
    	for (int j = 0; j < ret.length; j++) {

    		ret[j] = (String)vals[vals.length-j-1].getObject();
    		
    		System.out.println(ret[j] + ": " + vals[j].getValue());
		}
    	
    	return ret;
    }

    private void sort(SortElement[] values) {
        // check for empty or null array
        if (values ==null || values.length==0){
            return;
        }
        this.elements = values;
        length = values.length;
        quicksort(0, length - 1);
    }

    private void quicksort(int low, int high) {
        int i = low, j = high;
        // Get the pivot element from the middle of the list
        int pivot = elements[low + (high-low)/2].getValue();

        // Divide into two lists
        while (i <= j) {
            // If the current value from the left list is smaller than the pivot
            // element then get the next element from the left list
            while (elements[i].getValue() < pivot) {
                i++;
            }
            // If the current value from the right list is larger than the pivot
            // element then get the next element from the right list
            while (elements[j].getValue() > pivot) {
                j--;
            }

            // If we have found a value in the left list which is larger than
            // the pivot element and if we have found a value in the right list
            // which is smaller than the pivot element then we exchange the
            // values.
            // As we are done we can increase i and j
            if (i <= j) {
                exchange(i, j);
                i++;
                j--;
            }
        }
        // Recursion
        if (low < j)
            quicksort(low, j);
        if (i < high)
            quicksort(i, high);
    }

    private void exchange(int i, int j) {
        SortElement temp = elements[i];
        elements[i] = elements[j];
        elements[j] = temp;
    }
    
    private class SortElement {
    	
    	
    	private int value;
    	private Object name;
    	
    	public SortElement(String name, int value) {
    		this.name = name;
    		this.value = value;
    	}
    	
    	public int getValue() {
    		return value;
    	}
    	
    	public Object getObject() {
    		return name;
    	}
    }
    
}