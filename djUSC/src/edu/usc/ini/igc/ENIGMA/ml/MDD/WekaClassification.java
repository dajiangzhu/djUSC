package edu.usc.ini.igc.ENIGMA.ml.MDD;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.uga.DICCCOL.DicccolUtilIO;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.core.Instances;

public class WekaClassification {

	public void simpleWekaTrain(String filepath) {
		try {
			// Reading training arff or csv file
			FileReader trainreader = new FileReader(filepath);
			Instances train = new Instances(trainreader);
			train.setClassIndex(train.numAttributes() - 1);
			// Instance of NN
			MultilayerPerceptron mlp = new MultilayerPerceptron();
			// Setting Parameters
			mlp.setLearningRate(0.08);
			mlp.setMomentum(0.2);
			mlp.setTrainingTime(500);
			mlp.setHiddenLayers("5");
			mlp.buildClassifier(train);

			Evaluation eval = new Evaluation(train);
			// eval.evaluateModel(mlp, train);
			eval.crossValidateModel(mlp, train, 10, new Random(1));
			
			System.out.println("errorRate: "+eval.errorRate()); // Printing Training Mean root
													// squared Error
			System.out.println(eval.numTrueNegatives(0));
			System.out.println(eval.numTrueNegatives(1));
			System.out.println(eval.numTruePositives(0));
			System.out.println(eval.numTruePositives(1));
			System.out.println("Summary: "+eval.toSummaryString()); // Summary of Training
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void findBestPerformance(String filepath, int layer) throws Exception
	{
		int layerMin = 2;
		int layerMax = 6;
		int layerStep = 1;
		int trainingTimeMin = 200;
		int trainingTimeMax = 1000;
		int trainingTimeStep = 50;
		double momentumMin = 0.05;
		double momentumMax = 0.5;
		double momentumStep = 0.05;
		double learningRateMin = 0.01;
		double learningRateMax = 0.5;
		double learningRateStep = 0.005;

		
		FileReader trainreader = new FileReader(filepath);
		Instances train = new Instances(trainreader);
		train.setClassIndex(train.numAttributes() - 1);
		MultilayerPerceptron mlp = new MultilayerPerceptron();
		
		double bestTP = 100.0;
		double bestTN = 500.0;


		List<String> outputList = new ArrayList<String>();
//		for(int layer = layerMin;layer<=layerMax;layer = layer+layerStep)
			for(int trainingTime = trainingTimeMin;trainingTime<=trainingTimeMax;trainingTime = trainingTime+trainingTimeStep)
				for(double momentum = momentumMin;momentum<=momentumMax;momentum = momentum+momentumStep)
					for(double learningRate = learningRateMin;learningRate<=learningRateMax;learningRate = learningRate+learningRateStep)
					{
						String paras = "*************** Try MLP: (-L "+learningRate+" -M "+momentum+" -N "+trainingTime+" -H "+layer+")";
						System.out.println(paras);
						
						String currentResult = "";
						mlp.setLearningRate(learningRate);
						mlp.setMomentum(momentum);
						mlp.setTrainingTime(trainingTime);
						mlp.setHiddenLayers(String.valueOf(layer));
						mlp.buildClassifier(train);

						Evaluation eval = new Evaluation(train);
						eval.crossValidateModel(mlp, train, 10, new Random(1));
						double currentTP = eval.numTruePositives(1);
						double currentTN = eval.numTrueNegatives(1);

						System.out.println("TP:"+currentTP+"     TN:"+currentTN);
						if(currentTP>bestTP && currentTN>bestTN)
						{
							System.out.println("Good!!!!!!!!!!   TP:"+currentTP+"     TN:"+currentTN);
							bestTP = currentTP;
							bestTN = currentTN;
						}
						currentResult += currentTP+" "+currentTN+" "+"-L "+learningRate+" -M "+momentum+" -N "+trainingTime+" -H "+layer;
						outputList.add(currentResult);
					} //for
		DicccolUtilIO.writeArrayListToFile(outputList, "findBestPerformanceWeka_layer_"+layer+".txt");
	}

	public static void main(String[] args) throws Exception {
		if(args.length!=1)
		{
			System.out.println("Need Layer...");
			System.exit(0);
		}
		WekaClassification mainHandler = new WekaClassification();
		mainHandler.findBestPerformance("DataWekaList_Lasso_ICV.arff", Integer.valueOf(args[0].trim()));

	}

}
