package devutility.test.common.self.nio;

import java.io.IOException;
import java.nio.channels.Pipe;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.util.Set;

import devutility.internal.nio.ChannelUtils;
import devutility.internal.nio.pipe.ReversePipeThread;

public class DUTC_Self_NioApp {
	public static void main(String[] args) throws IOException {
		Selector selector = Selector.open();
		Pipe pipe = Pipe.open();

		ReversePipeThread reversePipeThread = new ReversePipeThread(System.in, pipe.sink());
		reversePipeThread.start();

		Pipe.SourceChannel sourceChannel = pipe.source();
		sourceChannel.configureBlocking(false);
		sourceChannel.register(selector, SelectionKey.OP_READ);

		while (true) {
			int readyChannelsCount = selector.select(1000);

			if (readyChannelsCount == 0) {
				System.out.println("I am waiting!");
				continue;
			}

			Set<SelectionKey> selectionKeys = selector.selectedKeys();

			for (SelectionKey selectionKey : selectionKeys) {
				try (ReadableByteChannel channel = (ReadableByteChannel) selectionKey.channel()) {
					byte[] bytes = ChannelUtils.read(channel, 64);
					System.out.format("Read from device: %s\n", new String(bytes, Charset.forName("UTF-8")));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}