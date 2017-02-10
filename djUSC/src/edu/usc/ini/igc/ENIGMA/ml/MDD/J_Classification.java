package edu.usc.ini.igc.ENIGMA.ml.MDD;

import java.io.FileReader;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.core.Instances;


public class J_Classification {
	
	public void simpleWekaTrain(String filepath) {
		try {
			//haha
			// Reading training arff or csv file
			FileReader trainreader = new FileReader(filepath);
			Instances train = new Instances(trainreader);
			train.setClassIndex(train.numAttributes() - 1);
			
			SMO svmClassifier = new SMO();
		    RBFKernel rbf = new RBFKernel();
		    for(double g=0.01;g<1;g=g+0.01)
		    {
		    	for(double c=0.5;c<200.0;c=c+0.5)
		    	{
		    		System.out.println("#################### Gamma: "+g+"    C: "+c);
				    rbf.setGamma(g);
				    svmClassifier.setKernel(rbf);
				    svmClassifier.setC(c);
				    svmClassifier.buildClassifier(train);

					Evaluation eval = new Evaluation(train);
					eval.crossValidateModel(svmClassifier, train, 10, new Random(1));
					
					double TP0 = eval.numTruePositives(0);
					double FP0 = eval.numFalsePositives(0);
					double TP1 = eval.numTruePositives(1);
					double FP1 = eval.numFalsePositives(1);
		
					System.out.println("All: "+(TP0+TP1)/(TP0+TP1+FP0+FP1)+"("+(TP0+TP1)+"/"+(FP0+FP1)+")         0: "+(TP0/(TP0+FP1))+"("+TP0+"/"+FP1+")     1: "+(TP1/(TP1+FP0))+"("+TP1+"/"+FP0+")");
		    	} //for c
		    } //for g
		    
//		    rbf.setGamma(0.5);
//		    svmClassifier.setKernel(rbf);
//		    svmClassifier.setC(25);
//		    svmClassifier.buildClassifier(train);
//
//			Evaluation eval = new Evaluation(train);
//			eval.crossValidateModel(svmClassifier, train, 10, new Random(1));
//			
//			double TP0 = eval.numTruePositives(0);
//			double FP0 = eval.numFalsePositives(0);
//			double TP1 = eval.numTruePositives(1);
//			double FP1 = eval.numFalsePositives(1);
//
//			System.out.println("All: "+(TP0+TP1)/(TP0+TP1+FP0+FP1)+"%("+(TP0+TP1)+"/"+(FP0+FP1)+")         0: "+(TP0/(TP0+FP1))+"%("+TP0+"/"+FP1+")     1: "+(TP1/(TP1+FP0))+"%("+TP1+"/"+FP0+")");
//			System.out.println("Summary: "+eval.toSummaryString()); // Summary of Training

			

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void Classification_SMO(String filepath, String GammaStart, String GammaEnd) {
		try {

			System.out.println("#################### Arff file: "+filepath);
			double gStart = 0.01*Double.valueOf(GammaStart);
			double gEnd = 0.01*Double.valueOf(GammaEnd);
			System.out.println("#################### GammaStart: "+gStart+"  GammaEnd: "+gEnd);
			FileReader trainreader = new FileReader(filepath);
			Instances train = new Instances(trainreader);
			train.setClassIndex(train.numAttributes() - 1);
			
			SMO svmClassifier = new SMO();
		    RBFKernel rbf = new RBFKernel();
		    for(double g=gStart;g<=gEnd;g=g+0.01)
		    {
		    	for(double c=0.5;c<200.0;c=c+0.5)
		    	{
		    		System.out.println("#################### Gamma: "+g+"    C: "+c);
				    rbf.setGamma(g);
				    svmClassifier.setKernel(rbf);
				    svmClassifier.setC(c);
				    svmClassifier.buildClassifier(train);

					Evaluation eval = new Evaluation(train);
					eval.crossValidateModel(svmClassifier, train, 10, new Random(1));
					
					double TP0 = eval.numTruePositives(0);
					double FP0 = eval.numFalsePositives(0);
					double TP1 = eval.numTruePositives(1);
					double FP1 = eval.numFalsePositives(1);
		
					System.out.println("All:"+(TP0+TP1)/(TP0+TP1+FP0+FP1)+"-("+(TP0+TP1)+"/"+(FP0+FP1)+")         0(Normal):"+(TP0/(TP0+FP1))+"-("+TP0+"/"+FP1+")     1(MDD):"+(TP1/(TP1+FP0))+"-("+TP1+"/"+FP0+")");
		    	} //for c
		    } //for g
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		if(args.length==3)
		{

			J_Classification mainHandler = new J_Classification();
			mainHandler.Classification_SMO(args[0].trim(), args[1].trim(), args[2].trim());
		}
		else
			System.out.println("Need arff file GammaStart and GammaEnd!");
//		J_Classification mainHandler = new J_Classification();
//		mainHandler.simpleWekaTrain("J_LassoInput_B.arff");
		
	}

}
