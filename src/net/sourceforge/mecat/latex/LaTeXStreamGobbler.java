/*
 *  MeCat - Media Catalog more information on
 *  http://mecat.sourceforge.net/
 *  Copyright (C) 2004, Stephan Richard Palm, All Rights Reserved.
 *
 *  This program is free software; you can redistribute it and/or modify 
 *  it under the terms of the GNU General Public License as published by 
 *  the Free Software Foundation; either version 2 of the License, or 
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but 
 *  WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU 
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License 
 *  along with this program; if not, write to the Free Software Foundation, 
 *  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

/**
 * Created on Jul 29, 2004
 * @author Stephan Richard Palm
 *
 * Latex does not produce output to stderr therefor
 * this one thread should do.
 */
package net.sourceforge.mecat.latex;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Vector;

public class LaTeXStreamGobbler extends Thread {

    
        LaTeXErrorException currentException = null;

        boolean finished = false;
	
		double LastLen = Double.NaN;
		
		final String typeCommand = "(Please type a command or say `\\end')";
		final String error = "! LaTeX Error";
        final String error2 = "H for help, X to quit.";
        final String outputError = "! I can't write on file";
        final static String prevException = "Ran into previous exception.";
        final static String possibleExcpStmtGetLen = "Exception happend while trying to get len.";
        final static String possibleExcpStmtWaitFor = "Exception happend while waiting on LaTeX processing to be finished.";
	
        final int timeout = 60;
        
        String prevCommand = null;
        
	    InputStream is;
	    String type;
	    OutputStreamWriter os;
	    
	    boolean acceptCommand = false;
	    boolean failure = false;
	    
	    // Holding a list of all Panels that
	    // have to be up to date	    
//	    Vector<JTextArea> OutPanels = new Vector<JTextArea>();
//	    Vector<JTextArea> FilteredOutPanels = new Vector<JTextArea>();
//	    Vector<JTextArea> InPanels = new Vector<JTextArea>();
//	    Vector<JTextArea> MixPanels = new Vector<JTextArea>();
        
	    Vector<Writer> OutPanels = new Vector<Writer>();
	    Vector<Writer> FilteredOutPanels = new Vector<Writer>();
	    Vector<Writer> InPanels = new Vector<Writer>();
	    Vector<Writer> InNoLenPanels = new Vector<Writer>();
	    Vector<Writer> MixPanels = new Vector<Writer>();

	    boolean gettingLen = false;
	    
		String lastLine = null;
        StringBuffer currentLine = new StringBuffer();
        int LineNumber = 0;
        
        public void addOut(OutputStream stream) {
        	OutPanels.add(new OutputStreamWriter(stream));
        }
        public void addFilteredOut(OutputStream stream) {
        	FilteredOutPanels.add(new OutputStreamWriter(stream));
        }
        public void addMix(OutputStream stream) {
        	MixPanels.add(new OutputStreamWriter(stream));
        }
        public void addIn(OutputStream stream) {
        	InPanels.add(new OutputStreamWriter(stream));
        }
        public void addInNoLen(OutputStream stream) {
        	InNoLenPanels.add(new OutputStreamWriter(stream));
        }
        
