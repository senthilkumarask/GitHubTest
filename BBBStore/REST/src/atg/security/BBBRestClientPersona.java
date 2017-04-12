package atg.security;

import java.util.Collection;

import atg.nucleus.GenericService;
import atg.repository.Repository;

/**
 * BBBRestClientPersona implements Persona
 * @author akhaju
 *
 */
public class BBBRestClientPersona extends GenericService implements Persona {

	private UserAuthority mAuthority;
	private Repository mRepository;
	private String mRoleName;
	private boolean mAuthenticated;
	private String mToken;
	private String mClientID;

	/**
	 * @return the token
	 */
	public String getToken() {
		return mToken;
	}

	/**
	 * @return the clientID
	 */
	public String getClientID() {
		return mClientID;
	}

	/**
	 * @param pClientID the clientID to set
	 */
	public void setClientID(String pClientID) {
		mClientID = pClientID;
	}

	/**
	 * @param pToken the token to set
	 */
	public void setToken(String pToken) {
		mToken = pToken;
	}


	/**
	 * @return the authority
	 */
	public UserAuthority getAuthority() {
		return mAuthority;
	}

	/**
	 * @param pAuthority the authority to set
	 */
	public void setAuthority(UserAuthority pAuthority) {
		mAuthority = pAuthority;
	}

	/**
	 * @return the authenticated
	 */
	public boolean isAuthenticated() {
		return mAuthenticated;
	}

	/**
	 * @param pAuthenticated the authenticated to set
	 */
	public void setAuthenticated(boolean pAuthenticated) {
		mAuthenticated = pAuthenticated;
	}

	public String getRoleName() {
		return mRoleName;
	}

	public void setRoleName(String pRoleName) {
		mRoleName = pRoleName;
	}

	/**
	 * @return the repository
	 */
	public Repository getRepository() {
		return mRepository;
	}

	/**
	 * @param pRepository the repository to set
	 */
	public void setRepository(Repository pRepository) {
		mRepository = pRepository;
	}

	public BBBRestClientPersona() {
		// default constructor
	}

	public BBBRestClientPersona(UserAuthority pAuthority) {
		this.mAuthority = pAuthority;
	}

	public void setUserAuthority(UserAuthority pAuthority) {
		this.mAuthority = pAuthority;
	}

	public String getName() {
		return getRoleName();
	}

	public UserAuthority getUserAuthority() {
		return this.mAuthority;
	}

	public boolean equals(Object pObject) {
		return ((pObject instanceof EveryonePersona)
				&& (this.mAuthority.equals(((BBBRestClientPersona) pObject)
						.getUserAuthority())) || (pObject instanceof BBBRestClientPersona));
	}

	public boolean hasPersona(Persona persona) {
		return this.getName().equals(persona.getName());
	}

	public Persona[] getSubPersonae() {
		return null;
	}

	public void setSubPersonae(Persona[] newSubPersonae) throws InvalidPersonaException {
		//do nothing
	}

	public void addSubPersona(Persona newSubPersona) throws InvalidPersonaException {
		//do nothing
	}

	public void removeSubPersona(Persona oldPersona) throws InvalidPersonaException {
		throw new InvalidPersonaException(oldPersona.getName());
	}

	public Object getAttribute(String attributeName) {
		return null;
	}

	public boolean hasRole(String roleName) {
		return true;
	}

	public Collection getAccessRights() {
		return null;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 1;
	}
	
}

