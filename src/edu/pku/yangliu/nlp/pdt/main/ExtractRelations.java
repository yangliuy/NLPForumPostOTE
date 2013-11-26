package edu.pku.yangliu.nlp.pdt.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import edu.pku.yangliu.nlp.pdt.common.FileUtil;
import edu.pku.yangliu.nlp.pdt.config.DataPathConfig;
import edu.pku.yangliu.nlp.pdt.lemma.Lemma;
import edu.pku.yangliu.nlp.pdt.lemma.POSMap;
import edu.pku.yangliu.nlp.pdt.stem.Porter;

/**Class for relation extraction
 * @author YANG Liu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yang.liu@pku.edu.cn
 */

public class ExtractRelations {
	
	private static void printUserUserAspectMatrix(String featureVectorPath, String resultFile,
			String allInforTokenFile, String MatrixType, String corpusFile, int cutNERMinSenderN) throws IOException {
		// TODO Auto-generated method stub
		String opinionWordsFilePath = DataPathConfig.opinionWordsFilePath;
		String UserAspectPositiveMatrixFileName = featureVectorPath + MatrixType + "PositiveMatrix_cutMin" + cutNERMinSenderN;
		String UserAspectNegativeMatrixFileName = featureVectorPath + MatrixType + "NegativeMatrix_cutMin" + cutNERMinSenderN;
		String UserAspectMentionsMatrixFileName = featureVectorPath + MatrixType + "MentionsMatrix_CutMin" + cutNERMinSenderN;
		String UserAspectUserFileName = featureVectorPath + MatrixType + "UserFile_cutMin" + cutNERMinSenderN;
		String UserAspectAspectFileName = featureVectorPath + MatrixType + "AspectFile_cutMin" + cutNERMinSenderN;
		String UAOPFile = featureVectorPath + MatrixType +"UAOPfile_cutMin" + cutNERMinSenderN;
		ArrayList<String> allInforTokenLines = new ArrayList<String>();
		ArrayList<String> corpusLines = new ArrayList<String>();
		ArrayList<String> extractRelResLines = new ArrayList<String>();
		ArrayList<String> positiveOpinions = new ArrayList<String>();
		ArrayList<String> negativeOpinions = new ArrayList<String>();
		FileUtil.readLines(allInforTokenFile, allInforTokenLines);
		FileUtil.readLines(resultFile, extractRelResLines);
		FileUtil.readLines(opinionWordsFilePath + "positive.txt",  positiveOpinions);
		FileUtil.readLines(opinionWordsFilePath + "negative.txt",  negativeOpinions);
		FileUtil.readLines(corpusFile, corpusLines);
		Map<String, String> postIDSenderMap = ExtractAspectUserMain.getPostIDSenderMapByAllInforLines(allInforTokenLines);
		Map<String, String> sentIDPostIDMap = getSentIDPostIDMap(corpusFile);
		Map<String, Integer> userIDs = new TreeMap<String, Integer>();
		Map<String, Integer> aspects = new TreeMap<String, Integer>();
		initialUserIDAspect(userIDs, aspects, postIDSenderMap, extractRelResLines, sentIDPostIDMap);
		int [][] UserAspectPosiMatrix = new int[userIDs.size()][aspects.size()];
		int [][] UserAspectNegaMatrix = new int[userIDs.size()][aspects.size()];	
		int [][] UserAspectMentionsMatrix = new int[userIDs.size()][aspects.size()];
		getUserAspectPosNegMatrix(UserAspectPosiMatrix, UserAspectNegaMatrix, userIDs, aspects, extractRelResLines, positiveOpinions, negativeOpinions, postIDSenderMap, sentIDPostIDMap);
		getUserAspectMentionsMatrix(UserAspectMentionsMatrix, userIDs, aspects, corpusLines, postIDSenderMap);
		printMatrix(UserAspectPosiMatrix, userIDs, aspects, UserAspectPositiveMatrixFileName);
		printMatrix(UserAspectNegaMatrix, userIDs, aspects, UserAspectNegativeMatrixFileName);
		printMatrix(UserAspectMentionsMatrix, userIDs, aspects, UserAspectMentionsMatrixFileName);
		printUserIDsAspects(userIDs, UserAspectUserFileName);
		printUserIDsAspects(aspects, UserAspectAspectFileName);
		printUserAspectOpinonPolarity(UAOPFile, extractRelResLines, positiveOpinions, postIDSenderMap, sentIDPostIDMap);
	}

