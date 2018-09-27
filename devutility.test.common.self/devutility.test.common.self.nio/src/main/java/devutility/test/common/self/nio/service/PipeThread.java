package devutility.test.common.self.nio.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public class PipeThread extends Thread {
	private boolean running = true;
	private int bufferSize = 1024;
	private byte[] bytes = new byte[bufferSize];
	private ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
	private InputStream inputStream;
	private WritableByteChannel writableByteChannel;

	public PipeThread(InputStream inputStream, WritableByteChannel writableByteChannel) {
		this.inputStream = inputStream;
		this.writableByteChannel = writableByteChannel;
		this.setDaemon(true);
	}

	public void run() {
		int count = 0;

		try {
			while (running && (count = inputStream.read(bytes)) != -1) {
				byteBuffer.clear().limit(count);
				writableByteChannel.write(byteBuffer);
			}

			writableByteChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void shutdown() {
		running = false;
		this.interrupt();
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
}