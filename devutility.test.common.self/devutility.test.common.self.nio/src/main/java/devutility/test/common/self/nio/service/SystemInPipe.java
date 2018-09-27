package devutility.test.common.self.nio.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Pipe;
import java.nio.channels.SelectableChannel;

public class SystemInPipe {
	private Pipe pipe;
	private PipeThread pipeThread;

	public SystemInPipe(InputStream inputStream) throws IOException {
		pipe = Pipe.open();
		pipeThread = new PipeThread(inputStream, pipe.sink());
	}

	public SystemInPipe() throws IOException {
		this(System.in);
	}

	public SelectableChannel getStdinChannel() throws IOException {
		SelectableChannel channel = pipe.source();
		channel.configureBlocking(false);
		return channel;
	}

	public void start() {
		pipeThread.start();
	}

	public void finalize() {
		pipeThread.shutdown();
	}
}