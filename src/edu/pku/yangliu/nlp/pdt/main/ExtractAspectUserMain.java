package edu.pku.yangliu.nlp.pdt.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.InvalidFormatException;
import edu.pku.yangliu.nlp.pdt.common.FileUtil;
import edu.pku.yangliu.nlp.pdt.common.Stopwords;
import edu.pku.yangliu.nlp.pdt.config.DataPathConfig;
import edu.pku.yangliu.nlp.pdt.lemma.Lemma;
import edu.pku.yangliu.nlp.pdt.lemma.POSMap;
import edu.pku.yangliu.nlp.pdt.parser.ShallowParser;
import edu.pku.yangliu.nlp.pdt.parser.StanfordNER;
import edu.pku.yangliu.nlp.pdt.parser.StanfordTokenizer;
import edu.pku.yangliu.nlp.pdt.stem.Porter;

/**Class for extraction of aspects and users in the thread posts
 * @author YANG Liu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yang.liu@pku.edu.cn
 */
public class ExtractAspectUserMain {
	
	/**Generate all information file.
	 * The format of all information file is as follows
	 * SentID/t sender /t receiver/t threadID /t postID /t sentence
	 * @param corpusPath
	 * @throws IOException 
	 */
	private void genAllInforFile(String corpusPath) throws IOException {
		// TODO Auto-generated method stub	
		File[] corpusFilesFolders = new File(corpusPath).listFiles();
		for(File corpusFilesFolder : corpusFilesFolders){
			System.out.println("corpusFilesFolder path" + corpusFilesFolder.getCanonicalPath());
			for(File corpusFile : corpusFilesFolder.listFiles()){
				System.out.println("corpusFile path" + corpusFile.getCanonicalPath());
				if(corpusFile.getName().contains("sent")){
					String allInforFileName = corpusFilesFolder.getCanonicalPath() + "/"+ corpusFilesFolder.getName() + "_allInf";
					ArrayList<String> sentLines = new ArrayList<String>();
					ArrayList<String> allInforLines = new ArrayList<String>();
					FileUtil.readLines(corpusFile.getAbsolutePath(), sentLines);
					int sentID = 1;
					for(String sentLine : sentLines){
						String allInforLine = sentID + "\t" + sentLine;
						allInforLines.add(allInforLine);
						sentID++;
					}
					FileUtil.writeLines(allInforFileName, allInforLines);
				}
			}
		}
	}
	
