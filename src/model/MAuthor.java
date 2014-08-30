package model;

public class MAuthor {
	
	private String uri;
	private String accountName;
	private String accountServerHomepage;
	
	private MReputation reputation;
	
	public String getUri() {
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
		return reputation;
	}
	public void setReputation(MReputation reputation) {
		this.reputation = reputation;
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
