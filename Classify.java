import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSink;
import weka.classifiers.functions.LibSVM;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import weka.classifiers.Evaluation;
public class Classify {
	
	private String filepath;//���ڷ����ȫ���ļ����ڵ��ļ���·��
	private double percent;//�����İٷֱȣ�������ѵ����
	private int num;//�ظ������Ĵ���
	private int obj_num;//��������
	private int classifier_num;//���������ĸ���
	private char[] flag;//��¼ÿ��������������0��ʾ��δ���䣬1��ʾ����ѵ������2��ʾ������֤����3��ʾ���ڲ��Լ�
	private ArrayList<String> dec_classname_list=new ArrayList<String>();//��¼�����
	private ArrayList<ArrayList<Integer>> class_obj_list=new ArrayList<ArrayList<Integer>>();//��¼ÿ��������Ӧ�Ķ���id�б�
	private int train_obj_num;//��¼ѵ�����������
	private int validation_obj_num;
	private int test_obj_num;
	private ArrayList<String> old_label=new ArrayList<String>();//��¼���Լ���ԭʼ��ǩ
	private ArrayList<String> new_label=new ArrayList<String>();//��¼���Լ����·����ǩ
	private double acc_list[];//��¼���������ķ�����ȷ��
	private int num_list[];//��¼ÿ������������Ӧ�����Ը���
	private int svm_support_num[][];
	private double end_acc=0.0;//��¼num��ƽ�����ɷ�����������ȷ��
	private double end_tp=0.0;
	private double end_tn=0.0;
	private double end_fn=0.0;
	private double end_fp=0.0;
	//private char GR_flag;//����Լ���־��'O'��ʾֻ��һ�����ȣ�GO��ţ���������ȫ��GO������������'M'��ʾ��Ҫ���
	private ArrayList<String> reduction_list=new ArrayList<String>();
	private double kappastatistic[];
	private double kappanumber=0;
	private int number=0;
	private int tp=0;
	private int tn=0;
	private int fn=0;
	private int fp=0;
	public Classify(String path,double per,int n,double kappanumber){
		this.filepath=path;
		this.percent=per;
		this.num=n;
		this.train_obj_num=0;
		this.kappanumber=kappanumber;
	}
	
	/*public Classify(String path,int n){
		this.filepath=path;
		this.num=n;
		this.train_obj_num=0;
	
	}*/
	
	public String getFilepath() {
		return filepath;
	}

	public double getPercent() {
		return percent;
	}

	public int getNum() {
		return num;
	}

	public int getObj_num() {
		return obj_num;
	}

	public int getClassifier_num(){
		return this.classifier_num;
	}

	public char[] getFlag() {
		return flag;
	}

	public ArrayList<String> getDec_classname_list() {
		return dec_classname_list;
	}

	public ArrayList<ArrayList<Integer>> getClass_obj_list() {
		return class_obj_list;
	}

	public int getTrain_obj_num() {
		return train_obj_num;
	}

	public ArrayList<String> getOld_label() {
		return old_label;
	}

	public ArrayList<String> getNew_label() {
		return new_label;
	}

	public double getEnd_acc() {
		return end_acc;
	}

	public double[] getAcc_list(){
		return this.acc_list;
	}

	public int[][] getSvm_support_num(){
		return svm_support_num;
	}
	