	/**Extract NP which contain more than two tokens
	 * @param formatRepPRPFileName
	 * @param allInforFileName
	 * @return String
	 * @throws IOException 
	 */
	private String replaceNP(String formatRepPRPFileName, String allInforFileName) throws IOException {
		// TODO Auto-generated method stub
		String opinionWordsPath = DataPathConfig.opinionWordsFilePath;
		String sentsReplaceNPPRPFileName = formatRepPRPFileName + "_repNP";
		FileWriter sentsReplaceNPPRPFileWriter = new FileWriter(sentsReplaceNPPRPFileName);
		ArrayList<String> fmtLines = new ArrayList<String>();//sentID /t postID /t sent
		ArrayList<String> opinionWords = new ArrayList<String>();
	    FileUtil.readLines(formatRepPRPFileName, fmtLines);	
	    for(File opinionFile : new File(opinionWordsPath).listFiles()){
	    	FileUtil.readLines(opinionFile.getAbsolutePath(), opinionWords);
	    }
	    Integer sentIDCounter;
	    ShallowParser swParser = ShallowParser.getInstance();
		for(String fmtline : fmtLines){
			if(!fmtline.isEmpty()){
			String [] tokens = fmtline.split("\t");
			sentIDCounter = Integer.valueOf(tokens[0]);	
			System.out.println("SentID Counter :" + sentIDCounter);
			Map<Integer,String> phLablePostagMap = new TreeMap<Integer,String>();
			phLablePostagMap = swParser.chunk(tokens[2].trim());
			String[] sentWords = WhitespaceTokenizer.INSTANCE.tokenize(tokens[2].trim());
			Map<Integer, Vector<Integer>> phraseMap = new TreeMap<Integer, Vector<Integer>>();
			//phraseMap  Key: index of phrase  Value: index of the words in the phrase
			for(Integer sentWordID : phLablePostagMap.keySet()){
				String phLable = phLablePostagMap.get(sentWordID).split("-")[0];
				Pattern p = Pattern.compile("[0-9]+"); 
				Matcher m = p.matcher(phLable); 
				m.find();
				Integer phLableIndexInSent = Integer.valueOf(m.group(0));
				if(phraseMap.containsKey(phLableIndexInSent)){
					Vector<Integer> phraseWordsIndex = phraseMap.get(phLableIndexInSent);
					phraseWordsIndex.add(sentWordID);
					phraseMap.put(phLableIndexInSent, phraseWordsIndex);
				} else {
					Vector<Integer> phraseWordsIndex = new Vector<Integer>();
					phraseWordsIndex.add(sentWordID);
					phraseMap.put(phLableIndexInSent, phraseWordsIndex);
				}
			}
			String sentsReplaceNP = "";
			for(Integer phLableIndex : phraseMap.keySet()){
				Vector<Integer> phraseWordsIndex = phraseMap.get(phLableIndex);
				String currentPhraseLable = phLablePostagMap.get(phraseWordsIndex.get(0)).split("-")[0];
				if(currentPhraseLable.contains("NP")){
					//!!!delete stop words in NP firstly
					Vector<Integer> phraseWordsIndexExceptStopWords = new Vector<Integer>();
					for(Integer wordIndex : phraseWordsIndex){
						if(!Stopwords.isStopword(sentWords[wordIndex].toLowerCase())){
							phraseWordsIndexExceptStopWords.add(wordIndex);	
						}
					}
					
					if(phraseWordsIndexExceptStopWords.size() >= 2){
						//The words in aspects should
						//1 not contained by opinionWords
						//2 the POS tag should only be NNS/NN/NNP/JJ
						boolean aspectFlag = false;
						for(int i = 0; i < phraseWordsIndexExceptStopWords.size() - 1; i++){
							String posTag = phLablePostagMap.get(phraseWordsIndexExceptStopWords.get(i)).split("-")[1];
							//System.out.println("NP sentWords: " + sentWords[phraseWordsIndexExceptStopWords.get(i)] + "\tNP posTag: " + posTag);
							if((!opinionWords.contains(sentWords[phraseWordsIndexExceptStopWords.get(i)].toLowerCase()))
							   && (posTag.equals("NN") || posTag.equals("NNS") ||posTag.equals("NNP") || posTag.equals("JJ"))){
								if(!aspectFlag) {
									sentsReplaceNP += "ASPECT_";
									aspectFlag = true;
								}
								sentsReplaceNP += sentWords[phraseWordsIndexExceptStopWords.get(i)].toUpperCase() + "_";		
							} else {
								sentsReplaceNP += sentWords[phraseWordsIndexExceptStopWords.get(i)] + " ";
								aspectFlag = false;
							}
						}
						//Assumption: the last word in NP is NNS/NN/NNP
						Integer lastWordIndex = phraseWordsIndexExceptStopWords.size() - 1;
						String lastWord = sentWords[phraseWordsIndexExceptStopWords.get(lastWordIndex)];
						if(!aspectFlag) {
							sentsReplaceNP += "ASPECT_";
							aspectFlag = true;
						}
						sentsReplaceNP += lastWord.toUpperCase() + " ";	
					} else {
						if(phraseWordsIndexExceptStopWords.size() != 0){
							sentsReplaceNP += sentWords[phraseWordsIndexExceptStopWords.get(0)] + " ";
							}
						}
				} else {
					for(Integer wordIndex : phraseWordsIndex){
						sentsReplaceNP += sentWords[wordIndex] + " ";
					}
				}
			}
			
			sentsReplaceNPPRPFileWriter.append(sentIDCounter + "\t" + tokens[1] + "\t" + sentsReplaceNP.trim() + "\n");
			}
		}
		sentsReplaceNPPRPFileWriter.flush();
		return sentsReplaceNPPRPFileName;
	}
	
	/**Generate a Replace RPR file in a more readable format
	 * @param replacePRPFileName
	 * @return String
	 * @throws IOException 
	 */
	private String genFormatRepRPRFile(String replacePRPFileName) {
		// TODO Auto-generated method stub
		String formatRepRPRFileName = replacePRPFileName + "_Format";
		ArrayList<String> oldFileLines = new ArrayList<String>();
		ArrayList<String> newFileLines = new ArrayList<String>();
		FileUtil.readLines(replacePRPFileName, oldFileLines);
		String newLine = "";
		Integer currentRepRPRSentID = 0;
		String currentPostID = oldFileLines.get(0).split(" ")[1];
		for(String oldLine : oldFileLines){
			if(oldLine.contains("CURRENTPOSTID")){
				currentPostID = oldLine.split(" ")[1];
				continue;
			} else {
				currentRepRPRSentID++;
				if(oldLine.split(" ").length <= 2) continue;// Delete sentences which contain only one or two tokens
				else {
					newLine = currentRepRPRSentID + "\t" + currentPostID + "\t" + oldLine;
					newFileLines.add(newLine);
				}
			}
		}
		FileUtil.writeLines(formatRepRPRFileName, newFileLines);
		return formatRepRPRFileName;
	}

