/*   @(#)  VArray.java  2002-02-04
*
*  Copyright(C) 2002, All Rights Reserved.
*  Ahlquist.com
*  516 Suisse Drive
*  San Jose, California 95123
*  U.S.A.
*
*  This document contains information proprietary and confidential to
*  Ahlquist.com, which is either copyrighted or which a
*  patent has been applied and/or protected by trade secret laws.
*
*  This document, or any parts thereof, may not be used, disclosed,
*  or reproduced in any form, by any method, or for any purpose without
*  the express written permission of Ahlquist.com.
*
*
*/

package com.ahlquist.common.util;

import java.util.*;

/**
 * The <code>VArray</code> class implements a growable array of objects. It is
 * similar to Vector but lacks its sychronization. It should give better
 * performance when there is no thread contention.
 *
 * @author Douglas Ahlquist
 * @version 1.0, 2000.12.13
 */
public class VArray extends MyObject implements java.io.Serializable, Cloneable {
	private Object elementData[];
	private int elementCount;
	private int capacityIncrement;

	/**
	 * Constructs an empty varray with the specified initial capacity and
	 * capacity increment.
	 *
	 * @param initialCapacity
	 *            the initial capacity of the varray.
	 * @param capacityIncrement
	 *            the amount by which the capacity is increased when the varray
	 *            overflows.
	 */
	public VArray(int initialCapacity, int capacityIncrement) {
		super();
		this.elementData = new Object[initialCapacity];
		this.capacityIncrement = capacityIncrement;
	}

	/**
	 * Constructs an empty varray with the specified initial capacity.
	 *
	 * @param initialCapacity
	 *            the initial capacity of the varray.
	 */
	public VArray(int initialCapacity) {
		this(initialCapacity, 0);
	}

	/**
	 * Constructs an empty varray with a capacity of 10.
	 */
	public VArray() {
		this(10);
	}

	/**
	 * Constructs an varray containing the elements from the specfied
	 * Enumeration.
	 *
	 * @param e
	 *            an Enumeration containing the elements to copy.
	 */
	public VArray(Enumeration e) {
		this();

		while (e.hasMoreElements())
			addElement(e.nextElement());
	}

	/**
	 * Constructs an empty varray with the specified initial capacity.
	 *
	 * @param elements
	 *            an array of objects with which to initialize VArray.
	 */
	public VArray(Object[] elements) {
		this();

		// FUTURE: Just set elementData = elements?
		for (int i = 0; i < elements.length; i++)
			addElement(elements[i]);
	}

	/**
	 * Copies the components of this varray into the specified array. The array
	 * must be big enough to hold all the objects in this varray.
	 *
	 * @param anArray
	 *            the array into which the components get copied.
	 */
	public final void copyInto(Object anArray[]) {
		int i = elementCount;
		while (i-- > 0) {
			anArray[i] = elementData[i];
		}
	}

	/**
	 * Trims the capacity of this varray to be the varray's current size. An
	 * application can use this operation to minimize the storage of a varray.
	 */
	public final void trimToSize() {
		int oldCapacity = elementData.length;
		if (elementCount < oldCapacity) {
			Object oldData[] = elementData;
			elementData = new Object[elementCount];
			System.arraycopy(oldData, 0, elementData, 0, elementCount);
		}
	}

	/**
	 * Increases the capacity of this varray, if necessary, to ensure that it
	 * can hold at least the number of components specified by the minimum
	 * capacity argument.
	 *
	 * @param minCapacity
	 *            the desired minimum capacity.
	 */
	public final void ensureCapacity(int minCapacity) {
		int oldCapacity = elementData.length;
		if (minCapacity > oldCapacity) {
			Object oldData[] = elementData;
			int newCapacity = (capacityIncrement > 0) ? (oldCapacity + capacityIncrement) : (oldCapacity * 2);
			if (newCapacity < minCapacity) {
				newCapacity = minCapacity;
			}
			elementData = new Object[newCapacity];
			System.arraycopy(oldData, 0, elementData, 0, elementCount);
		}
	}