	private static void getUserAspectMentionsMatrix(
			int[][] userAspectMentionsMatrix, Map<String, Integer> userIDs,
			Map<String, Integer> aspects, ArrayList<String> corpusLines,
			Map<String, String> postIDSenderMap) {
		// TODO Auto-generated method stub
		for(int i = 0; i < userIDs.size(); i++){
			for(int j = 0; j < aspects.size(); j++){
				userAspectMentionsMatrix[i][j] = 0;
			}
		}
		for(String corpusLine : corpusLines){
			String[] corpusLineTokens = corpusLine.split("\t");
			String user = postIDSenderMap.get(corpusLineTokens[1].trim());
			if(!userIDs.containsKey(user.trim())) continue;
			//System.out.println("test1");
			int userID = userIDs.get(user.trim());
			String[] sentWords = corpusLineTokens[2].split(" ");
			for(String sentWord : sentWords){
				if(sentWord.contains("ASPECT_")){
					if(aspects.containsKey(sentWord.trim())){
						int aspectID = aspects.get(sentWord.trim());
						//System.out.println("test2");
						userAspectMentionsMatrix[userID][aspectID]++;
					}
				}
			}
		}
	}

	private static void printUserIDsAspects(Map<String, Integer> aspects,
			String userAspectAspectFileName) throws IOException {
		// TODO Auto-generated method stub
		FileWriter aspectFileWriter = new FileWriter(userAspectAspectFileName);
		for(String aspect : aspects.keySet()){
			aspectFileWriter.append(aspect + "\n");
			
		}
		aspectFileWriter.flush();
		aspectFileWriter.close();
	}

	private static void printUserAspectOpinonPolarity(
			String UAOPFile,
			ArrayList<String> extractRelResLines,
			ArrayList<String> positiveOpinions,
			Map<String, String> postIDSenderMap,
			Map<String, String> sentIDPostIDMap) throws IOException {
		// TODO Auto-generated method stub
		FileWriter UAOFileWriter = new FileWriter(UAOPFile);
		for(String relLine : extractRelResLines){
			String[] relLineTokens = relLine.split("\t");
			UAOFileWriter.append(relLineTokens[0] + "\t" + relLineTokens[1] + "\t" 
								+postIDSenderMap.get(sentIDPostIDMap.get(relLineTokens[1])) + "\t"
								+relLineTokens[2] + "\t" +relLineTokens[3] + "\t" );
			String firstOpinionWord = relLineTokens[3].trim().split(" ")[0];
			if((positiveOpinions.contains(firstOpinionWord) && relLineTokens[3].trim().split(" ").length == 1)
			   ||(!positiveOpinions.contains(firstOpinionWord) && relLineTokens[3].trim().split(" ").length > 1)) {
				//System.out.println("find  positive word " + relLineTokens[3]);
				UAOFileWriter.append("Positive" + "\t");
			} else {
				UAOFileWriter.append("Negative" + "\t");
			}
			UAOFileWriter.append(relLineTokens[5] + "\t" + relLineTokens[4] + "\n");		
			UAOFileWriter.flush();
		}	
	}

	private static void printMatrix(int[][] userAspectMatrix,
			Map<String, Integer> userIDs, Map<String, Integer> aspects, String userAspectMatrixFileName) throws IOException {
		// TODO Auto-generated method stub
		FileWriter userAspectFileWriter = new FileWriter(userAspectMatrixFileName);
		for(String user : userIDs.keySet()){
			for(String aspect : aspects.keySet()){
				userAspectFileWriter.append(userAspectMatrix[userIDs.get(user)][aspects.get(aspect)] + "\t");
			}
			userAspectFileWriter.append("\n");
			userAspectFileWriter.flush();
		}
		userAspectFileWriter.close();
	}

	private static void getUserAspectPosNegMatrix(
			int[][] userAspectPosiMatrix,
			int[][] userAspectNegaMatrix, 
			Map<String, Integer> userIDs, 
			Map<String, Integer> aspects,
			ArrayList<String> extractRelResLines,
			ArrayList<String> PositiveOpinions, 
			ArrayList<String> negativeOpinions,
			Map<String, String> postIDSenderMap, 
			Map<String, String> sentIDPostIDMap) {
		for(int i = 0; i < userIDs.size(); i++){
			for(int j = 0; j < aspects.size(); j++){
				userAspectPosiMatrix[i][j] = 0;
			}
		}
		for(int i = 0; i < userIDs.size(); i++){
			for(int j = 0; j < aspects.size(); j++){
				userAspectNegaMatrix[i][j] = 0;
			}
		}
		
		for(String resLine : extractRelResLines){
			String[] resLineTokens = resLine.split("\t");
			int matrixUserIndex = userIDs.get(postIDSenderMap.get(sentIDPostIDMap.get(resLineTokens[1])).trim());
			int matrixAspectIndex = aspects.get(resLineTokens[2].trim());
			if(resLineTokens[3].split(" ").length == 0){
				System.out.println("find empty opinion! " + resLineTokens[3]);
				System.out.println("resLine : " + resLine);
				continue;
			}
			String firstOpinionWord = resLineTokens[3].split(" ")[0].trim();
			
			if(PositiveOpinions.contains(firstOpinionWord)){
				if( resLineTokens[3].split(" ").length > 1 && resLineTokens[3].split(" ")[1].trim().equals("no")){
					userAspectNegaMatrix[matrixUserIndex][matrixAspectIndex]++;
				} else {
					userAspectPosiMatrix[matrixUserIndex][matrixAspectIndex]++;
				}
			}
			if(negativeOpinions.contains(firstOpinionWord)){
				if( resLineTokens[3].split(" ").length > 1 && resLineTokens[3].split(" ")[1].trim().equals("no")){
					userAspectPosiMatrix[matrixUserIndex][matrixAspectIndex]++;
				} else {
					userAspectNegaMatrix[matrixUserIndex][matrixAspectIndex]++;
				}
			}
		}
	}