	/**Cut NP by three ways
	 * If the number of the senders of the NP is 
	 * less than 2, we cut it
	 * @param replacePRPNPFileName
	 * @param allInforFileName
	 * @param CutType 
	 * @throws MalformedURLException 
	 * @throws IOException 
	 */
	private String cutNP(String replacePRPNPFileName,
			String allInforFileName, int minSenderNum, String CutType) throws MalformedURLException {
		// TODO Auto-generated method stub
		String wordnetPath = "lib/dict/";
		new POSMap();
		new Lemma(wordnetPath);
		String replacePRPNPCutFileName = replacePRPNPFileName + "_" + CutType + "_minS" + minSenderNum;
		ArrayList<String> allInforLines = new ArrayList<String>();
		ArrayList<String> replacePRPNPLines = new ArrayList<String>();
		ArrayList<String> replacePRPNPCutLines = new ArrayList<String>();
		FileUtil.readLines(allInforFileName, allInforLines);
		FileUtil.readLines(replacePRPNPFileName, replacePRPNPLines);	
		Map<String, String> postIDSenderMap = getPostIDSenderMapByAllInforLines(allInforLines);
		Map<String, Set<String>> NPSenderMap =getNPSenderMap(replacePRPNPLines, postIDSenderMap);	
		for(String replacePRPNPLine : replacePRPNPLines){
			String [] replacePRPNPTokens =  replacePRPNPLine.split("\t");
			if(replacePRPNPTokens.length < 3) continue;
			String [] words = replacePRPNPTokens[2].split(" ");
			String replacePRPNPCutLine = replacePRPNPTokens[0] + "\t" + replacePRPNPTokens[1] + "\t";
			for(String word : words){
				if(word.contains("ASPECT_")){
					//find NP!
					if(NPSenderMap.get(word)== null 
					  || (NPSenderMap.get(word).size() < minSenderNum && !word.toLowerCase().contains("obama"))
					  || isNeedCut(word, CutType)){
						String[] wordParts = word.split("_");
						for(int i = 1; i < wordParts.length; i++){
							replacePRPNPCutLine += wordParts[i].toLowerCase() + " ";
						}
					} else {
						//Delete stop words in NP
						//Transfer to lemma
						String[] wordParts = word.split("_");
						replacePRPNPCutLine += "ASPECT_";
						for(int i = 1; i < wordParts.length - 1; i++){
							if(!Stopwords.isStopword(wordParts[i].toLowerCase())){
								String lemmaWord = Lemma.getLemma(wordParts[i].toLowerCase(), "NNS");
								//System.out.println("lemma: " + lemmaWord);
								replacePRPNPCutLine += lemmaWord.toUpperCase() + "_";
							}
						} if(!Stopwords.isStopword(wordParts[wordParts.length - 1].toLowerCase())){
							String lemmaWord = Lemma.getLemma(wordParts[wordParts.length - 1].toLowerCase(), "NNS");
							//System.out.println("lemma: " + lemmaWord);
							replacePRPNPCutLine += lemmaWord.toUpperCase() + " ";	
						}				
					}
				} else {
					replacePRPNPCutLine += word + " ";
				}
			}
			replacePRPNPCutLines.add(replacePRPNPCutLine);
		}
		System.out.println("replaceCutLines size " + replacePRPNPCutLines.size());
		FileUtil.writeLines(replacePRPNPCutFileName, replacePRPNPCutLines);
		return replacePRPNPCutFileName;
	}

	private boolean isNeedCut(String word, String cutType) {
		// TODO Auto-generated method stub
		if(word.split("_").length <= 2 && cutType.equals("CutNP")) return true;
		 Pattern p = Pattern.compile("[A-Z_]+"); 
		 Matcher m = p.matcher(word); 
		 m.find();
		 if(m.group(0).length() != word.length()) return true;
		 return false;
	}

