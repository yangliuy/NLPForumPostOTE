package edu.pku.yangliu.nlp.pdt.tree;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import opennlp.tools.util.InvalidFormatException;

import edu.pku.yangliu.nlp.pdt.parser.ShallowParser;
import edu.pku.yangliu.nlp.pdt.parser.StanfordParser;
import edu.pku.yangliu.nlp.pdt.stem.Porter;
import edu.stanford.nlp.trees.TypedDependency;

/**Class for the Words Dependency Tree 
 * @author YANG Liu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yang.liu@pku.edu.cn
 */

public class WDTree {
	
	private WDTreeNode root;

	public WDTreeNode getRoot() {
		return root;
	}

	public void setRoot(WDTreeNode root) {
		this.root = root;
	}
	
	/** Build WDTree from Typed Dependency List.Delete the stop words
	 *  and stemming at the same time
	 * @param tdl Typed Dependency List
	 * @param phraseLablesMap Map for the phrase labels of the words
	 * @param stopWordsSet Set for stop words
	 * @return WDTreeNode root of WDTree
	 */
	public WDTreeNode bulidWDTreeFromList(List<TypedDependency> tdl, Map<Integer,String> phraseLablesMap){
		WDTreeNode root = new WDTreeNode("ROOT-0");
		//System.out.println("haha!");
		int count = 0;//count the number of links
		WDTreeNode curNode = root;
		Queue<WDTreeNode> wordNodeList = new LinkedList<WDTreeNode>();//Tip: LinkedList implement Queue interface
		//System.out.println("wordNodelist size:" + wordNodeList.size());
		wordNodeList.add(curNode);
		String posTag = null, phLable = null;
		//System.out.println(tdl);
		while(count < tdl.size() && !wordNodeList.isEmpty()){ 
			//System.out.println("wordNodelist size:" + wordNodeList.size());
			curNode = wordNodeList.poll();	
			for(TypedDependency iTdl : tdl){
				//System.out.println("before propcess curGovString = " + iTdl.gov().toString());
				String curGovString = iTdl.gov().toString();		
				//curGovString = stemAndDelStopWords(curGovString, stopWordsSet);
				//System.out.println("curGovString = " + curGovString);
				if(curGovString.equals(curNode.getWords().get(0).toString())){
					String nodeWord = iTdl.dep().toString();
					//nodeWord = stemAndDelStopWords(nodeWord, stopWordsSet);				
					//System.out.println("nodeWord:" + nodeWord);
					WDTreeNode node = new WDTreeNode(nodeWord);
					node.setParent(curNode);
					node.setDepLinkLable(iTdl.reln().toString());
					String [] nodeWordParts = nodeWord.split("-+");
					String indexOfTokenInStanfordParser = nodeWordParts[nodeWordParts.length - 1];
					if(indexOfTokenInStanfordParser.contains("'")) {
						indexOfTokenInStanfordParser = indexOfTokenInStanfordParser.split("'")[0];
					}
					Integer indexOfTokenInOpenNLPParser = Integer.valueOf(indexOfTokenInStanfordParser) - new Integer(1);
					if(phraseLablesMap.get(indexOfTokenInOpenNLPParser) != null){//Notice!
						String [] phLableArray = phraseLablesMap.get(indexOfTokenInOpenNLPParser).split("-");
						phLable = phLableArray[0];
						posTag =  phLableArray[1];
					} 
					node.setPhLabel(phLable);
					String generalPosTag = convertToGeneralPosTag(posTag);
					node.setPosTagForHeadWord(generalPosTag);
					curNode.addChild(node);
					//System.out.println("add child:" + node.getWords());
					wordNodeList.add(node);
					count++;
				}
			}		
		}
		return root;	
	}
	
