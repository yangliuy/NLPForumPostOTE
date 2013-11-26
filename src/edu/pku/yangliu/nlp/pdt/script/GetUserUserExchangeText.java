package edu.pku.yangliu.nlp.pdt.script;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import edu.pku.yangliu.nlp.pdt.common.FileUtil;
import edu.pku.yangliu.nlp.pdt.config.DataPathConfig;

/**
 * @author liuyang
 *
 */
public class GetUserUserExchangeText {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String corpusFilePath = DataPathConfig.corpusPath;
		File [] corpusFolders = new File(corpusFilePath).listFiles();
		for(File corpusFolder : corpusFolders){
			File [] corpusFiles = corpusFolder.listFiles();
			String useruserIDFile = corpusFolder.getCanonicalPath() + "/user_relation_" + corpusFolder.getName()+".txt";
			String allInforTokenFile = "";
			for(File corpusFile : corpusFiles){
				if(corpusFile.getName().contains("repPRP_Format_Token")){
					allInforTokenFile = corpusFile.getAbsolutePath();
				}
			}
			if(new File(useruserIDFile).exists() && allInforTokenFile.length() != 0){
				genUserUserExchangeTextFile(useruserIDFile, allInforTokenFile);
			}
		}
	}

	private static void genUserUserExchangeTextFile(String useruserIDFile,
			String allInforTokenFile) {
		// TODO Auto-generated method stub
		System.out.println("now : useruserIDFile =" + useruserIDFile);
		ArrayList<String> useruserIDLines = new ArrayList<String>();
		ArrayList<String> allInforTokenLines = new ArrayList<String>();
		ArrayList<String> useruserExchangeTextLines = new ArrayList<String>();
		FileUtil.readLines(useruserIDFile, useruserIDLines);
		FileUtil.readLines(allInforTokenFile, allInforTokenLines);
		for(String useruserIDLine : useruserIDLines){
			String[] useruserIDLineTokens = useruserIDLine.split("\t");
			if(useruserIDLineTokens.length != 2){
				System.err.println("useruserID format wrong! now useruserIDFile =" + useruserIDFile);
			} else {
				String userA = transferTokenToUserID(useruserIDLineTokens[0]);
				String userB = transferTokenToUserID(useruserIDLineTokens[1]);
				for(String allInforTokenLine : allInforTokenLines){
					String[] allInforTokens = allInforTokenLine.split("\t");
					if(allInforTokens.length != 7){
						System.err.println("all infortokens fomat wrong");
					}
					if(allInforTokens[2].toLowerCase().trim().equals(userA) && allInforTokens[3].toLowerCase().trim().equals(userB)){
						useruserExchangeTextLines.add(allInforTokens[0] + "\t" + allInforTokens[5] + "\t" + useruserIDLineTokens[0] + "\t" + useruserIDLineTokens[1] + "\t" + useruserIDLineTokens[0] + "\t" + useruserIDLineTokens[1] + "\t" + allInforTokens[6]);
						
					}
					if(allInforTokens[2].toLowerCase().trim().equals(userB) && allInforTokens[3].toLowerCase().trim().equals(userA)){
						useruserExchangeTextLines.add(allInforTokens[0] + "\t" + allInforTokens[5] + "\t" + useruserIDLineTokens[0] + "\t" + useruserIDLineTokens[1] + "\t" + useruserIDLineTokens[1] + "\t" + useruserIDLineTokens[0] + "\t" + allInforTokens[6]);
					}
				}
			}
		}
		String useruserExchangeTextFile = useruserIDFile + "_exchangeText";
		FileUtil.writeLines(useruserExchangeTextFile, useruserExchangeTextLines);
	}

	private static String transferTokenToUserID(String token) {
		// TODO Auto-generated method stub
		String[] userTokens = token.split("_");
		String userID = "";
		for(int i = 0; i < userTokens.length - 1; i++){
			userID += userTokens[i] + " ";
		}
		return userID.trim();
	}
}