	/**Get NP Senders Map from the corpus
	 * @param replacePRPNPLines
	 * @param postIDSenderMap
	 * @return Map<String, Set<String>>
	 */
	private Map<String, Set<String>> getNPSenderMap(
			ArrayList<String> replacePRPNPLines,
			Map<String, String> postIDSenderMap) {
		// TODO Auto-generated method stub
		Map<String, Set<String>> NPSenderMap = new TreeMap<String, Set<String>>();
		for(String replacePRPNPLine : replacePRPNPLines){
			//System.out.println("replacePRPNPLine: " + replacePRPNPLine);
			String [] replacePRPNPTokens =  replacePRPNPLine.split("\t");
			Pattern p = Pattern.compile("[0-9]+"); 
			Matcher m = p.matcher(replacePRPNPTokens[0]); 		
			if(!m.find()) continue;
			Matcher mm = p.matcher(replacePRPNPTokens[1]); 		
			if(!mm.find()) continue;
			//System.out.println("replacePRPNPTokens size :" + replacePRPNPTokens.length);
			String sender = postIDSenderMap.get(replacePRPNPTokens[1]);
			String [] words = replacePRPNPTokens[2].split(" ");
			for(String word : words){
				if(word.contains("ASPECT_")){
					//find NP!
					if(NPSenderMap.containsKey(word)){
						Set<String> senderSet = NPSenderMap.get(word);
						senderSet.add(sender);
						//System.out.println("NP senderSet : " + word + senderSet);
						NPSenderMap.put(word, senderSet);
					} else {
						Set<String> senderSet = new HashSet<String>();
						senderSet.add(sender);
						//System.out.println("NP senderSet : " + word + senderSet);
						NPSenderMap.put(word, senderSet);
					}
				}
			}
		}
		System.out.println("NPSenderMap size " + NPSenderMap.size());
		System.out.println("NPSenderMap:" );
		for(String NP : NPSenderMap.keySet()){
			System.out.println(NP + "\t" + NPSenderMap.get(NP));
		}
		return NPSenderMap;
	}

	/**Get postID and sender Map from the all information file
	 * @param ArrayList<String> allInforLines
	 * @param allInforFileName
	 * @return Map<String, String>
	 */
	public static Map<String, String> getPostIDSenderMapByAllInforLines(
			ArrayList<String> allInforLines) {
		// TODO Auto-generated method stub
		Map<String, String> postIDSenderMap = new HashMap<String, String>();
		for(String allInforLine : allInforLines){
			String[] tokens = allInforLine.split("\t");
			postIDSenderMap.put(tokens[5], tokens[2]);
		}
		return postIDSenderMap;
	}
	
	/**Generate Aspect User file
	 * @param replacePRPNPCutNERFileName
	 * @param allInforFileName
	 * @param cutNERMinSenderNum 
	 * @return String
	 * @throws IOException 
	 */
	private String genAspectUsersFile(String replacePRPNPCutNERFileName,
			String allInforFileName, int cutNERMinSenderNum) throws IOException {
		// TODO Auto-generated method stub
		String AspectUsersFileName = replacePRPNPCutNERFileName + "_AspectUsers_minS" + cutNERMinSenderNum;
		//String AspectWordsFileName = replacePRPNPCutNERFileName + "_AspectWords";
		ArrayList<String> allInforLines = new ArrayList<String>();
		ArrayList<String> replacePRPNPLines = new ArrayList<String>();
		FileUtil.readLines(allInforFileName, allInforLines);
		FileUtil.readLines(replacePRPNPCutNERFileName, replacePRPNPLines);	
		Map<String, String> postIDSenderMap = getPostIDSenderMapByAllInforLines(allInforLines);
		Map<String, Set<String>> NPSenderMap = getNPSenderMap(replacePRPNPLines, postIDSenderMap);			
		FileWriter AspectUsersFileWriter = new FileWriter(AspectUsersFileName);
		for(String NP : NPSenderMap.keySet()){
				AspectUsersFileWriter.append(NP + "\t" + NPSenderMap.get(NP) + "\n");
				AspectUsersFileWriter.flush();	
		}	
		
		System.out.println("AspectUsersMap size = " + NPSenderMap.size());
		return AspectUsersFileName;
	}

