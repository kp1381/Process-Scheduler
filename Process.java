
public class Process implements Comparable<Process>{
	private int A,B,C,IO,State;//-2 done, -1 not started, 0 ready, 1 running, 2 blocked
	private int Finish=0, IOT=0, Wait=0;
	
	public void incIOT(int i){
		this.IOT+=i;
	}
	public void incWait(int i){
		this.Wait+=i;
	}
	public int getFinish() {
		return Finish;
	}
	public void setFinish(int finish) {
		Finish = finish;
	}
	public int getIOT() {
		return IOT;
	}
	public void setIOT(int iOT) {
		IOT = iOT;
	}
	public int getWait() {
		return Wait;
	}
	public void setWait(int wait) {
		Wait = wait;
	}
	public Process(int a, int b, int c, int io){
		A=a; B=b; C=c; IO=io; State=-1;
	}
	public void decC(){
		this.C--;
	}
	public int getA() {
		return A;
	}

	public void setA(int a) {
		A = a;
	}

	public int getB() {
		return B;
	}

	public void setB(int b) {
		B = b;
	}

	public int getC() {
		return C;
	}

	public void setC(int c) {
		C = c;
	}

	public int getIO() {
		return IO;
	}

	public void setIO(int iO) {
		IO = iO;
	}

	public int getState() {
		return State;
	}

	public void setState(int state) {
		State = state;
	}
	public int compareTo(Process a){
		if(a.A>this.A)return -1;
		else if(a.A<this.A)return 1;
		else if(a.B>this.B)return -1;
		else if(a.B<this.B)return 1;
		else if(a.C>this.C)return -1;
		else if(a.C<this.C)return 1;
		else if(a.IO>this.IO)return -1;
		else if(a.IO<this.IO)return 1;
		else return 0;
	}
	public String toString(){
		String res="";
		res+=this.A;
		res+=' ';
		res+=this.B;
		res+=' ';
		res+=this.C;
		res+=' ';
		res+=this.IO;
		res+=' ';
		res+=this.State;
		return res;
	}

}
