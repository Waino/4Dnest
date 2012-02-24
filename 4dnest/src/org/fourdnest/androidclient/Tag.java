package org.fourdnest.androidclient;

import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

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
	public Tag(CharSequence name){
		this.name = name.toString();
	}

	/**
	 * @return the user-visible string representing the tag.
	 */
	public String getName(){
		return this.name;
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Tag)) {
			return false;
		}

		Tag other = (Tag)o;

		return this.name.equalsIgnoreCase(other.name);
	}
	@Override
	public int hashCode() {
		return this.name.toLowerCase().hashCode();
	}

	/**
	 * InputFilter to constrain a text field to valid chars.
	 */
	public static class TagFilter implements InputFilter {
		/**
		 * Called by the input element each time the input text is changed.
		 * @return The altered string, or null if no changes needed
		 */
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
			StringBuilder sb = new StringBuilder();
			char cursor;
			boolean unmodified = true;

			for (int i = start; i < end; i++) {
				cursor = source.charAt(i);
				if (Character.isLetterOrDigit(cursor)) {
					// Let letters and digits through
					sb.append(cursor);
					continue;
				} else if (Character.isWhitespace(cursor)) {
					// Let whitespace through
					sb.append(cursor);
					continue;
				} else {
					unmodified = false;
				}
			}
			if (unmodified) {
				return null; // keep original
			}
			if (source instanceof Spanned) {
				SpannableString sp = new SpannableString(sb);
				TextUtils.copySpansFrom((Spanned) source,
						start, end, null, sp, 0);
				return sp;
			}
			return sb.toString();
		}
	}

}
