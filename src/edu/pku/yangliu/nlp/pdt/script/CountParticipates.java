package edu.pku.yangliu.nlp.pdt.script;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.pku.yangliu.nlp.pdt.common.FileUtil;
import edu.pku.yangliu.nlp.pdt.config.DataPathConfig;

public class CountParticipates {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String corpusFilePath = DataPathConfig.corpusPath;
		File [] corpusFolders = new File(corpusFilePath).listFiles();
		for(File corpusFolder : corpusFolders){
			Set<String> participaterSet = new HashSet<String>();
			File [] corpusFiles = corpusFolder.listFiles();
			String allInforTokenFile = "";
			for(File corpusFile : corpusFiles){
				if(corpusFile.getName().contains("repPRP_Format_Token")){
					allInforTokenFile = corpusFile.getAbsolutePath();
					System.out.println("now allInfor file is "+ allInforTokenFile);
					ArrayList<String> allInforLines = new ArrayList<String>();
					FileUtil.readLines(allInforTokenFile, allInforLines);
					for(String allInforTokenLine : allInforLines){
						String [] allInforTokenLineTokens = allInforTokenLine.split("\t");
						participaterSet.add(allInforTokenLineTokens[2].trim());
						//participaterSet.add(allInforTokenLineTokens[3].trim());
					}
				}
			}
			System.out.println("now participate set is " + participaterSet);
			System.out.println("The total participater in " + corpusFolder.getName() + " is" + participaterSet.size());
			participaterSet.clear();
		}
	}
}
