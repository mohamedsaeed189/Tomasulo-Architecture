
public class Instruction {

	String instruction;
	String i1;
	String i2;
	String i3="";
	int issue=-1;
	int execStart=-1;
	int execEnd=-1;
	int writeResult=-1;
	String tag=null;

	
	public Instruction(String instruction, String i1, String i2, String i3, int issue, int execStart, int execEnd, int writeResult) {
		this.instruction = instruction;
		this.i1 = i1;
		this.i2 = i2;
		this.i3 = i3;
		this.issue = issue;
		this.execStart = execStart;
		this.execEnd = execEnd;
		this.writeResult = writeResult;
		this.tag=null;
	}


	public void display() {
		System.out.println(instruction + " " + i1 + " " + i2 + " " + i3);

	}
}
