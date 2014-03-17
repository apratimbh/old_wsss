package req_data_struc;

public class histogram {
	
public double[] hist_data=null;
int size=0;
	
	public histogram(int size)
	{
		hist_data=new double[size];
		this.size=size;
	}
	
	public void print_resp()
	{
		System.out.println("\nResponse matrix> ");
		for(int i=0;i<hist_data.length;i++)
		{
				System.out.print(hist_data[i]+" ");
		}
		System.out.println();
	}
	
	public void add_full_resp(double[] resp)
	{
		for(int i=0;i<resp.length;i++)
		{
			hist_data[i]=resp[i];
		}
	}
	
	public void add(double value,int i)
	{
		hist_data[i]=value;
	}
	
	public double get(int i)
	{
		return hist_data[i];
	}
	
	public int size()
	{
		return size;
	}

}
