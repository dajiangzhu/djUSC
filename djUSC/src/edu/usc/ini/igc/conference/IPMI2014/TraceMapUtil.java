package edu.usc.ini.igc.conference.IPMI2014;

import java.util.ArrayList;
import java.util.List;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.fiberBundleService;
import edu.uga.liulab.djVtkBase.djVtkPoint;
import edu.uga.liulab.djVtkBase.djVtkUtil;

public class TraceMapUtil {
	
	public static final double ANGLE_STEP = Math.PI / 9.0;
	List<djVtkPoint> TraceMapSamplePointList = new ArrayList<djVtkPoint>();
	
	public void createTraceMapSamplePoints()
	{
		double angleTheta = 0.0;
		double anglePhi = 0.0;
		for (int i = 0; i < 9; i++) {
			angleTheta = TraceMapUtil.ANGLE_STEP * i;
			for (int j = 0; j < 18; j++) {
				anglePhi = TraceMapUtil.ANGLE_STEP * j;
				djVtkPoint samplePoint = new djVtkPoint();
				samplePoint.x = (float) (Math.cos(angleTheta) * Math.cos(anglePhi));
				samplePoint.y = (float) (Math.sin(angleTheta) * Math.cos(anglePhi));
				samplePoint.z = (float) (Math.sin(anglePhi));
				TraceMapSamplePointList.add(samplePoint);
			} //for j
		} //for i
		DicccolUtilIO.writeToPointsVtkFile("TraceMapSamplePoints.vtk", TraceMapSamplePointList);
	}
	
	public void checkReproducbility()
	{
		for(int i=0;i<TraceMapSamplePointList.size()-1;i++)
			for(int j=i+1;j<TraceMapSamplePointList.size();j++)
			{
				djVtkPoint pt1 = TraceMapSamplePointList.get(i);
				djVtkPoint pt2 = TraceMapSamplePointList.get(j);
//				if(pt1.x==pt2.x && pt1.y==pt2.y && pt1.z==pt2.z)
				if(djVtkUtil.calDistanceOfPoints(pt1, pt2)<0.001)
					System.out.println(i+"="+j);
			}
	}

	public static void main(String[] args) {
		TraceMapUtil mainHandler = new TraceMapUtil();
		mainHandler.createTraceMapSamplePoints();
		mainHandler.checkReproducbility();

	}

}
