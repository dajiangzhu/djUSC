package edu.usc.ini.igc.conference.ISBI2014;

import java.util.List;
import java.util.Random;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.extensions.MatlabTypeConverter;
import edu.uga.DICCCOL.DicccolUtil;
import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.liulab.djVtkBase.djVtkFiberData;
import edu.uga.liulab.djVtkBase.djVtkPoint;
import edu.uga.liulab.djVtkBase.djVtkSurData;
import edu.usc.ini.igc.conference.IPMI2014.TraceMapService;

public class test {
	//this is a test
	//this ia test too
	public void testRandomNum()
	{
		for (int i = 0; i < 10; i++) {
			System.out.println("****************");
			List<Integer> randomNumberList = DicccolUtil.geneRandom(1, 2);
			System.out.println(randomNumberList);
			boolean flag = true;
			for(int j=1;j<=261;j++)
				if(!randomNumberList.contains(j))
				{
					flag = false;
					System.out.println("No: "+ (j+1));
				}
			System.out.println(flag);
				
		}
	}
	
	public double[][] chang2DArray(double[] oriArray, int dim) {
		double[][] changedArray = new double[dim][1];
		for (int i = 0; i < dim; i++)
			changedArray[i][0] = oriArray[i];
		return changedArray;
	}
	
	public void testMatlab() throws MatlabConnectionException, MatlabInvocationException
	{
		// TTestImpl ttest = new TTestImpl();
		// Create a proxy, which we will use to control MATLAB
		double[] arr1 = {0.1,0.2,0.3,0.4,0.5,0.11,0.22,0.33,0.44,0.55};
		double[] arr2 = {1.1,1.2,1.3,1.4,1.5,1.11,1.22,1.33,1.44,1.55};
		MatlabProxyFactory factory = new MatlabProxyFactory();
		MatlabProxy proxy = factory.getProxy();
		MatlabTypeConverter processor = new MatlabTypeConverter(proxy);
		processor.setNumericArray("a_m", new MatlabNumericArray(
				this.chang2DArray(arr1, 10), null));
		processor.setNumericArray("a_s",
				new MatlabNumericArray(this.chang2DArray(arr2, 10), null));
		proxy.eval("[h p] = ttest2(a_m,a_s,0.05,'right','unequal')");
		double pValueTmp = ((double[]) proxy.getVariable("p"))[0];
		System.out.println("pValue: "+pValueTmp);
	}
	
	public void testRandom2()
	{
//		Random rn = new Random();
//		for(int i=0;i<10;i++)
//			System.out.println(rn.nextDouble());	
		double a=5;
		double b=Math.pow(a, 3);
		System.out.println("b: "+b);
	}
	
	public void testTraceMapService()
	{
		String fiberBundleName = "8003201_RC.dicccol.12.Fiber.vtk";
		TraceMapService ts = new TraceMapService();
		djVtkFiberData fiberBuldleData = new djVtkFiberData(fiberBundleName);
		//***********************************************
//		ts.fiberBuldleData = fiberBuldleData;
//		djVtkPoint iniPoint = fiberBuldleData.getPoint(0);
//		ts.initialPt = iniPoint;
//		ts.initialization();
//		ts.traceMapMName = fiberBundleName+"_traceMapM.txt";
//		ts.fiberEncoding();	
		
		//***********************************************
		String traceMapMFile = "8003201_RC.dicccol.12.Fiber.vtk_traceMapM.txt";
		double[][] traceMapM = DicccolUtilIO.loadFileAsArray(traceMapMFile, 162*20, 363);
		ts.traceMapM = traceMapM;
		djVtkSurData startPoints = new djVtkSurData("8003201_RC.dicccol.12.Fiber.vtk_traceMapM.txt_startPoints.vtk");
		ts.startPointList = startPoints.points;
		ts.initialization();
		ts.traceMapMRowNu = 162*20;
		ts.traceMapColNum = 363;
		ts.outputFiberName=fiberBundleName+"_recon.vtk";
		ts.fiberDecoding();
	}

	public static void main(String[] args) throws MatlabConnectionException, MatlabInvocationException {
		test mainHandler = new test();
//		mainHandler.testRandomNum();
//		mainHandler.testMatlab();
		mainHandler.testRandom2();
//		mainHandler.testTraceMapService();


	}

}
