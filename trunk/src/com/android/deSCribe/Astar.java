package com.android.deSCribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.sax.StartElementListener;
import android.util.Log;


public abstract class Astar {
	
	abstract LinkedList<Object> Queuing_Fn(LinkedList<Object> nodes,Object obj1);
	abstract boolean Goal_Test(String s1,String s2);
	abstract LinkedList<Object> Expand(LinkedList<Object> nodes,Node n);
	abstract void Print_Nodes(LinkedList<Object> nodes);
	abstract Object Make_Node(String Name, String Parent, String path,Double distance,Double totalDist);
	public static String finalPath = "test";
	boolean Search(String startBuilding,String endBuilding){
		
		LinkedList<Object> nodes = new LinkedList<Object>();
		//nodes.add(n);
		nodes = Queuing_Fn(nodes,(Object)Make_Node(startBuilding,"","",(Double)0.0,(Double)0.0));  //Making Queue
		//nodeCheck.add(n.id);
		Node currentNode;
		while(true){                                                      //Checking if Queue is empty
			if(nodes.isEmpty()){
				break;
			}
			//Print_Nodes(nodes);                                          //Printing nodes
			currentNode = (Node)nodes.remove(0);
			Log.d("starting","startid: "+Details.startId+" end ID "+ Details.endId+"current node ID"+currentNode.id+"end bldg"+endBuilding);
			//System.out.println("The current Node is:" + currentNode.id);
			if(Goal_Test(currentNode.id,endBuilding)){                                       //Checking if we arrived
				//System.out.println("The Final Solution is : [" + currentNode.path+ "]");   //at destination
				finalPath = currentNode.path;
				Log.d("ending","finalpath "+finalPath);
				return true;
			}
			
			nodes.clear();
			Log.d("calling expand","startid: "+Details.startId+" end ID "+ Details.endId);
			nodes = Expand(nodes,currentNode);                          //Adding new nodes in the queue
		}
		
		return false;
		
	}
}


class neighbor{
	String id;
	Double distance;
}
//This is the class which holds the information about the nodes 
//like path, the total distance from the nodes 
class Node{
	String id;
	String Parent;
	String path;
	Double distance;
	Double totalDist;
	
	Node(){
		
	}
	
}

//THis class extends from the node class and has information about the heuristics
class A_Node extends Node{
	Double f;
	Double h;
	
}

class A_Search extends Astar{
	
	String DBNAME = "deSCribe";
	String TABLENAME3 = "Roads";
	//String TABLENAME2 = "Adjacency";
	SQLiteDatabase db;
	//Double endLat = (Double)0.0;
	//Double endLng = (Double)0.0;
	
	public A_Node Make_Node(String id, String Parent, String path,Double distance,Double totalDist){
		A_Node n = new A_Node();
		n.id = id;
		n.Parent = Parent;
		path = new String();
		path = id;
		n.path = path;
		n.distance = distance;
		n.totalDist = totalDist;
		Double hdist = getDistance(id);
		n.h = hdist;
		n.f = n.totalDist + n.h;
		//System.out.println("path is:"+path+"name is"+Name);
		return n;
	}
	
	public void Print_Nodes(LinkedList<Object> nodes){
		
		System.out.print("{");
		for(int i = 0;i<nodes.size();i++){
			A_Node n = (A_Node)nodes.get(i);
			System.out.print("("+n.f+" " + n.totalDist + " [" + n.path + "])");
			if(i<nodes.size()-1){
				System.out.print(" ");
			}
		}
		System.out.println("}");
	}
	
	public boolean Goal_Test(String currNode,String endNode){
		if(currNode.equals(endNode) ){
			//e add code to get current lat,lng to end_lat and lng
			//end_lat=compass.currentLocation.getLatitude();
			
			return true;
		}
		else{
			return false;
		}
		
	}
	
	public LinkedList<Object> Expand(LinkedList<Object> nodes,Node currNode){
		ArrayList<neighbor> al = GetNeighborList(currNode.id);
		int dup;
		for(int i = 0;i<al.size();i++){
			A_Node n = new A_Node();
			n.id = al.get(i).id;
			
			//System.out.println("The child name is" + n.id);
			n.Parent = currNode.id;
			n.distance = al.get(i).distance;
			n.totalDist = currNode.totalDist + n.distance;
			String pathnew = currNode.path;
			
			String pathnew1 = pathnew;
			StringTokenizer st1 = new StringTokenizer(pathnew1," ");
			dup = 0;
			while(st1.hasMoreTokens()){
				if(st1.nextToken().equals(n.id))
					dup = 1;
			}
			if(dup==1){
				continue;
			}

			pathnew = pathnew.concat(" ");
			pathnew = pathnew.concat(n.id);
			n.path = pathnew;
			

			Double hDist = getDistance(n.id);
			n.h = hDist;
			n.f = hDist + n.totalDist;
			//System.out.println("path is"+n.path);
			//nodes.add(n);
			
			nodes = Queuing_Fn(nodes,(Object)n);
			//nodeCheck.add(n.id);


		}
		
		fComparator c = new fComparator();
		Collections.sort(nodes, c);
		
		return nodes;
	}
	
