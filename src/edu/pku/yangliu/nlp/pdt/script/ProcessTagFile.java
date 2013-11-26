package edu.pku.yangliu.nlp.pdt.script;

import java.util.ArrayList;

import edu.pku.yangliu.nlp.pdt.common.FileUtil;
import edu.pku.yangliu.nlp.pdt.config.DataPathConfig;

public class ProcessTagFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String scriptDataPath = DataPathConfig.scriptDataPath;
		ArrayList<String> tagLines = new ArrayList<String>();
		ArrayList<String> posiWords = new ArrayList<String>();
		ArrayList<String> negWords = new ArrayList<String>();
		String tagFileName = scriptDataPath + "UserAspectInteractionTagging_allLabled.txt";
		FileUtil.readLines(tagFileName, tagLines);
		for(String tagLine : tagLines){
			String [] tagLineTokens = tagLine.split("\t");
			System.out.println("tagLine: " + tagLine);
			if(tagLineTokens[4].equals("positive")){
				if(tagLineTokens[3].split(" ").length > 1 && tagLineTokens[3].split(" ")[1].equals("no")){
					System.out.println("neg1 : " + tagLineTokens[3].split(" ")[0]);
					negWords.add(tagLineTokens[3].split(" ")[0].split("_")[0]);
				} else {
					posiWords.add(tagLineTokens[3].split(" ")[0].split("_")[0]);
					System.out.println("pos1 : " + tagLineTokens[3].split(" ")[0]);
				}
			} else {
				if(tagLineTokens[3].split(" ").length > 1 && tagLineTokens[3].split(" ")[1].equals("no")){
					posiWords.add(tagLineTokens[3].split(" ")[0].split("_")[0]);
					System.out.println("pos2 : " + tagLineTokens[3].split(" ")[0]);
				} else {
					negWords.add(tagLineTokens[3].split(" ")[0].split("_")[0]);
					System.out.println("neg2 : " + tagLineTokens[3].split(" ")[0]);
				}
			}
		}
		System.out.println("posiWords size " + posiWords.size() + "negWords " + negWords.size());
		String addPosFile = scriptDataPath + "addPosi";
		String addNegFile = scriptDataPath + "addNeg";
		FileUtil.writeLines(addPosFile, posiWords);
		FileUtil.writeLines(addNegFile, negWords);
	}
}
