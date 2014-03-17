package req_data_struc;

import java.awt.image.BufferedImage;
import java.io.Serializable;

public class bundle implements Serializable {
	public BufferedImage img=null;
	public int[][] map=null;
	boolean[][] needed_map=null;
	public bundle(BufferedImage img,int[][] map,boolean[][] needed_map) {
		this.img=img;
		this.map=map;
		this.needed_map=needed_map;
	}
}