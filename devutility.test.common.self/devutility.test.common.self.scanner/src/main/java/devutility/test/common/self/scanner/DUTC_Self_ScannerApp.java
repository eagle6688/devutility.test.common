package devutility.test.common.self.scanner;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import devutility.internal.nio.pipe.ReversePipeThread;

public class DUTC_Self_ScannerApp {
	private static Logger logger = LoggerFactory.getLogger(DUTC_Self_ScannerApp.class);

	public static void main(String[] args) {
		logger.info("Starting...");
		ReversePipeThread reversePipeThread = new ReversePipeThread(System.in, null);

		reversePipeThread.setCallback(buffer -> {
			if (buffer.hasArray()) {
				String value = new String(buffer.array(), Charset.forName("UTF-8"));
				String logMessage = String.format("Read from device: %s", value);
				logger.info(logMessage);
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