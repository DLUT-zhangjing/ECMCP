import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;



public class Gene_Select {
	private String pathwayfile;//pathway信息文件
	private String datafile;//用于约简的微阵列数据文件
    private String selectdatafile;
	private int clean_num;//记录clean时的基因个数阈值
	private int length_thr_num;
	private double n_threshold;//邻域阈值
	private int attr_num;//属性个数
	private int object_num;//样本个数
	private ArrayList<String> attr_name_list=new ArrayList<String>();//属性名列表（不含决策属性）
	private ArrayList<String> dec_classname_list=new ArrayList<String>();//类别名列表
	private ArrayList<EquClass> equclasslist=new ArrayList<EquClass>();//等价类列表
	private double[][] max_min_values;//记录每个属性的最大和最小值，min存在max_min_values[][0],max存在max_min_values[][1]
	private Map<String,ArrayList<String>> All_Pathway_Map =new HashMap<String,ArrayList<String>>();//pathway键值对
	private Map<String,ArrayList<String>> Red_Pathway_Map=new HashMap<String,ArrayList<String>>();//粒度约简和属性约简后的pathway键值对
	private Map<String,ArrayList<Neighborhood>> Pathway_NS = new HashMap<String,ArrayList<Neighborhood>>();//每一个pathway标号作为一个关系，记录邻域系统
	private ArrayList<String> p_reduction=new ArrayList<String>();//用于记录约简后的pathway标号
	private char GR_flag;//粒度约简标志，'O'表示只需一个粒度（pathway标号）即可满足全体pathway的正域的情况；'M'表示需要多个
	
	public Gene_Select(String pathwayfilename,String datafilename,String selectdatafilename,double threshold,int cl_num,int lt_num){
		this.pathwayfile=pathwayfilename;
		this.datafile=datafilename;
        this.selectdatafile=selectdatafilename;
		this.n_threshold=threshold;
		this.clean_num=cl_num;
		this.length_thr_num=lt_num;
	}
	
	public int getAttr_num() {
		return attr_num;
	}

	public void setAttr_num(int attr_num) {
		this.attr_num = attr_num;
	}

	public int getObject_num() {
		return object_num;
	}

	public void setObject_num(int object_num) {
		this.object_num = object_num;
	}

	public ArrayList<String> getAttr_name_list() {
		return attr_name_list;
	}

	public void setAttr_name_list(ArrayList<String> attr_name_list) {
		this.attr_name_list = attr_name_list;
	}

	public ArrayList<String> getDec_classname_list() {
		return dec_classname_list;
	}

	public void setDec_classname_list(ArrayList<String> dec_classname_list) {
		this.dec_classname_list = dec_classname_list;
	}

	public ArrayList<EquClass> getEquclasslist() {
		return equclasslist;
	}

	public void setEquclasslist(ArrayList<EquClass> equclasslist) {
		this.equclasslist = equclasslist;
	}

	public double[][] getMax_min_values() {
		return max_min_values;
	}

	public void setMax_min_values(double[][] max_min_values) {
		this.max_min_values = max_min_values;
	}

	public Map<String, ArrayList<String>> getAll_Pathway_Map() {
		return All_Pathway_Map;
	}

	public void setAll_Pathway_Map(Map<String, ArrayList<String>> all_Pathway_Map) {
		All_Pathway_Map = all_Pathway_Map;
	}

	public Map<String, ArrayList<String>> getRed_Pathway_Map() {
		return Red_Pathway_Map;
	}

	public void setRed_Pathway_Map(Map<String, ArrayList<String>> red_Pathway_Map) {
		Red_Pathway_Map = red_Pathway_Map;
	}

	public Map<String, ArrayList<Neighborhood>> getPathway_NS() {
		return Pathway_NS;
	}

	public void setPathway_NS(Map<String, ArrayList<Neighborhood>> pathway_NS) {
		Pathway_NS = pathway_NS;
	}

	public ArrayList<String> getP_reduction() {
		return p_reduction;
	}

	public void setP_reduction(ArrayList<String> p_reduction) {
		this.p_reduction = p_reduction;
	}
	
	public char getGR_flag(){
		return this.GR_flag;
	}
	
