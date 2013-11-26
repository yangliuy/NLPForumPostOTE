package edu.pku.yangliu.nlp.pdt.tree;

import java.util.Vector;

/**Class for the node of the Words Dependency Tree 
 * @author YANG Liu
 * @blog http://blog.csdn.net/yangliuy
 * @mail yang.liu@pku.edu.cn
 */

public class WDTreeNode implements Comparable<WDTreeNode>{
	
	private Vector<String> words;//the words of the node. The first one is the head word
	private String posTagForHeadWord;//the POS-Tag of the head word of the node. 
	private String depLinkLable;//the type of word dependency edge linking to the node's parent
	private String phLabel;//the phrase label of the word in the node
	private WDTreeNode parent;// the parent node
	private Vector<WDTreeNode> children; //the children of the node
	private boolean isOpinionLable;// whether it is a candidate opinion word node: 1 means the node is a opinion node
	private boolean isTargetLable;// whether it is a candidate target word node: 1 means the node is a target node
	
	public String getPosTagForHeadWord() {
		return posTagForHeadWord;
	}

	public void setPosTagForHeadWord(String posTagForHeadWord) {
		this.posTagForHeadWord = posTagForHeadWord;
	}
	
	public boolean isTargetLable() {
		return isTargetLable;
	}

	public void setTargetLable(boolean isTargetLable) {
		this.isTargetLable = isTargetLable;
	}

	public Vector<String> getWords() {
		return words;
	}

	public void setWords(Vector<String> words) {
		this.words = words;
	}

	public String getDepLinkLable() {
		return depLinkLable;
	}

	public void setDepLinkLable(String depLinkLable) {
		this.depLinkLable = depLinkLable;
	}

	public String getPhLabel() {
		return phLabel;
	}

	public void setPhLabel(String phLabel) {
		this.phLabel = phLabel;
	}

	public WDTreeNode getParent() {
		return parent;
	}

	public void setParent(WDTreeNode parent) {
		this.parent = parent;
	}

	public Vector<WDTreeNode> getChildren() {
		return children;
	}

	public void setChildren(Vector<WDTreeNode> children) {
		this.children = children;
	}

	public boolean isOpinionLable() {
		return isOpinionLable;
	}

	public void setOpinionLable(boolean isOpinionLable) {
		this.isOpinionLable = isOpinionLable;
	}

	public WDTreeNode(String word) {
		Vector<String> words = new Vector<String>();
		words.add(word);
		this.setWords(words);
		this.setPhLabel("NULL");
	}
	
	/** Add child to the tree node 
	 * @param child The child node that will be added
	 * @return void
	 */
	public void addChild(WDTreeNode child){
		if(this.getChildren() == null) {
			Vector<WDTreeNode> newChildren = new Vector<WDTreeNode> ();
			newChildren.add(child);
			this.setChildren(newChildren);
		} else {
			this.getChildren().add(child);
		}
	}
	
	/** Add more than one children to the tree node 
	 * @param children The child node that will be added
	 * @return void
	 */
	public void addChild(Vector<WDTreeNode> children) {
		// TODO Auto-generated method stub
		if(this.getChildren() == null) {
			this.setChildren(children);
		} else {
			for(WDTreeNode child : children){
				this.getChildren().add(child);
			}
		}
	}
	
	/** Delete one child node
	 * @param child The child node that will be added
	 * @return void
	 */
	public void delChild(WDTreeNode child) {
		// TODO Auto-generated method stub
		if(this.getChildren() == null) {
			System.out.print("There is no child");
		} else {
			for(WDTreeNode oneChild : children){
				if(oneChild.getWords().get(0).equals(child.getWords().get(0))) {
					this.getChildren().remove(child);
					break;
				}
					
			}
		}	
	}
	
	/** Print the words of children nodes
	 * @param void
	 * @return void
	 */
	public void printChildrenNodeWord() {
		Vector<WDTreeNode> children = this.getChildren();
		if(children.isEmpty()) return;
		System.out.print("children of ");
		for(String word : this.getWords()){
			System.out.print(word+"_");
		}
		System.out.println(" (phLable:"+ this.getPhLabel() +" opinionLable:"+ this.isOpinionLable()+" targetLable:"+ this.isTargetLable()+" postag:"+ this.getPosTagForHeadWord()+"): ");
		for(WDTreeNode child : children){
			for(String word : child.getWords()){
				System.out.print(word + "_");
			}
			System.out.println(" "+ " rel:" + child.getDepLinkLable() + " phLable:" + child.getPhLabel() +" opinionLable:"+ child.isOpinionLable() + " targetLable:" + child.isTargetLable() + " postag:" + child.getPosTagForHeadWord());
		}
		System.out.println();
	}

	@Override
	//Just check whether the two nodes are equal
	public int compareTo(WDTreeNode o) {
		// TODO Auto-generated method stub
		if(this.getWords() == null || o.getWords() == null) {
			System.out.println("The Node contains no word!");
			return 1;
		} else{
			if(this.getWords().get(0).equals(o.getWords().get(0))){
				return 0;
			} else {
				return 1;
			}
		}
	}
}
