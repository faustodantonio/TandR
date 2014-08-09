package utility;

public class UDebug {

	public static void print(String text, int level)
	{
		if (level <= UConfig.debugLevel)
			System.out.print(text);
	}
	
}