	/**
	 * 读第object_id行样本数据，样本从0开始编号
	 * @param object_id 待读取的样本编号
	 * @return 返回读出的数据
	 * @throws Exception
	 */
	public String readdata(String file,int object_id) throws Exception{
		String out=null;
		FileInputStream in=new FileInputStream(file);
		InputStreamReader inReader=new InputStreamReader(in);
		BufferedReader bReader=new BufferedReader(inReader);
		String line=null;
		int flag=0;
		while((line=bReader.readLine())!=null){//找到@DATA行
			if(line!="" && line.substring(0, 5).toUpperCase().equals("@DATA")){
				flag=1;
				break;
			}
		}
		int i=-1;
		if(flag==1){//当读取到"@DATA"退出上一层循环时，说明有数据，读取具体数据
			do{
				i++;
				line=bReader.readLine();
			}while(line!=null && i!=object_id);
			out=line;
		}
		bReader.close();
		inReader.close();
		in.close();
		return out;
	}
	
	/**
	 * 读文件中数据对象个数、属性个数，并将属性名及类别名分别添加到相应的列表中,找到每一个属性的最大及最小取值
	 * @throws Exception
	 */
	public void read_info(String file)throws Exception{
                //清空attr_name_list和dec_classname_list
        Iterator<String> it = attr_name_list.iterator();  
        for(;it.hasNext();) {  
              it.next();  
              it.remove(); 
        }
        Iterator<String> it1 = dec_classname_list.iterator();  
        for(;it1.hasNext();) {  
              it1.next();  
              it1.remove(); 
        }
		FileInputStream in=new FileInputStream(file);
		InputStreamReader inReader=new InputStreamReader(in);
		BufferedReader bReader=new BufferedReader(inReader);
		String line=null;
		int flag=0;
		while((line=bReader.readLine())!=null){
			if(line!=""){
				if(line.substring(0, 5).toUpperCase().equals("@DATA")){//找到@DATA行
					flag=1;
					break;
				}
				else if(line.substring(0, 10).toUpperCase().equals("@ATTRIBUTE")){
					String split[]=line.split(" ");
					if(split.length==3 && !split[1].toUpperCase().equals("CLASS")){//添加属性列表
						attr_name_list.add(split[1]);
					}
					else if(split[1].toUpperCase().equals("CLASS")){//添加类别列表
						String temp=line.substring(18,line.length()-1);
						String split1[]=temp.split(",");
						for(int i=0;i<split1.length;i++){
							split1[i]=split1[i].trim();
						}
						for(int i=0;i<split1.length;i++){
							dec_classname_list.add(split1[i]);
						}
					}
				}
			}
		}
		this.attr_num=attr_name_list.size();//设置属性个数
		if(flag==1){//读数据对象总个数及每个属性下的最大最小值
			int i=0;
			max_min_values=new double[attr_num][2];
			for(int j=0;j<attr_num;j++){//初始化最大最小值
				max_min_values[j][0]=Double.MAX_VALUE;
				max_min_values[j][1]=-Double.MAX_VALUE;
			}
			while((line=bReader.readLine())!=null){
				String split[]=line.split(",");
				for(int j=0;j<split.length-1;j++){
					double temp=Double.parseDouble(split[j]);
					if(max_min_values[j][0]>temp){
						max_min_values[j][0]=temp;
					}
					if(max_min_values[j][1]<temp){
						max_min_values[j][1]=temp;
					}
				}
				i++;
			}
			object_num=i;
		}
		bReader.close();
		inReader.close();
		in.close();
	}
	
    public void get_Pathway_Map(){
		Pathway_Map pathway_map=new Pathway_Map(this.clean_num,this.length_thr_num);
		pathway_map.Run(pathwayfile,datafile);
		All_Pathway_Map=pathway_map.getAll_Pathway_Map();
	}
	
