package edu.pku.yangliu.nlp.pdt.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import edu.pku.yangliu.nlp.pdt.common.FileUtil;
import edu.pku.yangliu.nlp.pdt.stem.Porter;
import edu.pku.yangliu.nlp.pdt.stem.PorterFiles;
import edu.pku.yangliu.nlp.pdt.tree.WDTree;
import edu.pku.yangliu.nlp.pdt.tree.WDTreeNode;

/**Class for preprocess of text
 * @author YANG Liu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yang.liu@pku.edu.cn
 */

public class PreprocessText {
	
	
	/**Get the set of word from more than one files
	 * @param FileDir File directory of words files
	 * @param isStem whether stem words
	 * @return HashSet<String>
	 * @throws IOException   
	 */
	public HashSet<String> getWordsSetFromFiles(String FileDir, boolean isStem) throws IOException{
		File[] opinionFiles = new File(FileDir).listFiles();
		HashSet<String> wordsSet = new HashSet<String>();
		String word;
		for(File opinionFile : opinionFiles){
			if(opinionFile.getName().contains("stemed")) continue;
			System.out.println("get words set from " + opinionFile.getName());
			FileReader opinionFileReader;	
			if(isStem){
				String[] stemFiles = {opinionFile.getAbsolutePath()};
				PorterFiles.porterMain(stemFiles);
				opinionFileReader = new FileReader(opinionFile.getAbsolutePath() + "stemed");
			} else {
				opinionFileReader = new FileReader(opinionFile);
			}
			BufferedReader opinionBR = new BufferedReader(opinionFileReader);
			while((word = opinionBR.readLine()) != null){
				wordsSet.add(word);
			}
		}
		System.out.println("The word list in "+ FileDir +" contains " + wordsSet.size() + " words");
		return wordsSet;
	}
	
	/**Convert an array to a string splited by blank
	 * @param opinionFileDir File directory of opinion words files
	 * @return HashSet<String>
	 * @throws IOException   
	 */
	private String convertArraytoString(Vector<String> vector){
		String string = "";
		for(String s : vector){
			s = s.split("-")[0];
			if(!s.equals("NULL")){
				string += s + " ";
			}
		}
		return string;
	}
	
	/**Print the features of target and opinion phrase pair to the file
	 * @param root The root of PDT
	 * @param vectorFileWriter
	 * @param counterSent 
	 * @param line 
	 * @return void
	 * @throws IOException  
	 */
	private void printFeaturesOfTOPhrasePair(WDTreeNode root,
			FileWriter vectorFileWriter, int counterSent, String line) throws IOException {
		// TODO Auto-generated method stub
		Vector<WDTreeNode> targetNodes = new Vector<WDTreeNode>();
		Vector<WDTreeNode> opinionNodes = new Vector<WDTreeNode>();
		WDTreeNode curNode = root;
		Queue<WDTreeNode> nodeQueue = new LinkedList<WDTreeNode>();
		nodeQueue.add(curNode);
		while(nodeQueue.size() > 0){
			curNode = nodeQueue.poll();
			if(curNode.isOpinionLable() && !curNode.isTargetLable()) opinionNodes.add(curNode);
			else if(curNode.isTargetLable() && !curNode.isOpinionLable()) targetNodes.add(curNode);
			else if(curNode.isTargetLable() && curNode.isOpinionLable()) {
				if(targetNodes.size() == 0 || targetNodes.size() < opinionNodes.size()) {
					targetNodes.add(curNode);
				}
				else if(opinionNodes.size() == 0 || opinionNodes.size() < targetNodes.size()){
					opinionNodes.add(curNode);
				} else {
					opinionNodes.add(curNode);
				}
			}
			if (curNode.getChildren() == null) continue;
			for(WDTreeNode child : curNode.getChildren()){
				nodeQueue.add(child);			
				}
		}
		if(targetNodes.size() != 0 && opinionNodes.size() != 0){
			System.out.println("TargetNodes size " + targetNodes.size());
			System.out.println("OpinionNodes size " + opinionNodes.size());
		}
		
		for(WDTreeNode targetNode : targetNodes){
			for(WDTreeNode opinionNode : opinionNodes){
				//System.out.println("Finding the LCA Path between opinion : " + opinionNode.getWords() + " and target : " + targetNode.getWords());
				 // All direction are from govern node to dependency node
				 // The LCA path is from opinion node to target node
				String lcaPathString = WDTree.FindLCAPath(root, targetNode, opinionNode);
				if(Integer.valueOf(lcaPathString.split("_")[0]) > 7) continue;//The tree distance between the targetNode and the opinionNode should be less than 7		
				vectorFileWriter.append(line + "\t" + counterSent 
											 + "\t" + convertArraytoString(targetNode.getWords())
											 + "\t" + convertArraytoString(opinionNode.getWords())
											 + "\tLCApath:" + lcaPathString
											 +" Tph:" + targetNode.getPhLabel().replaceAll("[0-9]", "") + " Tpos:" + targetNode.getPosTagForHeadWord()									 
											 +" Oph:" + opinionNode.getPhLabel().replaceAll("[0-9]", "") + " Opos:" + opinionNode.getPosTagForHeadWord());
				//System.out.println("the LCA Path is : " + lcaPathString);
				vectorFileWriter.append("\n");
				vectorFileWriter.flush();
			}
		}
	}
	
