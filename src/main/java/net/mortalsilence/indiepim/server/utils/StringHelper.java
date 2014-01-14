package net.mortalsilence.indiepim.server.utils;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.List;

public class StringHelper {

	final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");
	
	public static String inputStreamToString(InputStream in) {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;

		try {			
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line + "\n");
			}
			
			bufferedReader.close();
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
		return stringBuilder.toString();
	}

	public static String mySqlConcatWs(final List<String> strings, final String separator) {
		if(strings == null)
			throw new IllegalArgumentException("Parameter strings must not be null");
		if(separator == null)
			throw new IllegalArgumentException("Parameter separator must not be null");		
		final StringBuffer buf = new StringBuffer();
		for(final String str : strings) {
			if(str != null) {
				if(buf.length() > 0)
					buf.append(separator);
				buf.append(str);
			}
		}
		return buf.toString();
	} 

	
	public static String md5( String source ) {
		try {
			MessageDigest md = MessageDigest.getInstance( "MD5" );
			byte[] bytes = md.digest( source.getBytes("UTF-8") );
			return getString( bytes );
		} catch( Exception e )      {
			e.printStackTrace();
			return null;
		}
	}

	private static String getString( byte[] bytes ) 
	{
		StringBuffer buf = new StringBuffer();
		for( int i=0; i<bytes.length; i++ )     
		{
			byte b = bytes[ i ];
			String hex = Integer.toHexString((int) 0x00FF & b);
			if (hex.length() == 1) 
				buf.append("0");
			buf.append( hex );
		}
		return buf.toString();
	}
	
	
}
