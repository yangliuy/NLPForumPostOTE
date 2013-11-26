package edu.pku.yangliu.nlp.pdt.svm;
import java.io.IOException;

/**JAVA test code for LibSVM
 * @author YANG Liu
 * @throws IOException 
 * @blog http://blog.csdn.net/yangliuy
 * @mail yang.liu@pku.edu.cn
 */

public class SVMClassification {
	
	public static void svmClassify(String trainFile, String testFile, String resultVectors, String penaltyParameter) throws IOException {
		// TODO Auto-generated method stub
		//Test for svm_train and svm_predict
		//svm_train: 
		//	  param: String[], parse result of command line parameter of svm-train
		//    return: String, the directory of modelFile
		//svm_predect:
		//	  param: String[], parse result of command line parameter of svm-predict, including the modelfile
		//    return: Double, the accuracy of SVM classification
		String resultFile = resultVectors;
		//String[] trainArgs = {"-v", "5", trainFile};//5 fold cross validation
		//String[] trainArgs = {trainFile};//directory of training file
		String[] trainArgs = {"-s", "0", "-b", "1", "-c", "10", "-w1", penaltyParameter, "-w-1", "1", trainFile};
		//String[] trainArgs = {"-b", "1", trainFile};
		//System.out.println("Training begin based on " + trainFile);
		String modelFile = svm_train.main(trainArgs);
		String[] testArgs = {"-b", "1", testFile, modelFile, resultFile};//directory of test file, model file, result file
		//System.out.println("Testing begin based on " + testFile + " and " + modelFile);
		Double accuracy = svm_predict.main(testArgs);
		//System.out.println("SVM Classification is done! The accuracy is " + accuracy);
		//System.out.println("SVM Classification is done! The resultFile is " + resultFile);
		
		//Test for cross validation
		//String[] crossValidationTrainArgs = {"-v", "5", trainFile};// 5 fold cross validation
		//String modelFile = svm_train.main(crossValidationTrainArgs);
		//System.out.println("Cross validation is done! The modelFile is " + modelFile);
	}

}