	private String convertToGeneralPosTag(String posTag) {
		// TODO Auto-generated method stub
		String generalPosTag = posTag;
		if(posTag.equals("CD")
		   || posTag.equals("JJ")
		   || posTag.equals("JJR")
		   || posTag.equals("JJS")) generalPosTag = "ADJ";
		
		if(posTag.equals("VB")
		   || posTag.equals("VBD")
		   || posTag.equals("VBG")
		   || posTag.equals("VBN")
		   || posTag.equals("VBP")
		   || posTag.equals("VBZ")
		   || posTag.equals("MD")) generalPosTag = "V";
		
		if(posTag.equals("NN")
		   || posTag.equals("NNS")
		   || posTag.equals("NNP")
		   || posTag.equals("NNPS")) generalPosTag = "N";
		
		if(posTag.equals("RB")
		   || posTag.equals("RBR")
		   || posTag.equals("RBS")
		   || posTag.equals("RP")
		   || posTag.equals("WRB")) generalPosTag = "ADV";
		
		if(posTag.equals("DT")
		   || posTag.equals("PDT")
		   || posTag.equals("WDT")
		   || posTag.equals("POS")
		   || posTag.equals("WRB")) generalPosTag = "DET";
		
		if(posTag.equals("PRP")
		   || posTag.equals("WP")) generalPosTag = "PRP";
		
		if(posTag.equals("PRP$")
		   || posTag.equals("WP$")) generalPosTag = "PRP$";
		
		if(posTag.equals("TO")
		   || posTag.equals("IN")) generalPosTag = "PREP";
		
		if(posTag.equals("CC")) generalPosTag = "CONJ";
		
		if(posTag.equals("EX")
		   || posTag.equals("FW")
		   || posTag.equals("SYM")
		   || posTag.equals("UH")
		   || posTag.equals("LS")) generalPosTag = "OTHER";

		return generalPosTag;
	}

	/** Stem and delete the stop words in the output tokens of Stanford Parser
	 *  Example: enjoyed-3  change to enjoi-3 ; the-6 change to NULL-6
	 * @param String
	 * @return String
	 */
	private String stemAndDelStopWords(String nodeWord, Set<String> stopWordsSet) {
		// TODO Auto-generated method stub
		Porter stemmer = new Porter();
		String[] nodeWordParts = nodeWord.split("-+");
		String newWord;
		if(nodeWordParts[0].isEmpty()) {
			newWord = nodeWordParts[1];
			for(int i = 2; i < nodeWordParts.length; i++){
				newWord += "-" + nodeWordParts[i];
			}
		} else {
			nodeWordParts[0] = stemmer.stripAffixes(nodeWordParts[0]);
			if(stopWordsSet.contains(nodeWordParts[0])) {
				newWord = "NULL"; //Set stop words to NULL
			} else {
				newWord = nodeWordParts[0];
			}	
			for(int i = 1; i < nodeWordParts.length; i++){
				newWord += "-" + nodeWordParts[i];
			}
		}
		return newWord;
	}

	/** convert a word dependency tree to a super word dependency tree,
	 *  that is, a node may contain more than one words. 
	 * @param tdl Typed Dependency List
	 * @param phraseLablesMap Map for the phrase labels of the words
	 * @return WDTreeNode root of WDTree
	 */
	public WDTreeNode converToSuperWdtree(WDTreeNode root) {
		// TODO Auto-generated method stub	
		WDTreeNode curNode = root;
		Queue<WDTreeNode> wordNodeList = new LinkedList<WDTreeNode>();//Tip: LinkedList implement Queue interface
		wordNodeList.add(curNode);
		while( !wordNodeList.isEmpty()){
			curNode = wordNodeList.poll();	
			if (curNode.getChildren() == null) continue;
			Vector<WDTreeNode> children = curNode.getChildren();
			for(int i = 0; i < children.size(); i++) {
				if(curNode.getPhLabel() != null && curNode.getPhLabel().equals(children.get(i).getPhLabel()) && curNode.getPhLabel().contains("NP")){
					//only compact NP as target phrase
					System.out.println("!" + curNode.getWords().get(0) + " compacts " + children.get(i).getWords().get(0));
					curNode.getWords().add(children.get(i).getWords().get(0));//The child node only have one word
					if(children.get(i).getChildren()!= null){
						for(WDTreeNode child2 : children.get(i).getChildren()){
							curNode.addChild(child2);
							child2.setParent(curNode);//!!!Noticed:The parent node of the children should also be updated!
							wordNodeList.add(child2);
						}
					}
					curNode.delChild(children.get(i));//Since the child have been compacted, it will not be existed
					i--;//Notice!
				} else {
					wordNodeList.add(children.get(i));
				}
			}		
		}
		return root;
	}
	
	/** Print a word dependency tree
	 * @param root The root of Typed Dependency Tree
	 * @return void
	 */
	public void printWDTree(WDTreeNode root){
		if(root.getChildren() == null) return;
		System.out.println();
		System.out.println();
		root.printChildrenNodeWord();
		for(WDTreeNode child : root.getChildren()){
			printWDTree(child);
		}
		System.out.println();
	}
	
