import java.util.ArrayList;


/***
 * �ȼ���
 * @author Administrator
 *
 */
public class EquClass extends ObjectSet{

	private String classname;//�ȼ������
	/**
	 * ���캯��
	 * @param num��������
	 * @param name�ȼ�������
	 */
	public EquClass(int num,ArrayList<String> region,String name) {
		super(num,region);//regionΪ��CLASS��
		classname=name;
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
}

