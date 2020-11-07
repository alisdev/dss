package eu.europa.esig.dss.compatibility.v54.validation.reports.wrapper;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

/**
 * Supported signature encryption algorithms.
 */
enum EncryptionAlgorithm {

	RSA("RSA", "1.2.840.113549.1.1.1", "RSA/ECB/PKCS1Padding"),

	DSA("DSA", "1.2.840.10040.4.1", "DSA"),

	ECDSA("ECDSA", "1.2.840.10045.2.1", "ECDSA"),

	PLAIN_ECDSA("PLAIN-ECDSA", "0.4.0.127.0.7.1.1.4.1", "PLAIN-ECDSA"),

	HMAC("HMAC", "", "");

	private String name;
	private String oid;
	private String padding;

	private static class Registry {

		private static final Map<String, EncryptionAlgorithm> OID_ALGORITHMS = registerOIDAlgorithms();

		private static Map<String, EncryptionAlgorithm> registerOIDAlgorithms() {
			Map<String, EncryptionAlgorithm> map = new HashMap<String, EncryptionAlgorithm>();
			for (EncryptionAlgorithm encryptionAlgorithm : values()) {
				map.put(encryptionAlgorithm.oid, encryptionAlgorithm);
			}
			return map;
		}
	}

	/**
	 * Returns the encryption algorithm associated to the given OID.
	 *
	 * @param oid
	 *            the ASN1 algorithm OID
	 * @return the linked encryption algorithm
	 * @throws DSSException
	 *             if the oid doesn't match any algorithm
	 */
	public static EncryptionAlgorithm forOID(String oid) {
		EncryptionAlgorithm algorithm = Registry.OID_ALGORITHMS.get(oid);
		if (algorithm == null) {
			throw new RuntimeException("Unsupported algorithm: " + oid);
		}
		return algorithm;
	}

	/**
	 * Returns the encryption algorithm associated to the given key.
	 *
	 * @param key the key
	 * @return the linked encryption algorithm
	 * @throws DSSException if the key doesn't match any algorithm
	 */
	public static EncryptionAlgorithm forKey(Key key) {
		return forName(key.getAlgorithm());
	}

	/**
	 * Returns the encryption algorithm associated to the given JCE name.
	 *
	 * @param name
	 *            the encryption algorithm name
	 * @return the linked encryption algorithm
	 * @throws DSSException
	 *             if the name doesn't match any algorithm
	 */
	public static EncryptionAlgorithm forName(final String name) {
		// To be checked if ECC exists also .
		if ("EC".equals(name) || "ECC".equals(name)) {
			return ECDSA;
		}

		try {
			return valueOf(name);
		} catch (Exception e) {
			throw new RuntimeException("Unsupported algorithm: " + name);
		}
	}

	/**
	 * Returns the encryption algorithm associated to the given JCE name.
	 *
	 * @param name
	 *            the encryption algorithm name
	 * @param defaultValue
	 *            The default value for the {@code EncryptionAlgorithm}
	 * @return the corresponding {@code EncryptionAlgorithm} or the default value
	 */
	public static EncryptionAlgorithm forName(final String name, final EncryptionAlgorithm defaultValue) {
		// To be checked if ECC exists also .
		if ("EC".equals(name) || "ECC".equals(name)) {
			return ECDSA;
		}

		try {
			return valueOf(name);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	EncryptionAlgorithm(String name, String oid, String padding) {
		this.name = name;
		this.oid = oid;
		this.padding = padding;
	}

	/**
	 * Get the algorithm name
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the ASN1 algorithm OID
	 * 
	 * @return the OID
	 */
	public String getOid() {
		return oid;
	}

	/**
	 * Get the algorithm padding
	 * 
	 * @return the padding
	 */
	public String getPadding() {
		return padding;
	}

}
