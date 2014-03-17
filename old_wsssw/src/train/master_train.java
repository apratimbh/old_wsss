package train;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;




import kmeans.kmeans_algo;
import req_data_struc.histogram;
import req_data_struc.neighbourhood;
import req_data_struc.super_pixel;
import req_data_struc.train_instance;
import test.master_test;



public class master_train {
	
	public static void main(String[] args)
	{
		master_train tr=new master_train();
		tr.main("E:/mp-images/","bmp","train.arff", 12,32);
	}

	public void main(String folder,String type,String train_file,int num_of_train_images,int num_of_clusters){
		ArrayList<super_pixel> sp_list=new ArrayList<super_pixel>();
		try 
		{
			for(int i=1;i<=num_of_train_images;i++)
			{
				BufferedImage  org_image=load_image(folder+"org/"+i+"."+type);
				add_super_pixels_of_image_to_list(org_image,sp_list);
			}
			System.out.println("List size: "+sp_list.size());
			kmeans_algo kmeans=new kmeans_algo();
			ArrayList<super_pixel> cluster_center_list=kmeans.do_kmeans(sp_list,num_of_clusters);
			ArrayList<train_instance> train_inst_list=new ArrayList<train_instance>();
			HashMap<Integer,Integer> map_color_to_class=new HashMap<Integer,Integer>();
			int max_class_idx=0;
			for(int i=1;i<=num_of_train_images;i++)
			{
				BufferedImage  org_image=load_image(folder+"org/"+i+"."+type);
				BufferedImage  seg_image=load_image(folder+"seg/"+i+"."+type);
				int[][] pixel_to_cluster=assign_pixel_to_clusters(org_image,cluster_center_list);
				max_class_idx=add_hist_to_train_data(org_image,seg_image,pixel_to_cluster,num_of_clusters,map_color_to_class,train_inst_list,max_class_idx,2);
			}
			 System.out.println(train_inst_list.size());
			 BufferedWriter bw=new BufferedWriter(new FileWriter(train_file));
				bw.write("@Relation 'gabor_dft'\n");
				for(int i=0;i<num_of_clusters;i++)
				{

						bw.write("@Attribute cluster"+i+" numeric\n");
				}
				String tmp1="{";
				for(int i=0;i<max_class_idx;i++)
				{
					tmp1+=i+",";
				}
				tmp1=tmp1.substring(0,tmp1.length()-1)+"}";
				bw.write("@Attribute class "+tmp1+" \n");
				bw.write("@Data\n");
				for(train_instance tr:train_inst_list)
				{
					String tmp2="";
					histogram ht=tr.get_hist();
					for(int i=0;i<ht.size();i++)
					{
						tmp2+=ht.get(i)+",";
					}
					tmp2+=tr.get_class();
					bw.write(tmp2+"\n");
				}
				bw.close();
				/*master_test m_test=new master_test();
				m_test.main(folder, type, train_file, num_of_train_images, 30, cluster_center_list, max_class_idx,map_color_to_class);*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public int add_hist_to_train_data(BufferedImage org_image,BufferedImage seg_image,int[][] pixel_to_cluster,int num_of_clusters,HashMap<Integer,Integer> map_color_to_class,ArrayList<train_instance> train_inst_list,int max_class_idx,int hist_size)
	{
		for(int i=0;i<org_image.getHeight();i+=4)
		{
			for(int j=0;j<org_image.getWidth();j+=4)
			{
				int curr_class_idx=0;
				if(map_color_to_class.containsKey(seg_image.getRGB(j, i)))
				{
					curr_class_idx=map_color_to_class.get(seg_image.getRGB(j, i));
				}
				else
				{
					map_color_to_class.put(seg_image.getRGB(j, i), max_class_idx);
					curr_class_idx=max_class_idx;
					max_class_idx++;
				}
				histogram ht=get_histogram(org_image,pixel_to_cluster,i,j,hist_size,num_of_clusters);
				train_instance tmp_tist=new train_instance(ht,curr_class_idx);
				train_inst_list.add(tmp_tist);
			}
		}
		return max_class_idx;
	}
	
	public histogram get_histogram(BufferedImage img,int[][] pixel_to_cluster,int x,int y,int size,int num_of_clusters)
	{
		neighbourhood nh=get_neighbourhood(img,x,y,size);
		int[] num_in_cluster=new int[num_of_clusters];
		histogram ht=new histogram(num_of_clusters);
		int cnt=0;
		for(int i=nh.x1;i<=nh.x2;i++)
		{
			for(int j=nh.y1;j<=nh.y2;j++)
			{
				num_in_cluster[pixel_to_cluster[i][j]]++;
				cnt++;
			}
		}
		for(int i=0;i<num_in_cluster.length;i++)
		{
			ht.add((double)num_in_cluster[i]/cnt, i);
		}
		//histogram ht=new histogram();
		return ht;
	}
	
	public int[][] assign_pixel_to_clusters(BufferedImage img,ArrayList<super_pixel> centers)
	{
		int[][] pixel_to_cluster=new int[img.getHeight()][img.getWidth()];
		for(int i=0;i<img.getHeight();i++)
		{
			for(int j=0;j<img.getWidth();j++)
			{
				neighbourhood tmp_nh=get_neighbourhood(img,i,j,1);
				super_pixel tmp_sp=get_super_pixel(img,tmp_nh);
				double min=compute_distance(tmp_sp,centers.get(0));
				int center_idx=0;
				for(int k=1;k<centers.size();k++)
				{
					double tmp=compute_distance(tmp_sp,centers.get(k));
					if(tmp<min)
					{
						min=tmp;
						center_idx=k;
					}
				}
				pixel_to_cluster[i][j]=center_idx;
			}
		}
		return pixel_to_cluster;
	}
	
	public void add_super_pixels_of_image_to_list(BufferedImage img,ArrayList<super_pixel> sp_list)
	{
		for(int i=0;i<img.getHeight();i+=2)
		{
			for(int j=0;j<img.getWidth();j+=2)
			{
				neighbourhood tmp_nh=get_neighbourhood(img,i,j,1);
				sp_list.add(get_super_pixel(img,tmp_nh));
			}
		}
	}
	
	public super_pixel get_super_pixel(BufferedImage img,neighbourhood nh)
	{
		int c=0;
		super_pixel sp=new super_pixel(9*3);
		for(int i=nh.x1;i<=nh.x2;i++)
		{
			for(int j=nh.y1;j<=nh.y2;j++)
			{
				try {
				Color c_tmp=get_color(img,j,i);
				sp.add(c_tmp.getRed(), c++);
				sp.add(c_tmp.getBlue(), c++);
				sp.add(c_tmp.getGreen(), c++);
				}
				catch(Exception e)
				{
					System.out.println("nh: x1:"+nh.x1+" y1:"+nh.y1+" x2:"+nh.x2+" y2:"+nh.y2);
					System.exit(0);
				}
			}
		}
		return sp;
	}
	
	public Color get_color(BufferedImage image,int x,int y) {
		int rgb = image.getRGB(x,y);
		Color c = new Color(rgb);
		return c;
	}
	
	public neighbourhood get_neighbourhood(BufferedImage img,int i,int j,int size)
	{
		int x1,y1,x2,y2;
		if(i<size)
		{
			x1=0;x2=2*size;
		}
		else if((img.getHeight()-i)<=size)
		{
			x1=img.getHeight()-2*size-1;
			x2=img.getHeight()-1;
		}
		else
		{
			x1=i-size;x2=i+size;
		}
		if(j<size)
		{
			y1=0;y2=2*size;
		}
		else if((img.getWidth()-j)<=size)
		{
			y1=img.getWidth()-2*size-1;
			y2=img.getWidth()-1;
		}
		else
		{
			y1=j-size;y2=j+size;
		}
		neighbourhood tmp=new neighbourhood(x1,y1,x2,y2);
		return tmp;
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

	public BufferedImage load_image(String file)
	{
		BufferedImage img=null;
		try 
		{
			img = ImageIO.read(new File(file)); 
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return img;
	}

}
