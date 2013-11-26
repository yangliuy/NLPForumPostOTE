package edu.pku.yangliu.nlp.pdt.lemma;
import java.util.HashMap;

public class POSMap {

	private static HashMap<String, String> TagMap;
	
	public POSMap() {
		TagMap = new HashMap<String, String>();
		String initialMap = init();
		String []hashdata = initialMap.split(" / ");
		//System.out.println("Tag Mapping: ");
		for(int i = 0; i < hashdata.length; i+=2) {
			TagMap.put(hashdata[i], hashdata[i+1]);
			//System.out.println(hashdata[i] + "\t" + TagMap.get(hashdata[i]));
		}
	}

	public static HashMap<String, String> getTagMap() {
		return TagMap;
	}

	public void setTagMap(HashMap<String, String> tagMap) {
		TagMap = tagMap;
	}

	private String init() {
		String initialMap = "CD / ADJ / " +
		"JJ / ADJ / " +
		"JJR / ADJ / " +
		"JJS / ADJ / " +
		"VB / V / " +
		"VBD / V / " +
		"VBG / V / " +
		"VBN / V / " +
		"VBP / V / " +
		"VBZ / V / " +
		"MD / V / " +
		"NN / N / " +
		"NNS / N / " +
		"NNP / N / " +
		"NNPS / N / " +
		"RB / ADV / " +
		"RBR / ADV / " +
		"RBS / ADV / " +
		"RP / ADV / " +
		"WRB / ADV / " +
		"DT / DET / " +
		"PDT / DET / " +
		"WDT / DET / " +
		"POS / DET / " +
		"PRP / PRP / " +
		"WP / PRP / " +
		"PRP$ / PRP$ / " +
		"WP$ / PRP$ / " +
		"TO / PREP / " +
		"IN / PREP / " +
		"CC / CONJ / " +
		"EX / OTHER / " +
		"FW / OTHER / " +
		"SYM / OTHER / " +
		"UH / OTHER / " +
		"LS / OTHER / ";
		return initialMap;
	}
}
