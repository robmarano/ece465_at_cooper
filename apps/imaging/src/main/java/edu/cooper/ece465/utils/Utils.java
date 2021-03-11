package edu.cooper.ece465.utils;

import java.io.*;
import java.io.FileOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

// import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Utils implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(Utils.class);
	// private static final Logger log = Logger.getLogger(Utils.class);

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

	public static void sendFile(String path, DataOutputStream dos) throws Exception {
		File file = new File(path);
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);
		DataInputStream dis = new DataInputStream(bis);

		int read;
		byte[] byteArray = new byte[8*1024];
//		dos.writeUTF(file.getName());
//		dos.writeLong(byteArray.length);

		while((read = dis.read(byteArray)) != -1) {
			dos.write(byteArray, 0, read);
		}
		dos.flush();
	}

	public static void receiveFile(String path, DataInputStream dis, int bufferSize) throws Exception {
//		int bufferSize = socket.getReceiveBufferSize();
//		InputStream in = socket.getInputStream();

//		String fileName = dis.readUTF();
//		System.out.println(fileName);
//		long byteArrayLength = dis.readLong();
//		System.out.println(byteArrayLength);

		OutputStream output = new FileOutputStream(path);
		byte[] buffer = new byte[bufferSize];
		int read;
		while((read = dis.read(buffer)) != -1){
			output.write(buffer, 0, read);
		}
		output.flush();
	}

	public static byte[] toBytes(char[] chars) {
		CharBuffer charBuffer = CharBuffer.wrap(chars);
		ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
		byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
				byteBuffer.position(), byteBuffer.limit());
		Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
		return bytes;
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
		return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC)) && (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
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
			Utils.handleException(LOG, ex, errorMessage);
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
			Utils.handleException(LOG, ex, errorMessage);
		}
		
		return body;
	}

}
