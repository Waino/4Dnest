package org.fourdnest.androidclient;

import java.util.Comparator;

/**
 * A simple Comparator implementation for sorting Eggs primarly by creation time. Used in
 * EggAdapters to sort lists to display the latest entry on the top of the list
 * view.
 */
public class EggTimeComparator implements Comparator<Egg> {
	/**
	 * Compares the arguments, first by creation time and if that is equal,
	 * by author in alphabetic order followed by caption.
	 * @param arg0 Egg to prefer if return value is negative
	 * @param arg1 Egg to prefer if return value is positive
	 * @returns -1 or +1 if one egg is larger than the other. 0 in extremely rare cases. 
	 */
	public int compare(Egg arg0, Egg arg1) {
	    if (arg0.getCreationDate() == null) {
	        return 1;
	    }
	    if (arg1.getCreationDate() == null) {
	        return -1;
	    }
		// Note the minus sign. Reverses sorting order so that latest entry is displayed on top.
		int value = -arg0.getCreationDate().compareTo(arg1.getCreationDate());
		
		if (value == 0) {
			value = -arg0.getId().compareTo(arg1.getId());
		}
		if (value == 0) {
			value = -arg0.getAuthor().compareTo(arg1.getAuthor());
		}
		if (value == 0) {
			value = -arg0.getCaption().compareTo(arg1.getCaption());
		}
		
		return value;
	}

}