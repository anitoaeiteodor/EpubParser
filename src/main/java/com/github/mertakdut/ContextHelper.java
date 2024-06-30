package com.github.mertakdut;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.github.mertakdut.exception.ReadingException;

/**
 * 
 * @author Mert
 * 
 *         Includes commonly needed general methods.
 */
class ContextHelper {

	private ContextHelper() {
		// private constructor
	}

	static String encodeToUtf8(String stringToEncode) {

		String encodedString = null;

        encodedString = URLDecoder.decode(stringToEncode, StandardCharsets.UTF_8); // Charset.forName("UTF-8").name()
        encodedString = URLEncoder.encode(encodedString, StandardCharsets.UTF_8).replace("+", "%20"); // Charset.forName("UTF-8").name()

        return encodedString;
	}

	static byte[] convertIsToByteArray(InputStream inputStream) throws IOException {

		byte[] buffer = new byte[8192];
		int bytesRead;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}

		return output.toByteArray();
	}

	static String getTextAfterCharacter(String text, char character) {

		int lastCharIndex = text.lastIndexOf(character);
		return text.substring(lastCharIndex + 1);

	}

	static String getTagsRegex(String tagName, boolean isIncludingEmptyTags) { // <style.*?</style> or <img.*?/>|<img.*?</img>

		if (isIncludingEmptyTags)
			return String.format("<%1$s.*?/>|<%1$s.*?</%1$s>", tagName);
		else
			return String.format("<%1$s.*?</%1$s>", tagName);

	}

	static void copy(InputStream input, OutputStream output) throws IOException {

		byte[] buffer = new byte[4096 * 1024];

		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}

}
