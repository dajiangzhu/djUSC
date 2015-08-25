package edu.usc.ini.igc.conference.IPMI2014;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.vecmath.Vector3d;

import org.jmat.data.Matrix;

import edu.uga.DICCCOL.DicccolUtilIO;
import edu.uga.DICCCOL.PCA;
import edu.uga.liulab.djVtkBase.djVtkCell;
import edu.uga.liulab.djVtkBase.djVtkFiberData;
import edu.uga.liulab.djVtkBase.djVtkPoint;
import edu.uga.liulab.djVtkBase.djVtkSurData;
import edu.uga.liulab.djVtkBase.djVtkUtil;

public class TraceMapService {

	// *************** TraceMap model parameters
	public static final int FIBER_PTNUM_THRESHOLD = 20;

	// *************** TraceMap sample points
	public djVtkSurData TraceMapSamplePoints = null;

	// *************** Main data structures
	public djVtkPoint initialPt = null; // for fiber encoding
	public List<djVtkPoint> startPointList;
	public djVtkFiberData fiberBuldleData;
	public ArrayList<ArrayList<djVtkPoint>> traceOnSphere;
	public double[][] traceMapM = null;
	public int traceMapMRowNu = -1;
	public int traceMapColNum = -1;

	// *************** Fiber partition parameters
	public int segNum = 20;
	public int pointInterval = 5;// consider 9 points when PCA
	public int samp_num = -1;

	// *************** Input/Output file name
	public String outputFiberName = "";
	public String traceMapMName = "";

	public void initialization() {
		TraceMapSamplePoints = new djVtkSurData("/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/TraceMapSamplePoints_48.vtk");
		this.samp_num = TraceMapSamplePoints.nPointNum;
		if (traceMapM == null) // Encode fibers to tracemap matrix
			if (fiberBuldleData != null && fiberBuldleData.nCellNum != 0)
				traceMapM = new double[segNum * this.samp_num][fiberBuldleData.nCellNum];
			else {
				System.out.println("No fibers when encoding!!!");
				System.exit(0);
			} // else
		else // Decode tracemap matrix to a new fiber
		{
			fiberBuldleData = new djVtkFiberData("/ifs/loni/faculty/thompson/four_d/dzhu/data/MICCAI2015/simplefiber.vtk"); // load a
																		// simple
																		// fiber
																		// for
																		// initialization
			if (startPointList == null || startPointList.size() == 0) {
				startPointList = new ArrayList<djVtkPoint>();
				Random rn = new Random();
				for (int f = 0; f < this.traceMapColNum; f++) {
					djVtkPoint tmPoint = new djVtkPoint();
					tmPoint.x += rn.nextFloat() * 3;
					tmPoint.y += rn.nextFloat() * 3;
					tmPoint.z += rn.nextFloat() * 3;
					startPointList.add(tmPoint);
				}
			} // if
		} // else
	}

	public int findClosestTraceMapSamplePoint(djVtkPoint tracePoint) {
		int theClosestSamplePoint = -1;
		double minDis = 10.0;
		for (int i = 0; i < this.TraceMapSamplePoints.nPointNum; i++) {
			double tmpDis = djVtkUtil.calDistanceOfPoints(tracePoint,
					this.TraceMapSamplePoints.getPoint(i));
			if (tmpDis < minDis) {
				minDis = tmpDis;
				theClosestSamplePoint = i;
			} // if
		} // for
		return theClosestSamplePoint;
	}

	public void updateTraceMapM(djVtkPoint tracePoint, int fiberID, int segID) {
		int theClosestSamplePoint = this
				.findClosestTraceMapSamplePoint(tracePoint);
		for (int i = 0; i < this.samp_num; i++)
			traceMapM[segID * this.samp_num + i][fiberID] = 0.0;
		traceMapM[segID * this.samp_num + theClosestSamplePoint][fiberID] = 1.0;
	}

