package net.mortalsilence.indiepim.server.utils;

import javax.persistence.OptimisticLockException;

public class ExceptionUtils {

	public static boolean isOptimisticLockException( Throwable t ) {
		while( t != null ) {
			if( t instanceof OptimisticLockException) return true;
			t = ( t != t.getCause() ) ? t.getCause() : null;
		}
		return false;
	}

}
