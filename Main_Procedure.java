import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;


public class Main_Procedure {
	public static void main(String args[]){
		try{
			String pathway_file="pathwayfile.txt";
			String data_file="ArabidopsisOxygen.arff";
			String selectdatafilename="pathway_file.getParent()"+"\\data\\";
			for(double threshold=0.05;threshold<=0.05;threshold=threshold+0.1){
				
				BigDecimal b =new BigDecimal(threshold);
				threshold=b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				
			        int cl_num=2;
				int lt_num=20;
				//Gene_Select gene_select=new Gene_Select(pathway_file,data_file,selectdatafilename,threshold,cl_num,lt_num);
				//gene_select.run();
				//char gr_flag=gene_select.getGR_flag();
				File in=new File(pathway_file);
				String c_data_path="pathway_file.getParent()"+"\\Red_data\\"+threshold;
				int iter=10;
				double percent=0.6;
				for(double kappa=0.1;kappa<1.0;kappa=kappa+0.1){
				BigDecimal c=new BigDecimal(kappa);
				kappa=c.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				Classify classifier=new Classify(c_data_path,percent,iter,kappa);
				classifier.run();
				}
			}	

		}
		catch(Exception e){
			
		}
	}
}
