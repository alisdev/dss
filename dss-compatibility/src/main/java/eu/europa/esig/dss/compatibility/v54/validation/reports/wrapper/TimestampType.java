package eu.europa.esig.dss.compatibility.v54.validation.reports.wrapper;

/**
 * Source of the timestamp
 *
 */
enum TimestampType {

	CONTENT_TIMESTAMP, // CAdES: id-aa-ets-contentTimestamp
	ALL_DATA_OBJECTS_TIMESTAMP, //XAdES: AllDataObjectsTimestamp
	INDIVIDUAL_DATA_OBJECTS_TIMESTAMP, // XAdES: IndividualDataObjectsTimeStamp
	SIGNATURE_TIMESTAMP, // CAdES: id-aa-signatureTimeStampToken, XAdES: SignatureTimeStamp
	VALIDATION_DATA_REFSONLY_TIMESTAMP, // CAdES: id-aa-ets-certCRLTimestamp, XAdES: RefsOnlyTimeStamp
	VALIDATION_DATA_TIMESTAMP, // CAdES: id-aa-ets-escTimeStamp, XAdES: SigAndRefsTimeStamp
	ARCHIVE_TIMESTAMP // CAdES: id-aa-ets-archiveTimestamp, XAdES: ArchiveTimeStamp, PAdES-LTV "document timestamp"
}