	/**
	 * 获得决策属性Class的所有等价类
	 * @return
	 * @throws Exception
	 */
	public void get_equ_class() throws Exception{
		ArrayList<String> name=new ArrayList<String>();
		name.add("CLASS");
		for(int i=0;i<dec_classname_list.size();i++){//对于类别名列表中的每一个类创建一个初始化的等价类
			EquClass new_equ_class=new EquClass(object_num,name,dec_classname_list.get(i));
			equclasslist.add(new_equ_class);
		}
		//读数据文件，完善等价类列表
		FileInputStream in=new FileInputStream(datafile);
		InputStreamReader inReader=new InputStreamReader(in);
		BufferedReader bReader=new BufferedReader(inReader);
		String line=null;
		int flag=0;
		while((line=bReader.readLine())!=null){//找到@DATA行
			if(line!="" && line.substring(0, 5).toUpperCase().equals("@DATA")){
				flag=1;
				break;
			}
		}
		if(flag==1){
			int i=0;
			while((line=bReader.readLine())!=null){//读数据，根据对象的类别，将对象加入相应的类中
				String split[]=line.split(",");
				for(int j=0;j<equclasslist.size();j++){
					EquClass tempclass=equclasslist.get(j);
					if(split[split.length-1].equals(tempclass.getClassname())){
						tempclass.addObject(i);
					}
					equclasslist.set(j, tempclass);//更新
				}
				i++;
			}
		}
		bReader.close();
		inReader.close();
		in.close();
	}
	/**
	 * 计算欧几里得距离
	 * @param a 对象1
	 * @param b 对象2
	 * @return
	 */
	public double e_distance(double a,double b){
		double distance=0;
			distance=Math.abs(a-b);//求和
		return distance;
	}
//根据pathway最终结果，调整数据文件
	public void print_data() throws Exception{
		//构建存放文件的文件夹，用阈值命名
        Iterator<Entry<String, ArrayList<String>>> keyValuePairs = All_Pathway_Map.entrySet().iterator();
		int all_map_num=All_Pathway_Map.size();
		for(int i=0;i<all_map_num;i++){
			Map.Entry<String, ArrayList<String>> entry=(Map.Entry<String, ArrayList<String>>) keyValuePairs.next();
			String pathway_label=entry.getKey();
		    File in=new File(this.pathwayfile);
		    String path=in.getParent();
		    path=path+"\\data\\";
		    File r=new File(path);
		    if(!r.exists()){
			    r.mkdirs();
		    }
			ArrayList<String> temp_attr_list =this.All_Pathway_Map.get(pathway_label);
			//读文件流
			FileInputStream ins=new FileInputStream(this.datafile);
			InputStreamReader inReader=new InputStreamReader(ins);
			BufferedReader bReader=new BufferedReader(inReader);
			String rline=null;
			//新文件命名
			System.out.println("写"+pathway_label+"文件");
			String newname=pathway_label+".arff";
			
			//写文件流
			File fout = new File(path+newname);	
			BufferedWriter bwriter = new BufferedWriter(new FileWriter(fout,false));	
			//按行读原文件并处理产生新文件
			ArrayList<String> attr=new ArrayList<String>();
			int flag=0;
			while((rline=bReader.readLine())!=null){
				if(rline.substring(0,5).toUpperCase().equals("@DATA")){
					flag=1;
					break;
				}
				else if(rline.substring(0,10).toUpperCase().equals("@ATTRIBUTE")){
					attr.add(rline);
				}
				else{
					bwriter.write(rline+"\r\n");
				}
			}
			if(flag==1){//读到数据标志而退出时的情况
				for(int j=0;j<temp_attr_list.size();j++){//输出选择后的基因描述
					String tempattr=temp_attr_list.get(j);
					int index=attr_name_list.indexOf(tempattr);
                    if(index==-1){
                         continue;
                    }
					bwriter.write(attr.get(index)+"\r\n");
				}
				bwriter.write(attr.get(attr.size()-1)+"\r\n");//输出类别描述
				bwriter.write(rline+"\r\n");//输出@DATA
				while((rline=bReader.readLine())!=null){//调整数据部分
					String outdata=null;
					String split[]=rline.split(",");
					for(int j=0;j<temp_attr_list.size();j++){//输出选择后的基因数据
						String tempattr=temp_attr_list.get(j);
						int index=attr_name_list.indexOf(tempattr);
                        if(index==-1){
                             continue;
                        }
						if(outdata==null){
							outdata=split[index];
						}
						else{
							outdata+=","+split[index];
						}
					}
					outdata+=","+split[split.length-1];
					bwriter.write(outdata+"\r\n");
				}
			}
			bwriter.close();
			bReader.close();
			inReader.close();
			ins.close();
		}
            
	} 
    public ArrayList<Neighborhood> get_distance_neighborhoods(String file,double threshold,ArrayList<String> attr_region) throws Exception{ 
		ArrayList<Neighborhood> neighborhood_list=new ArrayList<Neighborhood>();//邻域列表
		double distance[][][]=new double[object_num][object_num][attr_region.size()];//对象间距离矩阵，是对角线元素为0的对称矩阵

		//根据属性域计算对象间距离，对象个属性下的属性值是0~1标准化后的数
		for(int j=0;j<attr_region.size();j++)
		{
		    for(int i=0;i<object_num;i++){
			   distance[i][i][j]=0.0;
		    }
		}
		for(int i=0;i<object_num;i++){
			String i_string=this.readdata(file,i);
			String i_split[]=i_string.split(",");
			double i_data[]=new double[attr_region.size()];
			for(int pi=0;pi<i_data.length;pi++){
				int i_index=attr_name_list.indexOf(attr_region.get(pi));
                if(i_index==-1){              
                       continue;
                }
				i_data[pi]=Double.parseDouble(i_split[i_index]);
				double di=max_min_values[i_index][1]-max_min_values[i_index][0];
				i_data[pi]=(i_data[pi]-max_min_values[i_index][0])/di;//0~1标准化
			}
			for(int j=i+1;j<object_num;j++){
				String j_string=this.readdata(file,j);
				String j_split[]=j_string.split(",");
				double j_data[]=new double[attr_region.size()];
			        for(int pj=0;pj<j_data.length;pj++){
				        int j_index=attr_name_list.indexOf(attr_region.get(pj));
                        if(j_index==0){
                            continue;
                        }
				        j_data[pj]=Double.parseDouble(j_split[j_index]);
				        double dj=max_min_values[j_index][1]-max_min_values[j_index][0];
				        j_data[pj]=(j_data[pj]-max_min_values[j_index][0])/dj;//0~1标准化
                   }
		        for(int k=0;k<attr_region.size();k++)
				{
		        	distance[i][j][k]=this.e_distance(i_data[k], j_data[k]);
				    distance[j][i][k]=distance[i][j][k];
				}
			}
		}
		
		//根据邻域阈值，获取邻域
		for(int i=0;i<object_num;i++){//为每个对象初始化一个邻域
			Neighborhood n=new Neighborhood(object_num,attr_name_list,i);
			neighborhood_list.add(n);
		}
		for(int i=0;i<neighborhood_list.size();i++){//根据阈值及对象间的距离矩阵，向邻域中添加对象
			Neighborhood temp_neighborhood=neighborhood_list.get(i);
			for(int j=0;j<object_num;j++){
//				System.out.print(distance[i][j]+";");
				for(int k=0;k<attr_region.size();k++){
				    if(distance[i][j][k]>threshold){//根据阈值判断对象是否应该在当前邻域中
					    break;
				    }
				    else{
				    	if(k==(attr_region.size()-1)){
				    	   temp_neighborhood.addObject(j);
				    	}
				    }
				}
			}
			neighborhood_list.set(i, temp_neighborhood);//更新
		}
		
		return neighborhood_list;
	}
	
