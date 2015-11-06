import java.util.ArrayList;

/***
 * 邻域类
 * @author Administrator
 *
 */
public class Neighborhood extends ObjectSet{
	
	private int object;//此邻域的拥有者
	/**
	 * 构造函数
	 * @param num对象总数
	 * @param obj此邻域的拥有者
	 */
	public Neighborhood(int num,ArrayList<String> region,int obj) {
		super(num,region);//region为该邻域所关联的属性（或pathway标号）列表
		this.object=obj;
	}
	
	public int getObject() {
		return object;
	}
	public void setObject(int object) {
		this.object = object;
	}
	/**
	 * 返回该邻域中的对象是否完全包含于objectset对象集中
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
