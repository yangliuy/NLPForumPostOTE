/**
 * 
 */
package edu.pku.yangliu.nlp.pdt.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import edu.pku.yangliu.nlp.pdt.config.DataPathConfig;
import edu.pku.yangliu.nlp.pdt.main.ClassifyBasedOnPathRules;
import edu.pku.yangliu.nlp.pdt.main.PreprocessText;
import edu.pku.yangliu.nlp.pdt.svm.SVMClassification;

/**Script for matching LR Probility
 * @author YANG Liu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yang.liu@pku.edu.cn
 */
public class MatchLRProbility {

	private static void generateTrainAndTestSample(String featureVectorPath, String svmWholeVectors, String svmTrainVectors, String svmTestVectors, double positiveSamplePercent, double negativeSamplePercent) throws IOException {
		// TODO Auto-generated method stub
		FileWriter svmWholeVectorsWriter = new FileWriter(svmWholeVectors);
		FileWriter svmTrainVectorsWriter = new FileWriter(svmTrainVectors);
		FileWriter svmTestVectorsWriter = new FileWriter(svmTestVectors);
		Map<String, Vector<String>> finalVecMap = new HashMap<String, Vector<String>>();
		int positiveVectorCounter = 0;
		int negativeVectorCounter = 0;
		int vectorID = 0;
		String line;
		File[] featureVectorFiles = new File(featureVectorPath).listFiles();
		for(File f : featureVectorFiles){
			if(f.getName().contains("final")){
				BufferedReader finalFileBR = new BufferedReader(new FileReader(f.getAbsoluteFile()));
				Vector<String> v = new Vector<String>();
				while((line = finalFileBR.readLine()) != null){
					vectorID++;
					String[] tokens = line.split("	");
					v.clear();
					for(int i = 2; i < 5; i++){
						v.add(tokens[i]);
					}
					Vector<String> vcopy = new Vector<String>(v);
					String ID = String.valueOf(vectorID);
					System.out.println(ID + " " + vcopy);
					finalVecMap.put(ID, vcopy);
				}
			}
			vectorID = 0;
			//System.out.println(finalVecMap);
			if(f.getName().contains("svm")){
				//System.out.println("find svm vector file:" + f.getAbsolutePath());	
				FileReader fFileReader = new FileReader(f);
				BufferedReader fBR = new BufferedReader(fFileReader);
				while((line = fBR.readLine()) != null) {
					vectorID++;
					if(line.subSequence(0, 1).equals("1")){
						positiveVectorCounter++;
					} else {
						negativeVectorCounter++;
					}
					svmWholeVectorsWriter.append(vectorID + " " + line + "\n");
					svmWholeVectorsWriter.flush();
				}
			}
		}
		int totalVectorAmount = vectorID;
		int positiveVectorAmount = positiveVectorCounter;
		int negativeVectorAmount = negativeVectorCounter;
		System.out.println("vectorCounter = " + totalVectorAmount);
		System.out.println("positiveVectorCounter = " + positiveVectorAmount + " Choose " + positiveSamplePercent + " to train which is " + positiveSamplePercent * positiveVectorAmount);
		System.out.println("negativeVectorCounter = " + negativeVectorAmount + " Choose " + negativeSamplePercent + " to train which is " + negativeSamplePercent * negativeVectorAmount);	
		positiveVectorCounter = 0;
		negativeVectorCounter = 0;
		vectorID = 0;
		for(File f : featureVectorFiles){
			if(f.getName().contains("Whole")){
				System.out.println("find whole vector file:" + f.getAbsolutePath());	
				FileReader fFileReader = new FileReader(f);
				BufferedReader fBR = new BufferedReader(fFileReader);
				while((line = fBR.readLine()) != null) {
					vectorID++;
					if(!line.split(" ")[1].equals("1")){
						negativeVectorCounter++;
						if(negativeVectorCounter < negativeVectorAmount * negativeSamplePercent) {
							svmTrainVectorsWriter.append(line + "\n");
						} else {
							System.out.println("haha vectorID " + vectorID);
							//System.out.println("finalVecMap size" + finalVecMap.size());
							//System.out.println(finalVecMap);
							String vecString = "";
							for(String otp : finalVecMap.get(String.valueOf(vectorID))){
								vecString += otp + " ";
							}
							svmTestVectorsWriter.append(line.split(" ")[0] + " " + line.split(" ")[1] + " " +vecString + "\n");
						}
					} else {
						positiveVectorCounter++;
						if(positiveVectorCounter < positiveVectorAmount * positiveSamplePercent) {
							svmTrainVectorsWriter.append(line + "\n");
						} else {
							System.out.println("hehe vectorID " + vectorID);
							String vecString = "";
							for(String otp : finalVecMap.get(String.valueOf(vectorID))){
								vecString += otp + " ";
							}
							svmTestVectorsWriter.append(line.split(" ")[0] + " " + line.split(" ")[1] + " " +vecString + "\n");
						}
					}
					svmTrainVectorsWriter.flush();
					svmTestVectorsWriter.flush();
				}
			}
		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException  {
		// TODO Auto-generated method stub
		PreprocessText ppText = new PreprocessText();
		ClassifyBasedOnPathRules cbp = new ClassifyBasedOnPathRules();
		String corpusPath = DataPathConfig.corpusPath;
		String featureVectorPath = DataPathConfig.featureVectorPath;
		String opinionWordsFilePath = DataPathConfig.opinionWordsFilePath;
		String stopWordsFilePath = DataPathConfig.stopWordsFilePath;
		String aspectWordsFilePath = DataPathConfig.aspectWordsFilePath;
		String candidateOpinionPath = DataPathConfig.candidateOpinionPath;
		String candidateAspectPath = DataPathConfig.candidateAspectPath;
		String wholeVectors = featureVectorPath + "Vectors_Whole_DigitalCamera.txt";
		String trainVectors = featureVectorPath + "Vectors_Train_DigitalCamera.txt";
		String testVectors = featureVectorPath + "Vectors_Test_DigitalCamera.txt";
		String resultVectors = featureVectorPath + "Vectors_Result_DigitalCamera.txt";
		//1:penaltyParameter
		String[] penaltyParameter = {"10"};
		double[] positiveSamplePercent = {0.6};
		double[] negativeSamplePercent = {0.6};
		for(int i = 0; i < penaltyParameter.length; i++){
			for(int j = 0; j < positiveSamplePercent.length; j++){
				for(int k = 0; k < negativeSamplePercent.length; k++){
					System.out.println("now penaltyParameter = " + penaltyParameter[i]);
					System.out.println("now positiveSamplePercent = " + positiveSamplePercent[j]);
					System.out.println("now negativeSamplePercent = " + negativeSamplePercent[k]);
					generateTrainAndTestSample(featureVectorPath, wholeVectors, trainVectors, testVectors,positiveSamplePercent[j], negativeSamplePercent[k]);	
				}
			}
		}
		
		String problilityFile = DataPathConfig.scriptDataPath + "probability_Test.txt";
		String probability_OpinionSentIDFile = DataPathConfig.scriptDataPath + "probability_OpinionSentID.txt";
		String probability_TargetSentIDFile = DataPathConfig.scriptDataPath + "probability_TargetSentID.txt";
		Vector<String> proVec = new Vector<String>();
		BufferedReader problilityBR = new BufferedReader(new FileReader(problilityFile));
		BufferedReader testVectorsBR = new BufferedReader(new FileReader(testVectors));
		FileWriter probability_TargetSentIDFileWriter = new FileWriter(probability_TargetSentIDFile);
		FileWriter probability_OpinionSentIDFileWriter = new FileWriter(probability_OpinionSentIDFile);
		String line;
		while((line = problilityBR.readLine()) != null){
			proVec.add(line.trim());
		}
		
		int proVecCounter = 0;
		while((line = testVectorsBR.readLine()) != null){
			String[] tk = line.split(" ");
			System.out.println("opinion" + tk[4]);
			probability_TargetSentIDFileWriter.append(tk[0] + " " + tk[1] + " " + tk[2] +" " + tk[3] + " " + proVec.get(proVecCounter) + "\n");
			probability_OpinionSentIDFileWriter.append(tk[0] + " " + tk[1] + " " + tk[2] +" " + tk[5] + " " + proVec.get(proVecCounter) + "\n");
			proVecCounter++;
		}
		probability_TargetSentIDFileWriter.flush();
		probability_OpinionSentIDFileWriter.flush();
	}
}
