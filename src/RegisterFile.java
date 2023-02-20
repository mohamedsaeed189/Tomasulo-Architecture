
public class RegisterFile {

	Register[] file=new Register[32];
	
	public RegisterFile()
	{
		file=new Register[32];
		for(int i=0;i<32;i++)
			file[i]=new Register("F"+i);
		
		file[1].value=3.5;
		file[2].value=4.8;
		file[3].value=15;
		file[4].value=3;
		file[5].value=2;
		file[6].value=18;
		file[7].value=12;
		file[8].value=10;
		file[9].value=18;
		file[10].value=10;
		file[11].value=20;
		file[12].value=30;
		file[13].value=23;
		file[14].value=25;
		file[15].value=11;
	}
	
	public boolean isOperandAvailable(String regName)
	{
		int regNo;
		if(regName.length()==2)
			regNo=Integer.parseInt(regName.charAt(1)+"");
		else 
			regNo=Integer.parseInt(regName.charAt(1)+""+regName.charAt(2));
		if(file[regNo].Q.equals(""))
			return true;
		else
			return false;
	}
	
	public double getValue(String regName)
	{
		int regNo;
		if(regName.length()==2)
			regNo=Integer.parseInt(regName.charAt(1)+"");
		else
			regNo=Integer.parseInt(regName.charAt(1)+""+regName.charAt(2));
		return file[regNo].value;
	}
	
	public String getQ(String regName)
	{
		int regNo;
		if(regName.length()==2)
			regNo=Integer.parseInt(regName.charAt(1)+"");
		else
			regNo=Integer.parseInt(regName.charAt(1)+""+regName.charAt(2));
		return file[regNo].Q;
	}
	
	public void setQ(String tag,String regName)
	{
		int regNo;
		if(regName.length()==2)
			regNo=Integer.parseInt(regName.charAt(1)+"");
		else
			regNo=Integer.parseInt(regName.charAt(1)+""+regName.charAt(2));
		file[regNo].Q=tag;
	}
	
	public void setValue(double value,String regName)
	{
		int regNo;
		if(regName.length()==2)
			regNo=Integer.parseInt(regName.charAt(1)+"");
		else
			regNo=Integer.parseInt(regName.charAt(1)+""+regName.charAt(2));
		file[regNo].value=value;
		file[regNo].Q="";
	}
}
