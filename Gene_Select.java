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
	private String pathwayfile;//pathway��Ϣ�ļ�
	private String datafile;//����Լ���΢���������ļ�
    private String selectdatafile;
	private int clean_num;//��¼cleanʱ�Ļ��������ֵ
	private int length_thr_num;
	private double n_threshold;//������ֵ
	private int attr_num;//���Ը���
	private int object_num;//��������
	private ArrayList<String> attr_name_list=new ArrayList<String>();//�������б������������ԣ�
	private ArrayList<String> dec_classname_list=new ArrayList<String>();//������б�
	private ArrayList<EquClass> equclasslist=new ArrayList<EquClass>();//�ȼ����б�
	private double[][] max_min_values;//��¼ÿ�����Ե�������Сֵ��min����max_min_values[][0],max����max_min_values[][1]
	private Map<String,ArrayList<String>> All_Pathway_Map =new HashMap<String,ArrayList<String>>();//pathway��ֵ��
	private Map<String,ArrayList<String>> Red_Pathway_Map=new HashMap<String,ArrayList<String>>();//����Լ�������Լ����pathway��ֵ��
	private Map<String,ArrayList<Neighborhood>> Pathway_NS = new HashMap<String,ArrayList<Neighborhood>>();//ÿһ��pathway�����Ϊһ����ϵ����¼����ϵͳ
	private ArrayList<String> p_reduction=new ArrayList<String>();//���ڼ�¼Լ����pathway���
	private char GR_flag;//����Լ���־��'O'��ʾֻ��һ�����ȣ�pathway��ţ���������ȫ��pathway������������'M'��ʾ��Ҫ���
	
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
	 * ����object_id���������ݣ�������0��ʼ���
	 * @param object_id ����ȡ���������
	 * @return ���ض���������
	 * @throws Exception
	 */
	public String readdata(String file,int object_id) throws Exception{
		String out=null;
		FileInputStream in=new FileInputStream(file);
		InputStreamReader inReader=new InputStreamReader(in);
		BufferedReader bReader=new BufferedReader(inReader);
		String line=null;
		int flag=0;
		while((line=bReader.readLine())!=null){//�ҵ�@DATA��
			if(line!="" && line.substring(0, 5).toUpperCase().equals("@DATA")){
				flag=1;
				break;
			}
		}
		int i=-1;
		if(flag==1){//����ȡ��"@DATA"�˳���һ��ѭ��ʱ��˵�������ݣ���ȡ��������
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
	 * ���ļ������ݶ�����������Ը�����������������������ֱ���ӵ���Ӧ���б���,�ҵ�ÿһ�����Ե������Сȡֵ
	 * @throws Exception
	 */
	public void read_info(String file)throws Exception{
                //���attr_name_list��dec_classname_list
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
				if(line.substring(0, 5).toUpperCase().equals("@DATA")){//�ҵ�@DATA��
					flag=1;
					break;
				}
				else if(line.substring(0, 10).toUpperCase().equals("@ATTRIBUTE")){
					String split[]=line.split(" ");
					if(split.length==3 && !split[1].toUpperCase().equals("CLASS")){//��������б�
						attr_name_list.add(split[1]);
					}
					else if(split[1].toUpperCase().equals("CLASS")){//�������б�
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
		this.attr_num=attr_name_list.size();//�������Ը���
		if(flag==1){//�����ݶ����ܸ�����ÿ�������µ������Сֵ
			int i=0;
			max_min_values=new double[attr_num][2];
			for(int j=0;j<attr_num;j++){//��ʼ�������Сֵ
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
	 * ��þ�������Class�����еȼ���
	 * @return
	 * @throws Exception
	 */
	public void get_equ_class() throws Exception{
		ArrayList<String> name=new ArrayList<String>();
		name.add("CLASS");
		for(int i=0;i<dec_classname_list.size();i++){//����������б��е�ÿһ���ഴ��һ����ʼ���ĵȼ���
			EquClass new_equ_class=new EquClass(object_num,name,dec_classname_list.get(i));
			equclasslist.add(new_equ_class);
		}
		//�������ļ������Ƶȼ����б�
		FileInputStream in=new FileInputStream(datafile);
		InputStreamReader inReader=new InputStreamReader(in);
		BufferedReader bReader=new BufferedReader(inReader);
		String line=null;
		int flag=0;
		while((line=bReader.readLine())!=null){//�ҵ�@DATA��
			if(line!="" && line.substring(0, 5).toUpperCase().equals("@DATA")){
				flag=1;
				break;
			}
		}
		if(flag==1){
			int i=0;
			while((line=bReader.readLine())!=null){//�����ݣ����ݶ������𣬽����������Ӧ������
				String split[]=line.split(",");
				for(int j=0;j<equclasslist.size();j++){
					EquClass tempclass=equclasslist.get(j);
					if(split[split.length-1].equals(tempclass.getClassname())){
						tempclass.addObject(i);
					}
					equclasslist.set(j, tempclass);//����
				}
				i++;
			}
		}
		bReader.close();
		inReader.close();
		in.close();
	}
	/**
	 * ����ŷ����þ���
	 * @param a ����1
	 * @param b ����2
	 * @return
	 */
	public double e_distance(double a,double b){
		double distance=0;
			distance=Math.abs(a-b);//���
		return distance;
	}
//����pathway���ս�������������ļ�
	public void print_data() throws Exception{
		//��������ļ����ļ��У�����ֵ����
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
			//���ļ���
			FileInputStream ins=new FileInputStream(this.datafile);
			InputStreamReader inReader=new InputStreamReader(ins);
			BufferedReader bReader=new BufferedReader(inReader);
			String rline=null;
			//���ļ�����
			System.out.println("д"+pathway_label+"�ļ�");
			String newname=pathway_label+".arff";
			
			//д�ļ���
			File fout = new File(path+newname);	
			BufferedWriter bwriter = new BufferedWriter(new FileWriter(fout,false));	
			//���ж�ԭ�ļ�������������ļ�
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
			if(flag==1){//�������ݱ�־���˳�ʱ�����
				for(int j=0;j<temp_attr_list.size();j++){//���ѡ���Ļ�������
					String tempattr=temp_attr_list.get(j);
					int index=attr_name_list.indexOf(tempattr);
                    if(index==-1){
                         continue;
                    }
					bwriter.write(attr.get(index)+"\r\n");
				}
				bwriter.write(attr.get(attr.size()-1)+"\r\n");//����������
				bwriter.write(rline+"\r\n");//���@DATA
				while((rline=bReader.readLine())!=null){//�������ݲ���
					String outdata=null;
					String split[]=rline.split(",");
					for(int j=0;j<temp_attr_list.size();j++){//���ѡ���Ļ�������
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
		ArrayList<Neighborhood> neighborhood_list=new ArrayList<Neighborhood>();//�����б�
		double distance[][][]=new double[object_num][object_num][attr_region.size()];//������������ǶԽ���Ԫ��Ϊ0�ĶԳƾ���

		//�������������������룬����������µ�����ֵ��0~1��׼�������
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
				i_data[pi]=(i_data[pi]-max_min_values[i_index][0])/di;//0~1��׼��
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
				        j_data[pj]=(j_data[pj]-max_min_values[j_index][0])/dj;//0~1��׼��
                   }
		        for(int k=0;k<attr_region.size();k++)
				{
		        	distance[i][j][k]=this.e_distance(i_data[k], j_data[k]);
				    distance[j][i][k]=distance[i][j][k];
				}
			}
		}
		
		//����������ֵ����ȡ����
		for(int i=0;i<object_num;i++){//Ϊÿ�������ʼ��һ������
			Neighborhood n=new Neighborhood(object_num,attr_name_list,i);
			neighborhood_list.add(n);
		}
		for(int i=0;i<neighborhood_list.size();i++){//������ֵ�������ľ����������������Ӷ���
			Neighborhood temp_neighborhood=neighborhood_list.get(i);
			for(int j=0;j<object_num;j++){
//				System.out.print(distance[i][j]+";");
				for(int k=0;k<attr_region.size();k++){
				    if(distance[i][j][k]>threshold){//������ֵ�ж϶����Ƿ�Ӧ���ڵ�ǰ������
					    break;
				    }
				    else{
				    	if(k==(attr_region.size()-1)){
				    	   temp_neighborhood.addObject(j);
				    	}
				    }
				}
			}
			neighborhood_list.set(i, temp_neighborhood);//����
		}
		
		return neighborhood_list;
	}
	
	//����������ֵ�����ÿһ��pathway��ϵ��ÿ�����������
    public void create_pathway_ns(double threshold)  throws Exception{
		//�������е�pathway���
        File file=new File(selectdatafile);
        String[] filelist = file.list();
        for (int i = 0; i < filelist.length; i++) {
             this.read_info(selectdatafile+filelist[i]);
             System.out.println("����"+filelist[i].substring(0,filelist[i].length()-5)+"������");
             ArrayList<Neighborhood> neighborhood_list=get_distance_neighborhoods(selectdatafile+filelist[i],threshold,attr_name_list);//��ǰpathway�����Ϊһ����ϵ���������б�
             Pathway_NS.put(filelist[i].substring(0,filelist[i].length()-5), neighborhood_list);
         }
	}
    public PosRegion get_s_region(String file,ArrayList<String> attr_region) throws Exception{
		PosRegion pos_region_D=new PosRegion(object_num,attr_region,"Decision");
		ArrayList<Neighborhood> neighborhood_list=this.get_distance_neighborhoods(file, n_threshold,attr_region);//��������
		
		//��������
		for(int i=0;i<equclasslist.size();i++){
			EquClass temp_equ_class=equclasslist.get(i);
			PosRegion pos_region_equ=new PosRegion(object_num,attr_region,temp_equ_class.getClassname());
			char equ_class_objects[]=temp_equ_class.getObjects();
			for(int j=0;j<neighborhood_list.size();j++){
				Neighborhood temp_neighborhood=neighborhood_list.get(j);
				if(temp_neighborhood.is_included_in(equ_class_objects)){//�ж϶��������Ƿ�����ڸõȼ���
					pos_region_equ.addObject(j);//���ö�����뵽������
				}
			}
			pos_region_D.unite(pos_region_equ);
		}
		return pos_region_D;
	} 
     public PosRegion get_ns_region(EquClass equclass,ArrayList<String> pathway_list){
		PosRegion pos_region=new PosRegion(object_num,pathway_list,equclass.getClassname());//��¼����
		char equ_class_objects[]=equclass.getObjects(); //��õȼ���Ķ���
		for(int id=0;id<this.object_num;id++){//�ж�ÿ�������Ƿ�Ӧ����������
			for(int j=0;j<pathway_list.size();j++){//����ÿ��pathway��Ź�ϵ�¸ö��������
				ArrayList<Neighborhood> temp_list=this.Pathway_NS.get(pathway_list.get(j));//��õ�ǰpathway��������ж��������
				Neighborhood temp_neighborhood=temp_list.get(id);//��õ�ǰ�����ڵ�ǰpathway��Ź�ϵ�µ�����
				if(temp_neighborhood.is_included_in(equ_class_objects)){//���ڸö�����ĳһ��ϵ�µ�������ȫ�����ڸõȼ����С���ö������������
					 pos_region.addObject(id);
				}
			}
		}
		return pos_region;
	}
	
	//����Լ����pathway����б��е�ÿһ����Ž�������Լ��
    public void Attr_Reduction() throws Exception{
            File file=new File(selectdatafile);
            String[] filelist = file.list();
            for (int i = 0; i < filelist.length; i++) {
                  read_info(selectdatafile+filelist[i]);
                  String temp_pathway_label=filelist[i].substring(0,filelist[i].length()-5);
                  ArrayList<String> temp_list=new ArrayList<String>();
			      temp_list.add(temp_pathway_label);
		          ArrayList<String> pathway_attr_list=new ArrayList<String>();//��ö�Ӧ�������б�
                  for(int j=0;j<attr_name_list.size();j++){
                        pathway_attr_list.add(attr_name_list.get(j));
                  }
			//����ȫ�����µ�����
			      PosRegion pos_region_all=new PosRegion(object_num,pathway_attr_list,"Decision");
			      for(int j=0;j<equclasslist.size();j++){
				        EquClass temp_equ_class=equclasslist.get(j);
				        PosRegion temp_region=this.get_ns_region(temp_equ_class,temp_list);
				        pos_region_all.unite(temp_region);
			      }
			
			//�Ӻ���ǰ����ɾ�����Բ��ж��Ƿ񱣳����򲻱䣬�õ�һ��Լ��
			      int num=pathway_attr_list.size();//��¼��Լ������Ը���
			      for(int k=num-1;k>=0;k--){//�Ӻ���ǰɨ��һ������
				       System.out.println(filelist[i]+"�ļ��ĵ�"+(k+1)+"������");
				       String temp_str=pathway_attr_list.get(k);
				       pathway_attr_list.remove(k);
				       PosRegion pos_region_red=this.get_s_region(selectdatafile+filelist[i],pathway_attr_list);//����Լ��������
                       if(!pos_region_red.is_equals(pos_region_all)||(pathway_attr_list.size()==0)){//�ж������Ƿ����
					          pathway_attr_list.add(k,temp_str);//�����ʱ�������Բ�����ɾ��
				       }
			      }
			
			//��Լ���Ľ����¼��Red_Pathway_Map��
			      Red_Pathway_Map.put(filelist[i].substring(0,filelist[i].length()-5), pathway_attr_list);
		   }
	}

    public void MapPrint() throws Exception{
		File dir=new File("E:\\��ҵ����(�ž�)\\����ʵ��\\����1");
		if(!dir.exists()){
			dir.mkdir();
		}
		//����Map��������ļ�
		File out = new File("E:\\��ҵ����(�ž�)\\����ʵ��\\����1\\"+"Redution"+n_threshold+"\\Map.txt");	
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
		//��������ļ����ļ��У�����ֵ����
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
			    //���ļ���
			    FileInputStream ins=new FileInputStream(this.datafile);
			    InputStreamReader inReader=new InputStreamReader(ins);
			    BufferedReader bReader=new BufferedReader(inReader);
			    String rline=null;
			    //���ļ�����
			    System.out.println("д"+pathway_label+"�ļ�");
			    String newname="\\"+pathway_label+".arff";
			
			    //д�ļ���
			    File fout = new File(path+newname);	
			    BufferedWriter bwriter = new BufferedWriter(new FileWriter(fout,false));	
			    //���ж�ԭ�ļ�������������ļ�
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
			    if(flag==1){//�������ݱ�־���˳�ʱ�����
				    for(int j=0;j<temp_attr_list.size();j++){//���ѡ���Ļ�������
					    String tempattr=temp_attr_list.get(j);
					    int index=attr_name_list.indexOf(tempattr);
					    bwriter.write(attr.get(index)+"\r\n");
				    }
				    bwriter.write(attr.get(attr.size()-1)+"\r\n");//����������
				    bwriter.write(rline+"\r\n");//���@DATA
				    while((rline=bReader.readLine())!=null){//�������ݲ���
					     String outdata=null;
					     String split[]=rline.split(",");
					     for(int j=0;j<temp_attr_list.size();j++){//���ѡ���Ļ�������
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
			read_info(this.datafile);//��ȡ�����ļ���Ϣ
			System.out.println("�������ļ���Ϣ�ɹ���");
            get_equ_class();
            System.out.println("��õȼ���ɹ���");
			this.get_Pathway_Map();//���pathway��Ϣ
		    this.print_data();//�������������ļ�
		    System.out.println("���������ļ�����");
            this.create_pathway_ns(n_threshold);  
			this.Attr_Reduction();
            System.out.println("����Լ��ɹ���");
            this.print_red_data();
		}
		catch(Exception e){
			System.out.println("error!");
			e.printStackTrace();
		}
		
	}
    /*public static void main(String args[]){
         String pathwayfilename="E:\\��ҵ����(�ž�)\\����ʵ��\\����\\pathwayfile.txt";
         String datafilename="E:\\��ҵ����(�ž�)\\����ʵ��\\����\\ArabidopsisDrought.arff";
         String selectdatafilename="E:\\��ҵ����(�ž�)\\����ʵ��\\����\\data"+n_threshold+"\\";
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