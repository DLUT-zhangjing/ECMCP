import java.util.ArrayList;


/***
 * ������
 * @author Administrator
 *
 */
public class PosRegion extends ObjectSet{

	private String setregion;//�൱��POSc(D)�е�D�����ַ���Ϊ�ȼ�������������Decision��ʾȫ��
	/**
	 * ���캯��
	 * @param num��������
	 * @param setr
	 */
	public PosRegion(){}
	public PosRegion(int num,ArrayList<String> region,String setr) {
		super(num,region);//regionΪ��������������ԣ���pathway��ţ��б�
		setregion=setr;
	}

	public String getSetregion() {
		return setregion;
	}

	public void setSetregion(String setregion) {
		this.setregion = setregion;
	}
	/**
	 * �жϴ�����������pr�Ƿ����
	 * @param pr���Ƚϵ�����
	 * @return
	 */
	public boolean is_equals(PosRegion pr){
		
		char[] pr_objects=pr.getObjects();
		for(int i=0;i<pr_objects.length;i++){
			if(pr_objects[i]!=super.getObjects()[i]){
//				System.out.println("j");
				return false;
			}
		}
		return true;
	}
}
