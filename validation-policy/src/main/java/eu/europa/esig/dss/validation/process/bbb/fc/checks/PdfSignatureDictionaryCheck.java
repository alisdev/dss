package eu.europa.esig.dss.validation.process.bbb.fc.checks;

import eu.europa.esig.dss.detailedreport.jaxb.XmlFC;
import eu.europa.esig.dss.diagnostic.PDFRevisionWrapper;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.policy.jaxb.LevelConstraint;
import eu.europa.esig.dss.validation.process.ChainItem;

/**
 * This class verifies whether the corresponding signature dictionary is consistent across PDF revisions.
 *
 */
public class PdfSignatureDictionaryCheck extends ChainItem<XmlFC> {

    /** The PDF revision */
    private final PDFRevisionWrapper pdfRevision;

    /**
     * Default constructor
     *
     * @param i18nProvider {@link I18nProvider}
     * @param result {@link XmlFC}
     * @param pdfRevision {@link SignatureWrapper}
     * @param constraint {@link LevelConstraint}
     */
    public PdfSignatureDictionaryCheck(I18nProvider i18nProvider, XmlFC result, PDFRevisionWrapper pdfRevision, LevelConstraint constraint) {
        super(i18nProvider, result, constraint);
        this.pdfRevision = pdfRevision;
    }

    @Override
    protected boolean process() {
        return pdfRevision.isPdfSignatureDictionaryConsistent();
    }

    @Override
    protected MessageTag getMessageTag() {
        return MessageTag.BBB_FC_ISDC;
    }

    @Override
    protected MessageTag getErrorMessageTag() {
        return MessageTag.BBB_FC_ISDC_ANS;
    }

    @Override
    protected Indication getFailedIndicationForConclusion() {
        return Indication.FAILED;
    }

    @Override
    protected SubIndication getFailedSubIndicationForConclusion() {
        return SubIndication.FORMAT_FAILURE;
    }

}
