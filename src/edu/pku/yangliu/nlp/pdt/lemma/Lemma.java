package edu.pku.yangliu.nlp.pdt.lemma;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;

/**
 * A Document object represents a single document in ACE.
 */

public class Lemma {
	
	public static IDictionary dict;
	
	public static WordnetStemmer ws;
	
	public Lemma(String wordnetPath) {
		URL url;
		try {
			url = new URL("file", null, wordnetPath);
			dict = new Dictionary(url);
			dict.open();
			ws = new WordnetStemmer(dict);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}	
	}

	public static String getLemma(String token, String pos) throws MalformedURLException {
		String lemma = token;
		if(getWNPOS(pos) != null) {
			List<String> stemList = ws.findStems(token, getWNPOS(pos));
			if(!stemList.isEmpty()) {
				lemma = stemList.get(0);
			}
		}
		return lemma;
	}

	public static POS getWNPOS(String tag) {

		String gPOS = POSMap.getTagMap().get(tag);		
	  	if( gPOS != null) {
	  		if (gPOS.equals("N")) {
				return POS.NOUN;
			} else if (gPOS.equals("ADJ")) {
				return POS.ADJECTIVE;
			} else if (gPOS.equals("ADV")) {
				return POS.ADVERB;
			} else if (gPOS.equals("V")) {
				return POS.VERB;
			} 
		}
	  	return null;
	}
}