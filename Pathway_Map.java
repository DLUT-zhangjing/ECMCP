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


public class Pathway_Map{
	
	private Map<String,ArrayList<String>> All_Pathway_Map=new HashMap<String,ArrayList<String>>();//�洢Pathway��Ϣ��ֵ�ԣ�pathway���Ϊ���������б�Ϊֵ
    private int Clean_Num;//��¼cleanʱ�Ļ��������ֵ
	private int Length_thr_Num;//��¼Pathway��������ָ���ֵ�������������ǷָΪ0ʱ������ӦѰ��
    private ArrayList<String> attr_name_list=new ArrayList<String>();
    ArrayList<String>  pathway_genelist1=new ArrayList<String>();

    public Map<String, ArrayList<String>> getAll_Pathway_Map() {
		return All_Pathway_Map;
	}       

    public Pathway_Map(int cl_num,int lt_num){
		this.Clean_Num=cl_num;
		this.Length_thr_Num=lt_num;
	}


    public void read_info(String file)throws Exception{
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
				}
			}
		}
		
		bReader.close();
		inReader.close();
		in.close();
	}
    public void Create_Map(String pathway_filename) throws Exception{
		
		//�����ձ��	
		
		for(int i=0;i<attr_name_list.size();i++){
             pathway_genelist1.add(attr_name_list.get(i));
        }
		FileInputStream in=new FileInputStream(pathway_filename);
		InputStreamReader inReader=new InputStreamReader(in);
		BufferedReader bReader=new BufferedReader(inReader);
		String line=null;
		while((line=bReader.readLine())!=null){//��ȡһ�л����pathway��Ϣ
			String split[]=line.split("\t");//[0]��ţ�
                        ArrayList<String> pathway_genelist = new ArrayList<String>();
                        for(int i=1;i<split.length;i++){
                                  pathway_genelist.add(split[i]);
                                  for(int j=0;j<pathway_genelist1.size();j++){
                                         if(split[i].equals(pathway_genelist1.get(j))){
                                                  pathway_genelist1.remove(j);
                                                  continue;
                                         }
                                  }
                        }
	                
			All_Pathway_Map.put(split[0], pathway_genelist);
        }
		System.out.println("��������pathway�еĻ�������"+pathway_genelist1.size());
        bReader.close();
		inReader.close();
		in.close();
    }
    public void print_data(String pathwayfile,String datafile) throws Exception{
		File in=new File(pathwayfile);
        String path=in.getParent();
		File r=new File(path);
	    if(!r.exists()){
		     r.mkdirs();
		}
		ArrayList<String> temp_attr_list =new ArrayList<String>();
        temp_attr_list= attr_name_list;
        System.out.println(temp_attr_list.size());
        for(int i=0;i<pathway_genelist1.size();i++){
             for(int j=0;j<attr_name_list.size();j++){
                  if(temp_attr_list.get(j).equals(pathway_genelist1.get(i))){
                       temp_attr_list.remove(j);
                       continue;
                  }
             }
        }   
        System.out.println(temp_attr_list.size());              
	    //���ļ���
		FileInputStream ins=new FileInputStream(datafile);
		InputStreamReader inReader=new InputStreamReader(ins);
		BufferedReader bReader=new BufferedReader(inReader);
		String rline=null;
		//���ļ�����
		System.out.println("д"+"pathwayselect"+"�ļ�");
		String newname="pathwayselect"+".arff";
			
		//д�ļ���
		File fout = new File(path+"\\"+newname);	
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
//���ڰ���������ٵ�Pathway��Ž�������
	/*public void MapClean()
	{
		//���ÿһ��Pathway��Ű����Ļ����������<=clean_num������ɾ���ñ��
		Iterator<Entry<String, ArrayList<String>>> keyValuePairs = All_Pathway_Map.entrySet().iterator();
		int all_map_num=All_Pathway_Map.size();
		for(int i=0;i<all_map_num;i++){
			Map.Entry<String, ArrayList<String>> entry=(Map.Entry<String, ArrayList<String>>) keyValuePairs.next();
			System.out.println(i);
			String pathway_label=entry.getKey();//��ȡ��
			if(!pathway_label.equals("NonePathway")){//���ڷ�NonePathway�����м����м��
				ArrayList<String> gene_list=entry.getValue();//��ȡֵ�������б�
				if(gene_list.size()<=this.Clean_Num){//����ֻ��1��2�������Pathway��ţ�ɾ���ü�ֵ
					//ɾ���ü�ֵ��
					System.out.println("��Ҫ��֦");
					keyValuePairs.remove();
					System.out.println("��Ҫ�ƶ���NonePathway");
					ArrayList<String> none_pathway_list=All_Pathway_Map.get("NonePathway");
                                        for(int j=0;j<gene_list.size();j++){
					       String gene =  gene_list.get(j);//��¼������			                                                                                           ArrayList<String> none_pathway_list=All_Pathway_Map.get("NonePathway");
					       none_pathway_list.add(gene);
					       All_Pathway_Map.put("NonePathway", none_pathway_list);
                                        }
			        }			
			}
		}
        }*/
			
	
	
	/*public void Search_thr_Num(){
		//����Pathway����¼Pathway�����Ļ������������ִ���
		ArrayList<Integer> length=new ArrayList<Integer>();//��¼�������
		ArrayList<Integer> times=new ArrayList<Integer>();//��¼���ִ���
		Iterator<Entry<String, ArrayList<String>>> keyValuePairs = All_Pathway_Map.entrySet().iterator();
		int all_map_num=122;
		for(int i=0;i<all_map_num;i++){
			Map.Entry<String, ArrayList<String>> entry=(Map.Entry<String, ArrayList<String>>) keyValuePairs.next();
			ArrayList<String> pathway_list=entry.getValue();
			Integer temp=new Integer(pathway_list.size());
			if(!length.isEmpty() && length.contains(temp)){
				int id=length.indexOf(temp);
				times.set(id, new Integer(times.get(id)+1));
			}
			else{
				length.add(temp);
				times.add(new Integer(1));
			}
		}
		//��ӡ
		for(int i=0;i<length.size();i++){
			System.out.println(length.get(i)+"\t"+times.get(i));
		}
		//����
		for(int i=1;i<length.size();i++){
			for(int j=0;j<length.size()-i;j++){
				if(length.get(j)>length.get(j+1)){
					Integer temp=length.get(j);
					length.set(j, length.get(j+1));
					length.set(j+1,temp);
					Integer temp_times=times.get(j);
					times.set(j, times.get(j+1));
					times.set(j+1,temp_times);
				}
			}
		}
		//��ӡ
		System.out.println("Sort:");
		for(int i=0;i<length.size();i++){
			System.out.println(length.get(i)+"\t"+times.get(i));
		}
		//ȷ����ֵ
		int d_thr=2;
		if(all_map_num>100){
			d_thr=d_thr+1+((all_map_num-100)/200);
		}
		for(int i=times.size()-1;i>=d_thr-1;i--){
			//if(times.get(i)>1 && times.get(i-1)>1 && times.get(i-2)>1){
			int j=0;
			for(;j<d_thr;j++){
				if(!(times.get(i-j)>1)){
					break;
				}
			}
			if(j==d_thr){
				this.Length_thr_Num=length.get(i).intValue();
				break;
			}
		}
		System.out.println("length_thr_num:"+this.Length_thr_Num);
	}
	
	public void MapSplit(String printfilepath) throws Exception{
		//�������е�pathway��ţ����������������ֵ��pathway��ż�¼
		Iterator<Entry<String, ArrayList<String>>> keyValuePairs = All_Pathway_Map.entrySet().iterator();
		int all_map_num=119;
		ArrayList<String> sp_pathway_list=new ArrayList<String>();//��¼��Ҫ�����pathway���
		for(int i=0;i<all_map_num;i++){
			Map.Entry<String, ArrayList<String>> entry=(Map.Entry<String, ArrayList<String>>) keyValuePairs.next();
			String pathway_label=entry.getKey();//��ȡ��
			ArrayList<String> pathway_list=entry.getValue();
			if(pathway_list.size()>this.Length_thr_Num){
				sp_pathway_list.add(pathway_label);
			}
		}
		//����Pathway�ֶ�
		//System.out.println("pathway:"+sp_pathway_list.size());
		Map<String,ArrayList<String>> Split_Pathway_Map=new HashMap<String,ArrayList<String>>();
		for(int i=0;i<sp_pathway_list.size();i++){
			String pathway_label=sp_pathway_list.get(i);//��¼��ǰҪ����ı��
			ArrayList<String> pathway_list=this.All_Pathway_Map.get(pathway_label);
			int a=pathway_list.size()/this.Length_thr_Num;//ȡ�̣����ֶ���
			//System.out.println("split:"+a);
			if(a==1){
				int b=pathway_list.size()%this.Length_thr_Num;//ȡ����
				if(b>this.Length_thr_Num/4){//����>��ֵ/4�����÷ֶ���Ϊ2�����򲻷ֶ�
					a=2;
				}
			}
			if(a>1){//���зֶΣ��ֶ�����Ϊa
				for(int m=0;m<a;m++){
					String temp_label=pathway_label+"_"+(m+1);
					ArrayList<String> temp_list=new ArrayList<String>();
					for(int n=m;n<pathway_list.size();n=n+a){
						temp_list.add(pathway_list.get(n));
					}
					Split_Pathway_Map.put(temp_label, temp_list);
		
						this.All_Pathway_Map.put(temp_label, temp_list);//pathway��ֻ�����1��

				}
				this.All_Pathway_Map.remove(pathway_label);
			}
		}
		//��ӡ�ֶε�ȫ�屣�����
		
		File dir=new File(printfilepath);
		if(!dir.exists()){
			dir.mkdir();
		}
		//����Map��������ļ�
		File out = new File(printfilepath+"\\Split_Map.txt");	
		BufferedWriter bwriter = new BufferedWriter(new FileWriter(out,false));
		Iterator<Entry<String, ArrayList<String>>> keyValuePairs1 = Split_Pathway_Map.entrySet().iterator();
		for(int i=0;i<Split_Pathway_Map.size();i++){
			Map.Entry<String, ArrayList<String>> entry=(Map.Entry<String, ArrayList<String>>) keyValuePairs1.next();
			String pathway_label=entry.getKey();
			ArrayList<String> gene_list=entry.getValue();
			String temp=pathway_label+"\t";
			for(int j=0;j<gene_list.size()-1;j++){
				temp=temp+gene_list.get(j)+",";
			}
			temp=temp+gene_list.get(gene_list.size()-1);
			if(i==Split_Pathway_Map.size()-1){
				bwriter.write(temp);
			}
			else{
				bwriter.write(temp+"\r\n");
			}
		}
		bwriter.close();
	}*/
	
    /*public void MapPrint(String printfilepath) throws Exception{
		File dir=new File(printfilepath);
		if(!dir.exists()){
			dir.mkdir();
		}
		//����Map��������ļ�
		File out = new File(printfilepath+"\\Map.txt");	
		BufferedWriter bwriter = new BufferedWriter(new FileWriter(out,false));
		Iterator<Entry<String, ArrayList<String>>> keyValuePairs = All_Pathway_Map.entrySet().iterator();
		for(int i=0;i<All_Pathway_Map.size();i++){
			Map.Entry<String, ArrayList<String>> entry=(Map.Entry<String, ArrayList<String>>) keyValuePairs.next();
			String pathway_label=entry.getKey();
			ArrayList<String> gene_list=entry.getValue();
			if(gene_list.size()!=0){
				String temp=pathway_label+"\t";
				for(int j=0;j<gene_list.size()-1;j++){
					temp=temp+gene_list.get(j)+",";
				}
				temp=temp+gene_list.get(gene_list.size()-1);
				if(i==All_Pathway_Map.size()-1){
					bwriter.write(temp);
				}
				else{
					bwriter.write(temp+"\r\n");
				}
			}			
		}
		bwriter.close();
		
	}
	*/
	public void Run(String pathway_filename,String datafile){
		try{
			System.out.println("Start!");
            read_info(datafile);
            System.out.println("read_info datafile finish!");
			Create_Map(pathway_filename);
			System.out.println("Map finish!");
			File f= new File(pathway_filename);
			/*String path=f.getParent()+"\\BeforeClean";
			System.out.println("Print Start!");
			MapPrint(path);
			System.out.println("Print finish!");
			System.out.println("Clean Start!");
			MapClean();
			System.out.println("Clean Finish!");*/
            System.out.println("print_data start!");
            print_data(pathway_filename,datafile);
            System.out.println("print_data finfish!");
			String path=f.getParent();
			System.out.println("Print Start!");
                        
			//MapPrint(path);
			//System.out.println("Print finish!");
			/*if(this.Length_thr_Num==0){
				System.out.println("Search Start!");
				Search_thr_Num();
				System.out.println("Search finish!");
			}*/
			path=f.getParent();
			/*System.out.println("Split Start!");
			MapSplit(path);
			System.out.println("Split Finish!");*/
			/*System.out.println("Print Start!");
			MapPrint(path);
			*/System.out.println("Print finish!");
		}
		catch(Exception e){
			System.out.println(e.getMessage());
			System.out.println("error!");
		}
	}
	
	/*public static void main(String args[]){
		String filename="J:\\��ҵ����\\����ʵ��\\pathwayfile.txt";
                String datafile="J:\\��ҵ����\\����ʵ��\\ArabidopsisDrought.txt";
		Pathway_Map a = new Pathway_Map(2,20);
		try{
		    	a.Run(filename,datafile);
		}
		catch(Exception e){
			System.out.println("error!");
		}
	}*/
}