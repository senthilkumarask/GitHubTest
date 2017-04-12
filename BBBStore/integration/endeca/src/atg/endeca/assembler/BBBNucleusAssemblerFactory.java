package atg.endeca.assembler;

import java.util.List;

import java.util.concurrent.CopyOnWriteArrayList;

import com.endeca.infront.assembler.Assembler;
import com.endeca.infront.assembler.AssemblerException;

import com.endeca.infront.assembler.event.AssemblerEventListener;


public class BBBNucleusAssemblerFactory extends NucleusAssemblerFactory {

	private boolean lookupHandlerPath;

	public boolean isLookupHandlerPath() {
		return lookupHandlerPath;
	}

	public void setLookupHandlerPath(boolean lookupHandlerPath) {
		this.lookupHandlerPath = lookupHandlerPath;
	}

	private List<AssemblerEventListener> mAssemblerEventListeners;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atg.endeca.assembler.NucleusAssemblerFactory#getAssemblerEventListeners()
	 */
	public AssemblerEventListener[] getAssemblerEventListeners() {
		return (AssemblerEventListener[]) mAssemblerEventListeners
				.toArray(new AssemblerEventListener[0]);
	}

	public void addAssemblerEventListener(AssemblerEventListener pListener) {
		mAssemblerEventListeners.add(pListener);
	}

	public void removeAssemblerEventListener(AssemblerEventListener pListener) {
		mAssemblerEventListeners.remove(pListener);
	}

	/**
	 * 
	 */
	public BBBNucleusAssemblerFactory() {

		super();
		this.mAssemblerEventListeners = new CopyOnWriteArrayList();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atg.endeca.assembler.NucleusAssemblerFactory#createAssembler()
	 */
	public Assembler createAssembler() throws AssemblerException {
		BBBNucleusAssembler assembler = new BBBNucleusAssembler(this,
				isLookupHandlerPath());
		if (!mAssemblerEventListeners.isEmpty()) {
			AssemblerEventListener arr$[] = getAssemblerEventListeners();
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++) {
				AssemblerEventListener listenerCur = arr$[i$];
				assembler.addAssemblerEventListener(listenerCur);
			}

		}
		return assembler;
	}
}
