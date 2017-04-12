package com.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import atg.commerce.order.OrderImpl;
import atg.commerce.order.purchase.CartModifierFormHandler;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.common.BBBGenericService;

/*
 * This AspectJ class defines the pointcut and handling of events before call and after call
 * of the methods that match the pointcut pattern
 * 
 */
@Aspect
public class AspectHandler extends BBBGenericService {

	private static final ApplicationLogging LOG = ClassLoggingFactory
			.getFactory().getLoggerForClass(AspectHandler.class);
	private static final String PURCHASE = "purchase";
	private static final String CHECKOUT = "checkout";
	private static final String CART = "cart";
	private static final String VERSION = "version";	
	private static final String CART_FORMHANDLER_COMPONENT = "atg/commerce/order/purchase/CartModifierFormHandler";

	@Pointcut("call(* com.bbb.account..*(..)) && (!call(* *.set*(..))) "
			+ "&& (!call(* *.get*(..))) && !call(* *.is*(..)) "
			+ "&& !call(* *.log*(..))  && !call(* *.toString*(..))")
	public void handleCall(){
		//do nothing
	}
	
	@Pointcut("call(* com.bbb.commerce.order.purchase..*(..)) && (!call(* *.set*(..))) "
			+ "&& (!call(* *.get*(..))) && !call(* *.is*(..)) "
			+ "&& !call(* *.log*(..))  && !call(* *.toString*(..))")
	public void handlePurchase(){
		//do nothing
	}
	
	@Pointcut("call(* com.bbb.commerce.cart..*(..)) && (!call(* *.set*(..))) "
			+ "&& (!call(* *.get*(..))) && !call(* *.is*(..)) "
			+ "&& !call(* *.log*(..))  && !call(* *.toString*(..))")
	public void handleCart(){
		//do nothing
	}
	
	@Pointcut("call(* com.bbb.commerce.checkout..*(..)) && (!call(* *.set*(..))) "
			+ "&& (!call(* *.get*(..))) && !call(* *.is*(..)) "
			+ "&& !call(* *.log*(..))  && !call(* *.toString*(..))")
	public void handleCheckout(){
		//do nothing
	}
	
	
	
	@AfterReturning(pointcut = "handleCall()", returning = "obj")
	public void afterReturningMethod(JoinPoint joinPoint, Object obj) {
		
		if(LOG.isLoggingDebug()) {
		try {
			StringBuilder logMessage = new StringBuilder();
			
			Signature sig = joinPoint.getSignature();

			logMessage.append(" -----ASPECTJ LOGGING STARTS------ ");
			if (sig != null) {
				logMessage.append("Method Invoked :").append(sig)
						.append("; ");
			}
			int index = 1;
			if (joinPoint.getArgs() != null) {
				logMessage.append("Params : ");
				Object[] arrayOfObject;
				int j = (arrayOfObject = joinPoint.getArgs()).length;
				for (int i = 0; i < j; i++) {
					Object arg = arrayOfObject[i];
					if (arg != null) {
						logMessage.append(index++).append("). ")
								.append(arg.toString()).append(",");
					} else {
						logMessage.append(index++).append("). null")
								.append("; ");
					}
				}
				if ((joinPoint.getArgs() != null)
						&& (joinPoint.getArgs().length == 0)) {
					logMessage.append("None; ");
				}
			}

			if (obj != null) {
				logMessage.append("Return : ").append(obj.toString());
			} else {
				logMessage.append("Return : void");
			}
			logMessage.append(" -----ASPECTJ LOGGING ENDS------ ");
				  LOG.logDebug(logMessage.toString());
		} catch (Exception ex) {
			 if (LOG.isLoggingError()) {
				 LOG.logError(ex.getMessage(),ex);
			 }
		}
		}
		
		
	}
	
	 @Around("handleCall() || handleCheckout() || handlePurchase() || handleCart()")
	  public Object logTimeMethod(ProceedingJoinPoint joinPoint) throws Throwable {
		 Signature sig = null;
		 StringBuilder logMessage = null;
		 CartModifierFormHandler cartFormHandler = null;
		 OrderImpl order = null;
		 int orderVersion = 0;
		 int repVersion = 0;
		 long startTime = 0L;
		 
		 if(LOG.isLoggingDebug()){
			 startTime = System.currentTimeMillis();			 
			 logMessage = new StringBuilder();			 
		 }		 
	 
	    Object retVal = joinPoint.proceed();
	    
	    if(LOG.isLoggingDebug()){
	    	if(null!=logMessage){
	    		logMessage.append(" -----ASPECTJ LOGGING STARTS------ ");
	    	}
	    	sig = joinPoint.getSignature();
	    	if (sig != null && null != sig.getDeclaringTypeName() && (sig.getDeclaringTypeName().contains(PURCHASE) 
	    			|| sig.getDeclaringTypeName().contains(CART) || sig.getDeclaringTypeName().contains(CHECKOUT))) {	    		
	    		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
	    		cartFormHandler = (CartModifierFormHandler)request.resolveName(CART_FORMHANDLER_COMPONENT);
				order = (OrderImpl) cartFormHandler.getOrder();
	 	    	orderVersion = order.getVersion();
	 		    repVersion = (Integer) order.getPropertyValue(VERSION);

	 			if (orderVersion != repVersion){
	 				logMessage.append("Method Invoked :").append(sig).append("; ");
	 				logMessage.append("After method call, Order version : ").append(orderVersion).append(" : Repository version : ").append(repVersion);
	 				logMessage.append("After method call, Order and repository version mis-matched.");
	 			}
	 	    }
	    	
	    	long endTime = System.currentTimeMillis();
	        long executionTime = (endTime - startTime);
	        logMessage.append("Time taken to execute " ).append(joinPoint.getSignature().getName()).append(" method took ").append(executionTime).append(" ms").toString();
			LOG.logDebug(logMessage.toString());
	    }	
      return retVal;
	  }

}