	/**Replace users in the corpus with ASPECT_UPPERCASE
	 * @param allInforFileName
	 * @return Set<String>
	 */
	private void replaceUser(String allInforFileName, Set<String> users, String extractUserFileName) {
		// TODO Auto-generated method stub
		ArrayList<String> extractUserFileLines = new ArrayList<String>();
		ArrayList<String> allInforLines = new ArrayList<String>();
		FileUtil.readLines(allInforFileName, allInforLines);
		for(String allInforLine : allInforLines){
			String[] allInforLineTokens = allInforLine.split("\t");
			//System.out.println(allInforLineTokens[6]);
			String extactUserFileLine = allInforLineTokens[0] + "\t" + allInforLineTokens[5] + "\t"; 
			String[] words = allInforLineTokens[6].split(" ");
			if(words.length == 0) continue;
			//Replace userID
			/*for(String user : users){
				String [] userWords = user.split(" ");	
				for(int i = 0; i < words.length - userWords.length + 1; i++){
					String currentEQLUserWords = words[i];
					for(int j = i+1; j < i + userWords.length; j++){
						currentEQLUserWords += " " + words[j];
					}
					if(user.trim().equals(currentEQLUserWords)){
						String newAspectUser = "ASPECT_";
						for(String userToken : userWords){
							newAspectUser += userToken.toUpperCase() + "_";
						}
						allInforLineTokens[6] = allInforLineTokens[6].replace(user.trim(), newAspectUser);
					}
				}
			}*/
			//Replace "you";
			String[] newWords = allInforLineTokens[6].split(" ");
			for(int i = 0; i < newWords.length; i++){
				String word = newWords[i];
				if(word.trim().equals("you") || word.trim().equals("your") || word.trim().equals("yours")
				   || word.trim().equals("You") || word.trim().equals("Your") || word.trim().equals("Yours")
				   || word.trim().equals("YOU") || word.trim().equals("Yourself") || word.trim().equals("yourself")
				   || word.trim().equals("YOUR")){
					//System.out.println("!find " + word);
					String[] receiverTokens = allInforLineTokens[3].split(" ");
					String newAspectUser = "ASPECT_";
					for(String receiverToke : receiverTokens){
						newAspectUser += receiverToke.toUpperCase() + "_";
					}
					newWords[i] = newAspectUser;	
				}	
			}
			allInforLineTokens[6] = "";
			for(String word : newWords){
				allInforLineTokens[6] += word + " ";
			}
			
			extactUserFileLine += allInforLineTokens[6];
			extractUserFileLines.add(extactUserFileLine);
		}
		FileUtil.writeLines(extractUserFileName, extractUserFileLines);
	}

	public static boolean isNumeric(String str){ 
	    Pattern pattern = Pattern.compile("[0-9]*"); 
	    return pattern.matcher(str).matches();    
	 } 
	
	/**Get users from the all information file
	 * @param allInforFileName
	 * @return Set<String>
	 * @throws IOException 
	 */
	private Set<String> getUsers(String allInforFileName) {
		// TODO Auto-generated method stub
		Set<String> users = new TreeSet<String>();
		ArrayList<String> allInforLines = new ArrayList<String>();
		FileUtil.readLines(allInforFileName, allInforLines);
		for(String allInforLine : allInforLines){
			String[] allInforLineTokens = allInforLine.split("\t");
			users.add(allInforLineTokens[2].trim());
			users.add(allInforLineTokens[3].trim());
		}
		users.add("Johnny");
		users.add("Wolf");
		users.add("Albert");
		users.add("Salvo");
		users.add("Badgered");
		users.add("Hubbard");
		users.add("CRIMSON");
		users.add("MASK");
		users.add("Divine");
		users.add("Righteous");
		users.add("Jason");
		users.add("Shurnas");
		users.add("Joe");
		users.add("Wales");
		users.add("Josey");
		users.add("Justin");
		users.add("KGB");
		users.add("Nehemiah");
		users.add("Scudder");
		users.add("Ostap");
		users.add("Bump");
		users.add("Peabody");
		users.add("Raymond");
		users.add("James");
		users.add("Saxon");
		users.add("Caucasus");
		users.add("Hobbes");
		users.add("Mello");
		users.add("Yosh");
		users.add("Shmenge");
		users.add("Ziggy");
		users.add("Stardust");
		users.add("Dan");
		System.out.println("users size" + users.size());
		for(String user : users){
			System.out.println("user:" + user);
		}
		return users;
	}	
	
