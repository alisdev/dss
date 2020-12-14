package eu.europa.esig.dss.cookbook.example.sign;

import eu.europa.esig.dss.cookbook.example.CookbookTools;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.JWSSerializationType;
import eu.europa.esig.dss.enumerations.SigDMechanism;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.jades.HTTPHeader;
import eu.europa.esig.dss.jades.HTTPHeaderDigest;
import eu.europa.esig.dss.jades.JAdESSignatureParameters;
import eu.europa.esig.dss.jades.signature.JAdESService;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * How to sign HTTP request with JAdES-BASELINE-B detached signature.
 */
public class SignHttpHeadersJadesBTest extends CookbookTools {

    @Test
    public void signHttpHeadersJadesBaselineB() throws Exception {

        // GET document to be signed -
        // Return DSSDocument toSignDocument
        prepareXmlDoc();

        // Get a token connection based on a pkcs12 file commonly used to store private
        // keys with accompanying public key certificates, protected with a password-based
        // symmetric key -
        // Return AbstractSignatureTokenConnection signingToken

        // and it's first private key entry from the PKCS12 store
        // Return DSSPrivateKeyEntry privateKey *****
        try (SignatureTokenConnection signingToken = getPkcs12Token()) {

            DSSPrivateKeyEntry privateKey = signingToken.getKeys().get(0);

            JAdESSignatureParameters parameters = new JAdESSignatureParameters();
            parameters.setSigningCertificate(privateKey.getCertificate());
            parameters.setCertificateChain(privateKey.getCertificateChain());
            parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
            parameters.setSignatureLevel(SignatureLevel.JAdES_BASELINE_B);
            parameters.setJwsSerializationType(JWSSerializationType.FLATTENED_JSON_SERIALIZATION);

            // tag::demo[]

            // Set Detached packaging
            parameters.setSignaturePackaging(SignaturePackaging.DETACHED);
            // Set Mechanism HttpHeaders for 'sigD' header
            parameters.setSigDMechanism(SigDMechanism.HTTP_HEADERS);
            // The HttpHeaders mechanism shall be used with unencoded JWS payload ("b64"="false")
            // tag::unencodedPayload[]
            parameters.setBase64UrlEncodedPayload(false);
            // end::unencodedPayload[]
            // Create a list of headers to be signed
            List<DSSDocument> documentsToSign = new ArrayList<>();
            documentsToSign.add(new HTTPHeader("content-type", "application/json"));
            documentsToSign.add(new HTTPHeader("x-example", "HTTP Headers Example"));
            documentsToSign.add(new HTTPHeader("x-example", "Duplicated Header"));
            // Add a document representing the HTTP message body (optional)
            // Requires the message body content + digest algorithm to compute the hash to be signed
            documentsToSign.add(new HTTPHeaderDigest(toSignDocument, DigestAlgorithm.SHA1));

            // end::demo[]

            CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
            JAdESService service = new JAdESService(commonCertificateVerifier);

            ToBeSigned dataToSign = service.getDataToSign(documentsToSign, parameters);
            DigestAlgorithm digestAlgorithm = parameters.getDigestAlgorithm();
            SignatureValue signatureValue = signingToken.sign(dataToSign, digestAlgorithm, privateKey);

            DSSDocument signedDocument = service.signDocument(documentsToSign, parameters, signatureValue);

            testFinalDocument(signedDocument);
        }
    }

}
