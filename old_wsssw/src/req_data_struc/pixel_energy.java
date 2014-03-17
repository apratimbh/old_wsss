package req_data_struc;

public class pixel_energy extends pixel {
	public double[] class_prob=null;
	public int label=0;
	public double total_energy=0;
	public pixel_energy(int x,int y) {
		super(x,y);
	}
	public pixel_energy(int x,int y,double[] class_prob,int label,double total_energy) {
		super(x,y);
		this.class_prob=class_prob;
		this.label=label;
		this.total_energy=total_energy;
	}
}
