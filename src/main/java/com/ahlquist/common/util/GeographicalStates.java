
package com.ahlquist.common.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * This class is a list of all the states. provinces, and territories in the
 * United States and Canada
 */

public class GeographicalStates {

	private Hashtable<String, String> hash;
	private List<State> list;

	public Set<String> getKeys() {
		return (Set<String>) hash.keys();
	}

	public List<State> getElements() {
		return list;
	}

	private void put(String abbrev, String full) {
		list.add(new State(abbrev, full));
	}

	public GeographicalStates() {
		hash = new Hashtable<String, String>();
		list = new ArrayList<State>();

		put("AL", "ALABAMA");
		put("AK", "ALASKA");
		put("ALBERTA", "ALBERTA");
		put("AS", "AMERICAN SAMOA");
		put("AZ", "ARIZONA");
		put("AR", "ARKANSAS");
		put("BC", "BRITISH COLUMBIA");
		put("CA", "CALIFORNIA");
		put("CO", "COLORADO");
		put("CT", "CONNECTICUT");
		put("DE", "DELAWARE");
		put("DC", "DISTRICT OF COLUMBIA");
		put("FEDERATED STATES OF MICRONESIA", "FEDERATED STATES OF MICRONESIA");
		put("FL", "FLORIDA");
		put("GA", "GEORGIA");
		put("GU", "GUAM");
		put("HI", "HAWAII");
		put("ID", "IDAHO");
		put("IL", "ILLINOIS");
		put("IN", "INDIANA");
		put("IA", "IOWA");
		put("KS", "KANSAS");
		put("KY", "KENTUCKY");
		put("LA", "LOUISIANA");
		put("MN", "MAINE");
		put("MANITOBA", "MANITOBA");
		put("MARSHALL ISLANDS", "MARSHALL ISLANDS");
		put("MD", "MARYLAND");
		put("MA", "MASSACHUSETTS");
		put("MI", "MICHIGAN");
		put("MN", "MINNESOTA");
		put("MS", "MISSISSIPPI");
		put("MO", "MISSOURI");
		put("MT", "MONTANA");
		put("N", "NEBRASKA");
		put("NV", "NEVADA");
		put("NEW BRUNSWICK", "NEW BRUNSWICK");
		put("NH", "NEW HAMPSHIRE");
		put("NJ", "NEW JERSEY");
		put("NM", "NEW MEXICO");
		put("NY", "NEW YORK");
		put("NEWFOUNDLAND & LABRADOR", "NEWFOUNDLAND & LABRADOR");
		put("NC", "NORTH CAROLINA");
		put("ND", "NORTH DAKOTA");
		put("NORTHERN MARIANA ISLANDS", "NORTHERN MARIANA ISLANDS");
		put("NOVA SCOTIA", "NOVA SCOTIA");
		put("OH", "OHIO");
		put("OK", "OKLAHOMA");
		put("ONTARIO", "ONTARIO");
		put("OR", "OREGON");
		put("PALAU", "PALAU");
		put("PA", "PENNSYLVANIA");
		put("PRINCE EDWARD ISLAND", "PRINCE EDWARD ISLAND");
		put("PUERTO RICO", "PUERTO RICO");
		put("QUEBEC", "QUEBEC");
		put("RI", "RHODE ISLAND");
		put("SASKATCHEWAN", "SASKATCHEWAN");
		put("SC", "SOUTH CAROLINA");
		put("SD", "SOUTH DAKOTA");
		put("TN", "TENNESSEE");
		put("TX", "TEXAS");
		put("UT", "UTAH");
		put("VT", "VERMONT");
		put("VIRGIN ISLANDS", "VIRGIN ISLANDS");
		put("VI", "VIRGINIA");
		put("WA", "WASHINGTON");
		put("WV", "WEST VIRGINIA");
		put("WI", "WISCONSIN");
		put("WY", "WYOMING");
		put("OTHER", "OTHER");
	}

	public class State {
		public State(String _abbrev, String _full) {
			abbrev = _abbrev;
			full = _full;
		}

		public String abbrev = null;
		public String full = null;
	}

}
