package misc;

public class test {
	
	public static void main(String[] args)
	{
		test t=new test();
		t.main();
	}
	
	public void main()
	{
		int x=9;
		change(x);
		System.out.println(x);
	}
	
	public void change(int x)
	{
		x=10;
	}
	
}
