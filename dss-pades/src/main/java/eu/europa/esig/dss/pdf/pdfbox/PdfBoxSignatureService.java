/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 *
 * This file is part of the "DSS - Digital Signature Services" project.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.pdf.pdfbox;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSigProperties;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSignDesigner;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.europa.esig.dss.DSSASN1Utils;
import eu.europa.esig.dss.DSSDocument;
import eu.europa.esig.dss.DSSException;
import eu.europa.esig.dss.DSSUtils;
import eu.europa.esig.dss.DigestAlgorithm;
import eu.europa.esig.dss.InMemoryDocument;
import eu.europa.esig.dss.MimeType;
import eu.europa.esig.dss.pades.CertificationPermission;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.SignatureFieldParameters;
import eu.europa.esig.dss.pades.SignatureImageParameters;
import eu.europa.esig.dss.pades.signature.visible.ImageAndResolution;
import eu.europa.esig.dss.pades.signature.visible.ImageUtils;
import eu.europa.esig.dss.pdf.DSSDictionaryCallback;
import eu.europa.esig.dss.pdf.PDFSignatureService;
import eu.europa.esig.dss.pdf.PdfDict;
import eu.europa.esig.dss.pdf.PdfDssDict;
import eu.europa.esig.dss.pdf.PdfSignatureInfo;
import eu.europa.esig.dss.pdf.PdfSignatureOrDocTimestampInfo;
import eu.europa.esig.dss.pdf.PdfSignatureOrDocTimestampInfoComparator;
import eu.europa.esig.dss.pdf.SignatureValidationCallback;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.x509.CertificatePool;
import eu.europa.esig.dss.x509.CertificateToken;
import eu.europa.esig.dss.x509.Token;
import eu.europa.esig.dss.x509.crl.CRLToken;
import eu.europa.esig.dss.x509.ocsp.OCSPToken;

class PdfBoxSignatureService implements PDFSignatureService {

	private static final Logger LOG = LoggerFactory.getLogger(PdfBoxSignatureService.class);

	protected PdfBoxVisibleSignatureDrawer visibleSignatureDrawer = new DefaultPdfBoxVisibleSignatureDrawer();

	/**
	 * This method allows to inject a custom {@Code PdfBoxVisibleSignatureDrawer}
	 * 
	 * @param visibleSignatureDrawer
	 *            an implementation of {@Code PdfBoxVisibleSignatureDrawer} which
	 *            generates the visible signature
	 */
	public void setVisibleSignatureDrawer(PdfBoxVisibleSignatureDrawer visibleSignatureDrawer) {
		this.visibleSignatureDrawer = visibleSignatureDrawer;
	}

