package edu.usc.ini.igc.ENIGMA.ml.MDD;

import java.io.File;

import edu.uga.DICCCOL.SVM.SVM_Classifier;

public class J_SVM {
	
	public void runSVM(String dir, String arffFile, double GammaStart,
			double GammaEnd, double CStart, double CEnd) throws Exception {
		System.out
				.println("##################### runSVM... ");
		SVM_Classifier svm_Classifier = new SVM_Classifier();
			System.out.println("Running SVM for: "
					+ arffFile);
			double[] bestPerformance = svm_Classifier.SMO_Classification(
					dir + arffFile,
					GammaStart, GammaEnd, CStart, CEnd);
		System.out
				.println("##################### runSVM finished! ");
	}

	public static void main(String[] args) throws Exception {
		if (args.length != 6) {
			System.out
					.println("Need: Dir(String) arffInputFile(String) GammaStart(0.01) GammaEnd(0.05) CStart(1.0) CEnd(200.0)");
			System.exit(0);
		}
		String dir = args[0].trim();
		String arffInputFile = args[1].trim();

		File f = new File(dir + arffInputFile);
		if (!f.exists() || f.isDirectory()) {
			System.out.println("Shit! " + dir + arffInputFile
					+ " does not exist or it is a directory!");
			System.exit(0);
		}
		double GammaStart = Double.valueOf(args[2].trim());
		double GammaEnd = Double.valueOf(args[3].trim());
		double CStart = Double.valueOf(args[4].trim());
		double CEnd = Double.valueOf(args[5].trim());

		J_SVM mainHandler = new J_SVM();
		mainHandler.runSVM(dir, arffInputFile, GammaStart, GammaEnd, CStart, CEnd);

	}

}
