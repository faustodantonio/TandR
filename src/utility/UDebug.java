package utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class UDebug {
	
//	FileOutputStream output;
	private static PrintStream output;
	private static PrintStream log;

	public static void print(String text, int level){
		if (level <= UConfig.debugLevel)
			System.out.print(text);
	}

	public static void error(String text) {
		System.err.print(" *** ERROR: ");
		print(text, 1);
	}
	
	public static void output(String text, int level){
		
		if (level <= UConfig.debugLevel)
			System.out.print(text);
		
		getOutputFile().print(text);
	}
	
	public static void log(String text, int level){
		
		if (level <= UConfig.debugLevel)
			System.out.print(text);
		
		getLogFile().print(text);
	}
	
	private static PrintStream getOutputFile() {
		
		String path = UConfig.generalOutputFilePath;
		File file = new File(path);
		
		if ( ! file.exists() )
			file.getParentFile().mkdirs();
		
		if (UDebug.output == null)
		{
			try
		    {
				FileOutputStream outputFile = new FileOutputStream(path);
		        UDebug.output = new PrintStream(outputFile);
		    }
		    catch (IOException e)
		    {
		        System.out.println("Errore: " + e);
		        System.exit(1);
		    }
		}
		
		return UDebug.output;
	}
	
	private static PrintStream getLogFile() {
		
		String path = UConfig.logFilePath;
		File file = new File(path);
		
		if ( ! file.exists() )
			file.getParentFile().mkdirs();
		
		if (UDebug.log == null)
		{
			try
		    {
				FileOutputStream logFile = new FileOutputStream(path);
		        UDebug.log = new PrintStream(logFile);
		    }
		    catch (IOException e)
		    {
		        System.out.println("Errore: " + e);
		        System.exit(1);
		    }
		}
		
		return UDebug.log;
	}
	
}