	/**Genetate svm vector file
	 * @param finalVectorFileName
	 * @param svmVectorFileName
	 * @return void 
	 * @throws IOException 
	 */
	private void genSVMVectorFile(String finalVectorFileName,
			String svmVectorFileName) throws IOException {
		// TODO Auto-generated method stub
		FileReader finalVectorFileReader = new FileReader(finalVectorFileName);
		BufferedReader finalVectorBR = new BufferedReader(finalVectorFileReader);
		String line;
		Vector<String[]> featureVector = new Vector<String[]>();
		String[] tokens;
		TreeMap<Integer, String> svmFeatureMap = new TreeMap<Integer, String>();
		HashSet<String> svmFeatureSet = new HashSet<String>();
		while((line = finalVectorBR.readLine()) != null) {
			tokens = line.split(" ");
			featureVector.add(tokens);
			for(int i = 1; i < tokens.length; i++){
				svmFeatureSet.add(tokens[i]);
			}
		}
		finalVectorBR.close();
		finalVectorFileReader.close();
		System.out.println("Feature Set Size:" + svmFeatureSet.size());
		Integer featureIndex = 1;
		for(String feature : svmFeatureSet){
			System.out.println("featureIndex and feature" + featureIndex + " " + feature);
			svmFeatureMap.put(featureIndex, feature);
			featureIndex++;
		}
		
		FileWriter svmVectorFileWriter = new FileWriter(svmVectorFileName);
		for(String[] features : featureVector){
			svmVectorFileWriter.append(features[0] + " ");
			Set<Map.Entry<Integer, String>> svmFeatureMapSet = svmFeatureMap.entrySet();
			for(Iterator<Map.Entry<Integer, String>> it = svmFeatureMapSet.iterator(); it.hasNext();){
				Map.Entry<Integer, String> me = it.next();
				if(this.arryContains(features, me.getValue())){
					svmVectorFileWriter.append(me.getKey() + ":1 ");
				}
			}
			svmVectorFileWriter.append("\n");
		}
		svmVectorFileWriter.close();
	}

	private boolean arryContains(String[] features, String value) {
		// TODO Auto-generated method stub
		for(String feature : features){
			if(feature.equals(value)){
				return true;
			}
		}
		return false;
	}
	
	/**preProcess text and generate vectors
	 * @param corpusPath
	 * @param featureVectorPath
	 * @param aspectWordsFilePath
	 * @param opinionWordsFilePath
	 * @param stopWordsFilePath 
	 * @param candidateAspectPath 
	 * @param candidateOpinionPath 
	 * @return void 
	 */
	public void preProcess(File trainFile, String featureVectorPath,String opinionWordsFilePath) throws IOException {
		// TODO Auto-generated method stub
		HashSet<String> opinionWordsSet = new HashSet<String>();
		opinionWordsSet = getWordsSetFromFiles(opinionWordsFilePath, false);
		Map<Integer, String> sentIDMap = new TreeMap<Integer, String>();//Key: sentID	Value:Sent
		Map<Integer, String> sentPostIDMap = new TreeMap<Integer, String>();//Key:sentID	Value:PostID
		ArrayList<String> corpusFileLines = new ArrayList<String>();
		FileUtil.readLines(trainFile.getAbsolutePath(), corpusFileLines);			
		String vectorFileName = featureVectorPath + "Vectors_" + trainFile.getName();
		FileWriter vectorFileWriter = new FileWriter(vectorFileName);
		for(String corpusLine : corpusFileLines){
			String[] corpusTokens = corpusLine.split("\t");
			if(!ExtractAspectUserMain.isNumeric(corpusTokens[0])) continue; // Noise data		
			sentIDMap.put(Integer.valueOf(corpusTokens[0]), corpusTokens[2]);
			sentPostIDMap.put(Integer.valueOf(corpusTokens[0]),  corpusTokens[1]);
		}				
		for(Integer sentID : sentIDMap.keySet()){
			String Sent = sentIDMap.get(sentID);
			System.out.println("SentID:" + sentID + "\t" + Sent);
			WDTree wdtree = new WDTree();
			WDTreeNode root = wdtree.bulidPDTFromSentence(Sent,opinionWordsSet);
			printFeaturesOfTOPhrasePair(root, vectorFileWriter, sentID, Sent);	
		}	
		System.out.println(vectorFileName + " Preprocess Done!");
	}
}
