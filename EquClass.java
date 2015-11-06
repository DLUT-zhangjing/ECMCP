import java.util.ArrayList;


/***
 * 等价类
 * @author Administrator
 *
 */
public class EquClass extends ObjectSet{

	private String classname;//等价类类别
	/**
	 * 构造函数
	 * @param num对象总数
	 * @param name等价类的类别
	 */
	public EquClass(int num,ArrayList<String> region,String name) {
		super(num,region);//region为“CLASS”
		classname=name;
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
}

