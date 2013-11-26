package edu.pku.yangliu.nlp.pdt.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import edu.pku.yangliu.nlp.pdt.config.DataPathConfig;
import edu.pku.yangliu.nlp.pdt.main.PreprocessText;

public class GenTOPathPatternMap {
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String vectorFilePath = DataPathConfig.featureVectorPath;
		String scriptDataPath = DataPathConfig.scriptDataPath;	
		String pathRulesSupportFileName = scriptDataPath + "_pathRulesSupport_Camera";
		String pathRulesAllInforFileName = scriptDataPath + "_pathRulesAllInfor__Camera";
		FileWriter pathRulesSupportFileWriter = new FileWriter(pathRulesSupportFileName);
		FileWriter pathRulesAllInforFileWriter = new FileWriter(pathRulesAllInforFileName);
		Map<String, String> pathRulsSupportMap = new TreeMap<String, String>();
		File[] vectorFiles = new File(vectorFilePath).listFiles();
		for(File vectorFile : vectorFiles){	
			String vectorFileFileName = vectorFilePath + vectorFile.getName();
			if(vectorFileFileName.contains("final")){
				BufferedReader finalVectorFileBR = new BufferedReader(new FileReader(vectorFileFileName));
				String line;			
				while((line = finalVectorFileBR.readLine()) != null){
					String[] tokens = line.split("\t");
					if(tokens[0].equals("1")){
						pathRulesAllInforFileWriter.append(line + "\n");
						pathRulesAllInforFileWriter.flush();
						String path = tokens[1].split(" ")[0];
						if(pathRulsSupportMap.containsKey(path)) {
							String oldCounter = pathRulsSupportMap.get(path);
							String newCounter = String.valueOf(Integer.valueOf(oldCounter) + 1);
							pathRulsSupportMap.put(path, newCounter);
						} else {
							pathRulsSupportMap.put(path, "1");
						}
					}
				}
			}
		}
				
		for(String key : pathRulsSupportMap.keySet()){
			System.out.println(key + " " + pathRulsSupportMap.get(key));
			pathRulesSupportFileWriter.append(key + " " + pathRulsSupportMap.get(key) + "\n");
		}
		pathRulesSupportFileWriter.flush();			
	}
}
