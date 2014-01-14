package net.mortalsilence.indiepim.server.controller;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.Calendar;

@Aspect
public class CommandControllerLogAspect {

    final static Logger logger = Logger.getLogger("aspectj");

    @Around("execution(* net.mortalsilence.indiepim.server.controller.CommandController.*(..))")
    public Object commandController(ProceedingJoinPoint pjp) throws Throwable {
        final long start = Calendar.getInstance().getTimeInMillis();
        Object ret = pjp.proceed();
        final long duration = Calendar.getInstance().getTimeInMillis() - start;
        logger.trace(pjp.getSignature().getName() + " took " + duration + " ms.");
        return ret;
    }
}
