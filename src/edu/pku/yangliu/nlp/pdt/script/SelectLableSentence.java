package edu.pku.yangliu.nlp.pdt.script;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import edu.pku.yangliu.nlp.pdt.common.FileUtil;
import edu.pku.yangliu.nlp.pdt.config.DataPathConfig;

/**Script for selecting label sentences
 * @author YANG Liu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yang.liu@pku.edu.cn
 */
public class SelectLableSentence {
	
	public static void main(String[] args) throws IOException {
		String opinionWordsPath = DataPathConfig.opinionWordsFilePath;
		String featurePath = DataPathConfig.featureVectorPath;
		String formatRepPRPFileName = featurePath + "obamaAInf_repPRP_Format";
		String labelSentsFileName = DataPathConfig.scriptDataPath + "voteObama2012FMTlabelData";
		FileWriter labelSentsFileWriter = new FileWriter(labelSentsFileName);
		ArrayList<String> fmtLines = new ArrayList<String>();//sentID /t postID /t sent
		ArrayList<String> opinionWords = new ArrayList<String>();
	    FileUtil.readLines(formatRepPRPFileName, fmtLines);	
	    for(File opinionFile : new File(opinionWordsPath).listFiles()){
	    	FileUtil.readLines(opinionFile.getAbsolutePath(), opinionWords);
	    }
	    Integer sentIDCounter;
		for(String fmtline : fmtLines){
			if(!fmtline.isEmpty()){
			String [] tokens = fmtline.split("\t");
			sentIDCounter = Integer.valueOf(tokens[0]);	
			System.out.println("SentID Counter :" + sentIDCounter);
			if(isContainObamaOpinion(tokens[2], opinionWords)){
				String[] fmtlineTokens = fmtline.split("\t");
				String[] sentWords = fmtlineTokens[2].split(" ");
				String sentPlusID = "";
				for(int i = 0; i < sentWords.length; i++){
					sentPlusID += sentWords[i] + "_" + String.valueOf(i) + " ";
				}
				labelSentsFileWriter.append(fmtlineTokens[0] + "\t" + fmtlineTokens[1] + "\t" + sentPlusID + "\n");
				labelSentsFileWriter.flush();
				}
			}
		}
	}

	private static boolean isContainObamaOpinion(String sent,
			ArrayList<String> opinionWords) {
		// TODO Auto-generated method stub
		String [] sentWords = sent.split(" ");
		boolean opinionFlag = false;
		boolean obamaFlag = false;
		for(String word : sentWords){
			if(opinionWords.contains(word)){
				opinionFlag = true;
			}
			if(word.equals("Obama") || word.equals("OBAMA")){
				obamaFlag = true;
			}
		}
		if(opinionFlag && obamaFlag) return true;
		else return false;
	}
}
