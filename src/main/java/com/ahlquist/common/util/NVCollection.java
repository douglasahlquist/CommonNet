package com.ahlquist.common.util;

import java.util.Hashtable;
import java.util.Enumeration;
import com.ahlquist.common.util.VArray;

public class NVCollection {
	Hashtable hash;

	//
	int currentIndex = 0;

	public NVCollection() {
		hash = new Hashtable();
	}

	public void put(String _key, String _value) {
		// check to see if there is a current value array
		VArray array = get(_key);
		if (array == null) {
			array = new VArray();
		}
		array.addElement(_value);
		hash.put(_key, array);
	}

	public Enumeration keys() {
		return hash.keys();
	}

	public int size() {
		return hash.size();
	}

	public VArray get(String _key) {
		return (VArray) hash.get(_key);
	}

	public void remove(String _key) {
		hash.remove(_key);
	}

	public Hashtable getHash() {
		return this.hash;
	}
	/*
	 * public class Values { private int currentIndex =0; private VArray array;
	 * 
	 * public Values(String _value) { array = new VArray();
	 * array.addElement(_value); }
	 * 
	 * public Values(VArray _array) { array = _array; }
	 * 
	 * public void put(String _value) { array.addElement(_value); }
	 * 
	 * public boolean hasMoreElements() { if(currentIndex < array.size()){
	 * return true; }else{ return false; } } public String getNextElement() {
	 * return(String)array.elementAt(currentIndex++); } public void resetIndex()
	 * { currentIndex = 0; }
	 * 
	 * public VArray getArray() { return array; } }
	 */
}
