package simulator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import building.Building;
import building.Floor;
import controlSystem.CMS;
import controlSystem.Request;

public class Simulator {//userless now just ignore it
	private int currentTime;
	private CMS cms=CMS.getInstance();
	public Simulator() {
		currentTime=0;
	}
	public void StartSimulation(Building b, ArrayList<Request> inputList) {
		System.out.printf("------------------Start simulation-----------------%n%n");
		while (currentTime!=86400) {
			Iterator<Request> itr=inputList.iterator();
			while (itr.hasNext()) {
				Request r=itr.next();
				if (r.getRequestTime()<=currentTime) {//put request inside the req System when time meet in input
					r.getPassenger().makeRequest(currentTime);
					itr.remove();
				}
				else break;
			}
			cms.setCurrentTime(currentTime);
			if(!cms.curHaveRequest()) {
				if(cms.anyLiftRunning()) {
					cms.operate(currentTime);
				}
			}
			else {
				/*
				 * for(Map.Entry<Integer, List<Request>> map:
				 * cms.getReqSys().getEachFloorReq().entrySet()) { //check all request to design
				 * if we need to assign new, this part incomplete now
				 * System.out.println("Floor "+map.getKey()); for (Request r: map.getValue()) {
				 * if (r.getRequestTime()<=currentTime) { cms.assignClosest(r); } } }
				 * cms.operate(currentTime);
				 * System.out.println("Current time is: "+currentTime);
				 */
				for (Map.Entry<Integer, Floor> flrMap: b.getFlrMap().entrySet()) {
					Floor f=flrMap.getValue();
					if (f.haveUpReq(currentTime)&&!f.getUpflag()) {//have request and not yet accepted by any lift
						cms.assignClosest2(flrMap.getKey(), 1);
						System.out.println("trigger up assign");
					}
					if (f.haveDownReq(currentTime)&&!f.getDownflag()) {
						cms.assignClosest2(flrMap.getKey(), 0);
						System.out.println("trigger down assign");
					}
				}
				cms.operate(currentTime);
			}
			
			currentTime++;
		}
		System.out.println("Simulation ends!");
	}
}