	public void fiberEncoding() {
		System.out.println("FiberEncoding...");
		startPointList = new ArrayList<djVtkPoint>();
		djVtkCell tmpFiber;
		for (int i = 0; i < this.fiberBuldleData.nCellNum; i++) {
			// System.out.println("dealing with the " + i + "th fiber..");
			ArrayList<djVtkPoint> tmpPointsList = new ArrayList<djVtkPoint>();
			tmpFiber = this.fiberBuldleData.getcell(i);
			if (djVtkUtil.calDistanceOfPoints(initialPt,
					tmpFiber.pointsList.get(0)) < djVtkUtil
					.calDistanceOfPoints(initialPt, tmpFiber.pointsList
							.get(tmpFiber.pointsList.size() - 1))) {
				// System.out.println("---right order!");
				tmpPointsList.addAll(tmpFiber.pointsList);
			} else {
				// System.out.println("!!!false order!");
				for (int k = tmpFiber.pointsList.size() - 1; k >= 0; k--) {
					tmpPointsList.add(tmpFiber.pointsList.get(k));
				}
			}
			startPointList.add(tmpPointsList.get(0));
			if (tmpFiber.pointsList.size() < TraceMapService.FIBER_PTNUM_THRESHOLD) {
				traceOnSphere.add(new ArrayList<djVtkPoint>());
			} else {
//				int segNum = tmpFiber.pointsList.size() / this.pointInterval;
				int segID = 0;
				for (int j = this.pointInterval; j < tmpFiber.pointsList.size()
						- this.pointInterval; j = j + this.pointInterval) {
					djVtkPoint tmpPoint = new djVtkPoint();
					int tmpStart = j - this.pointInterval;
					int tmpEnd = j + this.pointInterval;
					djVtkPoint pt1 = tmpPointsList.get(tmpStart);
					djVtkPoint pt2 = tmpPointsList.get(tmpEnd);
					Vector3d vRefDirection = new Vector3d(pt2.x - pt1.x, pt2.y
							- pt1.y, pt2.z - pt1.z);
					double[][] ptBeforePCA = new double[tmpEnd - tmpStart + 1][3];
					int tmpCount = 0;
					for (int m = tmpStart; m <= tmpEnd; m++) {
						ptBeforePCA[tmpCount][0] = tmpPointsList.get(m).x;
						ptBeforePCA[tmpCount][1] = tmpPointsList.get(m).y;
						ptBeforePCA[tmpCount][2] = tmpPointsList.get(m).z;
						tmpCount++;
					}
					Matrix ptMatrixToPCA = new Matrix(ptBeforePCA);
					PCA pcaHandler = new PCA(ptMatrixToPCA);
					int eigenVecIndex = -1;
					if (pcaHandler.getValues().get(0, 0) > pcaHandler
							.getValues().get(1, 1)) {
						if (pcaHandler.getValues().get(0, 0) > pcaHandler
								.getValues().get(2, 2)) {
							eigenVecIndex = 0;
						}
					} else {
						if (pcaHandler.getValues().get(1, 1) > pcaHandler
								.getValues().get(2, 2)) {
							eigenVecIndex = 1;
						} else {
							eigenVecIndex = 2;
						}
					}
					Vector3d vDirection = new Vector3d(pcaHandler.getVectors()
							.get(0, eigenVecIndex), pcaHandler.getVectors()
							.get(1, eigenVecIndex), pcaHandler.getVectors()
							.get(2, eigenVecIndex));
					double angle = vRefDirection.angle(vDirection);
					if (angle > (3.1415927 / 2)) {
						// System.out.println("need to flip the vDirection!!!!");
						vDirection.x = vDirection.x * (-1);
						vDirection.y = vDirection.y * (-1);
						vDirection.z = vDirection.z * (-1);
					}
					// end of PCA
					tmpPoint.x = (float) vDirection.x;
					tmpPoint.y = (float) vDirection.y;
					tmpPoint.z = (float) vDirection.z;
					this.updateTraceMapM(tmpPoint, i, segID);
					segID++;
					if(segID==this.segNum)
						break;
				} // for each segment
			} // else
		} // for each fiber
		DicccolUtilIO.writeArrayToFile(traceMapM, segNum * this.samp_num,
				fiberBuldleData.nCellNum, " ", this.traceMapMName);
		DicccolUtilIO.writeToPointsVtkFile(this.traceMapMName
				+ "_startPoints.vtk", this.startPointList);

	}

