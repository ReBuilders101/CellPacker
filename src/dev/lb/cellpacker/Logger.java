package dev.lb.cellpacker;

public final class Logger {
	private Logger(){}
	
	public static void throwFatal(Throwable ex, int exitCode){
		System.err.println("[ErrorHandler]: A fatal error has occurred:");
		ex.printStackTrace();
		System.exit(exitCode);
	}
	
	public static void throwFatal(Throwable ex){
		throwFatal(ex, 1);
	}
	
	public static void printWarning(String trace, String warning){
		System.out.println("[WARNING|" + trace + "]: " + warning);
	}
	
}
