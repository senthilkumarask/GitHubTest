package atg.endeca.assembler;

import com.endeca.infront.assembler.CartridgeHandler;
import com.endeca.infront.assembler.CartridgeHandlerException;

public class BBBNucleusAssembler extends NucleusAssembler {

	private boolean lookupHandlerPath=true;
	
	public boolean isLookupHandlerPath() {
		return lookupHandlerPath;
	}

	public void setLookupHandlerPath(boolean lookupHandlerPath) {
		this.lookupHandlerPath = lookupHandlerPath;
	}
	
	public BBBNucleusAssembler(NucleusAssemblerFactory pNucleusAssemblerFactory, boolean lookupHandlerPath) {
		super(pNucleusAssemblerFactory);
		this.setLookupHandlerPath(lookupHandlerPath);
	}

	/* (non-Javadoc)
	 * @see atg.endeca.assembler.NucleusAssembler#getCartridgeHandler(java.lang.String)
	 */
	protected CartridgeHandler<?> getCartridgeHandler(String pCartridgeType)
	        throws CartridgeHandlerException
	    {
		if (getNucleusAssemblerFactory().isLoggingDebug()) {
			getNucleusAssemblerFactory()
					.logDebug("Enter BBBNucleusAssembler.getCartridgeHandler method");
		}
		if (getNucleusAssemblerFactory().getHandlerMapping().containsKey(
				pCartridgeType)) {
			String handlerPath = (String) getNucleusAssemblerFactory()
					.getHandlerMapping().get(pCartridgeType);
			CartridgeHandler cartridgeHandler = resolveHandler(handlerPath);
			if (getNucleusAssemblerFactory().isLoggingDebug()) {
				getNucleusAssemblerFactory()
						.logDebug(
								new StringBuilder()
										.append("Returning ")
										.append(handlerPath)
										.append(" from handlerMapping for cartridge type ")
										.append(pCartridgeType).toString());
			}
			return cartridgeHandler;
		}
		
		if(!isLookupHandlerPath()){
        	if(getNucleusAssemblerFactory().isLoggingDebug())
        		getNucleusAssemblerFactory().logDebug((new StringBuilder()).append("Performance fix: isLookupHandlerPath is false, Lookup for handler not mapped in NucleusAssemblerFactory is being skipped for cartridge type ").append(pCartridgeType).toString());
        	return null;
        }

		StringBuilder handlerBuilder = new StringBuilder();
		if (getNucleusAssemblerFactory().getAssemblerTools()
				.isExperienceManager()) {
			handlerBuilder.append(getNucleusAssemblerFactory()
					.getExperienceManagerHandlerPath());
		} else {
			handlerBuilder.append(getNucleusAssemblerFactory()
					.getGuidedSearchHandlerPath());
		}

		if (!(handlerBuilder.toString().endsWith("/"))) {
			handlerBuilder.append("/");
		}
		handlerBuilder.append(pCartridgeType);

		CartridgeHandler cartridgeHandler = resolveHandler(handlerBuilder
				.toString());
		if (cartridgeHandler != null) {
			if (getNucleusAssemblerFactory().isLoggingDebug()) {
				getNucleusAssemblerFactory()
						.logDebug(
								new StringBuilder()
										.append("Returning handler from installed product path: ")
										.append(handlerBuilder.toString())
										.toString());
			}
			return cartridgeHandler;
		}

		handlerBuilder = new StringBuilder(getNucleusAssemblerFactory()
				.getDefaultHandlerPath());
		if (!(handlerBuilder.toString().endsWith("/"))) {
			handlerBuilder.append("/");
		}
		handlerBuilder.append(pCartridgeType);

		cartridgeHandler = resolveHandler(handlerBuilder.toString());
		if (cartridgeHandler != null) {
			if (getNucleusAssemblerFactory().isLoggingDebug()) {
				getNucleusAssemblerFactory()
						.logDebug(
								new StringBuilder()
										.append("Returning handler from defaultHandlerPath: ")
										.append(handlerBuilder.toString())
										.toString());
			}
			return cartridgeHandler;
		}

		if (getNucleusAssemblerFactory().isLoggingDebug()) {
			getNucleusAssemblerFactory().logDebug(
					new StringBuilder()
							.append("No handler found for cartridge type ")
							.append(pCartridgeType).toString());
		}
		return null;
	}
	
}
