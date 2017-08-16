package com.wms;

import com.google.common.collect.Lists;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/*
 * SD2x Homework #1
 * Implement the methods below according to the specification in the assignment description.
 * Please be sure not to change the signature of any of the methods!
 */

public class LinkedListUtils {
	public static void main(String[] args) {
		LinkedList<Integer> one = new LinkedList<>();
		one.add(1);
		one.add(3);
		one.add(2);
		one.add(2);
		one.add(7);
		one.add(4);
		LinkedList<Integer> two = new LinkedList<>();
		System.out.println(LinkedListUtils.containsSubsequence(one,two));
	}
	
	public static void insertSorted(LinkedList<Integer> list, int value) {
		/* IMPLEMENT THIS METHOD! */
		if (list == null) {
			return;
		}
		//find the index that need to add
		int index = -1;
		int currentValue;
		for(int i = 0;i<list.size();i++){
			currentValue = list.get(i);
			if (value < currentValue) {
				index = i;
				break;
			}
		}
		//add
		if (index == -1) {
			list.addLast(value);
		}else{
			list.add(index,value);
		}
	}
	

	public static void removeMaximumValues(LinkedList<String> list, int N) {

		/* IMPLEMENT THIS METHOD! */
		if (list == null || N < 1) {
			return;
		}
		//
		LinkedList<String> lstUniqueValue = Lists.newLinkedList();
		for(String i: list){
			if (!lstUniqueValue.contains(i)) {
				lstUniqueValue.add(i);
			}
		}
		lstUniqueValue.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o2.compareTo(o1);
			}
		});
		//
		if (N > lstUniqueValue.size()) {
			list.clear();
		}else{
			List<String> lstLargest = Lists.newArrayList();
			for(int i = 0 ;i<N;i++){
				lstLargest.add(lstUniqueValue.get(i));
			}
			list.removeAll(lstLargest);
		}
	}
	
	public static boolean containsSubsequence(LinkedList<Integer> one, LinkedList<Integer> two) {

		/* IMPLEMENT THIS METHOD! */
		if (one == null || one.size() ==0 || two == null || two.size() == 0) {
			return false;
		}
		int startValue = two.get(0);
		for (int i = 0;i< one.size();i++) {
			if (one.get(i) == startValue) {
				if (checkContainAtPosition(one, two, i)) {
					return true;
				}
			}
		}
		//
		return false;
	}

	public static boolean checkContainAtPosition(LinkedList<Integer> one, LinkedList<Integer> two, int position){
		int itemCount = two.size();
		int sameCount = 0;
		int j = 0;
		for(int i = position;i < position + itemCount ;i ++){
			if (i > (one.size()-1)) {
				return  false;
			}
			if (one.get(i) == two.get(j)) {
				sameCount ++;
			}else{
				return false;
			}
			j++;
		}
		return  itemCount == sameCount;
	}
}
