package com.mohistmc.thor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public final class Utils {
	public final static String systemUID = "00000000-0000-0000-0000-000000000000";

	public static byte[] readAll(InputStream i) throws IOException {
		ByteArrayOutputStream ba = new ByteArrayOutputStream(16384);
		int nRead;
		byte[] data = new byte[4096];

		try {
			while ((nRead = i.read(data, 0, data.length)) != -1) { ba.write(data, 0, nRead); }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw e;
		}

		return ba.toByteArray();
	}

	public static byte[] readAll(File i) {
		try (FileInputStream fis = new FileInputStream(i)) {
			return Utils.readAll(fis);
		} catch (IOException ignored) {
			// TODO Auto-generated catch block
		}
		return new byte[0];
	}

}
