package devutility.test.common.self.nio.usb;

import org.usb4java.Context;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

public class AsyncEventHandlingThread extends Thread {
	private Context context;
	private int timeoutMicrosecond = 500 * 1000;
	private volatile boolean abort = false;

	public void abort() {
		this.abort = true;
	}

	@Override
	public void run() {
		while (!this.abort) {
			int result = LibUsb.handleEventsTimeout(context, timeoutMicrosecond);

			if (result != LibUsb.SUCCESS) {
				throw new LibUsbException("Unable to handle events", result);
			}
		}
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public int getTimeoutMicrosecond() {
		return timeoutMicrosecond;
	}

	public void setTimeoutMicrosecond(int timeoutMicrosecond) {
		this.timeoutMicrosecond = timeoutMicrosecond;
	}
}