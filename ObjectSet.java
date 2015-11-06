import java.util.ArrayList;


/***
 * 对象集合抽象类
 * @author Administrator
 *
 */
public abstract class ObjectSet {
	private char[] objects;//对象集合的二进制字符串
	private ArrayList<String> region=new ArrayList<String>();//所属域
	
	/**
	 * 构造函数
	 * @param num 对象总数
	 */
	public ObjectSet(){}
	public ObjectSet(int num,ArrayList<String> r){
		objects=new char[num];
		for(int i=0;i<num;i++){
			objects[i]='0';
		}
		region=r;
	}
	
	public char[] getObjects() {
		return objects;
	}
	public void setObjects(char[] objects) {
		this.objects = objects;
	}
	public ArrayList<String> getRegion() {
		return region;
	}
	public void setRegion(ArrayList<String> region) {
		this.region = region;
	}
	/**
	 * 将第i个对象添加到该集合中
	 * @param i
	 */
	public void addObject(int i){
		this.objects[i]='1';
	}
	/**
	 * 与对象集obj进行并运算
	 * @param obj
	 */
	public void unite(ObjectSet obj){
		char[] temp=obj.getObjects();
		for(int i=0;i<temp.length;i++){
			if(this.objects[i]=='0' && temp[i]=='1'){
				this.objects[i]='1';
			}
		}
	}
	/**
	 * 与对象集obj进行交运算
	 * @param obj
	 */
	public void intersect(ObjectSet obj){
		char[] temp=obj.getObjects();
		for(int i=0;i<temp.length;i++){
			if(this.objects[i]=='1' && temp[i]=='0'){
				this.objects[i]='0';
			}
		}
	}
	/**
	 * 与对象集obj进行差运算
	 * @param obj
	 */
	public void minus(ObjectSet obj){
		char[] temp=obj.getObjects();
		for(int i=0;i<temp.length;i++){
			if(this.objects[i]=='1' && temp[i]=='1'){
				this.objects[i]='0';
			}
		}
	}
	/**
	 * 返回该对象集中的对象个数
	 * @return
	 */
	public int num(){
		int num=0;
		for(int i=0;i<this.objects.length;i++){
			if(this.objects[i]=='1'){
				num++;
			}
		}
		return num;
	}
}