	/**
	 * Sets the size of this varray. If the new size is greater than the current
	 * size, new <code>null</code> items are added to the end of the varray. If
	 * the new size is less than the current size, all components at index
	 * <code>newSize</code> and greater are discarded.
	 *
	 * @param newSize
	 *            the new size of this varray.
	 */
	public final void setSize(int newSize) {
		if (newSize > elementCount) {
			ensureCapacity(newSize);
		} else {
			for (int i = newSize; i < elementCount; i++) {
				elementData[i] = null;
			}
		}
		elementCount = newSize;
	}

	/**
	 * Returns the current capacity of this varray.
	 *
	 * @return the current capacity of this varray.
	 */
	public final int capacity() {
		return elementData.length;
	}

	/**
	 * Returns the number of components in this varray.
	 *
	 * @return the number of components in this varray.
	 */
	public final int size() {
		return elementCount;
	}

	/**
	 * Tests if this varray has no components.
	 *
	 * @return <code>true</code> if this varray has no components;
	 *         <code>false</code> otherwise.
	 */
	public final boolean isEmpty() {
		return elementCount == 0;
	}

	/**
	 * Tests if the specified object is a component in this varray.
	 *
	 * @param elem
	 *            an object.
	 * @return <code>true</code> if the specified object is a component in this
	 *         varray; <code>false</code> otherwise.
	 */
	public final boolean contains(Object elem) {
		return indexOf(elem, 0) >= 0;
	}

	/**
	 * Searches for the first occurence of the given argument, testing for
	 * equality using the <code>equals</code> method.
	 *
	 * @param elem
	 *            an object.
	 * @return the index of the first occurrence of the argument in this varray;
	 *         returns <code>-1</code> if the object is not found.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public final int indexOf(Object elem) {
		return indexOf(elem, 0);
	}

	/**
	 * Searches for the first occurence of the given argument, beginning the
	 * search at <code>index</code>, and testing for equality using the
	 * <code>equals</code> method.
	 *
	 * @param elem
	 *            an object.
	 * @param index
	 *            the index to start searching from.
	 * @return the index of the first occurrence of the object argument in this
	 *         varray at position <code>index</code> or later in the varray;
	 *         returns <code>-1</code> if the object is not found.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public final int indexOf(Object elem, int index) {
		for (int i = index; i < elementCount; i++) {
			if (elem.equals(elementData[i])) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the last occurrence of the specified object in this
	 * varray.
	 *
	 * @param elem
	 *            the desired component.
	 * @return the index of the last occurrence of the specified object in this
	 *         varray; returns <code>-1</code> if the object is not found.
	 * 
	 */
	public final int lastIndexOf(Object elem) {
		return lastIndexOf(elem, elementCount - 1);
	}

