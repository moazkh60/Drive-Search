import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class SearchApp extends JFrame implements Runnable {
	
	private File[] myfile;	
	private static byte b[]; // for reading characters in file

	int directories_count = 0;
	String directory;
	
	private JTextField t_field = new JTextField("", 20); // for file name
	private JTextField p_field = new JTextField("", 30); // for diplaying the file path
	private JTextArea t_areaUpper  = new JTextArea(" FILE CONTENTS WILL BE DISPLAYED HERE "); //upper text area
	private JTextArea t_areaLower  = new JTextArea(" DIRECTORIES WILL BE DISPLAYED HERE ");   //lower text area
	private JLabel file_label  = new JLabel("File Name :");   //file name label
	private JLabel dir_label   = new JLabel("Directory :");   //directory label
	private JLabel status 	   = new JLabel("Status: Ready ");//status label
	private JLabel path		   = new JLabel("Path: ");        //path label
	
	private JScrollPane spUpper= new JScrollPane(t_areaUpper); //scrollpane for upper text area
	private JScrollPane spLower= new JScrollPane(t_areaLower); //scrollpane for lower text area
	
	private JComboBox jcb 	   = new JComboBox(); //combo box for directories
	private JButton search     = new JButton("Search"); // search button
	
	private String file_name; //for storing file name
	private String dir; //for storing directories
	boolean file_found; //checking if the file is found
	private String file_text = ""; //for displaying file contents
	private Thread t;  
	
	SearchApp() {	
		
		directories_count = File.listRoots().length; //counting total disk drives 
		myfile = File.listRoots(); // a file object for storing root directories
		
		for(int i=0;i<directories_count;i++)
			jcb.addItem(myfile[i]); // adding directories in comboBox
		
		JPanel up = new JPanel(); //panel to add north
		JPanel center = new JPanel(new GridLayout(2,1)); //panel to add in the center

		add(up, "North");     //adding a panel north
		add(center,"Center"); //adding a panel center
		
		up.add(file_label);//file name label
		up.add(t_field);   // text field
		up.add(dir_label); //directory label
		up.add(jcb);       //combo box
		up.add(search);    //search button
		up.add(status);    //status label
		up.add(path);      //path label
		up.add(p_field);   //path field

		setTitle("Search Application");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1000, 700);//frame size
		setVisible(true);
		
		center.add(spUpper); // adding upper text area with scroll pane
		center.add(spLower); // adding lower text area with scroll pane
		
		
		search.addMouseListener(new MouseAdapter(){
			
			public void mouseClicked(MouseEvent me) {
				
					p_field.setText(" "); 
					file_text = ""; //empty the string on click
					t_areaLower.setText(" "); //empty the t_area on click
					
				    file_name = t_field.getText(); //get file name form text field
				
					dir = jcb.getSelectedItem().toString(); //get directory
					dir +="\\";
					status.setText("Searching.."); //set status
					startThread();	//start the thread
				}
			});
	}// end constructor
	
	public void startThread(){
		t = new Thread(this);
		t.start();
	}

	public void Search(String v_dir, File v) {
		File[] list = v.listFiles(); //listing files
		
		if (list != null) {			    //checking so that nullpointerException cannot occur
			for (File subfile : list) { //listing every sub directory
				
				t_areaLower.append(""+v.getAbsolutePath()); //showing in text area
				t_areaLower.append("\n");				   // new line after printing a directory
				File v_file = new File(v.getAbsolutePath(), file_name); // getting the path of file
									
				if (subfile.isDirectory()) {
					Search(v_dir, subfile); //if more directories then search through them
				}
					
				if (v_dir.equalsIgnoreCase(subfile.getName())) { //compare file names
					file_found = true; //if file is found
					readFile(subfile); // read the contents
			
					status.setText("Status :File Found "); //set the status
					p_field.setText(subfile.getAbsolutePath());	
				}
		    }//end for
		}//end if
	}//end search
	
	public void readFile(File myfile){
		
		try {					
			 if(myfile.exists()){ 
					
					FileInputStream fis = new FileInputStream(myfile);
					b = new byte[(int) myfile.length()]; // creating byte array
					try {
						fis.read(b); //reading bytes and storing in b
					} catch (IOException e) {}
					for(int i=0;i<b.length;i++)
						file_text += (char)b[i]; //convert bytes to char and store in string
				}	
			} catch (FileNotFoundException e) {} //catch exceptions
		
		
		t_areaUpper.setText(file_text); //display file contens in upper text area
	}
	
	@Override
	public void run() {
		Search(file_name, new File(dir)); //call the function when thread is created
		if(file_found == false)
			status.setText("File Not Found !"); 
	}
	public static void main(String[] args){
		new SearchApp();
	}//end main	
}// end SearchApp Frame

