package edu.pku.yangliu.nlp.pdt.parser;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.pku.yangliu.nlp.pdt.common.FileUtil;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;

public class StanfordNER {
	
	 public static String ner(String fileName) throws IOException {
		 String nerResFileName = fileName + "_NER";
		 ArrayList<String> fileLines = new ArrayList<String>();
		 FileUtil.readLines(fileName, fileLines);
		 FileWriter nerResFileWriter = new FileWriter(nerResFileName);
	     String serializedClassifier = "lib/english.all.3class.distsim.crf.ser.gz";
	     AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);
	     for(String line : fileLines){
	    	 String[] lineTokens = line.split("\t");
	    	 List<List<CoreLabel>> sentsOut = classifier.classify(lineTokens[2]);
	         for (List<CoreLabel> sentence : sentsOut) {
	        	 nerResFileWriter.append(lineTokens[0] + "\t" + lineTokens[1] + "\t");
	        	 for(int i = 0; i < sentence.size(); i++){
	        		 CoreLabel word = sentence.get(i);
	        		 String wordNERTag = word.get(AnswerAnnotation.class);
		        	 if((wordNERTag.equals("PERSON") || wordNERTag.equals("ORGANIZATION") || wordNERTag.equals("LOCATION")) && !word.word().contains("ASPECT_")){
		        		 nerResFileWriter.append("ASPECT_" + word.word().toUpperCase() + " ");
		        		 //System.out.print("ASPECT_" + word.word().toUpperCase() + " ");
		        	 } else {
		        		 nerResFileWriter.append(word.word() + " ");
		        		 //System.out.print(word.word() + " ");
		        	 }
	        	 }
		         nerResFileWriter.append("\n");
		         nerResFileWriter.flush();
		         //System.out.println();	          
		       } 
	     }
        System.out.println("NER Done!");
		return nerResFileName;
	  }
}
