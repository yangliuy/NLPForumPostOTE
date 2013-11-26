package edu.pku.yangliu.nlp.pdt.main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**Classify the test samples based on path rules
 * @author YANG Liu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yang.liu@pku.edu.cn
 */

public class ClassifyBasedOnPathRules {

	/**Classify test samples based on path between the two node
	 * @param trainVectors
	 * @param testVectors
	 * @param resultVectors
	 * @return void
	 * @throws IOException 
	 */
	public void classifyBasedOnPath(String pathFile, String testFile,
			String resultFile) throws IOException {
		// TODO Auto-generated method stub
		Map<String, String> pathsMap = getPathsMapForTrainVectors(pathFile);
		BufferedReader testVectorsBR = new BufferedReader(new FileReader(testFile));
		FileWriter resultVectorsWriter = new FileWriter(resultFile);
		String line;
		while((line = testVectorsBR.readLine()) != null){
			String[] lineTokens = line.split("\t");
			String testPath = lineTokens[4].split(" ")[0];
			if(testPath.contains("prep")){
				testPath = testPath.replaceAll("prep_[a-z]+", "prep_*");
			}
			if(pathsMap.containsKey(testPath) && !lineTokens[3].trim().isEmpty() && !lineTokens[2].trim().isEmpty()){
				resultVectorsWriter.append("1\t" + lineTokens[1] + "\t" + lineTokens[2] + "\t" + lineTokens[3]+ "\t" + lineTokens[4] + "\t" + lineTokens[0]+ "\n");
			} else {
				//resultVectorsWriter.append("-1\t" + lineTokens[1] + "\t" + lineTokens[2] + "\t" + lineTokens[3]+ "\t" + lineTokens[4] + "\t" + lineTokens[0]+ "\n");
			}
		}
		resultVectorsWriter.flush();
		resultVectorsWriter.close();
	}
	
	/**Get paths map from train vector file
	 * @param trainVectors
	 * @param testVectors
	 * @param resultVectors
	 * @return void
	 * @throws IOException 
	 */
	private Map<String, String> getPathsMapForTrainVectors(String trainVectors) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader trainVectorsBR = new BufferedReader(new FileReader(trainVectors));
		Map<String, String> pathsMap = new HashMap<String, String>();
		String line;
		while((line = trainVectorsBR.readLine()) != null){
			String[] trainLineTokens = line.split("\t");
			if(trainLineTokens[0].equals("1") && !trainLineTokens[2].equals("1")){
				String path = trainLineTokens[1].split(" ")[0];
				//Convert prep_* in the path
				if(path.contains("prep")){
					path = path.replaceAll("prep_[a-z]+", "prep_*");
				}
				if(pathsMap.containsKey(path)){
					String newPathCounter = String.valueOf(Integer.valueOf(pathsMap.get(path)) + 1);
					pathsMap.put(path, newPathCounter);
				} else {
					pathsMap.put(path, "1");
				}
			}
		}
		System.out.println("pathsMap size" + pathsMap.size());
		return pathsMap;
	}

	/**Evaluate RPF of relation extraction
	 * @param testVectors
	 * @param resultVectors
	 * @return void
	 * @throws IOException 
	 */
	public Vector<Double> evaluateRelationExtractRPF(String testVectors,
			String resultVectors) throws IOException {
				
		// TODO Auto-generated method stub
		Vector<Vector<String>> testVec = getVecFromFile(testVectors);
		Vector<Vector<String>> resultVec = getVecFromFile(resultVectors);
		double presionCounter = 0, recallCounter = 0, totalResCounter = 0, totalTestCounter = 0;
		for(int i = 0; i < resultVec.size(); i++){
			if(resultVec.get(i).get(0).equals("1")){
				totalResCounter++;
				String testLabel = testVec.get(i).get(0);
				String resultLabel = resultVec.get(i).get(0); 
				if(resultLabel.equals(testLabel)){
					presionCounter++;
				}
			}
		}
		
		for(int i = 0; i < testVec.size(); i++){
			if(testVec.get(i).get(0).equals("1")){
				totalTestCounter++;
				String testLabel = testVec.get(i).get(0);
				String resultLabel = resultVec.get(i).get(0); 
				if(resultLabel.equals(testLabel)){
					recallCounter++;
				}
			}
		}
		
		double r = recallCounter / totalTestCounter;
		double p = presionCounter / totalResCounter;
		double f = 2 * r * p / (r + p);
		Vector<Double> RPF = new Vector<Double>();
		RPF.add(r);
		RPF.add(p);
		RPF.add(f);	
		System.out.println("recallCounter / totalTestCounter :"  +  recallCounter + " / " + totalTestCounter);
		System.out.println("presionCounter / totalResCounter :"  +  presionCounter + " / " + totalResCounter);
		return RPF;
	}

	/**Get a vector from the file
	 * @param testVectors
	 * @return Vector<Vector<String>>
	 * @throws IOException 
	 */
	private Vector<Vector<String>> getVecFromFile(String testVectors) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader testVectorsBR = new BufferedReader(new FileReader(testVectors));
		Vector<Vector<String>> testVec = new Vector<Vector<String>>();
		String line;
		while((line = testVectorsBR.readLine()) != null){
			String[] lineTokens = line.split(" ");
			Vector<String> lineVec = new Vector<String>();
			if(lineTokens[0].contains(".")){
				lineTokens[0] = lineTokens[0].split("[.]")[0];
			}
			lineVec.add(lineTokens[0]);
			if(lineTokens.length > 1){
				lineVec.add(lineTokens[1]);
			}
			testVec.add(lineVec);
		}
		return testVec;
	}
}
