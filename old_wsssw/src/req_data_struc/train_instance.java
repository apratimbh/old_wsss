package req_data_struc;

public class train_instance {
	histogram ht=null;
	int class_idx=0;
	public train_instance(histogram ht,int class_idx)
	{
		this.ht=ht;
		this.class_idx=class_idx;
	}
	public histogram get_hist()
	{
		return ht;
	}
	public int get_class()
	{
		return class_idx;
	}
}