	public djVtkPoint decodePointSimple(int fiberID, int segID) {
		djVtkPoint voidPoint = new djVtkPoint();
		for (int i = 0; i < this.samp_num; i++) {
			int rowID = segID * this.samp_num + i;
			if (this.traceMapM[rowID][fiberID] == 1.0)
				return this.TraceMapSamplePoints.getPoint(i);
		}
		return voidPoint;
	}

	public djVtkPoint pointLengthNor(djVtkPoint pt) {
		double curLength = Math.sqrt(Math.pow(pt.x, 2) + Math.pow(pt.y, 2)
				+ Math.pow(pt.z, 2));
		if (curLength > 0.0001) {
			pt.x /= curLength;
			pt.y /= curLength;
			pt.z /= curLength;
		}
		return pt;
	}

	public djVtkPoint decodePoint(int fiberID, int segID) {
		djVtkPoint voidPoint = new djVtkPoint();
		int rowID = segID * this.samp_num;
		double sum = 0.0;
		for (int i = 0; i < this.samp_num; i++)
			sum += this.traceMapM[rowID + i][fiberID];
		if (sum > 0.0001) {
			rowID = segID * this.samp_num;
			for (int i = 0; i < this.samp_num; i++) {
				double curProportion = this.traceMapM[rowID + i][fiberID] / sum;
				voidPoint.x += this.TraceMapSamplePoints.getPoint(i).x * curProportion;
				voidPoint.y += this.TraceMapSamplePoints.getPoint(i).y * curProportion;
				voidPoint.z += this.TraceMapSamplePoints.getPoint(i).z * curProportion;
			}
			return this.pointLengthNor(voidPoint);
		} else
			return voidPoint;
	}

	public void fiberDecoding() {
		System.out.println("FiberDecoding...");
		System.out.println("There are " + this.traceMapColNum
				+ " fibers in this fiber bundle!");
		this.fiberBuldleData.nCellNum = this.traceMapColNum;
		this.fiberBuldleData.points.clear();
		this.fiberBuldleData.cells.clear();
		int ptIndexCount = 0;
		for (int fiberID = 0; fiberID < this.traceMapColNum; fiberID++) {
			System.out
					.println("Reconstructing the " + fiberID + " th fiber...");
			djVtkCell curCell = new djVtkCell(fiberID);
			djVtkPoint curIniPoint = this.startPointList.get(fiberID);
			curIniPoint.pointId = ptIndexCount;
			ptIndexCount++;
			curCell.pointsList.add(curIniPoint);
			curIniPoint.cellsList.add(curCell);
			this.fiberBuldleData.points.add(curIniPoint);

			djVtkPoint curStartPoint = curIniPoint;
			for (int segID = 0; segID < this.segNum; segID++) {
				djVtkPoint curEndPoint = new djVtkPoint();
				curEndPoint.pointId = ptIndexCount;
				ptIndexCount++;
				djVtkPoint tmpPoint = this.decodePoint(fiberID, segID);
				curEndPoint.x = curStartPoint.x + (this.pointInterval+3)
						* tmpPoint.x;
				curEndPoint.y = curStartPoint.y + (this.pointInterval+3)
						* tmpPoint.y;
				curEndPoint.z = curStartPoint.z + (this.pointInterval+3)
						* tmpPoint.z;
				curCell.pointsList.add(curEndPoint);
				curEndPoint.cellsList.add(curCell);
				curStartPoint = curEndPoint;
				this.fiberBuldleData.points.add(curEndPoint);
			} // for all segments
			this.fiberBuldleData.cells.add(curCell);
		} // for all fibers
		this.fiberBuldleData.nPointNum = this.fiberBuldleData.points.size();
		this.fiberBuldleData.cellsOutput.addAll(this.fiberBuldleData.cells);
		this.fiberBuldleData.writeToVtkFile(this.outputFiberName);

	}

}