	/**build PDT from a Sentence
	 * @param sent The sentence from which to build PDT
	 * @param aspectWordsSet Set of aspect words
	 * @param opinionWordsSet Set of opinion words
	 * @return WDTreeNode Root of PDT
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 */
	public WDTreeNode bulidPDTFromSentence(String sent, HashSet<String> opinionWordsSet) throws InvalidFormatException, IOException {
		// TODO Auto-generated method stub
		//Notice: There should be " " BEFORE and after ",", " ","(",")" etc.
		  if(sent.isEmpty()) return null;
		  StanfordParser sdPaser = StanfordParser.getInstance();		 
		  List<TypedDependency> tdl = sdPaser.DPFromString(sent);
		 
		  ShallowParser swParser = ShallowParser.getInstance();
		  Map<Integer,String> phraseLablesMap = new TreeMap<Integer, String>();
		  phraseLablesMap = swParser.chunk(sent);
		  //System.out.println("build PDT from :" + sent);
		  //System.out.println("phraseLablesMap " );
		  /*for(Integer TokenID : phraseLablesMap.keySet()){
			  System.out.println(TokenID + ":" + phraseLablesMap.get(TokenID));
		  }*/
		  WDTreeNode root = bulidWDTreeFromList(tdl, phraseLablesMap);
		  //printWDTree(root);
		  //root = converToSuperWdtree(root);
		  //printWDTree(root);
		  root = findCandidateTargetAndOpinion(root, opinionWordsSet);
		  return root;
	}
	
	/**find candidate target and candidate opinion node in PDT
	 * @param WDTreeNode root
	 * @param HashSet<String> aspectWordsSet
	 * @param HashSet<String> opinionWordsSet
	 * @return WDTreeNode
	 */
	private WDTreeNode findCandidateTargetAndOpinion(WDTreeNode root, HashSet<String> opinionWordsSet) {
		// TODO Auto-generated method stub
		WDTreeNode curNode = root;
		Queue<WDTreeNode> wordNodeList = new LinkedList<WDTreeNode>();//Tip: LinkedList implement Queue interface
		wordNodeList.add(curNode);
		while( !wordNodeList.isEmpty()){
			curNode = wordNodeList.poll();	
			if(isCandidateOpinionNode(curNode, opinionWordsSet)){
				curNode.setOpinionLable(true);
				//Find negation flag not/no/n't
				if(isNegationOpinion(curNode)){
					Vector<String> newWords = curNode.getWords();
					newWords.add("no-1001");
					curNode.setWords(newWords);
				}
			} 
			if(isCandidateTargetNode(curNode)){
				curNode.setTargetLable(true);
			}
			if (curNode.getChildren() == null) continue;
			Vector<WDTreeNode> children = curNode.getChildren();
			for(int i = 0; i < children.size(); i++) {
				wordNodeList.add(children.get(i));		
			}		
		}
		return root;
	}

	private boolean isNegationOpinion(WDTreeNode curNode) {
		// TODO Auto-generated method stub
		Vector<WDTreeNode> children = curNode.getChildren();
		if(children == null) return false;
		for(WDTreeNode child: children){
			for(String childword : child.getWords()){
				String childwordPart = childword.split("-")[0];
				if(childwordPart.equals("no") || childwordPart.equals("not") || childwordPart.equals("n't") || childwordPart.equals("dont") || childwordPart.equals("wont")){
					return true;
				}
			}
		}	
		return false;
	}

	/** Check whether the node is a candidate opinion node
	 * @param node
	 * @param opinionWordsSet 
	 * @return boolean
	 */
	private boolean isCandidateOpinionNode(WDTreeNode node, HashSet<String> opinionWordsSet) {
		// TODO Auto-generated method stub
		for(String nodeWord : node.getWords()){
			nodeWord = nodeWord.split("-")[0];
			//System.out.println("opinion Words size " + opinionWordsSet.size());
			if(opinionWordsSet.contains(nodeWord.trim())){
				//System.out.println("In isCandidateOpinionNode function: hehe! find candidate opinion node:" + nodeWord);
				return true;
			}
		}	
		return false;
	}

	/** Check whether the node is a candidate target node
	 * @param node
	 * @param aspectWordsSet 
	 * @return boolean
	 */
	private boolean isCandidateTargetNode(WDTreeNode node) {
		// TODO Auto-generated method stub	
		if(node.getWords().get(0).split("-")[0].contains("ASPECT_")) return true;
		else return false;
	}
	