	@Override
	public byte[] digest(final DSSDocument toSignDocument, final PAdESSignatureParameters parameters, final DigestAlgorithm digestAlgorithm)
			throws DSSException {

		final byte[] signatureValue = DSSUtils.EMPTY_BYTE_ARRAY;
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				InputStream is = toSignDocument.openStream();
				PDDocument pdDocument = PDDocument.load(is)) {

			PDSignature pdSignature = createSignatureDictionary(parameters, pdDocument);
			return signDocumentAndReturnDigest(parameters, signatureValue, outputStream, pdDocument, pdSignature, digestAlgorithm);
		} catch (IOException e) {
			throw new DSSException(e);
		}
	}

	@Override
	public DSSDocument sign(final DSSDocument toSignDocument, final byte[] signatureValue, final PAdESSignatureParameters parameters,
			final DigestAlgorithm digestAlgorithm) throws DSSException {

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				InputStream is = toSignDocument.openStream();
				PDDocument pdDocument = PDDocument.load(is)) {

			final PDSignature pdSignature = createSignatureDictionary(parameters, pdDocument);
			signDocumentAndReturnDigest(parameters, signatureValue, baos, pdDocument, pdSignature, digestAlgorithm);

			DSSDocument signature = new InMemoryDocument(baos.toByteArray());
			signature.setMimeType(MimeType.PDF);
			return signature;
		} catch (IOException e) {
			throw new DSSException(e);
		}
	}

	private byte[] signDocumentAndReturnDigest(final PAdESSignatureParameters parameters, final byte[] signatureBytes, final OutputStream fileOutputStream,
			final PDDocument pdDocument, final PDSignature pdSignature, final DigestAlgorithm digestAlgorithm) throws DSSException {

		try (SignatureOptions options = createSignatureOptions(pdDocument, parameters)) {

			final MessageDigest digest = DSSUtils.getMessageDigest(digestAlgorithm);
			// register signature dictionary and sign interface
			SignatureInterface signatureInterface = new SignatureInterface() {

				@Override
				public byte[] sign(InputStream content) throws IOException {

					byte[] b = new byte[4096];
					int count;
					while ((count = content.read(b)) > 0) {
						digest.update(b, 0, count);
					}
					return signatureBytes;
				}
			};

			options.setPreferredSignatureSize(parameters.getSignatureSize());
			if (parameters.getSignatureImageParameters() != null) {
				fillImageParameters(pdDocument, parameters.getSignatureImageParameters(), options);
			}
			pdDocument.addSignature(pdSignature, signatureInterface, options);

			saveDocumentIncrementally(parameters, fileOutputStream, pdDocument);
			final byte[] digestValue = digest.digest();
			if (LOG.isDebugEnabled()) {
				LOG.debug("Digest to be signed: " + Utils.toHex(digestValue));
			}
			return digestValue;
		} catch (IOException e) {
			throw new DSSException(e);
		}
	}

	private void fillImageParameters(final PDDocument doc, final SignatureImageParameters imgParams, SignatureOptions options) throws IOException {

		// DSS-747. Using the DPI resolution to convert java size to dot
		ImageAndResolution ires = ImageUtils.create(imgParams);

		InputStream is = ires.getInputStream();
		try {
			PDVisibleSignDesigner visibleSig = new PDVisibleSignDesigner(doc, is, imgParams.getPage());
			visibleSig.xAxis(imgParams.getxAxis());
			visibleSig.yAxis(imgParams.getyAxis());
			visibleSig.width(ires.toXPoint(visibleSig.getWidth()));
			visibleSig.height(ires.toYPoint(visibleSig.getHeight()));
			visibleSig.zoom(imgParams.getZoom() - 100); // pdfbox is 0 based

			PDVisibleSigProperties signatureProperties = new PDVisibleSigProperties();
			signatureProperties.visualSignEnabled(true).setPdVisibleSignature(visibleSig);
			if (imgParams.getSignatureReason() != null) // alidev
				signatureProperties.signatureReason(imgParams.getSignatureReason());
			if (imgParams.getSignerLocation() != null) // alisdev
				signatureProperties.signerLocation(imgParams.getSignerLocation());
			signatureProperties.buildSignature();
			options.setVisualSignature(signatureProperties);
			options.setPage(imgParams.getPage() - 1); // DSS-1138
		} finally {
			Utils.closeQuietly(is);
		}
	}

	protected SignatureOptions createSignatureOptions(PDDocument pdDocument, PAdESSignatureParameters parameters)
			throws IOException {
		SignatureImageParameters signatureImageParameters = parameters.getSignatureImageParameters();
		return visibleSignatureDrawer.createVisualSignature(pdDocument, signatureImageParameters);
	}

	private PDSignature createSignatureDictionary(final PAdESSignatureParameters parameters, PDDocument pdDocument) {

		PDSignature signature;
		if (Utils.isStringNotEmpty(parameters.getSignatureFieldId())) {
			signature = findExistingSignature(pdDocument, parameters.getSignatureFieldId());
		} else {
			signature = new PDSignature();
		}

		signature.setType(getType());
		signature.setFilter(getFilter(parameters));
		// sub-filter for basic and PAdES Part 2 signatures
		signature.setSubFilter(getSubFilter(parameters));

		if (COSName.SIG.equals(getType())) {

			if (parameters.getSignatureName() != null) {
				signature.setName(parameters.getSignatureName());
			} else {

				CertificateToken token = parameters.getSigningCertificate();
				Date date = parameters.bLevel().getSigningDate();
				String encodedDate = Utils
						.toHex(DSSUtils.digest(DigestAlgorithm.SHA1, Long.toString(date.getTime()).getBytes()));

				if (token == null) {
					signature.setName("Unknown signer" + encodedDate);
				} else {
					String shortName = DSSASN1Utils.getHumanReadableName(token) + encodedDate;
					signature.setName(shortName);
				}
			}

			if (Utils.isStringNotEmpty(parameters.getContactInfo())) {
				signature.setContactInfo(parameters.getContactInfo());
			}

			if (Utils.isStringNotEmpty(parameters.getLocation())) {
				signature.setLocation(parameters.getLocation());
			}

			if (Utils.isStringNotEmpty(parameters.getReason())) {
				signature.setReason(parameters.getReason());
			}

			CertificationPermission permission = parameters.getPermission();
			// A document can contain only one signature field that contains a DocMDP transform method;
			// it shall be the first signed field in the document.
			if (permission != null && !containsFilledSignature(pdDocument)) {
				setMDPPermission(pdDocument, signature, permission.getCode());
			}

			// the signing date, needed for valid signature
			final Calendar cal = Calendar.getInstance();
			final Date signingDate = parameters.bLevel().getSigningDate();
			cal.setTime(signingDate);
			signature.setSignDate(cal);
		}

		return signature;
	}

	private boolean containsFilledSignature(PDDocument pdDocument) {
		try {
			List<PDSignature> signatures = pdDocument.getSignatureDictionaries();
			for (PDSignature pdSignature : signatures) {
				if (pdSignature.getCOSObject().containsKey(COSName.BYTERANGE)) {
					return true;
				}
			}
			return false;
		} catch (IOException e) {
			LOG.warn("Cannot read the existing signature(s)", e);
			return false;
		}
	}

	/**
	 * Set the access permissions granted for this document in the DocMDP transform parameters
	 * dictionary. Details are described in the table "Entries in the DocMDP transform parameters
	 * dictionary" in the PDF specification.
	 *
	 * @param doc
	 *            The document.
	 * @param signature
	 *            The signature object.
	 * @param accessPermissions
	 *            The permission value (1, 2 or 3).
	 */
	public void setMDPPermission(PDDocument doc, PDSignature signature, int accessPermissions) {
		COSDictionary sigDict = signature.getCOSObject();

		// DocMDP specific stuff
		COSDictionary transformParameters = new COSDictionary();
		transformParameters.setItem(COSName.TYPE, COSName.getPDFName("TransformParams"));
		transformParameters.setInt(COSName.P, accessPermissions);
		transformParameters.setName(COSName.V, "1.2");
		transformParameters.setNeedToBeUpdated(true);

		COSDictionary referenceDict = new COSDictionary();
		referenceDict.setItem(COSName.TYPE, COSName.getPDFName("SigRef"));
		referenceDict.setItem("TransformMethod", COSName.DOCMDP);
		referenceDict.setItem("TransformParams", transformParameters);
		referenceDict.setNeedToBeUpdated(true);

		COSArray referenceArray = new COSArray();
		referenceArray.add(referenceDict);
		sigDict.setItem("Reference", referenceArray);
		referenceArray.setNeedToBeUpdated(true);

		// Document Catalog
		COSDictionary catalogDict = doc.getDocumentCatalog().getCOSObject();
		COSDictionary permsDict = new COSDictionary();
		catalogDict.setItem(COSName.PERMS, permsDict);
		permsDict.setItem(COSName.DOCMDP, signature);
		catalogDict.setNeedToBeUpdated(true);
		permsDict.setNeedToBeUpdated(true);
	}

	private PDSignature findExistingSignature(PDDocument doc, String sigFieldName) {
		PDAcroForm acroForm = doc.getDocumentCatalog().getAcroForm();
		if (acroForm != null) {
			PDSignatureField signatureField = (PDSignatureField) acroForm.getField(sigFieldName);
			if (signatureField != null) {
				PDSignature signature = signatureField.getSignature();
				if (signature == null) {
					signature = new PDSignature();
					signatureField.getCOSObject().setItem(COSName.V, signature);
					return signature;
				} else {
					throw new DSSException("The signature field '" + sigFieldName + "' can not be signed since its already signed.");
				}
			}
		}
		throw new DSSException("The signature field '" + sigFieldName + "' does not exist.");
	}

	public void saveDocumentIncrementally(PAdESSignatureParameters parameters, OutputStream outputStream, PDDocument pdDocument) throws DSSException {
		try {
			// the document needs to have an ID, if not a ID based on the current system time is used, and then the
			// digest of the signed data is different
			if (pdDocument.getDocumentId() == null) {

				final byte[] documentIdBytes = DSSUtils.digest(DigestAlgorithm.SHA256, parameters.bLevel().getSigningDate().toString().getBytes());
				pdDocument.setDocumentId(DSSUtils.toLong(documentIdBytes));
			}
			pdDocument.saveIncremental(outputStream);
		} catch (IOException e) {
			throw new DSSException(e);
		}
	}

	protected COSName getType() {
		return COSName.SIG;
	}

	protected COSName getFilter(PAdESSignatureParameters parameters) {
		if (Utils.isStringNotEmpty(parameters.getSignatureFilter())) {
			return COSName.getPDFName(parameters.getSignatureFilter());
		}
		return PDSignature.FILTER_ADOBE_PPKLITE;
	}

	protected COSName getSubFilter(PAdESSignatureParameters parameters) {
		if (Utils.isStringNotEmpty(parameters.getSignatureSubFilter())) {
			return COSName.getPDFName(parameters.getSignatureSubFilter());
		}
		return PDSignature.SUBFILTER_ETSI_CADES_DETACHED;
	}

	@Override
	public void validateSignatures(CertificatePool validationCertPool, DSSDocument document, SignatureValidationCallback callback) throws DSSException {
		// recursive search of signature
		try (InputStream inputStream = document.openStream()) {
			List<PdfSignatureOrDocTimestampInfo> signaturesFound = getSignatures(validationCertPool, Utils.toByteArray(inputStream));
			for (PdfSignatureOrDocTimestampInfo pdfSignatureOrDocTimestampInfo : signaturesFound) {
				callback.validate(pdfSignatureOrDocTimestampInfo);
			}
		} catch (IOException e) {
			LOG.error("Cannot validate signatures : " + e.getMessage(), e);
		}
	}

	private List<PdfSignatureOrDocTimestampInfo> getSignatures(CertificatePool validationCertPool, byte[] originalBytes) {
		List<PdfSignatureOrDocTimestampInfo> signatures = new ArrayList<PdfSignatureOrDocTimestampInfo>();
		try (PDDocument doc = PDDocument.load(originalBytes)) {

			int originalBytesLength = originalBytes.length;

			List<PDSignature> pdSignatures = doc.getSignatureDictionaries();

			if (Utils.isCollectionNotEmpty(pdSignatures)) {
				LOG.debug("{} signature(s) found", pdSignatures.size());

				PdfDict catalog = new PdfBoxDict(doc.getDocumentCatalog().getCOSObject(), doc);
				PdfDssDict dssDictionary = PdfDssDict.extract(catalog);

				for (PDSignature signature : pdSignatures) {
					String subFilter = signature.getSubFilter();

					COSDictionary dict = signature.getCOSObject();
					COSString item = (COSString) dict.getDictionaryObject(COSName.CONTENTS);
					byte[] cms = item.getBytes();

					byte[] cmsWithByteRange = signature.getContents(originalBytes);

					if (!Arrays.equals(cmsWithByteRange, cms)) {
						LOG.warn("The byte range doesn't match found /Content value!");
					}

					if (Utils.isStringEmpty(subFilter) || Utils.isArrayEmpty(cms)) {
						LOG.warn("Wrong signature with empty subfilter or cms.");
						continue;
					}

					byte[] signedContent = signature.getSignedContent(originalBytes);
					int[] byteRange = signature.getByteRange();

					// /ByteRange [0 575649 632483 10206]
					int beforeSignatureLength = byteRange[1] - byteRange[0];
					int expectedCMSLength = byteRange[2] - byteRange[1];
					int afterSignatureLength = byteRange[3];
					int totalCoveredByByteRange = beforeSignatureLength + expectedCMSLength + afterSignatureLength;

					boolean coverAllOriginalBytes = (originalBytesLength == totalCoveredByByteRange);

					PdfDict signatureDictionary = new PdfBoxDict(signature.getCOSObject(), doc);
					PdfSignatureOrDocTimestampInfo signatureInfo = null;
					if (PdfBoxDocTimeStampService.SUB_FILTER_ETSI_RFC3161.getName().equals(subFilter)) {
						boolean isArchiveTimestamp = false;

						// LT or LTA
						if (dssDictionary != null) {
							// check is DSS dictionary already exist
							if (isDSSDictionaryPresentInPreviousRevision(getOriginalBytes(byteRange, signedContent))) {
								isArchiveTimestamp = true;
							}
						}

						signatureInfo = new PdfBoxDocTimestampInfo(validationCertPool, signature, signatureDictionary, dssDictionary, cms, signedContent,
								coverAllOriginalBytes, isArchiveTimestamp);
					} else {
						signatureInfo = new PdfBoxSignatureInfo(validationCertPool, signature, signatureDictionary, dssDictionary, cms, signedContent,
								coverAllOriginalBytes);
					}

					if (signatureInfo != null) {
						signatures.add(signatureInfo);
					}
				}
				Collections.sort(signatures, new PdfSignatureOrDocTimestampInfoComparator());
				linkSignatures(signatures);

				for (PdfSignatureOrDocTimestampInfo sig : signatures) {
					LOG.debug("Signature " + sig.uniqueId() + " found with byteRange " + Arrays.toString(sig.getSignatureByteRange()) + " ("
							+ sig.getSubFilter() + ")");
				}
			}

		} catch (Exception e) {
			LOG.warn("Cannot analyze signatures : " + e.getMessage(), e);
		}

		return signatures;
	}

	/**
	 * This method links previous signatures to the new one. This is useful to get revision number and to know if a TSP
	 * is over the DSS dictionary
	 */
	private void linkSignatures(List<PdfSignatureOrDocTimestampInfo> signatures) {
		List<PdfSignatureOrDocTimestampInfo> previousList = new ArrayList<PdfSignatureOrDocTimestampInfo>();
		for (PdfSignatureOrDocTimestampInfo sig : signatures) {
			if (Utils.isCollectionNotEmpty(previousList)) {
				for (PdfSignatureOrDocTimestampInfo previous : previousList) {
					previous.addOuterSignature(sig);
				}
			}
			previousList.add(sig);
		}
	}

	private boolean isDSSDictionaryPresentInPreviousRevision(byte[] originalBytes) {
		PdfDssDict dssDictionary = null;
		try (PDDocument doc = PDDocument.load(originalBytes)) {
			List<PDSignature> pdSignatures = doc.getSignatureDictionaries();
			if (Utils.isCollectionNotEmpty(pdSignatures)) {
				PdfDict catalog = new PdfBoxDict(doc.getDocumentCatalog().getCOSObject(), doc);
				dssDictionary = PdfDssDict.extract(catalog);
			}
		} catch (Exception e) {
			LOG.warn("Cannot check in previous revisions if DSS dictionary already exist : " + e.getMessage(), e);
		}

		return dssDictionary != null;
	}

	private byte[] getOriginalBytes(int[] byteRange, byte[] signedContent) {
		final int length = byteRange[1];
		final byte[] result = new byte[length];
		System.arraycopy(signedContent, 0, result, 0, length);
		return result;
	}

	@Override
	public DSSDocument addDssDictionary(DSSDocument document, List<DSSDictionaryCallback> callbacks) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); InputStream is = document.openStream(); PDDocument pdDocument = PDDocument.load(is)) {

			if (Utils.isCollectionNotEmpty(callbacks)) {
				final COSDictionary cosDictionary = pdDocument.getDocumentCatalog().getCOSObject();
				cosDictionary.setItem("DSS", buildDSSDictionary(callbacks));
				cosDictionary.setNeedToBeUpdated(true);
			}

			pdDocument.saveIncremental(baos);

			DSSDocument inMemoryDocument = new InMemoryDocument(baos.toByteArray());
			inMemoryDocument.setMimeType(MimeType.PDF);
			return inMemoryDocument;

		} catch (Exception e) {
			throw new DSSException(e);
		}
	}

	private COSDictionary buildDSSDictionary(List<DSSDictionaryCallback> callbacks) throws Exception {
		COSDictionary dss = new COSDictionary();

		Map<String, COSStream> streams = new HashMap<String, COSStream>();

		Set<CRLToken> allCrls = new HashSet<CRLToken>();
		Set<OCSPToken> allOcsps = new HashSet<OCSPToken>();
		Set<CertificateToken> allCertificates = new HashSet<CertificateToken>();

		COSDictionary vriDictionary = new COSDictionary();
		for (DSSDictionaryCallback callback : callbacks) {
			COSDictionary sigVriDictionary = new COSDictionary();
			sigVriDictionary.setDirect(true);

			if (Utils.isCollectionNotEmpty(callback.getCertificates())) {
				COSArray vriCertArray = new COSArray();
				for (CertificateToken token : callback.getCertificates()) {
					vriCertArray.add(getStream(streams, token));
					allCertificates.add(token);
				}
				sigVriDictionary.setItem("Cert", vriCertArray);
			}

			if (Utils.isCollectionNotEmpty(callback.getOcsps())) {
				COSArray vriOcspArray = new COSArray();
				for (OCSPToken token : callback.getOcsps()) {
					vriOcspArray.add(getStream(streams, token));
					allOcsps.add(token);
				}
				sigVriDictionary.setItem("OCSP", vriOcspArray);
			}

			if (Utils.isCollectionNotEmpty(callback.getCrls())) {
				COSArray vriCrlArray = new COSArray();
				for (CRLToken token : callback.getCrls()) {
					vriCrlArray.add(getStream(streams, token));
					allCrls.add(token);
				}
				sigVriDictionary.setItem("CRL", vriCrlArray);
			}

			// We can't use CMSSignedData, the pdSignature content is trimmed (000000)
			PdfSignatureInfo pdfSignatureInfo = callback.getSignature().getPdfSignatureInfo();
			final byte[] digest = DSSUtils.digest(DigestAlgorithm.SHA1, pdfSignatureInfo.getContent());
			String hexHash = Utils.toHex(digest).toUpperCase();

			vriDictionary.setItem(hexHash, sigVriDictionary);
		}
		dss.setItem("VRI", vriDictionary);

		if (Utils.isCollectionNotEmpty(allCertificates)) {
			COSArray arrayAllCerts = new COSArray();
			for (CertificateToken token : allCertificates) {
				arrayAllCerts.add(getStream(streams, token));
			}
			dss.setItem("Certs", arrayAllCerts);
		}

		if (Utils.isCollectionNotEmpty(allOcsps)) {
			COSArray arrayAllOcsps = new COSArray();
			for (OCSPToken token : allOcsps) {
				arrayAllOcsps.add(getStream(streams, token));
			}
			dss.setItem("OCSPs", arrayAllOcsps);
		}

		if (Utils.isCollectionNotEmpty(allCrls)) {
			COSArray arrayAllCrls = new COSArray();
			for (CRLToken token : allCrls) {
				arrayAllCrls.add(getStream(streams, token));
			}
			dss.setItem("CRLs", arrayAllCrls);
		}

		return dss;
	}

	private COSStream getStream(Map<String, COSStream> streams, Token token) throws IOException {
		COSStream stream = streams.get(token.getDSSIdAsString());

		if (stream == null) {
			stream = new COSStream();

			try (OutputStream unfilteredStream = stream.createOutputStream()) {
				unfilteredStream.write(token.getEncoded());
				unfilteredStream.flush();
			}
			streams.put(token.getDSSIdAsString(), stream);
		}
		return stream;
	}

	@Override
	public List<String> getAvailableSignatureFields(DSSDocument document) throws DSSException {
		List<String> result = new ArrayList<String>();
		try (InputStream is = document.openStream()) {
			PDDocument pdfDoc = PDDocument.load(is);
			List<PDSignatureField> signatureFields = pdfDoc.getSignatureFields();
			for (PDSignatureField pdSignatureField : signatureFields) {
				PDSignature signature = pdSignatureField.getSignature();
				if (signature == null) {
					result.add(pdSignatureField.getPartialName());
				}
			}
		} catch (Exception e) {
			throw new DSSException("Unable to determine signature fields", e);
		}
		return result;
	}

	@Override
	public DSSDocument addNewSignatureField(DSSDocument document, SignatureFieldParameters parameters) {
		DSSDocument newPdfDoc = null;
		try (InputStream is = document.openStream()) {
			PDDocument pdfDoc = PDDocument.load(is);
			PDPage page = pdfDoc.getPage(parameters.getPage());

			PDAcroForm acroForm = pdfDoc.getDocumentCatalog().getAcroForm();
			if (acroForm == null) {
				acroForm = new PDAcroForm(pdfDoc);
				pdfDoc.getDocumentCatalog().setAcroForm(acroForm);

				// Set default appearance
				PDResources resources = new PDResources();
				resources.put(COSName.getPDFName("Helv"), PDType1Font.HELVETICA);
				acroForm.setDefaultResources(resources);
				acroForm.setDefaultAppearance("/Helv 0 Tf 0 g");
			}

			PDSignatureField signatureField = new PDSignatureField(acroForm);
			if (Utils.isStringNotBlank(parameters.getName())) {
				signatureField.setPartialName(parameters.getName());
			}
			PDAnnotationWidget widget = signatureField.getWidgets().get(0);
			PDRectangle rect = new PDRectangle(parameters.getOriginX(), parameters.getOriginY(), parameters.getWidth(), parameters.getHeight());
			widget.setRectangle(rect);
			widget.setPage(page);
			page.getAnnotations().add(widget);
			acroForm.getFields().add(signatureField);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			pdfDoc.save(baos);
			pdfDoc.close();
			newPdfDoc = new InMemoryDocument(baos.toByteArray(), "new-document.pdf", MimeType.PDF);

		} catch (Exception e) {
			throw new DSSException("Unable to add a new signature fields", e);
		}
		return newPdfDoc;
	}

}