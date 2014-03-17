package req_data_struc;

public class super_pixel {
	
public double[] pixel_data=null;
int size=0;
	
	public super_pixel(int size)
	{
		pixel_data=new double[size];
		this.size=size;
	}
	
	public void print_resp()
	{
		System.out.println("\nResponse matrix> ");
		for(int i=0;i<pixel_data.length;i++)
		{
				System.out.print(pixel_data[i]+" ");
		}
		System.out.println();
	}
	
	public void add_full_resp(double[] resp)
	{
		for(int i=0;i<resp.length;i++)
		{
			pixel_data[i]=resp[i];
		}
	}
	
	public void add(double value,int i)
	{
		pixel_data[i]=value;
	}
	
	public double get(int i)
	{
		return pixel_data[i];
	}
	
	public int get_size()
	{
		return size;
	}

}
