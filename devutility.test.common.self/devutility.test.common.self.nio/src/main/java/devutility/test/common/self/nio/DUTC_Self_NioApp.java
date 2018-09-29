package devutility.test.common.self.nio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.util.Set;

import devutility.test.common.self.nio.service.SystemInPipe;

public class DUTC_Self_NioApp {
	public static void main(String[] args) throws IOException {
		Selector selector = Selector.open();
		SystemInPipe systemInPipe = new SystemInPipe();
		SelectableChannel systemInChannel = systemInPipe.getStdinChannel();
		systemInChannel.register(selector, SelectionKey.OP_READ);
		systemInPipe.start();

		while (true) {
			int readyChannelsCount = selector.select(1000);

			if (readyChannelsCount == 0) {
				System.out.println("I'm waiting");
				continue;
			}

			Set<SelectionKey> selectionKeys = selector.selectedKeys();

			for (SelectionKey selectionKey : selectionKeys) {
				try (ReadableByteChannel channel = (ReadableByteChannel) selectionKey.channel()) {
					byte[] bytes = read(channel);
					System.out.format("Read from device: %s\n", new String(bytes, Charset.forName("UTF-8")));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			System.out.println("");
		}
	}

	public static byte[] read(ReadableByteChannel channel) throws IOException {
		int count = 0;
		ByteBuffer byteBuffer = ByteBuffer.allocate(64);

		try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
			while ((count = channel.read(byteBuffer)) != -1) {
				System.out.format("Read from device: %s\n", new String(byteBuffer.array(), Charset.forName("UTF-8")));
				
				byteBuffer.flip();

				if (byteBuffer.hasArray()) {
					byteArrayOutputStream.write(byteBuffer.array(), 0, count);
				}

				byteBuffer.clear();
			}

			return byteArrayOutputStream.toByteArray();
		} catch (Exception e) {
			System.out.println(e.toString());
			return null;
		}
	}

	public static byte[] toBytes(ByteBuffer byteBuffer) {
		if (byteBuffer.hasArray()) {
			return byteBuffer.array();
		}

		return null;
	}

	public static String toString(ByteBuffer byteBuffer) {
		byte[] bytes = toBytes(byteBuffer);

		if (bytes == null) {
			return null;
		}

		return new String(bytes, Charset.forName("UTF-8"));
	}
}