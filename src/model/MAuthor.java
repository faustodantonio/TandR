package model;

import foundation.FFoundationFacade;
import utility.UConfig;

public class MAuthor {
	
	private String uri;
	private String accountName;
	private String accountServerHomepage;

	private String reputationUri;
	private MReputation reputation;
	
	private FFoundationFacade foundation;
	
	public MAuthor() {
		super();
		this.foundation = new FFoundationFacade();
	}
	public String getUri() {
		if (this.uri == null) this.uri = "";
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getAccountServerHomepage() {
		return accountServerHomepage;
	}
	public void setAccountServerHomepage(String accountServerHomepage) {
		this.accountServerHomepage = accountServerHomepage;
	}
	public MReputation getReputation() {
		if (this.reputation == null) {
			if (this.getReputationUri() != null && ! this.getReputationUri().equals(""))
				this.setReputation( (MReputation) foundation.retrieveByUri(this.getReputationUri(), UConfig.getTANDRGraphURI(), 0, MReputation.class) );
		} else this.reputation = null;
		return reputation;
	}
	public void setReputation(MReputation reputation) {
		this.reputation = reputation;
	}
	public String getReputationUri() {
		return reputationUri;
	}
	public void setReputationUri(String reputationUri) {
		this.reputationUri = reputationUri;
	}
	public void setReputationUri(String reputationUri, int lazyDepth) {
		this.reputationUri = reputationUri;
		if (lazyDepth > 0)
			this.setReputation( (MReputation) foundation.retrieveByUri(this.getReputationUri(), UConfig.getTANDRGraphURI(), lazyDepth - 1, MReputation.class) );
	}
	
	public String toString(String rowPrefix)
	{
		String authorString = "";
		
		authorString = rowPrefix +  "Author :" + "\n"
				+ rowPrefix + "\t uri                   = \""+ this.getUri() +"\"\n"
				+ rowPrefix +  "\t accountName           = \""+ this.getAccountName() +"\"\n"
				+ rowPrefix +  "\t accountServerHomepage = \""+ this.getAccountServerHomepage() +"\"\n";
		
		return authorString;
	}
	
}
