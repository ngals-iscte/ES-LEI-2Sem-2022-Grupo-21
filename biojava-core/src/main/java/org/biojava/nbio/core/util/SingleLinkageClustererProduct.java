package org.biojava.nbio.core.util;


import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class SingleLinkageClustererProduct {
	public class LinkedPair {
	
		private int first;
		private int second;
		private double closestDistance;
	
		public LinkedPair(int first, int second, double minDistance) {
			this.first = first;
			this.second = second;
			this.closestDistance = minDistance;
		}
	
		public int getFirst() {
			return first;
		}
	
		public int getSecond() {
			return second;
		}
	
		public double getClosestDistance() {
			return closestDistance;
		}
	
		@Override
		public String toString() {
	
			String closestDistStr = null;
			if (closestDistance==Double.MAX_VALUE) {
				closestDistStr = String.format("%6s", "inf");
			} else {
				closestDistStr = String.format(Locale.US, "%6.2f",closestDistance);
			}
	
			return "["+first+","+second+"-"+closestDistStr+"]";
		}
	
	}

	private boolean isScoreMatrix;
	private ArrayList<Integer> indicesToCheck;

	public boolean getIsScoreMatrix() {
		return isScoreMatrix;
	}

	public void setIsScoreMatrix(boolean isScoreMatrix) {
		this.isScoreMatrix = isScoreMatrix;
	}

	public ArrayList<Integer> getIndicesToCheck() {
		return indicesToCheck;
	}

	public void setIndicesToCheck(ArrayList<Integer> indicesToCheck) {
		this.indicesToCheck = indicesToCheck;
	}

	/**
	* The linkage function: minimum of the 2 distances (i.e. single linkage clustering)
	* @param d1
	* @param d2
	* @return
	*/
	public double link(double d1, double d2) {
		if (isScoreMatrix) {
			return Math.max(d1, d2);
		} else {
			return Math.min(d1, d2);
		}
	}

	public LinkedPair getClosestPair(double[][] thisMatrix, SingleLinkageClusterer singleLinkageClusterer) {
		LinkedPair closestPair = null;
		if (isScoreMatrix) {
			double max = 0.0;
			for (int i : indicesToCheck) {
				for (int j : indicesToCheck) {
					if (j <= i)
						continue;
					if (thisMatrix[i][j] >= max) {
						max = thisMatrix[i][j];
						closestPair = new LinkedPair(i, j, max);
					}
				}
			}
		} else {
			double min = Double.MAX_VALUE;
			for (int i : indicesToCheck) {
				for (int j : indicesToCheck) {
					if (j <= i)
						continue;
					if (thisMatrix[i][j] <= min) {
						min = thisMatrix[i][j];
						closestPair = new LinkedPair(i, j, min);
					}
				}
			}
		}
		return closestPair;
	}

	String clustersToString(Map<Integer, Set<Integer>> finalClusters) {
		StringBuilder sb = new StringBuilder();
		for (int cId:finalClusters.keySet()) {
			sb.append(cId).append(": ");
			for (int member:finalClusters.get(cId)) {
				sb.append(member).append(" ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	String matrixToString(SingleLinkageClusterer singleLinkageClusterer) {
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<singleLinkageClusterer.numItems;i++) {
			for (int j=0;j<singleLinkageClusterer.numItems;j++) {
				if (i==j) {
					sb.append(String.format("%6s ","x"));
				}
				else if (i<j) {
					if (singleLinkageClusterer.matrix[i][j]==Double.MAX_VALUE) sb.append(String.format("%6s ","inf"));
					else sb.append(String.format(Locale.US, "%6.2f ",singleLinkageClusterer.matrix[i][j]));
				}
				else {
					if (singleLinkageClusterer.matrix[j][i]==Double.MAX_VALUE) sb.append(String.format("%6s ","inf"));
					else sb.append(String.format(Locale.US, "%6.2f ",singleLinkageClusterer.matrix[j][i]));
				}
			}
			sb.append("\n");
		}
		sb.append("\n");
		return sb.toString();
	}

	boolean isWithinCutoff(SingleLinkageClusterer singleLinkageClusterer, int i, double cutoff) {
		if (getIsScoreMatrix()) {
			if (singleLinkageClusterer.dendrogram[i].getClosestDistance()>cutoff) {
				return true;
			} else {
				return false;
			}
		} else {
			if (singleLinkageClusterer.dendrogram[i].getClosestDistance()<cutoff) {
				return true;
			} else {
				return false;
			}
		}
	}
}