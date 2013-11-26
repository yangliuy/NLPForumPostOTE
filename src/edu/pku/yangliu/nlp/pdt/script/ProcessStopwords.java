package edu.pku.yangliu.nlp.pdt.script;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import edu.pku.yangliu.nlp.pdt.config.DataPathConfig;
import edu.pku.yangliu.nlp.pdt.main.PreprocessText;

/**Class for process of stop words list
 * @author YANG Liu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yang.liu@pku.edu.cn
 */

public class ProcessStopwords {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		PreprocessText ppText = new PreprocessText();
		String opinionWordsFilePath = DataPathConfig.opinionWordsFilePath;
		String stopWordsFilePath = DataPathConfig.stopWordsFilePath;
		HashSet<String> opinionWordsSet = new HashSet<String>();
		HashSet<String> stopWordsSet = new HashSet<String>();
		opinionWordsSet = ppText.getWordsSetFromFiles(opinionWordsFilePath, false);
		stopWordsSet = ppText.getWordsSetFromFiles(stopWordsFilePath, false);
		String newStopWordsName = stopWordsFilePath + "newStopWordList.txt";
		FileWriter newStopWordsWriter = new FileWriter(newStopWordsName);
		System.out.println("ori StopWordsSet size" + stopWordsSet.size());
		int counter = 0;
		for(String stopWord : stopWordsSet){
			if(!opinionWordsSet.contains(stopWord)) {
				newStopWordsWriter.append(stopWord + "\n");
				counter++;
			}
			newStopWordsWriter.flush();
		}
		System.out.println("new StopWordsSet size" + counter);	
	}
}
