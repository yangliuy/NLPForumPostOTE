package edu.pku.yangliu.nlp.pdt.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

/**Class for coreference resolution based on Stanford CoreNLP
 * @author YANG Liu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yang.liu@pku.edu.cn
 */
public class CoreferResolve {
	
	 /**Replace PRP in the corpus
	 * @param annotation
	 * @param fWriter
	 * @param pipeline
	 * @return void
	 * @throws IOException 
	 */
	 public static void replacePRP(Annotation annotation, FileWriter fWriter, StanfordCoreNLP pipeline) throws IOException {
		     //fWriter.append("New Post!!!");
		    List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
		    // display each sentence in this annotation
		    if (sentences != null) {
			    // display the new-style coreference graph
			    Map<Integer, CorefChain> corefChains =
			      annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
			    if (corefChains != null && sentences != null) { 
			     for (CorefChain chain : corefChains.values()) {
			        CorefChain.CorefMention representative =
			          chain.getRepresentativeMention();
			        boolean outputHeading = false;
			        for (CorefChain.CorefMention mention : chain.getMentionsInTextualOrder()) {
			          if (mention == representative)
			            continue;
			          if (!outputHeading) {
			            outputHeading = true;
			            //fWriter.append("Coreference set:\n");
			          }
			          // all offsets start at 1!
			          CoreLabel mentionToken =  sentences.get(mention.sentNum - 1).get(CoreAnnotations.TokensAnnotation.class).get(mention.headIndex - 1);
			          CoreLabel representativeToken = sentences.get(representative.sentNum - 1).get(CoreAnnotations.TokensAnnotation.class).get(representative.headIndex - 1);
			          /*fWriter.append("\t(" + mention.sentNum + "," +
				              mention.headIndex + " POSTag:" + 
				              mentionToken.tag()+ " NER: " + mentionToken.ner() + ",[" +
				              mention.startIndex + "," +
				              mention.endIndex + ")) -> (" +
				              representative.sentNum + "," +
				              representative.headIndex + " POSTag:" +
				              representativeToken.tag()+ " NER: " + representativeToken.ner() + ",[" +
				              representative.startIndex + "," +
				              representative.endIndex + ")), that is: \"" +
				              mentionToken.word() + "\" -> \"" +
				              representativeToken.word() + "\"" + "\n");*/
			          
			          if(mentionToken.tag().contains("PRP") && representativeToken.tag().contains("NN")){
			        	  mentionToken.setWord(representativeToken.word().toUpperCase());
				          mentionToken.setNER(representativeToken.ner());
				          mentionToken.setTag(representativeToken.tag());
			          }  
			        }
			      }
			    }
			    fWriter.flush();
			 
		      for(int i = 0, sz = sentences.size(); i < sz; i ++) {
		        CoreMap sentence = sentences.get(i);
		        List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
		        String text = sentence.get(CoreAnnotations.TextAnnotation.class);
		        //fWriter.append("Origanal Sent " + i + ": " +text + "\n"); 
		        //fWriter.append("New      Sent " + i + ": "); 
		        for (CoreLabel token: tokens) {
		        	 fWriter.append(token.word() + " ");
		        }
		        fWriter.append("\n");
		    }
		     fWriter.flush();
		  }
	 }
	 
	 /**Co-reference resolve
	 * @param fileName
	 * @return String
	 * @throws IOException 
	 */
	 public static String coreferResolve(String allInforFileName ) throws IOException {
		    FileWriter out = new FileWriter(allInforFileName + "_repPRP");
		    StanfordCoreNLP pipeline = new StanfordCoreNLP();
		    String annotationSents = "";
		    BufferedReader asBR = new BufferedReader(new FileReader(allInforFileName));
		    String line;
		    String prePostID = "0";
		    Annotation annotation;
		    while((true)){
		    	if((line = asBR.readLine()) == null) {
		    		 System.out.println("annotationSents	new\n" + annotationSents);
		    		 annotation = new Annotation(annotationSents);  
		    		 pipeline.annotate(annotation);	    
		 		     System.out.println("Post " + prePostID + " annotation done!");
		 		     out.append("CURRENTPOSTID " + prePostID + "\n");
		 		     CoreferResolve.replacePRP(annotation, out, pipeline);
		 		     break;
		    	}
		    	if(line.split("\t").length < 6) continue;
		    	String currentPostID = line.split("\t")[4];
		    	String currentSent = line.split("\t")[5];
		    	//System.out.println(line);
		    	if(currentPostID.equals(prePostID)){
		    		annotationSents += currentSent + " \n";
		    	} else {
		    		 System.out.println("New annotationSents\n" + annotationSents);
		    		 annotation = new Annotation(annotationSents);  
		    		 pipeline.annotate(annotation);	    
		 		     System.out.println("Post " + prePostID + " annotation done!");
		 		     out.append("CURRENTPOSTID " + prePostID + "\n");
		 		     CoreferResolve.replacePRP(annotation, out, pipeline);
		 		     annotationSents = "";
		 		     prePostID = currentPostID;
		 		     annotationSents += currentSent + " \n";     
		    	}	
		    }
		    out.close();
		    return allInforFileName + "_repPRP";
		   // Annotation annotation = new Annotation(annotationSents);   
		    
		    
		    /*Annotation annotation = new Annotation("Your answers are that of a typical blind Obama sheep . "
	    		  					+" He will be the worst President . "
	    		  					+" He will not keep his promise about tax cuts for 95 % of Americans . "
	    		  					+" His HCR bill is proof of that . "
	    		  					+" He wants the VAT for Christ 's sake . "
	    		  					+" What do you think that is ? "
	    		  					+" They have tied the hands of local law enforcement agencies in seeking illegal aliens ." 
	    		  					+" His DOHS has stopped enforcing immigration laws . "
	    		  					+" He has cut border agents . "
	    		  					+" He has weakened our border security , thus , or national security . "
	    		  					+" Cap & Tax is next , then amnesty . "
	    		  					+" Destructive for America . "
	    		  					+" None of what I said are lies . "
	    		  					+" You will see . ");*/    
		   
	 }	  
}
