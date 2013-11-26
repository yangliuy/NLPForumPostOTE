package edu.pku.yangliu.nlp.pdt.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.PerformanceMonitor;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

/**a Shallow Parser based on opennlp
 * @author YANG Liu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yang.liu@pku.edu.cn
 */

public class ShallowParser {
	
	private static ShallowParser instance = null ;
	private static POSModel model;
	private static ChunkerModel cModel ;
	
	//Singleton pattern
	public static ShallowParser getInstance() throws InvalidFormatException, IOException{
		if(ShallowParser.instance == null){
			POSModel model = new POSModelLoader().load(new File("model/en-pos-maxent.bin"));
			InputStream is = new FileInputStream("model/en-chunker.bin");
			ChunkerModel cModel = new ChunkerModel(is);
			ShallowParser.instance = new ShallowParser(model, cModel);
		}
		return ShallowParser.instance;
	}
	
	public ShallowParser(POSModel model, ChunkerModel cModel){
		ShallowParser.model = model;
		ShallowParser.cModel = cModel;
		
	}
	
	 /** A shallow Parser, chunk a sentence and return a map for the phrase
	  *  labels of words <wordsIndex, phraseLabel>
	 *   Notice: There should be " " BEFORE and after ",", " ","(",")" etc.
	 *   Notice: Put the POS tag of words in the phLable member
	 * @param input The input sentence
	 * @param model The POSModel of the chunk
	 * @param cModel The ChunkerModel of the chunk
	 * @return  HashMap<Integer,String> Key index of word ; Value phrase label and pos tag
	 */
	 public Map<Integer,String> chunk(String input) throws IOException { 	
			PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
			POSTaggerME tagger = new POSTaggerME(model);
			ObjectStream<String> lineStream = new PlainTextByLineStream(
					new StringReader(input));
			perfMon.start();
			String line;
			POSSample posTags = null;
			String whitespaceTokenizerLine[] = null; 
			String[] tags = null;
			while ((line = lineStream.read()) != null) {
				whitespaceTokenizerLine = WhitespaceTokenizer.INSTANCE
						.tokenize(line);
				tags = tagger.tag(whitespaceTokenizerLine);	 
			    posTags = new POSSample(whitespaceTokenizerLine, tags);
				//System.out.println(posTags.toString());
				perfMon.incrementCounter();
			}
			perfMon.stopAndPrintFinalResult();
	 
			// chunker
			ChunkerME chunkerME = new ChunkerME(cModel);
			String result[] = chunkerME.chunk(whitespaceTokenizerLine, tags);
			/*for(int i = 0; i < result.length; i++){
				System.out.println(i + ":" + result[i]);
			}*/
			Map<Integer,String> phLablePostagMap = new TreeMap<Integer, String>();
			Integer wordCount = 0;//All index is from 0
			Integer phLableCount = -1;
			for (String phLable : result){
				if(phLable.equals("O")) {
					phLable += "-PUNP"; //The phLable of punctuation
					phLableCount++;
				}
				if(phLable.split("-")[0].equals("B")) phLableCount++;
				if(phLableCount.equals(new Integer(-1)))  phLableCount++;
				phLable = phLable.split("-")[1] + phLableCount + "-" + posTags.getTags()[wordCount]; //Notice: Put the POS tag of words in the phLable member
				phLablePostagMap.put(wordCount, phLable);
				wordCount++;
			}
			//System.out.println(phLablePostagMap);
			return phLablePostagMap;
		}
	 
	 /** Just for testing
		 * @param tdl Typed Dependency List
		 * @return WDTreeNode root of WDTree
		 */
	 public static void main(String[] args) throws IOException {
		 //Notice: There should be " " BEFORE and after ",", " ","(",")" etc.
		 //String input = "We really enjoyed using the Canon PowerShot SD500 .";
		 //String input = "Bell , based in Los Angeles , makes and distributes electronic , computer and building products .";
		// ShallowParser swParser = ShallowParser.getInstance();
		 //swParser.chunk(input);

		 //Notice: There should be " " BEFORE and after ",", " ","(",")" etc.
		 //String input = "We really enjoyed using the Canon PowerShot SD500 .";
		// String corpusPath = "data/FudanSentimentCorpus/";
		 //String input = "Bell , based in Los Angeles , makes and distributes electronic , computer and building products .";
		 //ShallowParser swParser = ShallowParser.getInstance();
		 //swParser.chunk(input);
		 //StanfordParser sdParser = StanfordParser.getInstance();
		// File[] trainFiles = new File(corpusPath).listFiles();
		// for(File trainFile : trainFiles){
			 //sdParser.DPFromFile(trainFile.getAbsolutePath());
		// }
	 
	 }
	     
}
