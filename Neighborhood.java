import java.util.ArrayList;

/***
 * ������
 * @author Administrator
 *
 */
public class Neighborhood extends ObjectSet{
	
	private int object;//�������ӵ����
	/**
	 * ���캯��
	 * @param num��������
	 * @param obj�������ӵ����
	 */
	public Neighborhood(int num,ArrayList<String> region,int obj) {
		super(num,region);//regionΪ�����������������ԣ���pathway��ţ��б�
		this.object=obj;
	}
	
	public int getObject() {
		return object;
	}
	public void setObject(int object) {
		this.object = object;
	}
	/**
	 * ���ظ������еĶ����Ƿ���ȫ������objectset������
	 * @param objectset
	 * @return
	 */
	public boolean is_included_in(char[] objectset){
		for(int i=0;i<objectset.length;i++){
			if(super.getObjects()[i]=='1' && objectset[i]=='0'){
				return false;
			}
		}
		return true;
	}
}
