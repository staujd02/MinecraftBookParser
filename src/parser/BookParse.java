package parser;

import java.io.*;
import java.util.*;
import javax.swing.*;

public class BookParse {

	private static File out;	//Output File
	private static File file;	//Read File
	
	/**
	 * This program takes a standard ASCII file for input (UTF untested)
	 * and outputs a similar ASCII file with line breaks to indicate
	 * where to copy and paste each section into the Minecraft(Copyright) 
	 * virtual book.
	 * 
	 * Written for window file system.
 	 * 
 	 * Preconditions: Must be a file with read permissions, and the .jar
 	 * 					file must be in a directory with file creation permitted
 	 * 
	 * @param args
	 */
	public static void main(String[] args){
		//Creates a file chooser object for the user to select
		JFileChooser filePick = new JFileChooser();
		//Scanner for reading the file
		Scanner read;
		//bool indicates the first line
		boolean firstLine = true;

		//Displays filePicker
		int response = filePick.showOpenDialog(null);
		
		//Exits if approve option isn't selected
		if(response != JFileChooser.APPROVE_OPTION){
			System.exit(0);
		}
		
		//Grabs file
		file = filePick.getSelectedFile();
	
		try {
			read = new Scanner(file);	//Opens read file
			
			//Creates a new write file (File name will be "book_[File Name]")
		    out = new File("book_" + file.getName());
		    BufferedWriter writer = new BufferedWriter(new FileWriter(out.getAbsolutePath()));
		    
			String str;			//One read line from the string file
			int lnCount = 0;	//Line Count: max 14  -- count as two characters
			int charCount = 0; 	//Char Count: max 256
			int push = 0;		//if this number reaches 19 increment Line count
			int pageCount = 0;	//50 pages -- next volume
			ArrayList<Integer> spaceLst = new ArrayList<Integer>();	//Used for better line break choosing
			int pointer = 0;	//Used as a cursor to navigate data
			int wordIndex = 0;
			
			while(read.hasNextLine()){
				//Read One line of the file
				str = read.nextLine();
				
				
				if(!firstLine){
					//Append the new line character
					writer.write("\r\n");
					//Increment counter
					lnCount++;
				}else{firstLine = false;}
				
				
				//Build an Array with locations for all space characters
				for(int i = 0; i < str.length(); i++){
					if(str.indexOf(i) == ' ')
						spaceLst.add(i);
				}
				
				//Write complete words to a page
				while(pointer < str.length()){
					//Check for a solid block of characters
					if(spaceLst.size() == 0){
						if(str.length() - pointer < 256){
							writer.write(str);
							if(str.contains("\n")){
								for(int i=0; i<str.length();i++)
									if(str.charAt(i) == '\n')
										lnCount++;
							}
							pointer = str.length();
							push += str.length();
							charCount += str.length();
						}
						else{
							writer.write(str.substring(pointer,pointer + 256));
							pointer += 256;
							push = 0;
							lnCount = 14;
							charCount = 256;
						}
					}
					else
					//Word does fit on the page
					if(spaceLst.get(wordIndex) - pointer < ((14 - lnCount) * 19) - push && spaceLst.get(wordIndex) - pointer < 256 - charCount){
							//Yes, Write Word
							writer.write(str.substring(pointer, spaceLst.get(wordIndex)));
							
							//If the word is going to be wrapped, increment line count
							if(spaceLst.get(wordIndex) - pointer > 19 - push){
								lnCount++;
							}
							
							//Add characters to push counter and character counter
							push += spaceLst.get(wordIndex) - pointer;
							charCount += spaceLst.get(wordIndex) - pointer;
							
							//Move pointer to beginning of word
							pointer = spaceLst.get(wordIndex);
							
							//
							if(wordIndex == spaceLst.size() - 1){
								pointer = str.length();
							}
							else
								wordIndex++;
					}
					else{
						//Throws a flag value to print to the next page
						lnCount = 14;
					}
					
					//Check lines and pushes
					//-----------------------
					//Push overflow (Enough Characters printed in a row to suggest 
					//a new Line will be generated [word wrap])
							//-> Reset push and increment line counter
					if(push >= 19){
						//Add the lines for pushed amount (in case the word was really big)
						lnCount = lnCount + (push / 19);
						//Update the push value
						push = push % 19;
					}
					
					//Check if a new page needs to be added
					if(lnCount >= 14 || charCount >= 256){
						//Next Page
						//---------------
						if(pageCount < 50){
							//Document the start of the new page
							writer.write("\r\n======NEXT PAGE========");
							
							//Increment page count
							pageCount++;
						}
						else{ //The maximum number of pages has been reached
							
							//Next Volume
							writer.write("\r\n======NEXT VOLUME========");
							pageCount = 0;
						}
						
						//Reset Page counters
						charCount = 0;
						push = 0;
						lnCount = 0;
					}
					
					if(spaceLst.size() > 0)
					//If one word won't fit on a page (write 256 chars and move pointer)
					if(spaceLst.get(wordIndex) - pointer > 256){
						writer.write(str.substring(pointer, pointer + 256));
						pointer += 256;
						lnCount = 14;
					}
						
				}//End of NewLine
			
				//Clear spcList
				spaceLst.clear();
				//reset pointer and word index
				pointer = 0; wordIndex = 0;
			
			}//End of File
			
			//Flush/Close Writer and Close Reader
			writer.flush();writer.close();
			read.close();
		} 
		
		
		catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null,"Your selected file was not found!", "ERROR: FNF", JOptionPane.ERROR_MESSAGE);
		} 
		catch (IOException e) {
			JOptionPane.showMessageDialog(null,"The file failed to write!", "ERROR: I/O Exception", JOptionPane.ERROR_MESSAGE);
		}
		finally{
			
		}
		
	}
}