	//根据邻域阈值，获得每一个pathway关系下每个对象的邻域
    public void create_pathway_ns(double threshold)  throws Exception{
		//遍历所有的pathway标号
        File file=new File(selectdatafile);
        String[] filelist = file.list();
        for (int i = 0; i < filelist.length; i++) {
             this.read_info(selectdatafile+filelist[i]);
             System.out.println("构建"+filelist[i].substring(0,filelist[i].length()-5)+"的邻域");
             ArrayList<Neighborhood> neighborhood_list=get_distance_neighborhoods(selectdatafile+filelist[i],threshold,attr_name_list);//当前pathway标号作为一个关系，求邻域列表
             Pathway_NS.put(filelist[i].substring(0,filelist[i].length()-5), neighborhood_list);
         }
	}
    public PosRegion get_s_region(String file,ArrayList<String> attr_region) throws Exception{
		PosRegion pos_region_D=new PosRegion(object_num,attr_region,"Decision");
		ArrayList<Neighborhood> neighborhood_list=this.get_distance_neighborhoods(file, n_threshold,attr_region);//计算邻域
		
		//计算正域
		for(int i=0;i<equclasslist.size();i++){
			EquClass temp_equ_class=equclasslist.get(i);
			PosRegion pos_region_equ=new PosRegion(object_num,attr_region,temp_equ_class.getClassname());
			char equ_class_objects[]=temp_equ_class.getObjects();
			for(int j=0;j<neighborhood_list.size();j++){
				Neighborhood temp_neighborhood=neighborhood_list.get(j);
				if(temp_neighborhood.is_included_in(equ_class_objects)){//判断对象邻域是否包含于该等价类
					pos_region_equ.addObject(j);//将该对象加入到正域中
				}
			}
			pos_region_D.unite(pos_region_equ);
		}
		return pos_region_D;
	} 
     public PosRegion get_ns_region(EquClass equclass,ArrayList<String> pathway_list){
		PosRegion pos_region=new PosRegion(object_num,pathway_list,equclass.getClassname());//记录正域
		char equ_class_objects[]=equclass.getObjects(); //获得等价类的对象集
		for(int id=0;id<this.object_num;id++){//判断每个对象是否应该在正域中
			for(int j=0;j<pathway_list.size();j++){//遍历每个pathway标号关系下该对象的邻域
				ArrayList<Neighborhood> temp_list=this.Pathway_NS.get(pathway_list.get(j));//获得当前pathway标号下所有对象的邻域
				Neighborhood temp_neighborhood=temp_list.get(id);//获得当前对象在当前pathway标号关系下的邻域
				if(temp_neighborhood.is_included_in(equ_class_objects)){//存在该对象在某一关系下的邻域完全包含于该等价类中。则该对象放入正域中
					 pos_region.addObject(id);
				}
			}
		}
		return pos_region;
	}
	