        public void addOut(Writer stream) {
            OutPanels.add(stream);
        }
        public void addFilteredOut(Writer stream) {
            FilteredOutPanels.add(stream);
        }
        public void addMix(Writer stream) {
            MixPanels.add(stream);
        }
        public void addIn(Writer stream) {
            InPanels.add(stream);
        }
        public void addInNoLen(Writer stream) {
            InNoLenPanels.add(stream);
        }
        
/*        public JTextArea getOut() {
        	OutputStream ret = new OutputStream();
        	OutPanels.add(ret);
        	return ret;
        }
        public JTextArea getFilteredOut() {
        	JTextArea ret = new JTextArea();
        	FilteredOutPanels.add(ret);
        	return ret;
        }
        public JTextArea getIn() {
        	JTextArea ret = new JTextArea();
        	InPanels.add(ret);
        	return ret;
        }
        public JTextArea getMix() {
        	JTextArea ret = new JTextArea();
        	MixPanels.add(ret);
        	return ret;
        }*/
 	    void addTo(Vector<Writer> how, String line) {
	    	for (Writer stream : how)
	    		try {
	    			stream.write(line);
                    stream.flush();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
/*	    	if (how == MixPanels)
	    		System.out.print(line);*/
 	    }

 	    protected void resetLen() {
 	    	LastLen = Double.NaN;
 	    }

        protected void checkForException(String doing) throws LaTeXErrorException, NoAnswerException {
            if (currentException != null) {
                if (doing != null)
                    currentException.setMessage(doing);

                throw currentException;
            }
        }
        
 	    synchronized public double getLen(String command)  throws NoAnswerException, LaTeXErrorException {
            checkForException(prevException);
            
 	    	// We reset the system in order to later on
 	    	// get a new number from getLen();
 	    	resetLen();

 	    	gettingLen = true;
 	    	executeCommand("\\immediate\\write16{\\the" + command + "}");
 	    	gettingLen = false;

            try{
                return getLen();
            } catch (Exception e) {
                throw new LaTeXErrorException("Exception happend while trying to get len of:" +  command, e);
            }
 	    }

 	    synchronized public double getwidth(String command) throws NoAnswerException, LaTeXErrorException {
            checkForException(prevException);
 	    	// From here on InNoLen will not protocoled
 	    	gettingLen = true;

 	    	// We reset the system in order to later on
 	    	// get a new number from getLen();
 	    	resetLen();

 	    	// Create new LaTeX Length 'JavaTmpLen' iff it not already exists
 	    	String cmd = "\\ifx\\JavaTmpLen\\undefined\\newlength{\\JavaTmpLen}\\fi";
 	    	cmd += "\n";

    		// Set 'JavaTmpLen' to the width of the command
 	    	cmd += "\\settowidth{\\JavaTmpLen}{" + command + "}";
 	    	cmd += "\n";
 	    	
 	    	// Let LaTeX print the value of 'JavaTmpLen' to the stdout
 	    	cmd += "\\immediate\\write16{\\the\\JavaTmpLen}";
 	    	executeCommand(cmd);

 	    	// From here on InNoLen will be protocoled again
 	    	gettingLen = false;

 	    	// Because this function is synchronized
 	    	// the number we get here should be the 
 	    	// number from the request
            try{
                return getLen();
            } catch (Exception e) {
                throw new LaTeXErrorException("Exception happend while trying to get width of:" +  command, e);
            }
 	    }

 	    synchronized public double getheight(String command) throws NoAnswerException, LaTeXErrorException {
            checkForException(prevException);
 	    	// From here on InNoLen will not protocoled
 	    	gettingLen = true;

 	    	// We reset the system in order to later on
 	    	// get a new number from getLen();
 	    	resetLen();

 	    	// Create new LaTeX Length 'JavaTmpLen' iff it not already exists
 	    	String cmd = "\\ifx\\JavaTmpLen\\undefined\\newlength{\\JavaTmpLen}\\fi";
 	    	cmd += "\n";

    		// Set 'JavaTmpLen' to the width of the command
 	    	cmd += "\\settoheight{\\JavaTmpLen}{" + command + "}";
 	    	cmd += "\n";
 	    	
 	    	// Let LaTeX print the value of 'JavaTmpLen' to the stdout
 	    	cmd += "\\immediate\\write16{\\the\\JavaTmpLen}";
 	    	executeCommand(cmd);

 	    	// From here on InNoLen will be protocoled again
 	    	gettingLen = false;

 	    	// Because this function is synchronized
 	    	// the number we get here should be the 
 	    	// number from the request
            try{
                return getLen();
            } catch (Exception e) {
                throw new LaTeXErrorException("Exception happend while trying to get height of:" +  command, e);
            }
 	    }
 	    
 	    synchronized double getLen() throws NoAnswerException, LaTeXErrorException {
            long start = System.currentTimeMillis();
            while (Double.isNaN(LastLen)) {
	            try {
	                // wait for number to be read
	                wait(500);
                    checkForException(possibleExcpStmtGetLen);
	            } catch (InterruptedException e) {
                    checkForException(possibleExcpStmtGetLen);
	            }
                // Waited for 5 second. Don't thick there's 
                // any chance of an answer anymore
                if (System.currentTimeMillis() > start + (timeout * 1000)) {
                    currentException = new NoAnswerException();
                    this.interrupt();
                    throw (NoAnswerException) currentException;
                }
 	    	}
	        return (LastLen);
 	    }
        
        synchronized public void resetError() throws LaTeXErrorException, NoAnswerException {
            currentException = null;
            acceptCommand = true;
            executeCommand("");
        }

        synchronized public void cautiousJoin() throws LaTeXErrorException {
//            lookForError();
            int i;
            for (i = 0; i < 120; i ++) {
                try {
                    join(500);
                    if (!isAlive())
                        return;
                    checkForException("Exception while waiting for LaTeX to finish.");
                } catch (InterruptedException e) {
                    currentException = new LaTeXErrorException();
                    this.interrupt();
                    throw (LaTeXErrorException) currentException;
                }
//                lookForError();
            }
            if (i == 120) {
                currentException = new LaTeXTimeoutException();
                this.interrupt();
                throw (LaTeXTimeoutException) currentException;
            }
        }
        
        synchronized public void lookForError() throws LaTeXErrorException, NoAnswerException {
/*            try {
                // Give latex time to find the error
//                System.out.println(System.currentTimeMillis());
                Thread.sleep(1000);
//                System.out.println(System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            executeCommand("");
            executeCommand("");
        }

        /**
         * Caution this function should only be invoked if there was an error
         * and you know what to respond.
         * 
         * @param line
         * @throws LaTeXErrorException
         * @throws NoAnswerException
         */
        synchronized public void errorResponse(String line) throws LaTeXErrorException, NoAnswerException{
            currentException = null;
            acceptCommand = true;
            executeCommand(line);
        }

            
	    synchronized public void executeCommand(String line) throws LaTeXErrorException {
            long start = System.currentTimeMillis();
            String possibleExcpStmt = prevException;
            if (prevCommand != null)
                possibleExcpStmt = prevCommand;
            
	    	while (!acceptCommand) {
	    	    try {
	                // wait for latex to be ready again
	                wait(500);
                    checkForException(possibleExcpStmt);
	            } catch (InterruptedException e) {
                    checkForException(possibleExcpStmt);
                    System.err.println("LaTeXStreamGobbler interrupted without error.");
	            }
                // Waited for 5 second. Don't thick there's 
                // any chance of an answer anymore
                if (System.currentTimeMillis() > start + (timeout * 1000)) {
                    currentException = new LaTeXTimeoutException();
                    this.interrupt();
                    throw (LaTeXTimeoutException) currentException;
                }
	    	}
	    	acceptCommand = false;

            possibleExcpStmt = "Exception happend while executing " + line;
    		try {
				os.write((line+"\n")/*.getBytes()*/);
				os.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (!gettingLen)
	    		addTo(InNoLenPanels, line+"\n");
    		addTo(InPanels, line+"\n");
    		addTo(MixPanels, line+"\n");
            checkForException(possibleExcpStmt);
            prevCommand = possibleExcpStmt;
	    }

/*	    public boolean isSane() {
	    	return !failure;
	    }*/
	    
	    void writeCurrentLine() {
    		if (!getFilter(currentLine.toString()))
    			addTo(FilteredOutPanels, currentLine.toString());
    		
    		addTo(OutPanels, currentLine.toString());
			addTo(MixPanels, currentLine.toString());

			lastLine = new String(currentLine);
			currentLine = new StringBuffer();
			LineNumber++;
	    }
	    
	    synchronized void acceptNewCommand() {
	    	writeCurrentLine();
	    	
	    	acceptCommand = true;
	    	notifyAll();
	    }
	    
	    public LaTeXStreamGobbler(File dir)
	    {
//	    	this.addMix(System.out);
	    	Runtime rt = Runtime.getRuntime();
	    	Process proc;
			try {
				proc = rt.exec("latex", null, dir);
				is = proc.getInputStream();
		        os = new OutputStreamWriter(proc.getOutputStream(), "utf8");
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }

	    void fireReturn() {
	    	if (LineNumber < 2)
	    		return;
	    	if (newCommandState())
	    		return;
	    	if (currentLine.toString().compareTo("*") == 0) {
    			writeCurrentLine();
//	    		OutputStreamWriter osw = new OutputStreamWriter(os);
	    		try {
	    			os/*w*/.append('\n');
		    		os/*w*/.flush();
		    		addTo(MixPanels, "\n");
//		    		addTo(InPanels, "\n");
	    		} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
	    }
	    
	    boolean newCommandState() {
	    	if (lastLine == null)
	    		return false;
	    	
/*	    	if (lastLine.indexOf(typeCommand) == -1)
	    		return false;*/
	    	
	    	if (currentLine.toString().endsWith("*"))
	    		return true;
	    		
	    	return false;
	    }

        synchronized void foundFailure()  {
            foundFailure(new LaTeXErrorException());
        }
        
	    synchronized void foundFailure(LaTeXErrorException exception)  {
            if (currentException == null)
                currentException = exception;
			this.interrupt();
	    }
	    
	    boolean dezimal(char c) {
	    	return ((c >= '0') && (c <= '9'));
	    }
	    boolean doublepart(char c) {
	    	return (dezimal(c) || (c == '.') || (c == '-'));
	    }
	    
	    double getNumberFromCurrentLine() {
	    	if (!currentLine.toString().endsWith("pt"))
	    		return Double.NaN;

	    	try {
    		return Double.valueOf(currentLine.substring(0, currentLine.length() - 2)).doubleValue();
	    	} catch (Exception e) {
	    		return Double.NaN;
	    	}
	    	
	    }

/*	    double getNumberFromCurrentLine() {
	    	if (!currentLine.toString().endsWith("pt"))
	    		return Double.NaN;
	    	
	    	// cut the 'pt'
	    	int pos = currentLine.length() - 3;
	    	
	    	// There is no number on this line
	    	// there is not enough line for a number and 'pt'
	    	if (pos < 0)
	    		return Double.NaN;

	    	// Last character of a double must be a decimal
	    	if (!dezimal(currentLine.charAt(pos)))
	    		return Double.NaN;
	    	
	    	// Find the point of the double
	    	while (dezimal(currentLine.charAt(pos)) && (pos > 0))
	    		pos--;
	    	
	    	// This number has no point
	    	if (!doublepart(currentLine.charAt(pos)))
	    		return Double.valueOf(currentLine.substring(pos + 1, currentLine.length() - 2)).doubleValue();

	    	// This number has no point but is negative
	    	if (currentLine.charAt(pos) == '-')
	    		return Double.valueOf(currentLine.substring(pos, currentLine.length() - 2)).doubleValue();
	    	
	    	// We're at the beginning of the line. That means the point is not part of the number
	    	if (pos == 0)
	    		return Double.valueOf(currentLine.substring(pos + 1, currentLine.length() - 2)).doubleValue();

	    	pos--;

	    	// The point was not part of the number
	    	if (!dezimal(currentLine.charAt(pos)))
	    		return Double.valueOf(currentLine.substring(pos + 2, currentLine.length() - 2)).doubleValue();
	    		
	    	// Find the start of the double
	    	while (dezimal(currentLine.charAt(pos)) && (pos > 0))
	    		pos--;

	    	// The double is negative
	    	if ((currentLine.charAt(pos) == '-') || (dezimal(currentLine.charAt(pos))))
	    		pos--;
	    	
    		return Double.valueOf(currentLine.substring(pos + 1, currentLine.length() - 2)).doubleValue();
	    }*/
	    
	    void checkState() {
            if (currentException != null)
                return;


            if (currentLine.toString().endsWith(outputError)) 
                foundFailure(new LaTeXOutputExecption());
            
	    	// Check wether the last line indicates an error
	    	if (currentLine.toString().endsWith(error) || currentLine.toString().endsWith(error2))
	    	    foundFailure();

	    	// Get Length if returned
	    	if (currentLine.toString().endsWith("pt")) {
	    		LastLen = getNumberFromCurrentLine();
/*	    		if (!Double.isNaN(LastLen))
	    			notifyAll();*/
	    	}
	    	
	    	// See if there can be executed another without
	    	// running into failures from previus commands
	    	if (newCommandState()) {
	    		acceptNewCommand();
	    		return;
	    	}
	    	// This is necessary for the first command only
	    	if (currentLine.toString().compareTo("**") == 0) {
	    		acceptNewCommand();
	    		return;
	    	}
	    	
	    	// Fire an return if this gets us the information
	    	// if  the state is sane
	    	fireReturn();
	    }
	    
	    boolean getFilter(String line) {
	    	if (line.indexOf(typeCommand) != -1)
	    		return true;
	    	if (line.compareTo("**") == 0)
	    		return true;
	    	if (line.compareTo("*") == 0)
	    		return true;
	    	if ((line.compareTo("\n") == 0) || 
	    		(line.compareTo("\r") == 0) ||
	    		(line.compareTo("\r\n") == 0) )
	    		return true;
	    	
	    	return false;
	    }
	    
/*	    public synchronized void waitFor() throws LaTeXErrorException, NoAnswerException {
	    	while (!finished) {
	    		try {
	    			wait(1000);
	    		} catch (InterruptedException e) {
                    checkForException(possibleExcpStmtWaitFor);
	    		}
	    	}
//	        System.out.println("[ LaTeX ] Stoped waiting for end of process");
	    }*/

	    public void run()
	    {
	    	boolean foundWaitForCommand = false;
	    	
	        try
	        {
	            PrintWriter pw = null;
	            if (os != null)
	                pw = new PrintWriter(os);
	                
	            InputStreamReader isr = new InputStreamReader(is);
	            BufferedReader br = new BufferedReader(isr);

	            int ret;

	            while ( (ret = br.read()) != -1)
	            {
                    
                    currentLine.append((char)ret);
	            	// Check the state
	            	checkState();
	            	
	            	// Line has an and
	            	if (((char)ret == '\n') || ((char)ret == '\r')) {
	            		writeCurrentLine();
	            	}	            		
	            	
	            	
	            }
	        } catch (IOException ioe) {
	            ioe.printStackTrace();  
	        }
	        finished = true;
            if (currentException != null)
                this.interrupt();
//	        notifyAll();
//	        System.out.println("[ LaTeX ] Thread is finished");
	    }
}

