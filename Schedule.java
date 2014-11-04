import java.io.*;
import java.util.*;


public class Schedule {
	private static int time, running, cpuBurst;
	private static boolean isDone, verbose=false;
	private ArrayList<Process> pList1= new ArrayList<Process>();
	private static int[] blocked; 
	private static int[] bursts;
	private static ArrayList<Process> queue;
	private static ArrayList<Process> queue1;
	private static BufferedReader br;
	
	private static int getNextNum(BufferedReader br) throws IOException{
		int res=0;
		char temp=(char) br.read();
		while(Character.isDigit(temp)){
			res*=10;
			res+=temp-'0';
			temp=(char) br.read();
		}
		return res;
	}
	
	private static int getRand() throws NumberFormatException, IOException{
		int temp=Integer.parseInt(br.readLine());
		//System.out.println(temp);
		return temp;
	}
	
	public void input (String path) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(path));
		int cnt=br.read()-'0';
		int a,b,c,io;
		System.out.print("The original input was: " + cnt + " ");
		for(int i=0;i<cnt;i++){
			br.skip(3);
			a=getNextNum(br);
			b=getNextNum(br);
			c=getNextNum(br);
			io=getNextNum(br);
			br.skip(1);
			pList1.add(new Process(a,b,c,io));
			System.out.print("( " + a + ", " + b + ", "+ c + ", "+ io+ " ) ");
		}
		System.out.println();
		Collections.sort(pList1);	
		System.out.print("The (sorted) input is: " + cnt + " ");
		for(Process p:pList1){
			System.out.print("( " + p.getA() + ", " + p.getB() + ", "+ p.getC() + ", "+ p.getIO() + " ) ");
		}
		System.out.println();
		System.out.println();

	}
	private void runFCFS() throws NumberFormatException, IOException{
		ArrayList<Process> pList=new ArrayList<Process>();
		time=0;
		running=-1;
		isDone=false;
		queue= new ArrayList<Process>();
		queue1= new ArrayList<Process>();
		for(Process p:pList1){
			pList.add(new Process(p.getA(),p.getB(),p.getC(),p.getIO()));
		}
		System.out.println("The scheduling algorithm used was First Come First Served");
		System.out.println();
		int cnt=0;
		double cpuUtil=0,ioUtil=0;
		blocked = new int[pList.size()];
		for(int i=0;i<pList.size();i++)
			blocked[i]=-1;
 		while(!isDone){
			if(verbose){System.out.print("Before cycle " + time + "  :	");
 			for(Process p:pList){
 				if(p.getState()==-2){
 					System.out.print("Terminated 0	");
 				}
 				if(p.getState()==-1){
 					System.out.print("Unstarted 0	");
 				}
 				if(p.getState()==0){
 						System.out.print("Ready 0		");
 				}
 				if(p.getState()==1){
 					System.out.print("Running "+ cpuBurst+ "	");
 				}
 				if(p.getState()==2){
 					System.out.print("Blocked " + blocked[pList.indexOf(p)]+"	");
 				}
 			}
 			System.out.println();
			}
			for(int i=0;i<pList.size();i++){//unblock
				if(blocked[i]==0){
					blocked[i]--;
					if(pList.get(i).getState()!=-2){
						pList.get(i).setState(0);
						queue.add(pList.get(i));
					}
				}
			}
			for(Process p:pList){//if a new process came in
				if(p.getA()==time){
					p.setState(0);
					queue.add(p);
				}
			}
			//unblock 1st, check for new, run, block
			
			if(running==-1){//if there's nothing running
				if(!queue.isEmpty()){
				queue.get(0).setState(1);
				running=pList.indexOf(queue.get(0));
				cpuBurst=1+getRand()%pList.get(running).getB();//TODO generate a random number and all that
				queue.remove(0);
				}
			}
			else if(running!=-1 && cpuBurst==1 /*&& !ran*/){//if you have to block
				if(pList.get(running).getC()!=1){	
					pList.get(running).setState(2);
					pList.get(running).decC();
					blocked[running]=1+getRand()%pList.get(running).getIO();//TODO generate random number +1!!!!
					pList.get(running).incIOT(blocked[running]);
				}
				else{
					if(pList.get(running).getFinish()==0){
						pList.get(running).setFinish(time);
					}
					pList.get(running).setState(-2);
					pList.get(running).decC();
					
				}
				if(!queue.isEmpty()){
					running=pList.indexOf(queue.get(0));
					pList.get(running).setState(1);
					queue.remove(0);
					cpuBurst=1+getRand()%pList.get(running).getB();//TODO generate a random number and all that 
				}
				else
					running=-1;
			}
				
			else if(running!=-1 && cpuBurst>0 /*&& !ran*/){//if there's something running
				cpuBurst--;
				pList.get(running).decC();
			}
						
			for(int i=0;i<pList.size();i++){//decrement blocked time for all
				if(blocked[i]>0){
					blocked[i]--;
				}
			}
			cnt=0;
			for(Process p:pList){
				if(p.getC()==0){
					if(pList.indexOf(p)==running){
						if(!queue.isEmpty()){
							running=pList.indexOf(queue.get(0));
							pList.get(running).setState(1);
							queue.remove(0);
							cpuBurst=1+getRand()%pList.get(running).getB();
						}
					}
					cnt++;
					if(p.getFinish()==0){
						p.setFinish(time);
					}
					p.setState(-2);
					queue.remove(p);
				}
				if(cnt==pList.size()){
					isDone=true;
				}
			}
			for(Process p:pList){
				if(p.getState()==0)p.incWait(1);
			}
			time++;
			if(running!=-1)cpuUtil++;
			for(int i=0; i<pList.size(); i++){
				if(blocked[i]>=0){
					ioUtil++;
					break;
				}
			}
			
		}
 		System.out.println();
 		int count=0;
 		double avgW=0,avgT=0,max=-1;
 		for(Process p:pList){
 			if(p.getFinish()>max)max=p.getFinish();
 			avgW+=p.getWait();
 			avgT+=p.getFinish()-p.getA();
 			System.out.println("Process "+ count++ +":");
 			System.out.println("	(A,B,C,IO) = ("+p.getA()+ ","+p.getB()+","+ (p.getFinish()-p.getA()-p.getIOT()-p.getWait())+","+p.getIO()+")");
 			System.out.println("	Finishing time: "+ p.getFinish());
 			System.out.println("	Turnaround time: "+(p.getFinish()-p.getA()));
 			System.out.println("	I/O time: " + p.getIOT());
 			System.out.println("	Waiting time: " + p.getWait());
 			System.out.println();
 		}
 		System.out.println("Summary Data:");
 		System.out.println("	Finishing time: "+ max);
 		System.out.println("	CPU Utilization: "+ cpuUtil/max);
 		System.out.println("	I/O Utilization: "+ ioUtil/max);
 		System.out.println("	Throughput "+ (count/max)*100 +" processes per hundread cycles");
 		System.out.println("	Average Turnaround time " + avgT/count);
 		System.out.println("	Average Wait time "+ avgW/count); 
 		System.out.println();
 		System.out.println("------------------------------------------------------------------------------------------");
	}
	private void runRR() throws NumberFormatException, IOException{
		ArrayList<Process> pList=new ArrayList<Process>();
		time=0;
		running=-1;
		isDone=false;
		queue= new ArrayList<Process>();
		queue1= new ArrayList<Process>();
		for(Process p:pList1){
			pList.add(new Process(p.getA(),p.getB(),p.getC(),p.getIO()));
		}
		System.out.println("The scheduling algorithm used was Round Robin");
		System.out.println();
		int cnt=0;
		int quantum=2;
		double cpuUtil=0,ioUtil=0;
		blocked = new int[pList.size()];
		bursts = new int[pList.size()];
		for(int i=0;i<pList.size();i++){
			blocked[i]=-1;
			bursts[i]=0;
		}
 		while(!isDone){
			if(verbose){System.out.print("Before cycle " + time + "  :	");
 			for(Process p:pList){
 				if(p.getState()==-2){
 					System.out.print("Terminated 0	");
 				}
 				if(p.getState()==-1){
 					System.out.print("Unstarted 0	");
 				}
 				if(p.getState()==0){
 						System.out.print("Ready 0		");
 				}
 				if(p.getState()==1){
 					System.out.print("Running "+ bursts[pList.indexOf(p)]+ "	");
 				}
 				if(p.getState()==2){
 					System.out.print("Blocked " + (blocked[pList.indexOf(p)]+1) +"	");
 				}
 			}
 			System.out.println();
 			
			}
			for(int i=0;i<pList.size();i++){//unblock
				if(blocked[i]==0){
					blocked[i]--;
					if(pList.get(i).getState()!=-2){
						pList.get(i).setState(0);
						queue1.add(pList.get(i));
					}
				}
			}
			for(Process p:pList){//if a new process came in
				if(p.getA()==time){
					p.setState(0);
					queue1.add(p);
				}
			}
			//unblock 1st, check for new, run, block
			
			if(running==-1){//if there's nothing running
				if(!queue.isEmpty()){
				queue.get(0).setState(1);
				running=pList.indexOf(queue.get(0));
				if(bursts[running]==0)
				bursts[running]=1+getRand()%pList.get(running).getB();//TODO generate a random number and all that
				queue.remove(0);
				quantum=2;

				}else if(!queue1.isEmpty()){
					queue1.get(0).setState(1);
					running=pList.indexOf(queue1.get(0));
					if(bursts[running]==0)
					bursts[running]=1+getRand()%pList.get(running).getB();//TODO generate a random number and all that
					queue1.remove(0);
					quantum=2;

				}
			}
			else if(running!=-1 && bursts[running]==1 /*&& !ran*/){//if you have to block
				if(pList.get(running).getC()!=1){	
					pList.get(running).setState(2);
					bursts[running]--;
					pList.get(running).decC();
					blocked[running]=1+getRand()%pList.get(running).getIO();//TODO generate random number +1!!!!
					pList.get(running).incIOT(blocked[running]);
				}
				else{
					if(pList.get(running).getFinish()==0){
						pList.get(running).setFinish(time);
					}
					pList.get(running).setState(-2);
					pList.get(running).decC();
					
				}
				if(!queue.isEmpty()){
					running=pList.indexOf(queue.get(0));
					pList.get(running).setState(1);
					queue.remove(0);
					if(bursts[running]==0)
					bursts[running]=1+getRand()%pList.get(running).getB();//TODO generate a random number and all that 
					quantum=2;
				}else if(!queue1.isEmpty()){
					queue1.get(0).setState(1);
					running=pList.indexOf(queue1.get(0));
					if(bursts[running]==0)
					bursts[running]=1+getRand()%pList.get(running).getB();//TODO generate a random number and all that
					queue1.remove(0);	
					quantum=2;
				}
				else
					running=-1;
			}
				
			else if(running!=-1 && bursts[running]>0){//if there's something running
				if(quantum==1){
					pList.get(running).setState(0);
					pList.get(running).decC();
					bursts[running]--;
					queue1.add(pList.get(running));
					if(!queue.isEmpty()){
						running=pList.indexOf(queue.get(0));
						pList.get(running).setState(1);
						queue.remove(0);
						if(bursts[running]==0)
						bursts[running]=1+getRand()%pList.get(running).getB();//TODO generate a random number and all that 
						quantum=2;
					}else if(!queue1.isEmpty()){
						queue1.get(0).setState(1);
						running=pList.indexOf(queue1.get(0));
						if(bursts[running]==0)
						bursts[running]=1+getRand()%pList.get(running).getB();//TODO generate a random number and all that
						queue1.remove(0);	
						quantum=2;
					}
				}else{
					quantum--;
					bursts[running]--;
					pList.get(running).decC();
				}
			}
			Collections.sort(queue1);
			queue.addAll(queue1);
			queue1.clear();
			for(int i=0;i<pList.size();i++){//decrement blocked time for all
				if(blocked[i]>0){
					blocked[i]--;
				}
			}
			cnt=0;
			for(Process p:pList){
				if(p.getC()==0){
					if(pList.indexOf(p)==running){
						if(!queue.isEmpty()){
							running=pList.indexOf(queue.get(0));
							pList.get(running).setState(1);
							queue.remove(0);
							if(bursts[running]==0)
							bursts[running]=1+getRand()%pList.get(running).getB();
							quantum=2;
						}else if(!queue1.isEmpty()){
							queue1.get(0).setState(1);
							running=pList.indexOf(queue1.get(0));
							if(bursts[running]==0)
							bursts[running]=1+getRand()%pList.get(running).getB();//TODO generate a random number and all that
							queue1.remove(0);
							quantum=2;
						}
					}
					cnt++;
					if(p.getFinish()==0){
						p.setFinish(time);
					}
					p.setState(-2);
					queue.remove(p);
				}
				if(cnt==pList.size()){
					isDone=true;
				}
			}
			for(Process p:pList){
				if(p.getState()==0)p.incWait(1);
			}
			time++;
			if(running!=-1)cpuUtil++;
			for(int i=0; i<pList.size(); i++){
				if(blocked[i]>=0){
					ioUtil++;
					break;
				}
			}
		//	if(time==100)break;
		}
 		System.out.println();
 		int count=0;
 		double avgW=0,avgT=0,max=-1;
 		for(Process p:pList){
 			if(p.getFinish()>max)max=p.getFinish();
 			avgW+=p.getWait();
 			avgT+=p.getFinish()-p.getA();
 			System.out.println("Process "+ count++ +":");
 			System.out.println("	(A,B,C,IO) = ("+p.getA()+ ","+p.getB()+","+ (p.getFinish()-p.getA()-p.getIOT()-p.getWait())+","+p.getIO()+")");
 			System.out.println("	Finishing time: "+ p.getFinish());
 			System.out.println("	Turnaround time: "+(p.getFinish()-p.getA()));
 			System.out.println("	I/O time: " + p.getIOT());
 			System.out.println("	Waiting time: " + p.getWait());
 			System.out.println();
 		}
 		System.out.println("Summary Data:");
 		System.out.println("	Finishing time: "+ max);
 		System.out.println("	CPU Utilization: "+ cpuUtil/max);
 		System.out.println("	I/O Utilization: "+ ioUtil/max);
 		System.out.println("	Throughput "+ (count/max)*100 +" processes per hundread cycles");
 		System.out.println("	Average Turnaround time " + avgT/count);
 		System.out.println("	Average Wait time "+ avgW/count); 
 		System.out.println();
 		System.out.println("------------------------------------------------------------------------------------------");
	}
	private void runUni() throws NumberFormatException, IOException{
		ArrayList<Process> pList=new ArrayList<Process>();
		time=0;
		running=-1;
		isDone=false;
		queue= new ArrayList<Process>();
		queue1= new ArrayList<Process>();
		for(Process p:pList1){
			pList.add(new Process(p.getA(),p.getB(),p.getC(),p.getIO()));
		}
		System.out.println("The scheduling algorithm used was Uniprogrammed");
		System.out.println();
		boolean firstGo=false;
		int cnt=0;
		double cpuUtil=0,ioUtil=0;
		blocked = new int[pList.size()];
		for(int i=0;i<pList.size();i++)
			blocked[i]=-1;
 		while(!isDone){
 			firstGo=false;
			if(verbose){System.out.print("Before cycle " + time + "  :	");
 			for(Process p:pList){
 				if(p.getState()==-2){
 					System.out.print("Terminated 0	");
 				}
 				if(p.getState()==-1){
 					System.out.print("Unstarted 0	");
 				}
 				if(p.getState()==0){
 						System.out.print("Ready 0		");
 				}
 				if(p.getState()==1){
 					System.out.print("Running "+ cpuBurst+ "	");
 				}
 				if(p.getState()==2){
 					System.out.print("Blocked " + (blocked[pList.indexOf(p)]+1) +"	");
 				}
 			}
 			System.out.println();
			}
			if(running!=-1){
			if(blocked[running]==0){
					blocked[running]--;
					if(pList.get(running).getState()!=-2){
							pList.get(running).setState(1);
							cpuBurst=1+getRand()%pList.get(running).getB(); 
							firstGo=true;
					}
				}
			}
			for(Process p:pList){//if a new process came in
				if(p.getA()==time){
					if(running==-1){
						running=pList.indexOf(p);
						cpuBurst=1+getRand()%p.getB(); 
						firstGo=true;
						p.setState(1);
					}
					else p.setState(0);
				}
			}
			
			if(running!=-1 && cpuBurst==1 && !firstGo){//if you have to block
				if(pList.get(running).getState()==1){
				if(pList.get(running).getC()!=1){	
					pList.get(running).setState(2);
					pList.get(running).decC();
					blocked[running]=1+getRand()%pList.get(running).getIO();//TODO generate random number +1!!!!
					pList.get(running).incIOT(blocked[running]);
				}
				else{
					if(pList.get(running).getFinish()==0){
						pList.get(running).setFinish(time);
					}
					pList.get(running).setState(-2);
					pList.get(running).decC();
					if(running==pList.size()-1){
						isDone=true;
					}
					else if(pList.get(running+1).getState()==0){
						running++;
						pList.get(running).setState(1);
						cpuBurst=1+getRand()%pList.get(running).getB(); 
					}
					else running=-1;
				}
				}
			}
				
			else if(running!=-1 && cpuBurst>1 && !firstGo){//if there's something running
				if(pList.get(running).getState()==1){
					if(pList.get(running).getC()==1){
						if(pList.get(running).getFinish()==0){
							pList.get(running).setFinish(time);
						}
						pList.get(running).setState(-2);
						pList.get(running).decC();
						if(running==pList.size()-1){
							isDone=true;
						}
						else if(pList.get(running+1).getState()==0){
							running++;
							pList.get(running).setState(1);
							cpuBurst=1+getRand()%pList.get(running).getB(); 
						}
						else running=-1;
					}else{
					cpuBurst--;
					pList.get(running).decC();
					}
				}
			}
			if(blocked[running]>0){
				blocked[running]--;
			}
			cnt=0;
			for(Process p:pList){
				if(p.getC()==0){
					cnt++;
				}
				if(cnt==pList.size()){
					isDone=true;
				}
			}
			boolean didRun=false;
			for(Process p:pList){
				if(p.getState()==1)didRun=true;
				if(p.getState()==0)p.incWait(1);
			}
			if(didRun)cpuUtil++;
			time++;
			for(int i=0; i<pList.size(); i++){
				if(blocked[i]>=0){
					ioUtil++;
					break;
				}
			}
		}
 		System.out.println();
 		int count=0;
 		double avgW=0,avgT=0,max=-1;
 		for(Process p:pList){
 			if(p.getFinish()>max)max=p.getFinish();
 			avgW+=p.getWait();
 			avgT+=p.getFinish()-p.getA();
 			System.out.println("Process "+ count++ +":");
 			System.out.println("	(A,B,C,IO) = ("+p.getA()+ ","+p.getB()+","+ (p.getFinish()-p.getA()-p.getIOT()-p.getWait())+","+p.getIO()+")");
 			System.out.println("	Finishing time: "+ p.getFinish());
 			System.out.println("	Turnaround time: "+(p.getFinish()-p.getA()));
 			System.out.println("	I/O time: " + p.getIOT());
 			System.out.println("	Waiting time: " + p.getWait());
 			System.out.println();
 		}
 		System.out.println("Summary Data:");
 		System.out.println("	Finishing time: "+ max);
 		System.out.println("	CPU Utilization: "+ cpuUtil/max);
 		System.out.println("	I/O Utilization: "+ ioUtil/max);
 		System.out.println("	Throughput "+ (count/max)*100 +" processes per hundread cycles");
 		System.out.println("	Average Turnaround time " + avgT/count);
 		System.out.println("	Average Wait time "+ avgW/count); 
 		System.out.println();
 		System.out.println("------------------------------------------------------------------------------------------");
	}
	private void runSJF() throws NumberFormatException, IOException{
		ArrayList<Process> pList=new ArrayList<Process>();
		time=0;
		running=-1;
		isDone=false;
		queue= new ArrayList<Process>();
		queue1= new ArrayList<Process>();
		for(Process p:pList1){
			pList.add(new Process(p.getA(),p.getB(),p.getC(),p.getIO()));
		}
		System.out.println("The scheduling algorithm used was Shortest Job First");
		System.out.println();
		int cnt=0;
		double cpuUtil=0,ioUtil=0;
		blocked = new int[pList.size()];
		for(int i=0;i<pList.size();i++)
			blocked[i]=-1;
 		while(!isDone){
			if(verbose){System.out.print("Before cycle " + time + "  :	");
 			for(Process p:pList){
 				if(p.getState()==-2){
 					System.out.print("Terminated 0	");
 				}
 				if(p.getState()==-1){
 					System.out.print("Unstarted 0	");
 				}
 				if(p.getState()==0){
 						System.out.print("Ready 0		");
 				}
 				if(p.getState()==1){
 					System.out.print("Running "+ cpuBurst+ "	");
 				}
 				if(p.getState()==2){
 					System.out.print("Blocked " + blocked[pList.indexOf(p)]+"	");
 				}
 			}
 			System.out.println();
			}
			for(int i=0;i<pList.size();i++){//unblock
				if(blocked[i]==0){
					blocked[i]--;
					if(pList.get(i).getState()!=-2){
						pList.get(i).setState(0);
						queue.add(pList.get(i));
					}
				}
			}
			for(Process p:pList){//if a new process came in
				if(p.getA()==time){
					p.setState(0);
					queue.add(p);
				}
			}
			//unblock 1st, check for new, run, block
			
			if(running==-1){//if there's nothing running
				if(!queue.isEmpty()){
				int i=0,min=100000,runInd=-1;
				for(;i<queue.size();i++){
					if(queue.get(i).getC()<min){
						min=queue.get(i).getC();
						runInd=i;
					}
				}
				queue.get(runInd).setState(1);
				running=pList.indexOf(queue.get(runInd));
				cpuBurst=1+getRand()%pList.get(running).getB();//TODO generate a random number and all that
				queue.remove(runInd);
				}
			}
			else if(running!=-1 && cpuBurst==1 /*&& !ran*/){//if you have to block
				if(pList.get(running).getC()!=1){	
					pList.get(running).setState(2);
					pList.get(running).decC();
					blocked[running]=1+getRand()%pList.get(running).getIO();//TODO generate random number +1!!!!
					pList.get(running).incIOT(blocked[running]);
				}
				else{
					if(pList.get(running).getFinish()==0){
						pList.get(running).setFinish(time);
					}
					pList.get(running).setState(-2);
					pList.get(running).decC();
					
				}
				if(!queue.isEmpty()){
					int i=0,min=100000,runInd=-1;
					for(;i<queue.size();i++){
						if(queue.get(i).getC()<min){
							min=queue.get(i).getC();
							runInd=i;
						}
					}
					queue.get(runInd).setState(1);
					running=pList.indexOf(queue.get(runInd));
					cpuBurst=1+getRand()%pList.get(running).getB();//TODO generate a random number and all that
					queue.remove(runInd);
					}
				else
					running=-1;
			}
				
			else if(running!=-1 && cpuBurst>0 /*&& !ran*/){//if there's something running
				cpuBurst--;
				pList.get(running).decC();
			}
						
			for(int i=0;i<pList.size();i++){//decrement blocked time for all
				if(blocked[i]>0){
					blocked[i]--;
				}
			}
			cnt=0;
			for(Process p:pList){
				if(p.getC()==0){
					if(pList.indexOf(p)==running){
						if(!queue.isEmpty()){
							int i=0,min=100000,runInd=-1;
							for(;i<queue.size();i++){
								if(queue.get(i).getC()<min){
									min=queue.get(i).getC();
									runInd=i;
								}
							}
							queue.get(runInd).setState(1);
							running=pList.indexOf(queue.get(runInd));
							cpuBurst=1+getRand()%pList.get(running).getB();//TODO generate a random number and all that
							queue.remove(runInd);
							}
						else running=-1;
					}
					cnt++;
					if(p.getFinish()==0){
						p.setFinish(time);
					}
					p.setState(-2);
					queue.remove(p);
				}
				if(cnt==pList.size()){
					isDone=true;
				}
			}
			for(Process p:pList){
				if(p.getState()==0)p.incWait(1);
			}
			time++;
			if(running!=-1)cpuUtil++;
			for(int i=0; i<pList.size(); i++){
				if(blocked[i]>=0){
					ioUtil++;
					break;
				}
			}
			if(time==200)break;
		}
 		System.out.println();
 		int count=0;
 		double avgW=0,avgT=0,max=-1;
 		for(Process p:pList){
 			if(p.getFinish()>max)max=p.getFinish();
 			avgW+=p.getWait();
 			avgT+=p.getFinish()-p.getA();
 			System.out.println("Process "+ count++ +":");
 			System.out.println("	(A,B,C,IO) = ("+p.getA()+ ","+p.getB()+","+ (p.getFinish()-p.getA()-p.getIOT()-p.getWait())+","+p.getIO()+")");
 			System.out.println("	Finishing time: "+ p.getFinish());
 			System.out.println("	Turnaround time: "+(p.getFinish()-p.getA()));
 			System.out.println("	I/O time: " + p.getIOT());
 			System.out.println("	Waiting time: " + p.getWait());
 			System.out.println();
 		}
 		System.out.println("Summary Data:");
 		System.out.println("	Finishing time: "+ max);
 		System.out.println("	CPU Utilization: "+ cpuUtil/max);
 		System.out.println("	I/O Utilization: "+ ioUtil/max);
 		System.out.println("	Throughput "+ (count/max)*100 +" processes per hundread cycles");
 		System.out.println("	Average Turnaround time " + avgT/count);
 		System.out.println("	Average Wait time "+ avgW/count); 
 		System.out.println();
 		System.out.println("------------------------------------------------------------------------------------------");
	}

	public static void main(String args[]) throws IOException{
		Schedule s=new Schedule();
		if(args.length==2){
			verbose=true;
			s.input(args[1]);
		}else s.input(args[0]);
		br=new BufferedReader(new FileReader("random.txt"));
		s.runFCFS();
		br.close();
		br=new BufferedReader(new FileReader("random.txt"));
		s.runRR();
		br.close();
		br=new BufferedReader(new FileReader("random.txt"));
		s.runUni();
		br.close();
		br=new BufferedReader(new FileReader("random.txt"));
		s.runSJF();
		br.close();
	}

}