	private static void initialUserIDAspect(Map<String, Integer> userIDs,
			Map<String, Integer> aspects, Map<String, String> postIDSenderMap,
			ArrayList<String> extractRelResLines, Map<String, String> sentIDPostIDMap) {
		// TODO Auto-generated method stub
		Integer currentLastUserIndex = new Integer(0);
		Integer currentLastAspectIndex = new Integer(0);
		for(String resLine : extractRelResLines){
			String[] resLineTokens = resLine.split("\t");
			String aspect = resLineTokens[2].trim();
			if(!aspects.containsKey(aspect)){
				//System.out.println("new aspect:" + resLineTokens[2]);
				aspects.put(aspect, currentLastAspectIndex);
				currentLastAspectIndex++;
			}
			
			String newUser = postIDSenderMap.get(sentIDPostIDMap.get(resLineTokens[1])).trim();
			//System.out.println("now user = " + newUser);
			//System.out.println("userID size" + userIDs.size());
			if(!userIDs.containsKey(newUser)){
				//System.out.println("new user:" + newUser);
				//System.out.println("resLineTokens[1].trim()" + resLineTokens[1].trim());
				userIDs.put(newUser, currentLastUserIndex);
				currentLastUserIndex++;
			}
		}
		//!Assign a new ID for aspects and userIDs
		Integer count = new Integer(0);
		for(String aspect : aspects.keySet()){
			aspects.put(aspect, count);
			count++;
		}
		count = 0;
		for(String userID : userIDs.keySet()){
			userIDs.put(userID, count);
			count++;
		}
		System.out.println("aspects size " + aspects.size());
		System.out.println("userIDs size" + userIDs.size());	
	}

	private static Map<String, String> getSentIDPostIDMap(String sentIDPostIDFile) {
		// TODO Auto-generated method stub
		Map<String, String> sentIDPostIDMap =  new HashMap<String, String>();
		ArrayList<String> lines = new ArrayList<String>();
		FileUtil.readLines(sentIDPostIDFile, lines);
		for(String line : lines){
			String [] lineTokens = line.split("\t");
			sentIDPostIDMap.put(lineTokens[0], lineTokens[1]);
		}
		return sentIDPostIDMap;
	}

	private static double evaluateExtRelRecall(String uaopfileName,
			String labelfileName) throws IOException {
		// TODO Auto-generated method stub
		Map<String, Vector<String>> uaopResMap = new TreeMap<String, Vector<String>>();
		ArrayList<String> labelLines = new ArrayList<String>();
		//For each labelLine, try to whether find it appears in the result UAOP file
		BufferedReader uaopBR = new BufferedReader(new FileReader(uaopfileName));
		String line;
		while( (line = uaopBR.readLine()) != null){
			String[] lineTokens = line.split("\t");
			String newUAOP = lineTokens[3] + "\t" + lineTokens[4] + "\t" + lineTokens[5] + "\t" + lineTokens[6] + "\t" + lineTokens[7];
			if(uaopResMap.containsKey(lineTokens[1])){
				Vector<String> curUAOPS = uaopResMap.get(lineTokens[1]);
				curUAOPS.add(newUAOP);
				uaopResMap.put(lineTokens[1], curUAOPS);
			} else {
				Vector<String> curUAOPS = new Vector<String>();
				curUAOPS.add(newUAOP);
				uaopResMap.put(lineTokens[1], curUAOPS);
			}
		}
		FileUtil.readLines(labelfileName, labelLines);
		double totalNum = labelLines.size();
		double lableFound = 0;
		for(String labelLine : labelLines){
			System.out.println("Now lableLine = " + labelLine);
			Vector<String> uaops = uaopResMap.get(labelLine.split("\t")[0]);
			if(uaops == null) continue;
			if(isLabelFound(labelLine, uaops)){
				lableFound ++;
			}
		}
		System.out.println("lableFound / totalNum is: " + lableFound + "/" + totalNum);
		return lableFound / totalNum;
	}
	
