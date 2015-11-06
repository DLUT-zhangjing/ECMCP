import java.util.ArrayList;


/***
 * ���󼯺ϳ�����
 * @author Administrator
 *
 */
public abstract class ObjectSet {
	private char[] objects;//���󼯺ϵĶ������ַ���
	private ArrayList<String> region=new ArrayList<String>();//������
	
	/**
	 * ���캯��
	 * @param num ��������
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
	 * ����i��������ӵ��ü�����
	 * @param i
	 */
	public void addObject(int i){
		this.objects[i]='1';
	}
	/**
	 * �����obj���в�����
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
	 * �����obj���н�����
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
	 * �����obj���в�����
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
	 * ���ظö����еĶ������
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

