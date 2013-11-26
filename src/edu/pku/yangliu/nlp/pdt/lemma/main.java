package edu.pku.yangliu.nlp.pdt.lemma;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EmptyStackException;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.morph.WordnetStemmer;


public class main {
	public static void main(String args[]) throws IOException {
		String wordnetPath = "lib/dict/";
		new POSMap();
		new Lemma(wordnetPath);
		System.out.println(Lemma.getLemma("books", "NNS"));
		System.out.println("done");
	}

}