	//���б��е�һ���ļ�����ȡ������Ϣ
	public void read_info()throws Exception{
		//ֻ��ȡ�ļ����е�һ���ļ�
		File file=new File(this.filepath);
		String[] file_list=file.list();
		/*for(int i=0;i<file_list.length;i++){
			String[] split=file_list[i].split("\\.");
			//System.out.println(split[0].substring(7));
			this.reduction_list.add(split[0].substring(7));
		}*/
		this.classifier_num=file_list.length;
		this.acc_list=new double[file_list.length];//��ʼ��������ȷ���б���¼���������ķ�����ȷ��
		this.num_list=new int[file_list.length];//��ʼ�����Ը����б���¼����������Ӧ�����Ը���
		FileInputStream in=new FileInputStream(this.filepath+"\\"+file_list[0]);
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
					if(split[1].toUpperCase().equals("CLASS")){//�������б�
						String temp=line.substring(18,line.length()-1);
						String split1[]=temp.split(",");
						for(int i=0;i<split1.length;i++){
							split1[i]=split1[i].trim();
							dec_classname_list.add(split1[i]);
						}
					}
				}
			}
		}
		//��ʼ��������б�
		for(int i=0;i<this.dec_classname_list.size();i++){
			ArrayList<Integer> temp=new ArrayList<Integer>();
			this.class_obj_list.add(temp);
		}
		if(flag==1){//�����ݶ����ܸ�����ÿ���������������б�
			int i=0;
			while((line=bReader.readLine())!=null){
				String split[]=line.split(",");
				String obj_label=split[split.length-1].trim();
				for(int k=0;k<this.dec_classname_list.size();k++){
					String temp_class=this.dec_classname_list.get(k);
					if(obj_label.equals(temp_class)){
						ArrayList<Integer> temp_list=this.class_obj_list.get(k);
						temp_list.add(i);
						this.class_obj_list.set(k, temp_list);
					}
				}
				i++;
			}
			this.obj_num=i;
		}
		bReader.close();
		inReader.close();
		in.close();
	}
	
	//�޷Żس���
	public void sampling(){
		//��ʼ��
		int o_num=this.obj_num;
		flag=new char[o_num];
		for(int i=0;i<o_num;i++){
			flag[i]='0';
		}
		this.train_obj_num=0;
		//��ÿ������зֱ��ȡ
		for(int k=0;k<this.dec_classname_list.size();k++){
			ArrayList<Integer> temp_list=this.class_obj_list.get(k);//��õ�ǰ��Ķ����б�
			int class_obj_num=temp_list.size();
			//System.out.println("class_obj_num"+class_obj_num);
			int train_num=(int)(this.percent*class_obj_num);//ѵ������������
			int validation_num=(int)(0.2*class_obj_num);
			//System.out.println("train_num"+train_num);
			this.train_obj_num=this.train_obj_num+train_num;
			this.validation_obj_num=validation_obj_num+validation_num;
			for(int m=0,n=0;m<class_obj_num || n<train_num;m++){
				int r=(int)(Math.random()*(class_obj_num-m));
				//System.out.println("r"+r);
				if(r<(train_num-n)){
					flag[temp_list.get(m)]='1';
					n++;
					//System.out.println(j);
				}
				else{
					flag[temp_list.get(m)]='2';
				}
			}
			ArrayList<Integer> temp_list1=new ArrayList<Integer>();
			Iterator<Integer> it =temp_list1.iterator();  
	        for(;it.hasNext();) {  
	              it.next();  
	              it.remove(); 
	        }
			for(int i=0;i<class_obj_num;i++){
				if(flag[temp_list.get(i)]=='2'){
					temp_list1.add(temp_list.get(i));
				}
			}
			for(int i=0,j=0;i<temp_list1.size()||j<validation_num;i++){
				int r=(int)(Math.random()*(temp_list1.size()-i));
				if(r<(validation_num-j)){
					j++;
					//System.out.println(j);
				}
				else{
					flag[temp_list1.get(i)]='3';
				}
			}	
		}
	}
	
	//leave one out of����
	/*public void leave_one_sampling(int id){
		//��ʼ��
		int o_num=this.obj_num;
		flag=new char[o_num];
		for(int i=0;i<o_num;i++){
			flag[i]='0';
		}
		this.train_obj_num=this.obj_num-1;
		//����ǰid�����ǳɲ��Լ�����������Ϊѵ����
		for(int i=0;i<this.obj_num;i++){
			if(i==id){
				flag[i]='2';
			}
			else{
				flag[i]='1';
			}
		}
	}*/
	
	public double classifier(){
		int test_num=this.obj_num-this.train_obj_num-this.validation_obj_num;
		System.out.println(test_num);
		this.svm_support_num=new int[test_num][this.dec_classname_list.size()];
		this.kappastatistic=new double[121];//��¼ѵ������ÿ�����������ǩ֧�ֶ�
		this.old_label.clear();
		this.new_label.clear();
		int acc_num=0;//��¼�ж���ȷ�ĸ���
		double acc=0.0;//��¼��ȷ��
		
		try{
			
			//�����ļ����е�ÿһ�������ļ�
			File file=new File(this.filepath);
			String[] file_list=file.list();
			for(int i=0;i<file_list.length;i++){
				String path=this.filepath+"\\"+file_list[i];
				
				//��ȡ����
				FileReader data=new FileReader(path);
				Instances m_instances = new Instances(data);
				m_instances.setClassIndex( m_instances.numAttributes() - 1 );//���ø�ʵ����������
				this.num_list[i]=m_instances.numAttributes()-1;//��¼���Ը�����������������
				System.out.println("��ȡ���ݳɹ�");
				
				//�������ݱ�׼��-1~1
				Normalize normalize=new Normalize();
				String options[]=weka.core.Utils.splitOptions("-S 2.0 -T -1.0");//���ò���
				normalize.setOptions(options);
				normalize.setInputFormat(m_instances);//���������ļ�
				Instances n_m_instances=Filter.useFilter(m_instances, normalize);//ʹ�ù������������µ�����
				//DataSink.write(this.filepath+"\\nor_"+file_list[i],n_m_instances);//�����׼���������
				System.out.println("���ݱ�׼���ɹ�");
				
				//���ݳ������������ѵ�����Ͳ��Լ�
				Instances train_instances = new Instances(n_m_instances);//��¼ѵ��������ʼ��Ϊȫ��
				Instances test_instances = new Instances(n_m_instances);//��¼���Լ�����ʼ��Ϊȫ��
				Instances validation_instances=new Instances(n_m_instances);
				train_instances.setClassIndex( train_instances.numAttributes() - 1 );
				test_instances.setClassIndex( test_instances.numAttributes() - 1 );
				validation_instances.setClassIndex(validation_instances.numAttributes()-1);
				for(int j=this.obj_num-1;j>=0;j--){
					if(this.flag[j]=='1'){
						test_instances.delete(j);
						validation_instances.delete(j);
					}
					else if(this.flag[j]=='2'){
						train_instances.delete(j);
						test_instances.delete(j);
					}
					else if(this.flag[j]=='3'){
						train_instances.delete(j);
						validation_instances.delete(j);
					}
					else{
						System.out.println("���ݼ���������");
					}
				}
				//DataSink.write(this.filepath+"\\train_"+file_list[i],train_instances);
				//DataSink.write(this.filepath+"\\test_"+file_list[i],test_instances);
				System.out.println("���ݼ����ѳɹ�");
				
				//��ѵ����������SVM������
				Classifier svm= new LibSVM();
				svm.buildClassifier(train_instances);
				Evaluation eval=new Evaluation(train_instances);
				eval.evaluateModel(svm,validation_instances);
				kappastatistic[i]=eval.kappa();
				if(kappastatistic[i]>this.kappanumber){
					 number=number+1;
				}
				//���ԣ�����¼�Բ��Լ������ķ�����
				for(int k=0;k<test_instances.numInstances();k++){
					double t=test_instances.instance(k).classValue();
					String temp_old_label=test_instances.classAttribute().value((int)t);
					if(i==0){
						this.old_label.add(temp_old_label);//��Ӿɱ�ǩ
					}
					double class_l=svm.classifyInstance(test_instances.instance(k));//����
					String class_label=test_instances.classAttribute().value((int)class_l);//������ǩ
					
					//��¼���
					if(class_label.equals(temp_old_label)){
						this.acc_list[i]=this.acc_list[i]+((double)1)/test_num;
					}
					for(int m=0;m<this.dec_classname_list.size();m++){
						String temp_class=this.dec_classname_list.get(m);
						if((class_label.trim().equals(temp_class))&&(kappastatistic[i]>this.kappanumber)){
							this.svm_support_num[k][m]=this.svm_support_num[k][m]+1;
						}
					}
				}	
			}
			
			//'M'����������㼯�ɷ�����ȷ��
			//if(this.GR_flag=='M'){
				for(int x=0;x<test_num;x++){
					//����������ǩΪ��������ͶƱ������
					int max=this.svm_support_num[x][0];//��¼������֧��������ʼ��Ϊ��һ�����֧����
					int max_id=0;//��¼ȡ���ֵʱ����id
					for(int y=1;y<this.dec_classname_list.size();y++){
						if(this.svm_support_num[x][y]>max){
							max=this.svm_support_num[x][y];
							max_id=y;
						}
					}
					this.new_label.add(x,this.dec_classname_list.get(max_id));
					
					//��ԭ��ǩ�Ƚϣ���һ�£�����ȷ������1
					if(this.old_label.get(x).equals(this.new_label.get(x))){
						acc_num++;
						if(this.old_label.get(x).equals("control")){
							tp=tp+1;	
						}
						if(this.old_label.get(x).equals("hypoxia")){
							tn=tn+1;
					    }
				   }
					if(!(this.old_label.get(x).equals(this.new_label.get(x)))){
						if(this.old_label.get(x).equals("control")){
							fn=fn+1;	
						}
						if(this.old_label.get(x).equals("hypoxia")){
							fp=fp+1;
					    }
				   }
				}
				acc=((double)acc_num)/test_num;
			//}
			
			//���������������������ȷ��
			System.out.println("�������������ۼ�acc��kappaϵ��");
			for(int k=0;k<this.acc_list.length;k++){
				System.out.println(this.acc_list[k]+";"+kappastatistic[k]);
			}
			System.out.print("\n");
				}
				catch(Exception e){
			       e.printStackTrace();
		}
		return acc;
	}
	
	public void printresult(){
		try{
			File path=new File(this.filepath);
			String threshold=path.getName();
			String up_path=path.getParentFile().getParent();
			String path1=up_path+"\\Result\\";
			File r=new File(path1);
		    if(!r.exists()){
		    	r.mkdirs();
		    } 
            String newfile=path1+"Acc.txt";
			//System.out.println(newfile);
			File fout = new File(newfile);
			if(!fout.exists()){
				BufferedWriter bwriter= new BufferedWriter(new FileWriter(fout,true));
				bwriter.write("number\t"+"threshold\t"+"kappa"+"\titer\tavg_em_acc"+"\t"+"TP"+"\t"+"TN"+"\t"+"FN"+"\t"+"FP"+"\t"+"classifier_num"+"\t"+"avg_acc\r\n");
				bwriter.close();
			}
			BufferedWriter bwriter= new BufferedWriter(new FileWriter(fout,true));
			String line=this.number+"\t"+threshold+"\t"+this.kappanumber+"\t"+this.num+"\t"+this.end_acc+"\t"+this.end_tp+"\t"+this.end_tn+"\t"+this.end_fn+"\t"+this.end_fp+"\t"+this.classifier_num+"\t";
			for(int i=0;i<this.classifier_num-1;i++){
				line=line+this.acc_list[i]+"("+this.num_list[i]+")";//+"["+this.reduction_list.get(i)+"]"+";";
			}
			line=line+this.acc_list[this.classifier_num-1]+"("+this.num_list[this.classifier_num-1]+")";//["+this.reduction_list.get(this.classifier_num-1)+"]";
			bwriter.write(line+"\r\n");
			bwriter.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void run(){
		try{
			this.read_info();
			System.out.println("����Ϣ�ɹ���");
			for(int i=0;i<this.num;i++){
				this.train_obj_num=0;
				this.validation_obj_num=0;
				this.sampling();//����
				
				//this.leave_one_sampling(i);//leave one
				System.out.println("��"+(i+1)+"�γ����ɹ���");
				//��ӡ�������
				char[] f=this.getFlag();
				for(int j=0;j<f.length;j++){
					System.out.print(f[j]+";");
				}
				System.out.println("\n");
				double temp_acc=this.classifier();
				System.out.println("��"+(i+1)+"�η���ɹ���");
				System.out.println("temp_acc:"+temp_acc);
				this.end_acc+=temp_acc;
			}
			for(int i=0;i<this.classifier_num;i++){
				this.acc_list[i]=this.acc_list[i]/this.num;
				System.out.println("acc_list"+"["+i+"]"+acc_list[i]);
			}
			this.end_acc=this.end_acc/this.num;
			this.end_tp=((double)tp)/this.num;
			this.end_tn=((double)tn)/this.num;
			this.end_fn=((double)fn)/this.num;
			this.end_fp=((double)fp)/this.num;
			/*else if(this.GR_flag=='O'){//�����ֵ
				this.end_acc=this.acc_list[0];
				for(int k=1;k<this.classifier_num;k++){
					if(this.acc_list[k]>this.end_acc){
						this.end_acc=this.acc_list[k];
					}
				}
			}*/
			System.out.println("end_acc:"+this.end_acc);
			System.out.println("����ɹ�");
			this.printresult();
			System.out.println("��ӡ����ɹ�");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