	public LinkedList<Object> Queuing_Fn(LinkedList<Object> nodes,Object obj1){
		A_Node n = (A_Node)obj1;
		nodes.add(n);
		return nodes;
	}
	
	public Double getDistance(String id){
		Double lat = (Double)0.0;
		Double lng = (Double)0.0;
		
		Cursor c = deSCribe.db.query(TABLENAME3, new String[] {"latitude","longitude"}, "id="+id, null, null, null, null);
		if(c.getCount()>0){
			c.moveToFirst();
			
			lat = Double.parseDouble(c.getString(0));
			lng = Double.parseDouble(c.getString(1));
			
		}
		/*if(endLat == 0.0 && endLng == 0.0){
			Cursor c2 = deSCribe.db.query(TABLENAME3, new String[] {"latitude","longitude"}, "id="+deSCribe.endId, null, null, null, null);
			if(c2.getCount()>0){
				c2.moveToFirst();

				endLat = Double.parseDouble(c2.getString(0));
				endLng = Double.parseDouble(c2.getString(1));

			}
		}*/
		Integer R = 6371; // km
		Double distance = Math.acos(Math.sin(lat)*Math.sin(Details.endlat) + 
		                  Math.cos(lat)*Math.cos(Details.endlat) *
		                  Math.cos(Details.endlng-lng)) * R;

		//Double distance = Math.sqrt(((endLat - lat)*(endLat - lat)) +((endLng-lng)*(endLng-lng)));
		
		return distance;	
		
	}
	
	public ArrayList<neighbor> GetNeighborList(String id){
		
		ArrayList<neighbor> al = new ArrayList<neighbor>();
		neighbor n = null;
		
		String neighbors = "";
		Double neigh_lat = (Double)0.0;
		Double neigh_long = (Double)0.0;
		//Details.tvPath.setText(Details.tvPath.getText().toString()+id);
		//Log.d("in neighbours", id+"startid: "+Details.startId+" end ID "+ Details.endId);
		Double lat = (Double)0.0;
		Double lng = (Double)0.0;
		//String myRoadID="";
		String [] RoadId = null;
		Cursor c1 = deSCribe.db.query(TABLENAME3, new String[] {"latitude","longitude","road_id"}, "id="+id, null, null, null, null);
		if(c1.getCount()>0){
			c1.moveToFirst();
			lat = Double.parseDouble(c1.getString(0));
			lng = Double.parseDouble(c1.getString(1));
			RoadId=c1.getString(2).split("&");	
			//myRoadID=c1.getString(2);
		}
		
		if(id == Details.startId)
		{
			lat = Details.startlat;
			lng = Details.endlng;
		}
		
		//Neha add code to check road_id
		//String []allmyRoadid=myRoadID.split("&");
		//for(int j=0;j<allmyRoadid.length;j++)
			//Log.d("finding my road id","my road id's are: "+allmyRoadid[j]);
		//String query="";
		//String idAll="";
		//for(int j=0;j<allmyRoadid.length;j++)
		//idAll=allmyRoadid[j];
		//{
		Cursor c = deSCribe.db.query(TABLENAME3, new String[] {"id","latitude","longitude","road_id"}, null, null, null, null, null);
		if(c.getCount()>0){
			c.moveToFirst();
			do{
				String []allRoadid=c.getString(3).split("&");
				//String query="";
				for(int i=0;i<allRoadid.length;i++)
				{
					if(check_id_matches(allRoadid[i],RoadId/*allRoadid[i].equalsIgnoreCase(id)*/))//if(allRoadid[i].equalsIgnoreCase(idAll))
				{
					Integer R = 6371; // km
					Double distance = Math.acos(Math.sin(lat)*Math.sin(neigh_lat) + 
					                  Math.cos(lat)*Math.cos(neigh_lat) *
					                  Math.cos(neigh_long-lng)) * R;

					neighbors = c.getString(0);
					n = new neighbor();
					n.id = neighbors;
					n.distance = distance;					
					if(!(n.id.equals(id))){
						Log.d("adding neighbours","neighbour id: "+n.id);
						al.add(n);
					}
						break;	
				}
				}					
			}while(c.moveToNext());
		
		}
		//}
		
		return al;
	}
	
	private boolean check_id_matches(String id, String[] roadId) {
		for(int i=0;i<roadId.length;i++)
		{
			if(id.equalsIgnoreCase(roadId[i]))
				return true;			
		}	
		
		return false;
	}	
}


//THis comparator helps in sorting A* queue
class fComparator implements Comparator<Object>{
	public int compare(Object obj1, Object obj2){
		A_Node n1 = (A_Node)obj1;
		A_Node n2 = (A_Node)obj2;
		
		if(n1.f<n2.f)
			return -1;
		else if(n1.f>n2.f)
			return 1;
		else
			return 0;
		
	}
}
