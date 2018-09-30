package devutility.test.common.self.nio.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Pipe;
import java.nio.channels.SelectableChannel;

import devutility.internal.nio.pipe.ReversePipeThread;

public class SystemInPipe {
	private Pipe pipe;
	private ReversePipeThread pipeThread;

	public SystemInPipe() throws IOException {
		this(System.in);
	}

	public SystemInPipe(InputStream inputStream) throws IOException {
		pipe = Pipe.open();
		pipeThread = new ReversePipeThread(inputStream, pipe.sink());
	}

	public SelectableChannel stdinChannel() throws IOException {
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