package com.yelp_1;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import mulan.classifier.lazy.MLkNN;
import mulan.classifier.lazy.IBLR_ML;
import mulan.classifier.meta.RAkEL;
import mulan.classifier.neural.BPMLL;
import mulan.classifier.transformation.BinaryRelevance;
import mulan.classifier.transformation.ClassifierChain;
import mulan.classifier.transformation.LabelPowerset;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.Evaluation;
import mulan.evaluation.Evaluator;
import mulan.evaluation.MultipleEvaluation;

public class MulanTest {

	public static void main(String[] args) throws Exception {

		String arffFile_train = "./output/train.arff";
		String xmlFile_train = "./output/categories.xml";
		String arffFile_test = "./output/test.arff";
		String xmlFile_test = "./output/categories.xml";
		MultiLabelInstances data_train = null;
		MultiLabelInstances data_test = null;
		data_train = new MultiLabelInstances(arffFile_train, xmlFile_train);
		data_test = new MultiLabelInstances(arffFile_test, xmlFile_test);
		// RAkEL learner1 = new RAkEL(new LabelPowerset(new J48()));

		Evaluator eval = new Evaluator();
		Evaluation results;
		Classifier brClassifier = new NaiveBayes();
		// Binary Relevance method
		BinaryRelevance br = new BinaryRelevance(brClassifier);
		br.setDebug(true);
		br.build(data_train);
		results = eval.evaluate(br, data_test);
		System.out.println(results);

		// classifier chains
		ClassifierChain cc = new ClassifierChain(brClassifier);
		cc.setDebug(true);
		cc.build(data_train);
		results = eval.evaluate(cc, data_test);
		System.out.println(results);

		// Random k labelsets
		LabelPowerset lp = new LabelPowerset(brClassifier);
		RAkEL rakel = new RAkEL(lp);
		rakel.setDebug(true);
		rakel.build(data_train);
		results = eval.evaluate(rakel, data_test);
		System.out.println(results);

		// KNN
		MLkNN mlknn = new MLkNN();
		mlknn.setDebug(true);
		mlknn.build(data_train);
		results = eval.evaluate(mlknn, data_test);
		System.out.println(results);

		// logistic regression
		IBLR_ML iblr_ML = new IBLR_ML();
		iblr_ML.setDebug(true);
		iblr_ML.build(data_train);
		results = eval.evaluate(iblr_ML, data_test);
		System.out.println(results);

	}
}