	//对于约简后的pathway标号列表中的每一个标号进行属性约简
    public void Attr_Reduction() throws Exception{
            File file=new File(selectdatafile);
            String[] filelist = file.list();
            for (int i = 0; i < filelist.length; i++) {
                  read_info(selectdatafile+filelist[i]);
                  String temp_pathway_label=filelist[i].substring(0,filelist[i].length()-5);
                  ArrayList<String> temp_list=new ArrayList<String>();
			      temp_list.add(temp_pathway_label);
		          ArrayList<String> pathway_attr_list=new ArrayList<String>();//获得对应的属性列表
                  for(int j=0;j<attr_name_list.size();j++){
                        pathway_attr_list.add(attr_name_list.get(j));
                  }
			//计算全属性下的正域
			      PosRegion pos_region_all=new PosRegion(object_num,pathway_attr_list,"Decision");
			      for(int j=0;j<equclasslist.size();j++){
				        EquClass temp_equ_class=equclasslist.get(j);
				        PosRegion temp_region=this.get_ns_region(temp_equ_class,temp_list);
				        pos_region_all.unite(temp_region);
			      }
			
			//从后向前不断删减属性并判断是否保持正域不变，得到一个约简
			      int num=pathway_attr_list.size();//记录待约简的属性个数
			      for(int k=num-1;k>=0;k--){//从后向前扫描一遍属性
				       System.out.println(filelist[i]+"文件的第"+(k+1)+"个属性");
				       String temp_str=pathway_attr_list.get(k);
				       pathway_attr_list.remove(k);
				       PosRegion pos_region_red=this.get_s_region(selectdatafile+filelist[i],pathway_attr_list);//计算约简后的正域
                       if(!pos_region_red.is_equals(pos_region_all)||(pathway_attr_list.size()==0)){//判断正域是否相等
					          pathway_attr_list.add(k,temp_str);//不相等时，该属性不可以删除
				       }
			      }
			
			//将约简后的结果记录到Red_Pathway_Map中
			      Red_Pathway_Map.put(filelist[i].substring(0,filelist[i].length()-5), pathway_attr_list);
		   }
	}

