package org.fourdnest.androidclient;

/**
 * Represents one Tag(type). The same Tag object can be attached to several
 * Eggs.
 * 
 * FIXME: if there are characters that are invalid for tag names, validate for them. 
 */
public class Tag {
	//private static final String TAG = Tag.class.getSimpleName();
	private String name;
	
	/**
	 * Creates a new Tag.
	 * @param name The user-visible string representing the tag.
	 */
	public Tag(String name){
		this.name = name;
	}
	
	/**
	 * @return the user-visible string representing the tag.
	 */
	public String getName(){
		return this.name;
	}
}