	private String genAllinforTokenFile(String formatRepPRPFileName,
			String allInforFileName) {
		// TODO Auto-generated method stub
		Map<String, String> postIDSRTIDMap = new HashMap<String, String>();
		ArrayList<String> allInforLines = new ArrayList<String>();
		ArrayList<String> formatLines = new ArrayList<String>();
		ArrayList<String> allInforTokenLines = new ArrayList<String>();
		String allInforTokenFileName = formatRepPRPFileName + "_Token";
		FileUtil.readLines(allInforFileName, allInforLines);
		FileUtil.readLines(formatRepPRPFileName, formatLines);
		for(String allInforLine : allInforLines){
			String[] aLTokens = allInforLine.split("\t");
			postIDSRTIDMap.put(aLTokens[4], aLTokens[1] + "\t" + aLTokens[2] + "\t" + aLTokens[3]);
		}
		for(String formatLine : formatLines){
			String [] formatLineTokens = formatLine.split("\t");
			String allInforTokenLine = formatLineTokens[0] + "\t" + formatLineTokens[0] + "\t"+ postIDSRTIDMap.get(formatLineTokens[1]) + "\t" +formatLineTokens[1] + "\t" +formatLineTokens[2];
			allInforTokenLines.add(allInforTokenLine);			
		}
		FileUtil.writeLines(allInforTokenFileName, allInforTokenLines);
		return allInforTokenFileName;
	}

	/**Just for testing
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String corpusPath = DataPathConfig.corpusPath;
		ExtractAspectUserMain extractTarget = new ExtractAspectUserMain();
		
		//1 Generate all information file. Add sentID for every sentence
		//  Notice: Tokenizer is contained in Stanford CoreNLP for coreference resolve. So we didn't tokenize sentences here
		extractTarget.genAllInforFile(corpusPath);
		File[] corpusFilesFolders = new File(corpusPath).listFiles();
		for(File corpusFilesFolder : corpusFilesFolders){
			String featureVecPath = corpusFilesFolder.getCanonicalPath() + "/";
			//String allInforTokenFileName = featureVecPath + corpusFilesFolder.getName() + "_allInf_repPRP_Format_Token";
			String allInforFileName = corpusFilesFolder.getCanonicalPath() + "/"+ corpusFilesFolder.getName() + "_allInf";

			//2 Tokenize and Coreference resolve
			String replacePRPFileName = CoreferResolve.coreferResolve(allInforFileName);
			
			//3 Format the result file in Step 3 and use the tokenized sentences to generate an all information token file
			String formatRepPRPFileName = extractTarget.genFormatRepRPRFile(replacePRPFileName);
			String allInforTokenFileName = extractTarget.genAllinforTokenFile(formatRepPRPFileName, allInforFileName);
			
			//4 Replace NP
			String replacePRPNPFileName = extractTarget.replaceNP(formatRepPRPFileName, allInforTokenFileName);
			
			//5 Cut NP
			int minCut = 2;
			String replacePRPNPCutNPFileName = extractTarget.cutNP(replacePRPNPFileName, allInforTokenFileName, minCut, "CutNP");
			extractTarget.genAspectUsersFile(replacePRPNPCutNPFileName, allInforTokenFileName, minCut);
			
			//6 NER
			String replacePRPNPCutNPNERFileName = StanfordNER.ner(replacePRPNPCutNPFileName);
			
			//7 Cut NER
			String replacePRPNPCutNPNERCutNERFileName = extractTarget.cutNP(replacePRPNPCutNPNERFileName, allInforTokenFileName, minCut, "CutNER");
			extractTarget.genAspectUsersFile(replacePRPNPCutNPNERCutNERFileName, allInforTokenFileName, minCut);	
			//String replacePRPNPCutNPNERCutNERFileName = featureVecPath + corpusFilesFolder.getName() + "_allInf_repPRP_Format_repNP_CutNP_minS2_NER_CutNER_minS2";
			//8 Extract User Aspect Signed Network
			ExtractRelations.extractRel(new File(replacePRPNPCutNPNERCutNERFileName), allInforTokenFileName, "UserAspect", featureVecPath, minCut);
			
			//9 Replace "you" etc. to ASPECT_USERID
			/*minCut = 1;
			String extractUserFileName = featureVecPath + corpusFilesFolder.getName() + "_User";
			Set<String> users = extractTarget.getUsers(allInforTokenFileName);
			extractTarget.replaceUser(allInforTokenFileName, users, extractUserFileName);
			extractTarget.genAspectUsersFile(extractUserFileName, allInforTokenFileName, minCut);
			
			//10 Extract User User Signed Network
			ExtractRelations.extractRel(new File(extractUserFileName), allInforTokenFileName, "UserUser", featureVecPath, minCut);*/
		}			
	}
}

	
