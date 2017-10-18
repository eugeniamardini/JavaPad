/*Student: Yauheniya Zapryvaryna
 * Instructor: Bill Iverson
 * Bellevue College
 * April 24, 2014
 * 
 * Assignment #5
 * 
 * Program JavaPad was written to create a GUI application for text processing and reading:
 * creating new text, uploading the existing files, saving and loading from any 
 * location on the computer, changing font and color of the text and background. 
 */

/**********I USED THESE LINKS TO HELP
 *   1) http://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
 *   2) https://netbeans.org/kb/docs/java/gui-filechooser.html
 *   3) http://cgi.csc.liv.ac.uk/~frans/OldLectures/COMP101/AdditionalStuff/GUI/fileChooser.html#inputGUI
 *   4) http://www.leepoint.net/notes-java/GUI/containers/20dialogs/30filechooser.html
 * 
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;


public class JavaPad implements ActionListener {
	
	//fields
    public BufferedReader fileInput;
    public JTextArea text;
    public JTextField jt;
    public JLabel j;
    public JButton b1, b2, b3, b4, b5, b6;
    public Container bottomButtons, top, bottom, font;
    public JOptionPane jo;
    public File fileName;
    public String[]data;
    public int numLines;

	JFrame frame = new JFrame(); //creating a new frame 
    //constructor JavaPad() creates a JavaPad with multiple containers, buttons, labels inside.
	public JavaPad() {  
		frame.setSize(new Dimension(500, 200));
		frame.setLocation(0, 0);
		frame.setTitle("Microstuff JavaPad XP");
		frame.setVisible(true);
		frame.isResizable();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exits by clicking on red cross
		frame.setLayout(new BorderLayout()); //"parent" layout
		
		top =new JPanel(new GridLayout(1,4)); //top "child" layout
		b1 = new JButton("New");
		b2 = new JButton("Save");
		b3 = new JButton("Load");
		b4 = new JButton("Quit");
        b1.addActionListener(this);	//need to "listen" to every button click		
		b2.addActionListener(this);
		b3.addActionListener(this);			
		b4.addActionListener(this);
		top.add(b1);
		top.add(b2);
		top.add(b3 );
		top.add(b4);
		
		bottom = new JPanel(new GridLayout(2,1)); // bottom 'child' layout
		bottomButtons = new JPanel (new FlowLayout()); //layout within the bottom
		b5 = new JButton("Foreground");
		b6 = new JButton("Background");
		b5.addActionListener(this);			
		b6.addActionListener(this);
		bottomButtons.add(b5);
		bottomButtons.add(b6);
		bottom.add(bottomButtons);
		
		font = new JPanel(new FlowLayout()); //layout within the bottom layout
		j = new JLabel("Font Size:");	
		font.add(j);
		jt = new JTextField(3);
		jt.addActionListener(this);
        font.add(jt);	
        bottom.add(font);
        
		frame.add(bottom, BorderLayout.SOUTH);
		text = new JTextArea(15,25);
		text.setFont(new Font("Arial", Font.PLAIN, 15));
        text.setWrapStyleWord(true);	//sets the style allowing to wrap long lines
        text.setLineWrap(true); 

		JScrollPane scroll = new JScrollPane(text); //constructing a scroller for the text field
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); //only vertical scroller
		frame.add(scroll, BorderLayout.CENTER); //placing scroller with the text in the center

		frame.add(top, BorderLayout.NORTH); 
		frame.pack();
	}
	
    public void actionPerformed(ActionEvent e) {
        String s = jt.getText();
    	setFont(s);
    	//changing foreground and background colors of the text using JColorChooser
		if (e.getActionCommand()=="Foreground") {
		    Color foregroundColor = JColorChooser.showDialog(frame, "Microstuff JavaPad XP", frame.getForeground());
		    if (foregroundColor!=null){
			    text.setForeground(foregroundColor);
			}
		}
		if (e.getActionCommand()=="Background"){
		    Color backgroundColor = JColorChooser.showDialog(frame, "Microstuff JavaPad XP", frame.getBackground());
			if (backgroundColor!=null) {
				text.setBackground(backgroundColor);
		    }
		}
		//if user clicks Load, do the following: getFileName and readFile to be able to see it on the pad
		if (e.getActionCommand() == "Load") {	
		    getFileName();
		    showFile();
		}
		//appending text on the user preferred file on the user's computer
		if (e.getActionCommand()=="Save") {
            try{
                File file = getFileName();
			    PrintStream out = new PrintStream(file);
			    out.append(text.getText());
			    out.close();
        	} catch (IOException io) { //if the chosen location (like DVD ROM) can't be accessed for file saving
        		JOptionPane.showMessageDialog(frame,"Could not access file "+fileName, 
       				 "I/O Error",JOptionPane.ERROR_MESSAGE); 
        	}
		}
		//clearing the text area
		if (e.getActionCommand()=="New") {
			text.setText("");
		}
		//having decided to quit using the pad, user gets a dialog window to finalize the decision by either quitting w/o saving
		//or by saving the file and then quitting
        if (e.getActionCommand()=="Quit") { 
		    int choice = JOptionPane.showConfirmDialog(frame, "Quitting; Save?", "Quit", JOptionPane.YES_NO_OPTION);
            if (choice==JOptionPane.YES_OPTION){
			    try {
				    PrintStream out = new PrintStream(getFileName());
				    out.append(text.getText());
				    out.close();			       
				    System.exit(0); //close the program
				} catch (FileNotFoundException ex) {
			        JOptionPane.showMessageDialog(frame,"Could not access file "+fileName, 
			       				 "I/O Error",JOptionPane.ERROR_MESSAGE); 
			    }
			}
            else
		        System.exit(0);
        }
   	}
	//using JFileChooser choose the file for viewing on the pad
    public File getFileName() {
		JFileChooser fileChooser = new JFileChooser();
		int result = fileChooser.showOpenDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION){
            fileName = fileChooser.getSelectedFile();
        }
		return fileName;
	}	    
	    
	public void showFile() {
		getNumberOfLines();
		data = new String[numLines];//setting the size of array to a number of lines in a file 
		// read file
		readFile();
		// Output to text area	
		text.setText(data[0]+"\n");
		for(int index=1;index < data.length;index++) {
			text.append(data[index]+ "\n");
		}		    
	}
	    
	public void getNumberOfLines() {
	    int counter = 0;
		// open the file
		openFile();
		// looping through file incrementing counter
		try {
		    String line = fileInput.readLine();
		    while (line != null) {
		        counter++;
			    line = fileInput.readLine();
			}
		    numLines = counter;
	    } catch(IOException ioException) { //error message if file could not be read
		    JOptionPane.showMessageDialog(frame,"Could not read file " + fileName, 
				 "I/O Error ",JOptionPane.ERROR_MESSAGE); 
		    System.exit(0);
		}	    
	}
	
	public void readFile() {
	    openFile();
		try{
		    for (int index=0;index <data.length;index++) {
		        data[index]=fileInput.readLine(); //fill in the array with text
			}
	    } catch(IOException ioException) {
		    JOptionPane.showMessageDialog(frame,"Error reading File", 
				 "I/O Error",JOptionPane.ERROR_MESSAGE); 
		    System.exit(1);
		}	   
	}
	// opennig and reading the file
	public void openFile() {
		try {
		    FileReader file = new FileReader(fileName);
		    fileInput = new BufferedReader(file);
		} catch(IOException ioException) {
		    JOptionPane.showMessageDialog(frame,"Error Opening File " +fileName, 
				 "I/O Error ",JOptionPane.ERROR_MESSAGE);
		}
	} 
	
	public void setFont(String s){
	    try{
		    int size  = Integer.parseInt(s);//Bill's example in class
		    text.setFont(new Font("Serif",Font.PLAIN, size));//setting the font to the size the user prefers
		} catch (NumberFormatException nfe) {	//if font is not set or null - then we set it to the following
				text.setFont(new Font("Serif", Font.PLAIN, 18));
		}
	}
	
	public static void main(String[] args) {//Bill's way to launch Java GUI application
		new JavaPad();
	}
}
