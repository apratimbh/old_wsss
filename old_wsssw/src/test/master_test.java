package test;

import graph.edge;
import graph.vertex;

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

import FordFulkerson.FlowNetwork;
import FordFulkerson.FordFulkerson;
import kmeans.kmeans_algo;
import req_data_struc.bundle;
import req_data_struc.histogram;
import req_data_struc.neighbourhood;
import req_data_struc.pixel_energy;
import req_data_struc.super_pixel;
import req_data_struc.super_pixel_bundle;
import req_data_struc.train_instance;
import wekaCode.classify;

public class master_test {
	
	public void main(String folder,String type,String train_arff_file,int num_of_train_images,int num_of_test_images,ArrayList<super_pixel> cluster_centers,int max_class_index,HashMap<Integer,Integer> map_color_to_class)
	{
		try 
		{
			for(int i=num_of_train_images+1;i<=num_of_test_images;i++)
			{
				BufferedImage  org_image=load_image(folder+"org/"+i+"."+type);
				BufferedImage  seg_image=load_image(folder+"seg/"+i+"."+type);
				bundle tmp_bn=create_bundle(org_image,max_class_index);
				 int[][] pixel_to_cluster=assign_pixel_to_clusters(org_image,cluster_centers);
				 new_min_energy nme=new new_min_energy();
				 super_pixel_bundle spbn=nme.create_bundle(org_image, seg_image, map_color_to_class, max_class_index, pixel_to_cluster);
				 super_pixel_bundle classified_bn=nme.calc_min(spbn,max_class_index,cluster_centers.size(),train_arff_file);
				 nme.save_image(classified_bn,folder+"org/"+i+"-m");
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	public bundle create_bundle(BufferedImage img,int num_classes)
	{
		Random random = new Random();
		int[][] map=new int[img.getHeight()][img.getWidth()];
		int [] max=new int[num_classes];
		for(int i=0;i<img.getHeight();i++)
		{
			for(int j=0;j<img.getWidth();j++)
			{
				map[i][j]=(Math.abs(random.nextInt())%(num_classes));
				max[map[i][j]]++;
			}	
		}
		return (new bundle(img,map,null));
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
	// should not use neighbourhood as method argument
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
	
	public boolean is_adjacent(vertex v1,vertex v2)
	{
		if(Math.abs(v1.x-v2.x)<=1&&Math.abs(v1.y-v2.y)<=1&&(v1.x!=v2.x&&v1.y!=v2.y))
		{
			return true;
		}
		return false;
	}
	
	
	public double calc_simi(BufferedImage img,pixel_energy x,pixel_energy y) // low if different high if same
	{
		double diff=0;
		super_pixel sp1=get_super_pixel(img,get_neighbourhood(img,x.x,x.y,1));
		super_pixel sp2=get_super_pixel(img,get_neighbourhood(img,y.x,y.y,1));
		diff=Math.pow(Math.E,-compute_distance(sp1,sp2));
		if(x.label==y.label)
		{	
			//
		}
		else
		{
			diff=(1-(diff));
		}
		if(Double.isNaN(diff))
		{
			return 0.1;
		}
		return Math.max(diff,0.1);
	}
	
	public double calc_neighbour_energy(BufferedImage img,pixel_energy[][] p_arr,int x,int y) // change if block wise
	{
		double total=0;
		for(int i1=Math.max(x-1,0);i1<=x+1&&i1<p_arr.length;i1++)
		{
			for(int j1=Math.max(y-1,0);j1<=y+1&&j1<p_arr[0].length;j1++)
			{
				if(i1!=x&&j1!=y)
				{
					try {
						total+=calc_simi(img,p_arr[x][y],p_arr[i1][j1]);
					}
					catch(Exception e)
					{
						System.out.println(" x; "+x+" y: "+y+" i1: "+i1+" j1: "+j1);
						e.printStackTrace();
						System.exit(0);
					}
				}
			}
		}
		return (total/8);
	}
	
	public double calc_energy(BufferedImage img,pixel_energy[][] p_arr,int[][] map)
	{
		double total=0;
		for(int i=0;i<img.getHeight();i++)
		{
			for(int j=0;j<img.getWidth();j++)
			{
				p_arr[i][j].label=map[i][j];
				//System.out.println("Class & prob: "+map[i][j]+" "+p_arr[i][j].class_prob[map[i][j]]);
				p_arr[i][j].total_energy=(1-p_arr[i][j].class_prob[map[i][j]]); // class number should start from one
				total+=(1-p_arr[i][j].class_prob[map[i][j]]);
			}
		}
		/*System.out.println("Actual energy: "+total);*/
		for(int i=0;i<map.length;i++)
		{
			for(int j=0;j<map[0].length;j++)
			{
				try {
					double tmp=calc_neighbour_energy(img,p_arr,i,j);
					total+=tmp;
					p_arr[i][j].total_energy+=tmp;
				}
				catch(Exception e)
				{
					System.out.println("map: i: "+i+" j: "+j);
					System.exit(0);
				}
			}
		}
		return total;
	}
	
	public double if_labeled(BufferedImage img,pixel_energy[][] p_arr,int x,int y,int alpha,int beta)
	{
		int c=0;
		double total=0;
		for(int i1=Math.max(x-1,0);i1<=x+1&&i1<p_arr.length;i1++)
		{
			for(int j1=Math.max(y-1,0);j1<=y+1&&j1<p_arr[0].length;j1++)
			{
				if(p_arr[i1][j1].label!=alpha&&p_arr[i1][j1].label!=beta)
				{
					total+=calc_simi(img,p_arr[x][y],p_arr[i1][j1]);
					c++;
				}
			}
		}
		if(c>0)
			return (total/c);
		else
			return 0;
	}
	
	public bundle calc_min(bundle b,int[][] pixel_to_cluster,int no_of_classes,int num_of_clusters,String train_arff_file)
	{
		BufferedImage img=b.img;
		int[][] map=b.map;
		classify logistic_regressor=new classify(train_arff_file);
		pixel_energy[][] p_arr=new pixel_energy[img.getHeight()][img.getWidth()];

		for(int i=0;i<img.getHeight();i++)
		{
			for(int j=0;j<img.getWidth();j++)
			{
				p_arr[i][j]=new pixel_energy(i,j);
				p_arr[i][j].label=map[i][j];
				try {

					histogram curr_hist=get_histogram(img,pixel_to_cluster,i,j,2,num_of_clusters);
					p_arr[i][j].class_prob=logistic_regressor.get_probability(curr_hist.hist_data);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} 	
		}
		System.out.println("Calculating energy");
		double curr_e=calc_energy(img,p_arr,map),prev_e=0;
		System.out.println("Current energy: "+curr_e);
		int success=0;
		double cycle_e=curr_e,pcycle_e=0,tcycle_e=0;
		main_loop: while(true)
		{
			success=0;
			for(int alpha=0;alpha<no_of_classes;alpha++)
			{
				for(int beta=alpha+1;beta<no_of_classes;beta++)
				{
					System.out.println("Alpha: "+alpha+" Beta: "+beta);
					int [] max=new int[no_of_classes];
					for(int i=0;i<img.getHeight();i++)
					{
						for(int j=0;j<img.getWidth();j++)
						{
							max[map[i][j]]++;
						}	
					}
					ArrayList<vertex> v_list=new ArrayList<vertex>();
					ArrayList<edge> e_list=new ArrayList<edge>();
					vertex s=new vertex(-1,-1,0);
					vertex d=new vertex(-1,-1,1);

					int v_count=2;
					// loop
					for(int i1=0;i1<img.getHeight();i1++) // i1 and j1 are the pixel locations
					{
						for(int j1=0;j1<img.getWidth();j1++)
						{
							if(map[i1][j1]==alpha||map[i1][j1]==beta)
							{
								vertex vtmp=new vertex(i1,j1,v_count++); // source is alpha and destination is beta
								v_list.add(vtmp);
								double cap1=((p_arr[i1][j1].class_prob[alpha])+if_labeled(img,p_arr,i1,j1,alpha,beta));
								double cap2=((p_arr[i1][j1].class_prob[beta])+if_labeled(img,p_arr,i1,j1,beta,alpha));
								double cap3=0;
								edge etmp1=new edge(s,vtmp,cap1);
								edge etmp2=new edge(vtmp,d,cap2);
								e_list.add(etmp1);
								e_list.add(etmp2);
								for(vertex v : v_list)
								{
									if(is_adjacent(v,vtmp))
									{
										try {
											cap3=calc_simi(img,p_arr[v.x][v.y],p_arr[vtmp.x][vtmp.y]);
										}
										catch(Exception ee)
										{
											ee.printStackTrace();
										}
										edge etmp3=new edge(v,vtmp,cap3);
										e_list.add(etmp3);
									}
								}
							}
						}
					}
					// end of loop
					v_list.add(s);
					v_list.add(d);
					FlowNetwork G = new FlowNetwork(v_list, e_list);
					FordFulkerson maxflow = new FordFulkerson(G, 0, 1);
					ArrayList<Integer> min_cut=new ArrayList<Integer>();
					for (int v = 0; v < G.V(); v++) 
					{
						if (maxflow.inCut(v)) 
						{
							min_cut.add(v);
						}
					}
					int[][] new_map=new int[map.length][map[0].length];
					for(int i2=0;i2<map.length;i2++)
					{
						for(int j2=0;j2<map[0].length;j2++)
						{
							new_map[i2][j2]=map[i2][j2];
						}
					}
					for(vertex v : v_list)
					{
						if(v.num>1)
						{
							if(min_cut.contains(v.num))
							{
								new_map[v.x][v.y]=alpha;
							}
							else
							{
								new_map[v.x][v.y]=beta;
							}
						}
					}
					prev_e=curr_e;
					tcycle_e=curr_e=calc_energy(img,p_arr,new_map);
					System.out.println("Current energy: "+curr_e);
					if(curr_e>=prev_e)
					{
						curr_e=prev_e;
					}
					else
					{
						success=1;
						for(int i2=0;i2<map.length;i2++)
						{
							for(int j2=0;j2<map[0].length;j2++)
							{
								map[i2][j2]=new_map[i2][j2];
							}
						}
					}
				}
			}
			pcycle_e=cycle_e;
			cycle_e=tcycle_e;
			if(success==0||(((pcycle_e-cycle_e)/pcycle_e)*100)<5)
			{
				break main_loop;
			}
		}
		return b;
	}
	
	public void save_image(bundle b,String file)
	{
		BufferedImage img=b.img;
		int[][] map=b.map;
		int[] color=new int[50];
		Random random = new Random();
		for(int i=0;i<50;i++)
		{
			color[i]=random.nextInt(0xFFFFFF);
		}
		//System.out.println();
		for(int i=0;i<img.getHeight();i++)
		{
			for(int j=0;j<img.getWidth();j++)
			{
				//System.out.print(map[i][j]+" ");
				img.setRGB(j,i, color[map[i][j]]);
			}
			//System.out.println();
		}
		//System.out.println();
		File outputfile = new File(file);
		try {
			ImageIO.write(img, "bmp", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