	private static boolean isLabelFound(String labelLine, Vector<String> uaops) throws MalformedURLException {
		// TODO Auto-generated method stub
		String wordnetPath = "lib/dict/";
		new POSMap();
		new Lemma(wordnetPath);
		Porter stemmer = new Porter();
		String[] labelLineTokens = labelLine.split("\t");
		String labelAspect = converToLemmaWords(labelLineTokens[2]);
		String[] opinionWords = labelLineTokens[3].trim().split(" ");
		opinionWords[0] = opinionWords[0].split("_")[0];
		String labelOpinion = "";
		for(String opinionWord : opinionWords){
			labelOpinion += opinionWord + " ";
		}
		labelOpinion = labelOpinion.trim();
		String labelPorlarity = labelLineTokens[4].trim().toLowerCase();
		for(String uaop : uaops){
			System.out.println("uaop : " + uaop);
			String uaopTokens[] = uaop.split("\t");
			String[] aspectWords = uaopTokens[0].split("_");
			String aspect = "";
			for(int i = 1; i < aspectWords.length; i++){
				aspect += aspectWords[i].toLowerCase() + " ";
			}
			String opinion = uaopTokens[1].trim();
			System.out.println("aspect/labelApect: "  + aspect.trim() + "/" + labelAspect);
			System.out.println("opinion/labelOpinion: "  + opinion.trim() + "/" + labelOpinion);
			System.out.println("porlarity/labelPorlarity: " +  uaopTokens[2].trim().toLowerCase() + "/" + labelPorlarity.trim());
			
			if(isAspectOverLapEqual(aspect.trim(), labelAspect)
				//aspect.trim().equals(labelAspect)
			   && stemmer.stripAffixes(opinion.trim()).equals(stemmer.stripAffixes(labelOpinion))
			   && uaopTokens[2].trim().toLowerCase().equals(labelPorlarity)){
				System.out.println("haha! find lable!" );
				return true;
			}
		}
		return false;
	}

	private static boolean isAspectOverLapEqual(String aspect, String labelAspect) {
		// TODO Auto-generated method stub
		String [] aspectWords = aspect.split(" ");
		String [] labelAspectWords = labelAspect.split(" ");
		for(String aspectWord : aspectWords) {
			for(String labelAspectWord : labelAspectWords){
				if(aspectWord.equals(labelAspectWord)) return true;
			}
		}
		return false;
	}

	private static String converToLemmaWords(String labelAspect) throws MalformedURLException {
		// TODO Auto-generated method stub
		String[] labelAspectWords = labelAspect.split(" ");
		String stemLabelAspect = "";
		for(String labelAspectWord : labelAspectWords){
			String labelAspectWordPart = labelAspectWord.split("_")[0].toLowerCase();
			String lemmaWord = Lemma.getLemma(labelAspectWordPart, "NNS");
			stemLabelAspect += lemmaWord + " ";
		}
		return stemLabelAspect.trim();
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void extractRel(File corpusFile, String allInforTokenFile, String MatrixType, String featureVectorPath, int CutNERMinSenderN) throws IOException  {
		// TODO Auto-generated method stub
		PreprocessText ppText = new PreprocessText();
		ClassifyBasedOnPathRules cbp = new ClassifyBasedOnPathRules();
		String opinionWordsFilePath = DataPathConfig.opinionWordsFilePath;
		String pathRulesPath = DataPathConfig.pathRulesPath;
		ppText.preProcess(corpusFile, featureVectorPath, opinionWordsFilePath);
		String testFile = featureVectorPath + "Vectors_" + corpusFile.getName();
		String resultFile = featureVectorPath + "Vectors_" + MatrixType + "_RelResult_" + corpusFile.getName();
		String pathFile = new File(pathRulesPath).listFiles()[0].getAbsolutePath();	
		cbp.classifyBasedOnPath(pathFile, testFile, resultFile);
		printUserUserAspectMatrix(featureVectorPath, resultFile, allInforTokenFile, MatrixType, corpusFile.getAbsolutePath(), CutNERMinSenderN);
		//String uaopfileName = DataPathConfig.matrixPath + MatrixType +"UAOPfile_cutNERMin" + CutNERMinSenderN;
		//String labelfileName = DataPathConfig.scriptDataPath + "UserAspectInteractionTagging_allLabled.txt";
		//double recall = evaluateExtRelRecall(uaopfileName, labelfileName);
		//System.out.println("The recall of relation extraction is: " + recall);
	}
}
