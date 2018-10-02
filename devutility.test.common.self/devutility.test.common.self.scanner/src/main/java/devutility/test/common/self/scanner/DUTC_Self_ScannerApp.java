package devutility.test.common.self.scanner;

import java.nio.charset.Charset;

import devutility.internal.nio.pipe.ReversePipeThread;

public class DUTC_Self_ScannerApp {
	public static void main(String[] args) {
		ReversePipeThread reversePipeThread = new ReversePipeThread(System.in, null);

		reversePipeThread.setCallback(buffer -> {
			if (buffer.hasArray()) {
				System.out.format("Read from device: %s\n", new String(buffer.array(), Charset.forName("UTF-8")));
			}
		});

		reversePipeThread.start();

		try {
			reversePipeThread.join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		System.out.println("Test finished!");
	}
}