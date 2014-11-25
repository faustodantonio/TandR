package modules.tandr.view;


import model.MAuthor;
import modules.tandr.model.MFDirectEffect;
import modules.tandr.model.MFIndirectEffect;
import modules.tandr.model.MFTemporalEffect;
import modules.tandr.model.MReputationTandr;
import view.VReputation;

public class VReputationTandr extends VReputation{

	@Override
	public String getReputationString(MAuthor author) {
		
		MReputationTandr reputation;
		
		reputation = (MReputationTandr) author.getReputation();
		
//		FTandrFacade ffacade = new FTandrFacade();
//		UDebug.print("\n\n" + ffacade.convertToRDFTTL(trust),4);
		
		MFDirectEffect   direct   = reputation.getDirectEffect();
		MFIndirectEffect indirect = reputation.getIndirectEffect();
		MFTemporalEffect temporal = reputation.getTemporalEffect();
		
		String trustInfo = ""
				+ "Reputation: " + reputation.getValueString() + " {"
				+ "Direct: "     + direct.getValueString() + " "
					+ "[geom->"  + direct.getGeometricAspect().getValueString()
					+ "; qual->" + direct.getQualitativeAspect().getValueString()
					+ "; sem->"  + direct.getSemanticAspect().getValueString()
					+"] "
				+ "Indirect: "   + indirect.getValueString() + " "
					+ "[geom->"  + indirect.getGeometricAspect().getValueString()
					+ "; qual->" + indirect.getQualitativeAspect().getValueString()
					+ "; sem->"  + indirect.getSemanticAspect().getValueString()
					+"] "
				+ "Temporal: "   + temporal.getValueString() + " "
				+ "}"
				;
		
		return trustInfo;
	}

}
