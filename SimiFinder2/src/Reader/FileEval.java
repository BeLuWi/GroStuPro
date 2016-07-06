package Reader;

import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class FileEval {
	String fileLoc = "";
	boolean rdy1 = false, rdy2 = false;
	double stepdiffs = 0.0;//zeigt den Unteschied, der zwischen den einzelnen Schritten des streamrankings
	int streamdiffs = 0; //Streamdiffs hat in jeder Arrayzelle die Anzahl der entry Streams, die fuer den gleichen key Stream unterscheiden. Wenn man also einen keystream aus xml1 und xml2 vergleicht und entrystream1 nicht in beiden vorkommt, erhöht das die passende zelle in streamdiffs um 1
	double ratiodiffs = 0.0; //wenn fuer den KeyStream in xml1 und xml2 der gleiche entrystream eingetragen ist, wird die differenz der ratios in ratiodiffs gespeichert
	int stepdiffsAmount = 0, streamdiffsAmount = 0, ratiodiffsAmount = 0;
	
	Map<String, StreamWithRatio[]>xml1, xml2;
	Map<String, Values> out = new HashMap<String, Values>();
	FileEval(String file){
		this.fileLoc = file;
	}
	void inputMap(Map<String, StreamWithRatio[]> input){
		if (!rdy1){
			this.xml1 = input;
			this.rdy1 = true;
		}
		else if (rdy1){
			this.xml2 = input;
			this.rdy2 = true;
		}	
		if (rdy2){
			this.evalMaps();
		}
	}
	void evalMaps(){
		

		for (String stream1 : this.xml1.keySet()) {
			//herausfinden der Werte
			int streamdiff = 0;
			double stepdiff = 0.0;
			try{
				StreamWithRatio[]tmp1 = xml1.get(stream1);
				StreamWithRatio[]tmp2 = xml2.get(stream1);
				for (int i = 0; i < tmp1.length; i++){
					boolean found = false;
					if(i < (tmp1.length-1)){
						stepdiff += Math.abs( Math.abs((tmp1[i].ratio - tmp1[i+1].ratio) - Math.abs(tmp2[i].ratio - tmp2[i+1].ratio))  )/9.0;
					}
					for (int j = 0; j < tmp2.length; j++){
						if(tmp1[i].compare(tmp2[j])){
							found = true;
							ratiodiffs += Math.abs(tmp1[i].ratio - tmp2[j].ratio);
							ratiodiffsAmount++;
						}
					}
					if (!found){
						streamdiff++;
					}
				}
				stepdiffs += stepdiff ;
				streamdiffs += streamdiff ;
				streamdiffsAmount++;
				stepdiffsAmount++;
				out.put(stream1, new Values(stepdiff, streamdiff));
			}catch(Exception e){
				System.out.println("Can't find " + stream1 + " in other xml");
			}
		}
				
		try {
			File file = new File(fileLoc);
			PrintStream ps = new PrintStream(file);
			ps.println("Durchschnittliche Werte: ");
			ps.println("Schrittunterschied: "+ stepdiffs/(double)stepdiffsAmount);
			ps.println("Streamunterschied: "+ (double)streamdiffs/(double)streamdiffsAmount);
			ps.println("Ratiounterschied: "+ ratiodiffs/(double)ratiodiffsAmount);
			ps.println("");
			for(String str : out.keySet()){
				ps.println("<<<<<<<"+str +">>>>>>>");
				ps.println("Schrittunterschied: " + out.get(str).stepdiff);
				ps.println("Streamunterschied: " + out.get(str).streamdiff + "\n");
				ps.println("");
			}			
			ps.close();
			System.out.println(fileLoc + " printed");
		}catch(Exception e){}
	}
}
class Values{
	double stepdiff;
	int streamdiff;
	Values(double step, int stream){
		this.stepdiff = step;
		this.streamdiff = stream;
	}
}
