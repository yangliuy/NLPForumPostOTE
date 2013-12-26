NLPForumPostOTE
===============

/**
Copyright (C) 2013 by
SMU Text Mining Group/Singapore Management University/Peking University

NLPForumPostOTE is distributed for research purpose, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

If you use this code, please cite the following paper:

Minghui Qiu, Liu Yang and Jing Jiang. Mining User Relations from Online Discussions using Sentiment Analysis and Probabilistic Matrix Factorization.In Proceedings of the 2013 Conference of North American Chapter of Association for Computational Linguistics: Human Language Technologies (NAACL 2013). (http://aclweb.org/anthology//N/N13/N13-1041.pdf)

Feel free to contact the following people if you find any
problems in the package.
yang.liu@pku.edu.cn * */

Brief Introduction
===================

1. Advances in sentiment analysis have enabled extraction of user relations implied in online textual exchanges such as forum posts. However, recent studies in this direction only consider direct relation extraction from text. As user interactions can be sparse in online discussions, we propose to apply collaborative filtering through probabilistic matrix factorization to generalize and improve the opinion matrices extracted from forum posts.

2. This package implements the construction of opinion matrices which are the input of PMF model. The main features include aspect identification, opinion expression identification and opinion relation extraction based on dependency path rules.

3. The dependency path rules are extracted from the lowest common ancestor paths between opinion nodes and target nodes. For details, please refer to FindLCAPath() function in edu.pku.yangliu.nlp.pdt.tree.WDTree.java. All directions are from the govern nodes to the dependency nodes. See PreprocessText.preProcess() and PreprocessText.printFeaturesOfTOPhrasePair() for extracting the features of target and opinion phrase pairs in which the FindLCAPath() function has been callled.

4. More details of our methods for aspect identification, opinion identification and opinion relation extraction are described in the following paper:

   Minghui Qiu, Liu Yang and Jing Jiang. Mining User Relations from Online Discussions using Sentiment Analysis and Probabilistic Matrix Factorization.In Proceedings of the 2013 Conference of North American Chapter of Association for Computational Linguistics: Human Language Technologies (NAACL 2013). (http://aclweb.org/anthology//N/N13/N13-1041.pdf)
