package edu.pku.yangliu.nlp.pdt.script;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import edu.pku.yangliu.nlp.pdt.common.FileUtil;
import edu.pku.yangliu.nlp.pdt.config.DataPathConfig;
import edu.pku.yangliu.nlp.pdt.main.PreprocessText;

/**Class for process of opinion words list
 * @author YANG Liu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yang.liu@pku.edu.cn
 */

public class ProcessOpinionWords {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String opinionWordsPath = DataPathConfig.opinionWordsFilePath;
		
		/*String preProOpinionWordsPath = striptDataPath + "SAlexicon/";
		String oriWholeOpinionFileName = preProOpinionWordsPath + "SA_lexicon_m3.txt";
		String oriPosFileName = preProOpinionWordsPath + "poslist.txt";
		String oriNegFileName = preProOpinionWordsPath + "neglist.txt";
		String oriRMFileName = preProOpinionWordsPath + "rmlist.txt";
		String newPosFileName = preProOpinionWordsPath + "newPositiveWord.txt";
		String newNegaFileName = preProOpinionWordsPath + "newNegativeWord.txt";
		ArrayList<String> oriWholeLines = new ArrayList<String>();
		ArrayList<String> oriPosLines = new ArrayList<String>();
		ArrayList<String> oriNegLines = new ArrayList<String>();
		ArrayList<String> oriRMLines = new ArrayList<String>();
		FileUtil.readLines(oriWholeOpinionFileName, oriWholeLines);
		FileUtil.readLines(oriPosFileName, oriPosLines);
		FileUtil.readLines(oriNegFileName, oriNegLines);
		FileUtil.readLines(oriRMFileName, oriRMLines);
		ArrayList<String> newPosLines = new ArrayList<String>();
		ArrayList<String> newNegLines = new ArrayList<String>();
		for(String oriWholeLine : oriWholeLines){
			String [] oriWholeLineTokens = oriWholeLine.split("\t");
			if(oriRMLines.contains(oriWholeLineTokens[0])) continue;
			if(Double.valueOf(oriWholeLineTokens[1]) < 0.5) {
				newNegLines.add(oriWholeLineTokens[0]);
			} else {
				newPosLines.add(oriWholeLineTokens[0]);
			}
		}
		
		for(String oriPosLine : oriPosLines){
			newPosLines.add(oriPosLine);
		}
		for(String oriNegLine : oriNegLines){
			newNegLines.add(oriNegLine);
		}
		
		FileUtil.writeLines(newPosFileName, newPosLines);
		FileUtil.writeLines(newNegaFileName, newNegLines);*/
		
		
	}	
}

