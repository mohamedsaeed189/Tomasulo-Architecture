import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Engine {

	static Instruction[] instructionQueue;
	static ReservationStation[] addSubRS;
	static ReservationStation[] mulDivRS;
	static LoadBuffer[] loadBuffers;
	static StoreBuffer[] storeBuffers;
	static RegisterFile registerFile= new RegisterFile();
	static Memory memory= new Memory();
	static int addLatency;
	static int subLatency;
	static int mulLatency;
	static int divLatency;
	static int loadLatency;
	static int storeLatency;
	static int clock=1;
	static double bus;
	static String busTag;
	static int nextInstrIssue=0;
	static boolean writingNow=false;
	

	public static void parser(List<String> programFile) {
		int i = 0;
		for (String x : programFile) {
			String[] elements = x.split(" ");
			if(elements.length==4) {
				Instruction inst = new Instruction(elements[0], elements[1], elements[2], elements[3], -1, -1, -1, -1);
				instructionQueue[i] = inst;
			}
			else {
				Instruction inst = new Instruction(elements[0], elements[1], elements[2], "", -1, -1, -1, -1);
				instructionQueue[i] = inst;
			}	
			i++;
		}
	}
	
	public static int isAddSubAvail()
	{
		for(int i=0;i<addSubRS.length;i++)
			if(addSubRS[i].busy==0)
				return i;
		return -1;
	}
	
	public static int isMulDivAvail()
	{
		for(int i=0;i<mulDivRS.length;i++)
			if(mulDivRS[i].busy==0)
				return i;
		return -1;
	}
	
	public static int isLoadAvail()
	{
		for(int i=0;i<loadBuffers.length;i++)
			if(loadBuffers[i].busy==0)
				return i;
		return -1;
	}
	
	public static int isStoreAvail()
	{
		for(int i=0;i<storeBuffers.length;i++)
			if(storeBuffers[i].busy==0)
				return i;
		return -1;
	}
	
	public static boolean checkAllFinished()
	{
		for(int i=0;i<instructionQueue.length;i++)
			if(instructionQueue[i].writeResult==-1) //should be instructionQueue[i].writeResult==-1, but it's execEnd just for testing purposes 
				return false;
		return true;
	}
	
	public static void start()
	{
		while (!checkAllFinished()) //while there's any instruction that didn't finish  yet
		{
			//try to write result
			for(int i=0;i<instructionQueue.length;i++)
			{
				if(instructionQueue[i].writeResult==-1 && instructionQueue[i].execEnd!=-1 && !writingNow)
				{
					String station=instructionQueue[i].tag.charAt(0)+"";
					int stationNumber=Integer.parseInt(instructionQueue[i].tag.substring(1));
					if(station.equals("A"))
					{
						writingNow=true;
						bus=addSubRS[stationNumber-1].result; //put result on bus
						busTag="A"+stationNumber; //put tag on bus
						//addSubRS[i].busy=0; //empty the RS
						instructionQueue[i].writeResult=clock; 
						break;
					}
					else if(station.equals("M"))
					{
						writingNow=true;
						bus=mulDivRS[stationNumber-1].result; //put result on bus
						busTag="M"+stationNumber; //put tag on bus
						//mulDivRS[i].busy=0; //empty the RS
						instructionQueue[i].writeResult=clock; 
						break;
					}
					else if(station.equals("L"))
					{
						writingNow=true;
						bus=loadBuffers[stationNumber-1].result; //put result on bus
						busTag="L"+stationNumber; //put tag on bus
						//loadBuffers[i].busy=0; //empty the RS
						instructionQueue[i].writeResult=clock; 
						break;
					}
					else if(station.equals("S"))
					{
						writingNow=true;
						//storeBuffers[i].busy=0; //empty the RS
						busTag="S"+stationNumber;
						instructionQueue[i].writeResult=clock; 
						break;
					}
				}
			}
			//1)check addSubRS
			/*for(int i=0;i<addSubRS.length;i++)
			{
				if(addSubRS[i].busy==1 && addSubRS[i].time==0 && !writingNow) //check if there's an instruction that finished exec but didn't write and check that no one else is currently writing
				{
					writingNow=true;
					bus=addSubRS[i].result; //put result on bus
					int iplus1=i+1;
					busTag="A"+iplus1; //put tag on bus
					//addSubRS[i].busy=0; //empty the RS
					int idInInstrQueue=addSubRS[i].idInInstrQueue;
					instructionQueue[idInInstrQueue].writeResult=clock; 
					break;
				}
			}*/
			//2)check mulDivRS
			/*for(int i=0;i<mulDivRS.length;i++)//check if there's an instruction that finished exec but didn't write and check that no one else is currently writing
			{
				if(mulDivRS[i].busy==1 && mulDivRS[i].time==0 && !writingNow)
				{
					writingNow=true;
					bus=mulDivRS[i].result; //put result on bus
					int iplus1=i+1;
					busTag="M"+iplus1; //put tag on bus
					//mulDivRS[i].busy=0; //empty the RS
					int idInInstrQueue=mulDivRS[i].idInInstrQueue;
					instructionQueue[idInInstrQueue].writeResult=clock; 
					break;
				}
			}*/
			//3)check load buffers
			/*for(int i=0;i<loadBuffers.length;i++)//check if there's an instruction that finished exec but didn't write and check that no one else is currently writing
			{
				if(loadBuffers[i].busy==1 && loadBuffers[i].time==0 && !writingNow)
				{
					writingNow=true;
					bus=loadBuffers[i].result; //put result on bus
					int iplus1=i+1;
					busTag="L"+iplus1; //put tag on bus
					//loadBuffers[i].busy=0; //empty the RS
					int idInInstrQueue=loadBuffers[i].idInInstrQueue;
					instructionQueue[idInInstrQueue].writeResult=clock; 
					break;
				}
			}*/
			//4)check store buffers
			/*for(int i=0;i<storeBuffers.length;i++)//check if there's an instruction that finished exec but didn't write and check that no one else is currently writing
			{
				if(storeBuffers[i].busy==1 && storeBuffers[i].time==0 && !writingNow)
				{
					writingNow=true;
					//storeBuffers[i].busy=0; //empty the RS
					int iplus1=i+1;
					busTag="S"+iplus1;
					int idInInstrQueue=storeBuffers[i].idInInstrQueue;
					instructionQueue[idInInstrQueue].writeResult=clock; 
					break;
				}
			}*/
			
			//try to execute
			//1)addSub reservation stations
			for(int i=0;i<addSubRS.length;i++)
			{
				if(addSubRS[i].busy==1 && addSubRS[i].Qj.equals("") && addSubRS[i].Qk.equals("") && addSubRS[i].time>0) //check if there's a RS that has operands ready and has time left in execution
					{
						if((addSubRS[i].op.equals("ADD.D") && addSubRS[i].time==addLatency) || (addSubRS[i].op.equals("SUB.D") && addSubRS[i].time==subLatency )) //check if it's the first cycle of exec to set exec start time
						{
							int idInInstrQueue=addSubRS[i].idInInstrQueue;
							instructionQueue[idInInstrQueue].execStart=clock; 
						}
							
						addSubRS[i].time--; 
						if(addSubRS[i].time==0) //check if this is its last exec cycle
						{
							if(addSubRS[i].op.equals("ADD.D")) //check operation
								addSubRS[i].result=addSubRS[i].Vj+addSubRS[i].Vk; //calculate the result to write and store it
							if(addSubRS[i].op.equals("SUB.D"))
								addSubRS[i].result=addSubRS[i].Vj-addSubRS[i].Vk; //calculate the result to write and store it
							int idInInstrQueue=addSubRS[i].idInInstrQueue;
							instructionQueue[idInInstrQueue].execEnd=clock; //set the endExecution time in the instruction Queue table
						}
					}
			}
			
			//2)mulDiv reservation stations
			for(int i=0;i<mulDivRS.length;i++)
			{
				if(mulDivRS[i].busy==1 && mulDivRS[i].Qj.equals("") && mulDivRS[i].Qk.equals("") && mulDivRS[i].time>0) //check if there's a RS that has operands ready and has time left in execution
				{
					if((mulDivRS[i].op.equals("MUL.D") && mulDivRS[i].time==mulLatency) || (mulDivRS[i].op.equals("DIV.D") && mulDivRS[i].time==divLatency )) //check if it's the first cycle of exec to set exec start time
					{
						int idInInstrQueue=mulDivRS[i].idInInstrQueue;
						instructionQueue[idInInstrQueue].execStart=clock; 
					}
					mulDivRS[i].time--;
					if(mulDivRS[i].time==0) //check if this is its last exec cycle
					{
						if(mulDivRS[i].op.equals("MUL.D")) //check operation
							mulDivRS[i].result=mulDivRS[i].Vj*mulDivRS[i].Vk; //calculate the result to write and store it
						if(mulDivRS[i].op.equals("DIV.D"))
							mulDivRS[i].result=mulDivRS[i].Vj/mulDivRS[i].Vk; //calculate the result to write and store it
						int idInInstrQueue=mulDivRS[i].idInInstrQueue;
						instructionQueue[idInInstrQueue].execEnd=clock; //set the endExecution time in the instruction Queue table
					}
				}
			}
			
			//3)loadBuffers
			for(int i=0;i<loadBuffers.length;i++)
			{
				if(loadBuffers[i].busy==1 && loadBuffers[i].time>0)//check if there's a RS that has time left in execution
				{
					if( loadBuffers[i].time==loadLatency ) //check if it's the first cycle of exec to set exec start time
					{
						int idInInstrQueue=loadBuffers[i].idInInstrQueue;
						instructionQueue[idInInstrQueue].execStart=clock; 
					}
					loadBuffers[i].time--;
					if(loadBuffers[i].time==0) //check if this is its last exec cycle
					{
						int address=loadBuffers[i].address;
						loadBuffers[i].result=memory.memoryValues[address]; //get the loaded result from memory
						int idInInstrQueue=loadBuffers[i].idInInstrQueue;
						instructionQueue[idInInstrQueue].execEnd=clock; //set the endExecution time in the instruction Queue table
					}
				}
			}
			
			//4)storeBuffers
			for(int i=0;i<storeBuffers.length;i++)
			{
				if(storeBuffers[i].busy==1 && storeBuffers[i].Q.equals("") && storeBuffers[i].time>0)//check if there's a RS that has its operand ready and has time left in execution
				{
					if(storeBuffers[i].time==storeLatency ) //check if it's the first cycle of exec to set exec start time
					{
						int idInInstrQueue=storeBuffers[i].idInInstrQueue;
						instructionQueue[idInInstrQueue].execStart=clock; 
					}
					storeBuffers[i].time--;
					if(storeBuffers[i].time==0) //check if this is its last exec cycle
					{
						int address=storeBuffers[i].address;
						double value =storeBuffers[i].V;
						memory.memoryValues[address]=value; //store the value in memory
						int idInInstrQueue=storeBuffers[i].idInInstrQueue;
						instructionQueue[idInInstrQueue].execEnd=clock; //set the endExecution time in the instruction Queue table
					}
				}
			}
			
			//try to issue
			if(nextInstrIssue<instructionQueue.length)  //check if there is remaining instructions to issue
			{
				String operation=instructionQueue[nextInstrIssue].instruction;
				if(operation.equals("ADD.D") || operation.equals("SUB.D"))
				{
					int slot=isAddSubAvail();
					if (slot!=-1)
					{
						instructionQueue[nextInstrIssue].issue=clock; //set issue time for instruction
						
						addSubRS[slot].busy=1; //set RS as busy
						addSubRS[slot].op=operation; //set operation in RS
						
						String operand1=instructionQueue[nextInstrIssue].i2; //get 1st operand
						if(registerFile.isOperandAvailable(operand1)) //check if 1st operand is available
							{
								addSubRS[slot].Vj=registerFile.getValue(operand1);
								addSubRS[slot].Qj="";
							}
						else
							addSubRS[slot].Qj=registerFile.getQ(operand1);
						
						String operand2=instructionQueue[nextInstrIssue].i3;
						if(registerFile.isOperandAvailable(operand2)) //check if 2nd operand is available
							{
								addSubRS[slot].Vk=registerFile.getValue(operand2);
								addSubRS[slot].Qk="";
							}
						else
							addSubRS[slot].Qk=registerFile.getQ(operand2);
						
						if(operation.equals("ADD.D")) //set remaining exec cycles for instruction
							addSubRS[slot].time=addLatency;
						if(operation.equals("SUB.D"))
							addSubRS[slot].time=subLatency;
						
						int slotPlus1=slot+1;
						String destination=instructionQueue[nextInstrIssue].i1;
						registerFile.setQ("A"+slotPlus1,destination); //set the Q tag for the destination to be the tag of this RS
						
						addSubRS[slot].idInInstrQueue=nextInstrIssue; //save its id from the instruction queue
						instructionQueue[nextInstrIssue].tag="A"+slotPlus1;
						
						nextInstrIssue++;
					}
				}
				else if(operation.equals("MUL.D") || operation.equals("DIV.D"))
				{
					int slot=isMulDivAvail();
					if(slot!=-1)
					{
						instructionQueue[nextInstrIssue].issue=clock; //set issue time for instruction
						
						mulDivRS[slot].busy=1; //set RS as busy
						mulDivRS[slot].op=operation; //set operation in RS
						
						String operand1=instructionQueue[nextInstrIssue].i2; //get 1st operand
						if(registerFile.isOperandAvailable(operand1)) //check if 1st operand is available
							{
								mulDivRS[slot].Vj=registerFile.getValue(operand1);
								mulDivRS[slot].Qj="";
							}
						else
							mulDivRS[slot].Qj=registerFile.getQ(operand1);
						
						String operand2=instructionQueue[nextInstrIssue].i3;
						if(registerFile.isOperandAvailable(operand2)) //check if 2nd operand is available
							{
								mulDivRS[slot].Vk=registerFile.getValue(operand2);
								mulDivRS[slot].Qk="";
							}
						else
							mulDivRS[slot].Qk=registerFile.getQ(operand2);
						
						if(operation.equals("MUL.D")) //set remaining exec cycles for instruction
							mulDivRS[slot].time=mulLatency;
						if(operation.equals("DIV.D"))
							mulDivRS[slot].time=divLatency;
						
						int slotPlus1=slot+1;
						String destination=instructionQueue[nextInstrIssue].i1;
						registerFile.setQ("M"+slotPlus1,destination); //set the Q tag for the destination to be the tag of this RS
						
						mulDivRS[slot].idInInstrQueue=nextInstrIssue; //save its id from the instruction queue
						
						instructionQueue[nextInstrIssue].tag="M"+slotPlus1;
						
						nextInstrIssue++;
					}
				}
				else if(operation.equals("L.D"))
				{
					int slot=isLoadAvail();
					if(slot!=-1)
					{
						instructionQueue[nextInstrIssue].issue=clock; //set issue time for instruction
						loadBuffers[slot].busy=1; //set RS as busy
						
						int address=Integer.parseInt(instructionQueue[nextInstrIssue].i2);
						loadBuffers[slot].address=address;
						
						loadBuffers[slot].time=loadLatency;
						
						int slotPlus1=slot+1;
						String destination=instructionQueue[nextInstrIssue].i1;
						registerFile.setQ("L"+slotPlus1,destination); //set the Q tag for the destination to be the tag of this RS
						
						loadBuffers[slot].idInInstrQueue=nextInstrIssue; //save its id from the instruction queue
						
						instructionQueue[nextInstrIssue].tag="L"+slotPlus1;
						
						nextInstrIssue++;
					}
				}
				else if(operation.equals("S.D"))
				{
					int slot=isStoreAvail();
					if(slot!=-1)
					{
						instructionQueue[nextInstrIssue].issue=clock; //set issue time for instruction
						storeBuffers[slot].busy=1; //set RS as busy
						
						int address=Integer.parseInt(instructionQueue[nextInstrIssue].i2);
						storeBuffers[slot].address=address;
						
						String register=instructionQueue[nextInstrIssue].i1;
						if(registerFile.isOperandAvailable(register)) //check if value is available
							{
								storeBuffers[slot].V=registerFile.getValue(register);
								//System.out.println("HEREEEEEEEEEEEEEE "+registerFile.getQ(register)+" "+register);
								storeBuffers[slot].Q="";
							}
						else
							storeBuffers[slot].Q=registerFile.getQ(register);
						
						storeBuffers[slot].time=storeLatency;
						
						storeBuffers[slot].idInInstrQueue=nextInstrIssue; //save its id from the instruction queue
						
						int slotPlus1=slot+1;
						
						instructionQueue[nextInstrIssue].tag="S"+slotPlus1;
						
						nextInstrIssue++;
					}
				}
			}
			
			//check if RS/buffers/register files need the value that's written on the bus
			for(int i=0;i<addSubRS.length;i++)
			{
				if(writingNow && addSubRS[i].busy==1 && addSubRS[i].Qj.equals(busTag))
				{
					addSubRS[i].Vj=bus;
					addSubRS[i].Qj="";
				}
				if(writingNow && addSubRS[i].busy==1 && addSubRS[i].Qk.equals(busTag))
				{
					addSubRS[i].Vk=bus;
					addSubRS[i].Qk="";
				}
			}
			for(int i=0;i<mulDivRS.length;i++)
			{
				if(writingNow && mulDivRS[i].busy==1 && mulDivRS[i].Qj.equals(busTag))
				{
					mulDivRS[i].Vj=bus;
					mulDivRS[i].Qj="";
				}
				if(writingNow && mulDivRS[i].busy==1 && mulDivRS[i].Qk.equals(busTag))
				{
					mulDivRS[i].Vk=bus;
					mulDivRS[i].Qk="";
				}
			}
			for(int i=0;i<storeBuffers.length;i++)
			{
				if(writingNow && storeBuffers[i].busy==1 && storeBuffers[i].Q.equals(busTag))
				{
					storeBuffers[i].V=bus;
					storeBuffers[i].Q="";
				}
			}
			for(int i=0;i<registerFile.file.length;i++)
			{
				String Q=registerFile.getQ("F"+i);
				if(writingNow && Q.equals(busTag))
				{
					registerFile.setValue(bus,"F"+i);
				}
			}
			
			//empty the reservation station/buffer that wrote in this cycle
			if(busTag!=null)
			{
				String stationType=busTag.charAt(0)+"";
				int stationNumber=Integer.parseInt(busTag.substring(1));
				if(stationType.equals("A"))
				{
					addSubRS[stationNumber-1].busy=0;
				}
				else if (stationType.equals("M"))
				{
					mulDivRS[stationNumber-1].busy=0;
				}
				else if (stationType.equals("L"))
				{
					loadBuffers[stationNumber-1].busy=0;
				}
				else if (stationType.equals("S"))
				{
					storeBuffers[stationNumber-1].busy=0;
				}
			}
			
			
			
			writingNow=false;
			bus=0;
			busTag=null;
			
			
			
			
			displayTable();
			clock++;
		}
	}
	
	public static void displayTable()
	{
		System.out.println("Cycle: "+clock);
		System.out.println("instruction	i1		i2		i3	      issue	   execStart	    execEnd		  writeRes");
		for (int i=0;i<instructionQueue.length;i++)
		{
			String instruction=instructionQueue[i].instruction;
			String i1=instructionQueue[i].i1;
			String i2=instructionQueue[i].i2;
			String i3=instructionQueue[i].i3;
			
			String issue="";
			if(instructionQueue[i].issue!=-1)
				issue=""+instructionQueue[i].issue;
			
			String execStart="";
			if(instructionQueue[i].execStart!=-1)
				execStart=""+instructionQueue[i].execStart;
			
			String execEnd="";
			if(instructionQueue[i].execEnd!=-1)
				execEnd=""+instructionQueue[i].execEnd;
			
			String writeRes="";
			if(instructionQueue[i].writeResult!=-1)
				writeRes=""+instructionQueue[i].writeResult;
			
			System.out.println(instruction+"		"+i1+"		"+i2+"		"+i3+"		"+issue+"		"+execStart+"		"+execEnd+"			"+writeRes);
		}
		System.out.println();
		System.out.println();
		
		System.out.println("AddSub RS:");
		System.out.println("busy       op  	        VJ	        Vk		Qj              Qk	       time");
		for(int i=0;i<addSubRS.length;i++)
		{
			int busy=addSubRS[i].busy;
			String op=addSubRS[i].op;
			String Qj=addSubRS[i].Qj;
			String Qk=addSubRS[i].Qk;
			
			String Vj="";
			if(Qj.equals(""))
				Vj=addSubRS[i].Vj+"";
			
			String Vk="";
			if(Qk.equals(""))
				Vk=addSubRS[i].Vk+"";
			
			int time=addSubRS[i].time;
			
			if(busy==1)
				System.out.println(busy+"	  "+op+"		"+Vj+"		"+Vk+"		"+Qj+"		"+Qk+"		"+time);
			else
				System.out.println(busy);
		}
		System.out.println();
		System.out.println();
		
		System.out.println("MulDiv RS:");
		System.out.println("busy       op  	        VJ	        Vk		Qj              Qk	       time");
		for(int i=0;i<mulDivRS.length;i++)
		{
			int busy=mulDivRS[i].busy;
			String op=mulDivRS[i].op;
			String Qj=mulDivRS[i].Qj;
			String Qk=mulDivRS[i].Qk;
			
			String Vj="";
			if(Qj.equals(""))
				Vj=mulDivRS[i].Vj+"";
			
			String Vk="";
			if(Qk.equals(""))
				Vk=mulDivRS[i].Vk+"";
			
			int time=mulDivRS[i].time;
			
			if(busy==1)
				System.out.println(busy+"	  "+op+"		"+Vj+"		"+Vk+"		"+Qj+"		"+Qk+"		"+time);
			else
				System.out.println(busy);
		}
		System.out.println();
		System.out.println();
		
		System.out.println("Load buffers:");
		System.out.println("busy   address        time");
		for(int i=0;i<loadBuffers.length;i++)
		{
			int busy=loadBuffers[i].busy;
			int address=loadBuffers[i].address;
			int time=loadBuffers[i].time;
			
			if(busy==1)
				System.out.println(busy+"	"+address+"		"+time);
			else
				System.out.println(busy);
		}
		System.out.println();
		System.out.println();
		
		System.out.println("Store buffers:");
		System.out.println("busy     V      Q     address         time");
		for(int i=0;i<storeBuffers.length;i++)
		{
			int busy=storeBuffers[i].busy;
			int address=storeBuffers[i].address;
			int time=storeBuffers[i].time;
			
			String Q=storeBuffers[i].Q;
			
			String V="";
			if(Q.equals(""))
				V=storeBuffers[i].V+"";
			
			if(busy==1)
				System.out.println(busy+"	"+V+"	"+Q+"	"+address+"		"+time);
			else
				System.out.println(busy);
		}
		System.out.println();
		System.out.println();
		
		System.out.println("Register File");
		for(int i=0;i<registerFile.file.length;i++)
		{
			String regName=registerFile.file[i].name;
			String Q=registerFile.file[i].Q;
			
			String V="";
			if(Q.equals(""))
				V=registerFile.file[i].value+"";
			
			System.out.println(regName+"	"+V+"	"+Q);
		}
		
		System.out.println("-------------------------------------------------------------------------------------------------------------------------------");
	}
	
	public static void displayMemory()
	{
		for(int i=0;i<memory.memoryValues.length;i++)
		{
			System.out.println("Mem["+i+"]="+memory.memoryValues[i]);
		}
	}

	public static void main(String[] args) throws IOException {
		Scanner sc=new Scanner(System.in);
		
		//filling the instruction queue
		//List<String> programFile = Files.readAllLines(Paths.get("test1.txt"));
		//List<String> programFile = Files.readAllLines(Paths.get("test2.txt"));
		//List<String> programFile = Files.readAllLines(Paths.get("test3.txt"));
		List<String> programFile = Files.readAllLines(Paths.get("test4.txt"));
		instructionQueue = new Instruction[programFile.size()];
		parser(programFile);
		for (int j = 0; j < instructionQueue.length; j++)
			instructionQueue[j].display();
		
		//taking number of reservation stations and buffers
		System.out.println("Please enter number of add/sub reservation stations:");
		int addSubNo=sc.nextInt();
		System.out.println("Please enter number of mul/div reservation stations:");
		int mulDivNo=sc.nextInt();
		System.out.println("Please enter number of load buffers:");
		int loadNo=sc.nextInt();
		System.out.println("Please enter number of store buffers:");
		int storeNo=sc.nextInt();
		
		//initializing buffers and reservation stations
		addSubRS=new ReservationStation[addSubNo];
		for(int i=0;i<addSubNo;i++)
			addSubRS[i]=new ReservationStation();
		
		mulDivRS=new ReservationStation[mulDivNo];
		for(int i=0;i<mulDivNo;i++)
			mulDivRS[i]=new ReservationStation();
		
		loadBuffers=new LoadBuffer[loadNo];
		for(int i=0;i<loadNo;i++)
			loadBuffers[i]=new LoadBuffer();
		
		storeBuffers=new StoreBuffer[storeNo];
		for(int i=0;i<storeNo;i++)
			storeBuffers[i]=new StoreBuffer();
		
		//taking instruction latencies
		System.out.println("Please enter addition latency:");
		addLatency=sc.nextInt();
		System.out.println("Please enter subtraction latency:");
		subLatency=sc.nextInt();
		System.out.println("Please enter multiplication latency:");
		mulLatency=sc.nextInt();
		System.out.println("Please enter division latency:");
		divLatency=sc.nextInt();
		System.out.println("Please enter load latency:");
		loadLatency=sc.nextInt();
		System.out.println("Please enter store latency:");
		storeLatency=sc.nextInt();
		
		start();
		//System.out.println("Memory of 50: "+memory.memoryValues[50]);
		//System.out.println("Memory of 51: "+memory.memoryValues[51]);
		//System.out.println("Memory of 52: "+memory.memoryValues[52]);
		displayMemory();
	}

}