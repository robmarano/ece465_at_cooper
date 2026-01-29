package edu.cooper.ece465.commons.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(Utils.class);

	public enum STREAM_TYPE {
		WINDOWED, NON_WINDOWED
	};

	public static void printProperties(Properties prop, Logger log) {
		prop.keySet().stream()
				.map(key -> key + ": " + prop.getProperty(key.toString()))
				.forEach(log::info);
	}

	public static void handleException(Logger log, Exception ex1, String errorMessage) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex1.printStackTrace(pw);
		log.error(errorMessage, ex1);
		log.error(pw.toString());
	}

	public static void handleError(Logger log, Error ex1, String errorMessage) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex1.printStackTrace(pw);
		log.error(errorMessage, ex1);
		log.error(pw.toString());
	}

	public static byte[] compressAndEncode(String str) {
		return Utils.encode(compressGZ(str));
	}

	public static String decodeAndDecompress(byte[] zip) {
		return Utils.uncompressGZ(Utils.decode(zip));
	}

	public static String decodeAndDecompress(String zip) {
		return Utils.decodeAndDecompress(zip.getBytes());
	}

	public static byte[] encode(byte[] unencoded) {
		return Base64.getEncoder().encode(unencoded);
	}

	public static byte[] decode(byte[] encoded) {
		return Base64.getDecoder().decode(encoded);
	}

	public static boolean isZipped(final byte[] compressed) {
		return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC))
				&& (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
	}

	/**
	 * Compresses the String in GZIP format
	 */
	public static byte[] compressGZ(final String str) {

		try {
			if ((str == null) || (str.length() == 0)) {
				return null;
			}
			ByteArrayOutputStream obj = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(obj);
			gzip.write(str.getBytes("UTF-8"));
			gzip.flush();
			gzip.close();
			return obj.toByteArray();
		} catch (Exception ex) {
			String errorMessage = "Unable to compress item";
			Utils.handleException(log, ex, errorMessage);
		}
		return null;
	}

	/**
	 *
	 * Uncompresses the String in GZIP format
	 */
	public static String uncompressGZ(final byte[] zip) {

		if ((zip == null) || (zip.length == 0)) {
			return null;
		}
		String body = null;
		String charset = "UTF-8"; // You should determine it based on response header.

		try (InputStream gzippedResponse = new ByteArrayInputStream(zip);
				InputStream ungzippedResponse = new GZIPInputStream(gzippedResponse);
				Reader reader = new InputStreamReader(ungzippedResponse, charset);
				Writer writer = new StringWriter();) {
			char[] buffer = new char[10240];
			for (int length = 0; (length = reader.read(buffer)) > 0;) {
				writer.write(buffer, 0, length);
			}
			body = writer.toString();
		} catch (Exception ex) {
			String errorMessage = "Unable to uncompress item";
			Utils.handleException(log, ex, errorMessage);
		}

		return body;
	}

}
