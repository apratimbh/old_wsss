package kmeans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import req_data_struc.super_pixel;

public class kmeans_algo {
	
	public ArrayList<super_pixel> do_kmeans(ArrayList<super_pixel> super_pixel_list,int num)
	{
		ArrayList<super_pixel> centers=new ArrayList<super_pixel>();
		// get random centers
		Random r=new Random();
		for(int i=0;i<num;i++)
		{
			super_pixel tmp_cnt=super_pixel_list.get(Math.abs(r.nextInt())%super_pixel_list.size());
			if(!check_if_present(centers,tmp_cnt))
				centers.add(tmp_cnt);
			else
				i--;
		}
		System.out.println("Choosen centers: ");
		for(super_pixel cnt_sp: centers)
		{
			cnt_sp.print_resp();
		}
		// -------------
		System.out.println("Centers list size: "+centers.size());
		HashMap<super_pixel,Integer> assigned_cluster=new HashMap<super_pixel,Integer>();
		for(super_pixel sp:super_pixel_list)
		{
			assigned_cluster.put(sp, -1);
		}
		while(get_assignment( assigned_cluster, super_pixel_list,centers))
		{
			System.out.println("Centers list size: "+centers.size());
			get_new_centers( assigned_cluster,super_pixel_list,centers);
			System.out.println("Centers list size: "+centers.size());
		}
		return  centers;
	}

	public boolean check_if_present(ArrayList<super_pixel> centers,super_pixel sp)
	{
		for(super_pixel cnt_sp: centers)
		{
			if(equals(cnt_sp,sp))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean equals(super_pixel sp1,super_pixel sp2)
	{
		boolean flag=true;
		for(int i=0;i<sp1.get_size();i++)
		{
			if(sp1.get(i)!=sp2.get(i))
			{
				flag=false;
			}
		}
		return flag;
	}
	
	public void get_new_centers(HashMap<super_pixel,Integer> assigned_cluster,ArrayList<super_pixel> super_pixel_list,ArrayList<super_pixel> centers)
	{
		int num=centers.size();
		centers.clear();
		int[] num_ass_to_cluster=new int[num];
		for(int i=0;i<num;i++)
		{
			centers.add(new super_pixel(super_pixel_list.get(0).pixel_data.length));
		}
		for(super_pixel sp:super_pixel_list)
		{
			num_ass_to_cluster[assigned_cluster.get(sp)]++;
			add_response(centers.get(assigned_cluster.get(sp)),sp);
		}
		for(super_pixel sp:centers)
		{
			divide_response(sp,num_ass_to_cluster[centers.indexOf(sp)]);
		}
	}

	public void add_response(super_pixel r1,super_pixel r2)
	{
		for(int i=0;i<r1.pixel_data.length;i++)
		{
			r1.pixel_data[i]+=r2.pixel_data[i];
		}
	}

	public void divide_response(super_pixel r1,double d)
	{
		for(int i=0;i<r1.pixel_data.length;i++)
		{
			r1.pixel_data[i]/=d;
		}
	}
	
	public boolean get_assignment(HashMap<super_pixel,Integer> assigned_cluster,ArrayList<super_pixel> super_pixel_list,ArrayList<super_pixel> centers)
	{
		int cnt=0;
		for(super_pixel sp:super_pixel_list)
		{
			double min=compute_distance(sp,centers.get(0));
			int center_idx=0;
			for(int i=1;i<centers.size();i++)
			{
				double tmp=compute_distance(sp,centers.get(i));
				if(tmp<min)
				{
					min=tmp;
					center_idx=i;
				}
			}
			if(assigned_cluster.get(sp)!=center_idx)
			{
				cnt++;
				assigned_cluster.put(sp, center_idx);
			}
		}
		if(((double)cnt/super_pixel_list.size())>0.05)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public double compute_distance(super_pixel r1,super_pixel r2)
	{
		double sum=0;
		if(r1.pixel_data.length!=r2.pixel_data.length)
		{
			System.out.println("ERROR");
			System.exit(0);
		}
		for(int i=0;i<r1.pixel_data.length;i++)
		{
			sum+=Math.pow((r1.pixel_data[i]-r2.pixel_data[i]), 2);
		}
		return Math.sqrt(sum);
	}


}