	/**
	 * Searches backwards for the specified object, starting from the specified
	 * index, and returns an index to it.
	 *
	 * @param elem
	 *            the desired component.
	 * @param index
	 *            the index to start searching from.
	 * @return the index of the last occurrence of the specified object in this
	 *         varray at position less than <code>index</code> in the varray;
	 *         <code>-1</code> if the object is not found.
	 */
	public final int lastIndexOf(Object elem, int index) {
		for (int i = index; i >= 0; i--) {
			if (elem.equals(elementData[i])) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the component at the specified index.
	 *
	 * @param index
	 *            an index into this varray.
	 * @return the component at the specified index.
	 * @exception ArrayIndexOutOfBoundsException
	 *                if an invalid index was given.
	 */
	public final Object elementAt(int index) {
		if (index >= elementCount) {
			throw new ArrayIndexOutOfBoundsException(index + " >= " + elementCount);
		}
		/*
		 * Since try/catch is free, except when the exception is thrown, put in
		 * this extra try/catch to catch negative indexes and display a more
		 * informative error message. This might not be appropriate, especially
		 * if we have a decent debugging environment - JP.
		 */
		try {
			return elementData[index];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException(index + " < 0");
		}
	}

	/**
	 * Returns the first component of this varray.
	 *
	 * @return the first component of this varray.
	 * @exception NoSuchElementException
	 *                if this varray has no components.
	 */
	public final Object firstElement() {
		if (elementCount == 0) {
			throw new NoSuchElementException();
		}
		return elementData[0];
	}

	/**
	 * Returns the last component of the varray.
	 *
	 * @return the last component of the varray, i.e., the component at index
	 *         <code>size()&nbsp;-&nbsp;1</code>.
	 * @exception NoSuchElementException
	 *                if this varray is empty.
	 */
	public final Object lastElement() {
		if (elementCount == 0) {
			throw new NoSuchElementException();
		}
		return elementData[elementCount - 1];
	}

	/**
	 * Sets the component at the specified <code>index</code> of this varray to
	 * be the specified object. The previous component at that position is
	 * discarded.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code> and
	 * less than the current size of the varray.
	 *
	 * @param obj
	 *            what the component is to be set to.
	 * @param index
	 *            the specified index.
	 * @exception ArrayIndexOutOfBoundsException
	 *                if the index was invalid.
	 * @see com.ahlquist.common.util.VArray#size()
	 */
	public final void setElementAt(Object obj, int index) {
		if (index >= elementCount) {
			throw new ArrayIndexOutOfBoundsException(index + " >= " + elementCount);
		}
		elementData[index] = obj;
	}

	/**
	 * Deletes the component at the specified index. Each component in this
	 * varray with an index greater or equal to the specified <code>index</code>
	 * is shifted downward to have an index one smaller than the value it had
	 * previously.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code> and
	 * less than the current size of the varray.
	 *
	 * @param index
	 *            the index of the object to remove.
	 * @exception ArrayIndexOutOfBoundsException
	 *                if the index was invalid.
	 * @see com.ahlquist.common.util.VArray#size()
	 */
	public final void removeElementAt(int index) {
		if (index >= elementCount) {
			throw new ArrayIndexOutOfBoundsException(index + " >= " + elementCount);
		} else if (index < 0) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		int j = elementCount - index - 1;
		if (j > 0) {
			System.arraycopy(elementData, index + 1, elementData, index, j);
		}
		elementCount--;
		elementData[elementCount] = null; /* to let gc do its work */
	}

	/**
	 * Removes the first element.
	 *
	 * @exception ArrayIndexOutOfBoundsException
	 *                if no elements.
	 */
	public final Object removeFirstElement() {
		if (elementCount == 0)
			return (null);
		Object o = elementAt(0);
		removeElementAt(0);
		return (o);
	}

	/**
	 * Removes the last element
	 *
	 * @exception ArrayIndexOutOfBoundsException
	 *                if no elements.
	 */
	public final Object removeLastElement() {
		if (elementCount == 0)
			return (null);

		elementCount--;
		Object o = elementData[elementCount];
		elementData[elementCount] = null; // to let gc do its work
		return (o);
	}

	/**
	 * Inserts the specified object as a component in this varray at the
	 * specified <code>index</code>. Each component in this varray with an index
	 * greater or equal to the specified <code>index</code> is shifted upward to
	 * have an index one greater than the value it had previously.
	 * <p>
	 * The index must be a value greater than or equal to <code>0</code> and
	 * less than or equal to the current size of the varray.
	 *
	 * @param obj
	 *            the component to insert.
	 * @param index
	 *            where to insert the new component.
	 * @exception ArrayIndexOutOfBoundsException
	 *                if the index was invalid.
	 * @see com.ahlquist.common.util.VArray#size()
	 */
	public final void insertElementAt(Object obj, int index) {
		if (index >= elementCount + 1) {
			throw new ArrayIndexOutOfBoundsException(index + " > " + elementCount);
		}
		ensureCapacity(elementCount + 1);
		System.arraycopy(elementData, index, elementData, index + 1, elementCount - index);
		elementData[index] = obj;
		elementCount++;
	}

	/**
	 * Adds the specified component to the end of this varray, increasing its
	 * size by one. The capacity of this varray is increased if its size becomes
	 * greater than its capacity.
	 *
	 * @param obj
	 *            the component to be added.
	 */
	public final void addElement(Object obj) {
		ensureCapacity(elementCount + 1);
		elementData[elementCount++] = obj;
	}

	/**
	 * Removes the first occurrence of the argument from this varray. If the
	 * object is found in this varray, each component in the varray with an
	 * index greater or equal to the object's index is shifted downward to have
	 * an index one smaller than the value it had previously.
	 *
	 * @param obj
	 *            the component to be removed.
	 * @return <code>true</code> if the argument was a component of this varray;
	 *         <code>false</code> otherwise.
	 */
	public final boolean removeElement(Object obj) {
		int i = indexOf(obj);
		if (i >= 0) {
			removeElementAt(i);
			return true;
		}
		return false;
	}

	/**
	 * Removes all components from this varray and sets its size to zero.
	 *
	 */
	public final void removeAllElements() {
		for (int i = 0; i < elementCount; i++) {
			elementData[i] = null;
		}
		elementCount = 0;
	}

	/**
	 * Returns a clone of this varray.
	 * 
	 * @return a clone of this varray.
	 */
	public Object clone() {
		try {
			VArray v = (VArray) super.clone();
			v.elementData = new Object[elementCount];
			System.arraycopy(elementData, 0, v.elementData, 0, elementCount);
			return v;
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	/**
	 * add another VArray to the end of this array
	 * 
	 * @param newArray
	 *            Another array to be appended to this one
	 */
	public void append(VArray newArray) {
		for (int i = 0; i < newArray.size(); i++)
			addElement(newArray.elementAt(i));
	}

	/**
	 * replaces the object at the index with the input afgument _new
	 * 
	 * @param _new
	 *            - the new Object to place into the VArray
	 * @param index
	 *            - the index where to place the new Object
	 * @return the Object replaced
	 * @since 2001-05-04
	 */
	public Object replaceObject(Object _new, int index) {
		int i = size();
		this.addElement(_new);
		Object obj = this.elementAt(index);
		this.swap(index, i);
		this.removeElementAt(i);
		return obj;
	}

	/**
	 * Combines two VArrays into one, removing duplicates. this is public static
	 * method
	 *
	 * @param secondArray
	 *            Another array to be merged with this one
	 */
	public void merge(VArray secondVArray) {
		for (int i = secondVArray.size(); i-- > 0;) {
			Object obj = secondVArray.elementAt(i);
			// only add the element if it does not exist at target
			if (!this.contains(obj))
				this.addElement(obj);
		}
	}

	/**
	 * this static method will merge two VArrays into one and also sort them in
	 * ascend order, objects in those VArrays should implment Sortable interface
	 *
	 * @param iArray
	 *            first VArray needs to be merged
	 * @param jArray
	 *            second VArray needs to be merged
	 *
	 * @return a VArray combine two VArrays and sort them in ascend order
	 */
	public static VArray sortedMerge(VArray iArray, VArray jArray) {
		VArray sortedObjects = new VArray(iArray.size() + jArray.size());
		for (int i = 0, j = 0; i <= iArray.size() || j <= jArray.size();) {
			if (i == (iArray.size())) {
				// we are at the end of iArray, so add all remainning
				// jArray to sortedObjects
				for (; j < jArray.size(); j++) {
					sortedObjects.addElement(jArray.elementAt(j));
				}
				break;
			}
			if (j == (jArray.size())) {
				// we are at the end of jArray, so add all remainning
				// iArray to sortedObjects
				for (; i < iArray.size(); i++) {
					sortedObjects.addElement(iArray.elementAt(i));
				}
				break;
			}
			Sortable iObj = (Sortable) iArray.elementAt(i);
			Sortable jObj = (Sortable) jArray.elementAt(j);
			if (iObj.getPosition() < jObj.getPosition()) {
				sortedObjects.addElement(iObj);
				i++;
			} else {
				sortedObjects.addElement(jObj);
				j++;
			}
		}
		return (sortedObjects);
	}

	/**
	 * this method will sort the VArray
	 *
	 * @param compare
	 *            A object implement Compare interface so that it knows how to
	 *            sort objects in this VArray
	 * @param order
	 *            tell us if you want sort in ascend (true) or descend order
	 */
	public void sort(Compare compare, boolean order) {
		quickSort(compare, order, 0, this.size() - 1);
	}

	/**
	 * This is a generic version of C.A.R Hoare's Quick Sort algorithm.
	 * Algorithm copied and modified to accomodate our VArray.
	 * 
	 * If you think of a one dimensional array as going from the lowest index on
	 * the left to the highest index on the right then the parameters to this
	 * function are lowest index or left and highest index or right. The first
	 * time you call this function it will be with the parameters 0,
	 * VArray.size() - 1.
	 *
	 * @param compare
	 *            A object implement Compare interface so that it knows how to
	 *            sort objects in this VArray
	 * @param order
	 *            tell us if you want sort in ascend (true) or descend order
	 * @param lo0
	 *            left boundary of array partition
	 * @param hi0
	 *            right boundary of array partition
	 */
	void quickSort(Compare compare, boolean order, int lo0, int hi0) {
		int lo = lo0;
		int hi = hi0;
		Object mid;
		if (hi0 > lo0) {
			/*
			 * Arbitrarily establishing partition element as the midpoint of the
			 * array.
			 */
			mid = elementData[(lo0 + hi0) / 2];
			// loop through the array until indices cross
			while (lo <= hi) {
				/*
				 * find the first element that is greater than or equal to the
				 * partition element starting from the left Index.
				 */
				while ((lo < hi0) && ((order && compare.compare(elementData[lo], mid) < 0)
						|| (!order && compare.compare(elementData[lo], mid) > 0)))
					++lo;
				/*
				 * find an element that is smaller than or equal to the
				 * partition element starting from the right Index.
				 */
				while ((hi > lo0) && ((order && compare.compare(elementData[hi], mid) > 0)
						|| (!order && compare.compare(elementData[hi], mid) < 0)))
					--hi;
				// if the indexes have not crossed, swap
				if (lo <= hi) {
					swap(lo, hi);
					++lo;
					--hi;
				}
			}
			/*
			 * If the right index has not reached the left side of array must
			 * now sort the left partition.
			 */
			if (lo0 < hi)
				quickSort(compare, order, lo0, hi);
			/*
			 * If the left index has not reached the right side of array must
			 * now sort the right partition.
			 */
			if (lo < hi0)
				quickSort(compare, order, lo, hi0);
		}
	}

	private void swap(int i, int j) {
		Object t;
		t = elementData[i];
		elementData[i] = elementData[j];
		elementData[j] = t;
	}

	/**
	 * This is an implementation of the bubble sort algorithm. Sorting is done
	 * in ascending order. (Change your compare method if you want it
	 * descending!)
	 *
	 * @param compare
	 *            an object implementing the Compare interface
	 */
	public void bubbleSort(Compare compare) {
		for (int i = this.size(); --i >= 0;) {
			boolean swapped = false;
			for (int j = 0; j < i; j++) {
				if (compare.compare(elementData[j], elementData[j + 1]) > 0) {
					swap(j, j + 1);
					swapped = true;
				}
			}
			if (!swapped)
				return;
		}
	}

	/**
	 * Tests if the specified object is equal to one of the objects in this
	 * VArray.
	 *
	 * @param compare
	 *            A object implement Compare interface so that it knows how to
	 *            compare the objects in this VArray
	 * @param elem
	 *            an object.
	 * @return <code>true</code> Tests if the specified object is equal to one
	 *         of the objects in this VArray; <code>false</code> otherwise.
	 */
	public final boolean contains(Object elem, Compare compare) {
		for (int i = 0; i < elementCount; i++) {
			if (compare.compare(elem, elementData[i]) == 0)
				return true;
		}
		return false;
	}

	/**
	 * Remove multiple entries.
	 *
	 * @param compare
	 *            A object implement Compare interface so that it knows how to
	 *            compare objects in this VArray
	 */
	public final void removeMultiple(Compare compare) {
		for (int i = 0; i < elementCount; i++) {
			for (int j = i + 1; j < elementCount; j++) {
				if (compare.compare(elementData[j], elementData[i]) == 0) {
					removeElementAt(j);
					j--;
				}
			}
		}
	}

	/*
	 * Find all elements in current VArray belong to the standard defined in the
	 * VArray standard and compare
	 */
	public final VArray getSublist(VArray standard, Compare compare) {
		VArray sublist = new VArray();
		for (int i = 0; i < elementCount; i++) {
			for (int j = 0; j < standard.size(); j++) {
				if (compare.compare(standard.elementAt(j), elementData[i]) == 0) {
					sublist.addElement(elementData[i]);
				}
			}
		}
		return sublist;
	}

	public String toString() {
		Object obj = null;
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < elementCount; i++) {
			// if (i > 0)
			// sb.append(", ");
			obj = elementAt(i);
			if (obj instanceof String) {
				sb.append((String) obj);
			} else {
				sb.append(obj);
			}
		}
		// sb.append(" } ");
		return (sb.toString());
	}
}
