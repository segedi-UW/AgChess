package com.chess.agchess;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

public class Remarker {

	private final Random random;
	private final Hashtable<Type, ArrayList<String>> remarks;

	public enum Type {
		ERROR, GOOD, BAD
	}
	
	public Remarker() {
		random = new Random();
		String ERROR_FILE = "errorRemarks.txt";
		ArrayList<String> errorRemarks = readArrayList(ERROR_FILE);
		String GOOD_FILE = "goodRemarks.txt";
		ArrayList<String> goodRemarks = readArrayList(GOOD_FILE);
		String BAD_FILE = "badRemarks.txt";
		ArrayList<String> badRemarks = readArrayList(BAD_FILE);
		remarks = new Hashtable<>();
		remarks.put(Type.BAD, badRemarks);
		remarks.put(Type.ERROR, errorRemarks);
		remarks.put(Type.GOOD, goodRemarks);
	}

	public String getRemark(Type type) {
		ArrayList<String> list = remarks.get(type);
		return list.get(random.nextInt(list.size()));
	}
	
	private  ArrayList<String> readArrayList(String filename) {
		final int size = 30;
		ArrayList<String> list = new ArrayList<>(size);
		URL url = getClass().getResource(filename);
		if (url != null) {
			try (InputStreamReader streamReader = new InputStreamReader(url.openStream())) {
				try (BufferedReader reader = new BufferedReader(streamReader)) {
					reader.lines().forEach(list::add);
				}
			} catch (IOException e) {
				System.err.println("Error Reading remark file: " + e.getMessage());
			}
		} else System.err.println("Could not find Remarker file " + filename);
		return list;
	}
}
