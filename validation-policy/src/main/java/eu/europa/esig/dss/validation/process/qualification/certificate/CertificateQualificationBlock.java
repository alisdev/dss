package eu.europa.esig.dss.validation.process.qualification.certificate;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.europa.esig.dss.jaxb.detailedreport.XmlCertificate;
import eu.europa.esig.dss.jaxb.detailedreport.XmlConclusion;
import eu.europa.esig.dss.jaxb.detailedreport.XmlTLAnalysis;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.ValidationTime;
import eu.europa.esig.dss.validation.policy.rules.Indication;
import eu.europa.esig.dss.validation.process.Chain;
import eu.europa.esig.dss.validation.process.ChainItem;
import eu.europa.esig.dss.validation.process.qualification.signature.checks.AcceptableTrustedListCheck;
import eu.europa.esig.dss.validation.process.qualification.trust.filter.TrustedServiceFilter;
import eu.europa.esig.dss.validation.process.qualification.trust.filter.TrustedServicesFilterFactory;
import eu.europa.esig.dss.validation.reports.wrapper.CertificateWrapper;
import eu.europa.esig.dss.validation.reports.wrapper.TrustedServiceWrapper;

public class CertificateQualificationBlock extends Chain<XmlCertificate> {

	private final XmlConclusion buildingBlocksConclusion;
	private final Date validationTime;
	private final CertificateWrapper signingCertificate;
	private final CertificateWrapper rootCertificate;
	private final List<XmlTLAnalysis> tlAnalysis;
	private final String lotlCountryCode;

	public CertificateQualificationBlock(XmlConclusion buildingBlocksConclusion, Date validationTime, CertificateWrapper signingCertificate,
			CertificateWrapper rootCertificate, List<XmlTLAnalysis> tlAnalysis, String lotlCountryCode) {
		super(new XmlCertificate());

		this.buildingBlocksConclusion = buildingBlocksConclusion;
		this.validationTime = validationTime;
		this.signingCertificate = signingCertificate;
		this.rootCertificate = rootCertificate;
		this.tlAnalysis = tlAnalysis;
		this.lotlCountryCode = lotlCountryCode;
	}

	@Override
	protected void initChain() {

		// cover incomplete cert chain / expired/ revoked certs
		ChainItem<XmlCertificate> item = firstItem = isAcceptableBuildingBlockConclusion(buildingBlocksConclusion);

		if (signingCertificate != null && signingCertificate.hasTrustedServices()) {

			XmlTLAnalysis lotlAnalysis = getTlAnalysis(lotlCountryCode);
			if (lotlAnalysis != null) {
				item = item.setNextItem(isAcceptableTL(lotlAnalysis));
			}

			List<TrustedServiceWrapper> originalTSPs = signingCertificate.getTrustedServices();

			// 1. filter by service for CAQC
			TrustedServiceFilter filter = TrustedServicesFilterFactory.createFilterByCaQc();
			List<TrustedServiceWrapper> caqcServices = filter.filter(originalTSPs);

			Set<String> caQcCountryCodes = getCountryCodes(caqcServices);
			for (String countryCode : caQcCountryCodes) {
				XmlTLAnalysis currentTL = getTlAnalysis(countryCode);
				if (currentTL != null) {
					item = item.setNextItem(isAcceptableTL(currentTL));
				}
			}

			CertQualificationAtTimeBlock certQualAtIssuanceBlock = new CertQualificationAtTimeBlock(ValidationTime.CERTIFICATE_ISSUANCE_TIME,
					signingCertificate, rootCertificate, caqcServices);
			result.getValidationCertificateQualification().add(certQualAtIssuanceBlock.execute());

			CertQualificationAtTimeBlock certQualAtSigningTimeBlock = new CertQualificationAtTimeBlock(ValidationTime.VALIDATION_TIME, validationTime,
					signingCertificate, rootCertificate, caqcServices);
			result.getValidationCertificateQualification().add(certQualAtSigningTimeBlock.execute());

		}
	}

	private XmlTLAnalysis getTlAnalysis(String countryCode) {
		for (XmlTLAnalysis xmlTLAnalysis : tlAnalysis) {
			if (Utils.areStringsEqual(countryCode, xmlTLAnalysis.getCountryCode())) {
				return xmlTLAnalysis;
			}
		}
		return null;
	}

	private Set<String> getCountryCodes(List<TrustedServiceWrapper> caqcServices) {
		Set<String> countryCodes = new HashSet<String>();
		for (TrustedServiceWrapper trustedServiceWrapper : caqcServices) {
			countryCodes.add(trustedServiceWrapper.getCountryCode());
		}
		return countryCodes;
	}

	@Override
	protected void addAdditionalInfo() {
		collectErrorsWarnsInfos();
		setIndication();
	}

	private void setIndication() {
		XmlConclusion conclusion = result.getConclusion();
		if (conclusion != null) {
			if (Utils.isCollectionNotEmpty(conclusion.getErrors())) {
				conclusion.setIndication(Indication.FAILED);
			} else if (Utils.isCollectionNotEmpty(conclusion.getWarnings())) {
				conclusion.setIndication(Indication.INDETERMINATE);
			} else {
				conclusion.setIndication(Indication.PASSED);
			}
		}
	}

	private ChainItem<XmlCertificate> isAcceptableTL(XmlTLAnalysis xmlTLAnalysis) {
		return new AcceptableTrustedListCheck<XmlCertificate>(result, xmlTLAnalysis, getWarnLevelConstraint());
	}

	private ChainItem<XmlCertificate> isAcceptableBuildingBlockConclusion(XmlConclusion buildingBlocksConclusion) {
		return new AcceptableBuildingBlockConclusionCheck(result, buildingBlocksConclusion, getWarnLevelConstraint());
	}

}