	/** Check whether the nodeword is in aspectWordsSet
	 * @param nodeWord
	 * @param aspectWordsSet 
	 * @return boolean
	 */
	private boolean isInAspectWordsSet(HashSet<String> aspectWordsSet,
			String nodeWord) {
		if(nodeWord.isEmpty()) return false;
		// TODO Auto-generated method stub
		//System.out.println("apect words size " + aspectWordsSet.size());
		for(String aspectWords : aspectWordsSet){
			String[] aspectWord = aspectWords.split(" ");
			for(String aspectWordpart : aspectWord){
				if(aspectWordpart.equals(nodeWord)) return true;
			}
		}
		return false;
	}

	/**Find the shortest path in two nodes of the WDTree through LCA search algorithm
	 * Update: 2012/11/26 add direction for path
	 * All direction are from govern node to dependency node
	 * The LCA path is from opinion node to target node
	 * @param root Root of WDTree
	 * @param pNode 
	 * @param qNode
	 * @return String Path String contains word/phLable/link
	 */
	public static String FindLCAPath(WDTreeNode root, WDTreeNode pTargetNode, WDTreeNode qOpinionNode){
		WDTreeNode realRoot = root.getChildren().get(0);//The root node just has one child
		Vector<WDTreeNode> pPath = new Vector<WDTreeNode>();
		Vector<WDTreeNode> qPath = new Vector<WDTreeNode>();
		String lcaPathString = "";
		if(pTargetNode.getParent().compareTo(qOpinionNode) == 0){

			//lcaPathString = "2_" + pTargetNode.getPosTagForHeadWord() + "_" + pTargetNode.getDepLinkLable() + "_<" + qOpinionNode.getPosTagForHeadWord();
			lcaPathString = "2_" + qOpinionNode.getPosTagForHeadWord() + "_" + pTargetNode.getDepLinkLable() + "_>" + pTargetNode.getPosTagForHeadWord();
		} else if(qOpinionNode.getParent().compareTo(pTargetNode) == 0){

			lcaPathString = "2_" + qOpinionNode.getPosTagForHeadWord() + "<_" + qOpinionNode.getDepLinkLable() + "_" + pTargetNode.getPosTagForHeadWord();
		}else {
			WDTreeNode tempNode = qOpinionNode;
			boolean foundFlag = false;
			pPath.add(pTargetNode);
			if(pTargetNode.compareTo(realRoot) == 0){
				while(qOpinionNode.compareTo(realRoot) != 0){
					qPath.add(qOpinionNode);
					qOpinionNode = qOpinionNode.getParent();
				}
			} else {
				while(pTargetNode != realRoot){
					tempNode = qOpinionNode;
					qPath.clear();
					qPath.add(tempNode);
					while(tempNode != realRoot){
						if(tempNode.compareTo(pTargetNode) == 0 ){
							foundFlag = true;
							//System.out.println("Two Paths Crossed!!!");
							break;
						}
						tempNode = tempNode.getParent();
						qPath.add(tempNode);
					}
					if(foundFlag == true) break;
					pTargetNode = pTargetNode.getParent();
					pPath.add(pTargetNode);
				}
			}	
			//When the last node of pPath and the last node of qPath is the same,
			//we just remain the last node in pPath and delete the same last node in qPath
			//System.out.println("qPath size " + qPath.size());
			//System.out.println("pPath size " + pPath.size());
			if(qPath.get(qPath.size() - 1).compareTo(pPath.get(pPath.size() - 1)) == 0){
				qPath.remove(qPath.size() - 1).getWords();
				//System.out.println("removed!" );				
			}
			
			for(int i = 0; i < qPath.size(); i++){
				lcaPathString += qPath.get(i).getPosTagForHeadWord() + "<_"+qPath.get(i).getDepLinkLable()+"_";
			}
			
			for(int i = pPath.size() - 1; i > -1; i--){
				if(i == pPath.size() - 1) {
					lcaPathString += pPath.get(i).getPosTagForHeadWord();
				} else {
					lcaPathString += "_" + pPath.get(i).getDepLinkLable()+"_>" + pPath.get(i).getPosTagForHeadWord();
				}
				
			}	
			Integer lcaPathLength = pPath.size() + qPath.size();
			lcaPathString = lcaPathLength.toString() +"_"+ lcaPathString;
		}
		return lcaPathString;
	}
}
