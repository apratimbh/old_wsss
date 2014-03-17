package req_data_struc;

import java.awt.image.BufferedImage;

public class super_pixel_bundle {
	
	public super_pixel[][] sp_arr=null;
	public int[][] pixel_to_cluster=null;
	public BufferedImage img=null;
	public int[][] map=null;
	public int[][] actual_map=null;
	public super_pixel_bundle(super_pixel[][] sp_arr,BufferedImage img,int[][] map,int[][] actual_map,int[][] pixel_to_cluster)
	{
		this.sp_arr=sp_arr;
		this.img=img;
		this.map=map;
		this.actual_map=actual_map;
		this.pixel_to_cluster=pixel_to_cluster;
	}
}
