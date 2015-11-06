import java.util.ArrayList;


/***
 * 正域类
 * @author Administrator
 *
 */
public class PosRegion extends ObjectSet{

	private String setregion;//相当于POSc(D)中的D，该字符串为等价类的类别名或者Decision表示全部
	/**
	 * 构造函数
	 * @param num对象总数
	 * @param setr
	 */
	public PosRegion(){}
	public PosRegion(int num,ArrayList<String> region,String setr) {
		super(num,region);//region为该正域关联的属性（或pathway标号）列表
		setregion=setr;
	}

	public String getSetregion() {
		return setregion;
	}

	public void setSetregion(String setregion) {
		this.setregion = setregion;
	}
	/**
	 * 判断此正域与正域pr是否相等
	 * @param pr待比较的正域
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