    public void MapPrint() throws Exception{
		File dir=new File("E:\\毕业论文(张晶)\\论文实现\\数据1");
		if(!dir.exists()){
			dir.mkdir();
		}
		//遍历Map并输出到文件
		File out = new File("E:\\毕业论文(张晶)\\论文实现\\数据1\\"+"Redution"+n_threshold+"\\Map.txt");	
		BufferedWriter bwriter = new BufferedWriter(new FileWriter(out,false));
		Iterator<Entry<String, ArrayList<String>>> keyValuePairs = Red_Pathway_Map.entrySet().iterator();
		for(int i=0;i<Red_Pathway_Map.size();i++){
			Map.Entry<String, ArrayList<String>> entry=(Map.Entry<String, ArrayList<String>>) keyValuePairs.next();
			String pathway_label=entry.getKey();
			ArrayList<String> gene_list=entry.getValue();
			if(gene_list.size()!=0){
				String temp=pathway_label+"\t";
				for(int j=0;j<gene_list.size()-1;j++){
					temp=temp+gene_list.get(j)+",";
				}
				temp=temp+gene_list.get(gene_list.size()-1);
				if(i==Red_Pathway_Map.size()-1){
					bwriter.write(temp);
				}
				else{
					bwriter.write(temp+"\r\n");
				}
			}			
		}
		bwriter.close();
		
	}    
    public void print_red_data() throws Exception{
		//构建存放文件的文件夹，用阈值命名
           Iterator<Entry<String, ArrayList<String>>> keyValuePairs = Red_Pathway_Map.entrySet().iterator();
		   int all_map_num=Red_Pathway_Map.size();
           read_info(this.datafile);
		   for(int i=0;i<all_map_num;i++){
			    Map.Entry<String, ArrayList<String>> entry=(Map.Entry<String, ArrayList<String>>) keyValuePairs.next();
			    String pathway_label=entry.getKey();
		        File in=new File(this.pathwayfile);
		        String path=in.getParent();
		        path=path+"\\Red_data\\"+n_threshold;
		        File r=new File(path);
		        if(!r.exists()){
			         r.mkdirs();
		        }
			    ArrayList<String> temp_attr_list =this.Red_Pathway_Map.get(pathway_label);
			    //读文件流
			    FileInputStream ins=new FileInputStream(this.datafile);
			    InputStreamReader inReader=new InputStreamReader(ins);
			    BufferedReader bReader=new BufferedReader(inReader);
			    String rline=null;
			    //新文件命名
			    System.out.println("写"+pathway_label+"文件");
			    String newname="\\"+pathway_label+".arff";
			
			    //写文件流
			    File fout = new File(path+newname);	
			    BufferedWriter bwriter = new BufferedWriter(new FileWriter(fout,false));	
			    //按行读原文件并处理产生新文件
			    ArrayList<String> attr=new ArrayList<String>();
			    int flag=0;
			    while((rline=bReader.readLine())!=null){
				    if(rline.substring(0,5).toUpperCase().equals("@DATA")){
					    flag=1;
					    break;
				    }
				    else if(rline.substring(0,10).toUpperCase().equals("@ATTRIBUTE")){
					    attr.add(rline);
				    }
				    else{
					     bwriter.write(rline+"\r\n");
				    }
			    }
			    if(flag==1){//读到数据标志而退出时的情况
				    for(int j=0;j<temp_attr_list.size();j++){//输出选择后的基因描述
					    String tempattr=temp_attr_list.get(j);
					    int index=attr_name_list.indexOf(tempattr);
					    bwriter.write(attr.get(index)+"\r\n");
				    }
				    bwriter.write(attr.get(attr.size()-1)+"\r\n");//输出类别描述
				    bwriter.write(rline+"\r\n");//输出@DATA
				    while((rline=bReader.readLine())!=null){//调整数据部分
					     String outdata=null;
					     String split[]=rline.split(",");
					     for(int j=0;j<temp_attr_list.size();j++){//输出选择后的基因数据
						     String tempattr=temp_attr_list.get(j);
						     int index=attr_name_list.indexOf(tempattr);
						     if(outdata==null){
							      outdata=split[index];
						     }
						     else{
							      outdata+=","+split[index];
						     }
					     }
					     outdata+=","+split[split.length-1];
					     bwriter.write(outdata+"\r\n");
				    }
			}
			bwriter.close();
			bReader.close();
			inReader.close();
			ins.close();
		}
            
	} 
    public void run(){
		try{
			System.out.println("Start!");
			read_info(this.datafile);//读取数据文件信息
			System.out.println("读数据文件信息成功！");
            get_equ_class();
            System.out.println("获得等价类成功！");
			this.get_Pathway_Map();//获得pathway信息
		    this.print_data();//输出整理的数据文件
		    System.out.println("整理数据文件结束");
            this.create_pathway_ns(n_threshold);  
			this.Attr_Reduction();
            System.out.println("属性约简成功！");
            this.print_red_data();
		}
		catch(Exception e){
			System.out.println("error!");
			e.printStackTrace();
		}
		
	}
    /*public static void main(String args[]){
         String pathwayfilename="E:\\毕业论文(张晶)\\论文实现\\数据\\pathwayfile.txt";
         String datafilename="E:\\毕业论文(张晶)\\论文实现\\数据\\ArabidopsisDrought.arff";
         String selectdatafilename="E:\\毕业论文(张晶)\\论文实现\\数据\\data"+n_threshold+"\\";
	     double threshold=0.1;
	     int cl_num=2;
	     int lt_num=20;
		 Gene_Select a = new Gene_Select(pathwayfilename,datafilename,selectdatafilename,threshold,cl_num,lt_num);
		 try{
			
			  a.run();
		 }
		 catch(Exception e){
			  System.out.println("error!");
		 }
	 }*/
}