package modules.tandr.view;

import model.MFeatureVersion;
import modules.tandr.model.MFDirectEffect;
import modules.tandr.model.MFIndirectEffect;
import modules.tandr.model.MFTemporalEffect;
import modules.tandr.model.MTrustworthinessTandr;
import view.VTrustworthiness;

public class VTrustworthinessTandr extends VTrustworthiness{

	@Override
	public String getTrustworthinessString(MFeatureVersion fv) {
		
		MTrustworthinessTandr trust;
		if ( fv.getTrustworthiness() == null && (fv.getTrustworthinessUri() == null || fv.getTrustworthinessUri().equals("")) ) 
			trust = new MTrustworthinessTandr( fv );
		else trust = (MTrustworthinessTandr) fv.getTrustworthiness();
		
//		FTandrFacade ffacade = new FTandrFacade();
//		UDebug.print("\n\n" + ffacade.convertToRDFTTL(trust),4);
		
		MFDirectEffect direct = trust.getDirectEffect();
		MFIndirectEffect indirect = trust.getIndirectEffect();
		MFTemporalEffect temporal = trust.getTemporalEffect();
		
		String trustInfo = ""
				+ "Trustworthiness: " + trust.getValueString() + " {"
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
