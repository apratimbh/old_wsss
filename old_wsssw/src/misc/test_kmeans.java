package misc;

import java.util.ArrayList;

import req_data_struc.super_pixel;
import kmeans.kmeans_algo;

public class test_kmeans {

	public static void main(String[] args)
	{
		test_kmeans t=new test_kmeans();
		t.main();
	}
	
	public void main()
	{
		ArrayList<super_pixel> sp_list=new ArrayList<super_pixel>();
		super_pixel sp1=new super_pixel(2);
		sp1.add(0.2, 0);
		sp1.add(0.5, 1);
		sp_list.add(sp1);
		
		super_pixel sp2=new super_pixel(2);
		sp2.add(0.5, 0);
		sp2.add(0.8, 1);
		sp_list.add(sp2);
		super_pixel sp3=new super_pixel(2);
		sp3.add(0.1, 0);
		sp3.add(0.9, 1);
		sp_list.add(sp3);
		super_pixel sp4=new super_pixel(2);
		sp4.add(0.7, 0);
		sp4.add(0.3, 1);
		sp_list.add(sp4);
		super_pixel sp5=new super_pixel(2);
		sp5.add(0.4, 0);
		sp5.add(0.2, 1);
		sp_list.add(sp5);
		super_pixel sp6=new super_pixel(2);
		sp6.add(5.2, 0);
		sp6.add(5.5, 1);
		sp_list.add(sp6);
		super_pixel sp7=new super_pixel(2);
		sp7.add(5.8, 0);
		sp7.add(5.2, 1);
		sp_list.add(sp7);
		super_pixel sp8=new super_pixel(2);
		sp8.add(5.3, 0);
		sp8.add(5.7, 1);
		sp_list.add(sp8);
		super_pixel sp9=new super_pixel(2);
		sp9.add(5.4, 0);
		sp9.add(5.5, 1);
		sp_list.add(sp9);
		//-----------
		System.out.println("Sp1: ");
		sp1.print_resp();
		kmeans_algo km=new kmeans_algo();
		ArrayList<super_pixel> center_list=km.do_kmeans(sp_list, 2);
		for(super_pixel sp:center_list)
		{
			sp.print_resp();
		}
	}
}
